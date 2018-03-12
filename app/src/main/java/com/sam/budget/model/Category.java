package com.sam.budget.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by SAcevedoM on 25/09/2017.
 */

public class Category extends RealmObject {

    @PrimaryKey
    private int id;
    @Required
    private String name;
    private float limit;
    private float spent;
    private boolean temporary;
    private Budget budget;
    private RealmList<Expense> expenseList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getLimit() {
        return limit;
    }

    public void setLimit(float limit) {
        this.limit = limit;
    }

    public float getSpent() {
        return spent;
    }

    public void setSpent(float spent) {
        this.spent = spent;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public RealmList<Expense> getExpenseList() {
        return expenseList;
    }

    public void setExpenseList(RealmList<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    public boolean isTemporary() {
        return temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }
}
