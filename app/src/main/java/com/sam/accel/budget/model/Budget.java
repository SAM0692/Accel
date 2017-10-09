package com.sam.accel.budget.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by SAcevedoM on 25/09/2017.
 */

public class Budget extends RealmObject {

    @PrimaryKey
    private int id;
    private Date creationDate;
    private Date closingDate;
    private float income;
    private float totalIncome;
    private float totalSavings;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(Date closingDate) {
        this.closingDate = closingDate;
    }

    public float getIncome() {
        return income;
    }

    public void setIncome(float income) {
        this.income = income;
    }

    public float getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(float totalIncome) {
        this.totalIncome = totalIncome;
    }

    public float getTotalSavings() {
        return totalSavings;
    }

    public void setTotalSavings(float totalSavings) {
        this.totalSavings = totalSavings;
    }
}
