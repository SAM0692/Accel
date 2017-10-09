package com.sam.accel.budget.database;

import android.content.Context;

import com.sam.accel.budget.model.Budget;

import java.util.Date;

import io.realm.Realm;

/**
 * Created by SAcevedoM on 04/10/2017.
 */

public class BudgetDatabaseManager {

    Realm realm;

    public BudgetDatabaseManager(Context context) {
        Realm.init(context);
        realm = Realm.getDefaultInstance();
    }

    public void insertBudget(float income) {
        realm.beginTransaction();
        // INCREMENT ID
        Number lastId = realm.where(Budget.class).max("id");
        int newId;
        if(lastId == null) {
            newId = 1;
        }else {
            newId = lastId.intValue() + 1;
        }
        Budget newBudget = realm.createObject(Budget.class, newId);
        newBudget.setCreationDate(new Date());
        newBudget.setIncome(income);

        realm.commitTransaction();
    }
}
