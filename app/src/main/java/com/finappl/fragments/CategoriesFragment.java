package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.adapters.CategoriesFragmentListViewAdapter;
import com.finappl.models.CategoryMO;

import java.util.List;

import static com.finappl.utils.Constants.CATEGORY_OBJECT;
import static com.finappl.utils.Constants.SELECTED_CATEGORY_OBJECT;
import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 21/3/16.
 */
public class CategoriesFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    //components
    private RelativeLayout categoriesRL;
    private ListView categoriesLV;
    //end of components

    private List<CategoryMO> categoriesList;

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

    private void getCategoriesFromBundle() {
        categoriesList = (List<CategoryMO>) getArguments().get(CATEGORY_OBJECT);
        selectedCategoryStr = (String) getArguments().get(SELECTED_CATEGORY_OBJECT);
    }

    private void setupPage() {
        CategoriesFragmentListViewAdapter categoriesFragmentListViewAdapter = new CategoriesFragmentListViewAdapter(mContext, categoriesList, selectedCategoryStr);
        categoriesLV.setAdapter(categoriesFragmentListViewAdapter);
    }

    private void initComps(View view){
        categoriesRL = (RelativeLayout) view.findViewById(R.id.categoriesRLId);
        categoriesLV = (ListView) view.findViewById(R.id.categoriesContentLVId);

        categoriesLV.setOnItemClickListener(listViewItemClickListener);

        setFont(categoriesRL);
    }

    AdapterView.OnItemClickListener listViewItemClickListener;
    {
        listViewItemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TransactionFragment activity = (TransactionFragment) getTargetFragment();
                activity.onFinishDialog(categoriesList.get(position));
                dismiss();
            }
        };
    }


    // Empty constructor required for DialogFragment
    public CategoriesFragment() {}

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
