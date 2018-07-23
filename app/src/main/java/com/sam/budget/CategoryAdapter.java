package com.sam.budget;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.sam.budget.database.BudgetDatabaseManager;
import com.sam.budget.model.Category;
import com.sam.budget.model.Expense;
import com.sam.budget.utils.NumberFormatter;

import java.util.Date;
import java.util.List;

/**
 * Created by SAcevedoM on 28/09/2017.
 */

public class CategoryAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Category> categories;
    private BudgetDatabaseManager dbManager;


    public CategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
        inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.budget_list_item_category, parent, false);

            holder = new ViewHolder();
            holder.categoryName = (TextView) convertView.findViewById(R.id.textview_category_name);
            holder.available = (TextView) convertView.findViewById(R.id.textview_available_category);
            holder.amount = (EditText) convertView.findViewById(R.id.edittext_amount);
            holder.regExpense = (ImageButton) convertView.findViewById(R.id.button_register_expense);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final EditText amount = holder.amount;
        TextView categoryName = holder.categoryName;
        ImageButton regExpense = holder.regExpense;

        final Category category = categories.get(position);

        categoryName.setText(category.getName());

        updateAvailable(holder, category);

        if (category.isTemporary()) {
            amount.setVisibility(View.INVISIBLE);
            regExpense.setVisibility(View.INVISIBLE);
            return convertView;
        } else {
            amount.setVisibility(View.VISIBLE);
            regExpense.setVisibility(View.VISIBLE);
        }

        // Set the onClickListener for the register expense button
        regExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountText = amount.getText().toString();
                final float amountToReg = Float.valueOf(amountText);

                if (amountText.equals("")) {
                    Toast.makeText(v.getContext(), "Enter an amount first"
                            , Toast.LENGTH_SHORT).show();
                    return;
                }

                // If the amount to register ends up surpassing the limit for the category
                // create a dialog with a warning and the choices of continuing or not with the process
                if ((category.getLimit() - amountToReg) < 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle(category.getName() + " limit exceeded");
                    builder.setMessage(R.string.dialog_message_limit_exceeded);
                    builder.setCancelable(false);
                    // YES
                    builder.setPositiveButton(R.string.option_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            registerExpense(amountToReg);
                            updateAvailable(holder, category);
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
                } else {
                    registerExpense(amountToReg);
                    updateAvailable(holder, category);
                }

                amount.setText("");
            }
        });

        return convertView;
    }

    private void registerExpense(float amountToReg) {
        dbManager = new BudgetDatabaseManager(context);
        Category updateCategory = new Category();
        Expense newExpense = new Expense();

        newExpense.setAmount(amountToReg);
        newExpense.setDate(new Date());

        updateCategory.setSpent(amountToReg);
        dbManager.updateCategory(updateCategory, newExpense);

        BudgetActivity activity = (BudgetActivity) context;
        activity.updateMonthAvailable();
    }

    private void updateAvailable(ViewHolder holder, Category category) {
        if (category.isTemporary()) {
            holder.available.setText(NumberFormatter.formatFloat(category.getSpent()));
        } else {
            holder.available.setText(NumberFormatter.formatAvailable(category.getLimit(), category.getSpent()));
        }
    }

    private static class ViewHolder {
        TextView categoryName;
        TextView available;
        EditText amount;
        ImageButton regExpense;
    }
}
