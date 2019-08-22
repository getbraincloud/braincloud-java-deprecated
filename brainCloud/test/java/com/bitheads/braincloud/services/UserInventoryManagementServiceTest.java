package com.bitheads.braincloud.services;

import com.bitheads.braincloud.client.BrainCloudClient;
import com.bitheads.braincloud.client.ReasonCodes;

import org.junit.After;
import org.junit.Test;

import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;

/**
 * Created by bradleyh on 1/9/2017.
 */

public class UserInventoryManagementServiceTest extends TestFixtureBase {

    @Test
    public void awardUserItem() throws Exception {
        
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getUserInventoryManagementService().awardUserItem(
                "sword001",
                5,
                true,
                tr);
        tr.Run();
    }

    @Test
    public void dropUserItem() throws Exception {
        
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getUserInventoryManagementService().dropUserItem(
                "invalidForNow",
                1,
                true,
                tr);
        tr.RunExpectFail(400, ReasonCodes.ITEM_NOT_FOUND);
    }

    @Test
    public void getUserInventory() throws Exception {
        String criteria = "{\"itemData.bonus\": \"1\"}";
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getUserInventoryManagementService().getUserInventory(
                criteria,
                true,
                tr);
        tr.Run();
    }

    @Test
    public void getUserInventoryPage() throws Exception {
        String context = "{\"test\": \"Testing\"}";
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getUserInventoryManagementService().getUserInventoryPage(
                context,
                true,
                tr);
        tr.Run();
    }

    @Test
    public void getUserInventoryPageOffset() throws Exception {
        String context = "eyJzZWFyY2hDcml0ZXJpYSI6eyJnYW1lSWQiOiIyMDAwMSIsInBsYXllcklkIjoiNmVhYWU4M2EtYjZkMy00NTM5LWExZjAtZTIxMmMzYjUzMGIwIiwiZ2lmdGVkVG8iOm51bGx9LCJzb3J0Q3JpdGVyaWEiOnt9LCJwYWdpbmF0aW9uIjp7InJvd3NQZXJQYWdlIjoxMDAsInBhZ2VOdW1iZXIiOm51bGx9LCJvcHRpb25zIjpudWxsfQ";
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getUserInventoryManagementService().getUserInventoryPageOffset(
                context,
                1,
                true,
                tr);
        tr.Run();
    }

    @Test
    public void GetUserItem() throws Exception {
        
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getUserInventoryManagementService().getUserItem(
                "invalidForNow",
                true,
                tr);
        tr.RunExpectFail(400, ReasonCodes.ITEM_NOT_FOUND);
    }

    @Test
    public void giveUserItemTo() throws Exception {
        
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getUserInventoryManagementService().giveUserItemTo(
            getUser(Users.UserB).id, "invalidForNow", 1, true,
                tr);
        tr.RunExpectFail(400, ReasonCodes.ITEM_NOT_FOUND);
    }

    @Test
    public void purchaseUserItem() throws Exception {
        
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getUserInventoryManagementService().purchaseUserItem(
                "sword001",
                1,
                null,
                true,
                tr);
        tr.Run();
    }

    @Test
    public void receiveUserItemFrom() throws Exception {
        
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getUserInventoryManagementService().receiveUserItemFrom(
            getUser(Users.UserB).id, "invalidForNow",
                tr);
        tr.RunExpectFail(400, ReasonCodes.ITEM_NOT_FOUND);
    }

    @Test
    public void sellUserItem() throws Exception {
        
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getUserInventoryManagementService().sellUserItem(
                "invalidForNow",
                1,
                1,
                null,
                true,
                tr);
        tr.RunExpectFail(400, ReasonCodes.ITEM_NOT_FOUND);
    }

    @Test
    public void updateUserItemData() throws Exception {
        String newItemData = "{\"test\": \"Testing\"}";
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getUserInventoryManagementService().updateUserItemData(
                "invalidForNow",
                1,
                newItemData,
                tr);
        tr.RunExpectFail(400, ReasonCodes.ITEM_NOT_FOUND);
    }

    @Test
    public void useUserItem() throws Exception {
        String newItemData = "{\"test\": \"Testing\"}";
        TestResult tr = new TestResult(_wrapper);
        _wrapper.getUserInventoryManagementService().useUserItem(
                "invalidForNow",
                1,
                newItemData,
                true,
                tr);
        tr.RunExpectFail(400, ReasonCodes.ITEM_NOT_FOUND);
    }
}
