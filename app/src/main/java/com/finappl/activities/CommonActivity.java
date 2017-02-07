package com.finappl.activities;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.fragments.AddUpdateTransactionFragment;
import com.finappl.models.TransactionMO;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;
import com.finappl.utils.TestData;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;

import static com.finappl.utils.Constants.FRAGMENT_ADD_UPDATE_TRANSACTION;
import static com.finappl.utils.Constants.JAVA_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.TRANSACTION_OBJECT;

public abstract class CommonActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{
    private static final String CLASS_NAME = CommonActivity.class.getName();
    private Context mContext = this;

    protected UserMO user;

    private ProgressDialog progress;

    /*database*/
    private AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);
    /*database*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fetchUser();
    }

    @Override
    protected void onResume(){
        super.onResume();

        setupToolbar();

        setupNavigator();

        setupFab();
    }

    protected void fetchUser() {
        /*progress = FinappleUtility.getProgressDialog(mContext, "Verifying the user ..");
        FinappleUtility.showProgress(progress);

        FirebaseAuth fbUser = FirebaseAuth.getInstance();
        if(user != null && fbUser.getCurrentUser() != null && fbUser.getCurrentUser().getUid() != null){
            user = authorizationDbService.getActiveUser(fbUser.getCurrentUser().getUid());

            if(user != null){
                FinappleUtility.closeProgress(progress);
            }
            else{
                Log.e(CLASS_NAME, "Expecting user object to be non null here.");
            }
        }
        else{
            FinappleUtility.closeProgress(progress);
            FragmentManager fragMan = getFragmentManager();
            FinappleUtility.showLoginFragment(fragMan);
        }*/

        user = TestData.getUser();
    }

    private void setupNavigationDrawer() {
        //set navigation drawer header
        if(user != null){
            if(user.getNAME() != null){
                ((TextView)getNav_view().findViewById(R.id.common_nav_header_name_tv)).setText(user.getNAME());
            }

            if(user.getEMAIL() != null){
                ((TextView)getNav_view().findViewById(R.id.common_nav_header_email_tv)).setText(user.getEMAIL());
            }
        }
    }

    private void setupToolbar() {
        //toolbar
        //getToolbar().setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(getToolbar());
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setupNavigator() {
        //drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, getDrawer_layout(), getToolbar(), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        getDrawer_layout().addDrawerListener(toggle);
        toggle.syncState();

        //navigation
        getNav_view().setItemIconTintList(null);
        getNav_view().getMenu().getItem(1).setActionView(R.layout.menu_dot);
        getNav_view().setNavigationItemSelectedListener(this);
    }

    private void showFabToolbar(boolean show){
        if(show){
            getLayout().show();
        }
        else{
            getLayout().hide();
        }
    }



    @Override
    public void onClick(View view) {
        if(R.id.fab_transaction_ll == view.getId()){
            getLayout().hide();

            TransactionMO transaction = new TransactionMO();
            transaction.setTRAN_DATE(new Date());
            showTransactionFragment(transaction);
        }
        else if(R.id.fab_transfer_ll == view.getId()){
        }
        else{
            Log.e(CLASS_NAME, "Could not identify the view");
            //Utility.showSnacks(getWrapper_home_cl(), "Could not identify the view", OK, Snackbar.LENGTH_INDEFINITE);
        }
    }

    private void showTransactionFragment(TransactionMO transactionModelObj) {
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_TRANSACTION);

        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(TRANSACTION_OBJECT, transactionModelObj);
        bundle.putSerializable(LOGGED_IN_OBJECT, user);

        AddUpdateTransactionFragment fragment = new AddUpdateTransactionFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_ADD_UPDATE_TRANSACTION);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.calendar_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupFab() {
        getFabtoolbar_fab().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFabToolbar(true);
            }
        });
        getFab_transaction_ll().setOnClickListener(this);
        getFab_transfer_ll().setOnClickListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        /*if(R.id.navigation_drawer_logout == item.getItemId()){
            new SharedPrefUtility(mContext).clearSharedPreference();
            clearMasterData();
            setupAccountSummary();
        }
        else{
            Utility.showSnacks(getDrawer_layout(), "NOT IMPLEMENTED YET", OK, Snackbar.LENGTH_INDEFINITE);
            return true;
        }*/

        getDrawer_layout().closeDrawer(GravityCompat.START);
        onResume();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getDrawer_layout().isDrawerOpen(GravityCompat.START)) {
            getDrawer_layout().closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
        }

        if(getLayout().isToolbar()){
            getLayout().hide();
        }
        else{
            super.onBackPressed();
        }
    }

    protected abstract DrawerLayout getDrawer_layout();

    protected abstract Toolbar getToolbar();

    protected abstract NavigationView getNav_view();

    protected abstract FABToolbarLayout getLayout();

    protected abstract FloatingActionButton getFabtoolbar_fab();

    protected abstract LinearLayout getFab_transaction_ll();

    protected abstract LinearLayout getFab_transfer_ll();

    protected abstract CoordinatorLayout getWrapper_home_cl();
}
