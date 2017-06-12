package com.bitheads.braincloud.services;

import com.bitheads.braincloud.client.BrainCloudClient;
import com.bitheads.braincloud.client.IServerCallback;
import com.bitheads.braincloud.client.Platform;
import com.bitheads.braincloud.client.ServiceName;
import com.bitheads.braincloud.client.ServiceOperation;
import com.bitheads.braincloud.comms.ServerCall;

import org.json.JSONException;
import org.json.JSONObject;

public class PushNotificationService {

    private enum Parameter {
        deviceType,
        deviceToken,
        toPlayerId,
        profileId,
        message,
        notificationTemplateId,
        substitutions,
        groupId,
        alertContent,
        customData,
        profileIds,
        startDateUTC,
        minutesFromNow
    }

    private BrainCloudClient _client;

    public PushNotificationService(BrainCloudClient client) {
        _client = client;
    }

    /**
     * Deregisters all device tokens currently registered to the player.
     *
     * @param callback The method to be invoked when the server response is received
     */
    public void deregisterAllPushNotificationDeviceTokens(IServerCallback callback) {

        JSONObject data = new JSONObject();

        ServerCall sc = new ServerCall(ServiceName.pushNotification, ServiceOperation.DEREGISTER_ALL, data, callback);
        _client.sendRequest(sc);
    }

    /**
     * Deregisters the given device token from the server to disable this device
     * from receiving push notifications.
     *
     * @param platform The device platform being deregistered.
     * @param token The platform-dependant device token needed for push notifications.
     * @param callback The method to be invoked when the server response is received
     */
    public void deregisterPushNotificationDeviceToken(Platform platform, String token, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.deviceType.name(), platform.toString());
            data.put(Parameter.deviceToken.name(), token);

            ServerCall sc = new ServerCall(ServiceName.pushNotification, ServiceOperation.DEREGISTER, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * Registers the given device token with the server to enable this device
     * to receive push notifications.
     *
     * @param platform The device platform
     * @param token The platform-dependant device token needed for push notifications.
     * @param callback The method to be invoked when the server response is received
     */
    public void registerPushNotificationToken(Platform platform, String token, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.deviceType.name(), platform.toString());
            data.put(Parameter.deviceToken.name(), token);

            ServerCall sc = new ServerCall(ServiceName.pushNotification, ServiceOperation.REGISTER, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException je) {
        }
    }

    /**
     * Sends a simple push notification based on the passed in message.
     * NOTE: It is possible to send a push notification to oneself.
     *
     * @param toProfileId The braincloud profileId of the user to receive the notification
     * @param message Text of the push notification
     * @param callback The method to be invoked when the server response is received
     */
    public void sendSimplePushNotification(String toProfileId, String message, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.toPlayerId.name(), toProfileId);
            data.put(Parameter.message.name(), message);

            ServerCall sc = new ServerCall(ServiceName.pushNotification, ServiceOperation.SEND_SIMPLE, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException je) {
        }
    }

    /**
     * Sends a notification to a user based on a brainCloud portal configured notification template.
     * NOTE: It is possible to send a push notification to oneself.
     *
     * @param toProfileId The braincloud profileId of the user to receive the notification
     * @param notificationTemplateId Id of the notification template
     * @param callback The method to be invoked when the server response is received
     */
    public void sendRichPushNotification(String toProfileId, int notificationTemplateId, IServerCallback callback) {
        sendRichPushNotificationWithParams(toProfileId, notificationTemplateId, null, callback);
    }

    /**
     * Sends a notification to a user based on a brainCloud portal configured notification template.
     * Includes JSON defining the substitution params to use with the template.
     * See the Portal documentation for more info.
     * NOTE: It is possible to send a push notification to oneself.
     *
     * @param toProfileId The braincloud profileId of the user to receive the notification
     * @param notificationTemplateId Id of the notification template
     * @param substitutionJson JSON defining the substitution params to use with the template
     * @param callback The method to be invoked when the server response is received
     */
    public void sendRichPushNotificationWithParams(String toProfileId, int notificationTemplateId, String substitutionJson, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.toPlayerId.name(), toProfileId);
            data.put(Parameter.notificationTemplateId.name(), notificationTemplateId);
            if (StringUtil.IsOptionalParameterValid(substitutionJson)) {
                JSONObject subJson = new JSONObject(substitutionJson);
                data.put(Parameter.substitutions.name(), subJson);
            }

            ServerCall sc = new ServerCall(ServiceName.pushNotification, ServiceOperation.SEND_RICH, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * Sends a notification to a "group" of user based on a brainCloud portal configured notification template.
     * Includes JSON defining the substitution params to use with the template.
     * See the Portal documentation for more info.
     *
     * @param groupId Target group
     * @param notificationTemplateId Template to use
     * @param substitutionsJson Map of substitution positions to strings
     * @param callback The method to be invoked when the server response is received
     */
    public void sendTemplatedPushNotificationToGroup(String groupId, int notificationTemplateId, String substitutionsJson, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.groupId.name(), groupId);
            data.put(Parameter.notificationTemplateId.name(), notificationTemplateId);
            if (StringUtil.IsOptionalParameterValid(substitutionsJson)) {
                data.put(Parameter.substitutions.name(), new JSONObject(substitutionsJson));
            }

            ServerCall sc = new ServerCall(ServiceName.pushNotification, ServiceOperation.SEND_TEMPLATED_TO_GROUP, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * Sends a notification to a "group" of user consisting of alert content and custom data.
     * See the Portal documentation for more info.
     *
     * @param groupId Target group
     * @param alertContentJson Body and title of alert
     * @param customDataJson Optional custom data
     * @param callback The method to be invoked when the server response is received
     */
    public void sendNormalizedPushNotificationToGroup(String groupId, String alertContentJson, String customDataJson, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.groupId.name(), groupId);
            data.put(Parameter.alertContent.name(), new JSONObject(alertContentJson));
            if (StringUtil.IsOptionalParameterValid(customDataJson)) {
                data.put(Parameter.customData.name(), new JSONObject(customDataJson));
            }

            ServerCall sc = new ServerCall(ServiceName.pushNotification, ServiceOperation.SEND_NORMALIZED_TO_GROUP, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * Schedules a normalized push notification to a user
     *
     * @param profileId The profileId of the user to receive the notification
     * @param alertContentJson Body and title of alert
     * @param customDataJson Optional custom data
     * @param startTime Start time of sending the push notification
     * @param callback The method to be invoked when the server response is received
     */
    public void scheduleNormalizedPushNotificationUTC(String profileId, String alertContentJson, String customDataJson,
                                                      int startTime, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.profileId.name(), profileId);
            data.put(Parameter.alertContent.name(), new JSONObject(alertContentJson));
            if (StringUtil.IsOptionalParameterValid(customDataJson)) {
                data.put(Parameter.customData.name(), new JSONObject(customDataJson));
            }

            data.put(Parameter.startDateUTC.name(), startTime);

            ServerCall sc = new ServerCall(ServiceName.pushNotification, ServiceOperation.SCHEDULE_NORMALIZED_NOTIFICATION, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * Schedules a normalized push notification to a user
     *
     * @param profileId The profileId of the user to receive the notification
     * @param alertContentJson Body and title of alert
     * @param customDataJson Optional custom data
     * @param minutesFromNow Minutes from now to send the push notification
     * @param callback The method to be invoked when the server response is received
     */
    public void scheduleNormalizedPushNotificationMinutes(String profileId, String alertContentJson, String customDataJson,
                                                          int minutesFromNow, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.profileId.name(), profileId);
            data.put(Parameter.alertContent.name(), new JSONObject(alertContentJson));
            if (StringUtil.IsOptionalParameterValid(customDataJson)) {
                data.put(Parameter.customData.name(), new JSONObject(customDataJson));
            }

            data.put(Parameter.minutesFromNow.name(), minutesFromNow);

            ServerCall sc = new ServerCall(ServiceName.pushNotification, ServiceOperation.SCHEDULE_NORMALIZED_NOTIFICATION, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * Schedules a rich push notification to a user
     *
     * @param profileId The profileId of the user to receive the notification
     * @param notificationTemplateId Body and title of alert
     * @param substitutionsJson Map of substitution positions to strings
     * @param startTime Start time of sending the push notification
     * @param callback The method to be invoked when the server response is received
     */
    public void scheduleRichPushNotificationUTC(String profileId, int notificationTemplateId, String substitutionsJson,
                                                int startTime, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.profileId.name(), profileId);
            data.put(Parameter.notificationTemplateId.name(), notificationTemplateId);
            if (StringUtil.IsOptionalParameterValid(substitutionsJson)) {
                data.put(Parameter.substitutions.name(), new JSONObject(substitutionsJson));
            }

            data.put(Parameter.startDateUTC.name(), startTime);

            ServerCall sc = new ServerCall(ServiceName.pushNotification, ServiceOperation.SCHEDULE_RICH_NOTIFICATION, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }


    /**
     * Schedules a rich push notification to a user
     *
     * @param profileId The profileId of the user to receive the notification
     * @param notificationTemplateId Body and title of alert
     * @param substitutionsJson Map of substitution positions to strings
     * @param minutesFromNow Minutes from now to send the push notification
     * @param callback The method to be invoked when the server response is received
     */
    public void scheduleRichPushNotificationMinutes(String profileId, int notificationTemplateId, String substitutionsJson,
                                                    int minutesFromNow, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.profileId.name(), profileId);
            data.put(Parameter.notificationTemplateId.name(), notificationTemplateId);
            if (StringUtil.IsOptionalParameterValid(substitutionsJson)) {
                data.put(Parameter.substitutions.name(), new JSONObject(substitutionsJson));
            }

            data.put(Parameter.minutesFromNow.name(), minutesFromNow);

            ServerCall sc = new ServerCall(ServiceName.pushNotification, ServiceOperation.SCHEDULE_RICH_NOTIFICATION, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }


    /**
     * Sends a notification to a user consisting of alert content and custom data.
     *
     * @param toProfileId The profileId of the user to receive the notification
     * @param alertContentJson Body and title of alert
     * @param customDataJson Optional custom data
     * @param callback The method to be invoked when the server response is received
     */
    public void sendNormalizedPushNotification(String toProfileId, String alertContentJson, String customDataJson, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.toPlayerId.name(), toProfileId);
            data.put(Parameter.alertContent.name(), new JSONObject(alertContentJson));
            if (StringUtil.IsOptionalParameterValid(customDataJson)) {
                data.put(Parameter.customData.name(), new JSONObject(customDataJson));
            }

            ServerCall sc = new ServerCall(ServiceName.pushNotification, ServiceOperation.SEND_NORMALIZED, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /**
     * Sends a notification to multiple users consisting of alert content and custom data.
     *
     * @param profileIds Collection of profile IDs to send the notification to
     * @param alertContentJson Body and title of alert
     * @param customDataJson Optional custom data
     * @param callback The method to be invoked when the server response is received
     */
    public void sendNormalizedPushNotificationBatch(String[] profileIds, String alertContentJson, String customDataJson, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.profileIds.name(), profileIds);
            data.put(Parameter.alertContent.name(), new JSONObject(alertContentJson));
            if (StringUtil.IsOptionalParameterValid(customDataJson)) {
                data.put(Parameter.customData.name(), new JSONObject(customDataJson));
            }

            ServerCall sc = new ServerCall(ServiceName.pushNotification, ServiceOperation.SEND_NORMALIZED_BATCH, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }
}
