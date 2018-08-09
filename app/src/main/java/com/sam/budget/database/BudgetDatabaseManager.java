package com.sam.budget.database;

import android.content.Context;
import android.util.Log;

import com.sam.budget.model.Budget;
import com.sam.budget.model.Category;
import com.sam.budget.model.Expense;
import com.sam.budget.model.MonthlySavings;

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

    public void close() {
        if (!realm.isClosed()) {
            realm.close();
        }
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

        insertMonth();
    }

    // MONTHLYSAVINGS TABLE
    public MonthlySavings insertMonth() {
        Budget activeBudget = selectActiveBudget();
        int newId = createNewId(new MonthlySavings());
        Log.d("NEWID", "the new MonthlySaving id is: " + newId);
        realm.beginTransaction();

        MonthlySavings newMonth = realm.createObject(MonthlySavings.class, newId);
        newMonth.setBudget(activeBudget);
        newMonth.setIncome(activeBudget.getBaseIncome());
        newMonth.setDate(new Date());

        realm.commitTransaction();

        return newMonth;
    }

    // CATEGORY TABLE
    public Category insertCategory(String name, float limit, Budget budget, boolean temporary) {
        Category newCategory;
        int newId = createNewId(new Category());

        realm.beginTransaction();

        newCategory = realm.createObject(Category.class, newId);
        newCategory.setName(name);
        newCategory.setLimit(limit);
        newCategory.setBudget(budget);
        newCategory.setTemporary(temporary);
        if (temporary) {
            newCategory.setSpent(limit);
            MonthlySavings currentMonth = selectCurrentMonth(budget.getId());
            currentMonth.setSpent(currentMonth.getSpent() + limit);
        }

        realm.commitTransaction();

        return newCategory;
    }


    ////------------------------------------------------------------------------------------- SELECT

    // BUDGET TABLE
    public Budget selectActiveBudget() {
        Budget budget;

        budget = realm.where(Budget.class).isNull("closingDate").findFirst();

        return budget;
    }

    public Budget selectUnmanagedBudget() {
        return realm.copyFromRealm(selectActiveBudget());
    }

    // MONTHLYSAVINGS TABLE
    public MonthlySavings selectCurrentMonth(int idBudget) {
        MonthlySavings month;

        month = realm.where(MonthlySavings.class).equalTo("budget.id", idBudget).findAll()
                .sort("date", Sort.DESCENDING).first();

        return month;
    }

    public MonthlySavings selectUnmanagedMonth(int idBudget) {
        return realm.copyFromRealm(selectCurrentMonth(idBudget));
    }

    // CATEGORY TABLE
    private Category selectCategoryById(int idCategory) {
        Category category;

        category = realm.where(Category.class).equalTo("id", idCategory).findFirst();

        return category;
    }

    public List<Category> selectCategoriesByBudgetAsList(Budget budget) {
        List<Category> categories;

        categories = realm.where(Category.class).equalTo("budget.id", budget.getId()).findAll();
        categories = realm.copyFromRealm(categories);

        return categories;
    }


    ////------------------------------------------------------------------------------------- UPDATE

    // BUDGET TABLE
    public void updateActiveBudget(Budget updateBudget) {
        Budget activeBudget = selectActiveBudget();

        realm.beginTransaction();

        activeBudget.setTotalSavings(updateBudget.getTotalSavings());

        realm.commitTransaction();
    }

    // MONTHLYSAVINGS TABLE
    public void updateCurrentMonth(MonthlySavings updateMonth) {
        MonthlySavings currentMonth = selectCurrentMonth(selectActiveBudget().getId());

        realm.beginTransaction();

        currentMonth.setIncome(updateMonth.getIncome());
        currentMonth.setSpent(updateMonth.getSpent());
        currentMonth.setSaved(updateMonth.getSaved());

        realm.commitTransaction();
    }

    // CATEGORY TABLE
    public void updateCategory(Category updateCategory, Expense newExpense) {
        Category category = selectCategoryById(updateCategory.getId());
        MonthlySavings currentMonth = selectCurrentMonth(selectActiveBudget().getId());

        realm.beginTransaction();

        if (updateCategory.getSpent() > 0) {
            category.setSpent(updateCategory.getSpent());
            currentMonth.setSpent(currentMonth.getSpent() + updateCategory.getSpent());
        }

        if (newExpense != null) {
            category.getExpenseList().add(newExpense);
        }


        realm.commitTransaction();
    }

    public void resetCategories() {
        RealmResults<Category> categories = realm.where(Category.class).equalTo("budget.id", selectActiveBudget().getId()).findAll();

        realm.beginTransaction();

        for (Category c : categories) {
            if (c.isTemporary()) {
                c.deleteFromRealm();
            } else {
                c.setSpent(0);
            }
        }

        realm.commitTransaction();
    }

    //// -------------------------------------------------------------------------------------DELETE
    public void deleteCategory(int idCategory) {
        Category category = selectCategoryById(idCategory);

        realm.beginTransaction();

        category.deleteFromRealm();

        realm.commitTransaction();
    }
}
