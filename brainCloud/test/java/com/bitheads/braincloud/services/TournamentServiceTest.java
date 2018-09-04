package com.bitheads.braincloud.services;

import com.bitheads.braincloud.client.BrainCloudClient;
import com.bitheads.braincloud.client.ReasonCodes;

import org.junit.After;
import org.junit.Test;

import java.util.Date;

/**
 * Created by bradleyh on 1/9/2017.
 */

public class TournamentServiceTest extends TestFixtureBase {

    private String _tournamentCode = "testTournament";
    private String _leaderboardId = "testTournamentLeaderboard";
    private String _divSetId = "testDivisionSetID";
    private boolean _didJoin;

    @After
    public void Teardown() throws Exception {
        if (_didJoin) {
            leaveTestTournament();
        }
    }

    @Test
    public void claimTournamentReward() throws Exception {
        int version = joinTestTournament();
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getTournamentService().claimTournamentReward(
                _leaderboardId,
                version,
                tr);

        tr.RunExpectFail(400, ReasonCodes.VIEWING_REWARD_FOR_NON_PROCESSED_TOURNAMENTS);
    }

    @Test
    public void getDivisionInfo() throws Exception {
        joinTestDivision();

        TestResult tr = new TestResult(_wrapper);

        _wrapper.getTournamentService().getDivisionInfo(
            _divSetId,
            tr
        );
        tr.Run();

        leaveTestDivision();
    }

    @Test
    public void getMyDivisions() throws Exception {
        joinTestDivision();
        
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getTournamentService().getMyDivisions(tr);
        tr.Run();

        leaveTestDivision();
    }

    @Test
    public void getTournamentStatus() throws Exception {
        int version = joinTestTournament();
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getTournamentService().getTournamentStatus(
                _leaderboardId,
                version,
                tr);

        tr.Run();
    }

    @Test
    public void joinTournament() throws Exception {
        joinTestTournament();
    }

    @Test
    public void leaveTournament() throws Exception {
        joinTestTournament();
        leaveTestTournament();
    }

    @Test
    public void postTournamentScore() throws Exception {
        joinTestTournament();
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getTournamentService().postTournamentScore(
                _leaderboardId,
                200,
                Helpers.createJsonPair("test", 1),
                new Date(),
                tr);

        tr.Run();
    }

    @Test
    public void postTournamentScoreWithResults() throws Exception {
        joinTestTournament();
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getTournamentService().postTournamentScoreWithResults(
                _leaderboardId,
                200,
                Helpers.createJsonPair("test", 1),
                new Date(),
                SocialLeaderboardService.SortOrder.HIGH_TO_LOW,
                10,
                10,
                0,
                tr);

        tr.Run();
    }

    @Test
    public void viewCurrentReward() throws Exception {
        joinTestTournament();
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getTournamentService().viewCurrentReward(
                _leaderboardId,
                tr);

        tr.Run();
    }

    @Test
    public void viewReward() throws Exception {
        joinTestTournament();
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getTournamentService().viewReward(
                _leaderboardId,
                -1,
                tr);

        tr.RunExpectFail(400, ReasonCodes.PLAYER_NOT_ENROLLED_IN_TOURNAMENT);
    }

    private int joinTestTournament() throws Exception {
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getTournamentService().joinTournament(
                _leaderboardId,
                _tournamentCode,
                0,
                tr);

        tr.Run();
        _didJoin = true;

        _wrapper.getTournamentService().getTournamentStatus(
                _leaderboardId,
                -1,
                tr);
        tr.Run();

        int version = tr.m_response.getJSONObject("data").getInt("versionId");
        return version;
    }

    private void leaveTestTournament() {
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getTournamentService().leaveTournament(
                _leaderboardId,
                tr);

        tr.Run();

        _didJoin = false;
    }

    private void joinTestDivision() throws Exception {
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getTournamentService().joinDivision(
                _divSetId,
                _tournamentCode,
                0,
                tr);

        tr.Run();
        _didJoin = true;
    }

    
    private void leaveTestDivision() {

        //the unit test master 20001 has working API calls. I needed to hard code this leaderboardId 
        //because the LeaveDivisionInstance is looking for a string of a certain format, as a tag of a 
        //Division set instance. When I use the API explorer, I first authenticate, then join a division,
        //then getMyDivisions (which tells me my testDivSetId maps to "^D^testDivSetId^3", which is what I 
        //need to pass into LeaveDivisionInstance as the _leaderBoardId in order for success).
        //If I simply passed in _leaderBoardId, it tells me testTournamentLeaderBoard is not a division set instance. 
        //This is because its not the same format. I hard coded the response I got from GetMyDivisions from
        //the API explorer because it worked there, and it seems to work here if I pass in the same thing , therefore the 
        //unit test is considered a pass, as all the calls are successful. 
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getTournamentService().leaveDivisionInstance(
                "^D^testDivSetId^3",
                tr);

        tr.Run();

        _didJoin = false;
    }
}
