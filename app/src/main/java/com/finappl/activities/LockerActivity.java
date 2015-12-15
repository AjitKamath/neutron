package com.finappl.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.models.AccountsModel;
import com.finappl.models.ActivityModel;
import com.finappl.models.BudgetModel;
import com.finappl.models.ScheduledTransactionModel;
import com.finappl.models.ScheduledTransferModel;
import com.finappl.models.TransactionModel;
import com.finappl.models.TransferModel;
import com.finappl.models.UsersModel;
import com.finappl.utils.EncryptionUtil;

import java.util.Map;

public class LockerActivity extends Activity {

	private final String CLASS_NAME = this.getClass().getName();
    private final String PREFERENCE_LOCK = "PREF_LOCK";

    private Dialog dialog;
    private Context mContext = this;

    //Database
    private AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);

    //User
    private UsersModel loggedInUserObj;

    private EditText lockPoprPinET;

    private String oldPinStr = "";

    public boolean isNavigation = false;

    @Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause(){
        super.onPause();

        if(!isNavigation){
            lockApp(true);
        }

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        lockApp(true);
    }

    @Override
    public void onResume(){
        super.onResume();

        setVisible(true);

        SharedPreferences sharedpreferences = getSharedPreferences(PREFERENCE_LOCK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        if(sharedpreferences.getBoolean("LOCK", true)){
            loggedInUserObj = getUser();

            if("Y".equalsIgnoreCase(loggedInUserObj.getSET_SEC_ACTIVE())){
                showLockerPopper();
            }
        }
    }

    public void showLockerPopper(){
        // Create custom message popper object
        if(dialog != null){
            return;
        }

        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.lock_popper);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        oldPinStr = "";

        TextView lockPoprOneTV, lockPoprTwoTV, lockPoprThreeTV, lockPoprFourTV, lockPoprFiveTV, lockPoprSixTV, lockPoprSevenTV, lockPoprEightTV, lockPoprNineTV, lockPoprZeroTV;
        LinearLayout lockPoprDeleteLL, lockPoprSubmitLL;

        lockPoprPinET = (EditText) dialog.findViewById(R.id.lockPoprPinETId);

        lockPoprOneTV = (TextView) dialog.findViewById(R.id.lockPoprOneTVId);
        lockPoprTwoTV = (TextView) dialog.findViewById(R.id.lockPoprTwoTVId);
        lockPoprThreeTV = (TextView) dialog.findViewById(R.id.lockPoprThreeTVId);
        lockPoprFourTV = (TextView) dialog.findViewById(R.id.lockPoprfourTVId);
        lockPoprFiveTV = (TextView) dialog.findViewById(R.id.lockPoprFiveTVId);
        lockPoprSixTV = (TextView) dialog.findViewById(R.id.lockPoprSixTVId);
        lockPoprSevenTV = (TextView) dialog.findViewById(R.id.lockPoprSevenTVId);
        lockPoprEightTV = (TextView) dialog.findViewById(R.id.lockPoprEightTVId);
        lockPoprNineTV = (TextView) dialog.findViewById(R.id.lockPoprNineTVId);
        lockPoprZeroTV = (TextView) dialog.findViewById(R.id.lockPoprZeroTVId);

        LinearLayout lockPoprNewPinLL = (LinearLayout) dialog.findViewById(R.id.lockPoprNewPinLLId);
        lockPoprDeleteLL = (LinearLayout) dialog.findViewById(R.id.lockPoprDeleteLLId);
        lockPoprSubmitLL = (LinearLayout) dialog.findViewById(R.id.lockPoprSubmitLLId);

        lockPoprOneTV.setOnClickListener(textViewClickListener);
        lockPoprTwoTV.setOnClickListener(textViewClickListener);
        lockPoprThreeTV.setOnClickListener(textViewClickListener);
        lockPoprFourTV.setOnClickListener(textViewClickListener);
        lockPoprFiveTV.setOnClickListener(textViewClickListener);
        lockPoprSixTV.setOnClickListener(textViewClickListener);
        lockPoprSevenTV.setOnClickListener(textViewClickListener);
        lockPoprEightTV.setOnClickListener(textViewClickListener);
        lockPoprNineTV.setOnClickListener(textViewClickListener);
        lockPoprZeroTV.setOnClickListener(textViewClickListener);

        lockPoprDeleteLL.setOnClickListener(linearLayoutClickListener);
        lockPoprSubmitLL.setOnClickListener(linearLayoutClickListener);

        lockPoprNewPinLL.setVisibility(View.GONE);

        dialog.show();

        //set font for all the text view
        setFont((ViewGroup) dialog.findViewById(R.id.lockPoprLLId));
    }

    public void lockApp(boolean lockApp){
        SharedPreferences sharedpreferences = getSharedPreferences(PREFERENCE_LOCK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putBoolean("LOCK", lockApp);
        editor.commit();

        oldPinStr = "";
        if(lockPoprPinET != null){
            lockPoprPinET.setText("");
        }
    }

    protected void showToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    private UsersModel getUser(){
        Map<Integer, UsersModel> userMap = authorizationDbService.getActiveUser();

        if(userMap == null || (userMap != null && userMap.isEmpty())){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            showToast("Please Login");
            return null;
        }
        else if(userMap.size() > 1){
            Intent intent = new Intent(this, JimBrokeItActivity.class);
            startActivity(intent);
            finish();
            showToast("Multiple Users are Active : Possible DB Corruption.");
        }
        else{
            return userMap.get(0);
        }

        Log.e(CLASS_NAME, "I'm not supposed to be read/print/shown..... This should have been a dead code. If you can read me, Authorization of user has failed and you should " +
                "probably die twice by now.");
        return null;
    }

    private void killPoppers(){
        if(dialog != null){
            dialog.dismiss();
        }
    }

    private TextView.OnClickListener textViewClickListener;
    {
        textViewClickListener = new TextView.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(lockPoprPinET.isFocused()){
                    oldPinStr += String.valueOf(v.getTag());
                    lockPoprPinET.setText(lockPoprPinET.getText()+"*");
                }
            }
        };
    }

    private LinearLayout.OnClickListener linearLayoutClickListener;
    {
        linearLayoutClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch(v.getId()) {
                    case R.id.lockPoprDeleteLLId:
                        if (lockPoprPinET.isFocused()) {
                            String inputStr = String.valueOf(lockPoprPinET.getText());

                            if(!inputStr.isEmpty()) {
                                inputStr = inputStr.substring(0, inputStr.length()-1);
                                lockPoprPinET.setText(inputStr);

                                oldPinStr = oldPinStr.substring(0, oldPinStr.length()-1);
                            }
                        }
                        break;

                    case R.id.lockPoprSubmitLLId:
                        try {
                            if (oldPinStr.isEmpty()) {
                                showToast("Enter PIN");
                            } else if (!EncryptionUtil.encrypt(oldPinStr).equals(loggedInUserObj.getSET_SEC_PIN())) {
                                showToast("Incorrect PIN");

                                lockPoprPinET.setText("");
                                oldPinStr = "";
                            } else {
                                killPoppers();
                            }
                        } catch (Exception e) {
                            Log.e(CLASS_NAME, "Error !! While Encryption ");
                        }

                        break;

                    default: showToast("Error !!");
                }
            }
        };
    }

    public void navigateTo(Class className, String key, Object obj){
        lockApp(false);
        Intent intent = new Intent(this, className);

        if(key != null && !key.isEmpty()){
            if(obj instanceof ScheduledTransactionModel){
                intent.putExtra(key, (ScheduledTransactionModel)obj);
            }
            else if(obj instanceof ScheduledTransferModel){
                intent.putExtra(key, (ScheduledTransferModel)obj);
            }
            else if(obj instanceof ScheduledTransferModel){
                intent.putExtra(key, (ScheduledTransferModel)obj);
            }
            else if(obj instanceof TransactionModel){
                intent.putExtra(key, (TransactionModel)obj);
            }
            else if(obj instanceof TransferModel){
                intent.putExtra(key, (TransferModel)obj);
            }
            else if(obj instanceof BudgetModel){
                intent.putExtra(key, (BudgetModel)obj);
            }
            else if(obj instanceof AccountsModel){
                intent.putExtra(key, (AccountsModel)obj);
            }
            else if(obj instanceof ActivityModel){
                intent.putExtra(key, (ActivityModel)obj);
            }
        }

        startActivity(intent);
        isNavigation = true;
    }

    //method iterates over each component in the activity and when it finds a text view..sets its font
    public void setFont(ViewGroup group) {
        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(this.getAssets(), "Roboto-Light.ttf");

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

