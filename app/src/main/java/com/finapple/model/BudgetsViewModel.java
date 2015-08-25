package com.finapple.model;

import java.util.List;
import java.util.Map;

/**
 * Created by ajit on 10/5/15.
 */
public class BudgetsViewModel {

    private Map<String, List<BudgetModel>> budgetDailyMap;
    private Map<String, List<BudgetModel>> budgetWeeklyMap;
    private Map<String, List<BudgetModel>> budgetMonthlyMap;
    private Map<String, List<BudgetModel>> budgetYearlyMap;

    public Map<String, List<BudgetModel>> getBudgetDailyMap() {
        return budgetDailyMap;
    }

    public void setBudgetDailyMap(Map<String, List<BudgetModel>> budgetDailyMap) {
        this.budgetDailyMap = budgetDailyMap;
    }

    public Map<String, List<BudgetModel>> getBudgetWeeklyMap() {
        return budgetWeeklyMap;
    }

    public void setBudgetWeeklyMap(Map<String, List<BudgetModel>> budgetWeeklyMap) {
        this.budgetWeeklyMap = budgetWeeklyMap;
    }

    public Map<String, List<BudgetModel>> getBudgetMonthlyMap() {
        return budgetMonthlyMap;
    }

    public void setBudgetMonthlyMap(Map<String, List<BudgetModel>> budgetMonthlyMap) {
        this.budgetMonthlyMap = budgetMonthlyMap;
    }

    public Map<String, List<BudgetModel>> getBudgetYearlyMap() {
        return budgetYearlyMap;
    }

    public void setBudgetYearlyMap(Map<String, List<BudgetModel>> budgetYearlyMap) {
        this.budgetYearlyMap = budgetYearlyMap;
    }
}
