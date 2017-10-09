package com.sam.accel.budget.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;

import com.sam.accel.R;
import com.sam.accel.budget.interfaces.DialogButtonListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class BudgetDialogFragment extends DialogFragment {

    DialogButtonListener nbdListener;

    private int layoutReference;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            nbdListener = (DialogButtonListener) context;
        } catch(ClassCastException e) {
            throw new ClassCastException(context.toString() + "must be implemented");
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(layoutReference, null))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nbdListener.onDialogPositiveClick(BudgetDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nbdListener.onDialogNegativeClick(BudgetDialogFragment.this);
                    }
                });


        return builder.create();
    }

    public int getLayoutReference() {
        return layoutReference;
    }

    public void setLayoutReference(int layoutReference) {
        this.layoutReference = layoutReference;
    }
}
