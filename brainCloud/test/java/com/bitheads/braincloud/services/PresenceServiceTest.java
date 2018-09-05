package com.bitheads.braincloud.services;

import java.util.Arrays;
import java.util.ArrayList;

import com.bitheads.braincloud.client.BrainCloudClient;
import com.bitheads.braincloud.client.Platform;

import org.junit.Test;

public class PresenceServiceTest extends TestFixtureBase
{
    @Test
    public void testForcePush() throws Exception
    {
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getPresenceService().forcePush(
            tr
        );
        tr.Run();
    }

    @Test
    public void testGetPresenceOfFriends() throws Exception
    {
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getPresenceService().getPresenceOfFriends(
            "Test",
            true,
            tr
        );
        tr.Run();
    }

    @Test
    public void testGetPresenceOfGroup() throws Exception
    {
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getPresenceService().getPresenceOfGroup(
            "Test",
            true,
            tr
        );
        tr.Run();
    }

    @Test
    public void testGetPresenceOfUsers() throws Exception
    {
        ArrayList<String> users = new ArrayList<String>(
            Arrays.asList("aaa-bbb-ccc-ddd", "bbb-ccc-ddd-eee")
        );

        TestResult tr = new TestResult(_wrapper);
        _wrapper.getPresenceService().getPresenceOfUsers(
            users,
            true,
            tr
        );
        tr.Run();
    }

    @Test
    public void testRegisterListenersForFriends() throws Exception
    {
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getPresenceService().registerListenersForFriends(
            "Test",
            true,
            tr
        );
        tr.Run();
    }

    @Test
    public void testRegisterListenersForGroup() throws Exception
    {
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getPresenceService().registerListenersForGroup(
            "Test",
            true,
            tr
        );
        tr.Run();
    }

    @Test
    public void testRegisterListenersForProfiles() throws Exception
    {
        ArrayList<String> users = new ArrayList<String>(
            Arrays.asList("aaa-bbb-ccc-ddd", "bbb-ccc-ddd-eee")
        );

        TestResult tr = new TestResult(_wrapper);
        _wrapper.getPresenceService().registerListenersForProfiles(
            users,
            true,
            tr
        );
        tr.Run();
    }

    @Test
    public void testSetVisibility() throws Exception
    {
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getPresenceService().setVisibility(
            true,
            tr
        );
        tr.Run();
    }

    @Test
    public void testStopListening() throws Exception
    {
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getPresenceService().stopListening(
            tr
        );
        tr.Run();
    }

    @Test
    public void testUpdateActivity() throws Exception
    {
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getPresenceService().updateActivity(
            "Test",
            tr
        );
        tr.Run();
    }
}