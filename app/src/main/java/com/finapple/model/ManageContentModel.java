package com.finapple.model;

import java.util.List;
import java.util.Map;

/**
 * Created by ajit on 10/5/15.
 */
public class ManageContentModel {

    private Map<String, List<CategoryModel>> categoriesMap;
    private Map<String, List<AccountsModel>> accountsMap;
    private Map<String, List<SpentOnModel>> spentOnsMap;
    private String userNameStr;

    public String getUserNameStr() {
        return userNameStr;
    }

    public void setUserNameStr(String userNameStr) {
        this.userNameStr = userNameStr;
    }

    public Map<String, List<CategoryModel>> getCategoriesMap() {
        return categoriesMap;
    }

    public void setCategoriesMap(Map<String, List<CategoryModel>> categoriesMap) {
        this.categoriesMap = categoriesMap;
    }

    public Map<String, List<AccountsModel>> getAccountsMap() {
        return accountsMap;
    }

    public void setAccountsMap(Map<String, List<AccountsModel>> accountsMap) {
        this.accountsMap = accountsMap;
    }

    public Map<String, List<SpentOnModel>> getSpentOnsMap() {
        return spentOnsMap;
    }

    public void setSpentOnsMap(Map<String, List<SpentOnModel>> spentOnsMap) {
        this.spentOnsMap = spentOnsMap;
    }
}
