package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.activities.CalendarActivity;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.models.UserMO;

import static com.finappl.utils.Constants.SHARED_PREF;

/**
 * Created by ajit on 21/3/16.
 */
public class LoginFragment extends DialogFragment implements LinearLayout.OnClickListener {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    private TextView loginUsernameET, loginPasswordET;
    private LinearLayout loginLL, registerLL, register1NextLL, register2NextLL, register3NextLL;
    private TextView finapplTV;
    private ImageView discardIV;
    private LinearLayout finapplLL;
    private LinearLayout simpleLL;
    private LinearLayout register1ContentLL;

    private UserMO userObj;

    private AuthorizationDbService authorizationDbService;
    private Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login, container);

        dialog = getDialog();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        initComps(view);
        setupPage();

        return view;
    }

    public void getInputs(){
        userObj = new UserMO();
        userObj.setUSER_ID(String.valueOf(loginUsernameET.getText()));
        userObj.setPASS(String.valueOf(loginPasswordET.getText()));
    }

    private void setupPage() {
        //TODO: load most recent user
    }

    private void initComps(View view){
        loginUsernameET = (TextView) view.findViewById(R.id.loginUsernameETId);
        loginPasswordET = (TextView) view.findViewById(R.id.loginPasswordETId);
        loginLL = (LinearLayout) view.findViewById(R.id.loginButtonLLId);
        registerLL = (LinearLayout) view.findViewById(R.id.registerLLId);
        discardIV = (ImageView) view.findViewById(R.id.discardIVId);
        finapplLL = (LinearLayout) view.findViewById(R.id.loginLLId);
        simpleLL = (LinearLayout) view.findViewById(R.id.simpleLLId);
        register1NextLL = (LinearLayout) view.findViewById(R.id.register1NextLLId);

        loginLL.setOnClickListener(this);
        registerLL.setOnClickListener(this);
        discardIV.setOnClickListener(this);
        register1NextLL.setOnClickListener(this);

        discardIV.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        String messageStr = "Error !!";
        boolean doLogin = false;
        if(v.getId() == loginLL.getId()){
            getInputs();

            if(userObj == null){
                messageStr = "Enter Username/Password";
            }
            else if(userObj.getUSER_ID() == null || (userObj.getUSER_ID() != null && userObj.getUSER_ID().trim().isEmpty())){
                messageStr = "Enter Username";
            }
            else if(userObj.getUSER_ID().length() < 8){
                messageStr = "Username should be at least 8 characters long";
            }
            else if(userObj.getPASS() == null || (userObj.getPASS() != null && userObj.getPASS().trim().isEmpty())){
                messageStr = "Enter Password";
            }
            else if(userObj.getPASS().length() < 6){
                messageStr = "Username should be at least 6 characters long";
            }
            else{
                if(authorizationDbService.isAuthenticUser(userObj)){
                    SharedPreferences sharedpreferences = mContext.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(SHARED_PREF, userObj.getUSER_ID());
                    editor.commit();
                    doLogin = true;
                }
            }
        }
        else if(v.getId() == registerLL.getId()){
            animateView(finapplLL);
            discardIV.setVisibility(View.VISIBLE);
        }
        else if(v.getId() == register1NextLL.getId()){
            //TODO: registration is in complete
        }
        else if(v.getId() == discardIV.getId()) {
            doLogin = true;
            messageStr = "LOGIN";
        }

        if(doLogin){
            CalendarActivity activity = (CalendarActivity) this.getActivity();
            activity.onFinishUserDialog(messageStr);
            this.dismiss();
        }
    }

    private void animateView(final LinearLayout layout){
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) layout.getLayoutParams();
                params.leftMargin = (int)(-660 * interpolatedTime);
                layout.setLayoutParams(params);
            }
        };
        a.setDuration(250); // in ms
        layout.startAnimation(a);

        //TODO: -660 is based on the moto-g test on other screen sizes required
    }

    protected void showToast(String string){
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
    }

    // Empty constructor required for DialogFragment
    public LoginFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDb();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }

    private void initDb() {
        mContext = getActivity().getApplicationContext();

        authorizationDbService = new AuthorizationDbService(mContext);
    }

    public interface DialogResultListener {
        void onFinishUserDialog(String resultStr);
    }
}
