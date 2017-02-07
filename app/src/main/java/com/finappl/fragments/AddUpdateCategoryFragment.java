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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.finappl.R;
import com.finappl.adapters.TagsGridViewAdapter;
import com.finappl.dbServices.CalendarDbService;
import com.finappl.models.CategoryMO;
import com.finappl.models.UserMO;
import com.finappl.utils.FinappleUtility;
import com.finappl.utils.IdGenerator;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.finappl.utils.Constants.ADMIN_USERID;
import static com.finappl.utils.Constants.FRAGMENT_ADD_UPDATE_CATEGORY;
import static com.finappl.utils.Constants.FRAGMENT_SELECT_IMAGE;
import static com.finappl.utils.Constants.IMAGE_OBJECT;
import static com.finappl.utils.Constants.LOGGED_IN_OBJECT;
import static com.finappl.utils.Constants.OK;
import static com.finappl.utils.Constants.SELECTED_IMAGE_OBJECT;
import static com.finappl.utils.Constants.UI_FONT;

/**
 * Created by ajit on 21/3/16.
 */
public class AddUpdateCategoryFragment extends DialogFragment {
    private final String CLASS_NAME = this.getClass().getName();
    private Context mContext;

    /*components*/
    @InjectView(R.id.addUpdateCategoryRLId)
    RelativeLayout addUpdateCategoryRL;

    @InjectView(R.id.addUpdateCategoryIVId)
    ImageView addUpdateCategoryIV;

    @InjectView(R.id.addUpdateCategoryNameETId)
    EditText addUpdateCategoryNameET;

    @InjectView(R.id.addUpdateCategoryTagsETId)
    EditText addUpdateCategoryTagsET;

    @InjectView(R.id.addUpdateCategoryTagsGVId)
    GridView addUpdateCategoryTagsGV;

    @InjectView(R.id.addUpdateCategoryNoTagsTVId)
    TextView addUpdateCategoryNoTagsTV;
    /*components*/

    /*Data*/
    private CategoryMO category;
    private List<CategoryMO> categoriesList;
    private Set<String> tagsSet;
    private UserMO user;

    /*Database*/
    private CalendarDbService calendarDbService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_update_category, container);
        ButterKnife.inject(this, view);

        Dialog d = getDialog();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getDataFromBundle();
        initComps();
        setupPage();

        return view;
    }

    @OnClick(R.id.addUpdateCategoryHeaderCloseTVId)
    public void close(){
        dismiss();
    }

    @OnClick(R.id.addUpdateCategoryHeaderSaveTVId)
    public void onAddUpdate(){
        CategoryMO tempCategory = validateAndGetInputs();

        if(tempCategory == null){
            return;
        }

        String messageStr;

        //old category
        if(tempCategory.getCAT_ID() != null){
            if (calendarDbService.updateCategory(tempCategory)) {
                messageStr = "Category updated";
            } else {
                messageStr = "Failed to update Category";
            }
        }
        else{
            //check if the category already exists
            if(calendarDbService.isCategoryAlreadyExist(user.getUSER_ID(), tempCategory.getCAT_NAME())){
                FinappleUtility.showSnacks(addUpdateCategoryRL, "Category already exists", OK, Snackbar.LENGTH_LONG);
                return;
            }

            tempCategory.setCAT_ID(IdGenerator.getInstance().generateUniqueId("CAT"));
            tempCategory.setUSER_ID(user.getUSER_ID());
            long result = calendarDbService.addNewCategory(tempCategory);

            if (result == -1) {
                messageStr = "Failed to create a new Category !";
            } else {
                messageStr = "New Category created";
            }
        }

        close();
        ((CategoriesFragment)getTargetFragment()).onFragmentClose(messageStr);
    }

    private CategoryMO validateAndGetInputs() {
        String categoryNameStr = String.valueOf(addUpdateCategoryNameET.getText());
        String categoryImageStr = String.valueOf(((CategoryMO)addUpdateCategoryIV.getTag()).getCAT_IMG());

        if(categoryNameStr != null && !categoryNameStr.trim().isEmpty()){
            categoryNameStr = categoryNameStr.trim();
        }
        else{
            FinappleUtility.showSnacks(addUpdateCategoryRL, "Category Title is empty", OK, Snackbar.LENGTH_LONG);
            return null;
        }

        category.setCAT_NAME(categoryNameStr);
        category.setCAT_IMG(categoryImageStr);
        category.setTagsStr(FinappleUtility.setToCSV(tagsSet));

        return category;
    }

    @OnClick(R.id.addUpdateCategoryIVId)
    public void showSelectImage(){
        //if the category is admins category then do not allow to select image
        if(ADMIN_USERID.equalsIgnoreCase(category.getUSER_ID())){
            return;
        }

        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(FRAGMENT_SELECT_IMAGE);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(SELECTED_IMAGE_OBJECT, (Serializable) addUpdateCategoryIV.getTag());
        bundle.putSerializable(IMAGE_OBJECT, (Serializable) categoriesList);

        Fragment currentFrag = manager.findFragmentByTag(FRAGMENT_ADD_UPDATE_CATEGORY);

        SelectImageFragment fragment = new SelectImageFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(currentFrag, 0);
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_theme);
        fragment.show(manager, FRAGMENT_SELECT_IMAGE);
    }

    private void getDataFromBundle() {
        category = (CategoryMO) getArguments().get(SELECTED_IMAGE_OBJECT);
        categoriesList = (List<CategoryMO>) getArguments().get(IMAGE_OBJECT);
        user = (UserMO) getArguments().get(LOGGED_IN_OBJECT);
    }

    private void setupPage() {
        //old category
        if(category.getCAT_ID() != null){
            setCategoryImage(category);
            addUpdateCategoryNameET.setText(category.getCAT_NAME());

            tagsSet = FinappleUtility.csvToSet(tagsSet, calendarDbService.getTagOnTagTypeId(user.getUSER_ID(), category.getCAT_ID()).getTAGS());
            setupTagsGrid();

            //if this is a admin category, disable editing of the name
            if(ADMIN_USERID.equalsIgnoreCase(category.getUSER_ID())){
                addUpdateCategoryNameET.setClickable(false);
                addUpdateCategoryNameET.setEnabled(false);
            }
        }
        //new category
        else{
            setCategoryImage(category);
        }
    }

    public void setCategoryImage(CategoryMO category){
        addUpdateCategoryIV.setBackgroundResource(Integer.parseInt(category.getCAT_IMG()));
        addUpdateCategoryIV.setTag(category);
    }

    private void initComps(){
        setFont(addUpdateCategoryRL);
    }

    @OnClick(R.id.addUpdateCategoryTagsTVId)
    public void setupTagsGrid() {
        tagsSet = FinappleUtility.csvToSet(tagsSet, String.valueOf(addUpdateCategoryTagsET.getText()));

        TagsGridViewAdapter adapter = new TagsGridViewAdapter(mContext, tagsSet, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagsSet.remove(view.getTag());
                setupTagsGrid();
            }
        });
        addUpdateCategoryTagsGV.setAdapter(adapter);

        if(tagsSet == null || tagsSet.isEmpty()){
            addUpdateCategoryNoTagsTV.setVisibility(View.VISIBLE);
        }
        else{
            addUpdateCategoryNoTagsTV.setVisibility(View.GONE);
        }

        addUpdateCategoryTagsET.setText("");

        hideKeyboard();
    }

    private void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(addUpdateCategoryTagsET.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    // Empty constructor required for DialogFragment
    public AddUpdateCategoryFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();

        calendarDbService = new CalendarDbService(mContext);
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog d = getDialog();
        if (d != null) {
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
