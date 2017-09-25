package com.hfad.accel.budget.model;

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
    private float spendingLimit;
    private float spent;

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

    public float getSpendingLimit() {
        return spendingLimit;
    }

    public void setSpendingLimit(float spendingLimit) {
        this.spendingLimit = spendingLimit;
    }

    public float getSpent() {
        return spent;
    }

    public void setSpent(float spent) {
        this.spent = spent;
    }
}
