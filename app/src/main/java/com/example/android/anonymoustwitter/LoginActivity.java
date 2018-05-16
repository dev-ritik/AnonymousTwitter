package com.example.android.anonymoustwitter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button signinButton, registerButton, signUpButton, cancelButton;
    private SignInButton signInGoogleButton;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private LoginButton mloginButton;
    private EditText userEmailId, userPassword, userPasswordRegistration, getUserPasswordRecheck, userEmailNew;
    private InputMethodManager mgr, mgr1;
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
                        Log.i("signInWithEmail:success", "standpoint L240");
                    }

                    @Override
                    public void resultError(Exception errorResult) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Log.i("standpoint L245", errorResult.toString());
                        Toast.makeText(LoginActivity.this, "Login Id or Password is incorrect",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void wrongCrudentials(String errorMessage) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, "Invalid input Id or password", Toast.LENGTH_SHORT).show();
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
                    public void resultError(Exception errorResult) {
                        signInMenu.setVisibility(View.INVISIBLE);
                        signUpMenu.setVisibility(View.VISIBLE);
                        // If sign in fails, display a message to the user.
                        Log.i("crteUserWithEmail:fail", "standpoint L193");
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void resultName(FirebaseUser registeredUser) {
                    }

                    @Override
                    public void resultDp(Uri dpLink) {
                    }

                    @Override
                    public void wrongCrudentials(String errorMessage) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, "Invalid input Id or password", Toast.LENGTH_SHORT).show();
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
                Log.i("creating started", "standpoint l97");

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
            mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(userPassword.getWindowToken(), 0);

            mgr1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr1.hideSoftInputFromWindow(getUserPasswordRecheck.getWindowToken(), 0);
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
                    Log.i("auth state null", "standpoint L79");
                }
            }
        };
        // Initialize Facebook Login button

        mloginButton = findViewById(R.id.login_button);
        signInFacebook();

        signInGoogleButton = findViewById(R.id.signInGoogle);
        signInGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                signIn();
                mProgressBar.setVisibility(View.VISIBLE);

                googleLogin = new SimpleGoogleLogin(LoginActivity.this, RC_SIGN_IN_GOOGLE, getString(R.string.default_web_client_id));
                googleLogin.setOnGoogleLoginResult(new SimpleGoogleLogin.OnGoogleLoginResult() {
                    @Override
                    public void resultSuccessful(FirebaseUser registeredUser) {
                        Log.i("signInWithCrential:suce", "standpoint L290");
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void resultError(Exception errorResult) {
                        Log.i("", "Google sign in failed", errorResult);
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
                googleLogin.attemptGoogleLogin();
            }
        });

    }

    private boolean validateForm() {
        boolean valid = true;

        if (TextUtils.isEmpty(userEmailNew.getText().toString())) {
            userEmailNew.setError("Required");
            valid = false;
        } else {
            userEmailId.setError(null);
        }

        String password = userPasswordRegistration.getText().toString();
        if (TextUtils.isEmpty(password)) {
            userPasswordRegistration.setError("Required");
            valid = false;
        } else {
            userPassword.setError(null);
        }
        String rePassword = getUserPasswordRecheck.getText().toString();

        if (TextUtils.isEmpty(rePassword)) {
            getUserPasswordRecheck.setError("Required");
            valid = false;
        } else {
            userEmailId.setError(null);
        }

        if (!password.equals(rePassword)) {
            Toast.makeText(LoginActivity.this, "Passwords doesn't match", Toast.LENGTH_SHORT).show();
            valid = false;

        }
        return valid;

    }

    private void signInFacebook() {

        facebookLogin = new SimpleFacebookLogin(this);
        facebookLogin.setOnFacebookLoginResult(new SimpleFacebookLogin.OnFacebookLoginResult() {
            @Override
            public void resultFacebookLoggedIn() {
                mProgressBar.setVisibility(View.VISIBLE);
                Log.i("got that", "facebook:onSuccess:");
            }

            @Override
            public void resultActualLoggedIn(FirebaseUser registeredUser) {
                mloginButton.setEnabled(true);
                // Sign in success, update UI with the signed-in user's information
                Log.i("signInWthCredntialscess", "standpoint L350");
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
                Log.d("cancelled!!", "facebook:onCancel");
                mloginButton.setEnabled(true);
            }

            @Override
            public void resultError(Exception errorResult) {
                mProgressBar.setVisibility(View.INVISIBLE);
                Log.i("error!!", "facebook:onError", errorResult);
                mloginButton.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Please use your google account to signin", Toast.LENGTH_SHORT).show();
            }
        });
        Log.i("point la437", facebookLogin.toString());
        Log.i("point la438", mloginButton.toString());
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
