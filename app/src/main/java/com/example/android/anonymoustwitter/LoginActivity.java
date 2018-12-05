package com.example.android.anonymoustwitter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.loginlibrary.SimpleEmailLogin;
import com.example.android.loginlibrary.SimpleFacebookLogin;
import com.example.android.loginlibrary.SimpleGoogleLogin;
import com.example.android.loginlibrary.SimpleRegistration;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button signinButton, registerButton, signUpButton, cancelButton;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private LoginButton mloginButton;
    private EditText userEmailId, userPassword, userPasswordRegistration, getUserPasswordRecheck, userEmailNew;
    private LinearLayout signInMenu, signUpMenu;
    private ProgressBar mProgressBar;
    private SimpleGoogleLogin googleLogin;
    private SimpleFacebookLogin facebookLogin;

    private final static int RC_SIGN_IN_GOOGLE = 1;


    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthListener);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgressBar = findViewById(R.id.progressBar);

        signInMenu = findViewById(R.id.loginScreen);
        signUpMenu = findViewById(R.id.registerScreen);

        signUpButton = findViewById(R.id.signUpDo);
        cancelButton = findViewById(R.id.cancel);

        userPasswordRegistration = findViewById(R.id.password2);
        getUserPasswordRecheck = findViewById(R.id.password1);
        userEmailNew = findViewById(R.id.userName);

        signinButton = findViewById(R.id.signInButton);
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);

                SimpleEmailLogin login = new SimpleEmailLogin();
                login.setOnEmailLoginResult(new SimpleEmailLogin.OnEmailLoginResult() {
                    @Override
                    public void resultSuccessful(FirebaseUser registeredUser) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Log.i("point la86", "resultSuccessful: ");
                    }

                    @Override
                    public void noAccountFound(Exception e) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, "no account found", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void invalidCredentials(Exception e) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, "wrong password or this is a google or facebook logged in account", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void resultError(Exception errorResult) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        errorResult.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Login Id or Password is incorrect",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void networkError(Exception e) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, "network error occurred", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void wrongCredentials(String s, String s1) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, s + s1, Toast.LENGTH_SHORT).show();
                    }

                });
                login.attemptLogin(LoginActivity.this, userEmailId.getText().toString(), userPassword.getText().toString());
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                SimpleRegistration register = new SimpleRegistration();
                register.setOnRegistrationResult(new SimpleRegistration.OnRegistrationResult() {
                    @Override
                    public void resultSuccessful(FirebaseUser registeredUser) {
                        signInMenu.setVisibility(View.VISIBLE);
                        signUpMenu.setVisibility(View.INVISIBLE);
                        // Sign in success, update UI with the signed-in user's information
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void sameEmailError(Exception e) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, "Account exists with same email Id", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void networkError(Exception e) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, "network error occurred", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void resultError(Exception errorResult) {
                        signInMenu.setVisibility(View.INVISIBLE);
                        signUpMenu.setVisibility(View.VISIBLE);
                        // If sign in fails, display a message to the user.
                        Log.i("point la157", "resultError: ");
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void profileUpdateError(Exception e) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, "some error occurred while updating username and/or profile pic", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void resultName(FirebaseUser registeredUser) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, "name updated(user already registered", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void resultDp(Uri dpLink) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, "DP link updated(user already registered", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void wrongCredentials(String s, String s1) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, s + s1, Toast.LENGTH_SHORT).show();
                    }

                });
                register.attemptRegistration(LoginActivity.this, userEmailNew.getText().toString(), userPasswordRegistration.getText().toString(), getUserPasswordRecheck.getText().toString(), null, null);
            }
        });
        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInMenu.setVisibility(View.INVISIBLE);
                signUpMenu.setVisibility(View.VISIBLE);
                userEmailNew.setText(userEmailId.getText());

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInMenu.setVisibility(View.VISIBLE);
                signUpMenu.setVisibility(View.INVISIBLE);
            }
        });

        userEmailId = findViewById(R.id.emailId);
        userPassword = findViewById(R.id.password);

        if (!userPassword.getText().toString().isEmpty()) {
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(userPassword.getWindowToken(), 0);

            mgr.hideSoftInputFromWindow(getUserPasswordRecheck.getWindowToken(), 0);
        }

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    //pass intent ....
                    Intent intent = new Intent(getApplicationContext(), com.example.android.anonymoustwitter.ProfileActivity.class);

                    intent.putExtra("result", 1);
                    setResult(Activity.RESULT_OK, intent);
                    Toast.makeText(LoginActivity.this, "logged in", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.i("point L79","auth state null");
                }
            }
        };
        // Initialize Facebook Login button

        mloginButton = findViewById(R.id.login_button);
        signInFacebook();

        SignInButton signInGoogleButton = findViewById(R.id.signInGoogle);
        signInGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                signIn();
                mProgressBar.setVisibility(View.VISIBLE);

                googleLogin = new SimpleGoogleLogin(LoginActivity.this, RC_SIGN_IN_GOOGLE, getString(R.string.default_web_client_id));
                googleLogin.setOnGoogleLoginResult(new SimpleGoogleLogin.OnGoogleLoginResult() {
                    @Override
                    public void resultSuccessful(FirebaseUser registeredUser) {
                        Log.i("point 254", "resultSuccessful: ");
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void signinCancelledByUser(Exception e) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, "sign-in cancelled by user", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void accountCollisionError(Exception e) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, "account exists with same email Id", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void networkError(Exception e) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, "network error occurred", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void resultError(Exception errorResult) {
                        errorResult.printStackTrace();
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
                googleLogin.attemptGoogleLogin();
            }
        });

    }

    private void signInFacebook() {

        facebookLogin = new SimpleFacebookLogin(this);
        facebookLogin.setOnFacebookLoginResult(new SimpleFacebookLogin.OnFacebookLoginResult() {
            @Override
            public void resultFacebookLoggedIn(LoginResult loginResult) {
                mProgressBar.setVisibility(View.VISIBLE);
                Log.i("point 305", "resultFacebookLoggedIn: Facebook login successful, yet to authenticate Firebase");
            }

            @Override
            public void resultActualLoggedIn(FirebaseUser registeredUser) {
                mloginButton.setEnabled(true);
                // Sign in success, update UI with the signed-in user's information
                Log.i("point 302", "resultActualLoggedIn: ");
                mProgressBar.setVisibility(View.INVISIBLE);

                Intent intent = new Intent(getApplicationContext(), com.example.android.anonymoustwitter.ProfileActivity.class);

                intent.putExtra("result", 1);
                setResult(Activity.RESULT_OK, intent);
                Toast.makeText(LoginActivity.this, "logged in", Toast.LENGTH_SHORT).show();
                finish();
                // If sign in fails, display a message to the user.
            }

            @Override
            public void resultCancel() {
                mProgressBar.setVisibility(View.INVISIBLE);
                Log.i("point 316", "facebook:onCancel");
                mloginButton.setEnabled(true);
            }

            @Override
            public void accountCollisionError(Exception e) {
                mProgressBar.setVisibility(View.VISIBLE);
                Toast.makeText(LoginActivity.this, "account already exists with the different sign-in credentials", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void networkError(Exception e) {
                mProgressBar.setVisibility(View.VISIBLE);
                Toast.makeText(LoginActivity.this, "network error occurred", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void resultError(Exception errorResult) {
                mProgressBar.setVisibility(View.INVISIBLE);
                Log.i("point 335", "facebook:onError", errorResult);
                mloginButton.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Please use your google account to signin", Toast.LENGTH_SHORT).show();
            }
        });
        facebookLogin.attemptFacebookLogin(mloginButton);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            googleLogin.onActivityResult(requestCode, resultCode, data);
        }
        if (!(facebookLogin == null))
            facebookLogin.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        // Check if user is signed in (non-null) and update UI accordingly.
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);
    }

}
