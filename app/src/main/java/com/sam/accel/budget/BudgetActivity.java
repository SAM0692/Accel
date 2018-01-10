package com.sam.accel.budget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sam.accel.R;
import com.sam.accel.budget.database.BudgetDatabaseManager;
import com.sam.accel.budget.interfaces.DialogButtonListener;
import com.sam.accel.budget.fragment.BudgetDialogFragment;
import com.sam.accel.budget.model.Budget;
import com.sam.accel.budget.model.Category;
import com.sam.accel.budget.model.MonthlySavings;
import com.sam.accel.budget.utils.NumberFormatter;

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
    MenuItem miAddCategory;
    MenuItem miSummary;

    float income;
    float spent;


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
            updateMonthAvailable();

            //LOAD THE LIST OF THE BUDGET'S CATEGORIES
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
//                    Toast.makeText(BudgetActivity.this, "Testing long click", Toast.LENGTH_SHORT).show();

                    return true;
                }
            });

            //ENABLE THE "ADD CATEGORY" BUTTON
//            Button btnAddCategory = (Button) findViewById(R.id.button_add_category);
//            btnAddCategory.setEnabled(true);
        }
    }

    public void updateMonthAvailable() {
        income = month.getIncome();
        spent = month.getSpent();
        TextView tvIncome = (TextView) findViewById(R.id.textview_income);
        tvIncome.setText(NumberFormatter.formatAvailable(income, spent));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_budget, menu);

        budgetMenu = menu;

        miAddCategory = budgetMenu.getItem(1);
        miSummary = budgetMenu.getItem(2);

        if (activeBudget != null) {
            miAddCategory.setEnabled(true);
            miSummary.setEnabled(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_budget:
                layoutReference = R.layout.budget_dialog_new_budget;
                break;
            case R.id.action_add_category:
                Toast.makeText(this, "Adding category", Toast.LENGTH_SHORT).show();
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

    public void onButtonAddCategoryClick(View view) {
        layoutReference = R.layout.budget_dialog_new_category;
        showBudgetDialog();
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
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }

    private void createNewBudget(DialogFragment dialog) {
        Dialog d = dialog.getDialog();
        EditText etIncome = (EditText) d.findViewById(R.id.edittext_income);
        income = Float.valueOf(etIncome.getText().toString());

        dbManager.closeActiveBudget();
        dbManager.insertBudget(income);

        miSummary.setEnabled(true);

        loadBudget();

        Toast.makeText(this, "A new budget has been created", Toast.LENGTH_SHORT).show();
    }

    private void createNewCategory(DialogFragment dialog) {
        String name;
        float limit;
        Dialog d = dialog.getDialog();
        EditText etname = (EditText) d.findViewById(R.id.edittext_category_name);
        EditText etlimit = (EditText) d.findViewById(R.id.edittext_category_limit);
        name = etname.getText().toString();
        limit = Float.valueOf(etlimit.getText().toString());

        if (validateLimit(limit)) {
            Category cat = dbManager.insertCategory(name, limit, activeBudget);
            categories.add(cat);
            adapter.notifyDataSetChanged();
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
            Budget updateBudget = new Budget();
            MonthlySavings updateMonth = new MonthlySavings();

            float savings = month.getIncome() - month.getSpent();
            if (savings > 0) {
                updateMonth.setSaved(savings);
                updateBudget.setTotalSavings(savings);
            }

            dbManager.updateActiveBudget(updateBudget);
            dbManager.updateCurrentMonth(updateMonth);

            // CREATE A NEW MONTH AND UPDATE IT'S SPENT VALUE IF NEEDED
            month = dbManager.insertMonth(activeBudget);
            if (savings < 0) {
                float spent = savings * -1;

                updateMonth = new MonthlySavings();
                updateMonth.setSpent(spent);

                dbManager.updateCurrentMonth(updateMonth);
            }

            dbManager.resetCategories();

            Toast.makeText(this, "A new month has started", Toast.LENGTH_SHORT).show();
        }
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
