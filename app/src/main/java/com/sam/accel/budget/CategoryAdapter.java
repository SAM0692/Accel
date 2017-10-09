package com.sam.accel.budget;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sam.accel.R;

/**
 * Created by SAcevedoM on 28/09/2017.
 */

public class CategoryAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;

    public CategoryAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

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

        CharSequence cs = "TestCat";
        CharSequence ca = "0000/0000";

        categoryName.setText(cs);
        spent.setText(ca);

        return convertView;
    }

    private static class ViewHolder {
        public TextView categoryName;
        public TextView spent;
    }
}
