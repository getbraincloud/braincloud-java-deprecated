package com.bitheads.braincloud.services;

import com.bitheads.braincloud.client.BrainCloudClient;

import org.junit.Test;

import static org.junit.Assert.*;

public class GlobalFileServiceTest extends TestFixtureBase
{
    String testfileName = "png1.png";
    String testFileId = "34802251-0da0-419e-91b5-59d91790af15";
    String testFolderPath = "/existingfolder/";

    @Test
    public void testGetFileInfo() throws Exception
    {
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getGlobalFileService().getFileInfo(
            testFileId,
            tr);

        tr.Run();
    }

    @Test
    public void testGetFileInfoSimple() throws Exception
    {
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getGlobalFileService().getFileInfoSimple(
                testFolderPath,
                testfileName,
                tr);

        tr.Run();
    }

    @Test
    public void testGetGlobalCDNUrl() throws Exception
    {
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getGlobalFileService().getGlobalCDNUrl(
                testFileId,
                tr);

        tr.Run();
    }

    @Test
    public void testGetGlobalFileList() throws Exception
    {
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getGlobalFileService().getGlobalFileList(
                testFolderPath,
                true,
                tr);

        tr.Run();
    }
}
