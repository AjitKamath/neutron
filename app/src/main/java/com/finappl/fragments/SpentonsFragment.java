package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.activities.CalendarActivity;
import com.finappl.adapters.SpentonsFragmentListViewAdapter;
import com.finappl.dbServices.CalendarDbService;
import com.finappl.models.SpentOnMO;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;

import java.io.Serializable;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.finappl.utils.Constants.FRAGMENT_ADD_UPDATE_SPENTON;
import static com.finappl.utils.Constants.FRAGMENT_DELETE_CONFIRM;
import static com.finappl.utils.Constants.FRAGMENT_SPENTONS;
import static com.finappl.utils.Constants.IMAGE_OBJECT;
import static com.finappl.utils.Constants.IMAGE_SELECTED_SPENTON;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.OK;
import static com.finappl.utils.Constants.SELECTED_GENERIC_OBJECT;
import static com.finappl.utils.Constants.SELECTED_IMAGE_OBJECT;
import static com.finappl.utils.Constants.UI_FONT;
import static com.finappl.utils.Constants.UN_IDENTIFIED_VIEW;

/**
 * Created by ajit on 21/3/16.
 */
public class SpentonsFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    /*Components*/
    @InjectView(R.id.spentonsRLId)
    RelativeLayout spentonsRL;

    @InjectView(R.id.spentonsLVId)
    ListView spentonsLV;
    /*Components*/

    private List<SpentOnMO> spentonsList;
    private UserMO user;

    /*Database Service*/
    private CalendarDbService calendarDbService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.spentons, container);
        ButterKnife.inject(this, view);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getDataFromBundle();
        initComps();
        setupPage();

        return view;
    }

    @OnClick(R.id.spentonsAddUpdateTVId)
    public void showAddUpdateSpenton(){
        SpentOnMO spenton = new SpentOnMO();
        spenton.setSPNT_ON_IMG(String.valueOf(IMAGE_SELECTED_SPENTON));
        showAddUpdateSpenton(spenton);
    }

    @OnClick(R.id.spentonsCloseTVId)
    public void close(){
        dismiss();
    }

    private void getDataFromBundle() {
        user = (UserMO) getArguments().get(LOGGED_IN_OBJECT);
    }

    private void setupPage() {
        getMasterData();

        SpentonsFragmentListViewAdapter adapter = new SpentonsFragmentListViewAdapter(mContext, spentonsList, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(R.id.spentonDeleteIVId == view.getId()){
                    //check for transactions & budgets which were created using the category & warn user that all the transactions/budgets will be marked to default category
                    showDeleteConfirm((SpentOnMO)view.getTag());
                }
                else if(R.id.spentonModifyIVId == view.getId()){
                    showAddUpdateSpenton((SpentOnMO)view.getTag());
                }
                else{
                    FinappleUtility.showSnacks(spentonsRL, UN_IDENTIFIED_VIEW, OK, Snackbar.LENGTH_INDEFINITE);
                }
            }
        });
        spentonsLV.setAdapter(adapter);
    }

    private void showDeleteConfirm(SpentOnMO spenton){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_DELETE_CONFIRM);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(SELECTED_GENERIC_OBJECT, spenton);
        bundle.putSerializable(LOGGED_IN_OBJECT, user);

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_SPENTONS);

        DeleteConfirmFragment fragment = new DeleteConfirmFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PopupDialogTheme);
        fragment.show(manager, FRAGMENT_DELETE_CONFIRM);
    }

    private void showAddUpdateSpenton(SpentOnMO spenton){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_SPENTON);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(SELECTED_IMAGE_OBJECT, spenton);
        bundle.putSerializable(IMAGE_OBJECT, (Serializable) spentonsList);
        bundle.putSerializable(LOGGED_IN_OBJECT, user);

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_SPENTONS);

        AddUpdateSpentonFragment fragment = new AddUpdateSpentonFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PopupDialogTheme);
        fragment.show(manager, FRAGMENT_ADD_UPDATE_SPENTON);
    }

    private void getMasterData() {
        spentonsList = calendarDbService.getAllSpentOns(user.getUSER_ID());
    }

    private void initComps(){
        setFont(spentonsRL);
    }

    // Empty constructor required for DialogFragment
    public SpentonsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();

        initDb();
    }

    private void initDb() {
        calendarDbService = new CalendarDbService(mContext);
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog d = getDialog();
        if (d!=null) {
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            d.getWindow().setLayout(width, height);
        }
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

    public void onFragmentClose(String messageStr) {
        FinappleUtility.showSnacks(spentonsRL, messageStr, OK, Snackbar.LENGTH_LONG);
        setupPage();
    }

    //this method is called when u have to change all over the db where this spenton was used and then delete the spent on
    public void normalizeImpactsAndDeleteSpenton(SpentOnMO spentOn) {
        calendarDbService.updateAll(user, spentOn);
        deleteCategory(spentOn);
    }

    //this method is called when we just have to delete the apent on which has not been used anywhere
    public void deleteCategory(SpentOnMO spenton) {
        calendarDbService.deleteCategory(spenton.getSPNT_ON_ID());
        FinappleUtility.showSnacks(spentonsRL, "Spent On deleted !", OK, Snackbar.LENGTH_LONG);
        setupPage();
        ((CalendarActivity)getActivity()).initActivity();
    }
}
