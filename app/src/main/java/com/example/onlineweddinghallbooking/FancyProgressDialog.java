package com.example.onlineweddinghallbooking;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.WindowManager;

public class FancyProgressDialog {

    public static ProgressDialog createProgressDialog(Context context)
    {

        ProgressDialog dialog = new ProgressDialog(context);

        try
        {
            dialog.show();
        }
        catch (WindowManager.BadTokenException e)
        {

        }

        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progress_dialog);
        // dialog.setMessage(Message);
        return dialog;
    }

}
