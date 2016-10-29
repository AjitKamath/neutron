package com.finappl.activities;

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

import com.finappl.adapters.ManageContentAccountsSectionListAdapter;
import com.finappl.adapters.ManageContentCategoriesSectionListAdapter;
import com.finappl.adapters.ManageContentSpentOnsSectionListAdapter;
import com.finappl.R;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.dbServices.ManageContentDbService;
import com.finappl.models.ManageContentModel;
import com.finappl.models.UserMO;
import com.finappl.models.UsersModel;
import com.finappl.utils.FinappleUtility;

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
    private UserMO loggedInUserObj;

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
        loggedInUserObj = authorizationDbService.getActiveUser(FinappleUtility.getInstance().getActiveUserId(mContext));
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
                    .manage_content_list_view_divider, R.layout.manage_content_list_view_content_categories, manageContentModelObj.getCategoriesMap(),
                    manageContentModelObj.getUserNameStr());
        manageContentCategoriesSectionListAdapter.notifyDataSetChanged();
        manageContentCatLV.setAdapter(manageContentCategoriesSectionListAdapter);

        if(manageContentModelObj.getCategoriesMap().size() ==1){
            manageContentCatTabCounterTV.setText(String.valueOf(manageContentCategoriesSectionListAdapter.getCount()));
        }
        else{
            manageContentCatTabCounterTV.setText(String.valueOf(manageContentCategoriesSectionListAdapter.getCount() - 1));
        }


        //set accounts list
        ManageContentAccountsSectionListAdapter manageContentAccountsSectionListAdapter = new ManageContentAccountsSectionListAdapter(mContext, R.layout
                    .manage_content_list_view_divider, R.layout.manage_content_list_view_content_accounts, manageContentModelObj.getAccountsMap(),
                    manageContentModelObj.getUserNameStr());
        manageContentAccountsSectionListAdapter.notifyDataSetChanged();
        manageContentAccLV.setAdapter(manageContentAccountsSectionListAdapter);

        if(manageContentModelObj.getAccountsMap().size() ==1){
            manageContentAccTabCounterTV.setText(String.valueOf(manageContentAccountsSectionListAdapter.getCount()));
        }
        else{
            manageContentAccTabCounterTV.setText(String.valueOf(manageContentAccountsSectionListAdapter.getCount() - 1));
        }

        //set spent ons list
        ManageContentSpentOnsSectionListAdapter manageContentSpentOnsSectionListAdapter = new ManageContentSpentOnsSectionListAdapter(mContext, R.layout
                    .manage_content_list_view_divider, R.layout.manage_content_list_view_content_spent_ons, manageContentModelObj.getSpentOnsMap(),
                    manageContentModelObj.getUserNameStr());
        manageContentSpentOnsSectionListAdapter.notifyDataSetChanged();
        manageContentSpntOnLV.setAdapter(manageContentSpentOnsSectionListAdapter);

        if(manageContentModelObj.getSpentOnsMap().size() ==1){
            manageContentSpntOnTabCounterTV.setText(String.valueOf(manageContentSpentOnsSectionListAdapter.getCount()));
        }
        else{
            manageContentSpntOnTabCounterTV.setText(String.valueOf(manageContentSpentOnsSectionListAdapter.getCount() - 1));
        }

        resetTabs();

        manageContentCatTabTV.setBackgroundResource(R.drawable.view_activities_active_tab_inner);
        manageContentCatTabTV.setTextColor(manageContentCatTabTV.getResources().getColor(R.color.white));
        manageContentCatLL.setVisibility(View.VISIBLE);
    }

    private void resetTabs() {
        manageContentCatTabTV.setBackgroundResource(R.drawable.view_activities_inactive_tab_inner);
        manageContentAccTabTV.setBackgroundResource(R.drawable.view_activities_inactive_tab_inner);
        manageContentSpntOnTabTV.setBackgroundResource(R.drawable.view_activities_inactive_tab_inner);

        //labels
        manageContentCatTabTV.setTextColor(manageContentCatTabTV.getResources().getColor(R.color.finappleTheme));
        manageContentAccTabTV.setTextColor(manageContentCatTabTV.getResources().getColor(R.color.finappleTheme));
        manageContentSpntOnTabTV.setTextColor(manageContentCatTabTV.getResources().getColor(R.color.finappleTheme));

        //lists
        manageContentCatLL.setVisibility(View.GONE);
        manageContentAccLL.setVisibility(View.GONE);
        manageContentSpntOnLL.setVisibility(View.GONE);
    }

    public void onTabClick(View view){
        resetTabs();

        switch(view.getId()){
            case R.id.manageContentCatTabTVId : manageContentCatTabTV.setBackgroundResource(R.drawable.view_activities_active_tab_inner);
                manageContentCatTabTV.setTextColor(mContext.getResources().getColor(R.color.white));
                manageContentCatLL.setVisibility(View.VISIBLE);
                break;

            case R.id.manageContentAccTabTVId : manageContentAccTabTV.setBackgroundResource(R.drawable.view_activities_active_tab_inner);
                manageContentAccTabTV.setTextColor(mContext.getResources().getColor(R.color.white));
                manageContentAccLL.setVisibility(View.VISIBLE);
                break;

            case R.id.manageContentSpntOnTabTVId : manageContentSpntOnTabTV.setBackgroundResource(R.drawable.view_activities_active_tab_inner);
                manageContentSpntOnTabTV.setTextColor(mContext.getResources().getColor(R.color.white));
                manageContentSpntOnLL.setVisibility(View.VISIBLE);
                break;

            default:
                Log.e(CLASS_NAME, "TAB error !!! Something is wrong while switching tabs !! Check where ur clicking..");
                showToast("TAB ERROR !");
        }
    }

    public void onAddClick(View view) {
        Intent intent = null;

        /*if(manageContentCatLL.getVisibility() == View.VISIBLE){
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
        }*/

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
