package com.bitheads.braincloud.services;

import com.bitheads.braincloud.client.BrainCloudClient;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by prestonjennings on 15-09-02.
 */
public class PlayerStatisticsServiceTest extends TestFixtureBase
{

    @Test
    public void testReadAllPlayerStats() throws Exception
    {
        TestResult tr = new TestResult();

        BrainCloudClient.getInstance().getPlayerStatisticsService().readAllUserStats(
                tr);

        tr.Run();
    }

    @Test
    public void testReadPlayerStatsSubset() throws Exception
    {
        TestResult tr = new TestResult();
        String[] stats = {"currency", "highestScore"};

        BrainCloudClient.getInstance().getPlayerStatisticsService().readUserStatsSubset(
                stats,
                tr);

        tr.Run();
    }

    @Test
    public void testReadPlayerStatisticsByCategory() throws Exception
    {
        TestResult tr = new TestResult();
        BrainCloudClient.getInstance().getPlayerStatisticsService().readUserStatsForCategory("Test", tr);
        tr.Run();
    }

    @Test
    public void testResetAllPlayerStats() throws Exception
    {
        TestResult tr = new TestResult();

        BrainCloudClient.getInstance().getPlayerStatisticsService().resetAllUserStats(
                tr);

        tr.Run();
    }

    @Test
    public void testIncrementPlayerStats() throws Exception
    {
        TestResult tr = new TestResult();

        JSONObject stats = new JSONObject();
        stats.put("highestScore", "RESET");

        BrainCloudClient.getInstance().getPlayerStatisticsService().incrementUserStats(
                stats.toString(),
                tr);

        tr.Run();
    }

    @Test
    public void testIncrementExperiencePoints() throws Exception
    {
        TestResult tr = new TestResult();

        BrainCloudClient.getInstance().getPlayerStatisticsService().incrementExperiencePoints(
                10,
                tr);

        tr.Run();
    }

    @Test
    public void testGetNextExperienceLevel() throws Exception
    {
        TestResult tr = new TestResult();

        BrainCloudClient.getInstance().getPlayerStatisticsService().getNextExperienceLevel(
                tr);

        tr.Run();
    }

    @Test
    public void testSetExperiencePoints() throws Exception
    {
        TestResult tr = new TestResult();

        BrainCloudClient.getInstance().getPlayerStatisticsService().setExperiencePoints(
                100,
                tr);

        tr.Run();
    }

    @Test
    public void testProcessStatistics() throws Exception
    {
        TestResult tr = new TestResult();

        JSONObject stats = new JSONObject();
        stats.put("TestStat", "RESET");

        BrainCloudClient.getInstance().getPlayerStatisticsService().processStatistics(
                stats.toString(),
                tr);

        tr.Run();
    }
}