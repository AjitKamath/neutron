package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.adapters.AccountsFragmentListViewAdapter;
import com.finappl.models.AccountsMO;
import com.finappl.models.UserMO;

import java.util.List;

import static com.finappl.utils.Constants.ACCOUNT_OBJECT;
import static com.finappl.utils.Constants.CONFIRM_CLOSE_MESSAGE;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.SELECTED_ACCOUNT_OBJECT;

/**
 * Created by ajit on 21/3/16.
 */
public class CloseConfirmFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    //components
    private LinearLayout closeConfirmLL;
    private TextView closeConfirmMessageTV, closeConfirmOKTV, closeConfirmCancelTV;
    //end of components

    private String messageStr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.close_confirm, container);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getMessageFromBundle();
        initComps(view);
        setupPage();

        return view;
    }

    private void getMessageFromBundle() {
        messageStr = (String) getArguments().get(CONFIRM_CLOSE_MESSAGE);
    }

    private void setupPage() {
        closeConfirmMessageTV.setText(messageStr);
    }

    private void initComps(View view){
        closeConfirmLL = (LinearLayout) view.findViewById(R.id.closeConfirmLLId);
        closeConfirmMessageTV = (TextView) view.findViewById(R.id.closeConfirmMessageTVId);
        closeConfirmOKTV = (TextView) view.findViewById(R.id.closeConfirmOKTVId);
        closeConfirmCancelTV = (TextView) view.findViewById(R.id.closeConfirmCancelTVId);

        closeConfirmOKTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransactionFragment activity = (TransactionFragment) getTargetFragment();
                activity.onFinishDialog();
                dismiss();
            }
        });

        closeConfirmCancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    // Empty constructor required for DialogFragment
    public CloseConfirmFragment() {}

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
            int width = 500;
            int height = 300;
            d.getWindow().setLayout(width, height);
        }
    }
}
