package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.activities.CalendarActivity;
import com.finappl.adapters.CategoriesFragmentListViewAdapter;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.dbServices.CalendarDbService;
import com.finappl.dbServices.TransactionsDbService;
import com.finappl.models.AccountsMO;
import com.finappl.models.CategoryMO;
import com.finappl.models.SpentOnMO;
import com.finappl.models.SpinnerModel;
import com.finappl.models.TransactionModel;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;
import com.finappl.utils.IdGenerator;

import java.io.Serializable;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import static com.finappl.utils.Constants.CATEGORY_OBJECT;
import static com.finappl.utils.Constants.FRAGMENT_CATEGORY;
import static com.finappl.utils.Constants.FRAGMENT_TRANSACTION;
import static com.finappl.utils.Constants.SELECTED_CATEGORY_OBJECT;
import static com.finappl.utils.Constants.TRANSACTION_OBJECT;
import static com.finappl.utils.Constants.UI_DATE_FORMAT_SDF;

/**
 * Created by ajit on 21/3/16.
 */
public class CategoriesFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    private RelativeLayout categoriesRL;

    //components
    private ListView categoriesLV;

    //end of components

    private Map<String, CategoryMO> categoriesMap;



    private TextView addUpdateDateTV;
    private ImageView addUpdateTranBackImg;
    private EditText addUpdateTranNameET;
    private EditText addUpdateTranAmtET;
    private Spinner addUpdateCatSpn;
    private Spinner addUpdateAccSpn;
    private RadioButton addUpdateTranExpRadio;
    private RadioButton addUpdateTranIncRadio;
    private RadioGroup addUpdateTranExpIncRadioGrp;
    private Spinner addUpdateSpntOnSpn;
    private EditText addUpdateNoteET;
    private ImageView transactionSaveIV;
    private CheckBox transactionSchedueCB;
    private LinearLayout transactionSchedLL;

    private UserMO loggedInUserObj;

    private CalendarDbService calendarDbService;
    private TransactionsDbService transactionsDbService;
    private AuthorizationDbService authorizationDbService;

    private List<AccountsMO> accountList;
    private List<SpentOnMO> spentOnList;

    private TransactionModel transactionModelObj;
    private String selectedCategoryStr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.categories, container);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getCategoriesFromBundle();
        initComps(view);
        setupPage();

        return view;
    }

    public void getInputs(){
        try{
            transactionModelObj.setTRAN_DATE(UI_DATE_FORMAT_SDF.parse(String.valueOf(addUpdateDateTV.getText())));
        }
        catch (ParseException pe){
            Log.e(CLASS_NAME, "Parse Exception "+pe);
            return ;
        }

        transactionModelObj.setTRAN_AMT(Double.parseDouble(String.valueOf(addUpdateTranAmtET.getText())));
        transactionModelObj.setTRAN_NAME(String.valueOf(addUpdateTranNameET.getText()));
        transactionModelObj.setCAT_ID(String.valueOf(addUpdateCatSpn.getSelectedView().getTag()));
        transactionModelObj.setCategory(((SpinnerModel) addUpdateCatSpn.getSelectedItem()).getItemName());
        transactionModelObj.setACC_ID(String.valueOf(addUpdateAccSpn.getSelectedView().getTag()));
        transactionModelObj.setAccount(((SpinnerModel) addUpdateAccSpn.getSelectedItem()).getItemName());
        transactionModelObj.setTRAN_TYPE(String.valueOf(getView().findViewById(addUpdateTranExpIncRadioGrp.getCheckedRadioButtonId()).getTag()));
        transactionModelObj.setSPNT_ON_ID(String.valueOf(addUpdateSpntOnSpn.getSelectedView().getTag()));
        transactionModelObj.setSpentOn(((SpinnerModel) addUpdateSpntOnSpn.getSelectedItem()).getItemName());
        transactionModelObj.setTRAN_NOTE(String.valueOf(addUpdateNoteET.getText()));
        transactionModelObj.setUSER_ID(loggedInUserObj.getUSER_ID());
    }

    private void getCategoriesFromBundle() {
        categoriesMap = (Map<String, CategoryMO>) getArguments().get(CATEGORY_OBJECT);
        selectedCategoryStr = (String) getArguments().get(SELECTED_CATEGORY_OBJECT);
    }

    private void setupPage() {
        CategoriesFragmentListViewAdapter categoriesFragmentListViewAdapter = new CategoriesFragmentListViewAdapter(mContext, categoriesMap, selectedCategoryStr);
        categoriesLV.setAdapter(categoriesFragmentListViewAdapter);
    }

    private void initComps(View view){
        categoriesLV = (ListView) view.findViewById(R.id.categoriesContentLVId);

        categoriesLV.setOnItemClickListener(listViewItemClickListener);
    }

    AdapterView.OnItemClickListener listViewItemClickListener;
    {
        listViewItemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(CLASS_NAME, "Test");
            }
        };
    }


    // Empty constructor required for DialogFragment
    public CategoriesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null) {
            int width = R.integer.fragment_options_full_width;
            int height = R.integer.fragment_options_full_height;
            d.getWindow().setLayout(width, height);
        }
    }

    public interface DialogResultListener {
        void onFinishUserDialog(String resultStr);
    }
}
