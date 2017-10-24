package com.sam.accel.budget;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sam.accel.R;
import com.sam.accel.budget.model.Category;

import java.util.ArrayList;
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
        Category category = categories.get(position);

       if(convertView == null) {
           convertView = inflater.inflate(R.layout.list_item_category, parent, false);

           holder = new ViewHolder();
           holder.categoryName = (TextView) convertView.findViewById(R.id.textview_category_name);
           holder.spent = (TextView) convertView.findViewById(R.id.textview_spent);

           convertView.setTag(holder);
       } else {
           holder = (ViewHolder) convertView.getTag();
       }

        TextView categoryName = holder.categoryName;
        TextView spent = holder.spent;

        categoryName.setText(category.getName());
        spent.setText(category.getSpent() + " / " + category.getLimit());

        return convertView;
    }

    private static class ViewHolder {
        TextView categoryName;
         TextView spent;
    }
}
