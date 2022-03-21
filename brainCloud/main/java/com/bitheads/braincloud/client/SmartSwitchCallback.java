package com.bitheads.braincloud.client;

import com.bitheads.braincloud.client.IServerCallback;
import org.json.JSONObject;

public class SmartSwitchCallback implements IServerCallback 
{
    public BrainCloudWrapper _wrapper;
    public IServerCallback _callback;

    public EmailSmartSwitch EmailSmartSwitch;

    public SmartSwitchCallback(BrainCloudWrapper in_wrapper, IServerCallback in_callback) 
    {
        _wrapper = in_wrapper;
        _callback = in_callback;
    }

    public void SetUpEmailSmartSwitch(String in_email, String in_password, boolean in_forceCreate) 
    {
        EmailSmartSwitch = new EmailSmartSwitch(_wrapper, _callback);
        EmailSmartSwitch._email = in_email;
        EmailSmartSwitch._password = in_password;
        EmailSmartSwitch._forceCreate = in_forceCreate;
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

    public class EmailSmartSwitch extends SmartSwitchCallback 
    {
        public String _email;
        public String _password;
        public boolean _forceCreate;

        public EmailSmartSwitch(BrainCloudWrapper in_wrapper, IServerCallback in_callback) 
        {
            super(in_wrapper, in_callback);
            _wrapper = in_wrapper;
            _callback = in_callback;
        }

        public void serverCallback(ServiceName in_serviceName, ServiceOperation serviceOperation, String jsonString) 
        {
            clearIds();
            _wrapper.getClient().getAuthenticationService().authenticateEmailPassword(_email, _password, _forceCreate, _callback);
        }
    }

    public class AdvancedSmartSwitch extends SmartSwitchCallback 
    {

        public AdvancedSmartSwitch(BrainCloudWrapper in_wrapper, IServerCallback in_callback) 
        {
            super(in_wrapper, in_callback);

        }
    }
}
