package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.R;
import com.finappl.dbServices.TransfersDbService;
import com.finappl.models.TransferMO;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static android.view.View.GONE;
import static com.finappl.utils.Constants.CONFIRM_MESSAGE;
import static com.finappl.utils.Constants.FRAGMENT_CONFIRM;
import static com.finappl.utils.Constants.FRAGMENT_TRANSFER;
import static com.finappl.utils.Constants.FRAGMENT_TRANSFER_DETAILS;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.TRANSFER_OBJECT;
import static com.finappl.utils.Constants.UI_DATE_FORMAT_SDF;
import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 21/3/16.
 */
public class TransferDetailsFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    //components
    @InjectView(R.id.transferDetailsLLId)
    LinearLayout transferDetailsLL;

    @InjectView(R.id.transferDetailsDateTVId)
    TextView transferDetailsDateTV;

    @InjectView(R.id.transferDetailsAccountFromIVId)
    ImageView transferDetailsAccountFromIV;

    @InjectView(R.id.transferDetailsAccountFromTVId)
    TextView transferDetailsAccountFromTV;

    @InjectView(R.id.transferDetailsAmountCurrencyCodeTVId)
    TextView transferDetailsAmountCurrencyCodeTV;

    @InjectView(R.id.transferDetailsAmountTVId)
    TextView transferDetailsAmountTV;

    @InjectView(R.id.transferDetailsAccountToIVId)
    ImageView transferDetailsAccountToIV;

    @InjectView(R.id.transferDetailsAccountToTVId)
    TextView transferDetailsAccountToTV;

    @InjectView(R.id.transferDetailsRepeatTVId)
    TextView transferDetailsRepeatTV;

    @InjectView(R.id.transferDetailsScheduleTVId)
    TextView transferDetailsScheduleTV;

    @InjectView(R.id.transferDetailsNotifyAddTVId)
    TextView transferDetailsNotifyAddTV;

    @InjectView(R.id.transferDetailsNotifyAddTimeTVId)
    TextView transferDetailsNotifyAddTimeTV;

    @InjectView(R.id.transferDetailsNoteTVId)
    TextView transferDetailsNoteTV;

    @InjectView(R.id.transferDetailsRepeatLLId)
    LinearLayout transferDetailsRepeatLL;

    @InjectView(R.id.transferDetailsNoteLLId)
    LinearLayout transferDetailsNoteLL;
    //end of components

    //db services
    private TransfersDbService transfersDbService;

    private TransferMO transfer;
    private UserMO loggedInUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transfer_details, container);
        ButterKnife.inject(this, view);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getDataFromBundle();
        initComps();
        setupPage();

        return view;
    }

    private void getDataFromBundle() {
        transfer = (TransferMO) getArguments().get(TRANSFER_OBJECT);
        loggedInUser = (UserMO) getArguments().get(LOGGED_IN_OBJECT);
    }

    private void setupPage() {
        if(transfer == null){
            Log.e(CLASS_NAME, "Disaster !!! transfer object is null");
            showToast("Catastrophic Error !!");
            return;
        }
        else if(transfer.getTRNFR_ID() == null || transfer.getTRNFR_ID().trim().isEmpty()){
            Log.e(CLASS_NAME, "Disaster !!! TRNFR_ID is null/empty");
            showToast("Catastrophic Error !!");
            return;
        }

        transferDetailsDateTV.setText(UI_DATE_FORMAT_SDF.format(transfer.getTRNFR_DATE()));
        transferDetailsAccountFromIV.setBackgroundResource(Integer.parseInt(transfer.getFromAccImg()));
        transferDetailsAccountFromTV.setText(transfer.getFromAccName());
        transferDetailsAccountToIV.setBackgroundResource(Integer.parseInt(transfer.getToAccImg()));
        transferDetailsAccountToTV.setText(transfer.getToAccName());
        transferDetailsAmountCurrencyCodeTV.setText(loggedInUser.getCUR_CODE());

        transferDetailsAmountTV = FinappleUtility.formatAmountView(transferDetailsAmountTV, loggedInUser, transfer.getTRNFR_AMT());
        transferDetailsAmountTV.setTextColor(getResources().getColor(R.color.finappleCurrencyPosColor));

        //set up repeat
        if(transfer.getREPEAT_ID() == null || transfer.getREPEAT_ID().isEmpty()){
            transferDetailsRepeatLL.setVisibility(GONE);
        }
        else{
            transferDetailsRepeatTV.setText(transfer.getRepeat());
            transferDetailsScheduleTV.setText(transfer.getSCHD_UPTO_DATE());

            if("AUTO".equalsIgnoreCase(transfer.getNOTIFY())){
                transferDetailsNotifyAddTV.setText("AUTO ADD");
            }
            else{
                transferDetailsNotifyAddTV.setText("NOTIFY & ADD");
            }

            transferDetailsNotifyAddTimeTV.setText(transfer.getNOTIFY_TIME());
        }

        //set up notes
        if(transfer.getTRNFR_NOTE() == null || transfer.getTRNFR_NOTE().trim().isEmpty()){
            transferDetailsNoteLL.setVisibility(GONE);
        }
        else{
            transferDetailsNoteTV.setText(transfer.getTRNFR_NOTE());
        }
    }

    @OnClick(R.id.transferDetailsEditTVId)
    public void onEdit(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_TRANSFER);

        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(TRANSFER_OBJECT, transfer);
        bundle.putSerializable(LOGGED_IN_OBJECT, loggedInUser);

        TransferFragment fragment = new TransferFragment();
        fragment.setArguments(bundle);
        fragment.show(manager, FRAGMENT_TRANSFER);

        //dismiss current fragment
        dismiss();
    }

    private void initComps(){
        setFont(transferDetailsLL);
    }

    @OnClick(R.id.transferDetailsDeleteTVId)
    public void confirmDelete(){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_CONFIRM);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(CONFIRM_MESSAGE, "Delete Transfer ?");

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_TRANSFER_DETAILS);

        ConfirmFragment confirmFragment = new ConfirmFragment();
        confirmFragment.setArguments(bundle);
        confirmFragment.setTargetFragment(currentFrag, 0);
        confirmFragment.show(manager, FRAGMENT_CONFIRM);
    }

    protected void showToast(String string){
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
    }

    // Empty constructor required for DialogFragment
    public TransferDetailsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();

        transfersDbService = new TransfersDbService(mContext);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
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

    private void closeFragment(String messageStr){
        dismiss();
    }

    public void onFinishDialog(String messageStr) {
        if(!transfersDbService.deleteTransfer(transfer.getTRNFR_ID())){
            messageStr = "Could not delete the Transfer";
        }

        closeFragment(messageStr);
    }
}