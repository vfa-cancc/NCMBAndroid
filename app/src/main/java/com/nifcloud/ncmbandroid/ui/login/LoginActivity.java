package com.nifcloud.ncmbandroid.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nifcloud.mbaas.core.DoneCallback;
import com.nifcloud.mbaas.core.LoginCallback;
import com.nifcloud.mbaas.core.NCMBException;
import com.nifcloud.mbaas.core.NCMBUser;
import com.nifcloud.ncmbandroid.R;
import com.nifcloud.ncmbandroid.common.Utils;

public class LoginActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final Button signupButton = findViewById(R.id.signup);
        final Button autoLogin = findViewById(R.id.autoLogin);
        final Button socialLogin = findViewById(R.id.socialLogin);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                loginButton.setEnabled(usernameEditText.getText().length() > 0 && passwordEditText.getText().length() > 0);
                signupButton.setEnabled(usernameEditText.getText().length() > 0 && passwordEditText.getText().length() > 0);
                return false;
            }
        });

        final Context context = this;

        socialLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LoginActivity.this, SocialLoginActivity.class);
                startActivity(myIntent);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (userName.length() > 0) {
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    try {
                        if (Utils.isEmailFormat(userName)) {
                            NCMBUser.loginWithMailAddressInBackground(userName, password, new LoginCallback() {
                                @Override
                                public void done(NCMBUser ncmbUser, NCMBException e) {
                                    loadingProgressBar.setVisibility(View.GONE);
                                    if (e == null) {
                                        finish();
                                    } else {
                                        Utils.createErrorDialog(context, "", e.getMessage()).show();
                                    }
                                }
                            });

                        } else {
                            NCMBUser.loginInBackground(userName, password, new LoginCallback() {
                                @Override
                                public void done(NCMBUser ncmbUser, NCMBException e) {
                                    loadingProgressBar.setVisibility(View.GONE);
                                    if (e == null) {
                                        finish();
                                    } else {
                                        Utils.createErrorDialog(context, "", e.getMessage()).show();
                                    }
                                }
                            });
                        }
                    } catch (NCMBException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (userName.length() > 0) {
                    loadingProgressBar.setVisibility(View.VISIBLE);

                    if (Utils.isEmailFormat(userName)) {
                        NCMBUser.requestAuthenticationMailInBackground(userName, new DoneCallback() {
                            @Override
                            public void done(NCMBException e) {
                                loadingProgressBar.setVisibility(View.GONE);
                                if (e != null) {
                                    Utils.createErrorDialog(context, "", e.getMessage()).show();
                                } else {
                                    Utils.createErrorDialog(context, "", "Sign up success! Please confirm from your mailbox").show();
                                }
                            }
                        });
                    } else {
                        NCMBUser user = new NCMBUser();
                        user.setUserName(userName);
                        user.setPassword(password);
                        user.signUpInBackground(new DoneCallback() {
                            @Override
                            public void done(NCMBException e) {
                                loadingProgressBar.setVisibility(View.GONE);
                                if (e != null) {
                                    Utils.createErrorDialog(context, "", e.getMessage()).show();
                                } else {
                                    Utils.createErrorDialog(context, "", "Signup success! Please Signin to continue!").show();
                                }
                            }
                        });
                    }

                }
            }
        });

        autoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                NCMBUser.loginWithAnonymousInBackground(new LoginCallback() {
                    @Override
                    public void done(NCMBUser ncmbUser, NCMBException e) {
                        loadingProgressBar.setVisibility(View.GONE);
                        if (e == null) {
                            finish();
                        } else {
                            Utils.createErrorDialog(context, "", e.getMessage()).show();
                        }
                    }
                });
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}