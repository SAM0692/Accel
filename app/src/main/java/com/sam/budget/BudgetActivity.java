package com.sam.budget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sam.budget.database.BudgetDatabaseManager;
import com.sam.budget.interfaces.DialogButtonListener;
import com.sam.budget.fragment.BudgetDialogFragment;
import com.sam.budget.model.Budget;
import com.sam.budget.model.Category;
import com.sam.budget.model.MonthlySavings;

import java.util.Calendar;
import java.util.List;

public class BudgetActivity extends Activity
        implements DialogButtonListener {

    int layoutReference;

    BudgetDatabaseManager dbManager;
    CategoryAdapter adapter;
    List<Category> categories;
    MonthlySavings month;

    Budget activeBudget;

    Menu budgetMenu;
    MenuItem miAddIncome;
    MenuItem miSummary;
    MenuItem miNewCategory;

    float income;
    float available;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget_activity_budget);

        dbManager = new BudgetDatabaseManager(this);

        loadBudget();
    }

    private void loadBudget() {

        activeBudget = dbManager.selectActiveBudget();

        if (activeBudget != null) {
            //LOAD THE CURRENT MONTH OF THE ACTIVE BUDGET
            month = dbManager.selectCurrentMonth(activeBudget.getId());
            verifyMonth();

            //LOAD THE LIST OF THE BUDGET'S CATEGORIES
            loadCategories();
        }
    }

    private void loadCategories() {
        categories = dbManager.selectCategoriesByBudgetAsList(activeBudget);
        adapter = new CategoryAdapter(this, categories);
        ListView category = (ListView) findViewById(R.id.listview_category);
        category.setAdapter(adapter);
        category.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int categoryPosition = position;
                TextView tvCategoryName = (TextView) view.findViewById(R.id.textview_category_name);
                String categoryName = tvCategoryName.getText().toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(categoryName);
                builder.setMessage(R.string.dialog_message_delete);
                // YES
                builder.setPositiveButton(R.string.option_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Category catForDeletion = categories.get(categoryPosition);
                        categories.remove(catForDeletion);
                        adapter.notifyDataSetChanged();
                        dbManager.deleteCategory(catForDeletion.getId());
                    }
                });
                // NO
                builder.setNegativeButton(R.string.option_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_budget, menu);

        budgetMenu = menu;

        miAddIncome = budgetMenu.findItem(R.id.action_add_income);
        miSummary = budgetMenu.findItem(R.id.action_summary);
        miNewCategory = budgetMenu.findItem(R.id.action_new_category);

        if (activeBudget != null) {
            enableMenuItems();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_category:
                layoutReference = R.layout.budget_dialog_new_category;
                break;
            case R.id.action_new_budget:
                layoutReference = R.layout.budget_dialog_new_budget;
                break;
            case R.id.action_add_income:
                layoutReference = R.layout.budget_dialog_add_income;
                break;
            case R.id.action_summary:
                layoutReference = R.layout.budget_dialog_summary;
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
                break;
            case R.layout.budget_dialog_add_income:
                addIncome(dialog);
                break;
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }

    private void createNewBudget(DialogFragment dialog) {
        Dialog d = dialog.getDialog();
        EditText etIncome = d.findViewById(R.id.edittext_income);
        income = Float.valueOf(etIncome.getText().toString());

        dbManager.closeActiveBudget();
        dbManager.insertBudget(income);

        enableMenuItems();

        loadBudget();

        Toast.makeText(this, "A new budget has been created", Toast.LENGTH_SHORT).show();


    }

    private void createNewCategory(DialogFragment dialog) {
        String name;
        float limit;
        boolean temporary;
        Dialog d = dialog.getDialog();
        EditText etname = (EditText) d.findViewById(R.id.edittext_category_name);
        EditText etlimit = (EditText) d.findViewById(R.id.edittext_category_limit);
        CheckBox cbtemporary = (CheckBox) d.findViewById(R.id.checkbox_category_temporary);
        name = etname.getText().toString();
        limit = Float.valueOf(etlimit.getText().toString());
        temporary = cbtemporary.isChecked();

        if (validateLimit(limit)) {
            dbManager.insertCategory(name, limit, activeBudget, temporary);
            loadCategories();
        }
    }

    public void addIncome(DialogFragment dialog) {
        float income;
        Dialog d = dialog.getDialog();
        EditText etincome = (EditText) d.findViewById(R.id.edittext_add_income_amount);
        income = Float.valueOf(etincome.getText().toString());

        if (income > 0) {
            MonthlySavings updateMonth = new MonthlySavings();
            updateMonth.setIncome(income);
            dbManager.updateCurrentMonth(updateMonth);
        }
    }

    private boolean validateLimit(float limit) {
        float currentLimit = 0;
        float budgetLimit = dbManager.selectCurrentMonth(activeBudget.getId()).getIncome();
        float excess = 0;
        boolean valid = true;

        for (Category c : categories) {
            currentLimit += c.getLimit();
        }

        excess = (currentLimit + limit) - budgetLimit;

        if (excess > 0) {
            Toast.makeText(this, "The category limit you entered exceeds this month's by: " + excess
                    , Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    private void verifyMonth() {
        Calendar today = Calendar.getInstance();
        Calendar monthDate = Calendar.getInstance();
        monthDate.setTime(month.getDate());

        int day = today.get(Calendar.DAY_OF_MONTH);
        int m1 = today.get(Calendar.MONTH);
        int m2 = monthDate.get(Calendar.MONTH);

        if (day == 1 && m1 != m2) {
            Budget updateBudget = dbManager.selectUnmanagedBudget();
            MonthlySavings updateMonth = dbManager.selectUnmanagedMonth(updateBudget.getId());

            float savings = month.getIncome() - month.getSpent();

            updateBudget.setTotalSavings(updateBudget.getTotalSavings() + savings);
            updateMonth.setSaved(updateMonth.getSaved() + savings);

            dbManager.updateActiveBudget(updateBudget);
            dbManager.updateCurrentMonth(updateMonth);

            month = dbManager.insertMonth();
            MonthlySavings newMonth = new MonthlySavings();
            if (savings < 0) {
                newMonth.setIncome(month.getIncome() + savings);

                dbManager.updateCurrentMonth(newMonth);
            }

            dbManager.resetCategories();

            Toast.makeText(this, "A new month has started", Toast.LENGTH_SHORT).show();
        }
    }

    // Enables is disables some of the menu items
    private void enableMenuItems() {
        miAddIncome.setEnabled(true);
        miSummary.setEnabled(true);
        miNewCategory.setEnabled(true);
        // Change the new category item visibility
        miNewCategory.setVisible(true);
    }

    public Budget getActiveBudget() {
        return activeBudget;
    }

    public MonthlySavings getMonth() {
        return month;
    }

    public List<Category> getCategories() {
        return categories;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }
}
