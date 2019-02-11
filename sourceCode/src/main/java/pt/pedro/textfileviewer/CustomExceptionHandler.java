package pt.pedro.textfileviewer;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by pedro on 2/11/19.
 * LSTS - FEUP
 */
class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler defaultUEH;
    private String localPath;
    private Context mContext;
    private File storageDir;

    CustomExceptionHandler(String nameFolder, Context mContext) {
        storageDir = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + nameFolder);
        if(!storageDir.exists()){
            storageDir.mkdirs();
        }

        this.localPath = storageDir.toString();
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.mContext = mContext;
    }

    public void uncaughtException(Thread t, Throwable e) {
        String timestamp = Calendar.getInstance().getTime().toString();
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();
        String filename = timestamp + ".stacktrace";
        Date buildDate = new Date(BuildConfig.TIMESTAMP);
        String branchText = mContext.getString(R.string.gitBranch) + "@" + mContext.getString(R.string.gitSHA) + " - "+ buildDate.toString();
        String text = " \n manufacturer " + Build.MANUFACTURER
                + " \n model " + Build.MODEL
                + " \n version api "  + Build.VERSION.CODENAME + " ( " + Build.VERSION.SDK_INT + " )"
                + " \n version android " + Build.VERSION.RELEASE
                + " \n app version " + branchText
                + " \n cpu " + Build.HARDWARE + "\n\n";
        if (localPath != null) {
            writeToFile(text + stacktrace, filename);
        }
        defaultUEH.uncaughtException(t, e);
        System.gc();
        Runtime.getRuntime().gc();
        System.exit(0);
    }

    private void writeToFile(String stacktrace, String filename) {
        try {
            BufferedWriter bos = new BufferedWriter(new FileWriter(localPath + "/" + filename));
            bos.write(stacktrace);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
