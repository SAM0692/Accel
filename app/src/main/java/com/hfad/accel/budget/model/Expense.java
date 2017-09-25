package com.hfad.accel.budget.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by SAcevedoM on 25/09/2017.
 */

public class Expense extends RealmObject {

    @PrimaryKey
    private int id;
    private MonthlySavings monthSavings;
    private Category category;
    private float amount;
    @Required
    private Date date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MonthlySavings getMonthSavings() {
        return monthSavings;
    }

    public void setMonthSavings(MonthlySavings monthSavings) {
        this.monthSavings = monthSavings;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

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
