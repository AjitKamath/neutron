package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.activities.CalendarActivity;
import com.finappl.adapters.LoginViewPagerAdapter;
import com.finappl.customComponents.CustomLoginViewPager;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.models.UserMO;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.finappl.utils.Constants.UN_IDENTIFIED_OBJECT_TYPE;

/**
 * Created by ajit on 21/3/16.
 */
public class LoginFragment extends DialogFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    /*Components*/
    @InjectView(R.id.loginVPId)
    CustomLoginViewPager loginVP;

    @InjectView(R.id.loginSignUpTVId)
    TextView loginSignUpTV;

    @InjectView(R.id.loginSignUpUsingTVId)
    TextView loginSignUpUsingTV;

    private AuthorizationDbService authorizationDbService;
    private Dialog dialog;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //progress bar
    public ProgressDialog mProgressDialog;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    private static final int RC_SIGN_IN = 9001;

    @OnClick(R.id.loginSignUpUsingGoogleTVId)
    public void loginWithGoogle(){
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
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
                            showToast("Authentication failed.");
                        }
                        // ...
                    }
                });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_signup, container);
        ButterKnife.inject(this, view);

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
                    // User is signed in
                    Log.i(CLASS_NAME, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(CLASS_NAME, "onAuthStateChanged:signed_out");
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
                    case 0 : loginSignUpTV.setText("SIGN UP"); loginSignUpUsingTV.setText("OR LOGIN USING"); break;
                    case 1 : loginSignUpTV.setText("SIGN IN"); loginSignUpUsingTV.setText("OR SIGN UP USING"); break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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

        ((LoginViewPagerAdapter)loginVP.getAdapter()).setSignupEmail(user.getEMAIL());
        loginVP.setCurrentItem(1);

        try {
            throw task.getException();
        }
        catch (FirebaseNetworkException e){
            showToast("Could not connect. Check your internet !");
        }
        catch(FirebaseAuthInvalidUserException e) {
            showToast(user.getEMAIL()+" is not registered. Try Signing up !");
        }
        catch(FirebaseAuthInvalidCredentialsException e) {
            showToast("Invalid Credentials !");
        }
        catch(Exception e) {
            showToast(e.getMessage());
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
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
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
