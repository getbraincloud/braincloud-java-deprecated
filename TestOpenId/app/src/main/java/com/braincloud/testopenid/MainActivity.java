package com.braincloud.testopenid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bitheads.braincloud.client.BrainCloudClient;
import com.bitheads.braincloud.client.BrainCloudWrapper;
import com.bitheads.braincloud.client.IServerCallback;
import com.bitheads.braincloud.client.ServiceName;
import com.bitheads.braincloud.client.ServiceOperation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements IServerCallback {

    private static final String TAG = "TestOpenId";

    private BrainCloudWrapper wrapper;
    private GoogleSignInClient googleSignInClient;
    private SignInButton googleSignInButton;
    private Button signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleSignInButton = findViewById(R.id.sign_in_button);
        signOut=findViewById(R.id.sign_out);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("491644407231-dvll11v5ucs48et0cvbebegh7bpl7l4m.apps.googleusercontent.com")
                .requestEmail()
                .requestId()
                .requestProfile()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        wrapper = new BrainCloudWrapper();
        BrainCloudClient client = wrapper.getClient();
        client.enableLogging(true);
        String appId = "22978";
        String secret = "****************************";
        String appVersion = "1.0.0";
        client.initialize("https://internal.braincloudservers.com/dispatcherv2", appId, secret, appVersion);

        new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
//                Log.i("onStart", "tttt---before-onTick");
                wrapper.runCallbacks();
//                Log.i("onStart", "tttt---after-onTick");
            }
            public void onFinish() {
                start(); // just restart the timer
            }
        }.start();

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          /*
          Sign-out is initiated by simply calling the googleSignInClient.signOut API. We add a
          listener which will be invoked once the sign out is the successful
           */
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loggedOut();
                    }
                });
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount alreadyloggedAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (alreadyloggedAccount != null) {
            Toast.makeText(this, "Already Logged In", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Not Logged In", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Not logged in");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 101:
                    try {
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.
                        Task<GoogleSignInAccount> task2 = GoogleSignIn.getSignedInAccountFromIntent(data);
                        GoogleSignInAccount account = task2.getResult(ApiException.class);
                        onLoggedIn(account);
                    } catch (ApiException e) {
                        // The ApiException status code indicates the detailed failure reason.
                        Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                    }
                    break;
            }
        }
    }

    private void onLoggedIn(GoogleSignInAccount googleSignInAccount) {
        Log.d(TAG, "OpenId Token: " + googleSignInAccount.getIdToken());
        Toast.makeText(this, "Attempting brainCloud login", Toast.LENGTH_LONG).show();
        BrainCloudClient client = wrapper.getClient();
        client.getAuthenticationService().authenticateGoogleOpenId(googleSignInAccount.getId(),
                googleSignInAccount.getIdToken(), true, this);
    }

    private void loggedOut() {
        Toast.makeText(this, "Logged out", Toast.LENGTH_LONG).show();
    }

    @Override
    public void serverCallback(ServiceName serviceName, ServiceOperation serviceOperation, JSONObject jsonData) {
        Toast.makeText(this, "brainCloud login success", Toast.LENGTH_LONG).show();
    }

    @Override
    public void serverError(ServiceName serviceName, ServiceOperation serviceOperation, int statusCode, int reasonCode, String jsonError) {
        Toast.makeText(this, "brainCloud login failure", Toast.LENGTH_LONG).show();
    }

}
