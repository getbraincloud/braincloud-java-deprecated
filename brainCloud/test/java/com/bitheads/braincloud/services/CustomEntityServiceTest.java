package com.bitheads.braincloud.services;

import com.bitheads.braincloud.client.BrainCloudClient;
import com.bitheads.braincloud.client.ReasonCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by prestonjennings on 15-09-01.
 */
public class CustomEntityServiceTest extends TestFixtureBase {
    private final String _defaultEntityType = "athletes";

    @Test
    public void testCreateCustomEntity() throws Exception {
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getCustomEntityService().createEntity(
                _defaultEntityType,
                Helpers.createJsonPair("test", "testy"),
                null,
                1,
                true,
                tr);

        tr.Run();
    }

    @Test
    public void testDeleteEntity() throws Exception {
        TestResult tr = new TestResult(_wrapper);
        String entityId = createDefaultEntity(ACL.Access.None);

        _wrapper.getCustomEntityService().deleteEntity(
                _defaultEntityType,
                entityId,
                1,
                tr);

        tr.Run();
    }

    @Test
    public void testGetCount() throws Exception {
        TestResult tr = new TestResult(_wrapper);

        _wrapper.getCustomEntityService().getCount(
                _defaultEntityType,
                "{\"data.position\": \"defense\"}",
                tr);

        tr.Run();
    }

    // @Test
    // public void testGetPage() throws Exception {
    //     TestResult tr = new TestResult(_wrapper);

    //     _wrapper.getCustomEntityService().getPage(
    //             _defaultEntityType,
    //             1,
    //             "{\"data.position\": \"defense\"}",
    //             "{\"createdAt\": \"1\"}",
    //             false,
    //             tr);

    //     tr.Run();
    // }

    // @Test
    // public void testGetPageOffset() throws Exception {
    //     TestResult tr = new TestResult(_wrapper);
    //     String context = "";
    //     _wrapper.getCustomEntityService().getPageOffset(
    //             _defaultEntityType,
    //             context,
    //             1,
    //             tr);

    //     tr.Run();
    // }

    @Test
    public void testReadEntity() throws Exception {
        TestResult tr = new TestResult(_wrapper);
        String entityId = createDefaultEntity(ACL.Access.None);
        _wrapper.getCustomEntityService().readEntity(
                _defaultEntityType,
                entityId,
                tr);

        tr.Run();
    }

    @Test
    public void testUpdateEntity() throws Exception {
        TestResult tr = new TestResult(_wrapper);
        String entityId = createDefaultEntity(ACL.Access.None);
        _wrapper.getCustomEntityService().updateEntity(
                _defaultEntityType,
                entityId,
                1,
                "{\"test\": \"Testing\"}",
                null,
                0,
                tr);

        tr.Run();
    }
    
    @Test
    public void testUpdateEntityFields() throws Exception {
        TestResult tr = new TestResult(_wrapper);
        String entityId = createDefaultEntity(ACL.Access.None);
        _wrapper.getCustomEntityService().updateEntityFields(
                _defaultEntityType,
                entityId,
                1,
                "{\"goals\": \"3\"}",
                tr);
        tr.Run();
    }

    /// <summary>
    /// Creates a default entity on the server
    /// </summary>
    /// <param name="accessLevel"> accessLevel for entity </param>
    /// <returns> The ID of the entity </returns>
    private String createDefaultEntity(ACL.Access accessLevel) {
        TestResult tr = new TestResult(_wrapper);

        ACL access = new ACL();
        access.setOther(accessLevel);
        String entityId = "";

        //Create entity
        _wrapper.getCustomEntityService().createEntity(
            _defaultEntityType,
            Helpers.createJsonPair("test", "testy"),
            null,
            1,
            true,
            tr);

        if (tr.Run()) {
            entityId = getEntityId(tr.m_response);
        }

        return entityId;
    }

    /// <summary>
    /// Returns the entityId from a raw json response
    /// </summary>
    /// <param name="json"> Json to parse for ID </param>
    /// <returns> entityId from data </returns>
    private String getEntityId(JSONObject json) {
        try {
            return json.getJSONObject("data").getString("entityId");
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return "";
    }
}