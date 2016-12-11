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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.adapters.TagsGridViewAdapter;
import com.finappl.dbServices.CalendarDbService;
import com.finappl.models.SpentOnMO;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;
import com.finappl.utils.IdGenerator;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.finappl.utils.Constants.ADMIN_USERID;
import static com.finappl.utils.Constants.FRAGMENT_ADD_UPDATE_SPENTON;
import static com.finappl.utils.Constants.FRAGMENT_SELECT_IMAGE;
import static com.finappl.utils.Constants.IMAGE_OBJECT;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.OK;
import static com.finappl.utils.Constants.SELECTED_IMAGE_OBJECT;
import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 21/3/16.
 */
public class AddUpdateSpentonFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    /*components*/
    @InjectView(R.id.addUpdateSpentonRLId)
    RelativeLayout addUpdateSpentonRL;

    @InjectView(R.id.addUpdateSpentonIVId)
    ImageView addUpdateSpentonIV;

    @InjectView(R.id.addUpdateSpentonNameETId)
    EditText addUpdateSpentonNameET;

    @InjectView(R.id.addUpdateSpentonTagsETId)
    EditText addUpdateSpentonTagsET;

    @InjectView(R.id.addUpdateSpentonTagsGVId)
    GridView addUpdateSpentonTagsGV;

    @InjectView(R.id.addUpdateSpentonNoTagsTVId)
    TextView addUpdateSpentonNoTagsTV;
    /*components*/

    /*Data*/
    private SpentOnMO spenton;
    private List<SpentOnMO> spentonList;
    private Set<String> tagsSet;
    private UserMO user;

    /*Database*/
    private CalendarDbService calendarDbService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_update_spenton, container);
        ButterKnife.inject(this, view);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getDataFromBundle();
        initComps();
        setupPage();

        return view;
    }

    @OnClick(R.id.addUpdateSpentonHeaderCloseTVId)
    public void close(){
        dismiss();
    }

    @OnClick(R.id.addUpdateSpentonHeaderSaveTVId)
    public void onAddUpdate(){
        SpentOnMO tempSpenton = validateAndGetInputs();

        if(tempSpenton == null){
            return;
        }

        String messageStr;

        //old Spenton
        if(tempSpenton.getSPNT_ON_ID() != null){
            if (calendarDbService.updateSpenton(tempSpenton)) {
                messageStr = "Spent On updated";
            } else {
                messageStr = "Failed to update Spent On";
            }
        }
        else{
            //check if the Spenton already exists
            if(calendarDbService.isSpentonAlreadyExist(user.getUSER_ID(), tempSpenton.getSPNT_ON_NAME())){
                FinappleUtility.showSnacks(addUpdateSpentonRL, "Spent On already exists", OK, Snackbar.LENGTH_LONG);
                return;
            }

            tempSpenton.setSPNT_ON_ID(IdGenerator.getInstance().generateUniqueId("SPNT"));
            tempSpenton.setUSER_ID(user.getUSER_ID());
            long result = calendarDbService.addNewSpenton(tempSpenton);

            if (result == -1) {
                messageStr = "Failed to create a new Spent On !";
            } else {
                messageStr = "New Spent On created";
            }
        }

        close();
        ((SpentonsFragment)getTargetFragment()).onFragmentClose(messageStr);
    }

    private SpentOnMO validateAndGetInputs() {
        String spentonNameStr = String.valueOf(addUpdateSpentonNameET.getText());
        String spentonImageStr = String.valueOf(((SpentOnMO)addUpdateSpentonIV.getTag()).getSPNT_ON_IMG());

        if(spentonNameStr != null && !spentonNameStr.trim().isEmpty()){
            spentonNameStr = spentonNameStr.trim();
        }
        else{
            FinappleUtility.showSnacks(addUpdateSpentonRL, "Spent On Title is empty", OK, Snackbar.LENGTH_LONG);
            return null;
        }

        spenton.setSPNT_ON_NAME(spentonNameStr);
        spenton.setSPNT_ON_IMG(spentonImageStr);
        spenton.setTagsStr(FinappleUtility.setToCSV(tagsSet));

        return spenton;
    }

    @OnClick(R.id.addUpdateSpentonIVId)
    public void showSelectImage(){
        //if the Spenton is admins Spenton then do not allow to select image
        if(ADMIN_USERID.equalsIgnoreCase(spenton.getUSER_ID())){
            return;
        }

        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_SELECT_IMAGE);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(SELECTED_IMAGE_OBJECT, (Serializable) addUpdateSpentonIV.getTag());
        bundle.putSerializable(IMAGE_OBJECT, (Serializable) spentonList);

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_SPENTON);

        SelectImageFragment fragment = new SelectImageFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PopupDialogTheme);
        fragment.show(manager, FRAGMENT_SELECT_IMAGE);
    }

    private void getDataFromBundle() {
        spenton = (SpentOnMO) getArguments().get(SELECTED_IMAGE_OBJECT);
        spentonList = (List<SpentOnMO>) getArguments().get(IMAGE_OBJECT);
        user = (UserMO) getArguments().get(LOGGED_IN_OBJECT);
    }

    private void setupPage() {
        //old Spenton
        if(spenton.getSPNT_ON_ID() != null){
            setSpentonImage(spenton);
            addUpdateSpentonNameET.setText(spenton.getSPNT_ON_NAME());

            tagsSet = FinappleUtility.csvToSet(tagsSet, calendarDbService.getTagOnTagTypeId(user.getUSER_ID(), spenton.getSPNT_ON_ID()).getTAGS());
            setupTagsGrid();

            //if this is a admin Spenton, disable editing of the name
            if(ADMIN_USERID.equalsIgnoreCase(spenton.getUSER_ID())){
                addUpdateSpentonNameET.setClickable(false);
                addUpdateSpentonNameET.setEnabled(false);
            }
        }
        //new Spenton
        else{
            setSpentonImage(spenton);
        }
    }

    public void setSpentonImage(SpentOnMO spenton){
        addUpdateSpentonIV.setBackgroundResource(Integer.parseInt(spenton.getSPNT_ON_IMG()));
        addUpdateSpentonIV.setTag(spenton);
    }

    private void initComps(){
        setFont(addUpdateSpentonRL);
    }

    @OnClick(R.id.addUpdateSpentonTagsTVId)
    public void setupTagsGrid() {
        tagsSet = FinappleUtility.csvToSet(tagsSet, String.valueOf(addUpdateSpentonTagsET.getText()));

        TagsGridViewAdapter adapter = new TagsGridViewAdapter(mContext, tagsSet, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagsSet.remove(view.getTag());
                setupTagsGrid();
            }
        });
        addUpdateSpentonTagsGV.setAdapter(adapter);

        if(tagsSet == null || tagsSet.isEmpty()){
            addUpdateSpentonNoTagsTV.setVisibility(View.VISIBLE);
        }
        else{
            addUpdateSpentonNoTagsTV.setVisibility(View.GONE);
        }

        addUpdateSpentonTagsET.setText("");

        hideKeyboard();
    }

    private void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(addUpdateSpentonTagsET.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    // Empty constructor required for DialogFragment
    public AddUpdateSpentonFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();

        calendarDbService = new CalendarDbService(mContext);
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog d = getDialog();
        if (d != null) {
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
}