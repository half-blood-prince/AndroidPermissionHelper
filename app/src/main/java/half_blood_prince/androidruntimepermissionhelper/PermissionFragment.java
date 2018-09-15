package half_blood_prince.androidruntimepermissionhelper;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import half_blood_prince.androidruntimepermissionhelper.base.Permission;
import half_blood_prince.androidruntimepermissionhelper.base.PermissionHelper;

/**
 * Created by Sridhar on 15-09-2018.
 *
 * @author Half-Blood-Prince
 **/
public class PermissionFragment extends Fragment implements View.OnClickListener {

    private View btnRequestMultiplePermission;

    private TextView tvPermissionInfo;

    private PermissionHelper mPermissionHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_permission, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findGlobalViews(view);
        initGlobalInstance();
        setListeners();
    }

    private void findGlobalViews(View rootView) {
        btnRequestMultiplePermission = rootView.findViewById(R.id.btn_request_multiple_permission);
        tvPermissionInfo = rootView.findViewById(R.id.tv_permission_info);
    }

    private void initGlobalInstance() {
        assert null != getActivity();
        mPermissionHelper = PermissionHelper.fromFragment(this, getActivity(), Permission.getAllPermission());
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
                for (PermissionHelper.PermissionResult result : permissionResults) {
                    Log.d("garu", "mPermissionHelper Result " + result);
                    updatePermissionInfo("Result ::  " + result);
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
}
