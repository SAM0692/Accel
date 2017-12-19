package com.sam.accel.budget.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sam.accel.R;
import com.sam.accel.budget.BudgetActivity;
import com.sam.accel.budget.interfaces.DialogButtonListener;
import com.sam.accel.budget.model.Budget;
import com.sam.accel.budget.model.Category;
import com.sam.accel.budget.model.MonthlySavings;

import java.text.DateFormat;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BudgetDialogFragment extends DialogFragment {

    DialogButtonListener nbdListener;

    private int layoutReference;
    private int titleReference;

    View dialogLayoutView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            nbdListener = (DialogButtonListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must be implemented");
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogLayoutView = inflater.inflate(layoutReference, null);

        loadData();

        builder.setView(dialogLayoutView);
        builder.setTitle(titleReference);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nbdListener.onDialogPositiveClick(BudgetDialogFragment.this);
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nbdListener.onDialogNegativeClick(BudgetDialogFragment.this);
            }
        });

        return builder.create();
    }

    private void loadData() {
        switch (layoutReference) {
            case R.layout.budget_dialog_new_budget:
                titleReference = R.string.dialog_new_budget_title;
                break;
            case R.layout.budget_dialog_summary:
                titleReference = R.string.dialog_budget_summary_title;
                loadSummaryData();
                break;
            case R.layout.budget_dialog_new_category:
                titleReference = R.string.dialog_new_category_title;
                loadAvailable();
                break;
        }
    }

    private void loadSummaryData() {
        BudgetActivity activity = (BudgetActivity) getActivity();
        Budget activeBudget = activity.getActiveBudget();
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
        String strCreationDate = df.format(activeBudget.getCreationDate());

        TextView tvBaseIncome = (TextView) dialogLayoutView.findViewById(R.id.textview_budget_base_income);
        TextView tvDate = (TextView) dialogLayoutView.findViewById(R.id.textview_budget_creation_date);
        TextView tvTotalIncome = (TextView) dialogLayoutView.findViewById(R.id.textview_budget_total_income);
        TextView tvSavings = (TextView) dialogLayoutView.findViewById(R.id.textview_budget_total_savings);

        tvBaseIncome.append(" " + Float.toString(activeBudget.getBaseIncome()));
        tvDate.append(" " + strCreationDate);
        tvTotalIncome.append(" " + Float.toString(activeBudget.getTotalIncome()));
        tvSavings.append(" " + Float.toString(activeBudget.getTotalSavings()));


    }

    private void loadAvailable() {
        BudgetActivity activity = (BudgetActivity) getActivity();
        MonthlySavings month = activity.getMonth();
        List<Category> categories = activity.getCategories();
        TextView tvAvailable = (TextView)dialogLayoutView.findViewById(R.id.textview_category_available);
        float available = month.getIncome();

        for(Category c : categories) {
            available = available - c.getLimit();
        }

        tvAvailable.setText("Available income: " + available);
    }


    public int getLayoutReference() {
        return layoutReference;
    }

    public void setLayoutReference(int layoutReference) {
        this.layoutReference = layoutReference;
    }
}
