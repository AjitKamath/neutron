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
import com.finappl.adapters.SpentonsFragmentListViewAdapter;
import com.finappl.models.AccountsMO;
import com.finappl.models.SpentOnMO;

import java.util.List;

import static com.finappl.utils.Constants.ACCOUNT_OBJECT;
import static com.finappl.utils.Constants.SELECTED_ACCOUNT_OBJECT;
import static com.finappl.utils.Constants.SELECTED_SPENTON_OBJECT;
import static com.finappl.utils.Constants.SPENTON_OBJECT;

/**
 * Created by ajit on 21/3/16.
 */
public class SpentonsFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    private RelativeLayout accountsRL;

    //components
    private ListView spentonsLV;
    //end of components

    private List<SpentOnMO> spentonsList;

    private String selectedSpentOnStr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.spentons, container);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getSpentOnsFromBundle();
        initComps(view);
        setupPage();

        return view;
    }

    private void getSpentOnsFromBundle() {
        spentonsList = (List<SpentOnMO>) getArguments().get(SPENTON_OBJECT);
        selectedSpentOnStr = (String) getArguments().get(SELECTED_SPENTON_OBJECT);
    }

    private void setupPage() {
        SpentonsFragmentListViewAdapter spentonsFragmentListViewAdapter = new SpentonsFragmentListViewAdapter(mContext, spentonsList, selectedSpentOnStr);
        spentonsLV.setAdapter(spentonsFragmentListViewAdapter);
    }

    private void initComps(View view){
        spentonsLV = (ListView) view.findViewById(R.id.spentonsContentLVId);

        spentonsLV.setOnItemClickListener(listViewItemClickListener);
    }

    AdapterView.OnItemClickListener listViewItemClickListener;
    {
        listViewItemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TransactionFragment activity = (TransactionFragment) getTargetFragment();
                activity.onFinishUserDialog(spentonsList.get(position));
                dismiss();
            }
        };
    }


    // Empty constructor required for DialogFragment
    public SpentonsFragment() {}

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
