package com.bitheads.braincloud.services;

import com.bitheads.braincloud.client.AuthenticationType;
import com.bitheads.braincloud.client.BrainCloudClient;

import org.junit.After;
import org.junit.Before;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestFixtureBase {
    static protected String m_serverUrl = "";
    static protected String m_appId = "";
    static protected String m_secret = "";
    static protected String m_appVersion = "";
    static protected String m_parentLevelName = "";
    static protected String m_childAppId = "";
    static protected String m_peerName = "";

    @Before
    public void setUp() throws Exception {
        LoadIds();

        BrainCloudClient.getInstance().initialize(m_appId, m_secret, m_appVersion, m_serverUrl);
        BrainCloudClient.getInstance().enableLogging(true);

        if (shouldAuthenticate()) {
            TestResult tr = new TestResult();
            BrainCloudClient.getInstance().getAuthenticationService().authenticateUniversal(getUser(Users.UserA).id, getUser(Users.UserA).password, true, tr);
            if (!tr.Run()) {
                // what do we do on error?
            }
        }
    }

    @After
    public void tearDown() throws Exception {
        BrainCloudClient.getInstance().resetCommunication();
        BrainCloudClient.getInstance().deregisterEventCallback();
        BrainCloudClient.getInstance().deregisterRewardCallback();
    }

    /// <summary>
    /// Overridable method which if set to true, will cause unit test "SetUp" to
    /// attempt an authentication before calling the test method.
    /// </summary>
    /// <returns><c>true</c>, if authenticate was shoulded, <c>false</c> otherwise.</returns>
    public boolean shouldAuthenticate() {
        return true;
    }

    /// <summary>
    /// Routine loads up brainCloud configuration info from "tests/ids.txt" (hopefully)
    /// in a platform agnostic way.
    /// </summary>
    private void LoadIds() {
        if(m_serverUrl.length() > 0) return;

        File idsFile = new File("ids.txt");
        try {
            System.out.println("Looking for ids.txt file in " + idsFile.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(idsFile.exists()) System.out.println("Found ids.txt file");

        List<String> lines = new ArrayList<>();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(idsFile));
            String text;
            while ((text = reader.readLine()) != null) {
                lines.add(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (String line : lines) {
            String[] split = line.split("=");
            switch (split[0]) {
                case "serverUrl":
                    m_serverUrl = split[1];
                    break;
                case "appId":
                    m_appId = split[1];
                    break;
                case "secret":
                    m_secret = split[1];
                    break;
                case "version":
                    m_appVersion = split[1];
                    break;
                case "childAppId":
                    m_childAppId = split[1];
                    break;
                case "parentLevelName":
                    m_parentLevelName = split[1];
                    break;
                case "peerName":
                    m_peerName = split[1];
                    break;
            }
        }
    }

    public enum Users {
        UserA,
        UserB,
        UserC;

        public static Users byOrdinal(int ord) {
            for (Users m : Users.values()) {
                if (m.ordinal() == ord) {
                    return m;
                }
            }
            return null;
        }
    }

    private static TestUser[] _testUsers;
    private static boolean _init = false;

    /// <summary>
    /// Returns the specified user's data
    /// </summary>
    /// <param name="user"> User's data to return </param>
    /// <returns> Object contining the user's Id, Password, and profileId </returns>
    protected TestUser getUser(Users user) {
        if (!_init) {
            //Log.i(getClass().getName(), "Initializing New Random Users");
            BrainCloudClient.getInstance().enableLogging(false);
            _testUsers = new TestUser[TestFixtureBase.Users.values().length];
            Random rand = new Random();

            for (int i = 0, ilen = _testUsers.length; i < ilen; ++i) {
                _testUsers[i] = new TestUser(Users.byOrdinal(i).toString() + "-", rand.nextInt());
                //Log.i(getClass().getName(), ".");
            }
            //Log.i(getClass().getName(), "\n");
            BrainCloudClient.getInstance().enableLogging(true);
            _init = true;
        }

        return _testUsers[user.ordinal()];
    }

    public boolean goToChildProfile() {
        TestResult tr = new TestResult();
        BrainCloudClient.getInstance().getIdentityService().switchToChildProfile(null, m_childAppId, true, tr);
        return tr.Run();
    }

    public boolean goToParentProfile() {
        TestResult tr = new TestResult();
        BrainCloudClient.getInstance().getIdentityService().switchToParentProfile(m_parentLevelName, tr);
        return tr.Run();
    }

    public boolean attachPeer(Users user) {
        TestUser testUser = getUser(user);
        TestResult tr = new TestResult();
        BrainCloudClient.getInstance().getIdentityService().attachPeerProfile(
                m_peerName, testUser.id + "_peer", testUser.password, AuthenticationType.Universal,null,  true, tr);
        return tr.Run();
    }

    public boolean detachPeer() {
        TestResult tr = new TestResult();
        BrainCloudClient.getInstance().getIdentityService().detachPeer(m_peerName, tr);
        return tr.Run();
    }

    public void Logout() {
        TestResult tr = new TestResult();

        BrainCloudClient.getInstance().getPlayerStateService().logout(
                tr);
        tr.Run();
        BrainCloudClient.getInstance().resetCommunication();
        BrainCloudClient.getInstance().getAuthenticationService().clearSavedProfileId();
    }
}