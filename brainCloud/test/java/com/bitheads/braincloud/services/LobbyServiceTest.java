package com.bitheads.braincloud.services;

import com.bitheads.braincloud.client.ReasonCodes;
import com.bitheads.braincloud.client.StatusCodes;

import org.junit.Test;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by David St-Louis on 18-07-17.
 */
public class LobbyServiceTest extends TestFixtureBase
{
    static private String _lobbyId = "";

    @Test
    public void testCreateLobby() throws Exception {
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getLobbyService().createLobby("MATCH_UNRANKED", 0, null, true, "{}", "all", "{}", tr);
        tr.Run();
    }

    @Test
    public void testFindLobby() throws Exception {
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getLobbyService().findLobby("MATCH_UNRANKED", 0, 1, "{\"strategy\":\"ranged-absolute\",\"alignment\":\"center\",\"ranges\":[1000]}", "{}", null, true, "{}", "all", tr);
        tr.Run();
    }

    @Test
    public void testFindOrCreateLobby() throws Exception {
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getLobbyService().findOrCreateLobby("MATCH_UNRANKED", 0, 1, "{\"strategy\":\"ranged-absolute\",\"alignment\":\"center\",\"ranges\":[1000]}", "{}", null, "{}", true, "{}", "all", tr);
        tr.Run();
    }

    @Test
    public void testGetLobbyData() throws Exception {
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getLobbyService().getLobbyData("wrongLobbyId", tr);
        tr.RunExpectFail(StatusCodes.BAD_REQUEST, ReasonCodes.LOBBY_NOT_FOUND);
    }

    @Test
    public void testLeaveLobby() throws Exception {
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getLobbyService().leaveLobby("wrongLobbyId", tr);
        tr.RunExpectFail(StatusCodes.BAD_REQUEST, ReasonCodes.LOBBY_NOT_FOUND);
    }

    @Test
    public void testJoinLobby() throws Exception {
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getLobbyService().joinLobby("wrongLobbyId", true, "{}", "red", null, tr);
        tr.RunExpectFail(StatusCodes.BAD_REQUEST, ReasonCodes.LOBBY_NOT_FOUND);
    }

    @Test
    public void testRemoveMember() throws Exception {
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getLobbyService().removeMember("wrongLobbyId", "wrongConId", tr);
        tr.RunExpectFail(StatusCodes.BAD_REQUEST, ReasonCodes.LOBBY_NOT_FOUND);
    }

    @Test
    public void testSendSignal() throws Exception {
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getLobbyService().sendSignal("wrongLobbyId", "{\"msg\":\"test\"}", tr);
        tr.RunExpectFail(StatusCodes.BAD_REQUEST, ReasonCodes.LOBBY_NOT_FOUND);
    }

    @Test
    public void testSwitchTeam() throws Exception {
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getLobbyService().switchTeam("wrongLobbyId", "all", tr);
        tr.RunExpectFail(StatusCodes.BAD_REQUEST, ReasonCodes.LOBBY_NOT_FOUND);
    }

    @Test
    public void testUpdateReady() throws Exception {
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getLobbyService().updateReady("wrongLobbyId", true, "{}", tr);
        tr.RunExpectFail(StatusCodes.BAD_REQUEST, ReasonCodes.LOBBY_NOT_FOUND);
    }

    @Test
    public void testUpdateSettings() throws Exception {
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getLobbyService().updateSettings("wrongLobbyId", "{\"test\":\"me\"}", tr);
        tr.RunExpectFail(StatusCodes.BAD_REQUEST, ReasonCodes.LOBBY_NOT_FOUND);
    }
}
