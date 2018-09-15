
package half_blood_prince.androidruntimepermissionhelper.base;

import android.Manifest;
import android.app.Activity;

import java.util.LinkedHashMap;
import java.util.Map;

import half_blood_prince.androidruntimepermissionhelper.R;

/**
 * Class holds the permission related stuffs.
 * <p>
 * Run time permission to ask with unique request code and the messages to show in case the user denied the
 * permission previously.
 *
 * @author Half-Blood-Prince
 */
public final class Permission {

    /**
     * Enum associated the unique request code with the Run time permission.
     */
    public enum Permissions {

        /**
         * Enum constant having the unique identifier, which is useful when requesting
         * {@link Manifest.permission#WRITE_EXTERNAL_STORAGE} permission.
         */
        REQ_CODE_WRITE_EXTERNAL_STORAGE(0xA0, Manifest.permission.WRITE_EXTERNAL_STORAGE),

        REQ_CODE_ACCESS_LOCATION_(0xA1, Manifest.permission.ACCESS_FINE_LOCATION);

        /**
         * Request code uniquely identifying the permission.
         */
        int reqCode;

        /**
         * String representing the permission.
         */
        String permission;

        Permissions(int reqCode, String permission) {
            this.reqCode = reqCode;
            this.permission = permission;
        }

        /**
         * @return The {@link #reqCode} of this instance.
         */
        public int getReqCode() {
            return reqCode;
        }
    }

    /*Permission to write External storage -- START -- */

    /**
     * Method return the map object responsible for asking the permission to write the external storage.
     *
     * @return Map contains the permission and the request code associated with it, with the corresponding message to
     * show the need of the permission when the user denied it previously.
     * @see PermissionHelper#PermissionHelper(Activity, Map)
     */
    public static Map<Integer, PermissionHelper.PermissionModel> getWriteExternalStoragePermission() {
        Map<Integer, PermissionHelper.PermissionModel> permission = new LinkedHashMap<>();
        permission.put(Permissions.REQ_CODE_WRITE_EXTERNAL_STORAGE.reqCode,
                getWriteExternalStoragePermissionModel());
        return permission;
    }

    /**
     * Construct and return the {@link PermissionHelper.PermissionModel} for Permission {@link
     * Manifest.permission#WRITE_EXTERNAL_STORAGE} with the rationale message.
     * <p>
     * The rationale message is useful when the user denied giving permission earlier.
     *
     * @return return the {@link PermissionHelper.PermissionModel} for Permission {@link
     * Manifest.permission#WRITE_EXTERNAL_STORAGE}.
     */
    private static PermissionHelper.PermissionModel getWriteExternalStoragePermissionModel() {
        return new PermissionHelper.PermissionModel(
                Permissions.REQ_CODE_WRITE_EXTERNAL_STORAGE.permission,
                ResourceHelper.getString(R.string.text_permission_required),
                ResourceHelper.getString(R.string.msg_write_external_storage_permission_rationale));
    }

    /*Permission to write External storage -- END -- */

    /*Permission to Access Fine Location -- START -- */

    /**
     * Method return the map object responsible for asking the permission to write the external storage.
     *
     * @return Map contains the permission and the request code associated with it, with the corresponding message to
     * show the need of the permission when the user denied it previously.
     * @see PermissionHelper#PermissionHelper(Activity, Map)
     */
    public static Map<Integer, PermissionHelper.PermissionModel> getAccessFineLocationPermission() {
        Map<Integer, PermissionHelper.PermissionModel> permission = new LinkedHashMap<>();
        permission.put(Permissions.REQ_CODE_WRITE_EXTERNAL_STORAGE.reqCode,
                getWriteExternalStoragePermissionModel());
        return permission;
    }

    /**
     * Construct and return the {@link PermissionHelper.PermissionModel} for Permission {@link
     * Manifest.permission#ACCESS_FINE_LOCATION} with the rationale message.
     * <p>
     * The rationale message is useful when the user denied giving permission earlier.
     *
     * @return return the {@link PermissionHelper.PermissionModel} for Permission {@link
     * Manifest.permission#ACCESS_FINE_LOCATION}.
     */
    private static PermissionHelper.PermissionModel getAccessFineLocationPermissionModel() {
        return new PermissionHelper.PermissionModel(
                Permissions.REQ_CODE_ACCESS_LOCATION_.permission,
                ResourceHelper.getString(R.string.text_permission_required),
                ResourceHelper.getString(R.string.msg_write_external_storage_permission_rationale));
    }

    /*Permission to Access Fine Location -- END -- */

    /**
     * Method return the map object responsible for requesting all the permission listed in {@link Permissions}
     *
     * @return Map contains the permission and the request code associated with it, with the corresponding message to
     * show the need of the permission when the user denied it previously.
     * @see PermissionHelper#PermissionHelper(Activity, Map)
     */
    public static Map<Integer, PermissionHelper.PermissionModel> getAllPermission() {
        Map<Integer, PermissionHelper.PermissionModel> allPermission = new LinkedHashMap<>();
        allPermission.put(Permissions.REQ_CODE_WRITE_EXTERNAL_STORAGE.reqCode,
                getWriteExternalStoragePermissionModel());
        allPermission.put(Permissions.REQ_CODE_ACCESS_LOCATION_.reqCode, getAccessFineLocationPermissionModel());
        return allPermission;
    }


}
