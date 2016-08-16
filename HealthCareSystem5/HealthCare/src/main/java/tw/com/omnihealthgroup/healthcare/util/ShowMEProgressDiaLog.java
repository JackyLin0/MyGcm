package tw.com.omnihealthgroup.healthcare.util;

import android.app.ProgressDialog;
import android.content.Context;

public class ShowMEProgressDiaLog extends ProgressDialog{

    public ShowMEProgressDiaLog(Context context, String title, String message, boolean indeterminate, boolean canBeCanceled) {
        super(context);
        setTitle(title);
        setMessage(message);
        setIndeterminate(indeterminate);
        setCancelable(canBeCanceled);
    }


}
