package half_blood_prince.androidruntimepermissionhelper.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * PermissionHelper class helps to make the life easier working with android M runtime permission.
 * <p>
 * We all know working with android M requires explicitly asking permission to the user at run time and work according
 * to the result, This process is asynchronous (i.e you request permission and get the user result at {@link
 * android.support.v7.app.AppCompatActivity#onRequestPermissionsResult(int, String[], int[])} method) and you continue
 * the work from there, little messy right. This class helps you to perform synchronous operation when the user grant or
 * deny the permission you requested. This class also shows the rationale message when the user denied the permission
 * previously (help to explain the need of the permission to the user). This class has a special method {@link
 * PermissionHelper#permissionDeniedCompletely(int)} used to tell the permission is denied completely by the user.
 * <p>
 * Note * This class work in asynchronous fashion only, but it will provide the appropriate callback method so we can
 * perform our corresponding function according to the result at the same block of code.
 *
 * @see PermissionHelper#permissionDeniedCompletely(int) you can use this callback to inform the user that the permision
 * is required and needs to be enabled in the app settings.
 *
 * @author Half-Blood-Prince
 */
public class PermissionHelper {

    private static final String ACTION_OK = "Ok";

    private static final String ACTION_CANCEL = "Cancel";

    /**
     * Model class used to keep the required attributes about the permission.
     */
    public static final class PermissionModel {

        /**
         * Permission string to request. Constants defined in the {@link android.Manifest.permission}.
         */
        String permission;

        /**
         * Title of the dialog box to show when the user denied the permission previously.
         */
        String rationaleTitle;

        /**
         * Body of the dialog box to show when the user denied the permission previously.
         */
        String rationaleMessage;

        /**
         * Positive button text, default text is okay.
         */
        String posBtnText;

        /**
         * Negative button text,default text is Cancel.
         */
        String negBtnText;

        /**
         * Constructor initialized with the permission, rationaleTitle, rationaleMessage, and default values for
         * posBtnText and negBtnText.
         *
         * @param permission       Permission to request.
         * @param rationaleTitle   Dialog title to show if user denied the permission previously.
         * @param rationaleMessage Dialog content to show if user denied the permission previously.
         */
        public PermissionModel(String permission, String rationaleTitle, String rationaleMessage) {
            this(permission, rationaleTitle, rationaleMessage, ACTION_OK, ACTION_CANCEL);
        }

        /**
         * Constructor initialized with the permission, rationaleTitle, rationaleMessage,posBtnText. and negBtnText.
         *
         * @param permission       Permission to request.
         * @param rationaleTitle   Dialog title to show if user denied the permission previously.
         * @param rationaleMessage Dialog content to show if user denied the permission previously.
         * @param posBtnText       Positive button text.
         * @param negBtnText       Negative button text.
         */
        public PermissionModel(String permission, String rationaleTitle,
                               String rationaleMessage, String posBtnText, String negBtnText) {
            this.permission = permission;
            this.rationaleTitle = rationaleTitle;
            this.rationaleMessage = rationaleMessage;
            this.posBtnText = posBtnText;
            this.negBtnText = negBtnText;
        }
    }

    private static final class CallbackDispatchHandler extends Handler {

        @IntDef({WhichMethod.SINGLE_RESULT, WhichMethod.GROUP_RESULT})
        public @interface WhichMethod {
            int SINGLE_RESULT = 0x01;
            int GROUP_RESULT = 0x02;
        }

        private PermissionResultCallback mResultCallback;

        public CallbackDispatchHandler(Looper looper) {
            super(looper);
        }

        public void setResultCallback(PermissionResultCallback resultCallback) {
            mResultCallback = resultCallback;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WhichMethod.SINGLE_RESULT:
                    if (null != mResultCallback)
                        mResultCallback.onResult(new PermissionResult(msg.arg1, msg.what));
                    break;
                case WhichMethod.GROUP_RESULT:
                    PermissionResult[] groupResult = (PermissionResult[]) msg.obj;
                    if (null != groupResult && null != mResultCallback)
                        mResultCallback.onResult(groupResult);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

        private void dispatchPermissionGranted(int permissionRequestId) {
            obtainMessage(WhichMethod.SINGLE_RESULT, permissionRequestId, -1).sendToTarget();
        }

        private void dispatchPermissionDenied(int permissionRequestId) {
            obtainMessage(WhichMethod.SINGLE_RESULT, permissionRequestId, -1).sendToTarget();
        }

        private void dispatchPermissionDeniedCompletely(int permissionRequestId) {
            obtainMessage(WhichMethod.SINGLE_RESULT, permissionRequestId, -1).sendToTarget();
        }

        private void dispatchGroupResult(@NonNull PermissionResult... permissionResults) {
            obtainMessage(WhichMethod.GROUP_RESULT, permissionResults).sendToTarget();
        }

    }

    public interface PermissionResultCallback {
        void onResult(@NonNull PermissionResult... permissionResults);
    }

    public static final class PermissionResult {

        @IntDef({PermissionResultState.GRANTED, PermissionResultState.DENIED, PermissionResultState.DENIED_COMPLETELY})
        public @interface PermissionResultState {
            int GRANTED = 0x01;
            int DENIED = 0x02;
            int DENIED_COMPLETELY = 0x03;
        }

        private int mRequestId;

        @PermissionResultState
        private int mPermissionResultState;

        /**
         * Constructor for a Pair.
         *
         * @param mRequestId             The Request ID used when requesting the permission.
         * @param mPermissionResultState The Result of the permission.
         */
        public PermissionResult(int mRequestId, @PermissionResultState int mPermissionResultState) {
            this.mRequestId = mRequestId;
            this.mPermissionResultState = mPermissionResultState;
        }

        public int getRequestId() {
            return mRequestId;
        }

        @PermissionResultState
        public int getResult() {
            return mPermissionResultState;
        }

        public boolean isPermissionGranted() {
            return getResult() == PermissionResultState.GRANTED;
        }

        public boolean isPermissionDenied() {
            return getResult() == PermissionResultState.DENIED;
        }

        public boolean isPermissionDeniedCompletely() {
            return getResult() == PermissionResultState.DENIED_COMPLETELY;
        }

        public String stateToEng() {
            switch (this.mPermissionResultState) {
                case PermissionResultState.GRANTED:
                    return "Permission Granted";
                case PermissionResultState.DENIED:
                    return "Permission Denied";
                case PermissionResultState.DENIED_COMPLETELY:
                    return "Permission Denied Completely";
                default:
                    return "Unknown Permission state";
            }
        }

        @Override
        public String toString() {
            return String.format(Locale.ENGLISH, "Permission Id : %d  Result : %s", mRequestId, stateToEng());
        }
    }


    public static PermissionHelper fromActivity(@NonNull Activity activity, @NonNull Map<Integer, PermissionModel>
            permissionMap) {
        return new PermissionHelper(activity, permissionMap);
    }

    public static PermissionHelper fromFragment(@NonNull Fragment fragment, @NonNull Activity activity, @NonNull
            Map<Integer, PermissionModel> permissionMap) {
        return new PermissionHelper(fragment, activity, permissionMap);
    }

    private CallbackDispatchHandler mMainThreadHandler = new CallbackDispatchHandler(Looper.getMainLooper());

    /**
     * Activity reference.
     */
    private Activity activity;

    private Fragment fragment;

    /**
     * LinkedHashMap to hold permission to check and request access with their corresponding id.
     */
    private LinkedHashMap<Integer, PermissionModel> permissionMap;

    /**
     * Queue to process the pending permission.
     */
    private Deque<Integer> queue = new ArrayDeque<>();

    /**
     * List holds the requested id of all the granted permission.
     *
     * @see PermissionHelper#onGroupOfPermissionRequestResult(PermissionResult...)
     */
    private ArrayList<Integer> grantedPermission = new ArrayList<>();

    /**
     * List hold the requested id of all the denied permission.
     *
     * @see PermissionHelper#onGroupOfPermissionRequestResult(PermissionResult...)
     */
    private ArrayList<Integer> deniedPermission = new ArrayList<>();

    /**
     * List hold the requested id of all the permission which are completely denied by the user.
     *
     * @see PermissionHelper#onGroupOfPermissionRequestResult(PermissionResult...)  Note : The user can select never
     * to ask
     * permission by clicking "Never ask" and click Deny button.
     */
    private ArrayList<Integer> completelyDeniedPermission = new ArrayList<>();

    /**
     * Flag is used to determine whether a group of permission requested or single permission requested.
     */
    private boolean isGroupOfPermissionRequested;

    /**
     * Constructor used to initialize this class object.
     *
     * @param activity      Activity reference.
     * @param permissionMap Map contains permission to check and request with their corresponding id This also contains
     *                      the rationale message to show when the user denied the permission previously.
     */
    private PermissionHelper(@NonNull Activity activity,
                             @NonNull Map<Integer, PermissionModel> permissionMap) {
        this.activity = activity;
        this.permissionMap = (LinkedHashMap<Integer, PermissionModel>) permissionMap;

    }

    /**
     * Constructor used to initialize this class object.
     *
     * @param fragment      Fragment reference.
     * @param activity      Activity reference.
     * @param permissionMap Map contains permission to check and request with their corresponding id This also contains
     *                      the rationale message to show when the user denied the permission previously.
     */
    private PermissionHelper(@NonNull Fragment fragment, @NonNull Activity activity,
                             @NonNull Map<Integer, PermissionModel> permissionMap) {
        this.fragment = fragment;
        this.activity = activity;
        this.permissionMap = (LinkedHashMap<Integer, PermissionModel>) permissionMap;

    }

    public void setResultCallback(PermissionResultCallback resultCallback) {
        mMainThreadHandler.setResultCallback(resultCallback);
    }

    /**
     * This method initialize the queue and start the checking process.
     * <p>
     * Note * Please make sure to call {@link PermissionHelper#onRequestPermissionsResult(int, String[], int[])} method
     * from the activity {@link android.support.v7.app.AppCompatActivity#onRequestPermissionsResult(int, String[],
     * int[])} method, no callbacks relating to permission request status will be called if {@link
     * PermissionHelper#onRequestPermissionsResult(int, String[], int[])} method not get called.
     */
    public final void startCheckingPermission() {
        isGroupOfPermissionRequested = permissionMap.keySet().size() > 1;
        queue.addAll(permissionMap.keySet());
        if (!queue.isEmpty())
            checkPermission(queue.peek());
    }

    /**
     * This method checks whether this permission is granted or denied, if not granted it will ask request the
     * permission.
     *
     * @param permissionID Unique permission id pointing some permission model object in permissionMap.
     * @see PermissionModel
     */
    private void checkPermission(final int permissionID) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            notifyPermissionGranted(permissionID);
            return;
        }

        String permission = permissionMap.get(permissionID).permission;

        if (isPermitted(permission)) {
            notifyPermissionGranted(permissionID);
            return;
        }

        if (doIHaveToExplain(permission)) {
            // show msg, attach callback
            explainAboutPermission(activity, permissionID, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == AlertDialog.BUTTON_POSITIVE)
                        requestPermission(permissionID);
                    else
                        notifyPermissionDenied(permissionID);

                    dialog.dismiss();
                }
            });
        } else {
            requestPermission(permissionID);
        }

    }

    /**
     * This method check whether the requested permission has been granted or not.
     *
     * @param permission Permission to check.
     * @return true if the access is granted for the requested permission.
     */
    private boolean isPermitted(String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * This method request for the permission.
     * <p>
     * Note * As of now only one permission can be requested for each request, even though android support sending array
     * of permission to single request.i am not using that feature. however you can send more that one permission by
     * putting that in map {@link PermissionHelper#PermissionHelper(Activity, Map)}, this will give you the option to
     * show the rationale message to the user if permission denied previously.
     *
     * @param permissionID Corresponding permissionID for the permission to request.
     */
    private void requestPermission(int permissionID) {
        String permission = permissionMap.get(permissionID).permission;
        if (null != permission) {
            if (null != fragment)
                fragment.requestPermissions(new String[]{permission}, permissionID);
            else
                ActivityCompat.requestPermissions(activity, new String[]{permission}, permissionID);
        }
    }

    /**
     * This method must be called from the activity {@link android.support.v7.app
     * .AppCompatActivity#onRequestPermissionsResult(int, String[], int[])} method.
     * <p>
     * Failing to call this method give no callback like {@link #permissionGranted(int)}, {@link #permissionDenied(int)}
     * {@link #permissionDeniedCompletely(int)} {@link #onGroupOfPermissionRequestResult(PermissionResult...)} .
     *
     * @param requestCode  RequestCode is nothing but the corresponding permission id.
     * @param permissions  String array of permission (not using this right now).
     * @param grantResults GrantResults.
     */
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                 @NonNull int[] grantResults) {

        if (!permissionMap.containsKey(requestCode))
            return;

        String permission = permissionMap.get(requestCode).permission;
        if (null == permission)
            permission = "";

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && (permissions.length > 0 && permission.equals(permissions[0]))
                ) {
            notifyPermissionGranted(requestCode);
        } else handleDeny(requestCode);

    }

    /**
     * Removes the top element in the queue and proceed checking the next permission if available.
     * <p>
     * If group of permission is requested then {@link #onGroupOfPermissionRequestResult(PermissionResult...)} is
     * called with
     * the grant results.
     */
    private void checkNextPermission() {

        queue.poll();

        if (!queue.isEmpty()) {
            checkPermission(queue.peek());
        } else {
            if (isGroupOfPermissionRequested) {
                dispatchGroupResult();
            }
        }
    }

    /**
     * This method will get invoked when the user denied the permission. This method will determine whether the
     * permission is completely denied or currently denied.
     *
     * @param permissionID id of the permission to check.
     */
    private void handleDeny(int permissionID) {
        if (doIHaveToExplain(permissionMap.get(permissionID).permission)) {
            notifyPermissionDenied(permissionID);
        } else {
            notifyPermissionDeniedCompletely(permissionID);
        }
    }

    /**
     * Check whether we have to clarify the user about the permission we are requesting, why we need that permission and
     * what happens if we don't have it etc.
     *
     * @param permission permission to check whether the explanation needed to show.
     * @return true if we need to explain about the permission false otherwise.
     */
    private boolean doIHaveToExplain(String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    /**
     * This method show the dialog which contains the information about why we need this permission and what happens if
     * we don't get this access etc.
     *
     * @param activity        Activity reference.
     * @param permissionID    Permission id mapped to the corresponding permission.
     * @param onClickListener Listener to delegate the dialog click events back to the logic.
     */
    private void explainAboutPermission(Activity activity, int permissionID,
                                        DialogInterface.OnClickListener onClickListener) {

        PermissionModel permissionModel = permissionMap.get(permissionID);

        AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setTitle(permissionModel.rationaleTitle);
        dialog.setMessage(permissionModel.rationaleMessage);
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, permissionModel.posBtnText, onClickListener);
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, permissionModel.negBtnText, onClickListener);

        dialog.setCancelable(false);

        dialog.show();
    }

    /**
     * This method called at the end of requesting group of permission, passing the grant results back to the caller.
     */
    private void dispatchGroupResult() {
        int size = grantedPermission.size() +
                deniedPermission.size() + completelyDeniedPermission.size();

        List<PermissionResult> permissionResults = new ArrayList<>(size);

        for (int permissionId : grantedPermission) {
            permissionResults.add(new PermissionResult(permissionId,
                    PermissionResult.PermissionResultState.GRANTED));
        }
        grantedPermission.clear();

        for (int permissionId : deniedPermission) {
            permissionResults.add(new PermissionResult(permissionId,
                    PermissionResult.PermissionResultState.DENIED));
        }
        deniedPermission.clear();

        for (int permissionId : completelyDeniedPermission) {
            permissionResults.add(new PermissionResult(permissionId,
                    PermissionResult.PermissionResultState.DENIED_COMPLETELY));
        }
        completelyDeniedPermission.clear();


        onGroupOfPermissionRequestResult(permissionResults.toArray(new PermissionResult[size]));
    }

    /**
     * Check whether a single permission requested or a group of permission requested, if single permission is requested
     * {@link #permissionGranted(int)} method is dispatched, otherwise check next permission {@link
     * #checkNextPermission()}.
     *
     * @param permissionId Permission id uniquely identifying the permission that is requested.
     */
    private void notifyPermissionGranted(int permissionId) {
        if (isGroupOfPermissionRequested) {
            grantedPermission.add(permissionId);
            checkNextPermission();
        } else
            permissionGranted(permissionId);
    }

    /**
     * Check whether a single permission requested or a group of permission requested, if single permission is requested
     * {@link #permissionDenied(int)} method is dispatched, otherwise check next permission {@link
     * #checkNextPermission()}.
     *
     * @param permissionId Permission id uniquely identifying the permission that is requested.
     */
    private void notifyPermissionDenied(int permissionId) {
        if (isGroupOfPermissionRequested) {
            deniedPermission.add(permissionId);
            checkNextPermission();
        } else permissionDenied(permissionId);
    }

    /**
     * Check whether a single permission requested or a group of permission requested, if single permission is requested
     * {@link #permissionDeniedCompletely(int)} method is dispatched, otherwise check next permission {@link
     * #checkNextPermission()}.
     *
     * @param permissionId Permission id uniquely identifying the permission that is requested.
     */
    private void notifyPermissionDeniedCompletely(int permissionId) {
        if (isGroupOfPermissionRequested) {
            completelyDeniedPermission.add(permissionId);
            checkNextPermission();
        } else permissionDeniedCompletely(permissionId);
    }

    /**
     * Callback method to inform about the permission has been granted.
     *
     * @param permissionID Id mapped to PermissionModel.
     * @see PermissionModel
     */
    protected void permissionGranted(int permissionID) {
        mMainThreadHandler.dispatchPermissionGranted(permissionID);
    }

    /**
     * Callback method to inform about the permission has been denied.
     *
     * @param permissionID Id mapped to PermissionModel.
     * @see PermissionModel
     */
    protected void permissionDenied(int permissionID) {
        mMainThreadHandler.dispatchPermissionDenied(permissionID);
    }

    /**
     * Callback method to inform about the permission has been denied completely, and android will no longer display
     * that popup saying this app needs this permission.
     * <p>
     * This is the special function since android won't indicate us by saying user denied the permission completely( i.e
     * when the user press "Never Ask" and click deny this scenario occurs). You can override this method in the
     * sub-class to get the details about the permission which is completely denied by the user.
     *
     * @param permissionID id mapped to PermissionModel.
     * @see PermissionModel
     */
    protected void permissionDeniedCompletely(int permissionID) {
        mMainThreadHandler.dispatchPermissionDeniedCompletely(permissionID);
    }

    /**
     * This method called at the end of requesting more than one permission.
     * <p>
     * This method will be called only when permission requested is more than one.
     * <p>
     * This function will get called in the sub-class (if it's overridden there), when this class done requesting and
     * processing all the permission {@link #permissionMap} sent to this class.
     *
     * @param permissionResults The array of {@link PermissionResult}. contains permission result for all the
     *                          requested permission.
     * @see #permissionMap
     * @see #queue
     */
    protected void onGroupOfPermissionRequestResult(@NonNull PermissionResult... permissionResults) {
        // Override this method in the sub-class to get the result of permission request result.
        // The array <code>requestedIds</code> and <code>grantResults</code> are symmetrical(i.e both having same
        // length. for each request in <code>requestedIds</code> the result will be available in
        // <code>grantResults</code> array at corresponding index.
        mMainThreadHandler.dispatchGroupResult(permissionResults);
    }

}