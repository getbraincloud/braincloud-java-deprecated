package com.bitheads.braincloud.services;

import com.bitheads.braincloud.client.BrainCloudClient;
import com.bitheads.braincloud.client.IServerCallback;
import com.bitheads.braincloud.client.ServiceName;
import com.bitheads.braincloud.client.ServiceOperation;
import com.bitheads.braincloud.comms.ServerCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PresenceService
{
	private enum Parameter
	{
		in_platform,
		in_includeOffline,
		in_groupId,
		in_profileIds,
		in_bidirectional,
		in_visible,
		in_jsonActivity
	}

	private BrainCloudClient _client;
	
	public PresenceService(BrainCloudClient client)
	{
		_client = client; 
	}

 	/**
	* Force an RTT presence update to all listeners of the caller.
	*
	* Service Name - Presence
	* Service Operation - ForcePush
	*
	* @param callback The method to be invoked when the server response is received
	*/
	public void forcePush(IServerCallback in_callback)
	{
		ServerCall sc = new ServerCall(ServiceName.presence, ServiceOperation.FORCE_PUSH, null, in_callback);
		_client.sendRequest(sc);	
	}

	/**
	* Gets the presence data for the given <platform>. Can be one of "all",
	* "brainCloud", or "facebook". Will not include offline profiles
	* unless <includeOffline> is set to true.
	*/
	public void getPresenceOfFriends(String in_platform, boolean in_includeOffline, IServerCallback in_callback)
	{
		try {
			JSONObject data = new JSONObject();
			data.put(Parameter.in_platform.name(), in_platform);
			data.put(Parameter.in_includeOffline.name(), in_includeOffline);

			ServerCall sc = new ServerCall(ServiceName.presence, ServiceOperation.GET_PRESENCE_OF_FRIENDS, data, in_callback);
			_client.sendRequest(sc);	
		} catch (JSONException je) {
			je.printStackTrace();
		}
	}

	/**
	* Gets the presence data for the given <groupId>. Will not include
	* offline profiles unless <includeOffline> is set to true.
	*/
	public void getPresenceOfGroup(String in_groupId, boolean in_includeOffline, IServerCallback in_callback)
	{
		try {
			JSONObject data = new JSONObject();
			data.put(Parameter.in_groupId.name(), in_groupId);
			data.put(Parameter.in_includeOffline.name(), in_includeOffline);

			ServerCall sc = new ServerCall(ServiceName.presence, ServiceOperation.GET_PRESENCE_OF_GROUP, data, in_callback);
			_client.sendRequest(sc);	
		} catch (JSONException je) {
			je.printStackTrace();
		}
	}

	/**
	* Gets the presence data for the given <profileIds>. Will not include
	* offline profiles unless <includeOffline> is set to true.
	*/
	public void getPresenceOfUsers(ArrayList<String> in_profileIds, boolean in_includeOffline, IServerCallback in_callback)
	{
		try {
			JSONObject data = new JSONObject();
			data.put(Parameter.in_profileIds.name(), in_profileIds);
			data.put(Parameter.in_includeOffline.name(), in_includeOffline);

			ServerCall sc = new ServerCall(ServiceName.presence, ServiceOperation.GET_PRESENCE_OF_USERS, data, in_callback);
			_client.sendRequest(sc);	
		} catch (JSONException je) {
			je.printStackTrace();
		}
	}

	/**
	* Registers the caller for RTT presence updates from friends for the
	* given <platform>. Can be one of "all", "brainCloud", or "facebook".
	* If <bidirectional> is set to true, then also registers the targeted
	* users for presence updates from the caller.
	*/
	public void registerListenersForFriends(String in_platform, boolean in_bidirectional, IServerCallback in_callback)
	{
		try {
			JSONObject data = new JSONObject();
			data.put(Parameter.in_platform.name(), in_platform);
			data.put(Parameter.in_bidirectional.name(), in_bidirectional);

			ServerCall sc = new ServerCall(ServiceName.presence, ServiceOperation.REGISTER_LISTENER_FOR_FRIENDS, data, in_callback);
			_client.sendRequest(sc);	
		} catch (JSONException je) {
			je.printStackTrace();
		}
	}

	/**
	* Registers the caller for RTT presence updates from the members of
	* the given <groupId>. Caller must be a member of said group. If
	* <bidirectional> is set to true, then also registers the targeted
	* users for presence updates from the caller.
	*/
	public void registerListenersForGroup(String in_groupId, boolean in_bidirectional, IServerCallback in_callback)
	{
		try {
			JSONObject data = new JSONObject();
			data.put(Parameter.in_groupId.name(), in_groupId);
			data.put(Parameter.in_bidirectional.name(), in_bidirectional);

			ServerCall sc = new ServerCall(ServiceName.presence, ServiceOperation.REGISTER_LISTENER_FOR_GROUP, data, in_callback);
			_client.sendRequest(sc);	
		} catch (JSONException je) {
			je.printStackTrace();
		}
	}

	/**
	* Registers the caller for RTT presence updates for the given
	* <profileIds>. If <bidirectional> is set to true, then also registers
	* the targeted users for presence updates from the caller.
	*/
	public void registerListenersForProfiles(ArrayList<String> in_profileIds, boolean in_bidirectional, IServerCallback in_callback)
	{
		try {
			JSONObject data = new JSONObject();
			data.put(Parameter.in_profileIds.name(), in_profileIds);
			data.put(Parameter.in_bidirectional.name(), in_bidirectional);

			ServerCall sc = new ServerCall(ServiceName.presence, ServiceOperation.REGISTER_LISTENER_FOR_PROFILES, data, in_callback);
			_client.sendRequest(sc);	
		} catch (JSONException je) {
			je.printStackTrace();
		}
	}

	/**
	* Update the presence data visible field for the caller.
	*/
	public void setVisibility(boolean in_visible, IServerCallback in_callback)
	{
		try {
			JSONObject data = new JSONObject();
			data.put(Parameter.in_visible.name(), in_visible);

			ServerCall sc = new ServerCall(ServiceName.presence, ServiceOperation.SET_VISIBILITY, data, in_callback);
			_client.sendRequest(sc);	
		} catch (JSONException je) {
			je.printStackTrace();
		}
	}

	/**
	* Stops the caller from receiving RTT presence updates. Does not
	* affect the broadcasting of *their* presence updates to other
	* listeners.
	*/
	public void stopListening(IServerCallback in_callback)
	{
		ServerCall sc = new ServerCall(ServiceName.presence, ServiceOperation.STOP_LISTENING, null, in_callback);
		_client.sendRequest(sc);
	}

	/**
	* Update the presence data activity field for the caller.
	*/
	public void updateActivity(String in_jsonActivity, IServerCallback in_callback)
	{
		try {
			JSONObject data = new JSONObject();
			data.put(Parameter.in_jsonActivity.name(), in_jsonActivity);

			ServerCall sc = new ServerCall(ServiceName.presence, ServiceOperation.UPDATE_ACTIVITY, data, in_callback);
			_client.sendRequest(sc);	
		} catch (JSONException je) {
			je.printStackTrace();
		}
	}
}