package com.finapple.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.finapple.adapters.ManageContentAccountsSectionListAdapter;
import com.finapple.adapters.ManageContentCategoriesSectionListAdapter;
import com.finapple.adapters.ManageContentSpentOnsSectionListAdapter;
import com.finapple.R;
import com.finapple.dbServices.AuthorizationDbService;
import com.finapple.dbServices.ManageContentDbService;
import com.finapple.model.ManageContentModel;
import com.finapple.model.UsersModel;

import java.util.Map;

/**
 * Created by ajit on 31/1/15.
 */
public class ManageContentActivity extends Activity {

    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext = this;

    //db service
    private ManageContentDbService manageContentDbService = new ManageContentDbService(mContext);
    private AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);

    //User
    private UsersModel loggedInUserObj;

    //UI Components
    private LinearLayout manageContentCatTabLL;
    private LinearLayout manageContentAccTabLL;
    private LinearLayout manageContentSpntOnTabLL;

    private  TextView manageContentCatTabTV;
    private  TextView manageContentAccTabTV;
    private  TextView manageContentSpntOnTabTV;

    private  LinearLayout manageContentCatLL;
    private  LinearLayout manageContentAccLL;
    private  LinearLayout manageContentSpntOnLL;

    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_content);

        //get the Active user
        loggedInUserObj = getUser();
        if(loggedInUserObj == null){
            return;
        }

        //init ui components
        initUiComponents();

        //set up tab default and lists
        setUpTabs();

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) this.findViewById(R.id.manageContentRLId), robotoCondensedLightFont);
    }

    private void initUiComponents() {
        manageContentCatTabLL = (LinearLayout) this.findViewById(R.id.manageContentCatTabLLId);
        manageContentAccTabLL = (LinearLayout) this.findViewById(R.id.manageContentAccTabLLId);
        manageContentSpntOnTabLL = (LinearLayout) this.findViewById(R.id.manageContentSpntOnTabLLId);

        manageContentCatTabTV = (TextView) this.findViewById(R.id.manageContentCatTabTVId);
        manageContentAccTabTV = (TextView) this.findViewById(R.id.manageContentAccTabTVId);
        manageContentSpntOnTabTV = (TextView) this.findViewById(R.id.manageContentSpntOnTabTVId);

        manageContentCatLL = (LinearLayout) this.findViewById(R.id.manageContentCatLLId);
        manageContentAccLL = (LinearLayout) this.findViewById(R.id.manageContentAccLLId);
        manageContentSpntOnLL = (LinearLayout) this.findViewById(R.id.manageContentSpntOnLLId);
    }

    private void setUpTabs() {
        //reset all tabs
        resetTabs();

        ListView manageContentCatLV = (ListView) this.findViewById(R.id.manageContentCatLVId);
        ListView manageContentAccLV = (ListView) this.findViewById(R.id.manageContentAccLVId);
        ListView manageContentSpntOnLV = (ListView) this.findViewById(R.id.manageContentSpntOnLVId);

        //Counters
        TextView manageContentCatTabCounterTV = (TextView) this.findViewById(R.id.manageContentCatTabCounterTVId);
        TextView manageContentAccTabCounterTV = (TextView) this.findViewById(R.id.manageContentAccTabCounterTVId);
        TextView manageContentSpntOnTabCounterTV = (TextView) this.findViewById(R.id.manageContentSpntOnTabCounterTVId);

        //get all categories, spenton & accounts categorized into user created and default from the db
        ManageContentModel manageContentModelObj = manageContentDbService.getAllContent(loggedInUserObj.getUSER_ID());

        //set categories list
        ManageContentCategoriesSectionListAdapter manageContentCategoriesSectionListAdapter = new ManageContentCategoriesSectionListAdapter(mContext, R.layout
                    .manage_content_list_view_header_categories, R.layout.manage_content_list_view_content_categories, manageContentModelObj.getCategoriesMap(),
                    manageContentModelObj.getUserNameStr());
        manageContentCategoriesSectionListAdapter.notifyDataSetChanged();
        manageContentCatLV.setAdapter(manageContentCategoriesSectionListAdapter);
        manageContentCatTabCounterTV.setText(String.valueOf(manageContentCategoriesSectionListAdapter.getCount() - manageContentModelObj.getCategoriesMap().size()));

        //set accounts list
        ManageContentAccountsSectionListAdapter manageContentAccountsSectionListAdapter = new ManageContentAccountsSectionListAdapter(mContext, R.layout
                    .manage_content_list_view_header_accounts, R.layout.manage_content_list_view_content_accounts, manageContentModelObj.getAccountsMap(),
                    manageContentModelObj.getUserNameStr());
        manageContentAccountsSectionListAdapter.notifyDataSetChanged();
        manageContentAccLV.setAdapter(manageContentAccountsSectionListAdapter);
        manageContentAccTabCounterTV.setText(String.valueOf(manageContentAccountsSectionListAdapter.getCount() - manageContentModelObj.getAccountsMap().size()));

        //set spent ons list
        ManageContentSpentOnsSectionListAdapter manageContentSpentOnsSectionListAdapter = new ManageContentSpentOnsSectionListAdapter(mContext, R.layout
                    .manage_content_list_view_header_spent_ons, R.layout.manage_content_list_view_content_spent_ons, manageContentModelObj.getSpentOnsMap(),
                    manageContentModelObj.getUserNameStr());
        manageContentSpentOnsSectionListAdapter.notifyDataSetChanged();
        manageContentSpntOnLV.setAdapter(manageContentSpentOnsSectionListAdapter);
        manageContentSpntOnTabCounterTV.setText(String.valueOf(manageContentSpentOnsSectionListAdapter.getCount() - manageContentModelObj.getSpentOnsMap().size()));

        //tabs
        manageContentCatTabLL.setBackground(manageContentCatTabLL.getResources().getDrawable(R.color.activeTab));
        manageContentAccTabLL.setBackground(manageContentAccTabLL.getResources().getDrawable(R.color.inactiveTab));
        manageContentSpntOnTabLL.setBackground(manageContentSpntOnTabLL.getResources().getDrawable(R.color.inactiveTab));

        //labels
        manageContentCatTabTV.setTextColor(manageContentCatTabTV.getResources().getColor(R.color.activeTab));
        manageContentAccTabTV.setTextColor(manageContentCatTabTV.getResources().getColor(R.color.inactiveTab));
        manageContentSpntOnTabTV.setTextColor(manageContentCatTabTV.getResources().getColor(R.color.inactiveTab));

        //lists
        manageContentCatLL.setVisibility(View.VISIBLE);
        manageContentAccLL.setVisibility(View.GONE);
        manageContentSpntOnLL.setVisibility(View.GONE);
    }

    private void resetTabs() {
        //tabs
        manageContentCatTabLL.setBackground(manageContentCatTabLL.getResources().getDrawable(R.color.inactiveTab));
        manageContentAccTabLL.setBackground(manageContentAccTabLL.getResources().getDrawable(R.color.inactiveTab));
        manageContentSpntOnTabLL.setBackground(manageContentSpntOnTabLL.getResources().getDrawable(R.color.inactiveTab));

        //labels
        manageContentCatTabTV.setTextColor(manageContentCatTabTV.getResources().getColor(R.color.inactiveTab));
        manageContentAccTabTV.setTextColor(manageContentCatTabTV.getResources().getColor(R.color.inactiveTab));
        manageContentSpntOnTabTV.setTextColor(manageContentCatTabTV.getResources().getColor(R.color.inactiveTab));

        //lists
        manageContentCatLL.setVisibility(View.GONE);
        manageContentAccLL.setVisibility(View.GONE);
        manageContentSpntOnLL.setVisibility(View.GONE);

    }

    public void onTabClick(View view){
        resetTabs();

        switch(view.getId()){
            case R.id.manageContentCatTabLLId : manageContentCatTabLL.setBackground(manageContentCatTabLL.getResources().getDrawable(R.color.activeTab));
                manageContentCatTabTV.setTextColor(manageContentCatTabTV.getResources().getColor(R.color.activeTab));
                manageContentCatLL.setVisibility(View.VISIBLE);
                break;

            case R.id.manageContentAccTabLLId : manageContentAccTabLL.setBackground(manageContentAccTabLL.getResources().getDrawable(R.color.activeTab));
                manageContentAccTabTV.setTextColor(manageContentCatTabTV.getResources().getColor(R.color.activeTab));
                manageContentAccLL.setVisibility(View.VISIBLE);
                break;

            case R.id.manageContentSpntOnTabLLId : manageContentSpntOnTabLL.setBackground(manageContentSpntOnTabLL.getResources().getDrawable(R.color.activeTab));
                manageContentSpntOnTabTV.setTextColor(manageContentCatTabTV.getResources().getColor(R.color.activeTab));
                manageContentSpntOnLL.setVisibility(View.VISIBLE);
                break;

            default:
                Log.e(CLASS_NAME, "TAB error !!! Something is wrong while switching tabs !! Check where ur clicking..");
                showToast("TAB ERROR !");
        }
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

    public void onAddClick(View view) {
        Intent intent = null;

        if(manageContentCatLL.getVisibility() == View.VISIBLE){
            intent = new Intent(ManageContentActivity.this, AddUpdateCategoryActivity.class);
        }
        else if(manageContentAccLL.getVisibility() == View.VISIBLE){
            intent = new Intent(ManageContentActivity.this, AddUpdateAccountActivity.class);
        }
        else if(manageContentSpntOnLL.getVisibility() == View.VISIBLE){
            intent = new Intent(ManageContentActivity.this, AddUpdateSpentOnActivity.class);
        }
        else{
            Log.e(CLASS_NAME, "Error !! while knowing which page to navigate to on add button click !!");
            showToast("Error !!");
            return;
        }

        startActivity(intent);
        finish();
    }

    public void onBackClick(View view){
        Intent intent = new Intent(ManageContentActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ManageContentActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    //method iterates over each component in the activity and when it finds a text view..sets its font
    public void setFont(ViewGroup group, Typeface font) {
        int count = group.getChildCount();
        View v;
        for(int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if(v instanceof TextView) {
                ((TextView) v).setTypeface(font);
            }
            else if(v instanceof ViewGroup) {
                setFont((ViewGroup) v, font);
            }
        }
    }

    protected void showToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}
