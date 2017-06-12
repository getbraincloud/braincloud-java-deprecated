package com.bitheads.braincloud.services;

import com.bitheads.braincloud.client.BrainCloudClient;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by prestonjennings on 15-09-02.
 */
public class PlayerStateServiceTest extends TestFixtureBase
{

    @Test
    public void testDeletePlayer() throws Exception
    {
        TestResult tr = new TestResult();

        BrainCloudClient.getInstance().getPlayerStateService().deleteUser(
                tr);
        tr.Run();
        BrainCloudClient.getInstance().getAuthenticationService().clearSavedProfileId();
        BrainCloudClient.getInstance().getAuthenticationService().authenticateUniversal(
                getUser(Users.UserA).id,
                getUser(Users.UserA).password,
                true,
                tr);
        tr.Run();
        getUser(Users.UserA).profileId = BrainCloudClient.getInstance().getAuthenticationService().getProfileId();
    }

    @Test
    public void testGetAttributes() throws Exception
    {
        TestResult tr = new TestResult();

        BrainCloudClient.getInstance().getPlayerStateService().getAttributes(
                tr);

        tr.Run();
    }

    @Test
    public void testLogout() throws Exception
    {
        TestResult tr = new TestResult();

        BrainCloudClient.getInstance().getPlayerStateService().logout(
                tr);

        tr.Run();
    }

    @Test
    public void testReadPlayerState() throws Exception
    {
        TestResult tr = new TestResult();

        BrainCloudClient.getInstance().getPlayerStateService().readUserState(
                tr);

        tr.Run();
    }

    @Test
    public void testRemoveAttributes() throws Exception
    {
        TestResult tr = new TestResult();

        BrainCloudClient.getInstance().getPlayerStateService().removeAttributes(
                new String[]{"testAttrib1", "testAttrib2"},
                tr);

        tr.Run();
    }

    @Test
    public void testResetPlayer() throws Exception
    {
        TestResult tr = new TestResult();

        BrainCloudClient.getInstance().getPlayerStateService().resetUser(
                tr);

        tr.Run();
    }

    @Test
    public void testUpdateAttributes() throws Exception
    {
        TestResult tr = new TestResult();

        JSONObject stats = new JSONObject();
        stats.put("testAttrib1", "value1");
        stats.put("testAttrib2", "value2");

        BrainCloudClient.getInstance().getPlayerStateService().updateAttributes(
                stats.toString(),
                false,
                tr);

        tr.Run();
    }

    @Test
    public void testUpdatePlayerName() throws Exception
    {
        TestResult tr = new TestResult();

        BrainCloudClient.getInstance().getPlayerStateService().updateUserName(
                "ABC",
                tr);

        tr.Run();
    }


    @Test
    public void testUpdateSummaryFriendData() throws Exception
    {
        TestResult tr = new TestResult();

        JSONObject friendData = new JSONObject();
        friendData.put("field", "value");
        BrainCloudClient.getInstance().getPlayerStateService().updateSummaryFriendData(
                friendData.toString(),
                tr);

        tr.Run();
    }

    @Test
    public void testUpdatePlayerPictureUrl() throws Exception
    {
        TestResult tr = new TestResult();

        BrainCloudClient.getInstance().getPlayerStateService().updateUserPictureUrl(
                "https://some.domain.com/mypicture.jpg",
                tr);

        tr.Run();
    }

    @Test
    public void testUpdateContactEmail() throws Exception
    {
        TestResult tr = new TestResult();

        BrainCloudClient.getInstance().getPlayerStateService().updateContactEmail(
                "something@bctestdomain.com",
                tr);

        tr.Run();
    }
}