package com.bitheads.braincloud.services;

import com.bitheads.braincloud.client.BrainCloudClient;
import com.bitheads.braincloud.client.IServerCallback;
import com.bitheads.braincloud.client.ServiceName;
import com.bitheads.braincloud.client.ServiceOperation;
import com.bitheads.braincloud.comms.ServerCall;

import org.json.JSONException;
import org.json.JSONObject;

public class BrainCloudBlockchainService {

    private enum Parameter{
        integrationId,
        contextJson
    }

    private BrainCloudClient _client;

    public BrainCloudBlockchain(BrainCloudClient client){
        _client = client;
    }

    public void GetBlockchainItems(String in_integrationID = "default",
                                   String in_contextJson = "{}",
                                   IServerCallback callback){
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.integrationId.name(), in_integrationID);

            JSONObject jsonData = new JSONObject(in_contextJson);
            data.put(Parameter.contextJson.name(), in_contextJson);

            ServerCall serverCall = new ServerCall(ServiceName.blockchain,
                    ServiceOperation.GET_BLOCKCHAIN_ITEMS, data, callback);
            _client.sendRequest(serverCall);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void GetUniqs(String in_integrationID = "default",
                         String in_contextJson = "{}",
                         IServerCallback callback){
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.integrationId.name(), in_integrationID);

            JSONObject jsonData = new JSONObject(in_contextJson);
            data.put(Parameter.contextJson.name(), in_contextJson);

            ServerCall serverCall = new ServerCall(ServiceName.blockchain,
                    ServiceOperation.GET_UNIQS, data, callback);
            _client.sendRequest(serverCall);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}