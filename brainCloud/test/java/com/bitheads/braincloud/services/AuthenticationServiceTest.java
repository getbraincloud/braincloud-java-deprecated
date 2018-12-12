package com.bitheads.braincloud.services;

import com.bitheads.braincloud.client.ReasonCodes;
import com.bitheads.braincloud.client.StatusCodes;

import com.bitheads.braincloud.client.BrainCloudClient;
import com.bitheads.braincloud.client.BrainCloudWrapper;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by prestonjennings on 15-08-31.
 */
public class AuthenticationServiceTest extends TestFixtureNoAuth
{

    @Test
    public void testAuthenticateAnonymous() throws Exception
    {
        // not implemented
    }

    @Test
    public void testAuthenticateUniversalInstance() throws Exception
    {

        TestResult tr = new TestResult(_wrapper);
        _client.getAuthenticationService().authenticateUniversal("abc", "abc", true, tr);

        tr.Run();
    }

    @Test
    public void testAuthenticateEmailPassword() throws Exception
    {
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getClient().getAuthenticationService().authenticateEmailPassword(
            getUser(Users.UserA).email,
            getUser(Users.UserA).password,
            true,
            tr);

        tr.Run();
    }

    @Test
    public void testAuthenticateHandoff() throws Exception
    {

        TestResult tr = new TestResult(_wrapper);
        _client.getAuthenticationService().authenticateHandoff("invalid_handoffId", "invalid_securityToken", tr);

        tr.RunExpectFail(403, ReasonCodes.TOKEN_DOES_NOT_MATCH_USER);
    }

    @Test
    public void testAuthenticateExternal() throws Exception
    {

    }

    @Test
    public void testAuthenticateFacebook() throws Exception
    {

    }

    @Test
    public void testAuthenticateGoogle() throws Exception
    {

    }

    @Test
    public void testAuthenticateSteam() throws Exception
    {

    }

    @Test
    public void testAuthenticateTwitter() throws Exception
    {

    }

    @Test
    public void testAuthenticateUniversal() throws Exception {
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getClient().getAuthenticationService().authenticateUniversal("abc", "abc", true, tr);
        tr.Run();
    }

    @Test
    public void testResetEmailPassword() throws Exception
    {
        String email = "braincloudunittest@gmail.com";

        TestResult tr = new TestResult(_wrapper);
        _wrapper.getClient().getAuthenticationService().resetEmailPassword(
                email, tr);
        tr.Run();
    }

    @Test
    public void testResetEmailPasswordAdvanced() throws Exception
    {
        TestResult tr = new TestResult(_wrapper);

        //this is something we want to avoid in future, our goal is to be able to test without authentication
        _wrapper.getClient().getAuthenticationService().authenticateEmailPassword(
                getUser(Users.UserA).email,
                getUser(Users.UserA).password,
                true,
                tr);

        tr.Run();

        Map testMap = new HashMap();
        testMap.put("fromAddress", "fromAddress");
        testMap.put("fromName", "fromName");
        testMap.put("replyName", "replyName");
        testMap.put("templateId", "8f14c77d-61f4-4966-ab6d-0bee8b13d090");
        testMap.put("subject", "subject");
        testMap.put("body", "body here");
        Map substitutions =  new HashMap();
        substitutions.put(":name", "John Doe");
        substitutions.put(":resetLink", "www.dummyLink.io");
        testMap.put("substitutions", substitutions);
        String[] categories = new String[2];
        categories[0] = "category1";
        categories[1] = "category2";
        testMap.put("categories", categories);

        _wrapper.getClient().getAuthenticationService().resetEmailPasswordAdvanced(
                "braincloudunittest@gmail.com",
                testMap,
                tr);

        tr.RunExpectFail(StatusCodes.BAD_REQUEST, ReasonCodes.INVALID_FROM_ADDRESS);
    }
}