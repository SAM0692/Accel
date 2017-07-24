package com.hfad.accel.budget.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hfad.accel.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewBudgetFragment extends DialogFragment {


    public interface NewBudgetDialogButtonListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    NewBudgetDialogButtonListener nbdListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            nbdListener = (NewBudgetDialogButtonListener) context;
        } catch(ClassCastException e) {
            throw new ClassCastException(context.toString() + "must be implemented");
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.budget_dialog_new_budget, null))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nbdListener.onDialogPositiveClick(NewBudgetFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nbdListener.onDialogNegativeClick(NewBudgetFragment.this);
                    }
                });


        return builder.create();
    }
}
