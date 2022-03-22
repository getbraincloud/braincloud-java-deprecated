package com.bitheads.braincloud.client;

import com.bitheads.braincloud.client.IServerCallback;
import org.json.JSONObject;

public class SmartSwitchCallback implements IServerCallback 
{
    protected BrainCloudWrapper _wrapper;
    protected IServerCallback _callback;

    public SmartSwitchCallback(BrainCloudWrapper in_wrapper, IServerCallback in_callback) 
    {
        _wrapper = in_wrapper;
        _callback = in_callback;
    }

    public void clearIds() 
    {
        _wrapper.resetStoredAnonymousId();
        _wrapper.resetStoredProfileId();
        _wrapper.getClient().getAuthenticationService().clearSavedProfileId();
    }

    public void serverCallback(ServiceName serviceName, ServiceOperation serviceOperation, JSONObject jsonData) 
    {
        _callback.serverCallback(serviceName, serviceOperation, jsonData);
    }

    public void serverError(ServiceName in_serviceName, ServiceOperation in_serviceOperation, int in_statusCode, int in_reasonCode, String jsonString) 
    {
        _callback.serverError(in_serviceName, in_serviceOperation, in_statusCode, in_reasonCode, jsonString);
    }

    public class SmartSwitchEmail extends SmartSwitchCallback 
    {
        String _email;
        String _password;
        boolean _forceCreate;

        public SmartSwitchEmail(BrainCloudWrapper in_wrapper, IServerCallback in_callback) 
        {
            super(in_wrapper, in_callback);
        }

        public SmartSwitchEmail(String in_email, String in_password, boolean in_forceCreate, BrainCloudWrapper in_wrapper, IServerCallback in_callback) 
        {
            super(in_wrapper, in_callback);
            _email = in_email;
            _password = in_password;
            _forceCreate = in_forceCreate;
        }

        public void serverCallback(ServiceName in_serviceName, ServiceOperation serviceOperation, String jsonString) 
        {
            clearIds();
            _wrapper.getClient().getAuthenticationService().authenticateEmailPassword(_email, _password, _forceCreate, _callback);
        }
    }

    public class SmartSwitchExternal extends SmartSwitchCallback
    {
        String _userId;
        String _token;
        String _externalAuthName;
        boolean _forceCreate;

        public SmartSwitchExternal(BrainCloudWrapper in_wrapper, IServerCallback in_callback)
        {
            super(in_wrapper, in_callback);
        }

        public SmartSwitchExternal(String in_userId, String in_token, String in_externalAuthName, boolean in_forceCreate, BrainCloudWrapper in_wrapper, IServerCallback in_callback)
        {
            super(in_wrapper, in_callback);
            _userId = in_userId;
            _token = in_token;
            _externalAuthName = in_externalAuthName;
            _forceCreate = in_forceCreate;
        }

        public void serverCallback(ServiceName in_serviceName, ServiceOperation serviceOperation, String jsonString) 
        {
            clearIds();
            _wrapper.getClient().getAuthenticationService().authenticateExternal(_userId, _token, _externalAuthName, _forceCreate, _callback);
        }
    }

    public class SmartSwitchFacebook extends SmartSwitchCallback
    {
        String _fbUserId;
        String _fbAuthToken;
        boolean _forceCreate;

        public SmartSwitchFacebook(BrainCloudWrapper in_wrapper, IServerCallback in_callback)
        {
            super(in_wrapper, in_callback);
        }

        public SmartSwitchFacebook(String in_fbUserId, String in_fbAuthToken, boolean in_forceCreate, BrainCloudWrapper in_wrapper, IServerCallback in_callback)
        {
            super(in_wrapper, in_callback);
            _fbUserId = in_fbUserId;
            _fbAuthToken = in_fbAuthToken;
            _forceCreate = in_forceCreate;
        }

        public void serverCallback(ServiceName in_serviceName, ServiceOperation serviceOperation, String jsonString)
        {
            clearIds();
            _wrapper.getClient().getAuthenticationService().authenticateFacebook(_fbUserId, _fbAuthToken, _forceCreate, _callback);
        }
    }

    public class SmartSwitchOculus extends SmartSwitchCallback
    {
        String _oculusUserId;
        String _oculusNonce;
        boolean _forceCreate;

        public SmartSwitchOculus(BrainCloudWrapper in_wrapper, IServerCallback in_callback)
        {
            super(in_wrapper, in_callback);
        }

        public SmartSwitchOculus(String in_oculusUserId, String in_oculusNonce, boolean in_forceCreate, BrainCloudWrapper in_wrapper, IServerCallback in_callback)
        {
            super(in_wrapper, in_callback);
            _oculusUserId = in_oculusUserId;
            _oculusNonce = in_oculusNonce;
            _forceCreate = in_forceCreate;
        }

        public void serverCallback(ServiceName in_serviceName, ServiceOperation serviceOperation, String jsonString)
        {
            clearIds();
            _wrapper.getClient().getAuthenticationService().authenticateOculus(_oculusUserId, _oculusNonce, _forceCreate, _callback);
        }
    }

    public class SmartSwitchGoogle extends SmartSwitchCallback
    {
        String _googleUserId;
        String _serverAuthCode;
        boolean _forceCreate;

        public SmartSwitchGoogle(BrainCloudWrapper in_wrapper, IServerCallback in_callback)
        {
            super(in_wrapper, in_callback);
        }

        public SmartSwitchGoogle(String in_googleUserId, String in_serverAuthCode, boolean in_forceCreate, BrainCloudWrapper in_wrapper, IServerCallback in_callback)
        {
            super(in_wrapper, in_callback);
            _googleUserId = in_googleUserId;
            _serverAuthCode = in_serverAuthCode;
            _forceCreate = in_forceCreate;
        }

        public void serverCallback(ServiceName in_serviceName, ServiceOperation serviceOperation, String jsonString)
        {
            clearIds();
            _wrapper.getClient().getAuthenticationService().authenticateGoogle(_googleUserId, _serverAuthCode, _forceCreate, _callback);
        }
    }

    public class SmartSwitchGoogleOpenId extends SmartSwitchCallback
    {
        String _googleUserAccountEmail;
        String _IdToken;
        boolean _forceCreate;

        public SmartSwitchGoogleOpenId(BrainCloudWrapper in_wrapper, IServerCallback in_callback)
        {
            super(in_wrapper, in_callback);
        }

        public SmartSwitchGoogleOpenId(String in_googleUserAccountEmail, String in_IdToken, boolean in_forceCreate, BrainCloudWrapper in_wrapper, IServerCallback in_callback)
        {
            super(in_wrapper, in_callback);
            _googleUserAccountEmail = in_googleUserAccountEmail;
            _IdToken = in_IdToken;
            _forceCreate = in_forceCreate;
        }

        public void serverCallback(ServiceName in_serviceName, ServiceOperation serviceOperation, String jsonString)
        {
            clearIds();
            _wrapper.getClient().getAuthenticationService().authenticateGoogle(_googleUserAccountEmail, _IdToken, _forceCreate, _callback);
        }
    }

    public class SmartSwitchApple extends SmartSwitchCallback
    {
        String _appleUserId;
        String _token;
        boolean _forceCreate;

        public SmartSwitchApple(BrainCloudWrapper in_wrapper, IServerCallback in_callback)
        {
            super(in_wrapper, in_callback);
        }

        public SmartSwitchApple(String in_appleUserId, String in_token, boolean in_forceCreate, BrainCloudWrapper in_wrapper, IServerCallback in_callback)
        {
            super(in_wrapper, in_callback);
            _appleUserId = in_appleUserId;
            _token = in_token;
            _forceCreate = in_forceCreate;
        }

        public void serverCallback(ServiceName in_serviceName, ServiceOperation serviceOperation, String jsonString)
        {
            clearIds();
            _wrapper.getClient().getAuthenticationService().authenticateApple(_appleUserId, _token, _forceCreate, _callback);
        }
    }

    public class SmartSwitchSteam extends SmartSwitchCallback
    {
        String _steamUserId;
        String _sessionTicket;
        boolean _forceCreate;

        public SmartSwitchSteam(BrainCloudWrapper in_wrapper, IServerCallback in_callback)
        {
            super(in_wrapper, in_callback);
        }

        public SmartSwitchSteam(String in_steamUserId, String in_sessionTicket, boolean in_forceCreate, BrainCloudWrapper in_wrapper, IServerCallback in_callback)
        {
            super(in_wrapper, in_callback);
            _steamUserId = in_steamUserId;
            _sessionTicket = in_sessionTicket;
            _forceCreate = in_forceCreate;
        }

        public void serverCallback(ServiceName in_serviceName, ServiceOperation serviceOperation, String jsonString)
        {
            clearIds();
            _wrapper.getClient().getAuthenticationService().authenticateSteam(_steamUserId, _sessionTicket, _forceCreate, _callback);
        }
    }

    public class SmartSwitchTwitter extends SmartSwitchCallback
    {
        String _userId;
        String _token;
        String _secret;
        boolean _forceCreate;

        public SmartSwitchTwitter(BrainCloudWrapper in_wrapper, IServerCallback in_callback)
        {
            super(in_wrapper, in_callback);
        }

        public SmartSwitchTwitter(String in_userId, String in_token, String in_secret, boolean in_forceCreate, BrainCloudWrapper in_wrapper, IServerCallback in_callback)
        {
            super(in_wrapper, in_callback);
            _userId = in_userId;
            _token = in_token;
            _secret = in_secret;
            _forceCreate = in_forceCreate;
        }

        public void serverCallback(ServiceName in_serviceName, ServiceOperation serviceOperation, String jsonString) 
        {
            clearIds();
            _wrapper.getClient().getAuthenticationService().authenticateTwitter(_userId, _token, _secret, _forceCreate, _callback);
        }
    }

    public class SmartSwitchUniversal extends SmartSwitchCallback 
    {
        String _userId;
        String _password;
        boolean _forceCreate;

        public SmartSwitchUniversal(BrainCloudWrapper in_wrapper, IServerCallback in_callback) 
        {
            super(in_wrapper, in_callback);
        }

        public SmartSwitchUniversal(String in_userId, String in_password, boolean in_forceCreate, BrainCloudWrapper in_wrapper, IServerCallback in_callback) 
        {
            super(in_wrapper, in_callback);
            _userId = in_userId;
            _password = in_password;
            _forceCreate = in_forceCreate;
        }

        public void serverCallback(ServiceName in_serviceName, ServiceOperation serviceOperation, String jsonString) 
        {
            clearIds();
            _wrapper.getClient().getAuthenticationService().authenticateUniversal(_userId, _password, _forceCreate, _callback);
        }
    }

    public class SmartSwitchUltra extends SmartSwitchCallback 
    {
        String _ultraUserId;
        String _ultraIdToken;
        boolean _forceCreate;

        public SmartSwitchUltra(BrainCloudWrapper in_wrapper, IServerCallback in_callback) 
        {
            super(in_wrapper, in_callback);
        }

        public SmartSwitchUltra(String in_ultraUserId, String in_ultraIdToken, boolean in_forceCreate, BrainCloudWrapper in_wrapper, IServerCallback in_callback) 
        {
            super(in_wrapper, in_callback);
            _ultraUserId = in_ultraUserId;
            _ultraIdToken = in_ultraIdToken;
            _forceCreate = in_forceCreate;
        }

        public void serverCallback(ServiceName in_serviceName, ServiceOperation serviceOperation, String jsonString) 
        {
            clearIds();
            _wrapper.getClient().getAuthenticationService().authenticateUltra(_ultraUserId, _ultraIdToken, _forceCreate, _callback);
        }
    }

    public class SmartSwitchAdvanced extends SmartSwitchCallback 
    {
        AuthenticationType _authenticationType;
        AuthenticationIds _ids;
        boolean _forceCreate;
        String _extraJson;

        public SmartSwitchAdvanced(BrainCloudWrapper in_wrapper, IServerCallback in_callback) 
        {
            super(in_wrapper, in_callback);

        }

        public SmartSwitchAdvanced(AuthenticationType in_authenticationType, AuthenticationIds in_ids, boolean in_forceCreate, String in_extraJson, BrainCloudWrapper in_wrapper, IServerCallback in_callback) 
        {
            super(in_wrapper, in_callback);
            _authenticationType = in_authenticationType;
            _ids = in_ids;
            _forceCreate = in_forceCreate;
            _extraJson = in_extraJson;
        }

        public void serverCallback(ServiceName in_serviceName, ServiceOperation serviceOperation, String jsonString) 
        {
            clearIds();
            _wrapper.getClient().getAuthenticationService().authenticateAdvanced(_authenticationType, _ids, _forceCreate, _extraJson, _callback);
        }
    }
}
