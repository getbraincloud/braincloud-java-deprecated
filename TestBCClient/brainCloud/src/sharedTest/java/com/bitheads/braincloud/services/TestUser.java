package com.bitheads.braincloud.services;

import com.bitheads.braincloud.client.BrainCloudClient;

import org.json.JSONException;

/**
 * Created by prestonjennings on 15-09-01.
 */

/// <summary>
/// Holds data for a randomly generated user
/// </summary>
public class TestUser
{
    public String id = "";
    public String password = "";
    public String profileId = "";
    public String email = "";

    public TestUser(String idPrefix, int suffix)
    {
        id = idPrefix + suffix;
        password = id;
        email = id + "@bctestuser.com";
        Authenticate();
    }

    private void Authenticate()
    {
        TestResult tr = new TestResult();
        BrainCloudClient.getInstance().getAuthenticationService().authenticateUniversal(
                id,
                password,
                true,
                tr);
        tr.Run();
        profileId = BrainCloudClient.getInstance().getAuthenticationService().getProfileId();

        try
        {
            if (tr.m_response.getJSONObject("data").getBoolean("newUser") == true)
            {
                BrainCloudClient.getInstance().getMatchMakingService().enableMatchMaking(tr);
                tr.Run();
                BrainCloudClient.getInstance().getPlayerStateService().updateUserName(id, tr);
                tr.Run();
                BrainCloudClient.getInstance().getPlayerStateService().updateContactEmail("braincloudunittest@gmail.com", tr);
                tr.Run();
            }

            BrainCloudClient.getInstance().getPlayerStateService().logout(tr);
            tr.Run();
        }
        catch(JSONException je)
        {
            je.printStackTrace();
        }
    }
}
