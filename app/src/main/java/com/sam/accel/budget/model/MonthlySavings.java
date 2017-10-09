package com.sam.accel.budget.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by SAcevedoM on 25/09/2017.
 */

public class MonthlySavings extends RealmObject {

    @PrimaryKey
    private int id;
    private Budget budget;
    private float saved;
    @Required
    private Date date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public float getSaved() {
        return saved;
    }

    public void setSaved(float saved) {
        this.saved = saved;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
