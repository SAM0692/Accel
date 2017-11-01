package com.sam.accel.budget;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sam.accel.R;
import com.sam.accel.budget.model.Category;

import java.util.List;

/**
 * Created by SAcevedoM on 28/09/2017.
 */

public class CategoryAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Category> categories;

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
            convertView = inflater.inflate(R.layout.list_item_category, parent, false);

            holder = new ViewHolder();
            holder.categoryName = (TextView) convertView.findViewById(R.id.textview_category_name);
            holder.available = (TextView) convertView.findViewById(R.id.textview_available);
            holder.amount = (EditText) convertView.findViewById(R.id.edittext_amount);
            holder.regExpense = (Button) convertView.findViewById(R.id.button_register_expense);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        final Category category = categories.get(position);
        final TextView available = holder.available;
        final EditText amount = holder.amount;
        TextView categoryName = holder.categoryName;
        Button regExpense = holder.regExpense;

        categoryName.setText(category.getName());
        available.setText(category.getLimit() + " / " + (category.getLimit() - category.getSpent()));
        regExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountText = amount.getText().toString();

                if(amountText.equals("")) {
                    Toast.makeText(v.getContext(), "Enter an amount first"
                            , Toast.LENGTH_SHORT).show();
                    return;
                }

                float amountToReg = Float.valueOf(amountText);
                float totalSpent = category.getSpent() + amountToReg;

                if ((category.getLimit() - totalSpent) < 0) {
                    Toast.makeText(v.getContext(), "You don't have that much left for this category!!"
                            , Toast.LENGTH_SHORT).show();
                } else {
                    category.setSpent(totalSpent);
                    available.setText(category.getLimit() + " / " + (category.getLimit() - category.getSpent()));
                }

                amount.setText("");
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView categoryName;
        TextView available;
        EditText amount;
        Button regExpense;
    }
}
