package pt.pedro.textfileviewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Context mContext;
    LogCatUtil logCat;
    private ConstraintLayout mainActivityLayout;
    private boolean firstBack = true;
    private static final int READ_REQUEST_CODE = 42;
    private TextView testFile;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        firstBack = true;
        testFile.setText(" ");
        switch (item.getItemId()) {
            case R.id.openFile:
                performFileSearch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        logCat = new LogCatUtil(mContext);
        mainActivityLayout = findViewById(R.id.mainActivityLayout);
        testFile = findViewById(R.id.testFile);

        if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler))
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler("TextFileViewer", mContext));

        if(Build.VERSION.SDK_INT <= 22){
            startAppConnection();
        }else {
            requestForSpecificPermission();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(firstBack){
                firstBack = false;
                logCat.infoToast("Press back again to exit", false);
            }
            else{
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                logCat.infoToast("Done", false);
                this.finish();
                System.exit(0);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void requestForSpecificPermission() {
        int PERMISSION_ALL = 101;
        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
                //Manifest.permission.ACCESS_WIFI_STATE,
                //Manifest.permission.CHANGE_WIFI_STATE,
                //Manifest.permission.ACCESS_FINE_LOCATION,
                //Manifest.permission.ACCESS_COARSE_LOCATION,
                //Manifest.permission.INTERNET
        };

        hasPermissions(this, PERMISSIONS);
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
    }

    public void hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                ActivityCompat.checkSelfPermission(context, permission);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        boolean result_permission = true;
        for (int grantResult : grantResults) {
            if (grantResult == -1)
                result_permission = false;
        }
        if (!result_permission) {
            logCat.errorPopUp("Permissions", "Please accept the permissions!!!\nIt is necessary to accept the permissions to run da app!!!", true);
        } else {
            //logCat.snackBar(mainActivityLayout, "Tudo OK", true, LogCatUtil.INFO_TYPE.CONFIRM);
            startAppConnection();
        }
    }

    private void startAppConnection() {
        logCat.infoToast("Welcome to Text File Viewer", false);
    }

    private void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra("android.content.extra.SHOW_ADVANCED", true);

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    readTextFromUri(uri);
                } catch (IOException e) {
                    logCat.snackBar(mainActivityLayout, "Error open file text.\n"+e.toString(), true, LogCatUtil.INFO_TYPE.ERROR);
                    e.printStackTrace();
                }
            }
        }
    }

    private void readTextFromUri(Uri uri) throws IOException {
        if(Objects.requireNonNull(getContentResolver().getType(uri)).split("/")[0].equals("text")) {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            testFile.setText(stringBuilder.toString());
            //logCat.terminal("core_", stringBuilder.toString());
        }else{
            logCat.terminal("core_", getContentResolver().getType(uri));
            logCat.snackBar(mainActivityLayout, "File not supported ( "+getContentResolver().getType(uri)+" )",true, LogCatUtil.INFO_TYPE.ALERT);
        }
    }
}
