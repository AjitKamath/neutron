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
import com.finappl.adapters.RepeatsFragmentListViewAdapter;
import com.finappl.models.AccountsMO;
import com.finappl.models.RepeatMO;

import java.util.List;

import static com.finappl.utils.Constants.ACCOUNT_OBJECT;
import static com.finappl.utils.Constants.REPEAT_OBJECT;
import static com.finappl.utils.Constants.SELECTED_ACCOUNT_OBJECT;
import static com.finappl.utils.Constants.SELECTED_REPEAT_OBJECT;

/**
 * Created by ajit on 21/3/16.
 */
public class RepeatsFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    private RelativeLayout repeatsRL;

    //components
    private ListView repeatsLV;
    //end of components

    private List<RepeatMO> repeatsList;

    private String selectedRepeatStr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.repeats, container);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getrepeatsFromBundle();
        initComps(view);
        setupPage();

        return view;
    }

    private void getrepeatsFromBundle() {
        repeatsList = (List<RepeatMO>) getArguments().get(REPEAT_OBJECT);
        selectedRepeatStr = (String) getArguments().get(SELECTED_REPEAT_OBJECT);
    }

    private void setupPage() {
        RepeatsFragmentListViewAdapter repeatsFragmentListViewAdapter = new RepeatsFragmentListViewAdapter(mContext, repeatsList, selectedRepeatStr);
        repeatsLV.setAdapter(repeatsFragmentListViewAdapter);
    }

    private void initComps(View view){
        repeatsLV = (ListView) view.findViewById(R.id.repeatsContentLVId);

        repeatsLV.setOnItemClickListener(listViewItemClickListener);
    }

    AdapterView.OnItemClickListener listViewItemClickListener;
    {
        listViewItemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TransactionFragment activity = (TransactionFragment) getTargetFragment();
                activity.onFinishDialog(repeatsList.get(position));
                dismiss();
            }
        };
    }


    // Empty constructor required for DialogFragment
    public RepeatsFragment() {}

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
            int width = 450;
            int height = 550;
            d.getWindow().setLayout(width, height);
        }
    }

    public interface DialogResultListener {
        void onFinishUserDialog(String str);
    }
}
