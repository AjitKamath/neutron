package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.finappl.R;
import com.finappl.adapters.AccountsFragmentListViewAdapter;
import com.finappl.adapters.CategoriesFragmentListViewAdapter;
import com.finappl.models.AccountsMO;
import com.finappl.models.CategoryMO;

import java.util.List;

import static com.finappl.utils.Constants.ACCOUNT_OBJECT;
import static com.finappl.utils.Constants.CATEGORY_OBJECT;
import static com.finappl.utils.Constants.SELECTED_ACCOUNT_OBJECT;
import static com.finappl.utils.Constants.SELECTED_CATEGORY_OBJECT;

/**
 * Created by ajit on 21/3/16.
 */
public class AccountsFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    //components
    private ListView accountsLV;
    //end of components

    private List<AccountsMO> accountsList;

    private String selectedAccountStr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.accounts, container);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getAccountsFromBundle();
        initComps(view);
        setupPage();

        return view;
    }

    private void getAccountsFromBundle() {
        accountsList = (List<AccountsMO>) getArguments().get(ACCOUNT_OBJECT);
        selectedAccountStr = (String) getArguments().get(SELECTED_ACCOUNT_OBJECT);
    }

    private void setupPage() {
        AccountsFragmentListViewAdapter accountsFragmentListViewAdapter = new AccountsFragmentListViewAdapter(mContext, accountsList, selectedAccountStr);
        accountsLV.setAdapter(accountsFragmentListViewAdapter);
    }

    private void initComps(View view){
        accountsLV = (ListView) view.findViewById(R.id.accountsContentLVId);

        accountsLV.setOnItemClickListener(listViewItemClickListener);
    }

    AdapterView.OnItemClickListener listViewItemClickListener;
    {
        listViewItemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TransactionFragment activity = (TransactionFragment) getTargetFragment();
                activity.onFinishDialog(accountsList.get(position));
                dismiss();
            }
        };
    }


    // Empty constructor required for DialogFragment
    public AccountsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog d = getDialog();
        if (d!=null) {
            int width = 600;
            int height = 800;
            d.getWindow().setLayout(width, height);
        }
    }

    public interface DialogResultListener {
        void onFinishUserDialog(String str);
    }
}
