package geoimage.ret.geoimage;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GetLocationService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS

    private static final String ACTION_LOCATION = "location";
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;


    public GetLocationService() {
        super("GetLocationService");
    }


    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startGetLocation(Context context) {
        Intent intent = new Intent(context, GetLocationService.class);
        intent.setAction(ACTION_LOCATION);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_LOCATION.equals(action)) {
                handleLocation();
            }
        }
    }


    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleLocation() {
        // TODO: Handle action Baz
        buildGoogleApiClient();
        mGoogleApiClient.connect();

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1);
        mLocationRequest.setFastestInterval(1);
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        sendBroadcast(connectionResult.getErrorMessage());
    }

    @Override
    public void onLocationChanged(Location location) {


        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        App.setLat(location.getLatitude());
        App.setLon(location.getLongitude());

        sendBroadcast("LocationObtained");


    }

    private void sendBroadcast(String errStatus) {
        Intent intent = new Intent("location");
        intent.putExtra("status", errStatus);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        stopSelf();
    }


}
