package com.sam.budget.model;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by SAcevedoM on 25/09/2017.
 */

public class Expense extends RealmObject {

    private float amount;
    private Date date;

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
