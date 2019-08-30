package com.bitheads.braincloud.services;

import com.bitheads.braincloud.client.AuthenticationType;
import com.bitheads.braincloud.client.BrainCloudClient;
import com.bitheads.braincloud.client.IServerCallback;
import com.bitheads.braincloud.client.ServiceName;
import com.bitheads.braincloud.client.ServiceOperation;
import com.bitheads.braincloud.comms.ServerCall;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInventoryManagementService {

    private enum Parameter {
        defId,
        quantity,
        includeDef,
        criteria, 
        context, 
        pageOffset,
        itemId,
        version,
        immediate,
        shopId,
        newItemData,
        profileId
    }

    private BrainCloudClient _client;

    public UserInventoryManagementService(BrainCloudClient client) {
        _client = client;
    }

	/**
	 * Allows item(s) to be awarded to a user without collecting
	 *  the purchase amount. If includeDef is true, response 
	 * includes associated itemDef with language fields limited
	 *  to the current or default language.
	 *
	 * Service Name - userInventoryManagement
	 * Service Operation - AWARD_USER_ITEM
	 *
	 * @param defId 
	 * @param quantity
	 * @param includeDef 
	 */
    public void awardUserItem(String defId, int quantity, boolean includeDef, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.defId.name(), defId);
            data.put(Parameter.quantity.name(), quantity);
            data.put(Parameter.includeDef.name(), includeDef);

            ServerCall sc = new ServerCall(ServiceName.userInventoryManagement, ServiceOperation.AWARD_USER_ITEM, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException ignored) {
        }
    }

	/**
	 * Allows a quantity of a specified user item to be dropped, 
	 * without any recovery of the money paid for the item. 
	 * If any quantity of the user item remains, it will be returned,
	 * potentially with the associated itemDef (with language fields 
	 * limited to the current or default language).
	 *
	 * Service Name - userInventoryManagement
	 * Service Operation - DROP_USER_ITEM
	 *
	 * @param defId 
	 * @param quantity
	 * @param includeDef 
	 */
    public void dropUserItem(String itemId, int quantity, boolean includeDef, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.itemId.name(), itemId);
            data.put(Parameter.quantity.name(), quantity);
            data.put(Parameter.includeDef.name(), includeDef);

            ServerCall sc = new ServerCall(ServiceName.userInventoryManagement, ServiceOperation.DROP_USER_ITEM, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException ignored) {
        }
    }

	/**
	 * Retrieves the user's inventory from the 
	 * server (or inventory specified by criteria). 
	 * If includeDef is true, response includes associated 
	 * itemDef with each user item, with language fields 
	 * limited to the current or default language.
	 *
	 * Service Name - userInventoryManagement
	 * Service Operation - GET_USER_INVENTORY
	 *
	 * @param criteria 
	 * @param includeDef 
	 */
    public void getUserInventory(String criteria, boolean includeDef, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.newItemData.name(), new JSONObject(criteria));
            data.put(Parameter.includeDef.name(), includeDef);

            ServerCall sc = new ServerCall(ServiceName.userInventoryManagement, ServiceOperation.GET_USER_INVENTORY, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException ignored) {
        }
    }

	/**
	 * Retrieves the page of user's inventory from the server 
	 * based on the context. If includeDef is true, response
	 *  includes associated itemDef with each user item, with 
	 * language fields limited to the current or default language.
	 *
	 * Service Name - userInventoryManagement
	 * Service Operation - GET_USER_INVENTORY_PAGE
	 *
	 * @param context
	 * @param includeDef 
	 */
    public void getUserInventoryPage(String context, boolean includeDef, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();

            data.put(Parameter.context.name(), new JSONObject(context));
            data.put(Parameter.includeDef.name(), includeDef);

            ServerCall sc = new ServerCall(ServiceName.userInventoryManagement, ServiceOperation.GET_USER_INVENTORY_PAGE, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException ignored) {
        }
    }

	/**
	 * Retrieves the page of user's inventory from the server
	 *  based on the encoded context. If includeDef is true, 
	 * response includes associated itemDef with each user item, 
	 * with language fields limited to the current or default
	 * language.
	 *
	 * Service Name - userInventoryManagement
	 * Service Operation - GET_USER_INVENTORY_PAGE_OFFSET
	 *
	 * @param context
	 * @param pageOffset
	 * @param includeDef 
	 */
    public void getUserInventoryPageOffset(String context, int pageOffset, boolean includeDef, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.context.name(), context);
            data.put(Parameter.pageOffset.name(), pageOffset);
            data.put(Parameter.includeDef.name(), includeDef);

            ServerCall sc = new ServerCall(ServiceName.userInventoryManagement, ServiceOperation.GET_USER_INVENTORY_PAGE_OFFSET, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException ignored) {
        }
    }

	/**
	 * Retrieves the identified user item from the server. 
	 * If includeDef is true, response includes associated
	 * itemDef with language fields limited to the current 
	 * or default language.
	 *
	 * Service Name - userInventoryManagement
	 * Service Operation - GET_USER_ITEM
	 *
	 * @param itemId
	 * @param includeDef 
	 * @param callback The method to be invoked when the server response is received
	 */
    public void getUserItem(String itemId, boolean includeDef, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.itemId.name(), itemId);
            data.put(Parameter.includeDef.name(), includeDef);

            ServerCall sc = new ServerCall(ServiceName.userInventoryManagement, ServiceOperation.GET_USER_ITEM, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException ignored) {
        }
    }

	/**
	 * Gifts item to the specified player.
	 *
	 * Service Name - userInventoryManagement
	 * Service Operation - GIVE_USER_ITEM_TO
	 *
	 * @param profileId
	 * @param itemId
	 * @param version
	 * @param immediate 
	 */
    public void giveUserItemTo(String profileId, String itemId, int version, boolean immediate, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.profileId.name(), profileId);
            data.put(Parameter.itemId.name(), itemId);
            data.put(Parameter.version.name(), version);
            data.put(Parameter.immediate.name(), immediate);

            ServerCall sc = new ServerCall(ServiceName.userInventoryManagement, ServiceOperation.GIVE_USER_ITEM_TO, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException ignored) {
        }
    }

	/**
	 * Retrieves the identified user item from the server. 
	 * If includeDef is true, response includes associated
	 * itemDef with language fields limited to the current 
	 * or default language.
	 *
	 * Service Name - userInventoryManagement
	 * Service Operation - PURCHASE_USER_ITEM
	 *
	 * @param defId
	 * @param quantity
	 * @param shopId
	 * @param includeDef 
	 */
    public void purchaseUserItem(String defId, int quantity, String shopId, boolean includeDef, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.defId.name(), defId);
            data.put(Parameter.quantity.name(), quantity);
            data.put(Parameter.shopId.name(), shopId);
            data.put(Parameter.includeDef.name(), includeDef);

            ServerCall sc = new ServerCall(ServiceName.userInventoryManagement, ServiceOperation.PURCHASE_USER_ITEM, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException ignored) {
        }
    }

	/**
	 * Retrieves and transfers the gift item from 
	 * the specified player, who must have previously 
	 * called giveUserItemTo.
	 *
	 * Service Name - userInventoryManagement
	 * Service Operation - RECEVIE_USER_ITEM_FROM
	 *
	 * @param profileId
	 * @param itemId
	 */
    public void receiveUserItemFrom(String profileId, String itemId, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.profileId.name(), profileId);
            data.put(Parameter.itemId.name(), itemId);

            ServerCall sc = new ServerCall(ServiceName.userInventoryManagement, ServiceOperation.RECEIVE_USER_ITEM_FROM, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException ignored) {
        }
    }

	/**
	 * Allows a quantity of a specified user item to be sold. 
	 * If any quantity of the user item remains, it will be returned, 
	 * potentially with the associated itemDef (with language fields 
	 * limited to the current or default language), along with the 
	 * currency refunded and currency balances.
	 *
	 * Service Name - userInventoryManagement
	 * Service Operation - SELL_USER_ITEM
	 *
	 * @param itemId
	 * @param version
	 * @param quantity
	 * @param shopId
	 * @param includeDef 
	 */
    public void sellUserItem(String itemId, int version, int quantity, String shopId, boolean includeDef, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.itemId.name(), itemId);
            data.put(Parameter.version.name(), version);
            data.put(Parameter.quantity.name(), quantity);
            data.put(Parameter.shopId.name(), shopId);
            data.put(Parameter.includeDef.name(), includeDef);


            ServerCall sc = new ServerCall(ServiceName.userInventoryManagement, ServiceOperation.SELL_USER_ITEM, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException ignored) {
        }
    }

	/**
	 * Updates the item data on the specified user item.
	 *
	 * Service Name - userInventoryManagement
	 * Service Operation - UPDATE_USER_ITEM_DATA
	 *
	 * @param itemId
	 * @param version
	 * @param newItemData
	 */
    public void updateUserItemData(String itemId, int version, String newItemData, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.itemId.name(), itemId);
            data.put(Parameter.version.name(), version);
            data.put(Parameter.newItemData.name(), new JSONObject(newItemData));

            ServerCall sc = new ServerCall(ServiceName.userInventoryManagement, ServiceOperation.UPDATE_USER_ITEM_DATA, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException ignored) {
        }
    }

	/**
	 * Uses the specified item, potentially consuming it.
	 *
	 * Service Name - userInventoryManagement
	 * Service Operation - USE_USER_ITEM
	 *
	 * @param itemId
	 * @param version
	 * @param newItemData
	 * @param includeDef 
	 */
    public void useUserItem(String itemId, int version, String newItemData, boolean includeDef, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.itemId.name(), itemId);
            data.put(Parameter.version.name(), version);
            data.put(Parameter.newItemData.name(), new JSONObject(newItemData));
            data.put(Parameter.includeDef.name(), includeDef);

            ServerCall sc = new ServerCall(ServiceName.userInventoryManagement, ServiceOperation.USE_USER_ITEM, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException ignored) {
        }
	}
	
	/**
	 * Publishes the specified item to the item management attached blockchain. Results are reported asynchronously via an RTT event.
	 *
	 * Service Name - userInventoryManagement
	 * Service Operation - PUBLISH_USER_ITEM_TO_BLOCKCHAIN
	 *
	 * @param itemId
	 * @param version
	 */
    public void publishUserItemToBlockchain(String itemId, int version, IServerCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put(Parameter.itemId.name(), itemId);
            data.put(Parameter.version.name(), version);

            ServerCall sc = new ServerCall(ServiceName.userInventoryManagement, ServiceOperation.PUBLISH_USER_ITEM_TO_BLOCKCHAIN, data, callback);
            _client.sendRequest(sc);
        } catch (JSONException ignored) {
        }
	}
	
	/**
	 * Syncs the caller's user items with the item management attached blockchain. Results are reported asynchronously via an RTT event.
	 *
	 * Service Name - userInventoryManagement
	 * Service Operation - REFRESH_BLOCKCHAIN_USER_ITEMS
	 *
	 */
    public void refreshBlockchainUserItems(IServerCallback callback) {
            JSONObject data = new JSONObject();
            ServerCall sc = new ServerCall(ServiceName.userInventoryManagement, ServiceOperation.REFRESH_BLOCKCHAIN_USER_ITEMS, data, callback);
            _client.sendRequest(sc);
    }
}
