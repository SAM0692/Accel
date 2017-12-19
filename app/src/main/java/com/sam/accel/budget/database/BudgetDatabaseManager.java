package com.sam.accel.budget.database;

import android.content.Context;
import android.util.Log;

import com.sam.accel.budget.model.Budget;
import com.sam.accel.budget.model.Category;
import com.sam.accel.budget.model.Expense;
import com.sam.accel.budget.model.MonthlySavings;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by SAcevedoM on 04/10/2017.
 */

public class BudgetDatabaseManager {

    private Realm realm;

    public BudgetDatabaseManager(Context context) {
        Realm.init(context);
        realm = Realm.getDefaultInstance();
    }



    ////-------------------------------------------------------------------------------------- UTILS

    private int createNewId(RealmObject modelClass) {
        int newId;
        Number lastId = realm.where(modelClass.getClass()).max("id");
        if (lastId == null) {
            newId = 1;
        } else {
            newId = lastId.intValue() + 1;
        }
        return newId;
    }

    public void closeActiveBudget() {
        Budget activeBudget = selectActiveBudget();

        if (activeBudget != null) {
            realm.beginTransaction();

            activeBudget.setClosingDate(new Date());

            realm.commitTransaction();
        }
    }


    ////------------------------------------------------------------------------------------- INSERT

    // BUDGET TABLE
    public void insertBudget(float income) {
        // CREATE A NEW ID
        int newId = createNewId(new Budget());
        Log.d("NEWID", "the new Budget id is: " + newId);
        realm.beginTransaction();

        Budget newBudget = realm.createObject(Budget.class, newId);
        newBudget.setCreationDate(new Date());
        newBudget.setBaseIncome(income);

        realm.commitTransaction();

        insertMonth(newBudget);
    }

    // MONTHLYSAVINGS TABLE
    public void insertMonth(Budget activeBudget) {
        int newId = createNewId(new MonthlySavings());
        Log.d("NEWID", "the new MonthlySaving id is: " + newId);
        realm.beginTransaction();

        MonthlySavings newMonth = realm.createObject(MonthlySavings.class, newId);
        newMonth.setBudget(activeBudget);
        newMonth.setIncome(activeBudget.getBaseIncome());
        newMonth.setDate(new Date());

        realm.commitTransaction();
    }

    // CATEGORY TABLE
    public Category insertCategory(String name, float limit, Budget budget) {
        Category newCategory;
        int newId = createNewId(new Category());

        realm.beginTransaction();

        newCategory = realm.createObject(Category.class, newId);
        newCategory.setName(name);
        newCategory.setLimit(limit);
        newCategory.setBudget(budget);

        realm.commitTransaction();

        return newCategory;
    }


    ////------------------------------------------------------------------------------------- SELECT

    // BUDGET TABLE
    public Budget selectActiveBudget() {
        Budget budget;

        realm.beginTransaction();

        budget = realm.where(Budget.class).isNull("closingDate").findFirst();

        realm.commitTransaction();

        return budget;
    }

    // MONTHLYSAVINGS TABLE
    public MonthlySavings selectCurrentMonth(Budget activeBudget) {
        MonthlySavings month;

        realm.beginTransaction();

        month = realm.where(MonthlySavings.class).equalTo("budget.id", activeBudget.getId())
                .findAllSorted("date", Sort.DESCENDING).first();

        realm.commitTransaction();

        return month;
    }

    // CATEGORY TABLE
    public Category selectCategoryById(int idCategory) {
        Category category;

        realm.beginTransaction();

        category = realm.where(Category.class).equalTo("id", idCategory).findFirst();

        realm.commitTransaction();

        return category;
    }

    public List<Category> selectCategoriesByBudget(Budget budget) {
        List<Category> categories;

        realm.beginTransaction();

        categories = realm.where(Category.class).equalTo("budget.id", budget.getId()).findAll();
        categories = realm.copyFromRealm(categories);

        realm.commitTransaction();

        return categories;
    }


    ////------------------------------------------------------------------------------------- UPDATE

    // BUDGET TABLE
    public void updateActiveBudget(Budget updateBudget) {
        Budget activeBudget = selectActiveBudget();

        realm.beginTransaction();

        activeBudget.setTotalIncome(activeBudget.getTotalIncome() + updateBudget.getTotalIncome());
        activeBudget.setTotalSavings(activeBudget.getTotalIncome() + updateBudget.getTotalSavings());

        realm.commitTransaction();
    }

    // MONTHLYSAVINGS TABLE
    public void updateCurrentMonth(MonthlySavings updateMonth) {
        MonthlySavings currentMonth = selectCurrentMonth(selectActiveBudget());

        realm.beginTransaction();

        currentMonth.setIncome(currentMonth.getIncome() + updateMonth.getIncome());
        currentMonth.setSpent(currentMonth.getSpent() + updateMonth.getSpent());

        if (updateMonth.getSaved() != 0) {
            currentMonth.setSaved(updateMonth.getSaved());
        }

        realm.commitTransaction();
    }

    // CATEGORY TABLE
    public void updateCategory(Category updateCategory, Expense newExpense) {
        Category category = selectCategoryById(updateCategory.getId());
        MonthlySavings currentMonth = selectCurrentMonth(selectActiveBudget());

        realm.beginTransaction();

        if (updateCategory.getSpent() != 0) {
            category.setSpent(category.getSpent() + updateCategory.getSpent());
            currentMonth.setSpent(currentMonth.getSpent() + updateCategory.getSpent());
        }

        if (newExpense != null) {
            category.getExpenseList().add(newExpense);
        }


        realm.commitTransaction();
    }
}
