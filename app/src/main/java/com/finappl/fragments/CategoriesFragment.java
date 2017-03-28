package com.finappl.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.activities.HomeActivity;
import com.finappl.adapters.CategoriesFragmentListViewAdapter;
import com.finappl.dbServices.CalendarDbService;
import com.finappl.models.AccountMO;
import com.finappl.models.CategoryMO;
import com.finappl.models.SpentOnMO;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;

import java.io.Serializable;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.finappl.utils.Constants.CONFIRM_MESSAGE;
import static com.finappl.utils.Constants.FRAGMENT_ADD_UPDATE_CATEGORY;
import static com.finappl.utils.Constants.FRAGMENT_CATEGORIES;
import static com.finappl.utils.Constants.FRAGMENT_CONFIRM;
import static com.finappl.utils.Constants.FRAGMENT_DELETE_CONFIRM;
import static com.finappl.utils.Constants.IMAGE_OBJECT;
import static com.finappl.utils.Constants.IMAGE_SELECTED_CATEGORY;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.OK;
import static com.finappl.utils.Constants.SELECTED_ACCOUNT_OBJECT;
import static com.finappl.utils.Constants.SELECTED_CATEGORY_OBJECT;
import static com.finappl.utils.Constants.SELECTED_GENERIC_OBJECT;
import static com.finappl.utils.Constants.SELECTED_IMAGE_OBJECT;
import static com.finappl.utils.Constants.SELECTED_SPENTON_OBJECT;
import static com.finappl.utils.Constants.UI_FONT;
import static com.finappl.utils.Constants.UN_IDENTIFIED_VIEW;

/**
 * Created by ajit on 21/3/16.
 */
public class CategoriesFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    /*Components*/
    @InjectView(R.id.categoriesRLId)
    RelativeLayout categoriesRL;

    @InjectView(R.id.categoriesLVId)
    ListView categoriesLV;
    /*Components*/

    private List<CategoryMO> categoriesList;
    private UserMO user;

    /*Database Service*/
    private CalendarDbService calendarDbService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.categories, container);
        ButterKnife.inject(this, view);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getDataFromBundle();
        initComps();
        setupPage();

        return view;
    }

    @OnClick(R.id.categoriesAddUpdateTVId)
    public void showAddUpdateCategory(){
        CategoryMO category = new CategoryMO();
        category.setCAT_IMG(String.valueOf(IMAGE_SELECTED_CATEGORY));
        showAddUpdateCategory(category);
    }

    @OnClick(R.id.categoriesCloseTVId)
    public void close(){
        dismiss();
    }

    private void getDataFromBundle() {
        user = (UserMO) getArguments().get(LOGGED_IN_OBJECT);
    }

    private void setupPage() {
        getMasterData();

        CategoriesFragmentListViewAdapter adapter = new CategoriesFragmentListViewAdapter(mContext, categoriesList, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(R.id.categoryDeleteIVId == view.getId()){
                    //check for transactions & budgets which were created using the category & warn user that all the transactions/budgets will be marked to default category
                    showDeleteConfirm((CategoryMO)view.getTag());
                }
                else if(R.id.categoryModifyIVId == view.getId()){
                    showAddUpdateCategory((CategoryMO)view.getTag());
                }
                else{
                    FinappleUtility.showSnacks(categoriesRL, UN_IDENTIFIED_VIEW, OK, Snackbar.LENGTH_INDEFINITE);
                }
            }
        });
        categoriesLV.setAdapter(adapter);
    }

    private void showDeleteConfirm(CategoryMO category){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_DELETE_CONFIRM);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(SELECTED_GENERIC_OBJECT, category);
        bundle.putSerializable(LOGGED_IN_OBJECT, user);

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_CATEGORIES);

        DeleteConfirmFragment fragment = new DeleteConfirmFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_DELETE_CONFIRM);
    }

    private void showAddUpdateCategory(CategoryMO category){
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_CATEGORY);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(SELECTED_IMAGE_OBJECT, category);
        bundle.putSerializable(IMAGE_OBJECT, (Serializable) categoriesList);
        bundle.putSerializable(LOGGED_IN_OBJECT, user);

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_CATEGORIES);

        AddUpdateCategoryFragment fragment = new AddUpdateCategoryFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_ADD_UPDATE_CATEGORY);
    }

    private void getMasterData() {
        categoriesList = calendarDbService.getAllCategories(user.getUSER_ID());
    }

    private void initComps(){
        setFont(categoriesRL);
    }

    // Empty constructor required for DialogFragment
    public CategoriesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();

        initDb();
    }

    private void initDb() {
        calendarDbService = new CalendarDbService(mContext);
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

    public void onFragmentClose(String messageStr) {
        FinappleUtility.showSnacks(categoriesRL, messageStr, OK, Snackbar.LENGTH_LONG);
        setupPage();
    }

    //this method is called when u have to change all over the db where this category was used and then delete the category
    public void normalizeImpactsAndDeleteCategory(CategoryMO category) {
        calendarDbService.updateAll(user, category);
        deleteCategory(category);
    }

    //this method is called when we just have to delete the category which has not been used anywhere
    public void deleteCategory(CategoryMO category) {
        calendarDbService.deleteCategory(category.getCAT_ID());
        FinappleUtility.showSnacks(categoriesRL, "Category deleted !", OK, Snackbar.LENGTH_LONG);
        setupPage();
        //((HomeActivity)getActivity()).initActivity();
    }
}
