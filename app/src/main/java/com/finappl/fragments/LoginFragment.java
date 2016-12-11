package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.finappl.R;
import com.finappl.activities.CalendarActivity;
import com.finappl.adapters.LoginViewPagerAdapter;
import com.finappl.customComponents.CustomLoginViewPager;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.finappl.utils.Constants.OK;
import static com.finappl.utils.Constants.UN_IDENTIFIED_OBJECT_TYPE;


/**
 * Created by ajit on 21/3/16.
 */
public class LoginFragment extends DialogFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    /*Components*/
    @InjectView(R.id.loginRLId)
    RelativeLayout loginRL;

    @InjectView(R.id.loginVPId)
    CustomLoginViewPager loginVP;

    @InjectView(R.id.loginSignUpWithTVId)
    TextView loginSignUpWithTV;

    @InjectView(R.id.loginSignUpTVId)
    TextView loginSignUpTV;
    /*Components*/

    private AuthorizationDbService authorizationDbService;
    private Dialog dialog;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //progress bar
    public ProgressDialog mProgressDialog;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    private static final int GOOGLE_SIGN_IN = 2548;
    private static final int FB_SIGN_IN = 8452;

    private CallbackManager callbackManager;

    private LoginButton loginButton;

    @OnClick(R.id.loginSignUpUsingGoogleTVId)
    public void loginWithGoogle() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.OAUTH_TOKEN))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage((CalendarActivity)getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        FinappleUtility.showSnacks(loginRL, "Connection Failed", OK, Snackbar.LENGTH_INDEFINITE);
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                FinappleUtility.showSnacks(loginRL, "Could not Login", OK, Snackbar.LENGTH_INDEFINITE);
            }
        }
        else{
            // Pass the activity result back to the Facebook SDK
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleFacebookAccessToken(AccessToken token, final UserMO user) {
        Log.d(CLASS_NAME, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(CLASS_NAME, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            onLoginFailed(task, user);
                            return;
                        }
                    }
                });
    }

    public void showSnacks(String messageStr, final String doWhatStr, int duration){
        Snackbar snackbar = Snackbar.make(loginRL, messageStr, duration).setAction(doWhatStr, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //OK
                if(OK.equalsIgnoreCase(doWhatStr)){

                }
                else{
                    Log.e(CLASS_NAME, "Could not identify the action of the snacks");
                }
            }
        });

        snackbar.show();
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(CLASS_NAME, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(CLASS_NAME, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(CLASS_NAME, "signInWithCredential", task.getException());
                            FinappleUtility.showSnacks(loginRL, "Login Failed", OK, Snackbar.LENGTH_INDEFINITE);
                        }
                    }
                });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(mContext);
        callbackManager = CallbackManager.Factory.create();

        View view = inflater.inflate(R.layout.login_signup, container);
        ButterKnife.inject(this, view);

        loginButton = (LoginButton) view.findViewById(R.id.loginSignUpUsingFacebookButtonId);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                try {
                                    // Application code
                                    String email = object.getString("email");
                                    String name = object.getString("name");

                                    UserMO user = new UserMO();
                                    user.setEMAIL(email);
                                    user.setNAME(name);

                                    handleFacebookAccessToken(loginResult.getAccessToken(), user);
                                }
                                catch (JSONException e){
                                    Log.e(CLASS_NAME, "Exception while fetching facebook user details : "+e);
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.i(CLASS_NAME, "");
            }

            @Override
            public void onError(FacebookException e) {
                Log.i(CLASS_NAME, "");
            }
        });

        dialog = getDialog();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setupPage();

        return view;
    }

    private void initFirebase(){
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    UserMO userObject = new UserMO();
                    userObject.setUSER_ID(user.getUid());
                    userObject.setNAME(user.getDisplayName());
                    userObject.setEMAIL(user.getEmail());

                    onLoginSuccess(userObject);
                } else {
                    FinappleUtility.showSnacks(loginRL, "Please Login", OK, Snackbar.LENGTH_SHORT);
                }
            }
        };
    }

    private void setupPage() {
        List<Integer> viewPagerTabsList = new ArrayList<>();
        viewPagerTabsList.add(R.layout.login);
        viewPagerTabsList.add(R.layout.sign_up);

        LoginViewPagerAdapter viewPagerAdapter = new LoginViewPagerAdapter(mContext, viewPagerTabsList, new LoginFragment.OnClickListener() {
            @Override
            public void onClickListener(Object object, String loginOrSignUp) {
                if(object instanceof UserMO){
                    if("LOGIN".equalsIgnoreCase(loginOrSignUp)){
                        login((UserMO) object);
                    }
                    else if("SIGNUP".equalsIgnoreCase(loginOrSignUp)){
                        signup((UserMO) object);
                    }
                }
                else{
                    Log.e(CLASS_NAME, UN_IDENTIFIED_OBJECT_TYPE+object.getClass());
                }
            }
        });

        int activePageIndex = 0;
        if(loginVP != null && loginVP.getAdapter() != null){
            activePageIndex = loginVP.getCurrentItem();
        }

        loginVP.setAdapter(viewPagerAdapter);
        loginVP.setCurrentItem(activePageIndex);
        loginVP.setPagingEnabled(false);

        loginVP.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch(position){
                    case 0 : loginSignUpTV.setText("SIGN UP"); loginSignUpWithTV.setText("OR SIGN IN WITH"); break;
                    case 1 : loginSignUpTV.setText("SIGN IN"); loginSignUpWithTV.setText("OR SIGN UP WITH"); break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //setUploginWithFacebook();
    }

    @OnClick(R.id.loginSignUpTVId)
    public void showSignUp(){
        if("SIGN UP".equalsIgnoreCase(String.valueOf(loginSignUpTV.getText()))){
            loginVP.setCurrentItem(1);
        }
        else if("SIGN IN".equalsIgnoreCase(String.valueOf(loginSignUpTV.getText()))){
            loginVP.setCurrentItem(0);
        }
    }


    private void login(final UserMO user) {
        showProgressDialog();

        mProgressDialog.setTitle("Signing into "+R.string.app_name);
        mProgressDialog.setMessage("Checking Credentials ..");

        mAuth.signInWithEmailAndPassword(user.getEMAIL(), user.getPASS())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(CLASS_NAME, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            onLoginFailed(task, user);
                            return;
                        }

                        user.setUSER_ID(mAuth.getCurrentUser().getUid());

                        onLoginSuccess(user);
                    }
                });
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void signup(final UserMO user) {
        showProgressDialog();

        mProgressDialog.setMessage("Registering "+user.getEMAIL()+" into "+getResources().getString(R.string.app_name));

        mAuth.createUserWithEmailAndPassword(user.getEMAIL(), user.getPASS())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.i(CLASS_NAME, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            onSignUpFailed(task, user);
                            return;
                        }

                        user.setUSER_ID(mAuth.getCurrentUser().getUid());

                        onSignUpSuccess(user);
                    }
                });
    }

    public void onSignUpSuccess(final UserMO user) {
        mProgressDialog.setMessage("Registration successful. Sending verification mail.");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Log.e(CLASS_NAME, "Verification email could not be sent");
                            showToast("Verification email could not be sent");
                            return;
                        }

                        mProgressDialog.setMessage("Verification mail has been sent to : "+user.getEMAIL());

                        //Add the user to the local database with default values
                        authorizationDbService.addNewUser(user);

                        mProgressDialog.setMessage("All done !");

                        ((CalendarActivity)getActivity()).initActivity();

                        hideProgressDialog();
                        dismiss();
                    }
                });

    }

    public void onLoginSuccess(UserMO user) {
        //Add the user to the local database with default values
        authorizationDbService.addNewUser(user);

        ((CalendarActivity)getActivity()).initActivity();

        hideProgressDialog();
        dismiss();

        String nameStr = user.getNAME();
        if(nameStr == null){
            nameStr = "";
        }

        ((CalendarActivity)getActivity()).showSnacks("Hi "+nameStr+" !", "", Snackbar.LENGTH_SHORT);
    }

    public void onSignUpFailed(Task<AuthResult> task, UserMO user) {
        hideProgressDialog();

        try {
            throw task.getException();
        }
        catch (FirebaseNetworkException e){
            showToast("Could not connect. Check your internet !");
        }
        catch(FirebaseAuthWeakPasswordException e) {
            showToast("The password is too weak !");
        }
        catch(FirebaseAuthUserCollisionException e) {
            showToast(user.getEMAIL()+" is already registered. Try logging in !");
        }
        catch(Exception e) {
            showToast(e.getMessage());
        }
    }

    public void onLoginFailed(Task<AuthResult> task, UserMO user) {
        hideProgressDialog();

        try {
            throw task.getException();
        }
        catch (FirebaseNetworkException e){
            FinappleUtility.showSnacks(loginRL, "Could not connect. Check your internet !", OK, Snackbar.LENGTH_INDEFINITE);
        }
        catch(FirebaseAuthInvalidUserException e) {
            FinappleUtility.showSnacks(loginRL, user.getEMAIL()+" is not registered. Try Signing up !", OK, Snackbar.LENGTH_INDEFINITE);

            ((LoginViewPagerAdapter)loginVP.getAdapter()).setSignupEmail(user.getEMAIL());
            loginVP.setCurrentItem(1);
        }
        catch(FirebaseAuthInvalidCredentialsException e) {
            FinappleUtility.showSnacks(loginRL, "Invalid Credentials !", OK, Snackbar.LENGTH_LONG);
        }
        catch(FirebaseAuthUserCollisionException e) {
            FinappleUtility.showSnacks(loginRL, user.getEMAIL()+" is already registered from a different provider", OK, Snackbar.LENGTH_LONG);
        }
        catch(Exception e) {
            Log.e(CLASS_NAME, task.getException()+" : this exception has been not handled. Recommending to handle it.");
            FinappleUtility.showSnacks(loginRL, e.getMessage(), OK, Snackbar.LENGTH_INDEFINITE);
        }
    }

    protected void showToast(String string){
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
    }

    // Empty constructor required for DialogFragment
    public LoginFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();

        initDb();
        initFirebase();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            d.getWindow().setLayout(width, height);
            d.setCancelable(false);
        }

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        closeAuthenticators();
    }

    private void closeAuthenticators(){
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

        if(mGoogleApiClient != null){
            mGoogleApiClient.stopAutoManage((CalendarActivity)getActivity());
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(getActivity(), "User is connected!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        showToast(connectionResult.getErrorMessage());
    }

    //abstracts
    public interface OnClickListener {
        void onClickListener(Object object, String loginOrSignup);
    }

    private void initDb() {
        authorizationDbService = new AuthorizationDbService(mContext);
    }
}
