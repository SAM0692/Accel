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
import com.sam.budget.interfaces.ButtonListener;
import com.sam.budget.model.Category;
import com.sam.budget.model.Expense;
import com.sam.budget.utils.NumberFormatter;

import java.util.Date;
import java.util.List;

import static java.security.AccessController.getContext;

/**
 * Created by SAcevedoM on 28/09/2017.
 */

public class CategoryAdapter extends BaseAdapter implements ButtonListener {
    private Context context;
    private LayoutInflater inflater;
    private List<Category> categories;
    private BudgetDatabaseManager dbManager;

    private Category category;
    private TextView available;
    private EditText amount;

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
        ViewHolder holder;

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

        category = categories.get(position);
        available = holder.available;
        amount = holder.amount;
        TextView categoryName = holder.categoryName;
        ImageButton regExpense = holder.regExpense;

        categoryName.setText(category.getName());
        available.setText(NumberFormatter.formatAvailable(category.getLimit(),
                (category.getLimit() - category.getSpent())));

        regExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(v);
            }
        });

        return convertView;
    }

    @Override
    public void onButtonClick(View view) {
        String amountText = amount.getText().toString();
        dbManager = new BudgetDatabaseManager(context);
        final Category updateCategory = new Category();
        final float amountToReg = Float.valueOf(amountText);
        final Expense newExpense = new Expense();

        if (amountText.equals("")) {
            Toast.makeText(view.getContext(), "Enter an amount first"
                    , Toast.LENGTH_SHORT).show();
            return;
        }

        if ((category.getLimit() - amountToReg) < 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle(category.getName() + " limit exceeded");
            builder.setMessage(R.string.dialog_message_limit_exceeded);
            builder.setCancelable(false);
            // YES
            builder.setPositiveButton(R.string.option_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    newExpense.setAmount(amountToReg);
                    newExpense.setDate(new Date());

                    updateCategory.setId(category.getId());
                    updateCategory.setSpent(amountToReg);
                    dbManager.updateCategory(updateCategory, newExpense);

                    available.setText(NumberFormatter.formatAvailable(category.getLimit(), updateCategory.getSpent()));
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
            newExpense.setAmount(amountToReg);
            newExpense.setDate(new Date());

            updateCategory.setId(category.getId());
            updateCategory.setSpent(amountToReg);
            dbManager.updateCategory(updateCategory, newExpense);

            available.setText(NumberFormatter.formatAvailable(category.getLimit(), updateCategory.getSpent()));
        }

        BudgetActivity activity = (BudgetActivity) context;
        activity.updateMonthAvailable();
        amount.setText("");
    }

    private static class ViewHolder {
        TextView categoryName;
        TextView available;
        EditText amount;
        ImageButton regExpense;
    }
}
