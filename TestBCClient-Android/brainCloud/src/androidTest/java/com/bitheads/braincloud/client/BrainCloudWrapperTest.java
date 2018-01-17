package com.bitheads.braincloud.client;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.bitheads.braincloud.services.TestFixtureNoAuth;
import com.bitheads.braincloud.services.TestResult;


import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class BrainCloudWrapperTest extends TestFixtureNoAuth
{
    @Before
    public void setUp() throws Exception
    {
        super.setUp();

        // this forces us to create a new anonymous account
        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();

        _wrapper.setContext(ctx);
        _wrapper.setStoredAnonymousId("");
        _wrapper.setStoredProfileId("");
    }

    @Test
    public void testAuthenticateAnonymous()
    {
        _wrapper.initialize(m_appId, m_secret, m_appVersion, m_serverUrl);

        TestResult tr = new TestResult(_wrapper);
        _wrapper.authenticateAnonymous(tr);
        tr.Run();

        String anonId = _wrapper.getStoredAnonymousId();
        String profileId = _wrapper.getStoredProfileId();

        Logout();
        _wrapper.getClient().getAuthenticationService().clearSavedProfileId();

        _wrapper.authenticateAnonymous(tr);
        tr.Run();

        Assert.assertEquals(anonId, _wrapper.getStoredAnonymousId());
        Assert.assertEquals(profileId, _wrapper.getStoredProfileId());

        Logout();
    }

    @Test
    public void testAuthenticateEmailPassword()
    {
        _wrapper.initialize(m_appId, m_secret, m_appVersion, m_serverUrl);

        String email = getUser(Users.UserA).email;
        email += "_wrapper";

        TestResult tr = new TestResult(_wrapper);
        _wrapper.authenticateEmailPassword(email, getUser(Users.UserA).password, true, tr);
        tr.Run();

        Logout();
    }

    @Test
    public void testAuthenticateUniversal()
    {
        _wrapper.initialize(m_appId, m_secret, m_appVersion, m_serverUrl);

        TestResult tr = new TestResult(_wrapper);
        String uid = getUser(Users.UserA).id;
        uid += "_wrapper";
        _wrapper.authenticateUniversal(uid, getUser(Users.UserA).password, true, tr);
        tr.Run();

        Logout();
    }

    @Test
    public void testReconnect()
    {
        _wrapper.initialize(m_appId, m_secret, m_appVersion, m_serverUrl);

        TestResult tr = new TestResult(_wrapper);
        String uid = getUser(Users.UserA).id;
        uid += "_wrapper";
        _wrapper.authenticateUniversal(uid, getUser(Users.UserA).password, true, tr);
        tr.Run();

        _wrapper.getClient().getPlayerStateService().logout(tr);
        tr.Run();

        _wrapper.reconnect(tr);
        tr.Run();

        Logout();
    }
/*
    @Test
    public void testVerifyAlwaysAllowProfileFalse()
    {
        BrainCloudWrapper bcw = BrainCloudWrapper.getInstance();
        bcw.initialize(m_appId, m_secret, m_appVersion, m_serverUrl);
        bcw.setAlwaysAllowProfileSwitch(false);

        // this forces us to create a new anonymous account
        bcw.setStoredAnonymousId("");
        bcw.setStoredProfileId("");

        TestResult tr = new TestResult(_wrapper);
        bcw.authenticateAnonymous(tr);
        tr.Run();

        String anonId = bcw.getStoredAnonymousId();
        String profileId = bcw.getStoredProfileId();
        String uid = "aaa";//getUser(Users.UserA).id;
        uid += "_wrapperVerifyAlwaysAllowProfileFalse";

        BrainCloudWrapper.getBC().getIdentityService().attachUniversalIdentity(uid, uid, tr);
        tr.Run();

        Logout();
        BrainCloudWrapper.getBC().getAuthenticationService().clearSavedProfileId();

        bcw.authenticateUniversal(uid, uid, true, tr);
        tr.Run();

        Assert.assertEquals(anonId, bcw.getStoredAnonymousId());
        Assert.assertEquals(profileId, bcw.getStoredProfileId());

        bcw.setAlwaysAllowProfileSwitch(true);
        Logout();
    }*/
}
