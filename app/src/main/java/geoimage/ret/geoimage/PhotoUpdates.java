package geoimage.ret.geoimage;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PhotoUpdates extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_PHOTOLISTENER = "photolistener";
    static private Context callingContext;


    public PhotoUpdates() {
        super("PhotoUpdates");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startPhotoListener(Context context) {
        callingContext = context;
        Intent intent = new Intent(context, PhotoUpdates.class);
        intent.setAction(ACTION_PHOTOLISTENER);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PHOTOLISTENER.equals(action)) {
                handlePhotoListener();
            }
        }
    }


    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handlePhotoListener() {

        // get a Calendar object with current time
        Calendar cal = Calendar.getInstance();
        // add 30 seconds to the calendar object
        cal.setTimeInMillis(System.currentTimeMillis() + 120000);

        //registering our pending intent with alarmmanager
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(sendIntent(this));
        alarmManager.setInexactRepeating(AlarmManager.RTC, cal.getTimeInMillis(), 120000
                , sendIntent(this));



    }

    private PendingIntent sendIntent(Context context) {
        Intent intent = new Intent(ACTION_PHOTOLISTENER);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return pendingIntent;
    }

}
