package com.hfad.accel.budget;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.hfad.accel.R;
import com.hfad.accel.budget.fragment.NewBudgetFragment;

public class BudgetActivity extends Activity
                            implements NewBudgetFragment.NewBudgetDialogButtonListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_budget, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_new_budget:
                showNewBudgetDialog();
                return true;
            case R.id.action_add_category:
                return true;
            case R.id.action_summary:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showNewBudgetDialog() {
        DialogFragment dialog = new NewBudgetFragment();
        dialog.show(getFragmentManager(), "NewBudgetFragmentDialog");
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        CharSequence salary = "none";
        Dialog d = (Dialog) dialog.getDialog();
        TextView textViewSalary = (TextView) d.findViewById(R.id.editText_salary);
        salary = textViewSalary.getText();
        Log.i("BudgetActivity", "Salary: " + salary);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }
}
