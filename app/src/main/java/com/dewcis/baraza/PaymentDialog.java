package com.dewcis.baraza;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Faith on 4/10/2018.
 */

public class PaymentDialog extends AppCompatDialogFragment {
    String title, instructions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        title = bundle.getString("title");
        instructions = bundle.getString("instructions");

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.rev_base,null);

        Button button = (Button)view.findViewById(R.id.button);
        TextView textView = (TextView)view.findViewById(R.id.textView);
        button.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
        dialog.setView(view);

        dialog.setTitle(title);
        dialog.setMessage(instructions);
        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        return dialog.create();
    }
}
