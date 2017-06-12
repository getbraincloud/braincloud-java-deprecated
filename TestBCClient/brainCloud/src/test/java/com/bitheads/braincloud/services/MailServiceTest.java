package com.bitheads.braincloud.services;

import com.bitheads.braincloud.client.BrainCloudClient;
import org.json.JSONObject;
import org.junit.Test;

public class MailServiceTest extends TestFixtureBase {

    @Test
    public void testSendBasicEmail() throws Exception {
        TestResult tr = new TestResult();

        BrainCloudClient.getInstance().getMailService().sendBasicEmail(
                getUser(Users.UserA).profileId,
                "Test Subject - TestSendBasicEmail",
                "Test body content message.",
                tr);

        tr.Run();
    }

    @Test
    public void testSendAdvancedEmailSendGrid() throws Exception {
        TestResult tr = new TestResult();

        JSONObject data = new JSONObject();
        data.put("subject", "Test Subject - TestSendAdvancedEmailSendGrid");
        data.put("body", "Test body");
        data.put("categories", new String[]{"unit-test"});

        BrainCloudClient.getInstance().getMailService().sendAdvancedEmail(
                getUser(Users.UserA).profileId,
                data.toString(),
                tr);

        tr.Run();
    }

    @Test
    public void testSendAdvancedEmailByAddress() throws Exception {
        TestResult tr = new TestResult();

        JSONObject data = new JSONObject();
        data.put("subject", "Test Subject - TestSendAdvancedEmailByAddress");
        data.put("body", "Test body");
        data.put("categories", new String[]{"unit-test"});

        BrainCloudClient.getInstance().getMailService().sendAdvancedEmailByAddress(
                getUser(Users.UserA).email,
                data.toString(),
                tr);

        tr.Run();
    }
}