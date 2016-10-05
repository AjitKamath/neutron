package com.finappl.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.finappl.R;

/**
 * Created by ajit on 21/3/16.
 */
public class TransferFragment extends DialogFragment implements ImageButton.OnClickListener {

    //components
    private TextView addUpdateYearTV;
    private TextView addUpdateMonthTV;
    private TextView addUpdateSuperScriptTV;
    private TextView addUpdateDayTV;
    private ImageView addUpdateTranBackImg;

    //page content
    private EditText addUpdateTranNameET;
    private EditText addUpdateTranAmtET;
    private Spinner addUpdateCatSpn;
    private Spinner addUpdateAccSpn;
    private RadioButton addUpdateTranExpRadio;
    private RadioButton addUpdateTranIncRadio;
    private RadioGroup addUpdateTranExpIncRadioGrp;
    private Spinner addUpdateSpntOnSpn;
    private EditText addUpdateNoteET;

    private ImageButton addUpdatePageFabIB;

    public interface DialogResultListener {
        void onFinishUserDialog(String resultStr);
    }

    // Empty constructor required for DialogFragment
    public TransferFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transaction, container);

        //header
        /*addUpdateYearTV = (TextView) view.findViewById(R.id.addUpdateYearTVId);
        addUpdateMonthTV = (TextView) view.findViewById(R.id.addUpdateMonthTVId);
        addUpdateSuperScriptTV = (TextView) view.findViewById(R.id.addUpdateSuperScriptTVId);
        addUpdateDayTV = (TextView) view.findViewById(R.id.addUpdateDayTVId);
        addUpdateTranBackImg = (ImageView) view.findViewById(R.id.addUpdateTranBackImgId);*/

        //page content
        addUpdateTranNameET = (EditText) view.findViewById(R.id.addUpdateTranNameETId);
        addUpdateTranAmtET = (EditText) view.findViewById(R.id.addUpdateTranAmtETId);
        addUpdateCatSpn = (Spinner) view.findViewById(R.id.addUpdateCatSpnId);
        addUpdateAccSpn = (Spinner) view.findViewById(R.id.addUpdateAccSpnId);
        addUpdateTranExpRadio = (RadioButton) view.findViewById(R.id.addUpdateTranExpRadioId);
        addUpdateTranIncRadio = (RadioButton) view.findViewById(R.id.addUpdateTranIncRadioId);
        addUpdateTranExpIncRadioGrp = (RadioGroup) view.findViewById(R.id.addUpdateTranExpIncRadioGrpId);
        addUpdateNoteET = (EditText) view.findViewById(R.id.addUpdateNoteETId);

        // = (ImageButton) view.findViewById(R.id.addUpdatePageFabIBId);

        addUpdateTranAmtET.requestFocus();
        addUpdatePageFabIB.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        DialogResultListener activity = (DialogResultListener) getActivity();

        //Do the stuff


        activity.onFinishUserDialog("RESULT OF THE STUFF");
        this.dismiss();
    }
}
