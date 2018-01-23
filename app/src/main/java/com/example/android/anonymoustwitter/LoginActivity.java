package com.example.android.anonymoustwitter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class LoginActivity extends AppCompatActivity {

    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private Button signinButton, registerButton, signUpButton, cancelButton;
    private ImageView dpButton;
    private SignInButton signInGoogleButton;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private LoginButton mloginButton;
    private EditText userEmailId, userPassword, userPasswordRegistration, getUserPasswordRecheck, userEmailNew, UserName;
    private InputMethodManager mgr, mgr1;
    private LinearLayout signInMenu, signUpMenu;
    private ProgressBar mProgressBar;
    private Uri selectedImageUri=null, downloadUrl=null;
    private FirebaseUser user;

    private final static int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER = 2;

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
        dpButton = findViewById(R.id.dpButton);

        userPasswordRegistration = findViewById(R.id.password2);
        getUserPasswordRecheck = findViewById(R.id.password1);
        userEmailNew = findViewById(R.id.userEmail);
        UserName = findViewById(R.id.userName);

        signinButton = findViewById(R.id.signInButton);
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userEmailId.getText().toString().isEmpty() && !userPassword.getText().toString().isEmpty()) {
                    Log.i("login started", "standpoint l84");
                    signIn(userEmailId.getText().toString(), userPassword.getText().toString());
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(userEmailNew.getText().toString(), userPasswordRegistration.getText().toString(), getUserPasswordRecheck.getText().toString());

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
//                   startActivity(intent);
                    Toast.makeText(LoginActivity.this, "logged in", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.i("auth state null", "standpoint L79");
                }
            }
        };
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();

        mloginButton = findViewById(R.id.login_button);
        mloginButton.setReadPermissions("email", "public_profile");
        mloginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("got that", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("cancelled!!", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.i("error!!", "facebook:onError", error);
            }
        });

        signInGoogleButton = findViewById(R.id.signInGoogle);
        signInGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        dpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Fire an intent to show an image picker
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpej");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Configure Google Sign In
    }

    private void createAccount(String email, String password, String recheck) {
        Log.i(email + " " + password, "standpoint L174");

        if (!validateForm()) {
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            FirebaseUser user = MainActivity.firebaseAuth.getCurrentUser();
                            signInMenu.setVisibility(View.VISIBLE);
                            signUpMenu.setVisibility(View.INVISIBLE);
                            // Sign in success, update UI with the signed-in user's information
                            user = mAuth.getCurrentUser();
                            if (selectedImageUri != null) {
                                if (downloadUrl != null) {
                                    Log.i(downloadUrl.toString(), "standpoint L255");
                                    Log.i("to signup", "standpoint L255");
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(UserName.getText().toString())
                                            .setPhotoUri(downloadUrl)
                                            .build();
                                    Log.i(selectedImageUri.toString(), "standpoint L246");
                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("standpoint L251", "User profile successfully updated.");
                                                        selectedImageUri=null;
                                                        downloadUrl=null;
                                                    }
                                                }
                                            });
                                }else {
                                    Log.i("to signup", "standpoint L266");
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(UserName.getText().toString())
                                            .build();
                                    Log.i(selectedImageUri.toString(), "standpoint L246");
                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("standpoint L251", "User profile successfully updated.");
                                                        selectedImageUri=null;
                                                        downloadUrl=null;
                                                    }
                                                }
                                            });
                                }
                            } else {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(UserName.getText().toString())
                                        .build();
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("standpoint L264", "User profile successfully updated.");
                                                    selectedImageUri=null;
                                                    downloadUrl=null;
                                                }
                                            }
                                        });
                            }
                            Log.i(user.getDisplayName(), "standpoint L188");
                            mProgressBar.setVisibility(View.INVISIBLE);
                        } else {
                            signInMenu.setVisibility(View.INVISIBLE);
                            signUpMenu.setVisibility(View.VISIBLE);
                            // If sign in fails, display a message to the user.
                            Log.i("crteUserWithEmail:fail", "standpoint L193");
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }

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

        String newUserName = UserName.getText().toString();

        if (TextUtils.isEmpty(newUserName)) {
            UserName.setError("Required");
            valid = false;
        } else {
            UserName.setError(null);
        }

        if (!password.equals(rePassword)) {
            Toast.makeText(LoginActivity.this, "Passwords doesn't match", Toast.LENGTH_SHORT).show();
            valid = false;

        }
        return valid;

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("signInWithEmail:success", "standpoint L240");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.i(user.getDisplayName(), "standpoint L310");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("standpoint L245", task.getException().toString());
                            Toast.makeText(LoginActivity.this, "Login Id or Password is incorrect",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.i("", "Google sign in failed", e);
                // ...
            }
        } else if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            Log.i(selectedImageUri.toString(), "standpoint m302");
            dpButton.setImageURI(selectedImageUri);
            if (selectedImageUri != null) {
                signUpButton.setActivated(false);
                StorageReference photoREf = MainActivity.mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());
//                              take last part of uri location link and make child of mChatPhotosStorageReference
                photoREf.putFile(selectedImageUri).addOnSuccessListener(LoginActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    //                    upload file to firebase onsucess of upload
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downloadUrl = taskSnapshot.getDownloadUrl();//url of uploaded image
                        Log.i("success at profile up", "standpoint L255");

                        signUpButton.setActivated(true);
                    }
                });
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.i("standpoint L281", "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("signInWithCrential:suce", "standpoint L290");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mProgressBar.setVisibility(View.INVISIBLE);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("standpoint L295", "signInWithCredential:failure", task.getException());
                            mProgressBar.setVisibility(View.INVISIBLE);

                        }

                    }
                });
    }
//        LoginButton loginButton = findViewById(R.id.login_button);
//        loginButton.setReadPermissions("email", "public_profile");
//        loginButton.registerCallback(


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

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mloginButton.setEnabled(true);
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("signInWthCredntialscess", "standpoint L350");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(), com.example.android.anonymoustwitter.ProfileActivity.class);

                            intent.putExtra("result", 1);
                            setResult(Activity.RESULT_OK, intent);
                            Toast.makeText(LoginActivity.this, "logged in", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            mloginButton.setEnabled(true);
                            // If sign in fails, display a message to the user.
                            Log.i("signInWithCredentl:fail", "standpoint L363");
                            Toast.makeText(LoginActivity.this, "Please use your google acount to signin", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
