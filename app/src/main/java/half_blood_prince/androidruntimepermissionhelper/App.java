package half_blood_prince.androidruntimepermissionhelper;

import android.app.Application;

/**
 * @author Half-Blood-Prince
 */
public class App extends Application {

    private static App sInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static App getInstance() {
        return sInstance;
    }
}
