package com.brzimetrokliziretro.notebook;


import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FragmentListDialog extends DialogFragment {
    public static final String TITLE = "dataKey";

    String text = "";
    String changed_text = "";

    public FragmentListDialog(){}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View convertview = inflater.inflate(R.layout.dialogset, null);

        final EditText et_text = (EditText)convertview.findViewById(R.id.et_dialog);
        Button btn_cancel = (Button)convertview.findViewById(R.id.btn_cancel_dialog);
        Button btn_ok = (Button)convertview.findViewById(R.id.btn_ok_dialog);

        et_text.setText(text);

        builder.setView(convertview);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changed_text = et_text.getText().toString();


                dismiss();
            }
        });

        return builder.create();

    }
}