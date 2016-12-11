package com.finappl.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.fragments.LoginFragment;
import com.finappl.models.UserMO;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import static com.finappl.utils.Constants.UI_FONT;
import static com.finappl.utils.Constants.UN_IDENTIFIED_VIEW;

/**
 * Created by ajit on 30/9/15.
 */
public class LoginViewPagerAdapter extends PagerAdapter {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    private List<Integer> layoutsList;

    public int activePageIndex = 0;

    /*Login Screen*/
    private EditText loginEmailET;
    private EditText loginPasswordET;

    /*Sign Up Screen*/
    private EditText signupNameET;
    private EditText signupEmailET;
    private EditText signupPasswordET;

    //listener
    private LoginFragment.OnClickListener onClickListener;

    public LoginViewPagerAdapter(Context context, List<Integer> layoutsList, LoginFragment.OnClickListener onClickListener) {
        this.mContext = context;
        this.layoutsList = layoutsList;
        this.onClickListener = onClickListener;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(layoutsList.get(position), collection, false);

        activePageIndex = position;

        switch(position){
            case 0: setUpSignIn(layout); break;
            case 1: setUpSignUp(layout); break;
        }

        setFont(layout);

        collection.addView(layout);

        return layout;
    }

    private void setUpSignIn(ViewGroup layout){
        loginEmailET = (EditText)layout.findViewById(R.id.loginEmailETId);
        loginPasswordET = (EditText)layout.findViewById(R.id.loginPasswordETId);

        FirebaseAuth user = FirebaseAuth.getInstance();
        if(user != null && user.getCurrentUser() != null && user.getCurrentUser().getEmail() != null){
            loginEmailET.setText(user.getCurrentUser().getEmail());
        }

        AppCompatButton loginBtn = (AppCompatButton) layout.findViewById(R.id.loginBtnId);
        loginBtn.setOnClickListener(clickListener);
    }

    private UserMO validateSignIn(){
        String emailStr = String.valueOf(loginEmailET.getText()).trim();
        String passStr = String.valueOf(loginPasswordET.getText()).trim();

        boolean isEmailValid = false;
        boolean isPasswordValid = false;

        /*validate email*/
        if(emailStr.isEmpty()){
            loginEmailET.setError("Enter Email");
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()){
            loginEmailET.setError("Enter a valid Email");
        }
        else{
            loginEmailET.setError(null);
            isEmailValid = true;
        }

        /*validate password*/
        if(passStr.isEmpty()){
            loginPasswordET.setError("Enter Password");
        }
        else if(passStr.length() < 6){
            loginPasswordET.setError("Password should have at least 6 characters");
        }
        else{
            loginPasswordET.setError(null);
            isPasswordValid = true;
        }

        if(isEmailValid && isPasswordValid){
            UserMO user = new UserMO();
            user.setEMAIL(emailStr);
            user.setPASS(passStr);
            return user;
        }
        return null;
    }

    public void setSignupEmail(String emailStr){
        //prepopulate emailStr into sign up email field
        if(emailStr != null){
            signupEmailET.setText(emailStr);
        }
    }

    private void setUpSignUp(ViewGroup layout){
        signupNameET = (EditText)layout.findViewById(R.id.signupNameETId);
        signupEmailET = (EditText)layout.findViewById(R.id.signupEmailETId);
        signupPasswordET = (EditText)layout.findViewById(R.id.signupPasswordETId);

        AppCompatButton signupBtn = (AppCompatButton) layout.findViewById(R.id.signupBtnId);
        signupBtn.setOnClickListener(clickListener);
    }

    private UserMO validateSignUp(){
        String nameStr = String.valueOf(signupNameET.getText()).trim();
        String emailStr = String.valueOf(signupEmailET.getText()).trim();
        String passStr = String.valueOf(signupPasswordET.getText()).trim();

        boolean isNameValid = false;
        boolean isEmailValid = false;
        boolean isPasswordValid = false;

        /*validate name*/
        if(nameStr.isEmpty()){
            signupNameET.setError("Enter Name");
        }
        else{
            signupNameET.setError(null);
            isNameValid = true;
        }

        /*validate email*/
        if(emailStr.isEmpty()){
            loginEmailET.setError("Enter Email");
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()){
            loginEmailET.setError("Enter a valid Email");
        }
        else{
            loginEmailET.setError(null);
            isEmailValid = true;
        }

        /*validate password*/
        if(passStr.isEmpty()){
            loginPasswordET.setError("Enter Password");
        }
        else if(passStr.length() < 6){
            loginPasswordET.setError("Password should have at least 6 characters");
        }
        else{
            loginPasswordET.setError(null);
            isPasswordValid = true;
        }

        if(isNameValid && isEmailValid && isPasswordValid){
            UserMO user = new UserMO();
            user.setEMAIL(emailStr);
            user.setPASS(passStr);
            user.setNAME(nameStr);
            return user;
        }
        return null;
    }

    protected void showToast(String string) {
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return layoutsList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getString(layoutsList.get(position));
    }

    //--------------------------------list view click listener------------------------------------------------------
    private AppCompatButton.OnClickListener clickListener;
    {
        clickListener = new AppCompatButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(R.id.loginBtnId == view.getId()){
                    UserMO user = validateSignIn();
                    if(user != null){
                        onClickListener.onClickListener(user, "LOGIN");
                    }
                }
                else if(R.id.signupBtnId == view.getId()){
                    UserMO user = validateSignUp();
                    if(user != null){
                        onClickListener.onClickListener(user, "SIGNUP");
                    }
                }
                else{
                    Log.e(CLASS_NAME, UN_IDENTIFIED_VIEW);
                }
            }
        };
    }

    //method iterates over each component in the activity and when it finds a text view..sets its font
    public void setFont(ViewGroup group) {
        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), UI_FONT);

        int count = group.getChildCount();
        View v;

        for(int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if(v instanceof TextView) {
                ((TextView) v).setTypeface(robotoCondensedLightFont);
            }
            else if(v instanceof ViewGroup) {
                setFont((ViewGroup) v);
            }
        }
    }
}