package debaod.topswim;

import android.app.Application;
import android.content.Context;

/**
 * Created by debaod on 3/25/2015.
 */
public class myApplication  extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        myApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return myApplication.context;
    }
}
