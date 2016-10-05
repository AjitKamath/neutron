package com.finappl.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.adapters.ViewTransactionSectionListAdapter;
import com.finappl.adapters.ViewTransferSectionListAdapter;
import com.finappl.R;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.dbServices.ViewActivitiesDbService;
import com.finappl.models.ActivityModel;
import com.finappl.models.DayTransactionsModel;
import com.finappl.models.DayTransfersModel;
import com.finappl.models.TransactionModel;
import com.finappl.models.TransferModel;
import com.finappl.models.UserMO;
import com.finappl.models.UsersModel;
import com.finappl.utils.FinappleUtility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

public class ViewActivitiesActivity extends Activity {
	
	private final String CLASS_NAME = this.getClass().getName();

    Context mContext = this;

    //No Activity
    private TextView viewActsNoTransactionsTV, viewActsNoTransfersTV;

    //Lists
    private ListView viewTransactsLV, viewTransfersLV;

    //Activity Object
    private ActivityModel activityModelObj;

    //db service
    private ViewActivitiesDbService viewActivitiesDbService = new ViewActivitiesDbService(mContext);
    private AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);

    //User
    private UserMO loggedInUserObj;

    //dialogs
    private Dialog messageDialog, detailsDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activities_view);

        //get the Active user
        loggedInUserObj = authorizationDbService.getActiveUser(FinappleUtility.getInstance().getActiveUserId(mContext));
        if(loggedInUserObj == null){
            return;
        }

        //get data from intent
        getIntentData();

        //initialize UI components
        initUIComponents();

        //setUpTabs
        setUpTabs();

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup)findViewById(R.id.viewActsRLId), robotoCondensedLightFont);
	}

    private void setUpTabs() {
        if(activityModelObj == null){
            Log.e(CLASS_NAME, "ACTIVITY_OBJ is null in the intent");
            return;
        }

        //LinearLayout viewActsTransactTabLL = (LinearLayout) this.findViewById(R.id.viewActsTransactTabLLId);
        LinearLayout viewActsTransferTabLL = (LinearLayout) this.findViewById(R.id.viewActsTransferTabLLId);

        TextView viewActsTransactTabTV = (TextView) this.findViewById(R.id.viewActsTransactTabTVId);
        TextView viewActsTransferTabTV = (TextView) this.findViewById(R.id.viewActsTransferTabTVId);

        LinearLayout viewActsTransactionsLL = (LinearLayout) this.findViewById(R.id.viewActsTransactionsLLId);
        LinearLayout viewActsTransfersLL = (LinearLayout) this.findViewById(R.id.viewActsTransfersLLId);

        //depending on whichActivityStr in the ACTIVITY_OBJ, set the active tab
        if("TRANSACTIONS".equalsIgnoreCase(activityModelObj.getWhichActivityStr())){
            deselectAllTabs();
            viewActsTransactTabTV.setBackgroundResource(R.drawable.view_activities_active_tab_inner);
            viewActsTransactTabTV.setTextColor(viewActsTransactTabTV.getResources().getColor(R.color.white));
            viewActsTransactionsLL.setVisibility(View.VISIBLE);
        }
        else{
            deselectAllTabs();
            viewActsTransferTabTV.setBackgroundResource(R.drawable.view_activities_active_tab_inner);
            viewActsTransferTabTV.setTextColor(viewActsTransferTabTV.getResources().getColor(R.color.white));
            viewActsTransfersLL.setVisibility(View.VISIBLE);
        }

        //add user id
        activityModelObj.setUserId(loggedInUserObj.getUSER_ID());

        //get both transactions and transfers for the particular date or date range
        ActivityModel  activityObj = viewActivitiesDbService.getActivitesForDate(activityModelObj);
        Map<String, DayTransactionsModel> rangeTransactionsMap = activityObj.getTransactionsMap();
        Map<String, DayTransfersModel> rangeTransfersMap = activityObj.getTransfersMap();

        //setup transaction list view
        ViewTransactionSectionListAdapter viewTransactionListAdapter =
                    new ViewTransactionSectionListAdapter(mContext, R.layout.activities_transactions_view_list_view_header, R.layout.activities_transactions_view_list_view_content, rangeTransactionsMap);
        viewTransactionListAdapter.notifyDataSetChanged();
        viewTransactsLV.setAdapter(viewTransactionListAdapter);


        //setup transfer list view
        ViewTransferSectionListAdapter viewTransferListAdapter =
                    new ViewTransferSectionListAdapter(mContext, R.layout.activities_transfers_view_list_view_header, R.layout.activities_transfers_view_list_view_content, rangeTransfersMap);
        viewTransfersLV.setAdapter(viewTransferListAdapter);
        viewTransferListAdapter.notifyDataSetChanged();

        //set the no activity message as visible
        if(viewTransactionListAdapter.isEmpty()){
            viewActsNoTransactionsTV.setVisibility(View.VISIBLE);
            viewTransactsLV.setVisibility(View.GONE);

        }
        else{
            viewActsNoTransactionsTV.setVisibility(View.GONE);
            viewTransactsLV.setVisibility(View.VISIBLE);
        }

        //set the no activity message as visible
        if(viewTransferListAdapter.isEmpty()){
            viewActsNoTransfersTV.setVisibility(View.VISIBLE);
            viewTransfersLV.setVisibility(View.GONE);

        }
        else{
            viewActsNoTransfersTV.setVisibility(View.GONE);
            viewTransfersLV.setVisibility(View.VISIBLE);
        }

        //set counters of transaction and transfers
        TextView viewActsTransactTabCounterTV = (TextView) this.findViewById(R.id.viewActsTransactTabCounterTVId);
        TextView viewActsTransferTabCounterTV = (TextView) this.findViewById(R.id.viewActsTranferTabCounterTVId);

        int transactCounter = 0, transferCounter = 0;
        for(Map.Entry<String, DayTransactionsModel> iterMap : rangeTransactionsMap.entrySet()){
            transactCounter += iterMap.getValue().getDayTransactionsList().size();
        }
        for(Map.Entry<String, DayTransfersModel> iterMap : rangeTransfersMap.entrySet()){
            transferCounter += iterMap.getValue().getDayTransfersList().size();
        }

        viewActsTransactTabCounterTV.setText(String.valueOf(transactCounter));
        viewActsTransferTabCounterTV.setText(String.valueOf(transferCounter));
    }

    public void onTabSelect(View view){
        Log.i(CLASS_NAME, "Son....i created you. The tab implementation...although sucks, a very own of my creation. And you work !! selected: "+view.getId());

        //LinearLayout viewActsTransactTabLL = (LinearLayout) this.findViewById(R.id.viewActsTransactTabLLId);
        LinearLayout viewActsTransferTabLL = (LinearLayout) this.findViewById(R.id.viewActsTransferTabLLId);

        TextView viewActsTransactTabTV = (TextView) this.findViewById(R.id.viewActsTransactTabTVId);
        TextView viewActsTransferTabTV = (TextView) this.findViewById(R.id.viewActsTransferTabTVId);

        LinearLayout viewActsTransactionsLL = (LinearLayout) this.findViewById(R.id.viewActsTransactionsLLId);
        LinearLayout viewActsTransfersLL = (LinearLayout) this.findViewById(R.id.viewActsTransfersLLId);

        switch(view.getId()){
            case R.id.viewActsTransactTabTVId: deselectAllTabs();
                viewActsTransactTabTV.setBackgroundResource(R.drawable.view_activities_active_tab_inner);
                viewActsTransactTabTV.setTextColor(viewActsTransactTabTV.getResources().getColor(R.color.white));
                viewActsTransactionsLL.setVisibility(View.VISIBLE);
                break;

            case R.id.viewActsTransferTabTVId : deselectAllTabs();
                viewActsTransferTabTV.setBackgroundResource(R.drawable.view_activities_active_tab_inner);
                viewActsTransferTabTV.setTextColor(viewActsTransferTabTV.getResources().getColor(R.color.white));
                viewActsTransfersLL.setVisibility(View.VISIBLE);
                break;

            default: showToast("Tab Error !!");
        }
    }

    private void deselectAllTabs() {
        //tabs
        //LinearLayout viewActsTransactTabLL = (LinearLayout) this.findViewById(R.id.viewActsTransactTabLLId);
        LinearLayout viewActsTransferTabLL = (LinearLayout) this.findViewById(R.id.viewActsTransferTabLLId);

        //tab text
        TextView viewActsTransactTabTV = (TextView) this.findViewById(R.id.viewActsTransactTabTVId);
        TextView viewActsTransferTabTV = (TextView) this.findViewById(R.id.viewActsTransferTabTVId);

        //tab content
        LinearLayout viewActsTransactionsLL = (LinearLayout) this.findViewById(R.id.viewActsTransactionsLLId);
        LinearLayout viewActsTransfersLL = (LinearLayout) this.findViewById(R.id.viewActsTransfersLLId);

        viewActsTransactTabTV.setBackgroundResource(R.drawable.view_activities_inactive_tab_inner);
        viewActsTransferTabTV.setBackgroundResource(R.drawable.view_activities_inactive_tab_inner);

        viewActsTransactTabTV.setTextColor(viewActsTransactTabTV.getResources().getColor(R.color.finappleTheme));
        viewActsTransferTabTV.setTextColor(viewActsTransferTabTV.getResources().getColor(R.color.finappleTheme));

        viewActsTransactionsLL.setVisibility(View.GONE);
        viewActsTransfersLL.setVisibility(View.GONE);
    }

    private void getIntentData() {
        if(!getIntent().getExtras().containsKey("ACTIVITY_OBJ")){
            Log.e(CLASS_NAME, "Expensive error !! coud not find ACTIVITY_OBJ in intent !!");
            return;
        }
        activityModelObj = (ActivityModel)getIntent().getExtras().getSerializable("ACTIVITY_OBJ");
    }

    public void backToHome(View view){
        Intent intent = new Intent(ViewActivitiesActivity.this, CalendarActivity.class);
        startActivity(intent);
        finish();
    }

    private void initUIComponents() {
        //No Activity
        viewActsNoTransactionsTV = (TextView) this.findViewById(R.id.viewActsNoTransactsTVId);
        viewActsNoTransfersTV = (TextView) this.findViewById(R.id.viewActsNoTransfersTVId);

        //Lists
        viewTransactsLV = (ListView) this.findViewById(R.id.viewTransactsLVId);
        viewTransfersLV = (ListView) this.findViewById(R.id.viewTransfersLVId);

        //add click listener
        viewTransactsLV.setOnItemClickListener(listViewClickListener);
        viewTransfersLV.setOnItemClickListener(listViewClickListener);
    }

    public void showMessagePopper(View view){
        // Create custom message popper object
        messageDialog = new Dialog(ViewActivitiesActivity.this);
        messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        messageDialog.setContentView(R.layout.message_popper);

        messageDialog.show();

        //buttons
        LinearLayout msgPoprPosLL, msgPoprNegLL;
        msgPoprPosLL = (LinearLayout) messageDialog.findViewById(R.id.msgPoprPosLLId);
        msgPoprNegLL = (LinearLayout) messageDialog.findViewById(R.id.msgPoprNegLLId);

        //set listeners for the buttons
        msgPoprPosLL.setOnClickListener(linearLayoutClickListener);
        msgPoprNegLL.setOnClickListener(linearLayoutClickListener);

        //validation
        if(view.getTag() == null){
            Log.e(CLASS_NAME, "ERROR !! Tag is null");
            return;
        }

        //set positive buttons tag as tran id for deleting
        msgPoprPosLL.setTag(view.getTag());

        //texts
        TextView msgPoprNegTV, msgPoprPosTV, msgPoprMsgTV;
        msgPoprNegTV = (TextView) messageDialog.findViewById(R.id.msgPoprNegTVId);
        msgPoprPosTV = (TextView) messageDialog.findViewById(R.id.msgPoprPosTVId);
        msgPoprMsgTV = (TextView) messageDialog.findViewById(R.id.msgPoprMsgTVId);

        if(view.getTag().toString().contains("TRAN")){
            msgPoprMsgTV.setText("Delete this Transaction ?");
        }
        else if(view.getTag().toString().contains("TRNSFR")){
            msgPoprMsgTV.setText("Delete this Transfer ?");
        }
        else{
            Log.e(CLASS_NAME, "Boy !! let me tell you something. Man-Man. I'm expecting either a transaction ID or a Transfer ID. Nothing else. NOT JUNK !!!!");
            return;
        }

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) messageDialog.findViewById(R.id.msgPoprLLId), robotoCondensedLightFont);
    }

    //---------------------------------List View Click Listener------------------------------------------
    ListView.OnItemClickListener listViewClickListener;
    {
        listViewClickListener = new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(CLASS_NAME, "MASTER !! you click hath " + view.getTag() + ". You shalt be giventh with the transaction details popper");

                if(view.getTag() == null){
                    return;
                }

                if(view.getTag().toString().contains("TRAN")){
                    TransactionModel transObj = viewActivitiesDbService.getTransactionOnTransactionID(view.getTag().toString());
                    if(transObj != null){
                        showTransactionDetailsPopper(transObj);
                    }
                }
                else if(view.getTag().toString().contains("TRNSFR")){
                    TransferModel transferObj = viewActivitiesDbService.getTransferOnTransferID(view.getTag().toString());

                    if(transferObj != null){
                        showTransferDetailsPopper(transferObj);
                    }
                }
                else{
                    Intent intent = new Intent(ViewActivitiesActivity.this, JimBrokeItActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    private void showTransactionDetailsPopper(TransactionModel transObj) {
        detailsDialog = new Dialog(ViewActivitiesActivity.this);
        detailsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        detailsDialog.setContentView(R.layout.transaction_view_details_popper);

        detailsDialog.show();

        //Dialogue UI items
        LinearLayout tranDtlPopperSchTranLL, tranDtlPopperCatTattooLL, tranDtlPopperNoteLL, tranDtlPopperContentLL;
        tranDtlPopperSchTranLL = (LinearLayout) detailsDialog.findViewById(R.id.tranDtlPopperSchTranLLId);
        tranDtlPopperNoteLL = (LinearLayout) detailsDialog.findViewById(R.id.tranDtlPopperNoteLLId);

        //Buttons
        ImageView tranDtlPopperDelIV, tranDtlPopperEditIV;
        tranDtlPopperDelIV = (ImageView) detailsDialog.findViewById(R.id.tranDtlPopperDelIVId);
        tranDtlPopperEditIV = (ImageView) detailsDialog.findViewById(R.id.tranDtlPopperEditIVId);

        tranDtlPopperDelIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessagePopper(v);
            }
        });
        tranDtlPopperEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toEditTransaction((String) v.getTag());
                if(detailsDialog != null){
                    detailsDialog.dismiss();
                }
            }
        });

        //texts
        TextView tranDtlPopperTranNameTV, tranDtlPopperCatTV, tranDtlPopperAccTV, tranDtlPopperSpntOnTV, tranDtlPopperNoteTV, tranDtlPopperTranTypTV,
                    tranDtlPopperAmtTV, tranDtlPopperCreateTV, tranDtlPopperUpdtdTV, tranDtlPopperTranDateTV ;

        //tranDtlPopperTranTypTV = (TextView) detailsDialog.findViewById(R.id.tranDtlPopperTranTypTVId);
        tranDtlPopperTranDateTV = (TextView) detailsDialog.findViewById(R.id.tranDtlPopperTranDateTVId);
        tranDtlPopperAmtTV = (TextView) detailsDialog.findViewById(R.id.tranDtlPopperAmtTVId);
        tranDtlPopperTranNameTV = (TextView) detailsDialog.findViewById(R.id.tranDtlPopperTranNameTVId);
        tranDtlPopperCatTV = (TextView) detailsDialog.findViewById(R.id.tranDtlPopperCatTVId);
        tranDtlPopperAccTV = (TextView) detailsDialog.findViewById(R.id.tranDtlPopperAccTVId);
        tranDtlPopperSpntOnTV = (TextView) detailsDialog.findViewById(R.id.tranDtlPopperSpntOnTVId);
        tranDtlPopperNoteTV = (TextView) detailsDialog.findViewById(R.id.tranDtlPopperNoteTVId);
        tranDtlPopperCreateTV = (TextView) detailsDialog.findViewById(R.id.tranDtlPopperCreateTVId);
        tranDtlPopperUpdtdTV = (TextView) detailsDialog.findViewById(R.id.tranDtlPopperUpdtdTVId);

        if("INCOME".equalsIgnoreCase(transObj.getTRAN_TYPE())){
            tranDtlPopperAmtTV.setTextColor(tranDtlPopperAmtTV.getResources().getColor(R.color.finappleCurrencyPosColor));
            //tranDtlPopperTranTypTV.setBackground(tranDtlPopperTranTypTV.getResources().getDrawable(R.drawable.circle_income_text_view, null));
            //tranDtlPopperTranTypTV.setText("I");
            tranDtlPopperAmtTV.setText(String.valueOf(transObj.getTRAN_AMT()));
        }
        else{
            tranDtlPopperAmtTV.setTextColor(tranDtlPopperAmtTV.getResources().getColor(R.color.finappleCurrencyNegColor));
            //tranDtlPopperTranTypTV.setBackground(tranDtlPopperTranTypTV.getResources().getDrawable(R.drawable.circle_expense_text_view, null));
            //tranDtlPopperTranTypTV.setText("E");
            tranDtlPopperAmtTV.setText(String.valueOf("-"+transObj.getTRAN_AMT()));
        }

        SimpleDateFormat goodFormat1 = new SimpleDateFormat("d MMM ''yy, h:mm a");
        SimpleDateFormat goodFormat2 = new SimpleDateFormat("d MMM ''yy");

        //set values
        tranDtlPopperTranNameTV.setText(transObj.getTRAN_NAME());
        tranDtlPopperCatTV.setText(transObj.getCategory());
        tranDtlPopperAccTV.setText(transObj.getAccount());
        tranDtlPopperSpntOnTV.setText(transObj.getSpentOn());
        tranDtlPopperNoteTV.setText(transObj.getTRAN_NOTE());

        tranDtlPopperTranDateTV.setText(goodFormat2.format(transObj.getTRAN_DATE()));
        tranDtlPopperCreateTV.setText(goodFormat1.format(transObj.getCREAT_DTM()));

        if(transObj.getMOD_DTM() != null){
            tranDtlPopperUpdtdTV.setText(goodFormat2.format(transObj.getMOD_DTM()));
        }

        tranDtlPopperDelIV.setTag(transObj.getTRAN_ID());
        tranDtlPopperEditIV.setTag(transObj.getTRAN_ID());

        //hide the notes panel if there isnt any note
        if(transObj.getTRAN_NOTE() == null){
            tranDtlPopperNoteLL.setVisibility(View.GONE);
        }
        else if(transObj.getTRAN_NOTE().isEmpty()){
            tranDtlPopperNoteLL.setVisibility(View.GONE);
        }

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup)detailsDialog.findViewById(R.id.tranDtlPopperLLId), robotoCondensedLightFont);
    }

    private void showTransferDetailsPopper(TransferModel transferObj) {
        detailsDialog = new Dialog(ViewActivitiesActivity.this);
        detailsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        detailsDialog.setContentView(R.layout.transfer_view_details_popper);

        detailsDialog.show();

        //Buttons
        ImageView tranferDtlPopperEditIV, transferDtlPopperDelIV;
        tranferDtlPopperEditIV = (ImageView) detailsDialog.findViewById(R.id.tranferDtlPopperEditIVId);
        transferDtlPopperDelIV = (ImageView) detailsDialog.findViewById(R.id.transferDtlPopperDelIVId);

        transferDtlPopperDelIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessagePopper(v);
            }
        });
        tranferDtlPopperEditIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toEditTransfer((String) v.getTag());
                if(detailsDialog != null) {
                    detailsDialog.dismiss();
                }
            }
        });

        //texts
        TextView transferDtlPopperFromAccTV, transferDtlPopperToAccTV, transferDtlPopperNoteTV, transferDtlPopperDateTV, transferDtlPopperAmtTV,
                    transferDtlPopperCreateTV, transferDtlPopperUpdtdTV;
        LinearLayout transferDtlPopperNoteLL;
        transferDtlPopperFromAccTV = (TextView) detailsDialog.findViewById(R.id.transferDtlPopperFromAccTVId);
        transferDtlPopperToAccTV = (TextView) detailsDialog.findViewById(R.id.transferDtlPopperToAccTVId);
        transferDtlPopperNoteTV = (TextView) detailsDialog.findViewById(R.id.transferDtlPopperNoteTVId);
        transferDtlPopperDateTV = (TextView) detailsDialog.findViewById(R.id.transferDtlPopperDateTVId);
        transferDtlPopperAmtTV = (TextView) detailsDialog.findViewById(R.id.transferDtlPopperAmtTVId);
        transferDtlPopperCreateTV = (TextView) detailsDialog.findViewById(R.id.transferDtlPopperCreateTVId);
        transferDtlPopperUpdtdTV = (TextView) detailsDialog.findViewById(R.id.transferDtlPopperUpdtdTVId);
        transferDtlPopperNoteLL = (LinearLayout) detailsDialog.findViewById(R.id.transferDtlPopperNoteLLId);

        //set values getFormattedDate
        transferDtlPopperFromAccTV.setText(transferObj.getFromAccName());
        transferDtlPopperToAccTV.setText(transferObj.getToAccName());
        transferDtlPopperNoteTV.setText(transferObj.getTRNFR_NOTE());
        transferDtlPopperAmtTV.setText(String.valueOf(transferObj.getTRNFR_AMT()));

        tranferDtlPopperEditIV.setTag(transferObj.getTRNFR_ID());
        transferDtlPopperDelIV.setTag(transferObj.getTRNFR_ID());

        //hide the notes panel if there isnt any note
        if(transferObj.getTRNFR_NOTE() == null){
            transferDtlPopperNoteLL.setVisibility(View.GONE);
        }
        else if(transferObj.getTRNFR_NOTE().isEmpty()){
            transferDtlPopperNoteLL.setVisibility(View.GONE);
        }


        SimpleDateFormat goodFormat1 = new SimpleDateFormat("d MMM ''yy, h:mm a");
        SimpleDateFormat goodFormat2 = new SimpleDateFormat("d MMM ''yy");

        transferDtlPopperDateTV.setText(goodFormat2.format(transferObj.getTRNFR_DATE()));
        transferDtlPopperCreateTV.setText(goodFormat1.format(transferObj.getCREAT_DTM()));

        if(transferObj.getMOD_DTM() != null){
            transferDtlPopperUpdtdTV.setText(goodFormat2.format(transferObj.getMOD_DTM()));
        }

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup)detailsDialog.findViewById(R.id.transferDtlPopperLLId), robotoCondensedLightFont);
    }

    private void toEditTransaction(String tag) {
        //get the transaction from db..fill it up in the intent..send it to addUpdateTransactionActivity as TRANSACTION_OBJ

        //TODO: this db call is the second call for the same purpose. ideally try avoiding this by utilizing the 1st call while calling the popper. just an advice.
        TransactionModel transObj = viewActivitiesDbService.getTransactionOnTransactionID(tag);

        Intent intent = new Intent(ViewActivitiesActivity.this, AddUpdateTransactionActivity.class);
        intent.putExtra("TRANSACTION_OBJ", transObj);
        startActivity(intent);
        finish();
    }

    private void toEditTransfer(String tag) {
        //get the transfer from db..fill it up in the intent..send it to addUpdateTransferActivity as TRANSFER_OBJ

        //TODO: this db call is the second call for the same purpose. ideally try avoiding this by utilizing the 1st call while calling the popper. just an advice.
        TransferModel transferObj = viewActivitiesDbService.getTransferOnTransferID(tag);

        Intent intent = new Intent(ViewActivitiesActivity.this, AddUpdateTransferActivity.class);
        intent.putExtra("TRANSFER_OBJ", transferObj);
        startActivity(intent);
        finish();
    }

    protected void showToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    private void deleteItem(Object tag){
        Log.i(CLASS_NAME, "Its 12:02 Am and you are still sober if this log prints the deleteItem id:"+(String)tag);

        /*if(viewTransactionsDbService.deleteTransaction(tag.toString()) != 0){
            showToast("Transaction deleted !");
        }
        else{
            showToast("Could not delete the transaction");
        }*/
    }

    //---------------------------------List View Click Listener ends------------------------------------------

    public void addNewTransaction(View view){
        //TODO:this needs to be changed when u implement custom date range... ideally ask the user to pick the date when he is seeing the custom date range
        /*TransactionModel newTranObj = new TransactionModel();
        newTranObj.setTRAN_DATE(transactionObj.getTRAN_DATE());

        Intent intent = new Intent(ViewActivitiesActivity.this, AddUpdateTransactionActivity.class);
        intent.putExtra("TRANSACTION_OBJ", newTranObj);
        startActivity(intent);
        finish();*/
    }

    //--------------------------------Linear Layout click listener--------------------------------------------------
    private LinearLayout.OnClickListener linearLayoutClickListener;
    {
        linearLayoutClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(CLASS_NAME, "Linear Layout Click is working !! There's hope :) by the way you clicked:"+ v.getId());

                Intent intent = null;

                switch(v.getId()){
                    case R.id.msgPoprPosLLId :      deleteItem(v.getTag());
                        intent = getIntent();
                                                    break;
                    case R.id.msgPoprNegLLId :      break;

                    default:intent = new Intent(ViewActivitiesActivity.this, JimBrokeItActivity.class); break;
                }

                if(messageDialog != null){
                    messageDialog.dismiss();
                }

                if(detailsDialog != null){
                    detailsDialog.dismiss();
                }

                if(intent != null){
                    startActivity(intent);
                    finish();
                }
            }
        };
    }
    //--------------------------------Linear Layout ends--------------------------------------------------

    //method iterates over each component in the activity and when it finds a text view..sets its font
    public void setFont(ViewGroup group, Typeface font) {
        int count = group.getChildCount();
        View v;

        for(int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if(v instanceof TextView) {
                ((TextView) v).setTypeface(font);
            }
            else if(v instanceof ViewGroup) {
                setFont((ViewGroup) v, font);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewActivitiesActivity.this, CalendarActivity.class);
        startActivity(intent);
        finish();
    }
}

