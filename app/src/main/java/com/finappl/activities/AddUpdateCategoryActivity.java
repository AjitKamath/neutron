package com.finappl.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.finappl.adapters.AddCategoryAdapter;
import com.finappl.R;
import com.finappl.dbServices.AddUpdateCatDbService;
import com.finappl.dbServices.AuthorizationDbService;
import com.finappl.models.CategoryModel;
import com.finappl.models.TagModel;
import com.finappl.models.UserMO;
import com.finappl.models.UsersModel;
import com.finappl.utils.FinappleUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ajit on 31/1/15.
 */
public class AddUpdateCategoryActivity extends Activity {

    private final String CLASS_NAME = this.getClass().getName();

    //header
    private ImageView addCatHeaderBackImg;
    private TextView addCatLabelTV;

    //page
    private EditText addCatCatNameET, addCatNoteNameET, addCatTagET;
    private RadioGroup addCatTranTypeRG;
    private RadioButton addCatTranExpRB, addCatTranIncRB;
    private TextView addCatNoTagsLabelTV;
    private ListView addCatTagsLV;

    //buttons
    private TextView addCatDoneTV, addCatDiscardTV;

    private List<TagModel> tagList = new ArrayList<TagModel>();

    private Context mContext = this;

    //message popper
    private Dialog dialog;

    //db service
    private AddUpdateCatDbService addUpdateCatDbService = new AddUpdateCatDbService(mContext);
    private AuthorizationDbService authorizationDbService = new AuthorizationDbService(mContext);

    //User
    private UserMO loggedInUserObj;

    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_content_add_update_category);

        //get the Active user
        loggedInUserObj = authorizationDbService.getActiveUser(FinappleUtility.getInstance().getActiveUserId(mContext));
        if(loggedInUserObj == null){
            return;
        }

        //init ui components
        initUIComponents();

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) this.findViewById(R.id.addCatRLId), robotoCondensedLightFont);
    }

    public void onDoneUpdate(View view){
        String catnameStr = addCatCatNameET.getText().toString();
        String catTypeStr = "";

        if(addCatTranTypeRG.getCheckedRadioButtonId() == addCatTranExpRB.getId()){
            catTypeStr = "EXPENSE";
        }
        else{
            catTypeStr = "INCOME";
        }

        String catNotesStr = addCatNoteNameET.getText().toString();

        //validate stale inputs from user
        if(catnameStr.trim().length() == 0){
            addCatCatNameET.setText("");
            showToast("category name is empty !");
            return;
        }

        CategoryModel catObject = new CategoryModel();
        catObject.setCAT_NAME(catnameStr);
        catObject.setCAT_TYPE(catTypeStr);
        catObject.setCAT_NOTE(catNotesStr);
        catObject.setCategoryTagList(tagList);

        //add user id
        catObject.setUSER_ID(loggedInUserObj.getUSER_ID());

        if("SAVE".equalsIgnoreCase(addCatDoneTV.getText().toString())){
            long result = addUpdateCatDbService.addNewCategory(catObject);

            if(result == -2){
                showToast("category already exists !");
                return;
            }
            else if(result == -1){
                showToast("adding category failed !");
                return;
            }
            else{
                showToast("new category added !");

                Intent intent = new Intent(this, ManageContentActivity.class);
                startActivity(intent);
                finish();
            }
        }
        else{
            //TODO: update for add category yet to be implemented
        }
    }

    public void showMessagePopper(View view){
        if("BACK".equalsIgnoreCase(addCatHeaderBackImg.getTag().toString())){
            Intent intent = new Intent(this, ManageContentActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Create custom message popper object
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.message_popper);

        dialog.show();

        //buttons
        LinearLayout msgPoprPosLL, msgPoprNegLL;
        msgPoprPosLL = (LinearLayout) dialog.findViewById(R.id.msgPoprPosLLId);
        msgPoprNegLL = (LinearLayout) dialog.findViewById(R.id.msgPoprNegLLId);

        //set listeners for the buttons
        msgPoprPosLL.setOnClickListener(linearLayoutClickListener);
        msgPoprNegLL.setOnClickListener(linearLayoutClickListener);

        //set font for all the text view
        final Typeface robotoCondensedLightFont = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        setFont((ViewGroup) dialog.findViewById(R.id.msgPoprLLId), robotoCondensedLightFont);
    }

    public void onBackClick(View view){
        Intent intent = null;

        if("BACK".equalsIgnoreCase(view.getTag().toString())){
            intent = new Intent(this, ManageContentActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            showMessagePopper(view);
        }
    }

    private void initUIComponents() {
        //initialize UI components
        //header
        addCatHeaderBackImg = (ImageView) this.findViewById(R.id.addCatHeaderBackImgId);

        //page
        addCatCatNameET = (EditText) this.findViewById(R.id.addCatCatNameETId);
        addCatNoteNameET = (EditText) this.findViewById(R.id.addCatNoteNameETId);
        addCatTagET = (EditText) this.findViewById(R.id.addCatTagETId);
        addCatTranTypeRG = (RadioGroup) this.findViewById(R.id.addCatTranTypeRGId);
        addCatTranExpRB = (RadioButton) this.findViewById(R.id.addCatTranExpRBId);
        addCatTranIncRB = (RadioButton) this.findViewById(R.id.addCatTranIncRBId);
        addCatNoTagsLabelTV = (TextView) this.findViewById(R.id.addCatNoTagsLabelTVId);
        addCatTagsLV= (ListView) this.findViewById(R.id.addCatTagsLVId);

        //buttons
        addCatDoneTV = (TextView) this.findViewById(R.id.addCatDoneTVId);

        //text watcher
        addCatCatNameET.addTextChangedListener(fileTextWatcher);
    }

    public void addNewCatTag(View view){
        //check for stupid inputs in tag field
        String tagStr = addCatTagET.getText().toString();

        if(addCatTagET != null && "".trim().equalsIgnoreCase(tagStr)){
            showToast("tag is empty !");
            return;
        }

        //check if the tag is already in the tagList
        boolean invalidTag = false;
        for(TagModel iterList : tagList){
            if(iterList.getTag().equalsIgnoreCase(tagStr.trim())){
                showToast("tag already added !");
                addCatTagET.setText("");
                invalidTag = true;
                break;
            }
        }

        if(invalidTag){
            return;
        }

        tagList.add(new TagModel(tagStr.toLowerCase()));
        tagsChanged();
        addCatTagET.setText("");
    }

    public void remOldCatTag(View view){
        String tagDelTagStr = view.getTag().toString();
        List<TagModel> newTagList = new ArrayList<TagModel>();

        for(TagModel iterList : tagList){
            if(!iterList.getTag().equalsIgnoreCase(tagDelTagStr)){
                newTagList.add(iterList);
            }
        }

        tagList.clear();
        for(TagModel iterList : newTagList){
            tagList.add(iterList);
        }

        tagsChanged();
        showToast("tag deleted !");
    }

    private void tagsChanged() {
        if(tagList != null){
            AddCategoryAdapter adapter = new AddCategoryAdapter(this, R.layout.category_add_update_list_view, tagList);
            adapter.notifyDataSetChanged();
            addCatTagsLV.setAdapter(adapter);
        }

        if(addCatTagsLV != null && addCatTagsLV.getAdapter().getCount() != 0){
            addCatNoTagsLabelTV.setVisibility(View.GONE);
            addCatTagsLV.setVisibility(View.VISIBLE);
        }
        else{
            addCatNoTagsLabelTV.setVisibility(View.VISIBLE);
            addCatTagsLV.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        showMessagePopper(null);
    }

    //---------------------------------Edit Text type Listener-----------------------------------
    TextWatcher fileTextWatcher;
    {
        fileTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                //restrict the user to enter only 2 decimal inputs
                if(!addCatCatNameET.getText().toString().trim().isEmpty()){
                    if("DISCARD".equalsIgnoreCase(addCatHeaderBackImg.getTag().toString())){
                        return;
                    }

                    int currentRotation = 0;
                    final RotateAnimation rotateAnim = new RotateAnimation(currentRotation, currentRotation + 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
                    rotateAnim.setInterpolator(new LinearInterpolator());
                    rotateAnim.setDuration(100);
                    rotateAnim.setFillEnabled(true);
                    rotateAnim.setFillAfter(true);

                    rotateAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            addCatHeaderBackImg.setBackground(addCatHeaderBackImg.getResources().getDrawable(R.drawable.cancel));
                            addCatHeaderBackImg.setTag("DISCARD");
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    addCatHeaderBackImg.startAnimation(rotateAnim);
                }
                else{
                    if("BACK".equalsIgnoreCase(addCatHeaderBackImg.getTag().toString())){
                        return;
                    }

                    int currentRotation = 0;
                    final RotateAnimation rotateAnim = new RotateAnimation(currentRotation, currentRotation+360, Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF,0.5f);
                    rotateAnim.setInterpolator(new LinearInterpolator());
                    rotateAnim.setDuration(100);
                    rotateAnim.setFillEnabled(true);
                    rotateAnim.setFillAfter(true);

                    rotateAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            addCatHeaderBackImg.setBackground(addCatHeaderBackImg.getResources().getDrawable(R.drawable.back));
                            addCatHeaderBackImg.setTag("BACK");
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    addCatHeaderBackImg.startAnimation(rotateAnim);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        };
    }
    //---------------------------------Edit Text type Listener ends-----------------------------------

    //--------------------------------Linear Layout click listener--------------------------------------------------
    private LinearLayout.OnClickListener linearLayoutClickListener;
    {
        linearLayoutClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(CLASS_NAME, "Linear Layout Click is working !! There's hope :) by the way you clicked:" + v.getId());

                Intent intent = null;

                switch(v.getId()){
                    case R.id.msgPoprPosLLId :      intent = new Intent(mContext, ManageContentActivity.class);
                        break;
                    case R.id.msgPoprNegLLId :      break;

                    default:intent = new Intent(mContext, JimBrokeItActivity.class); break;
                }

                if(dialog != null){
                    dialog.dismiss();
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

    protected void showToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}
