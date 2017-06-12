package com.bitheads.braincloud.services;

import com.bitheads.braincloud.client.AuthenticationType;
import com.bitheads.braincloud.client.BrainCloudClient;
import com.bitheads.braincloud.client.ReasonCodes;
import com.bitheads.braincloud.client.StatusCodes;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by prestonjennings on 15-10-05.
 */
public class CommsTest extends TestFixtureNoAuth
{
    /*
    @Test
    public void testDevBadAuthNoRetry() throws Exception
    {

        // this should always succeed but helps devs verify that only one auth packet gets sent

        TestResult tr = new TestResult();
        BrainCloudClient.getInstance().initialize("123", "123", "1.0.0", "http://localhost:5432");
        BrainCloudClient.getInstance().getAuthenticationService().authenticateUniversal("abc", "123", true, tr);
        tr.RunExpectFail(-1, -1);

        BrainCloudClient.getInstance().resetCommunication();
    }

    @Test
    public void testDevBad503() throws Exception
    {
        // this test assumes you're running a server that returns 503
        TestResult tr = new TestResult();
        BrainCloudClient.getInstance().initialize("123", "123", "1.0.0", "http://localhost:5432");
        // don't authenticate as we want retries to happen
        BrainCloudClient.getInstance().getPlayerStateService().getAttributes(tr);
        tr.RunExpectFail(StatusCodes.CLIENT_NETWORK_ERROR, ReasonCodes.CLIENT_NETWORK_ERROR_TIMEOUT);

        BrainCloudClient.getInstance().resetCommunication();
    }
    */

    @Override
    public boolean shouldAuthenticate() {
        return false;
    }

    @Test
    public void testGlobalErrorHandler() throws Exception
    {
        TestResult tr = new TestResult();
        BrainCloudClient.getInstance().registerGlobalErrorCallback(tr);

        int globalErrorCount = 0;

        BrainCloudClient.getInstance().getTimeService().readServerTime(tr);
        tr.RunExpectFail(StatusCodes.FORBIDDEN, ReasonCodes.NO_SESSION);
        globalErrorCount += tr.m_globalErrorCount;

        BrainCloudClient.getInstance().getTimeService().readServerTime(tr);
        tr.RunExpectFail(StatusCodes.FORBIDDEN, ReasonCodes.NO_SESSION);
        globalErrorCount += tr.m_globalErrorCount;

        BrainCloudClient.getInstance().deregisterGlobalErrorCallback();
        Assert.assertEquals(2, globalErrorCount);

        BrainCloudClient.getInstance().resetCommunication();
    }

    @Test
    public void testNoSession() throws Exception
    {
        TestResult tr = new TestResult();

        BrainCloudClient.getInstance().getTimeService().readServerTime(tr);
        tr.RunExpectFail(StatusCodes.FORBIDDEN, ReasonCodes.NO_SESSION);

        System.out.println(tr.m_statusMessage);

        BrainCloudClient.getInstance().resetCommunication();
    }

    @Test
    public void testSessionTimeout() throws Exception
    {
        // this test assumes you're running a server that returns 503
        TestResult tr = new TestResult();

        BrainCloudClient.getInstance().getAuthenticationService().authenticateUniversal(getUser(Users.UserA).id, getUser(Users.UserA).password, true, tr);
        tr.Run();

        long prevSessionTimeout = BrainCloudClient.getInstance().getHeartbeatInterval();
        BrainCloudClient.getInstance().setHeartbeatInterval(prevSessionTimeout * 4);

        System.out.println("Waiting for session to timeout...");

        Thread.sleep(61 * 1000);

        BrainCloudClient.getInstance().getTimeService().readServerTime(tr);
        tr.RunExpectFail(StatusCodes.FORBIDDEN, ReasonCodes.USER_SESSION_EXPIRED);

        BrainCloudClient.getInstance().getTimeService().readServerTime(tr);
        tr.RunExpectFail(StatusCodes.FORBIDDEN, ReasonCodes.USER_SESSION_EXPIRED);

        BrainCloudClient.getInstance().getAuthenticationService().authenticateUniversal(getUser(Users.UserA).id, getUser(Users.UserA).password, true, tr);
        tr.Run();

        BrainCloudClient.getInstance().getPlayerStateService().logout(tr);
        tr.Run();

        BrainCloudClient.getInstance().getTimeService().readServerTime(tr);
        tr.RunExpectFail(StatusCodes.FORBIDDEN, ReasonCodes.NO_SESSION);

        BrainCloudClient.getInstance().resetCommunication();
        BrainCloudClient.getInstance().setHeartbeatInterval(prevSessionTimeout);
    }

    @Test
    public void testErrorCallback() throws Exception
    {
        BrainCloudClient.getInstance().initialize(m_appId, m_secret, m_appVersion, m_serverUrl);
        BrainCloudClient.getInstance().enableLogging(true);

        TestResult tr = new TestResult();
        BrainCloudClient.getInstance().getEntityService().createEntity("type", "{}", "", tr);
        tr.RunExpectFail(-1, -1);
        Assert.assertTrue(tr.m_statusMessage.startsWith("{"));

        tr.Reset();
        BrainCloudClient.getInstance().setOldStyleStatusMessageErrorCallback(true);
        BrainCloudClient.getInstance().getEntityService().createEntity("type", "{}", "", tr);
        tr.RunExpectFail(-1, -1);
        Assert.assertFalse(tr.m_statusMessage.startsWith("{"));

        BrainCloudClient.getInstance().initialize(m_appId, m_secret, m_appVersion, "https://localhost:5432");

        tr.Reset();
        BrainCloudClient.getInstance().setOldStyleStatusMessageErrorCallback(false);
        BrainCloudClient.getInstance().getEntityService().createEntity("type", "{}", "", tr);
        tr.RunExpectFail(-1, -1);
        Assert.assertTrue(tr.m_statusMessage.startsWith("{"));

        tr.Reset();
        BrainCloudClient.getInstance().setOldStyleStatusMessageErrorCallback(true);
        BrainCloudClient.getInstance().getEntityService().createEntity("type", "{}", "", tr);
        tr.RunExpectFail(-1, -1);
        Assert.assertFalse(tr.m_statusMessage.startsWith("{"));

        BrainCloudClient.getInstance().setOldStyleStatusMessageErrorCallback(false);
    }

    @Test
    public void testMessageCache() throws Exception
    {
        TestResult tr = new TestResult();
        BrainCloudClient bcc = BrainCloudClient.getInstance();

        bcc.initialize(m_appId, m_secret, m_appVersion, m_serverUrl + "failunittest");
        bcc.enableLogging(true);
        bcc.registerNetworkErrorCallback(tr);
        bcc.registerGlobalErrorCallback(tr);
        bcc.enableNetworkErrorMessageCaching(true);

        ArrayList<Integer> packetTimeouts = new ArrayList<Integer>();
        packetTimeouts.add(1);
        packetTimeouts.add(1);
        packetTimeouts.add(1);
        bcc.setPacketTimeouts(packetTimeouts);

        tr.setMaxWait(30);

        int networkErrorCount = 0;
        int globalErrorCount = 0;

        System.out.println("Authenticate Universal");
        bcc.getAuthenticationService().authenticateUniversal("abc", "abc", true, tr);
        tr.RunExpectFail(StatusCodes.CLIENT_NETWORK_ERROR, ReasonCodes.CLIENT_NETWORK_ERROR_TIMEOUT);
        networkErrorCount += tr.m_networkErrorCount;

        System.out.println("retryCachedMessages");
        bcc.retryCachedMessages();
        tr.RunExpectFail(StatusCodes.CLIENT_NETWORK_ERROR, ReasonCodes.CLIENT_NETWORK_ERROR_TIMEOUT);
        networkErrorCount += tr.m_networkErrorCount;

        System.out.println("flushCachedMessages");
        bcc.flushCachedMessages(true);
        bcc.runCallbacks();
        globalErrorCount += tr.m_globalErrorCount;

        bcc.enableNetworkErrorMessageCaching(false);
        bcc.deregisterNetworkErrorCallback();
        bcc.deregisterGlobalErrorCallback();
        bcc.setPacketTimeoutsToDefault();
        bcc.resetCommunication();

        Assert.assertEquals(2, networkErrorCount);
        Assert.assertEquals(1, globalErrorCount);
    }

    @Test(timeout=600000)
    public void testMessageBundleMarker() throws Exception
    {
        TestResult tr = new TestResult();
        tr.setMaxWait(600);
        BrainCloudClient bcc = BrainCloudClient.getInstance();

        //bcc.initialize(m_appId, m_secret, m_appVersion, m_serverUrl);
        //bcc.enableLogging(true);
        BrainCloudClient.getInstance().getAuthenticationService().authenticateUniversal("abc", "abc", true, tr);
        BrainCloudClient.getInstance().insertEndOfMessageBundleMarker();
        BrainCloudClient.getInstance().getPlayerStatisticsService().readAllUserStats(tr);
        BrainCloudClient.getInstance().insertEndOfMessageBundleMarker();
        BrainCloudClient.getInstance().getPlayerStatisticsService().readAllUserStats(tr);
        BrainCloudClient.getInstance().getPlayerStatisticsService().readAllUserStats(tr);

        // messages launch right away so only need to call run twice

        tr.Run();
        tr.Run();
        tr.Run();
    }

    @Test(timeout=10000000)
    public void testAuthFirst() throws Exception
    {
        TestUser user = getUser(Users.UserA);
        TestResult tr = new TestResult();
        BrainCloudClient bcc = BrainCloudClient.getInstance();

        bcc.getPlayerStatisticsService().readAllUserStats(tr);
        bcc.insertEndOfMessageBundleMarker();

        bcc.getPlayerStatisticsService().readAllUserStats(tr);
        bcc.getAuthenticationService().authenticateUniversal(user.id, user.password, true, tr);

        tr.RunExpectFail(403, ReasonCodes.NO_SESSION);
        tr.Run();
        tr.Run();


        tr.setMaxWait(30);
    }

    //@Test
    public void testKillSwitch() throws Exception
    {
        TestResult tr = new TestResult();

        BrainCloudClient.getInstance().getAuthenticationService().authenticateUniversal(
                getUser(Users.UserA).id, getUser(Users.UserA).password, true, tr);
        tr.Run();

        for (int i  = 0; i < 3; i++)
        {
            BrainCloudClient.getInstance().getIdentityService().refreshIdentity(
                    "fail", "fail", AuthenticationType.Universal, tr);
            tr.Run(true);
        }

        BrainCloudClient.getInstance().getTimeService().readServerTime(tr);
        tr.Run();

        for (int i  = 0; i < 12; i++)
        {
            BrainCloudClient.getInstance().getIdentityService().refreshIdentity(
                    "fail", "fail", AuthenticationType.Universal, tr);
            tr.Run(true);
        }

        BrainCloudClient.getInstance().getTimeService().readServerTime(tr);
        tr.RunExpectFail(900, ReasonCodes.CLIENT_DISABLED);
    }
}