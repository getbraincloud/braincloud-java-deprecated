package com.bitheads.braincloud.services;

import com.bitheads.braincloud.client.BrainCloudClient;
import com.bitheads.braincloud.client.BrainCloudWrapper;

import org.junit.After;
import org.junit.Before;
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
}