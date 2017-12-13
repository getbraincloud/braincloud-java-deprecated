package com.bitheads.braincloud.client;

import com.bitheads.braincloud.comms.BrainCloudRestClient;
import com.bitheads.braincloud.comms.ServerCall;
import com.bitheads.braincloud.services.AsyncMatchService;
import com.bitheads.braincloud.services.AuthenticationService;
import com.bitheads.braincloud.services.DataStreamService;
import com.bitheads.braincloud.services.EntityService;
import com.bitheads.braincloud.services.EventService;
import com.bitheads.braincloud.services.FileService;
import com.bitheads.braincloud.services.FriendService;
import com.bitheads.braincloud.services.GamificationService;
import com.bitheads.braincloud.services.GlobalAppService;
import com.bitheads.braincloud.services.GlobalEntityService;
import com.bitheads.braincloud.services.GlobalStatisticsService;
import com.bitheads.braincloud.services.GroupService;
import com.bitheads.braincloud.services.IdentityService;
import com.bitheads.braincloud.services.MailService;
import com.bitheads.braincloud.services.MatchMakingService;
import com.bitheads.braincloud.services.OneWayMatchService;
import com.bitheads.braincloud.services.PlaybackStreamService;
import com.bitheads.braincloud.services.PlayerStateService;
import com.bitheads.braincloud.services.PlayerStatisticsEventService;
import com.bitheads.braincloud.services.PlayerStatisticsService;
import com.bitheads.braincloud.services.ProductService;
import com.bitheads.braincloud.services.ProfanityService;
import com.bitheads.braincloud.services.PushNotificationService;
import com.bitheads.braincloud.services.RedemptionCodeService;
import com.bitheads.braincloud.services.S3HandlingService;
import com.bitheads.braincloud.services.ScriptService;
import com.bitheads.braincloud.services.SocialLeaderboardService;
import com.bitheads.braincloud.services.TimeService;
import com.bitheads.braincloud.services.TournamentService;

import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

public class BrainCloudClient {

    public static final boolean EnableSingletonMode = false;
    public static final String SingletonUseErrorMessage =
            "Singleton usage is disabled. If called by mistake, use your own variable that holds an instance of the bcWrapper/bcClient.";


    private String _appId;
    private Platform _releasePlatform;
    private String _appVersion;
    private String _countryCode;
    private String _languageCode;
    private double _timeZoneOffset;

    private final static String BRAINCLOUD_VERSION = "3.6.5";

    private BrainCloudRestClient _restClient;

    private AuthenticationService _authenticationService = new AuthenticationService(this);
    private AsyncMatchService _asyncMatchService = new AsyncMatchService(this);
    private DataStreamService _dataStreamService = new DataStreamService(this);
    private EntityService _entityService = new EntityService(this);
    private EventService _eventService = new EventService(this);
    private FileService _fileService = new FileService(this);
    private FriendService _friendService = new FriendService(this);
    private GamificationService _gamificationService = new GamificationService(this);
    private GlobalAppService _globalAppService = new GlobalAppService(this);
    private GlobalEntityService _globalEntityService = new GlobalEntityService(this);
    private GlobalStatisticsService _globalStatisticsService = new GlobalStatisticsService(this);
    private GroupService _groupService = new GroupService(this);
    private IdentityService _identityService = new IdentityService(this);
    private MailService _mailService = new MailService(this);
    private MatchMakingService _matchMakingService = new MatchMakingService(this);
    private OneWayMatchService _oneWayMatchService = new OneWayMatchService(this);
    private PlaybackStreamService _playbackStreamService = new PlaybackStreamService(this);
    private PlayerStateService _playerStateService = new PlayerStateService(this);
    private PlayerStatisticsService _playerStatisticsService = new PlayerStatisticsService(this);
    private PlayerStatisticsEventService _playerStatisticsEventService = new PlayerStatisticsEventService(this);
    private ProductService _productService = new ProductService(this);
    private ProfanityService _profanityService = new ProfanityService(this);
    private PushNotificationService _pushNotificationService = new PushNotificationService(this);
    private RedemptionCodeService _redemptionCodeService = new RedemptionCodeService(this);
    private S3HandlingService _s3HandlingService = new S3HandlingService(this);
    private ScriptService _scriptService = new ScriptService(this);
    private SocialLeaderboardService _socialLeaderboardService = new SocialLeaderboardService(this);
    private TimeService _timeService = new TimeService(this);
    private TournamentService _tournamentService = new TournamentService(this);

    private static BrainCloudClient instance = null;

    private static String DEFAULT_SERVER_URL = "https://sharedprod.braincloudservers.com/dispatcherv2";

    public BrainCloudClient() {
        _restClient = new BrainCloudRestClient(this);
    }

    /**
     * @deprecated Use of the *singleton* has been deprecated. We recommend that you create your own *variable* to hold an instance of the brainCloudWrapper. Explanation here: http://getbraincloud.com/apidocs/wrappers-clients-and-inconvenient-singletons/
     */
    public static BrainCloudClient getInstance() {

        if (BrainCloudClient.EnableSingletonMode == false) {
            throw new AssertionError(BrainCloudClient.SingletonUseErrorMessage);
        }

        if (instance == null) {
            instance = new BrainCloudClient();
        }
        return instance;
    }

    public static void setInstance(BrainCloudClient client) {
        instance = client;
    }

    public BrainCloudRestClient getRestClient() {
        return _restClient;
    }

    /**
     * Initializes the brainCloud client with your app information. This method
     * must be called before any API method is invoked.
     *
     * @param appId
     *            The app id
     * @param secretKey
     *            The app secret
     * @param appVersion
     *            The app version (e.g. "1.0.0").
     */
    public void initialize(String appId, String secretKey, String appVersion) {
        initialize(appId, secretKey, appVersion, DEFAULT_SERVER_URL);
    }

    /**
     * Initializes the brainCloud client with your app information. This method
     * must be called before any API method is invoked.
     *
     * @param appId
     *            The app id
     * @param secretKey
     *            The app secret
     * @param appVersion
     *            The app version (e.g. "1.0.0").
     * @param serverUrl
     *              The server url (e.g. "https://sharedprod.braincloudservers.com").
     */
    public void initialize(String appId, String secretKey, String appVersion, String serverUrl) {
        String error = null;
        if (isNullOrEmpty(serverUrl))
            error = "serverUrl was null or empty";
        else if (isNullOrEmpty(secretKey))
            error = "secretKey was null or empty";
        else if (isNullOrEmpty(appId))
            error = "appId was null or empty";
        else if (isNullOrEmpty(appVersion))
            error = "appVersion was null or empty";


        if (error != null) {
            System.out.println("ERROR | Failed to initialize brainCloud - " + error);
            return;
        }

        _appId = appId;
        _appVersion = appVersion;
        _releasePlatform = Platform.GooglePlayAndroid;

        Locale locale = Locale.getDefault();
        if (_countryCode == null || _countryCode.isEmpty()) _countryCode = locale.getCountry();
        if (_languageCode == null || _languageCode.isEmpty()) _languageCode = locale.getLanguage();

        TimeZone timeZone = TimeZone.getDefault();
        _timeZoneOffset = ((double) timeZone.getRawOffset()) / (1000.0 * 60.0 * 60.0);

        _restClient.initialize(
                serverUrl.endsWith("/dispatcherv2") ? serverUrl : serverUrl + "/dispatcherv2",
                appId, secretKey);
    }

    private static boolean isNullOrEmpty(String param) {
        return param == null || param.trim().length() == 0;
    }

    /**
     * Initialize - initializes the identity service with the saved
     * anonymous installation id and most recently used profile id
     *
     * @param profileId The id of the profile id that was most recently used by the app (on this device)
     * @param anonymousId  The anonymous installation id that was generated for this device
     */
    public void initializeIdentity(String profileId, String anonymousId) {
        getAuthenticationService().setProfileId(profileId);
        getAuthenticationService().setAnonymousId(anonymousId);
    }

    public void resetCommunication() {
        _restClient.resetCommunication();
    }

    /**
     * Run callbacks, to be called every so often (e.g. once per frame) from your main thread.
     */
    public void runCallbacks() {
        _restClient.runCallbacks();
    }

    /**
     * Returns whether the client is authenticated with the brainCloud server.
     * @return True if authenticated, false otherwise.
     */
    public boolean isAuthenticated() {
        return _restClient != null && _restClient.isAuthenticated();
    }

    /**
     * Returns whether the client is initialized.
     * @return True if initialized, false otherwise.
     */
    public boolean isInitialized() {
        return _restClient != null && _restClient.isInitialized();
    }

    public void enableLogging(boolean shouldEnable) {
        _restClient.enableLogging(shouldEnable);
    }

    /**
     * Sets a callback handler for any out of band event messages that come from
     * brainCloud.
     *
     * @param callback The event callback
     * The json format looks like the following:
     * {
     *   "events": [{
     *      "fromPlayerId": "178ed06a-d575-4591-8970-e23a5d35f9df",
     *      "eventId": 3967,
     *      "createdAt": 1441742105908,
     *      "gameId": "123",
     *      "toPlayerId": "178ed06a-d575-4591-8970-e23a5d35f9df",
     *      "eventType": "test",
     *      "eventData": {"testData": 117}
     *    }],
     *    ]
     *  }
     */
    public void registerEventCallback(IEventCallback callback) {
        _restClient.registerEventCallback(callback);
    }

    /**
     * Deregisters the event callback
     */
    public void deregisterEventCallback() {
        _restClient.deregisterEventCallback();
    }

    /**
     * Sets a reward handler for any api call results that return rewards.
     *
     * @param in_rewardCallback The reward callback handler.
     * @seealso The brainCloud apidocs site for more information on the return JSON
     */
    public void registerRewardCallback(IRewardCallback in_rewardCallback) {
        _restClient.registerRewardCallback(in_rewardCallback);
    }

    /**
     * Deregisters the reward callback
     */
    public void deregisterRewardCallback() {
        _restClient.deregisterRewardCallback();
    }

    /**
     * Registers a file upload callback handler to listen for status updates on uploads
     *
     * @param fileUploadCallback The file upload callback handler.
     */
    public void registerFileUploadCallback(IFileUploadCallback fileUploadCallback) {
        _restClient.registerFileUploadCallback(fileUploadCallback);
    }

    /**
     * Deregisters the file upload callback
     */
    public void deregisterFileUploadCallback() {
        _restClient.deregisterFileUploadCallback();
    }

    /**
     * Registers a callback that is invoked for all errors generated
     *
     * @param in_globalErrorCallback The global error callback handler.
     */
    public void registerGlobalErrorCallback(IGlobalErrorCallback in_globalErrorCallback) {
        _restClient.registerGlobalErrorCallback(in_globalErrorCallback);
    }

    /**
     * Deregisters the global error callback
     */
    public void deregisterGlobalErrorCallback() {
        _restClient.deregisterGlobalErrorCallback();
    }

    /**
     * Registers a callback that is invoked for network errors.
     * Note this is only called if enableNetworkErrorMessageCaching
     * has been set to true.
     *
     * @param in_networkErrorCallback The network error callback handler.
     */
    public void registerNetworkErrorCallback(INetworkErrorCallback in_networkErrorCallback) {
        _restClient.registerNetworkErrorCallback(in_networkErrorCallback);
    }

    /**
     * Deregisters the network error callback
     */
    public void deregisterNetworkErrorCallback() {
        _restClient.deregisterNetworkErrorCallback();
    }

    /**
     * Returns the list of packet timeouts.
     */
    public ArrayList<Integer> getPacketTimeouts() {
        return _restClient.getPacketTimeouts();
    }

    /**
     * Sets the packet timeouts using a list of integers that
     * represent timeout values in seconds for each packet retry. The
     * first item in the list represents the timeout for the first packet
     * attempt, the second for the second packet attempt, and so on.
     *
     * The number of entries in this array determines how many packet
     * retries will occur.
     *
     * By default, the packet timeout array is {10, 10, 10}
     *
     * Note that this method does not change the timeout for authentication
     * packets (use setAuthenticationPacketTimeout method).
     *
     * @param in_packetTimeouts An ArrayList of packet timeouts.
     */
    public void setPacketTimeouts(ArrayList<Integer> in_packetTimeouts) {
        _restClient.setPacketTimeouts(in_packetTimeouts);
    }

    /**
     * Sets the packet timeouts back to the default ie {10, 10, 10}
     */
    public void setPacketTimeoutsToDefault() {
        _restClient.setPacketTimeoutsToDefault();
    }

    /**
     * Gets the authentication packet timeout which is tracked separately
     * from all other packets. Note that authentication packets are never
     * retried and so this value represents the total time a client would
     * wait to receive a reply to an authentication api call. By default
     * this timeout is set to 15 seconds.
     *
     * @return The timeout in seconds
     */
    public int getAuthenticationPacketTimeout() {
        return _restClient.getAuthenticationPacketTimeout();
    }

    /**
     * Sets the authentication packet timeout which is tracked separately
     * from all other packets. Note that authentication packets are never
     * retried and so this value represents the total time a client would
     * wait to receive a reply to an authentication api call. By default
     * this timeout is set to 15 seconds.
     *
     * @param in_timeoutSecs The timeout in seconds
     */
    public void setAuthenticationPacketTimeout(int in_timeoutSecs) {
        _restClient.setAuthenticationPacketTimeout(in_timeoutSecs);
    }

    /**
     * Sets the error callback to return the status message instead of the
     * error json string. This flag is used to conform to pre-2.17 client
     * behaviour.
     *
     * @param in_enabled If set to true, enable
     */
    public void setOldStyleStatusMessageErrorCallback(boolean in_enabled) {
        _restClient.setOldStyleStatusMessageErrorCallback(in_enabled);
    }

    /**
     * Returns the low transfer rate timeout in secs
     *
     * @returns The low transfer rate timeout in secs
     */
    public int getUploadLowTransferRateTimeout() {
        return _restClient.getUploadLowTransferRateTimeout();
    }

    /**
     * Sets the timeout in seconds of a low speed upload
     * (ie transfer rate which is underneath the low transfer rate threshold).
     * By default this is set to 120 secs. Setting this value to 0 will
     * turn off the timeout.
     *
     * @param timeoutSecs The timeout in secs
     */
    public void setUploadLowTransferRateTimeout(int timeoutSecs) {
        _restClient.setUploadLowTransferRateTimeout(timeoutSecs);
    }

    /**
     * Returns the low transfer rate threshold in bytes/sec
     *
     * @returns The low transfer rate threshold in bytes/sec
     */
    public int getUploadLowTransferRateThreshold() {
        return _restClient.getUploadLowTransferRateThreshold();
    }

    /**
     * Sets the low transfer rate threshold of an upload in bytes/sec.
     * If the transfer rate dips below the given threshold longer
     * than the specified timeout, the transfer will fail.
     * By default this is set to 50 bytes/sec. Note that this setting
     * only works on platforms that use libcurl (non-windows and win32 but
     * not windows store or phone apps).
     *
     * @param bytesPerSec The low transfer rate threshold in bytes/sec
     */
    public void setUploadLowTransferRateThreshold(int bytesPerSec) {
        _restClient.setUploadLowTransferRateThreshold(bytesPerSec);
    }

    /**
     * Enables the message caching upon network error, which is disabled by default.
     * Once enabled, if a client side network error is encountered
     * (i.e. brainCloud server is unreachable presumably due to the client
     * network being down) the sdk will do the following:
     *
     * 1 - cache the currently queued messages to brainCloud
     * 2 - call the network error callback
     * 3 - then expect the app to call either:
     *     a) retryCachedMessages() to retry sending to brainCloud
     *     b) flushCachedMessages() to dump all messages in the queue.
     *
     * Between steps 2 & 3, the app can prompt the user to retry connecting
     * to brainCloud to determine whether to follow path 3a or 3b.
     *
     * Note that if path 3a is followed, and another network error is encountered,
     * the process will begin all over again from step 1.
     *
     * WARNING - the brainCloud sdk will cache *all* api calls sent
     * when a network error is encountered if this mechanism is enabled.
     * This effectively freezes all communication with brainCloud.
     * Apps must call either retryCachedMessages() or flushCachedMessages()
     * for the brainCloud SDK to resume sending messages.
     * resetCommunication() will also clear the message cache.
     *
     * @param in_enabled True if message should be cached on timeout
     */
    public void enableNetworkErrorMessageCaching(boolean in_enabled) {
        _restClient.enableNetworkErrorMessageCaching(in_enabled);
    }

    /** Attempts to resend any cached messages. If no messages are in the cache,
     * this method does nothing.
     */
    public void retryCachedMessages() {
        _restClient.retryCachedMessages();
    }

    /** Flushs the cached messages to resume api call processing. This will dump
     * all of the cached messages in the queue.
     * @param in_sendApiErrorCallbacks If set to true API error callbacks will
     * be called for every cached message with statusCode CLIENT_NETWORK_ERROR
     * and reasonCode CLIENT_NETWORK_ERROR_TIMEOUT.
     */
    public void flushCachedMessages(boolean in_sendApiErrorCallbacks) {
        _restClient.flushCachedMessages(in_sendApiErrorCallbacks);
    }


    /**
     * Inserts a marker which will tell the brainCloud comms layer
     * to close the message bundle off at this point. Any messages queued
     * before this method was called will likely be bundled together in
     * the next send to the server.
     *
     * To ensure that only a single message is sent to the server you would
     * do something like this:
     *
     * InsertEndOfMessageBundleMarker()
     * SomeApiCall()
     * InsertEndOfMessageBundleMarker()
     *
     */
    public void insertEndOfMessageBundleMarker() {
        _restClient.insertEndOfMessageBundleMarker();
    }


    public void sendRequest(ServerCall serverCall) {
        _restClient.addToQueue(serverCall);
    }

    /**
     * Returns the sessionId or empty string if no session present.
     *
     * @returns The sessionId or empty string if no session present.
     */
    public void getSessionId() {
        _restClient.getSessionId();
    }

    /**
     * @deprecated Use getAppId instead - removal after September 1 2017
     */
    @Deprecated
    public String getGameId() {
        if (_restClient == null) {
            return null;
        }
        return _restClient.getAppId();
    }

    public String getAppId() {
        if (_restClient == null) {
            return null;
        }
        return _restClient.getAppId();
    }


    public Platform getReleasePlatform() {
        return _releasePlatform;
    }

    public void setReleasePlatform(Platform _releasePlatform) {
        this._releasePlatform = _releasePlatform;
    }


    /**
     * @deprecated Use getAppVersion instead - removal after September 1 2017
     */
    @Deprecated
    public String getGameVersion() {
        return _appVersion;
    }

    /**
     * @deprecated Use getAppVersion instead - removal after September 1 2017
     */
    public String getVersion() {
        return _appVersion;
    }

    public String getAppVersion() {
        return _appVersion;
    }

    /**
     * @deprecated Use setAppVersion instead - removal after September 1 2017
     */
    public void setGameVersion(String appVersion) {
        this._appVersion = appVersion;
    }

    /**
     * @deprecated Use setAppVersion instead - removal after September 1 2017
     */
    public void setVersion(String appVersion) {
        this._appVersion = appVersion;
    }

    public void setAppVersion(String appVersion) {
        this._appVersion = appVersion;
    }

    public String getBrainCloudVersion() {
        return BRAINCLOUD_VERSION;
    }

    public String getCountryCode() {
        return _countryCode;
    }

    /**
     * Sets the country code sent to brainCloud when a user authenticates.
     * Will override any auto detected country.
     * @param countryCode ISO 3166-1 two-letter country code
     */
    public void overrideCountryCode(String countryCode) {
        _countryCode = countryCode;
    }

    public String getLanguageCode() {
        return _languageCode;
    }

    //For testing purposes
    public long getHeartbeatInterval() {
        return _restClient.getHeartbeatInterval();
    }

    public void setHeartbeatInterval(long intervalMillis) {
        _restClient.setHeartbeatInterval(intervalMillis);
    }

    /**
     * Sets the language code sent to brainCloud when a user authenticates.
     * If the language is set to a non-ISO 639-1 standard value the app default will be used instead.
     * Will override any auto detected language.
     * @param languageCode ISO 639-1 two-letter language code
     */
    public void overrideLanguageCode(String languageCode) {
        _languageCode = languageCode;
    }

    public double getTimeZoneOffset() {
        return _timeZoneOffset;
    }

    public AuthenticationService getAuthenticationService() {
        return _authenticationService;
    }

    public AsyncMatchService getAsyncMatchService() {
        return _asyncMatchService;
    }

    public DataStreamService getDataStreamService() {
        return _dataStreamService;
    }

    public EntityService getEntityService() {
        return _entityService;
    }

    public EventService getEventService() {
        return _eventService;
    }

    public FileService getFileService() {
        return _fileService;
    }

    public FriendService getFriendService() {
        return _friendService;
    }

    public GamificationService getGamificationService() {
        return _gamificationService;
    }

    public GlobalAppService getGlobalAppService() {
        return _globalAppService;
    }

    public GlobalEntityService getGlobalEntityService() {
        return _globalEntityService;
    }

    public GlobalStatisticsService getGlobalStatisticsService() {
        return _globalStatisticsService;
    }

    public GroupService getGroupService() {
        return _groupService;
    }

    public IdentityService getIdentityService() {
        return _identityService;
    }

    public MailService getMailService() {
        return _mailService;
    }

    public MatchMakingService getMatchMakingService() {
        return _matchMakingService;
    }

    public OneWayMatchService getOneWayMatchService() {
        return _oneWayMatchService;
    }

    public PlaybackStreamService getPlaybackStreamService() {
        return _playbackStreamService;
    }

    public PlayerStateService getPlayerStateService() {
        return _playerStateService;
    }

    public PlayerStatisticsService getPlayerStatisticsService() {
        return _playerStatisticsService;
    }

    public PlayerStatisticsEventService getPlayerStatisticsEventService() {
        return _playerStatisticsEventService;
    }

    public ProductService getProductService() {
        return _productService;
    }

    public ProfanityService getProfanityService() {
        return _profanityService;
    }

    public PushNotificationService getPushNotificationService() {
        return _pushNotificationService;
    }

    public RedemptionCodeService getRedemptionCodeService() {
        return _redemptionCodeService;
    }

    public S3HandlingService getS3HandlingService() {
        return _s3HandlingService;
    }

    public ScriptService getScriptService() {
        return _scriptService;
    }

    public SocialLeaderboardService getSocialLeaderboardService() {
        return _socialLeaderboardService;
    }

    public TimeService getTimeService() {
        return _timeService;
    }

    public TournamentService getTournamentService() {
        return _tournamentService;
    }
}
