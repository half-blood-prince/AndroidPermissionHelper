package half_blood_prince.androidruntimepermissionhelper.base;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.Toast;

import half_blood_prince.androidruntimepermissionhelper.App;

/**
 * @author Half-Blood-Prince
 */
public final class ResourceHelper {

    private ResourceHelper() {
        //To forbid object creation from outside world.
    }

    public static void showBriefToast(@StringRes int stringId) {
        showBriefToast(getString(stringId));
    }

    public static void showBriefToast(@NonNull String message) {
        Toast.makeText(App.getInstance(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Return the string associated with the {@code id}.
     *
     * @param id Id referring the string to access.
     * @return The string associated with the {@code id}.
     */
    public static String getString(@StringRes int id) {
        return App.getInstance().getResources().getString(id);
    }
}
