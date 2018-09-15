package half_blood_prince.androidruntimepermissionhelper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import half_blood_prince.androidruntimepermissionhelper.base.ResourceHelper;

/**
 * @author Half-Blood-Prince
 */
public class Utils {

    /**
     * Callback method used when clicking the button's in alert dialog.
     */
    public interface DialogClickListener {

        /**
         * Callback method triggered when user presses the action button in alert dialog.
         *
         * @param buttonType Type of the button, to indicate which button is pressed.
         */
        void onClick(int buttonType);
    }

    /**
     * Show this alert when user denied the location permission.
     *
     * @param context  Context from which this dialog launched.
     * @param listener Listener to delegate the action click event.
     * @param message  to show to the user, that why this permission is necessary.
     */
    public static void showPermissionDeniedAlert(@NonNull final Context context, @Nullable final DialogClickListener
            listener, @NonNull String message) {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (which == AlertDialog.BUTTON_POSITIVE) {
                    context.startActivity(Utils.getAppSettingsIntent(context.getPackageName()));
                    dialog.dismiss();
                    if (null != listener)
                        listener.onClick(AlertDialog.BUTTON_POSITIVE);
                } else {
                    if (null != listener)
                        listener.onClick(AlertDialog.BUTTON_NEGATIVE);
                }
            }
        };
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(ResourceHelper.getString(R.string.text_settings), onClickListener)
                .setNegativeButton(ResourceHelper.getString(R.string.action_cancel), onClickListener)
                .setCancelable(false)
                .show();

    }

    /**
     * Return the intent object to open application setting page for this app.
     *
     * @param packageName Package name of this application.
     * @return The intent object to open application setting page for this app.
     */
    public static Intent getAppSettingsIntent(String packageName) {
        Intent appSettingsIntent = new Intent();
        appSettingsIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        appSettingsIntent.setData(uri);
        return appSettingsIntent;
    }
}
