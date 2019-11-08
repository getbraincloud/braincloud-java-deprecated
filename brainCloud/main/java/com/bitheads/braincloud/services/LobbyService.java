package com.bitheads.braincloud.services;

import com.bitheads.braincloud.client.BrainCloudClient;
import com.bitheads.braincloud.client.IServerCallback;
import com.bitheads.braincloud.client.ReasonCodes;
import com.bitheads.braincloud.client.ServiceName;
import com.bitheads.braincloud.client.ServiceOperation;
import com.bitheads.braincloud.client.StatusCodes;
import com.bitheads.braincloud.comms.ServerCall;
import com.bitheads.braincloud.comms.ServerResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;

import java.net.URL;

/**
 * Created by David St-Louis on 2018-07-04
 */
public class LobbyService implements IServerCallback{
 
    private enum Parameter {
        lobbyType,
        rating,
        maxSteps,
        algo,
        filterJson,
        otherUserCxIds,
        settings,
        isReady,
        extraJson,
        teamCode,
        lobbyId,
        cxId,
        signalData,
        toTeamCode,
        roomType,
        lobbyTypes,
        pingData
    }

    private BrainCloudClient _client;

    public LobbyService(BrainCloudClient client) {
        _client = client;
    }

    /**
     * Creates a new lobby.
     * 
     * Sends LOBBY_JOIN_SUCCESS message to the user, with full copy of lobby data Sends LOBBY_MEMBER_JOINED to all lobby members, with copy of member data
     *
     * Service Name - Lobby
     * Service Operation - CREATE_LOBBY
     *
     * @param lobbyType The type of lobby to look for. Lobby types are defined in the portal.
     * @param rating The skill rating to use for finding the lobby. Provided as a separate parameter because it may not exactly match the user's rating (especially in cases where parties are involved).
     * @param otherUserCxIds Array of other users (i.e. party members) to add to the lobby as well. Will constrain things so that only lobbies with room for all players will be considered.
     * @param isReady Initial ready-status of this user.
     * @param extraJson Initial extra-data about this user.
     * @param teamCode Preferred team for this user, if applicable. Send "" or null for automatic assignment.
     * @param settings Configuration data for the room.
     */
    public void createLobby(String lobbyType, int rating, ArrayList<String> otherUserCxIds, Boolean isReady, String extraJson, String teamCode, String settings, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.lobbyType.name(), lobbyType);
            data.put(Parameter.rating.name(), rating);
            if (otherUserCxIds != null) {
                data.put(Parameter.otherUserCxIds.name(), new JSONArray(otherUserCxIds));
            }
            data.put(Parameter.isReady.name(), isReady);
            if (StringUtil.IsOptionalParameterValid(extraJson)) {
                data.put(Parameter.extraJson.name(), new JSONObject(extraJson));
            }
            data.put(Parameter.teamCode.name(), teamCode);
            if (StringUtil.IsOptionalParameterValid(settings)) {
                data.put(Parameter.settings.name(), new JSONObject(settings));
            }

            ServerCall sc = new ServerCall(ServiceName.lobby,
                    ServiceOperation.CREATE_LOBBY, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * Creates a new lobby with ping data 
     * 
     * Sends LOBBY_JOIN_SUCCESS message to the user, with full copy of lobby data Sends LOBBY_MEMBER_JOINED to all lobby members, with copy of member data
     *
     * Service Name - Lobby
     * Service Operation - CREATE_LOBBY_WITH_PING_DATA
     *
     * @param lobbyType The type of lobby to look for. Lobby types are defined in the portal.
     * @param rating The skill rating to use for finding the lobby. Provided as a separate parameter because it may not exactly match the user's rating (especially in cases where parties are involved).
     * @param otherUserCxIds Array of other users (i.e. party members) to add to the lobby as well. Will constrain things so that only lobbies with room for all players will be considered.
     * @param isReady Initial ready-status of this user.
     * @param extraJson Initial extra-data about this user.
     * @param teamCode Preferred team for this user, if applicable. Send "" or null for automatic assignment.
     * @param settings Configuration data for the room.
     */
    public void createLobbyWithPingData(String lobbyType, int rating, ArrayList<String> otherUserCxIds, Boolean isReady, String extraJson, String teamCode, String settings, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.lobbyType.name(), lobbyType);
            data.put(Parameter.rating.name(), rating);
            if (otherUserCxIds != null) {
                data.put(Parameter.otherUserCxIds.name(), new JSONArray(otherUserCxIds));
            }
            data.put(Parameter.isReady.name(), isReady);
            if (StringUtil.IsOptionalParameterValid(extraJson)) {
                data.put(Parameter.extraJson.name(), new JSONObject(extraJson));
            }
            data.put(Parameter.teamCode.name(), teamCode);
            if (StringUtil.IsOptionalParameterValid(settings)) {
                data.put(Parameter.settings.name(), new JSONObject(settings));
            }

            attachPingDataAndSend(data, ServiceOperation.CREATE_LOBBY_WITH_PING_DATA, callback);

        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * Finds a lobby matching the specified parameters. Asynchronous - returns 200 to indicate that matchmaking has started.
     *
     * Service Name - Lobby
     * Service Operation - FIND_LOBBY
     *
     * @param lobbyType The type of lobby to look for. Lobby types are defined in the portal.
     * @param rating The skill rating to use for finding the lobby. Provided as a separate parameter because it may not exactly match the user's rating (especially in cases where parties are involved).
     * @param maxSteps The maximum number of steps to wait when looking for an applicable lobby. Each step is ~5 seconds.
     * @param algo The algorithm to use for increasing the search scope.
     * @param filterJson Used to help filter the list of rooms to consider. Passed to the matchmaking filter, if configured.
     * @param otherUserCxIds Array of other users (i.e. party members) to add to the lobby as well. Will constrain things so that only lobbies with room for all players will be considered.
     * @param isReady Initial ready-status of this user.
     * @param extraJson Initial extra-data about this user.
     * @param teamCode Preferred team for this user, if applicable. Send "" or null for automatic assignment
     */
    public void findLobby(String lobbyType, int rating, int maxSteps, String algo, String filterJson, ArrayList<String> otherUserCxIds, Boolean isReady, String extraJson, String teamCode, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.lobbyType.name(), lobbyType);
            data.put(Parameter.rating.name(), rating);
            data.put(Parameter.maxSteps.name(), maxSteps);
            if (StringUtil.IsOptionalParameterValid(algo)) {
                data.put(Parameter.algo.name(), new JSONObject(algo));
            }
            if (StringUtil.IsOptionalParameterValid(filterJson)) {
                data.put(Parameter.filterJson.name(), new JSONObject(filterJson));
            }
            if (otherUserCxIds != null) {
                data.put(Parameter.otherUserCxIds.name(), new JSONArray(otherUserCxIds));
            }
            data.put(Parameter.isReady.name(), isReady);
            if (StringUtil.IsOptionalParameterValid(extraJson)) {
                data.put(Parameter.extraJson.name(), new JSONObject(extraJson));
            }
            data.put(Parameter.teamCode.name(), teamCode);

            ServerCall sc = new ServerCall(ServiceName.lobby,
                    ServiceOperation.FIND_LOBBY, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

        /**
     * Finds a lobby matching the specified parameters. Asynchronous - returns 200 to indicate that matchmaking has started. But adds ping data to the call
     *
     * Service Name - Lobby
     * Service Operation - FIND_LOBBY
     *
     * @param lobbyType The type of lobby to look for. Lobby types are defined in the portal.
     * @param rating The skill rating to use for finding the lobby. Provided as a separate parameter because it may not exactly match the user's rating (especially in cases where parties are involved).
     * @param maxSteps The maximum number of steps to wait when looking for an applicable lobby. Each step is ~5 seconds.
     * @param algo The algorithm to use for increasing the search scope.
     * @param filterJson Used to help filter the list of rooms to consider. Passed to the matchmaking filter, if configured.
     * @param otherUserCxIds Array of other users (i.e. party members) to add to the lobby as well. Will constrain things so that only lobbies with room for all players will be considered.
     * @param isReady Initial ready-status of this user.
     * @param extraJson Initial extra-data about this user.
     * @param teamCode Preferred team for this user, if applicable. Send "" or null for automatic assignment
     */
    public void findLobbyWithPingData(String lobbyType, int rating, int maxSteps, String algo, String filterJson, ArrayList<String> otherUserCxIds, Boolean isReady, String extraJson, String teamCode, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.lobbyType.name(), lobbyType);
            data.put(Parameter.rating.name(), rating);
            data.put(Parameter.maxSteps.name(), maxSteps);
            if (StringUtil.IsOptionalParameterValid(algo)) {
                data.put(Parameter.algo.name(), new JSONObject(algo));
            }
            if (StringUtil.IsOptionalParameterValid(filterJson)) {
                data.put(Parameter.filterJson.name(), new JSONObject(filterJson));
            }
            if (otherUserCxIds != null) {
                data.put(Parameter.otherUserCxIds.name(), new JSONArray(otherUserCxIds));
            }
            data.put(Parameter.isReady.name(), isReady);
            if (StringUtil.IsOptionalParameterValid(extraJson)) {
                data.put(Parameter.extraJson.name(), new JSONObject(extraJson));
            }
            data.put(Parameter.teamCode.name(), teamCode);

            attachPingDataAndSend(data, ServiceOperation.FIND_LOBBY_WITH_PING_DATA, callback);

        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * Adds the caller to the lobby entry queue and will create a lobby if none are found.
     *
     * Service Name - Lobby
     * Service Operation - FIND_OR_CREATE_LOBBY
     *
     * @param lobbyType The type of lobby to look for. Lobby types are defined in the portal.
     * @param rating The skill rating to use for finding the lobby. Provided as a separate parameter because it may not exactly match the user's rating (especially in cases where parties are involved).
     * @param maxSteps The maximum number of steps to wait when looking for an applicable lobby. Each step is ~5 seconds.
     * @param algo The algorithm to use for increasing the search scope.
     * @param filterJson Used to help filter the list of rooms to consider. Passed to the matchmaking filter, if configured.
     * @param otherUserCxIds Array of other users (i.e. party members) to add to the lobby as well. Will constrain things so that only lobbies with room for all players will be considered.
     * @param settings Configuration data for the room.
     * @param isReady Initial ready-status of this user.
     * @param extraJson Initial extra-data about this user.
     * @param teamCode Preferred team for this user, if applicable. Send "" or null for automatic assignment.
     */
    public void findOrCreateLobby(String lobbyType, int rating, int maxSteps, String algo, String filterJson, ArrayList<String> otherUserCxIds, String settings, Boolean isReady, String extraJson, String teamCode, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.lobbyType.name(), lobbyType);
            data.put(Parameter.rating.name(), rating);
            data.put(Parameter.maxSteps.name(), maxSteps);
            if (StringUtil.IsOptionalParameterValid(algo)) {
                data.put(Parameter.algo.name(), new JSONObject(algo));
            }
            if (StringUtil.IsOptionalParameterValid(filterJson)) {
                data.put(Parameter.filterJson.name(), new JSONObject(filterJson));
            }
            if (otherUserCxIds != null) {
                data.put(Parameter.otherUserCxIds.name(), new JSONArray(otherUserCxIds));
            }
            if (StringUtil.IsOptionalParameterValid(settings)) {
                data.put(Parameter.settings.name(), new JSONObject(settings));
            }
            data.put(Parameter.isReady.name(), isReady);
            if (StringUtil.IsOptionalParameterValid(extraJson)) {
                data.put(Parameter.extraJson.name(), new JSONObject(extraJson));
            }
            data.put(Parameter.teamCode.name(), teamCode);

            ServerCall sc = new ServerCall(ServiceName.lobby,
                    ServiceOperation.FIND_OR_CREATE_LOBBY, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * Adds the caller to the lobby entry queue and will create a lobby if none are found.
     *
     * Service Name - Lobby
     * Service Operation - FIND_OR_CREATE_LOBBY_WITH_PING_DATA
     *
     * @param lobbyType The type of lobby to look for. Lobby types are defined in the portal.
     * @param rating The skill rating to use for finding the lobby. Provided as a separate parameter because it may not exactly match the user's rating (especially in cases where parties are involved).
     * @param maxSteps The maximum number of steps to wait when looking for an applicable lobby. Each step is ~5 seconds.
     * @param algo The algorithm to use for increasing the search scope.
     * @param filterJson Used to help filter the list of rooms to consider. Passed to the matchmaking filter, if configured.
     * @param otherUserCxIds Array of other users (i.e. party members) to add to the lobby as well. Will constrain things so that only lobbies with room for all players will be considered.
     * @param settings Configuration data for the room.
     * @param isReady Initial ready-status of this user.
     * @param extraJson Initial extra-data about this user.
     * @param teamCode Preferred team for this user, if applicable. Send "" or null for automatic assignment.
     */
    public void findOrCreateLobbyWithPingData(String lobbyType, int rating, int maxSteps, String algo, String filterJson, ArrayList<String> otherUserCxIds, String settings, Boolean isReady, String extraJson, String teamCode, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.lobbyType.name(), lobbyType);
            data.put(Parameter.rating.name(), rating);
            data.put(Parameter.maxSteps.name(), maxSteps);
            if (StringUtil.IsOptionalParameterValid(algo)) {
                data.put(Parameter.algo.name(), new JSONObject(algo));
            }
            if (StringUtil.IsOptionalParameterValid(filterJson)) {
                data.put(Parameter.filterJson.name(), new JSONObject(filterJson));
            }
            if (otherUserCxIds != null) {
                data.put(Parameter.otherUserCxIds.name(), new JSONArray(otherUserCxIds));
            }
            if (StringUtil.IsOptionalParameterValid(settings)) {
                data.put(Parameter.settings.name(), new JSONObject(settings));
            }
            data.put(Parameter.isReady.name(), isReady);
            if (StringUtil.IsOptionalParameterValid(extraJson)) {
                data.put(Parameter.extraJson.name(), new JSONObject(extraJson));
            }
            data.put(Parameter.teamCode.name(), teamCode);

            attachPingDataAndSend(data, ServiceOperation.FIND_OR_CREATE_LOBBY_WITH_PING_DATA, callback);

        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * Returns the data for the specified lobby, including member data.
     *
     * Service Name - Lobby
     * Service Operation - GET_LOBBY_DATA
     *
     * @param lobbyId Id of chosen lobby.
     */
    public void getLobbyData(String lobbyId, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.lobbyId.name(), lobbyId);

            ServerCall sc = new ServerCall(ServiceName.lobby,
                    ServiceOperation.GET_LOBBY_DATA, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * Causes the caller to leave the specified lobby. If the user was the owner, a new owner will be chosen. If user was the last member, the lobby will be deleted.
     *
     * Service Name - Lobby
     * Service Operation - LEAVE_LOBBY
     *
     * @param lobbyId Id of chosen lobby.
     */
    public void leaveLobby(String lobbyId, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.lobbyId.name(), lobbyId);

            ServerCall sc = new ServerCall(ServiceName.lobby,
                    ServiceOperation.LEAVE_LOBBY, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * Causes the caller to join the specified lobby. 
     *
     * Service Name - Lobby
     * Service Operation - JOIN_LOBBY
     * 
     * @param lobbyId Id of chosen lobby.
     * @param isReady initial ready status of this user
     * @param extraJson Initial extra-data about this user
     * @param teamCode specified team code
     * @param otherUserCxIds Array fo other users (ie party members) to add to the lobby as well. Constrains things so only lobbies with room for all players will be considered. 
     */
    public void joinLobby(String lobbyId, boolean isReady, String extraJson, String teamCode, ArrayList<String> otherUserCxIds, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.lobbyId.name(), lobbyId);
            data.put(Parameter.isReady.name(), isReady);
            if (StringUtil.IsOptionalParameterValid(extraJson)) {
                data.put(Parameter.extraJson.name(), new JSONObject(extraJson));
            }
            data.put(Parameter.teamCode.name(), teamCode);
            if (otherUserCxIds != null) {
                data.put(Parameter.otherUserCxIds.name(), new JSONArray(otherUserCxIds));
            }

            ServerCall sc = new ServerCall(ServiceName.lobby,
                    ServiceOperation.JOIN_LOBBY, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * Causes the caller to join the specified lobby. 
     *
     * Service Name - Lobby
     * Service Operation - JOIN_LOBBY_WITH_PING_DATA
     * 
     * @param lobbyId Id of chosen lobby.
     * @param isReady initial ready status of this user
     * @param extraJson Initial extra-data about this user
     * @param teamCode specified team code
     * @param otherUserCxIds Array fo other users (ie party members) to add to the lobby as well. Constrains things so only lobbies with room for all players will be considered. 
     */
    public void joinLobbyWithPingData(String lobbyId, boolean isReady, String extraJson, String teamCode, ArrayList<String> otherUserCxIds, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.lobbyId.name(), lobbyId);
            data.put(Parameter.isReady.name(), isReady);
            if (StringUtil.IsOptionalParameterValid(extraJson)) {
                data.put(Parameter.extraJson.name(), new JSONObject(extraJson));
            }
            data.put(Parameter.teamCode.name(), teamCode);
            if (otherUserCxIds != null) {
                data.put(Parameter.otherUserCxIds.name(), new JSONArray(otherUserCxIds));
            }

            attachPingDataAndSend(data, ServiceOperation.FIND_OR_CREATE_LOBBY_WITH_PING_DATA, callback);

        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * Evicts the specified user from the specified lobby. The caller must be the owner of the lobby.
     *
     * Service Name - Lobby
     * Service Operation - REMOVE_MEMBER
     *
     * @param lobbyId Id of chosen lobby.
     * @param cxId Specified member to be removed from the lobby.
     */
    public void removeMember(String lobbyId, String cxId, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.lobbyId.name(), lobbyId);
            data.put(Parameter.cxId.name(), cxId);

            ServerCall sc = new ServerCall(ServiceName.lobby,
                    ServiceOperation.REMOVE_MEMBER, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * Sends LOBBY_SIGNAL_DATA message to all lobby members.
     *
     * Service Name - Lobby
     * Service Operation - SEND_SIGNAL
     *
     * @param lobbyId Id of chosen lobby.
     * @param signalData Signal data to be sent.
     */
    public void sendSignal(String lobbyId, String signalData, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.lobbyId.name(), lobbyId);
            if (StringUtil.IsOptionalParameterValid(signalData)) {
                data.put(Parameter.signalData.name(), new JSONObject(signalData));
            }

            ServerCall sc = new ServerCall(ServiceName.lobby,
                    ServiceOperation.SEND_SIGNAL, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * Switches to the specified team (if allowed.)
     * 
     * Sends LOBBY_MEMBER_UPDATED to all lobby members, with copy of member data
     *
     * Service Name - Lobby
     * Service Operation - SWITCH_TEAM
     *
     * @param lobbyId Id of chosen lobby.
     * @param toTeamCode Specified team code.
     */
    public void switchTeam(String lobbyId, String toTeamCode, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.lobbyId.name(), lobbyId);
            data.put(Parameter.toTeamCode.name(), toTeamCode);

            ServerCall sc = new ServerCall(ServiceName.lobby,
                    ServiceOperation.SWITCH_TEAM, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * Updates the ready status and extra json for the given lobby member.
     *
     * Service Name - Lobby
     * Service Operation - UPDATE_READY
     *
     * @param lobbyId The type of lobby to look for. Lobby types are defined in the portal.
     * @param isReady Initial ready-status of this user.
     * @param extraJson Initial extra-data about this user.
     */
    public void updateReady(String lobbyId, Boolean isReady, String extraJson, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.lobbyId.name(), lobbyId);
            data.put(Parameter.isReady.name(), isReady);
            if (StringUtil.IsOptionalParameterValid(extraJson)) {
                data.put(Parameter.extraJson.name(), new JSONObject(extraJson));
            }

            ServerCall sc = new ServerCall(ServiceName.lobby,
                    ServiceOperation.UPDATE_READY, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * Updates the ready status and extra json for the given lobby member.
     *
     * Service Name - Lobby
     * Service Operation - UPDATE_SETTINGS
     *
     * @param lobbyId Id of the specfified lobby.
     * @param settings Configuration data for the room.
     */
    public void updateSettings(String lobbyId, String settings, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.lobbyId.name(), lobbyId);
            if (StringUtil.IsOptionalParameterValid(settings)) {
                data.put(Parameter.settings.name(), new JSONObject(settings));
            }

            ServerCall sc = new ServerCall(ServiceName.lobby,
                    ServiceOperation.UPDATE_SETTINGS, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /// <summary>
    /// Cancel this members Find, Join and Searching of Lobbies
    /// </summary>
    public void cancelFindRequest(String lobbyType, String cxId, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.lobbyType.name(), lobbyType);
            data.put(Parameter.cxId.name(), cxId);

            ServerCall sc = new ServerCall(ServiceName.lobby,
                    ServiceOperation.CANCEL_FIND_REQUEST, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    public void getRegionsForLobbies(String[] in_lobbyTypes, IServerCallback callback)
    {
        try {    
        _regionsForLobbiesCallback = callback;
        JSONObject data = new JSONObject();
        data.put(Parameter.lobbyTypes.name(), in_lobbyTypes);

        ServerCall sc = new ServerCall(ServiceName.lobby,
        ServiceOperation.GET_REGIONS_FOR_LOBBIES, data, this);
        _client.sendRequest(sc);

        } catch (JSONException je) 
        {
            je.printStackTrace();
        }
    }   

    public void pingRegions(IServerCallback callback)
    {
        //we have our regions data, so now we can start pinging each region and its target if its PING type
        //Doing a fresh set of pinging on regions
        if(callback != null)
        {
            _pingSuccessCallback = callback;
        }

        //clear the cached pings
        Iterator cachedKeys = m_cachedPingResponses.keys();
        while(cachedKeys.hasNext())
            m_cachedPingResponses.remove((String)m_cachedPingResponses.keys().next());

        //reset the ping data
        Iterator pingDataKeys = m_pingData.keys();
        while(pingDataKeys.hasNext())
            m_pingData.remove((String)m_pingData.keys().next());
        
        //make temp variables for extra storage purposes
        String targetStr;

        //interpret the ping data
        if(m_regionPingData.length() > 0)
        {
            //grab the keys and iterate through them 
            Iterator <String> keys = m_regionPingData.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                try{
                    if (m_regionPingData.get(key) instanceof JSONObject) 
                    {
                        //is the key of type PING?
                        if(m_regionPingData.getJSONObject(key).getString("type").equals("PING"))
                        {
                            //update our cache with the regions so we can stroe ping values in individual arrays
                            ArrayList<Long> tempArr = new ArrayList<Long>();
                            m_cachedPingResponses.put(key, tempArr);
                            targetStr = m_regionPingData.getJSONObject(key).getString("target");

                            //now we want to setup the regions we want to process, and have a number based on the amount of ping calls we want to make
                            for(int i = 0; i < MAX_PING_CALLS; i++)
                            {
                                //get region and target and prepare to test
                                JSONObject keyValuePair = new JSONObject();
                                keyValuePair.put(key, targetStr);
                                m_regionTargetsToProcess.add(keyValuePair);
                            }
                        }
                    }
                }catch(JSONException je)
                {}
            }
            //start pinging
            PingNextItemToProcess();
        }
        else
        {
            _client.getRestClient().fakeErrorResponse(StatusCodes.BAD_REQUEST, ReasonCodes.MISSING_REQUIRED_PARAMETER, "No Regions to ping. Please call GetRegionsForLobbies and await the response before calling PingRegions");
        }

    }

    private void PingNextItemToProcess()
    {
        //check that there's regions to process
        if(m_regionTargetsToProcess.size() > 0)
        {
            String region;
            String target;
            for(int i = 0; i < NUM_PING_CALLS_IN_PARALLEL && m_regionTargetsToProcess.size() > 0; i++)
            {
                Iterator keys = m_regionTargetsToProcess.get(i).keys();
                region = keys.next().toString();
                try{
                target = m_regionTargetsToProcess.get(i).getString(region);
                
                m_cachedRegionArr =(ArrayList<Long>) m_cachedPingResponses.get(region);

                m_regionTargetsToProcess.remove(0);

                pingHost(region, target, m_cachedRegionArr.size());

                }catch(JSONException je){}
            }
        }
        else if(m_regionPingData.length() == m_pingData.length() && _pingSuccessCallback != null && _pingSuccessCallback.toString() != "undefined")
        {
            _pingSuccessCallback.serverCallback(ServiceName.lobby, ServiceOperation.PING_DATA, m_pingData);
        }
    }

    private void pingHost(String region, String target, int index)
    {
        //set up target
        String targetURL = "https://" + target;

        //store a start time for each region to allow parallel
        try
        {
        ArrayList<Long> arr;
        arr = (ArrayList<Long>) m_cachedPingResponses.get(region);
        arr.add(System.currentTimeMillis());
        }catch(JSONException je){}

        //make http request
        HttpURLConnection connection = null;
        try{
            connection = (HttpURLConnection) new URL(targetURL).openConnection();
            try {
                connection.setRequestMethod("GET");
            }catch(java.net.ProtocolException pe){}

            if(connection.getResponseCode() == 200)
            {
                handlePingResponse(region, index);
            }
        }catch(java.io.IOException io){}
    }

    private void handlePingResponse(String region, int index)
    {
        //get the ping time
        try{
            ArrayList<Long> cachedArr;
            cachedArr = (ArrayList<Long>) m_cachedPingResponses.get(region);
            long time = System.currentTimeMillis() - cachedArr.get(index);
            cachedArr.set(index, time);

            //we've reached our desired number of ping calls, so we now need to do some logic to get the average ping
            if(cachedArr.size() == MAX_PING_CALLS)
            {
                long totalAccumulated = 0;
                long highestValue = 0;

                for (Long pingResponse : cachedArr) 
                {
                    totalAccumulated += pingResponse;
                    if(pingResponse > highestValue)
                    {
                        highestValue = pingResponse;
                    }    
                }

                //accumulated all, now subtract the highest value
                totalAccumulated -= highestValue;
                m_pingData.put(region, totalAccumulated / (cachedArr.size() - 1));
            }

            PingNextItemToProcess();

        }catch(JSONException je){}

    }

    private void attachPingDataAndSend(JSONObject in_data, ServiceOperation in_operation, IServerCallback callback)
    {
        if(m_pingData != null && m_pingData.length() > 0)
        {
            try{
            in_data.put(Parameter.pingData.name(), m_pingData);
            ServerCall sc = new ServerCall(ServiceName.lobby,
            in_operation, in_data, callback);
            _client.sendRequest(sc);
            }catch(JSONException je){
                je.printStackTrace();
            }
        }
        else
        {
            //fake error response
            _client.getRestClient().fakeErrorResponse(StatusCodes.BAD_REQUEST, ReasonCodes.MISSING_REQUIRED_PARAMETER, 
            "Required parameter 'pingData' is missing. Please ensure 'pingData' exists by first calling GetRegionsForLobbies, then wait for the response and then call PingRegions");
        }
    }

    public void serverCallback(ServiceName serviceName, ServiceOperation serviceOperation, JSONObject jsonData)
    {
        if(serviceName.toString().equals("lobby") && serviceOperation.toString().equals("GET_REGIONS_FOR_LOBBIES"))
        {
            try {
                m_regionPingData = jsonData.getJSONObject("data").getJSONObject("regionPingData");
            }
            catch (JSONException je)
            {}

            if(_regionsForLobbiesCallback != null)
            {
                _regionsForLobbiesCallback.serverCallback(serviceName, serviceOperation, jsonData);
            }
        }
    }

    public void serverError(ServiceName serviceName, ServiceOperation serviceOperation, int statusCode, int reasonCode, String jsonError)
    {
        if(serviceName.toString().equals("lobby") && serviceOperation.toString().equals("GET_REGIONS_FOR_LOBBIES"))
        {
            _regionsForLobbiesCallback.serverError(serviceName, serviceOperation, statusCode, reasonCode, jsonError);
        }
    }

    private JSONObject m_pingData = new JSONObject();
    private JSONObject m_regionPingData = new JSONObject();
    private JSONObject m_lobbyTypeRegions = new JSONObject();
    private JSONObject m_cachedPingResponses = new JSONObject();
    private ArrayList<Long> m_cachedRegionArr = new ArrayList<Long>();
    private ArrayList<JSONObject> m_regionTargetsToProcess = new ArrayList<JSONObject>();
    private Object m_pingRegionObject;
    private IServerCallback _regionsForLobbiesCallback;
    private IServerCallback _pingSuccessCallback;
    private final Object _lock = new Object();
    private final int MAX_PING_CALLS = 4;
    private final int NUM_PING_CALLS_IN_PARALLEL = 2;
}
