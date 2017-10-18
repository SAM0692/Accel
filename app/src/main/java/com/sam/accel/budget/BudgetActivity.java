package com.sam.accel.budget;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sam.accel.R;
import com.sam.accel.budget.database.BudgetDatabaseManager;
import com.sam.accel.budget.interfaces.DialogButtonListener;
import com.sam.accel.budget.fragment.BudgetDialogFragment;
import com.sam.accel.budget.model.Budget;
import com.sam.accel.budget.model.MonthlySavings;

public class BudgetActivity extends Activity
        implements DialogButtonListener {

    int layoutReference;

    BudgetDatabaseManager dbManager;

    Budget activeBudget;

    Menu budgetMenu;
    MenuItem miAddCategory;
    MenuItem miSummary;

    String income;
    String spent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        ListView category = (ListView) findViewById(R.id.listview_category);
        CategoryAdapter adapter = new CategoryAdapter(this);
        category.setAdapter(adapter);

        dbManager = new BudgetDatabaseManager(this);

        loadBudget();
    }

    private void loadBudget() {
        activeBudget = dbManager.selectActiveBudget();

        if (activeBudget != null) {
            MonthlySavings month = dbManager.selectCurrentMonth(activeBudget.getId());
            spent = Float.toString(month.getSpent());
            income = Float.toString(month.getIncome());
            TextView tvIncome = (TextView) findViewById(R.id.textview_income);
            tvIncome.setText(spent + " / " + income);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_budget, menu);

        budgetMenu = menu;

        miAddCategory = budgetMenu.getItem(0);
        miAddCategory.setEnabled(false);
        miSummary = budgetMenu.getItem(2);
        miSummary.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_budget:
                layoutReference = R.layout.budget_dialog_new_budget;
                break;
            case R.id.action_add_category:
                layoutReference = R.layout.budget_dialog_new_category;
                break;
            case R.id.action_summary:
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        showBudgetDialog();

        return true;
    }

    public void showBudgetDialog() {
        BudgetDialogFragment dialog = new BudgetDialogFragment();
        dialog.setLayoutReference(layoutReference);
        dialog.show(getFragmentManager(), "BudgetDialog");
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        switch (layoutReference) {
            case R.layout.budget_dialog_new_budget:
                createNewBudget(dialog);
                break;
            case R.layout.budget_dialog_new_category:
                createNewCategory(dialog);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }

    private void createNewBudget(DialogFragment dialog) {
        Dialog d = dialog.getDialog();
        EditText etIncome = (EditText) d.findViewById(R.id.edittext_income);
        income = etIncome.getText().toString();

        dbManager.closeActiveBudget();
        dbManager.insertBudget(Float.valueOf(income));

        miAddCategory.setEnabled(true);
        miSummary.setEnabled(true);

        loadBudget();

        Toast.makeText(this, "A new budget has been created", Toast.LENGTH_SHORT).show();
    }

    private void createNewCategory(DialogFragment dialog) {
        CharSequence name, limit;
        Dialog d = dialog.getDialog();
        EditText etname = (EditText) d.findViewById(R.id.edittext_category_name);
        EditText etlimit = (EditText) d.findViewById(R.id.edittext_category_limit);
        name = etname.getText();
        limit = etlimit.getText();
        Log.i("BudgetActivity", "Name: " + name);
        Log.i("BudgetActivity", "Limit: " + limit);
    }
}
