package half_blood_prince.androidruntimepermissionhelper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import half_blood_prince.androidruntimepermissionhelper.base.Permission;
import half_blood_prince.androidruntimepermissionhelper.base.PermissionHelper;

/**
 * @author Half-Blood-Prince
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private View btnRequestMultiplePermission;

    private TextView tvPermissionInfo;

    private PermissionHelper mPermissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findGlobalViews();
        initGlobalInstance();
        setListeners();
    }

    private void findGlobalViews() {
        btnRequestMultiplePermission = findViewById(R.id.btn_request_multiple_permission);
        tvPermissionInfo = findViewById(R.id.tv_permission_info);
    }

    private void initGlobalInstance() {
        mPermissionHelper = PermissionHelper.fromActivity(this, Permission.getAllPermission());
    }

    private void setListeners() {
        btnRequestMultiplePermission.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_request_multiple_permission) {
            clearPermissionInfo();
            startRequestingMultiplePermission();
        }
    }


    private void startRequestingMultiplePermission() {

        mPermissionHelper.setResultCallback(new PermissionHelper.PermissionResultCallback() {
            @Override
            public void onResult(@NonNull PermissionHelper.PermissionResult... permissionResults) {
                boolean isAnyPermissionDeniedCompletely = false;

                for (PermissionHelper.PermissionResult result : permissionResults) {
                    Log.d("garu", "mPermissionHelper Result " + result);
                    updatePermissionInfo("Result ::  " + result);

                    if (result.isPermissionDeniedCompletely())
                        isAnyPermissionDeniedCompletely = true;
                }

                if (isAnyPermissionDeniedCompletely) {
                    Utils.showPermissionDeniedAlert(MainActivity.this, new Utils.DialogClickListener() {
                        @Override
                        public void onClick(int buttonType) {
                            if (buttonType == AlertDialog.BUTTON_NEGATIVE)
                                Toast.makeText(MainActivity.this,
                                        "Button type " + buttonType, Toast.LENGTH_SHORT)
                                        .show();
                        }
                    }, "Permission Denied Completely");
                }
            }
        });

        mPermissionHelper.startCheckingPermission();
    }

    private void clearPermissionInfo() {
        tvPermissionInfo.setText("");
    }

    private void updatePermissionInfo(@NonNull String info) {
        tvPermissionInfo.append(info);
        tvPermissionInfo.append("\n");
        Log.d("garu", info);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_show_app_settings) {
            startActivity(Utils.getAppSettingsIntent(getPackageName()));
            return true;
        } else if (itemId == R.id.menu_open_repo_in_git) {
            openThisProjectInGithub();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openThisProjectInGithub() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://github.com/half-blood-prince/AndroidPermissionHelper.git"));
        if (null != intent.resolveActivity(getPackageManager()))
            startActivity(intent);

    }
}

