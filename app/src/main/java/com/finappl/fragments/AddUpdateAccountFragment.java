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
import com.finappl.models.AccountMO;
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
import static com.finappl.utils.Constants.FRAGMENT_ADD_UPDATE_ACCOUNT;
import static com.finappl.utils.Constants.FRAGMENT_SELECT_IMAGE;
import static com.finappl.utils.Constants.IMAGE_OBJECT;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.OK;
import static com.finappl.utils.Constants.SELECTED_IMAGE_OBJECT;
import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 21/3/16.
 */
public class AddUpdateAccountFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    /*components*/
    @InjectView(R.id.addUpdateAccountRLId)
    RelativeLayout addUpdateAccountRL;

    @InjectView(R.id.addUpdateAccountIVId)
    ImageView addUpdateAccountIV;

    @InjectView(R.id.addUpdateAccountNameETId)
    EditText addUpdateAccountNameET;

    @InjectView(R.id.addUpdateAccountTagsETId)
    EditText addUpdateAccountTagsET;

    @InjectView(R.id.addUpdateAccountTagsGVId)
    GridView addUpdateAccountTagsGV;

    @InjectView(R.id.addUpdateAccountNoTagsTVId)
    TextView addUpdateAccountNoTagsTV;
    /*components*/

    /*Data*/
    private AccountMO account;
    private List<AccountMO> accountsList;
    private Set<String> tagsSet;
    private UserMO user;

    /*Database*/
    private CalendarDbService calendarDbService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_update_account, container);
        ButterKnife.inject(this, view);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getDataFromBundle();
        initComps();
        setupPage();

        return view;
    }

    @OnClick(R.id.addUpdateAccountHeaderCloseTVId)
    public void close(){
        dismiss();
    }

    @OnClick(R.id.addUpdateAccountHeaderSaveTVId)
    public void onAddUpdate(){
        AccountMO tempAccount = validateAndGetInputs();

        if(tempAccount == null){
            return;
        }

        String messageStr;

        //old account
        if(tempAccount.getACC_ID() != null){
            if (calendarDbService.updateAccount(tempAccount)) {
                messageStr = "Account updated";
            } else {
                messageStr = "Failed to update Account";
            }
        }
        else{
            //check if the account already exists
            if(calendarDbService.isAccountAlreadyExist(user.getUSER_ID(), tempAccount.getACC_NAME())){
                FinappleUtility.showSnacks(addUpdateAccountRL, "Account already exists", OK, Snackbar.LENGTH_LONG);
                return;
            }

            tempAccount.setACC_ID(IdGenerator.getInstance().generateUniqueId("ACC"));
            tempAccount.setUSER_ID(user.getUSER_ID());
            long result = calendarDbService.addNewAccount(tempAccount);

            if (result == -1) {
                messageStr = "Failed to create a new Account !";
            } else {
                messageStr = "New Account created";
            }
        }

        close();
        ((AccountsFragment)getTargetFragment()).onFragmentClose(messageStr);
    }

    private AccountMO validateAndGetInputs() {
        String accountNameStr = String.valueOf(addUpdateAccountNameET.getText());
        String accountImageStr = String.valueOf(((AccountMO)addUpdateAccountIV.getTag()).getACC_IMG());

        if(accountNameStr != null && !accountNameStr.trim().isEmpty()){
            accountNameStr = accountNameStr.trim();
        }
        else{
            FinappleUtility.showSnacks(addUpdateAccountRL, "Category Title is empty", OK, Snackbar.LENGTH_LONG);
            return null;
        }

        account.setACC_NAME(accountNameStr);
        account.setACC_IMG(accountImageStr);
        account.setTagsStr(FinappleUtility.setToCSV(tagsSet));

        return account;
    }

    @OnClick(R.id.addUpdateAccountIVId)
    public void showSelectImage(){
        //if the account is admins category then do not allow to select image
        if(ADMIN_USERID.equalsIgnoreCase(account.getUSER_ID())){
            return;
        }

        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_SELECT_IMAGE);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(SELECTED_IMAGE_OBJECT, (Serializable) addUpdateAccountIV.getTag());
        bundle.putSerializable(IMAGE_OBJECT, (Serializable) accountsList);

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_ACCOUNT);

        SelectImageFragment fragment = new SelectImageFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_SELECT_IMAGE);
    }

    private void getDataFromBundle() {
        account = (AccountMO) getArguments().get(SELECTED_IMAGE_OBJECT);
        accountsList = (List<AccountMO>) getArguments().get(IMAGE_OBJECT);
        user = (UserMO) getArguments().get(LOGGED_IN_OBJECT);
    }

    private void setupPage() {
        //old account
        if(account.getACC_ID() != null){
            setAccountImage(account);
            addUpdateAccountNameET.setText(account.getACC_NAME());

            tagsSet = FinappleUtility.csvToSet(tagsSet, calendarDbService.getTagOnTagTypeId(user.getUSER_ID(), account.getACC_ID()).getTAGS());
            setupTagsGrid();

            //if this is a admin account, disable editing of the name
            if(ADMIN_USERID.equalsIgnoreCase(account.getUSER_ID())){
                addUpdateAccountNameET.setClickable(false);
                addUpdateAccountNameET.setEnabled(false);
            }
        }
        //new category
        else{
            setAccountImage(account);
        }
    }

    public void setAccountImage(AccountMO account){
        addUpdateAccountIV.setBackgroundResource(Integer.parseInt(account.getACC_IMG()));
        addUpdateAccountIV.setTag(account);
    }

    private void initComps(){
        setFont(addUpdateAccountRL);
    }

    @OnClick(R.id.addUpdateAccountTagsTVId)
    public void setupTagsGrid() {
        tagsSet = FinappleUtility.csvToSet(tagsSet, String.valueOf(addUpdateAccountTagsET.getText()));

        TagsGridViewAdapter adapter = new TagsGridViewAdapter(mContext, tagsSet, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagsSet.remove(view.getTag());
                setupTagsGrid();
            }
        });
        addUpdateAccountTagsGV.setAdapter(adapter);

        if(tagsSet == null || tagsSet.isEmpty()){
            addUpdateAccountNoTagsTV.setVisibility(View.VISIBLE);
        }
        else{
            addUpdateAccountNoTagsTV.setVisibility(View.GONE);
        }

        addUpdateAccountTagsET.setText("");

        hideKeyboard();
    }

    private void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(addUpdateAccountTagsET.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    // Empty constructor required for DialogFragment
    public AddUpdateAccountFragment() {}

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
