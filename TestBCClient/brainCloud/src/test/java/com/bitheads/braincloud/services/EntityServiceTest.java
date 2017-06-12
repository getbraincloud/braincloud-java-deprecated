package com.bitheads.braincloud.services;

import com.bitheads.braincloud.client.BrainCloudClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by prestonjennings on 15-09-01.
 */
public class EntityServiceTest extends TestFixtureBase {
    private final String _defaultEntityType = "address";
    private final String _defaultEntityValueName = "street";
    private final String _defaultEntityValue = "1309 Carling";

    @Test
    public void testCreateEntity() throws Exception {
        TestResult tr = new TestResult();

        BrainCloudClient.getInstance().getEntityService().createEntity(
                _defaultEntityType,
                Helpers.createJsonPair(_defaultEntityValueName, _defaultEntityValue),
                null,
                tr);

        tr.Run();
        deleteAllDefaultEntities();
    }

    @Test
    public void testDeleteEntity() throws Exception {
        TestResult tr = new TestResult();
        String entityId = createDefaultAddressEntity(ACL.Access.None);

        //Delete entity
        BrainCloudClient.getInstance().getEntityService().deleteEntity(entityId, 1, tr);

        tr.Run();
        deleteAllDefaultEntities();
    }

    @Test
    public void testDeleteSingleton() throws Exception {
        TestResult tr = new TestResult();
        createDefaultAddressEntity(ACL.Access.ReadWrite);

        BrainCloudClient.getInstance().getEntityService().deleteSingleton(
                _defaultEntityType,
                1,
                tr);

        tr.Run();
        deleteAllDefaultEntities();
    }

    @Test
    public void testGetInstanceEntitiesByType() throws Exception {
        TestResult tr = new TestResult();
        createDefaultAddressEntity(ACL.Access.None);

        //getInstanceEntity
        BrainCloudClient.getInstance().getEntityService().getEntitiesByType(_defaultEntityType, tr);

        tr.Run();
        deleteAllDefaultEntities();
    }

    @Test
    public void testGetInstanceEntity() throws Exception {
        TestResult tr = new TestResult();
        String entityId = createDefaultAddressEntity(ACL.Access.None);

        //getInstanceEntity
        BrainCloudClient.getInstance().getEntityService().getEntity(entityId, tr);

        tr.Run();
        deleteAllDefaultEntities();
    }

    @Test
    public void testGetInstanceSharedEntitiesForProfileId() throws Exception {
        TestResult tr = new TestResult();
        createDefaultAddressEntity(ACL.Access.None);

        //getInstanceEntity
        BrainCloudClient.getInstance().getEntityService().getSharedEntitiesForProfileId(getUser(Users.UserA).profileId, tr);
        tr.Run();
        deleteAllDefaultEntities();
    }

    @Test
    public void testUpdateEntity() throws Exception {
        TestResult tr = new TestResult();
        String entityId = createDefaultAddressEntity(ACL.Access.None);

        //Update entity
        String updatedAddress = "1609 Bank St";

        BrainCloudClient.getInstance().getEntityService().updateEntity(
                entityId,
                _defaultEntityType,
                Helpers.createJsonPair(_defaultEntityValueName, updatedAddress),
                null,
                1,
                tr);

        tr.Run();
        deleteAllDefaultEntities(2);
    }

    @Test
    public void testUpdateSharedEntity() throws Exception {
        TestResult tr = new TestResult();
        String entityId = createDefaultAddressEntity(ACL.Access.ReadWrite);

        String updatedAddress = "1609 Bank St";

        BrainCloudClient.getInstance().getEntityService().updateSharedEntity(
                BrainCloudClient.getInstance().getAuthenticationService().getProfileId(),
                entityId,
                _defaultEntityType,
                Helpers.createJsonPair(_defaultEntityValueName, updatedAddress),
                1,
                tr);

        tr.Run();
        deleteAllDefaultEntities(2);
    }

    @Test
    public void testGetSharedEntityForProfileId() throws Exception {
        TestResult tr = new TestResult();
        String entityId = createDefaultAddressEntity(ACL.Access.ReadWrite);

        BrainCloudClient.getInstance().getEntityService().getSharedEntityForProfileId(
                BrainCloudClient.getInstance().getAuthenticationService().getProfileId(),
                entityId,
                tr);

        tr.Run();
        deleteAllDefaultEntities(1);
    }

    @Test
    public void testUpdateSingleton() throws Exception {
        TestResult tr = new TestResult();
        createDefaultAddressEntity(ACL.Access.ReadWrite);

        String updatedAddress = "1609 Bank St";

        BrainCloudClient.getInstance().getEntityService().updateSingleton(
                _defaultEntityType,
                Helpers.createJsonPair(_defaultEntityValueName, updatedAddress),
                ACL.readWriteOther().toJsonString(),
                1,
                tr);

        tr.Run();
        deleteAllDefaultEntities(2);
    }

    @Test
    public void testGetSingleton() throws Exception {
        TestResult tr = new TestResult();
        createDefaultAddressEntity(ACL.Access.None);

        BrainCloudClient.getInstance().getEntityService().getSingleton(
                _defaultEntityType,
                tr);

        tr.Run();
        deleteAllDefaultEntities(1);
    }

    @Test
    public void testGetList() throws Exception {
        TestResult tr = new TestResult();

        createDefaultAddressEntity(ACL.Access.None);
        createDefaultAddressEntity(ACL.Access.None);

        BrainCloudClient.getInstance().getEntityService().getList(
                Helpers.createJsonPair("entityType", _defaultEntityType),
                "",
                1000,
                tr);

        tr.Run();
        deleteAllDefaultEntities(1);
    }

    @Test
    public void testGetListCount() throws Exception {
        TestResult tr = new TestResult();

        createDefaultAddressEntity(ACL.Access.None);
        createDefaultAddressEntity(ACL.Access.None);

        BrainCloudClient.getInstance().getEntityService().getListCount(
                Helpers.createJsonPair("entityType", _defaultEntityType),
                tr);

        tr.Run();
        deleteAllDefaultEntities(1);
    }

    @Test
    public void testGetPage() throws Exception {
        createDefaultAddressEntity(ACL.Access.ReadWrite);

        TestResult tr = new TestResult();
        JSONObject context = new JSONObject();
        JSONObject pagination = new JSONObject();
        pagination.put("rowsPerPage", 50);
        pagination.put("pageNumber", 1);
        context.put("pagination", pagination);
        JSONObject searchCriteria = new JSONObject();
        searchCriteria.put("entityType", _defaultEntityType);
        context.put("searchCriteria", searchCriteria);

        BrainCloudClient.getInstance().getEntityService().getPage(context.toString(), tr);
        tr.Run();

        deleteAllDefaultEntities();
    }

    @Test
    public void testGetPageOffset() throws Exception {
        createDefaultAddressEntity(ACL.Access.ReadWrite);

        TestResult tr = new TestResult();
        JSONObject context = new JSONObject();
        JSONObject pagination = new JSONObject();
        pagination.put("rowsPerPage", 50);
        pagination.put("pageNumber", 1);
        context.put("pagination", pagination);
        JSONObject searchCriteria = new JSONObject();
        searchCriteria.put("entityType", _defaultEntityType);
        context.put("searchCriteria", searchCriteria);

        BrainCloudClient.getInstance().getEntityService().getPage(context.toString(), tr);
        tr.Run();

        String retCtx = tr.m_response.getJSONObject("data").getString("context");

        tr.Reset();
        BrainCloudClient.getInstance().getEntityService().getPageOffset(retCtx, 1, tr);
        tr.Run();

        deleteAllDefaultEntities();
    }

    @Test
    public void testIncrementUserEntityData() throws Exception {
        TestResult tr = new TestResult();

        BrainCloudClient.getInstance().getEntityService().createEntity(
                _defaultEntityType,
                Helpers.createJsonPair("test", 1234),
                "",
                tr);
        tr.Run();
        String entityId = getEntityId(tr.m_response);

        BrainCloudClient.getInstance().getEntityService().incrementUserEntityData(
                entityId,
                Helpers.createJsonPair("test", 1234),
                tr);
        tr.Run();

        BrainCloudClient.getInstance().getEntityService().deleteEntity(entityId, -1, tr);
        tr.Run();
    }

    @Test
    public void testIncrementSharedUserEntityData() throws Exception {
        TestResult tr = new TestResult();

        BrainCloudClient.getInstance().getEntityService().createEntity(
                _defaultEntityType,
                Helpers.createJsonPair("test", 1234),
                "",
                tr);
        tr.Run();
        String entityId = getEntityId(tr.m_response);

        BrainCloudClient.getInstance().getEntityService().incrementSharedUserEntityData(
                entityId,
                getUser(Users.UserA).profileId,
                Helpers.createJsonPair("test", 1234),
                tr);
        tr.Run();

        BrainCloudClient.getInstance().getEntityService().deleteEntity(entityId, -1, tr);
        tr.Run();
    }

    @Test
    public void testGetSharedEntitiesListForProfileId() throws Exception {
        TestResult tr = new TestResult();

        createDefaultAddressEntity(ACL.Access.None);
        createDefaultAddressEntity(ACL.Access.None);

        BrainCloudClient.getInstance().getEntityService().getSharedEntitiesListForProfileId(
                getUser(Users.UserA).profileId,
                Helpers.createJsonPair("entityType", _defaultEntityType),
                "",
                1000,
                tr);

        tr.Run();
        deleteAllDefaultEntities(1);
    }

    // private methods below


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

    /// <summary>
    /// Creates a default entity on the server
    /// </summary>
    /// <param name="accessLevel"> accessLevel for entity </param>
    /// <returns> The ID of the entity </returns>
    private String createDefaultAddressEntity(ACL.Access accessLevel) {
        TestResult tr = new TestResult();

        ACL access = new ACL();
        access.setOther(accessLevel);
        String entityId = "";

        //Create entity
        BrainCloudClient.getInstance().getEntityService().createEntity(
                _defaultEntityType,
                Helpers.createJsonPair(_defaultEntityValueName, _defaultEntityValue),
                access.toJsonString(),
                tr);

        if (tr.Run()) {
            entityId = getEntityId(tr.m_response);
        }

        return entityId;
    }

    private void deleteAllDefaultEntities() {
        deleteAllDefaultEntities(1);
    }

    /// <summary>
    /// Deletes all default entities
    /// </summary>
    private void deleteAllDefaultEntities(int version) {
        TestResult tr = new TestResult();

        ArrayList<String> entityIds = new ArrayList<>(0);

        //get all entities
        BrainCloudClient.getInstance().getEntityService().getEntitiesByType(_defaultEntityType, tr);

        if (tr.Run()) {
            try {
                JSONArray entities = tr.m_response.getJSONObject("data").getJSONArray("entities");
                if (entities.length() <= 0) {
                    return;
                }

                for (int i = 0, ilen = entities.length(); i < ilen; ++i) {
                    entityIds.add(entities.getJSONObject(i).getString("entityId"));
                }
            } catch (JSONException je) {
                je.printStackTrace();
            }
        }

        while (!entityIds.isEmpty()) {
            tr.Reset();
            BrainCloudClient.getInstance().getEntityService().deleteEntity(entityIds.remove(0), version, tr);
            tr.Run();
        }
    }
}