package com.bitheads.braincloud.services;

import com.bitheads.braincloud.client.AuthenticationType;
import com.bitheads.braincloud.client.BrainCloudClient;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by prestonjennings on 15-09-02.
 */
public class IdentityServiceTest extends TestFixtureBase {

    @Test
    public void testAttachEmailIdentity() throws Exception {
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getIdentityService().attachEmailIdentity(
                "id_" + getUser(Users.UserA).email,
                getUser(Users.UserA).password,
                tr);

        tr.Run();
    }

    @Test
    public void testMergeEmailIdentity() throws Exception {

    }

    @Test
    public void testDetachEmailIdentity() throws Exception {

    }

    @Ignore("Currently fails. Must be reviewed")
    @Test
    public void testSwitchToChildProfile() throws Exception {
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getIdentityService().switchToChildProfile(null, m_childAppId, true, tr);
        tr.Run();
    }

    @Ignore("Currently fails. Must be reviewed")
    @Test
    public void testSwitchToSingletonChildProfile() throws Exception {
        Logout();

        TestResult tr = new TestResult(_wrapper);
        _wrapper.getClient().getAuthenticationService().authenticateEmailPassword(
                getUser(Users.UserC).email,
                getUser(Users.UserC).password,
                true,
                tr);
        tr.Run();
        tr.Reset();

        _wrapper.getIdentityService().switchToSingletonChildProfile(m_childAppId, true, tr);
        tr.Run();
    }

    @Ignore("Currently fails. Must be reviewed")
    @Test
    public void testSwitchToParentProfile() throws Exception {
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getIdentityService().switchToChildProfile(null, m_childAppId, true, tr);
        tr.Run();

        tr.Reset();
        _wrapper.getIdentityService().switchToParentProfile(m_parentLevelName, tr);
        tr.Run();
    }

    @Ignore("Currently fails. Must be reviewed")
    @Test
    public void testAttachParentWithIdentity() throws Exception {
        TestResult tr = new TestResult(_wrapper);
        goToChildProfile();

        _wrapper.getIdentityService().detachParent(tr);

        TestUser testUser = getUser(Users.UserA);
        _wrapper.getIdentityService().attachParentWithIdentity(
                testUser.id, testUser.password, AuthenticationType.Universal, null, true, tr);
        tr.Run();
    }

    @Test
    public void testGetChildProfiles() throws Exception {
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getIdentityService().getChildProfiles(true, tr);
        tr.Run();
    }

    @Test
    public void testGetIdentities() throws Exception {
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getIdentityService().getIdentities(tr);
        tr.Run();
    }

    @Test
    public void testGetExpiredIdentities() throws Exception {
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getIdentityService().getExpiredIdentities(tr);
        tr.Run();
    }

    @Test
    public void testRefreshIdentity() throws Exception {
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getIdentityService().refreshIdentity(
                getUser(Users.UserA).id,
                getUser(Users.UserA).password,
                AuthenticationType.Universal,
                tr);
        tr.RunExpectFail(400, 40464);
    }

    @Test
    public void testAttachPeerProfile() throws Exception {
        TestResult tr = new TestResult(_wrapper);

        TestUser testUser = getUser(Users.UserA);
        _wrapper.getIdentityService().attachPeerProfile(
                m_peerName, testUser.id + "_peer", testUser.password, AuthenticationType.Universal, null, true, tr);

        if (tr.Run()) detachPeer();
    }

    @Test
    public void testDetachPeer() throws Exception {
        TestResult tr = new TestResult(_wrapper);

        if (attachPeer(Users.UserA)) {
            _wrapper.getIdentityService().detachPeer(m_peerName, tr);
            tr.Run();
        }
    }

    @Test
    public void testGetPeerProfiles() throws Exception {
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getIdentityService().getIdentities(tr);
        tr.Run();
    }
}
