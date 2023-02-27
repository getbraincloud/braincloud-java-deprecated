package com.bitheads.braincloud.services;

import com.bitheads.braincloud.client.BrainCloudClient;
import com.bitheads.braincloud.client.IServerCallback;
import com.bitheads.braincloud.client.ServiceName;
import com.bitheads.braincloud.client.ServiceOperation;
import com.bitheads.braincloud.comms.ServerCall;

import org.json.JSONException;
import org.json.JSONObject;

public class GroupFileService {
    private enum Parameter {
        groupId,
        folderPath,
        filename,
        fullPathFilename,
        fileId,
        version,
        newTreeId,
        treeVersion,
        newFilename,
        overwriteIfPresent,
        recurse,
        userCloudPath,
        userCloudFilename,
        groupTreeId,
        groupFilename,
        groupFileAcl,
        newAcl
    }
    private BrainCloudClient _client;

    public GroupFileService(BrainCloudClient client){
        _client = client;
    }

    /**
     * Return whether the specified file exists.
     * No error if it doesn't - just the 200 return and info
     *
     * Note - requires FILE READ permission
     *      - so user must have at least READ permission on the file
     *      - otherwise will return exists: false.
     * @param groupId
     * @param folderPath
     * @param filename
     * @param callback
     */
    public void checkFilenameExists(
            String groupId,
            String folderPath,
            String filename,
            IServerCallback callback
    ){
        JSONObject data = new JSONObject();
        try {
            data.put(Parameter.groupId.name(), groupId);
            data.put(Parameter.folderPath.name(), folderPath);
            data.put(Parameter.filename.name(), filename);

            ServerCall sc = new ServerCall(
                    ServiceName.groupFile,
                    ServiceOperation.CHECK_FILENAME_EXISTS,
                    data,
                    callback
            );
            _client.sendRequest(sc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return whether the specified file exists.
     * No error if it doesn't - just the 200 return and info
     *
     * Note - requires FILE READ permission
     *      - so user must have at least READ permission on the file
     *      - otherwise will return exists: false.
     * @param groupId
     * @param fullPathFilename
     * @param callback
     */
    public void checkFullpathFilenameExists(
            String groupId,
            String fullPathFilename,
            IServerCallback callback
    ){
        JSONObject data = new JSONObject();
        try {
            data.put(Parameter.groupId.name(), groupId);
            data.put(Parameter.fullPathFilename.name(), fullPathFilename);

            ServerCall sc = new ServerCall(
                    ServiceName.groupFile,
                    ServiceOperation.CHECK_FULLPATH_FILENAME_EXISTS,
                    data,
                    callback
            );
            _client.sendRequest(sc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to make a duplicate of a file - potentially in a new directory.
     * User must have at least READ permission on the existing file.
     * User must have at least WRITE permission in the target directory.
     * If overwriting a file, user must have WRITE permission on the file to be overwritten.
     * @param groupId
     * @param fileId
     * @param version
     * @param newTreeId
     * @param treeVersion
     * @param newFilename
     * @param overwriteIfPresent
     * @param callback
     */
    public void copyFile(
            String groupId,
            String fileId,
            int version,
            String newTreeId,
            int treeVersion,
            String newFilename,
            boolean overwriteIfPresent,
            IServerCallback callback
    ){
        JSONObject data = new JSONObject();
        try {
            data.put(Parameter.groupId.name(), groupId);
            data.put(Parameter.fileId.name(), fileId);
            data.put(Parameter.version.name(), version);
            data.put(Parameter.newTreeId.name(), newTreeId);
            data.put(Parameter.treeVersion.name(), treeVersion);
            data.put(Parameter.newFilename.name(), newFilename);
            data.put(Parameter.overwriteIfPresent.name(), overwriteIfPresent);

            ServerCall sc = new ServerCall(
                    ServiceName.groupFile,
                    ServiceOperation.COPY_FILE,
                    data,
                    callback
            );
            _client.sendRequest(sc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the specified file.
     * User must have at least WRITE permission on the existing file.
     * @param groupId
     * @param fileId
     * @param version
     * @param filename
     * @param callback
     */
    public void deleteFile(
            String groupId,
            String fileId,
            int version,
            String filename,
            IServerCallback callback
    ){
        JSONObject data = new JSONObject();
        try {
            data.put(Parameter.groupId.name(), groupId);
            data.put(Parameter.fileId.name(), fileId);
            data.put(Parameter.version.name(), version);
            data.put(Parameter.filename.name(), filename);

            ServerCall sc = new ServerCall(
                    ServiceName.groupFile,
                    ServiceOperation.DELETE_FILE,
                    data,
                    callback
            );
            _client.sendRequest(sc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return the CDN url for the specified file.
     * Should only be used if the client cannot follow a standard HTTP redirect
     *      (which is rare!) ← otherwise don’t waste the API call!
     * Requires FILE READ permission
     * @param groupId
     * @param fileId
     * @param callback
     */
    public void getCDNUrl(String groupId, String fileId, IServerCallback callback){
        JSONObject data = new JSONObject();
        try {
            data.put(Parameter.groupId.name(), groupId);
            data.put(Parameter.fileId.name(), fileId);

            ServerCall sc = new ServerCall(
                    ServiceName.groupFile,
                    ServiceOperation.GET_CDN_URL,
                    data,
                    callback);
            _client.sendRequest(sc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns info about the specified file
     * Requires FILE READ permission
     *      - so user must have at least READ permission on the file
     *      - otherwise return error as if file does not exist.
     * @param groupId
     * @param fileId
     * @param callback
     */
    public void getFileInfo(String groupId, String fileId, IServerCallback callback){
        JSONObject data = new JSONObject();
        try {
            data.put(Parameter.groupId.name(), groupId);
            data.put(Parameter.fileId.name(), fileId);

            ServerCall sc = new ServerCall(
                    ServiceName.groupFile,
                    ServiceOperation.GET_FILE_INFO,
                    data,
                    callback
            );
            _client.sendRequest(sc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns info about the specified file
     * Requires FILE READ permission
     *      - so user must have at least READ permission on the file
     *      - otherwise return error as if file does not exist.
     * @param groupId
     * @param folderPath
     * @param filename
     * @param callback
     */
    public void getFileInfoSimple(
            String groupId,
            String folderPath,
            String filename,
            IServerCallback callback
    ){
        JSONObject data = new JSONObject();
        try {
            data.put(Parameter.groupId.name(), groupId);
            data.put(Parameter.folderPath.name(), folderPath);
            data.put(Parameter.filename.name(), filename);

            ServerCall sc = new ServerCall(
                    ServiceName.groupFile,
                    ServiceOperation.GET_FILE_INFO_SIMPLE,
                    data,
                    callback
            );
            _client.sendRequest(sc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the list of files starting at the specified directory.
     * Requires FOLDER READ permission, as well as FILE READ permission for each file listed.
     * Note that user must have at least READ permission on the directory,
     *      and at least READ permission on each of the files listed…
     *      (files without permission will not be included in the returned results).
     * @param groupId
     * @param folderPath
     * @param recurse
     * @param callback
     */
    public void getFileList(
            String groupId,
            String folderPath,
            boolean recurse,
            IServerCallback callback
    ){
        JSONObject data = new JSONObject();
        try {
            data.put(Parameter.groupId.name(), groupId);
            data.put(Parameter.folderPath.name(), folderPath);
            data.put(Parameter.recurse.name(), recurse);

            ServerCall sc = new ServerCall(
                    ServiceName.groupFile,
                    ServiceOperation.GET_FILE_LIST,
                    data,
                    callback
            );
            _client.sendRequest(sc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to move a file - potentially to a new filename.
     * User must have at least READ permission on the existing file.
     * User must have at least WRITE permission in the target directory.
     * If overwriting a file, user must have WRITE permission on the file to be overwritten.
     * @param groupId
     * @param fileId
     * @param version
     * @param newTreeId
     * @param treeVersion
     * @param newFilename
     * @param overwriteIfPresent
     * @param callback
     */
    public void moveFile(
            String groupId,
            String fileId,
            int version,
            String newTreeId,
            int treeVersion,
            String newFilename,
            boolean overwriteIfPresent,
            IServerCallback callback
    ){
        JSONObject data = new JSONObject();
        try {
            data.put(Parameter.groupId.name(), groupId);
            data.put(Parameter.fileId.name(), fileId);
            data.put(Parameter.version.name(), version);
            data.put(Parameter.newTreeId.name(), newTreeId);
            data.put(Parameter.treeVersion.name(), treeVersion);
            data.put(Parameter.newFilename.name(), newFilename);
            data.put(Parameter.overwriteIfPresent.name(), overwriteIfPresent);

            ServerCall sc = new ServerCall(
                    ServiceName.groupFile,
                    ServiceOperation.MOVE_FILE,
                    data,
                    callback
            );
            _client.sendRequest(sc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Moves a user file that has been uploaded to group files.
     * User must have at least WRITE permission in the target directory.
     * @param userCloudPath
     * @param userCloudFilename
     * @param groupId
     * @param groupTreeId
     * @param groupFileName
     * @param overwriteIfPresent
     * @param callback
     */
    public void moveUserToGroupFile(
            String userCloudPath,
            String userCloudFilename,
            String groupId,
            String groupTreeId,
            String groupFileName,
            JSONObject groupFileAcl,
            boolean overwriteIfPresent,
            IServerCallback callback
    ){
        JSONObject data = new JSONObject();
        try {
            data.put(Parameter.userCloudPath.name(), userCloudPath);
            data.put(Parameter.userCloudFilename.name(), userCloudFilename);
            data.put(Parameter.groupId.name(), groupId);
            data.put(Parameter.groupTreeId.name(), groupTreeId);
            data.put(Parameter.groupFilename.name(), groupFileName);
            data.put(Parameter.groupFileAcl.name(), groupFileAcl);
            data.put(Parameter.overwriteIfPresent.name(), overwriteIfPresent);

            ServerCall sc = new ServerCall(
                    ServiceName.groupFile,
                    ServiceOperation.MOVE_USER_TO_GROUP_FILE,
                    data,
                    callback
            );
            _client.sendRequest(sc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to rename or edit permissions of an uploaded file. Does not change the contents of the file.
     * Note that the user must have at least WRITE permissions on the file. ← requires FILE WRITE permission.
     * If 'newFilename' is not ““ or null, then the filename of the file is set to 'newFilename.'
     * If 'newAcl' is not null or {}, then the acl is set to 'newAcl'
     * @param groupId
     * @param fileId
     * @param version
     * @param newFilename
     * @param callback
     */
    public void updateFileInfo(
            String groupId,
            String fileId,
            int version,
            String newFilename,
            JSONObject newACL,
            IServerCallback callback
    ){
        JSONObject data = new JSONObject();
        try {
            data.put(Parameter.groupId.name(), groupId);
            data.put(Parameter.fileId.name(), fileId);
            data.put(Parameter.version.name(), version);
            data.put(Parameter.newFilename.name(), newFilename);
            data.put(Parameter.newAcl.name(), newACL);

            ServerCall sc = new ServerCall(
                    ServiceName.groupFile,
                    ServiceOperation.UPDATE_FILE_INFO,
                    data,
                    callback
            );
            _client.sendRequest(sc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
