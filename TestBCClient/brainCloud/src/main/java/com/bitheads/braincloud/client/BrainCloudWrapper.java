package com.bitheads.braincloud.client;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The BrainCloudWrapper provides some convenience functionality to developers when they are
 * getting started with the authentication system.
 *
 * By using the wrapper authentication methods, the anonymous and profile ids will be automatically
 * persisted upon successful authentication. When authenticating, any stored anonymous/profile ids will
 * be sent to the server. This strategy is useful when using anonymous authentication.
 */
public class BrainCloudWrapper implements IServerCallback {
    protected Context _context = null;

    protected boolean _alwaysAllowProfileSwitch = true;
    protected IServerCallback _authenticateCallback = null;

    public static String AUTHENTICATION_ANONYMOUS = "anonymous";
    protected static String _SHARED_PREFERENCES = "bcprefs";
    private static String _DEFAULT_URL = "https://sharedprod.braincloudservers.com/dispatcherv2";
    private static BrainCloudWrapper _instance = new BrainCloudWrapper();

    protected BrainCloudWrapper() {
    }

    /**
     * Method returns a singleton instance of the BrainCloudWrapper.
     * @return A singleton instance of the BrainCloudWrapper.
     */
    public static BrainCloudWrapper getInstance() {
        return _instance;
    }

    /**
     * Returns a singleton instance of the BrainCloudClient.
     * @return A singleton instance of the BrainCloudClient.
     */
    public static BrainCloudClient getBC() {
        return BrainCloudClient.getInstance();
    }

    /**
     * Sets the context required for saving anonymous and profile ids to the
     * private SharedPreferences file.
     * @param ctx The application context
     */
    public void setContext(Context ctx) {
        _context = ctx;
    }

    /**
     * Method initializes the BrainCloudClient.
     *
     * @param ctx The application context
     * @param appId The app id
     * @param secretKey The secret key for your app
     * @param appVersion The app version
     */
    public void initialize(Context ctx, String appId, String secretKey, String appVersion) {
        setContext(ctx);
        BrainCloudClient.getInstance().initialize(appId, secretKey, appVersion, _DEFAULT_URL);
    }

    /**
     * Method initializes the BrainCloudClient.
     *
     * @param ctx The application context
     * @param appId The app id
     * @param secretKey The secret key for your app
     * @param appVersion The app version
     * @param serverUrl The url to the brainCloud server
     */
    public void initialize(Context ctx, String appId, String secretKey, String appVersion, String serverUrl) {
        setContext(ctx);
        BrainCloudClient.getInstance().initialize(appId, secretKey, appVersion, serverUrl);
    }


    /**
     * Method initializes the BrainCloudClient. Make sure to
     * set the context via setContext() if you're using this method.
     *
     * @param appId The app id
     * @param secretKey The secret key for your app
     * @param appVersion The app version
     * @param serverUrl The url to the brainCloud server
     */
    public void initialize(String appId, String secretKey, String appVersion, String serverUrl) {
        BrainCloudClient.getInstance().initialize(appId, secretKey, appVersion, serverUrl);
    }

    protected void initializeIdentity(boolean isAnonymousAuth) {
        // check if we already have saved IDs
        String profileId = getStoredProfileId();
        String anonymousId = getStoredAnonymousId();

        // create an anonymous ID if necessary
        if ((!anonymousId.isEmpty() && profileId.isEmpty()) || anonymousId.isEmpty()) {
            anonymousId = BrainCloudClient.getInstance().getAuthenticationService().generateAnonymousId();
            profileId = "";
            setStoredAnonymousId(anonymousId);
            setStoredProfileId(profileId);
        }

        String profileIdToAuthenticateWith = profileId;
        if (!isAnonymousAuth && _alwaysAllowProfileSwitch) {
            profileIdToAuthenticateWith = "";
        }
        setStoredAuthenticationType(isAnonymousAuth ? AUTHENTICATION_ANONYMOUS : "");

        // send our IDs to brainCloud
        BrainCloudClient.getInstance().initializeIdentity(profileIdToAuthenticateWith, anonymousId);
    }

    /**
     * Returns the stored profile id
     * @return The stored profile id
     */
    public String getStoredProfileId() {
        SharedPreferences sharedPref = _context.getSharedPreferences(_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPref.getString("profileId", "");
    }

    /**
     * Sets the stored profile id
     * @param profileId The profile id to set
     */
    public void setStoredProfileId(String profileId) {
        SharedPreferences sharedPref = _context.getSharedPreferences(_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("profileId", profileId);
        editor.commit();
    }

    /**
     * Resets the profile id to empty string
     */
    public void resetStoredProfileId() {
        setStoredProfileId("");
    }

    /**
     * Returns the stored anonymous id
     * @return The stored anonymous id
     */
    String getStoredAnonymousId() {
        SharedPreferences sharedPref = _context.getSharedPreferences(_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPref.getString("anonymousId", "");
    }

    /**
     * Sets the stored anonymous id
     * @param anonymousId The anonymous id to set
     */
    public void setStoredAnonymousId(String anonymousId) {
        SharedPreferences sharedPref = _context.getSharedPreferences(_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("anonymousId", anonymousId);
        editor.commit();
    }

    /**
     * Resets the anonymous id to empty string
     */
    public void resetStoredAnonymousId() {
        setStoredAnonymousId("");
    }

    /**
     * For non-anonymous authentication methods, a profile id will be passed in
     * when this value is set to false. This will generate an error on the server
     * if the profile id passed in does not match the profile associated with the
     * authentication credentials. By default, this value is true.
     *
     * @param alwaysAllow Controls whether the profile id is passed in with
     * non-anonymous authentications.
     */
    public void setAlwaysAllowProfileSwitch(boolean alwaysAllow) {
        _alwaysAllowProfileSwitch = alwaysAllow;
    }

    /**
     * Returns the value for always allow profile switch
     * @return Whether to always allow profile switches
     */
    public boolean getAlwaysAllowProfileSwitch() {
        return _alwaysAllowProfileSwitch;
    }

    // these methods are not really used
    protected String getStoredAuthenticationType() {
        SharedPreferences sharedPref = _context.getSharedPreferences(_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPref.getString("authenticationType", "");
    }

    protected void setStoredAuthenticationType(String authenticationType) {
        SharedPreferences sharedPref = _context.getSharedPreferences(_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("authenticationType", authenticationType);
        editor.commit();
    }

    protected void resetStoredAuthenticationType() {
        setStoredAuthenticationType("");
    }

    /**
     * Authenticate a user anonymously with brainCloud - used for apps that
     * don't want to bother the user to login, or for users who are sensitive to
     * their privacy
     *
     * @param callback    The callback handler
     */
    public void authenticateAnonymous(IServerCallback callback) {
        _authenticateCallback = callback;

        initializeIdentity(true);

        BrainCloudClient.getInstance().getAuthenticationService().authenticateAnonymous(true, this);
    }

    /**
     * Authenticate the user with a custom Email and Password. Note that the
     * client app is responsible for collecting (and storing) the e-mail and
     * potentially password (for convenience) in the client data. For the
     * greatest security, force the user to re-enter their * password at each
     * login. (Or at least give them that option).
     *
     * Note that the password sent from the client to the server is protected
     * via SSL.
     *
     * @param email       The e-mail address of the user
     * @param password    The password of the user
     * @param forceCreate Should a new profile be created for this user if the account
     *                    does not exist?
     * @param callback    The callback handler
     */
    public void authenticateEmailPassword(String email,
                                          String password,
                                          boolean forceCreate,
                                          IServerCallback callback) {
        _authenticateCallback = callback;

        initializeIdentity(false);

        BrainCloudClient.getInstance().getAuthenticationService().authenticateEmailPassword(email, password, forceCreate, this);
    }

    /**
     * Authenticate the user via cloud code (which in turn validates the supplied credentials against an external system).
     * This allows the developer to extend brainCloud authentication to support other backend authentication systems.
     *
     * Service Name - Authenticate
     * Server Operation - Authenticate
     *
     * @param userId           The user id
     * @param token            The user token (password etc)
     * @param externalAuthName The name of the cloud script to call for external authentication
     * @param forceCreate      Should a new profile be created for this user if the account
     *                         does not exist?
     * @returns performs the in_success callback on success, in_failure callback on failure
     */
    public void authenticateExternal(String userId,
                                     String token,
                                     String externalAuthName,
                                     boolean forceCreate,
                                     IServerCallback callback) {
        _authenticateCallback = callback;

        initializeIdentity(false);

        BrainCloudClient.getInstance().getAuthenticationService().authenticateExternal(userId, token, externalAuthName, forceCreate, this);
    }

    /**
     * Authenticate the user with brainCloud using their Facebook Credentials
     *
     * @param fbUserId    The facebook id of the user
     * @param fbAuthToken The validated token from the Facebook SDK (that will be
     *                    further validated when sent to the bC service)
     * @param forceCreate Should a new profile be created for this user if the account
     *                    does not exist?
     * @param callback    The callback handler
     */
    public void authenticateFacebook(String fbUserId,
                                     String fbAuthToken,
                                     boolean forceCreate,
                                     IServerCallback callback) {
        _authenticateCallback = callback;

        initializeIdentity(false);

        BrainCloudClient.getInstance().getAuthenticationService().authenticateFacebook(fbUserId, fbAuthToken, forceCreate, this);
    }

    /**
     * Authenticate the user using a google userid(email address) and google
     * authentication token.
     *
     * @param googleUserId    String representation of google+ userid (email)
     * @param googleAuthToken The authentication token derived via the google apis.
     * @param forceCreate     Should a new profile be created for this user if the account
     *                        does not exist?
     * @param callback        The callback handler
     */
    public void authenticateGoogle(String googleUserId,
                                   String googleAuthToken,
                                   boolean forceCreate,
                                   IServerCallback callback) {
        _authenticateCallback = callback;

        initializeIdentity(false);

        BrainCloudClient.getInstance().getAuthenticationService().authenticateGoogle(googleUserId, googleAuthToken, forceCreate, this);
    }

    /**
     * Authenticate the user using a steam userid and session ticket (without
     * any validation on the userid).
     *
     * @param steamUserId        String representation of 64 bit steam id
     * @param steamSessionTicket The session ticket of the user (hex encoded)
     * @param forceCreate        Should a new profile be created for this user if the account
     *                           does not exist?
     * @param callback           The callback handler
     */
    public void authenticateSteam(String steamUserId,
                                  String steamSessionTicket,
                                  boolean forceCreate,
                                  IServerCallback callback) {
        _authenticateCallback = callback;

        initializeIdentity(false);

        BrainCloudClient.getInstance().getAuthenticationService().authenticateSteam(steamUserId, steamSessionTicket, forceCreate, this);
    }

    /**
     * Authenticate the user using a Twitter userid, authentication token, and secret from Twitter.
     *
     * Service Name - Authenticate
     * Service Operation - Authenticate
     *
     * @param userId String representation of Twitter userid
     * @param token The authentication token derived via the Twitter apis.
     * @param secret The secret given when attempting to link with Twitter
     * @param forceCreate Should a new profile be created for this user if the account does not exist?
     * @param callback The callback handler
     *
     * @returns performs the in_success callback on success, in_failure callback on failure
     *
     */
    public void authenticateTwitter(String userId,
                                    String token,
                                    String secret,
                                    boolean forceCreate,
                                    IServerCallback callback) {
        _authenticateCallback = callback;

        initializeIdentity(false);

        BrainCloudClient.getInstance().getAuthenticationService().authenticateTwitter(userId, token, secret, forceCreate, this);
    }


    /**
     * Authenticate the user using a userid and password (without any validation
     * on the userid). Similar to AuthenticateEmailPassword - except that that
     * method has additional features to allow for e-mail validation, password
     * resets, etc.
     *
     * @param userId       The e-mail address of the user
     * @param userPassword The password of the user
     * @param forceCreate  Should a new profile be created for this user if the account
     *                     does not exist?
     * @param callback     The callback handler
     */
    public void authenticateUniversal(String userId,
                                      String userPassword,
                                      boolean forceCreate,
                                      IServerCallback callback) {
        _authenticateCallback = callback;

        initializeIdentity(false);

        BrainCloudClient.getInstance().getAuthenticationService().authenticateUniversal(userId, userPassword, forceCreate, this);
    }

    /**
     * Re-authenticates the user with brainCloud
     *
     * @param callback The callback handler
     */
    public void reconnect(IServerCallback callback) {
        authenticateAnonymous(callback);
    }

    /**
     * Run callbacks, to be called once per frame from your main thread
     */
    public void runCallbacks() {
        BrainCloudClient.getInstance().runCallbacks();
    }


    /**
     * The serverCallback() method returns server data back to the layer
     * interfacing with the BrainCloud library.
     *
     * @param serviceName - name of the requested service
     * @param serviceOperation - requested operation
     * @param jsonData - returned data from the server
     */
    public void serverCallback(ServiceName serviceName, ServiceOperation serviceOperation, JSONObject jsonData) {
        if (serviceName.equals(ServiceName.authenticationV2) && serviceOperation.equals(ServiceOperation.AUTHENTICATE)) {
            try {
                String profileId = jsonData.getJSONObject("data").getString("profileId");
                if (!profileId.isEmpty()) {
                    setStoredProfileId(profileId);
                }
            } catch (JSONException je) {
                je.printStackTrace();
            }
        }

        if (_authenticateCallback != null) {
            _authenticateCallback.serverCallback(serviceName, serviceOperation, jsonData);
        }
    }

    /**
     * Errors are returned back to the layer which is interfacing with the
     * BrainCloud library through the serverError() callback.
     *
     * A server error might indicate a failure of the client to communicate
     * with the server after N retries.
     *
     * @param serviceName - name of the requested service
     * @param serviceOperation - requested operation
     * @param statusCode The error status return code (400, 403, 500, etc)
     * @param reasonCode The brainCloud reason code (see reason codes on apidocs site)
     * @param jsonError The error json string
     */
    public void serverError(ServiceName serviceName, ServiceOperation serviceOperation, int statusCode, int reasonCode, String jsonError) {
        if (_authenticateCallback != null) {
            _authenticateCallback.serverError(serviceName, serviceOperation, statusCode, reasonCode, jsonError);
        }
    }
}
