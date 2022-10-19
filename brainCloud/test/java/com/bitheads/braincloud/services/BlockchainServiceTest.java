package com.bitheads.braincloud.services;

import com.bitheads.braincloud.client.BrainCloudClient;
import com.bitheads.braincloud.client.ReasonCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class BlockchainServiceTest extends TestFixtureBase {
    private String _defaultIntegrationId = "default";
    private String _defaultContextJson = "{}";
     
    @Test
    public void testGetBlockchainItems() throws Exception {
        TestResult tr = new TestResult(_wrapper);
        
        _wrapper.getClient().getBlockchainService().GetBlockchainItems(_defaultIntegrationId,
                _defaultContextJson, tr);

        tr.Run();
    }

    @Test
    public void testGetUniqs() throws Exception {
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getClient().getBlockchainService().GetUniqs(_defaultIntegrationId, _defaultContextJson, tr);

        tr.Run();
    }
    
}
