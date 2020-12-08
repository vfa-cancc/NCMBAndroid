package com.nifcloud.ncmbandroid.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.OAuthCredential;
import com.google.firebase.auth.OAuthProvider;
import com.google.gson.Gson;
import com.nifcloud.mbaas.core.LoginCallback;
import com.nifcloud.mbaas.core.NCMBException;
import com.nifcloud.mbaas.core.NCMBGoogleParameters;
import com.nifcloud.mbaas.core.NCMBTwitterParameters;
import com.nifcloud.mbaas.core.NCMBUser;
import com.nifcloud.ncmbandroid.R;
import com.google.firebase.FirebaseApp;


import java.io.IOException;
import java.util.Arrays;

public class SocialLoginActivity extends AppCompatActivity {
    private String consumerApiKey = "Your_Twitter_ConsumerApiKey";
    private String consumerSecretKey = "Your_Twitter_ConsumerSecretKey";
    // Facebook callback
    CallbackManager callbackManager = CallbackManager.Factory.create();

    private static final String TAG = "MAIN";
    private static final String ACCOUNT_TYPE_GOOGLE = "com.google";
    private static final String AUTH_SCOPE = "oauth2:profile email";
    private static final int REQUEST_SIGN_IN = 10000;
    private int REQUEST_CODE_PICK_ACCOUNT = 111;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_social_login);
        findViewById(R.id.twitterLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twitterLogin();
            }
        });

        findViewById(R.id.googleLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGoogleToken();
            }
        });

        final LoginButton loginButton = (LoginButton) findViewById(R.id.facebookLogin);
        loginButton.setReadPermissions("email");
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                finish();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.GET_ACCOUNTS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.GET_ACCOUNTS},
                        1);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults)
    {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "ACCESS_GET_ACCOUNTS is granted!");
                } else {
                    Log.d(TAG, "ACCESS_GET_ACCOUNTS is denied!");
                }
            }
        }
    }

    private void getGoogleToken() {
        Intent intent =
                AccountPicker.newChooseAccountIntent(
                        new AccountPicker.AccountChooserOptions.Builder()
                                .setAllowableAccountsTypes(Arrays.asList("com.google"))
                                .build());

        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    protected void twitterLogin(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");


        firebaseAuth
                .startActivityForSignInWithProvider(/* activity= */ this, provider.build())
                .addOnSuccessListener(
                        new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                final Gson gson = new Gson();

                                OAuthCredential oAuthCredential = (OAuthCredential)authResult.getCredential();
                                String accessToken = oAuthCredential.getAccessToken();
                                String serect = oAuthCredential.getSecret();

                                String tokenid = authResult.getAdditionalUserInfo().getProfile().get("id").toString();
                                String providerId = authResult.getUser().getProviderId();
                                Log.d("NCMB", authResult.getUser().getProviderId());
                                String userName = authResult.getAdditionalUserInfo().getUsername();

                                NCMBTwitterParameters parameters = new NCMBTwitterParameters(
                                        tokenid,
                                        authResult.getAdditionalUserInfo().getUsername(),
                                        consumerApiKey,
                                        consumerSecretKey,
                                        accessToken,
                                        serect
                                );
                                NCMBUser.loginInBackgroundWith(parameters, new LoginCallback(){
                                    @Override
                                    public void done(NCMBUser user, NCMBException e) {
                                        if (e != null) {
                                            Log.d("NCMB", e.getMessage());
                                            Log.d("NCMB", e.getCode());
                                        } else {
                                            Log.d("NCMB", user.getObjectId());
                                            Log.d("NCMB", gson.toJson(user));
                                        }
                                    }
                                });
                                // User is signed in.
                                // IdP data available in
                                // authResult.getAdditionalUserInfo().getProfile().
                                // The OAuth access token can also be retrieved:
                                // authResult.getCredential().getAccessToken().
                                // The OAuth secret can be retrieved by calling:
                                // authResult.getCredential().getSecret().
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure.
                                Log.d("NCMB", e.getMessage());
                                Log.d("NCMB", e.toString());
                            }
                        });
    }

    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT && resultCode == RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            try {
                // Do what you need with email`
                String id = GoogleAuthUtil.getAccountId(getApplicationContext(), accountName);

                String token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, "oauth2:profile email");
                NCMBGoogleParameters googleParams = new NCMBGoogleParameters(
                        id,
                        token
                );

                NCMBUser.loginInBackgroundWith(googleParams, new LoginCallback() {
                    @Override
                    public void done(NCMBUser user, NCMBException e) {

                    }
                });
            } catch (GoogleAuthException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}