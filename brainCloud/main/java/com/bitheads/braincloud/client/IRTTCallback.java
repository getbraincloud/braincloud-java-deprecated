package com.bitheads.braincloud.client;

import org.json.JSONObject;

public interface IRTTCallback {
    void rttConnected();
    void rttError(String errorMessage);
    void rttEvent(JSONObject eventJson);
}
