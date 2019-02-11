package pt.pedro.textfileviewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

/**
 * Created by pedro on 2/11/19.
 * LSTS - FEUP
 */
class LogCatUtil {
    Context mContext;
    Activity context;

    public enum INFO_TYPE {
        ALERT,
        CONFIRM,
        INFO,
        ERROR,
        DEFAULT
    }

    private ProgressDialog pDialog;
    private boolean isDialogCancel = true;

    LogCatUtil(Context mContext) {
        this.mContext = mContext;
        context = (Activity) mContext;
    }

    void errorPopUp(String title, String text, final boolean exitApp){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(text)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(exitApp)
                            System.exit(0);
                    }
                });
        alertDialogBuilder.setIcon(R.drawable.icon_error);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    void successPopUp(String text){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Success !!!");
        alertDialogBuilder.setMessage(text)
                .setCancelable(true)
                .setPositiveButton("Ok",null);
        alertDialogBuilder.setIcon(R.drawable.icon_sucess);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    void alertPopUp(String title, String text){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(text)
                .setCancelable(false)
                .setIcon(R.drawable.icon_alert)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    void terminal(String labelId, String text){
        Log.i(labelId, text);
    }

    void infoToast(String text, boolean longShow){
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View layout = Objects.requireNonNull(inflater).inflate(R.layout.my_custom_toast, (ViewGroup) context.findViewById(R.id.custom_toast_layout));
        TextView textView = layout.findViewById(R.id.textToShow);
        textView.setText(text);
        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);

        if(longShow)
            toast.setDuration(Toast.LENGTH_LONG);
        else
            toast.setDuration(Toast.LENGTH_SHORT);

        toast.show();
    }

    void showDialog(final String title, final boolean cancelable){
        if (Looper.myLooper() == null){
            Looper.prepare();
        }

        context.runOnUiThread(new Runnable() {
            public void run() {
                if (Looper.myLooper() == null){
                    Looper.prepare();
                }
                if(pDialog != null)
                    pDialog.dismiss();
                pDialog = new ProgressDialog(context);
                pDialog.setMessage(title);
                pDialog.setCancelable(false);
                if(cancelable) {
                    pDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteDialog();
                            isDialogCancel = true;
                        }
                    });
                }
                pDialog.show();
            }
        });
        isDialogCancel = false;
    }

    void updateDialog(final String text){
        context.runOnUiThread(new Runnable() {
            public void run() {
                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }
                pDialog.setMessage(text);
            }
        });
    }

    void deleteDialog(){
        try{
            pDialog.cancel();
            if(pDialog != null)
                pDialog.dismiss();
        }catch (Exception ignored){}
    }

    boolean isDialogCancel() {
        if(isDialogCancel) {
            isDialogCancel = false;
            return true;
        }else
            return false;
    }

    void snackBar(ConstraintLayout constraintlayout, String text, boolean longShow, INFO_TYPE type){
        Snackbar snackbar;
        if(longShow) {
            snackbar = Snackbar.make(constraintlayout, text, Snackbar.LENGTH_LONG).setAction("Action", null);
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);
            switch (type){
                case ALERT:
                    sbView.setBackgroundColor(context.getResources().getColor(R.color.ALERT));
                    break;

                case CONFIRM:
                    sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.CONFIRM));
                    break;

                case INFO:
                    sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.INFO));
                    break;

                case ERROR:
                    sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.ERROR));
                    break;

                case DEFAULT:
                default:
                    break;
            }

        }
        else {
            snackbar = Snackbar.make(constraintlayout, text, Snackbar.LENGTH_SHORT).setAction("Action", null);
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);
            switch (type){
                case ALERT:
                    sbView.setBackgroundColor(context.getResources().getColor(R.color.ALERT));
                    break;

                case CONFIRM:
                    sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.CONFIRM));
                    break;

                case INFO:
                    sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.INFO));
                    break;

                case ERROR:
                    sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.ERROR));
                    break;

                case DEFAULT:
                default:
                    break;
            }
        }
        snackbar.show();
    }
}
