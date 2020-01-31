package com.bitheads.braincloud.comms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bitheads.braincloud.client.BrainCloudClient;
import com.bitheads.braincloud.client.IRelayCallback;
import com.bitheads.braincloud.client.IRelayConnectCallback;
import com.bitheads.braincloud.client.IRelaySystemCallback;
import com.bitheads.braincloud.client.IServerCallback;
import com.bitheads.braincloud.client.RelayConnectionType;
import com.bitheads.braincloud.client.RelayConnectionType;
import com.bitheads.braincloud.client.ServiceName;
import com.bitheads.braincloud.client.ServiceOperation;
import com.bitheads.braincloud.services.AuthenticationService;

public class RelayComms {

    enum RelayCallbackType {
        ConnectSuccess,
        ConnectFailure,
        Relay,
        System
    }

    private final int CONTROL_BYTES_SIZE = 1;

    private final int MAX_PLAYERS       = 128;
    private final int INVALID_NET_ID    = MAX_PLAYERS;

    // Messages sent from Client to Relay-Server
    private final int CL2RS_CONNECTION          = 129;
    private final int CL2RS_DISCONNECT          = 130;
    private final int CL2RS_RELAY               = 131;
    private final int CL2RS_PING                = 133;
    private final int CL2RS_RSMG_ACKNOWLEDGE    = 134;
    private final int CL2RS_ACKNOWLEDGE         = 135;

    // Messages sent from Relay-Server to Client
    private final int RS2CL_RSMG         = 129;
    private final int RS2CL_PONG         = CL2RS_PING;
    private final int RS2CL_ACKNOWLEDGE  = CL2RS_ACKNOWLEDGE;

    private final int RELIABLE_BIT = 0x8000;
    private final int ORDERED_BIT  = 0x4000;

    private class RelayCallback {
        public RelayCallbackType _type;
        public String _message;
        public JSONObject _json;
        public int _netId;
        public byte[] _data;

        RelayCallback(RelayCallbackType type) {
            _type = type;
        }

        RelayCallback(RelayCallbackType type, String message) {
            _type = type;
            _message = message;
        }

        RelayCallback(RelayCallbackType type, JSONObject json) {
            _type = type;
            _json = json;
        }

        RelayCallback(RelayCallbackType type, int netId, byte[] data) {
            _type = type;
            _netId = netId;
            _data = data;
        }
    }

    private class ConnectInfo {
        public String _passcode;
        public String _lobbyId;

        ConnectInfo(String passcode, String lobbyId) {
            _passcode = passcode;
            _lobbyId = lobbyId;
        }
    }

    private class WSClient extends WebSocketClient {
        public WSClient(String ip) throws Exception {
            super(new URI(ip));
        }
        
        @Override
        public void onMessage(String message) {
            onRecv(ByteBuffer.wrap(message.getBytes()));
        }
        
        @Override
        public void onMessage(ByteBuffer bytes) {
            onRecv(bytes);
        }

        @Override
        public void onOpen(ServerHandshake handshake) {
            System.out.println("Relay WS Connected");
            onWSConnected();
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            System.out.println("Relay WS onClose: " + reason + ", code: " + Integer.toString(code) + ", remote: " + Boolean.toString(remote));
            disconnect();
            synchronized(_callbackEventQueue) {
                _callbackEventQueue.add(new RelayCallback(RelayCallbackType.ConnectFailure, "webSocket onClose: " + reason));
            }
        }

        @Override
        public void onError(Exception e) {
            e.printStackTrace();
            disconnect();
            synchronized(_callbackEventQueue) {
                _callbackEventQueue.add(new RelayCallback(RelayCallbackType.ConnectFailure, "webSocket onError"));
            }
        }
    }

    private BrainCloudClient _client;
    private boolean _loggingEnabled = false;
    private IRelayConnectCallback _connectCallback = null;
    private ArrayList<RelayCallback> _callbackEventQueue = new ArrayList<RelayCallback>();

    private boolean _isConnected = false;
    private int _netId = -1;
    private String _ownerId = null;
    private HashMap<Integer, String> _netIdToProfileId = new HashMap<Integer, String>();
    private HashMap<String, Integer> _profileIdToNetId = new HashMap<String, Integer>();
    private int _packetIdPerChannel[] = new int[]{0, 0, 0, 0};
    private ConnectInfo _connectInfo = null;
    
    private int _ping = 999;
    private boolean _pingInFlight = false;
    private int _pingIntervalMS = 1000;
    private long _lastPingTime = 0;
    
    private RelayConnectionType _connectionType = RelayConnectionType.WEBSOCKET;
    private WSClient _webSocketClient;
    private Socket _tcpSocket;
    private Object _lock = new Object();

    private IRelayCallback _relayCallback = null;
    private IRelaySystemCallback _relaySystemCallback = null;
    
    public RelayComms(BrainCloudClient client) {
        _client = client;
    }

    public boolean getLoggingEnabled() {
        return _loggingEnabled;
    }

    public void enableLogging(boolean isEnabled) {
        _loggingEnabled = isEnabled;
    }

    public void connect(RelayConnectionType connectionType, JSONObject options, IRelayConnectCallback callback) {
        if (_isConnected) {
            disconnect();
        }

        _connectionType = connectionType;
        _isConnected = false;
        _connectCallback = callback;
        _ping = 999;
        _pingInFlight = false;
        _netIdToProfileId.clear();
        _profileIdToNetId.clear();
        _netId = -1;
        _ownerId = null;

        if (options == null) {
            _callbackEventQueue.add(new RelayCallback(RelayCallbackType.ConnectFailure, "Invalid arguments"));
            return;
        }

        final boolean ssl;
        final String host;
        final int port;
        final String passcode;
        final String lobbyId;

        try {
            ssl = options.has("ssl") ? options.getBoolean("ssl") : false;
            host = options.getString("host");
            port = options.getInt("port");
            passcode = options.getString("passcode");
            lobbyId = options.getString("lobbyId");
        } catch (JSONException e) {
            e.printStackTrace();
            _callbackEventQueue.add(new RelayCallback(RelayCallbackType.ConnectFailure, "Invalid arguments"));
            return;
        }

        _connectInfo = new ConnectInfo(passcode, lobbyId);

        // connect...
        switch (_connectionType) {
            case WEBSOCKET: {
                try {
                    String uri = (ssl ? "wss://" : "ws://") + host + ":" + port;

                    _webSocketClient = new WSClient(uri);
        
                    if (ssl) {
                        setupSSL();
                    }
                    
                    _webSocketClient.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                    _callbackEventQueue.add(new RelayCallback(RelayCallbackType.ConnectFailure, "Failed to connect"));
                    disconnect();
                    return;
                }
                break;
            }
            case TCP: {
                Thread connectionThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            _tcpSocket = new Socket(InetAddress.getByName(host), port);
                            _tcpSocket.setTcpNoDelay(true);
                            if (_loggingEnabled) {
                                System.out.println("RELAY TCP: Connected");
                            }
                            onTCPConnected();
                        } catch (Exception e) {
                            e.printStackTrace();
                            _callbackEventQueue.add(new RelayCallback(RelayCallbackType.ConnectFailure, "Failed to connect"));
                            disconnect();
                            return;
                        }
                    }
                });
                connectionThread.start();
                break;
            }
            default: {
                _callbackEventQueue.add(new RelayCallback(RelayCallbackType.ConnectFailure, "Protocol Unimplemented"));
            }
        }
    }
    
    public void disconnect() {
        synchronized(_lock) {
            _isConnected = false;

            if (_webSocketClient != null) {
                _webSocketClient.close();
                _webSocketClient = null;
            }

            try {
                if (_tcpSocket != null) {
                    _tcpSocket.close();
                    _tcpSocket = null;
                }
            } catch (Exception e) {
                _tcpSocket = null;
            }

            for (int i = 0; i < 4; ++i) _packetIdPerChannel[i] = 0;
            _connectInfo = null;
        }
    }

    public boolean isConnected() {
        boolean ret;
        synchronized(_lock) {
            ret = _isConnected;
        }
        return ret;
    }

    public int getPing() {
        return _ping;
    }

    public void setPingInterval(int intervalMS) {
        _pingIntervalMS = intervalMS;
    }

    public String getOwnerProfileId() {
        return _ownerId;
    }

    public String getProfileIdForNetId(int netId) {
        return _netIdToProfileId.get(netId);
    }

    public int getNetIdForProfileId(String profileId) {
        if (_profileIdToNetId.containsKey(profileId))
        {
            return _profileIdToNetId.get(profileId);
        }
        return INVALID_NET_ID;
    }
    
    public void registerRelayCallback(IRelayCallback callback) {
        _relayCallback = callback;
    }
    public void deregisterRelayCallback() {
        _relayCallback = null;
    }
    
    public void registerSystemCallback(IRelaySystemCallback callback) {
        _relaySystemCallback = callback;
    }
    public void deregisterSystemCallback() {
        _relaySystemCallback = null;
    }

    private void setupSSL() throws Exception {

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) 
                    throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) 
                    throws CertificateException {
            }
        }};

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new SecureRandom());
        SSLSocketFactory factory = sc.getSocketFactory();

        _webSocketClient.setSocket(factory.createSocket());
    }

    private void onWSConnected() {
        try {
            send(CL2RS_CONNECTION, buildConnectionRequest());
        } catch(Exception e) {
            e.printStackTrace();
            disconnect();
            synchronized(_callbackEventQueue) {
                _callbackEventQueue.add(new RelayCallback(RelayCallbackType.ConnectFailure, "Failed to build Connection Request"));
            }
        }
    }

    private void onTCPConnected() {
        try {
            startTCPReceivingThread();
            send(CL2RS_CONNECTION, buildConnectionRequest());
        } catch(Exception e) {
            e.printStackTrace();
            disconnect();
            synchronized(_callbackEventQueue) {
                _callbackEventQueue.add(new RelayCallback(RelayCallbackType.ConnectFailure, "Failed to build Connection Request"));
            }
        }
    }

    private JSONObject buildConnectionRequest() throws Exception {
        JSONObject json = new JSONObject();

        json.put("lobbyId", _connectInfo._lobbyId);
        json.put("profileId", _client.getAuthenticationService().getProfileId());
        json.put("passcode", _connectInfo._passcode);

        return json;
    }

    private void send(int netId, JSONObject json) {
        send(netId, json.toString());
    }

    private void send(int netId, String text) {
        if (_loggingEnabled) {
            System.out.println("RELAY SEND: " + text);
        }

        byte[] textBytes = text.getBytes(StandardCharsets.US_ASCII);
        int bufferSize = textBytes.length + 3;
        
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putShort((short)bufferSize);
        buffer.put((byte)netId);
        buffer.put(textBytes, 0, textBytes.length);
        buffer.rewind();

        send(buffer);
    }

    private void send(ByteBuffer buffer) {
        buffer.rewind();

        try {
            synchronized(_lock) {
                switch (_connectionType) {
                    case WEBSOCKET: {
                        if (_webSocketClient != null) {
                            _webSocketClient.send(buffer);
                        }
                        break;
                    }
                    case TCP: {
                        if (_tcpSocket != null) {
                            byte[] bytes = buffer.array();
                            _tcpSocket.getOutputStream().write(bytes, 0, bytes.length);
                            _tcpSocket.getOutputStream().flush();
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            disconnect();
            synchronized(_callbackEventQueue) {
                _callbackEventQueue.add(new RelayCallback(RelayCallbackType.ConnectFailure, "RELAY Send Failed"));
            }
        }
    }

    public void sendRelay(byte[] data, int toNetId, boolean reliable, boolean ordered, int channel) {
        if (!isConnected()) return;

        int bufferSize = data.length + 5;

        // Relay Header
        int rh = 0;
        if (reliable) rh |= RELIABLE_BIT;
        if (ordered) rh |= ORDERED_BIT;
        rh |= channel << 12;
        rh |= _packetIdPerChannel[channel];
        _packetIdPerChannel[channel] = (_packetIdPerChannel[channel] + 1) % 0x1000;

        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putShort((short)bufferSize);
        buffer.put((byte)toNetId);
        buffer.putShort((short)rh);
        buffer.put(data, 0, data.length);
        send(buffer);
    }

    private void startTCPReceivingThread() {
        Thread receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                DataInputStream in;
                try {
                    synchronized(_lock) {
                        in = new DataInputStream(_tcpSocket.getInputStream());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    disconnect();
                    synchronized(_callbackEventQueue) {
                        _callbackEventQueue.add(new RelayCallback(RelayCallbackType.ConnectFailure, "TCP Connect Failed"));
                    }
                    return;
                }
                while (true) {
                    try {
                        int len = in.readShort() & 0xFFFF;
                        byte[] bytes = new byte[len - 2];
                        in.readFully(bytes);

                        ByteBuffer buffer = ByteBuffer.allocate(len);
                        buffer.order(ByteOrder.BIG_ENDIAN);
                        buffer.putShort((short)len);
                        buffer.put(bytes, 0, bytes.length);
                        buffer.rewind();
                        
                        onRecv(buffer);
                    } catch (Exception e) {
                        e.printStackTrace();
                        disconnect();
                        synchronized(_callbackEventQueue) {
                            _callbackEventQueue.add(new RelayCallback(RelayCallbackType.ConnectFailure, "TCP Connect Failed"));
                        }
                        return;
                    }
                }
            }
        });

        receiveThread.start();
    }

    private void sendPing() {
        if (_pingInFlight) return;
        if (_loggingEnabled) {
            System.out.println("RELAY SEND: PING");
        }

        ByteBuffer buffer = ByteBuffer.allocate(5);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putShort((short)5);
        buffer.put((byte)CL2RS_PING);
        buffer.putShort((short)_ping);
        send(buffer);
        
        _lastPingTime = System.currentTimeMillis();
        _pingInFlight = true;
    }

    private void onRecv(ByteBuffer buffer) {
        int len = buffer.limit();
        if (len < 3) {
            disconnect();
            synchronized(_callbackEventQueue) {
                _callbackEventQueue.add(new RelayCallback(RelayCallbackType.ConnectFailure, "Relay Recv Error: packet cannot be smaller than 3 bytes"));
            }
            return;
        }

        buffer.rewind();
        buffer.order(ByteOrder.BIG_ENDIAN);

        int size = buffer.getShort() & 0xFFFF;
        int netId = buffer.get() & 0xFF;

        if (len < size) {
            disconnect();
            synchronized(_callbackEventQueue) {
                _callbackEventQueue.add(new RelayCallback(RelayCallbackType.ConnectFailure, "Relay Recv Error: Packet is smaller than header's size"));
            }
            return;
        }

        if (netId == RS2CL_RSMG) {
            onRSMG(buffer, size - 3);
        }
        else if (netId == RS2CL_PONG) {
            onPONG();
        }
        else if (netId == RS2CL_ACKNOWLEDGE) {
            if (_connectionType == RelayConnectionType.UDP) {
                // ...
            }
        }
        else if (netId < MAX_PLAYERS) {
            // if (_loggingEnabled) { // This is overkill
            //     System.out.println("RELAY MSG From: " + netId);
            // }
            if (len < 5) {
                disconnect();
                synchronized(_callbackEventQueue) {
                    _callbackEventQueue.add(new RelayCallback(RelayCallbackType.ConnectFailure, "Relay Recv Error: relay packet cannot be smaller than 5 bytes"));
                }
                return;
            }
            byte[] eventBuffer = new byte[size - 5];
            buffer.position(5);
            buffer.get(eventBuffer, 0, size - 5);
            synchronized(_callbackEventQueue) {
                _callbackEventQueue.add(new RelayCallback(RelayCallbackType.Relay, netId, eventBuffer));
            }
        }
        else {
            disconnect();
            synchronized(_callbackEventQueue) {
                _callbackEventQueue.add(new RelayCallback(RelayCallbackType.ConnectFailure, "Relay Recv Error: Unknown netId: " + netId));
            }
            return;
        }
    }

    private void onPONG() {
        if (_pingInFlight) {
            _pingInFlight = false;
            _ping = (int)Math.min((long)999, System.currentTimeMillis() - _lastPingTime);
            if (_loggingEnabled) {
                System.out.println("RELAY PONG: " + _ping);
            }
        }
    }

    private void onRSMG(ByteBuffer buffer, int size) {
        try {
            int rsmgPacketId = buffer.getShort() & 0xFFFF;
            size -= 2;
            byte[] bytes = new byte[size];
            buffer.get(bytes, 0, size);
            String jsonString = new String(bytes, StandardCharsets.US_ASCII);
            JSONObject json = new JSONObject(jsonString);
            
            if (_loggingEnabled) {
                System.out.println("RELAY System Msg: " + jsonString);
            }

            switch (json.getString("op")) {
                case "CONNECT": {
                    int netId = json.getInt("netId");
                    String profileId = json.getString("profileId");
                    _netIdToProfileId.put(netId, profileId);
                    _profileIdToNetId.put(profileId, netId);
                    if (profileId.equals(_client.getAuthenticationService().getProfileId())) {
                        synchronized(_lock) {
                            if (!_isConnected) {
                                _isConnected = true;
                                _lastPingTime = System.currentTimeMillis();
                                _netId = netId;
                                _ownerId = json.getString("ownerId");
                                                    
                                synchronized(_callbackEventQueue) {
                                    _callbackEventQueue.add(new RelayCallback(RelayCallbackType.ConnectSuccess, json));
                                }
                            }
                        }
                    }
                    break;
                }
                case "NET_ID": {
                    int netId = json.getInt("netId");
                    String profileId = json.getString("profileId");
                    _netIdToProfileId.put(netId, profileId);
                    _profileIdToNetId.put(profileId, netId);
                    break;
                }
                case "MIGRATE_OWNER": {
                    _ownerId = json.getString("profileId");
                    break;
                }
            }

            synchronized(_callbackEventQueue) {
                _callbackEventQueue.add(new RelayCallback(RelayCallbackType.System, json));
            }
        } catch(Exception e) {
            e.printStackTrace();
            disconnect();
            synchronized(_callbackEventQueue) {
                _callbackEventQueue.add(new RelayCallback(RelayCallbackType.ConnectFailure, "Relay System Msg error"));
            }
        }
    }

    public void runCallbacks() {
        synchronized(_callbackEventQueue) {
            while (!_callbackEventQueue.isEmpty()) {
                RelayCallback relayCallback = _callbackEventQueue.remove(0);
                switch (relayCallback._type) {
                    case ConnectSuccess: {
                        if (_connectCallback != null) {
                            _connectCallback.relayConnectSuccess(relayCallback._json);
                        }
                        break;
                    }
                    case ConnectFailure: {
                        if (_connectCallback != null) {
                            _connectCallback.relayConnectFailure(relayCallback._message);
                        }
                        break;
                    }
                    case Relay: {
                        if (_relayCallback != null) {
                            _relayCallback.relayCallback(relayCallback._netId, relayCallback._data);
                        }
                        break;
                    }
                    case System: {
                        if (_relaySystemCallback != null) {
                            _relaySystemCallback.relaySystemCallback(relayCallback._json);
                        }
                        break;
                    }
                }
            }
        }

        // Ping. Which also works as a heartbeat
        if (_isConnected) {
            if (System.currentTimeMillis() - _lastPingTime >= _pingIntervalMS) {
                sendPing();
            }
        }
    }
}
