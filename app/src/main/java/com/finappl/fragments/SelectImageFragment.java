package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.adapters.SelectImageGridViewAdapter;
import com.finappl.models.AccountMO;
import com.finappl.models.CategoryMO;
import com.finappl.models.SpentOnMO;
import com.finappl.utils.FinappleUtility;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.finappl.utils.Constants.IMAGE_OBJECT;
import static com.finappl.utils.Constants.OK;
import static com.finappl.utils.Constants.SELECTED_IMAGE_OBJECT;
import static com.finappl.utils.Constants.UI_FONT;
import static com.finappl.utils.Constants.UN_IDENTIFIED_PARENT_FRAGMENT;

/**
 * Created by ajit on 21/3/16.
 */
public class SelectImageFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    //components
    @InjectView(R.id.selectImageLLId)
    LinearLayout selectImageLL;

    @InjectView(R.id.selectImageGVId)
    GridView selectImageGV;
    //end of components

    private List<Object> imagesList;
    private Object selectedImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_image, container);
        ButterKnife.inject(this, view);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getDataFromBundle();
        initComps();
        setupPage();

        return view;
    }

    private void getDataFromBundle() {
        imagesList = (List<Object>) getArguments().get(IMAGE_OBJECT);
        selectedImage =  getArguments().get(SELECTED_IMAGE_OBJECT);
    }

    private void setupPage() {
        SelectImageGridViewAdapter adapter = new SelectImageGridViewAdapter(mContext, imagesList, selectedImage, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getTargetFragment() instanceof AddUpdateCategoryFragment){
                    ((AddUpdateCategoryFragment)getTargetFragment()).setCategoryImage((CategoryMO)view.getTag());
                }
                else if(getTargetFragment() instanceof AddUpdateAccountFragment){
                    ((AddUpdateAccountFragment)getTargetFragment()).setAccountImage((AccountMO) view.getTag());
                }
                else if(getTargetFragment() instanceof AddUpdateSpentonFragment){
                    ((AddUpdateSpentonFragment)getTargetFragment()).setSpentonImage((SpentOnMO) view.getTag());
                }
                else{
                    FinappleUtility.showSnacks(selectImageLL, UN_IDENTIFIED_PARENT_FRAGMENT, OK, Snackbar.LENGTH_INDEFINITE);
                    return;
                }

                dismiss();
            }
        });
        selectImageGV.setAdapter(adapter);
    }

    private void initComps(){
    }

    // Empty constructor required for DialogFragment
    public SelectImageFragment() {}

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
