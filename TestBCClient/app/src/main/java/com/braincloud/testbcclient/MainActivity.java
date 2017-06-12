package com.braincloud.testbcclient;

import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.bitheads.braincloud.client.BrainCloudClient;
import com.bitheads.braincloud.client.IServerCallback;
import com.bitheads.braincloud.client.ServiceName;
import com.bitheads.braincloud.client.ServiceOperation;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import org.json.JSONObject;

import java.io.IOException;


public class MainActivity extends ActionBarActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        IServerCallback
{

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;
    private static final int REQ_SIGN_IN_REQUIRED = 55664;
    private static final String TAG = "RetrieveAccessToken";

    private static final String GOOGLE_ACCOUNT = "xxx@gmail.com";

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    /* A flag indicating that a PendingIntent is in progress and prevents
     * us from starting further intents.
     */
    private boolean mIntentInProgress;

    private String authToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        BrainCloudClient client = BrainCloudClient.getInstance();
        String appId = "";
        String secret = "";
        String appVersion = "1.0.0";
        client.initialize(appId, secret, appVersion);

        EditText txtUser = (EditText) findViewById(R.id.txtUser);
        EditText txtPwd = (EditText) findViewById(R.id.txtPassword);
        txtUser.setText("");
        txtPwd.setText("");
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        int i;

        if (v.getId() == R.id.sign_in_button
                && !mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }

        switch(v.getId())
        {
            case R.id.bcLogin:
                testGoogleLogin();
                break;
            case R.id.btnUniversalConnect:
                testUniversalLogin();
                break;
        }

    }

    private void testGoogleLogin()
    {
        BrainCloudClient client = BrainCloudClient.getInstance();
        client.getAuthenticationService().authenticateGoogle(GOOGLE_ACCOUNT,
                authToken, true, this);
    }

    private void testUniversalLogin()
    {
        EditText txtUser = (EditText) findViewById(R.id.txtUser);
        EditText txtPwd = (EditText) findViewById(R.id.txtPassword);

        String user = txtUser.getText().toString();
        String pwd = txtPwd.getText().toString();

        if (user.length() > 0 && pwd.length() > 0)
        {
            BrainCloudClient client = BrainCloudClient.getInstance();
            client.getAuthenticationService().authenticateUniversal(user, pwd, true, this);
        }
    }

    /* Track whether the sign-in button has been clicked so that we know to resolve
    * all issues preventing sign-in without waiting.
    */
    private boolean mSignInClicked;

    /* Store the connection result from onConnectionFailed callbacks so that we can
     * resolve them when the user clicks sign-in.
     */
    private ConnectionResult mConnectionResult;

    /* A helper method to resolve the current ConnectionResult error. */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress) {
            // Store the ConnectionResult so that we can use it later when the user clicks
            // 'sign-in'.
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    public void onConnected(Bundle connectionHint) {
        // We've resolved any connection errors.  mGoogleApiClient can be used to
        // access Google APIs on behalf of the user.
        new RetrieveTokenTask().execute(GOOGLE_ACCOUNT);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    public void onDisconnected() {

    }

    @Override
    public void serverCallback(ServiceName serviceName, ServiceOperation serviceOperation, JSONObject jsonData)
    {
        if (serviceName.equals(ServiceName.authenticationV2))
        {
            if (serviceOperation.equals(ServiceOperation.AUTHENTICATE))
            {
                onAuthenticate(jsonData);
            }
            else if (serviceOperation.equals(ServiceOperation.RESET_EMAIL_PASSWORD))
            {
                onResetEmailPassword(jsonData);
            }
        }
    }

    @Override
    public void serverError(ServiceName serviceName, ServiceOperation serviceOperation, int statusCode, int reasonCode, String statusMessage)
    {
        Log.d(TAG, "Auth error statusCode:" + statusCode + " reasonCode:" + reasonCode);
    }

    public void onAuthenticate(JSONObject response) {
        try {
            int statusCode = response.getInt("status");
            if (statusCode == 200) {
                Log.d(TAG, "Auth success:" + response.toString());
            }
            else {
                Log.e(TAG, "Auth failure:" + response.toString());
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception:", e);
        }

    }

    public void onResetEmailPassword(JSONObject response) {

    }


    private class RetrieveTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scopes = "oauth2:profile email";
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(),
                        GOOGLE_ACCOUNT,
                        "oauth2:profile email");
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (UserRecoverableAuthException e) {
                startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
            } catch (GoogleAuthException e) {
                Log.e(TAG, e.getMessage());
            }
            return token;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e(TAG, "Token:" + s);
            authToken = s;
        }
    }

}
