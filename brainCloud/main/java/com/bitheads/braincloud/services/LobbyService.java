package com.bitheads.braincloud.services;

import com.bitheads.braincloud.client.BrainCloudClient;
import com.bitheads.braincloud.client.IServerCallback;
import com.bitheads.braincloud.client.ServiceName;
import com.bitheads.braincloud.client.ServiceOperation;
import com.bitheads.braincloud.comms.ServerCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by David St-Louis on 2018-07-04
 */
public class LobbyService {

    private enum Parameter {
        lobbyType,
        maxSteps,
        rating,
        algo,
        filterJson,
        otherUserCxIds,
        settings,
        isReady,
        extraJson,
        teamCode
    }

    private BrainCloudClient _client;

    public LobbyService(BrainCloudClient client) {
        _client = client;
    }

    public void findOrCreateLobby(String lobbyType, int rating, int maxSteps, String algoJson, String filterJson, ArrayList<String> otherUserCxIds, String settingsJson, Boolean isReady, String extraJson, String teamCode, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.lobbyType.name(), lobbyType);
            data.put(Parameter.rating.name(), rating);
            data.put(Parameter.maxSteps.name(), maxSteps);
            if (StringUtil.IsOptionalParameterValid(algoJson)) {
                data.put(Parameter.algo.name(), new JSONObject(algoJson));
            }
            if (StringUtil.IsOptionalParameterValid(algoJson)) {
                data.put(Parameter.filterJson.name(), new JSONObject(algoJson));
            }
            if (otherUserCxIds != null) {
                data.put(Parameter.otherUserCxIds.name(), new JSONArray(otherUserCxIds));
            }
            if (StringUtil.IsOptionalParameterValid(settingsJson)) {
                data.put(Parameter.settings.name(), new JSONObject(settingsJson));
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
}
