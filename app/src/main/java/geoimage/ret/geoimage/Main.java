package geoimage.ret.geoimage;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Main extends AppCompatActivity {
    Toolbar toolbar;
    static View rootview;
    RecyclerView recyclerView;
    int start = 0;
    int end = 0;
    LinkedHashMap<String, ArrayList<ParseObject>> urlHashMap;
    LinearLayoutManager linearLayoutManager;
    ArrayList<ParseObject> picsField;
    ArrayList<String> keyList;
    int rowSize;
    //sublist of all users , default display first 3, will be used for pagination also.
    ArrayList<ArrayList<ParseObject>> sublist;
    RootImageAdapter adapter;
    private boolean reachedEndOfLast = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_all);

        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbarlayout object
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);

        //snack stuff
        rootview = findViewById(R.id.displayalllayout);
        makeSnack("Obtaining Location...");

        if (android.os.Build.VERSION.SDK_INT == 23) {
            requestPermission();
        } else {
            startLocService();

        }


    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("location")) {
                makeSnack("Location Obtained!");
                if (!PhotoUpdates.getStarted() == true) {
                    PhotoUpdates.startPhotoListener(getApplicationContext());
                }
                displayImages();
            } else if (intent.getAction().equals("photolistener")) {
                makeSnack("Updating photos.");

                //TODO:Fetch/Warmup new images
            }

        }
    };

    static void makeSnack(String text) {
        Snackbar.make(rootview, text, Snackbar.LENGTH_LONG).show();
    }


    @TargetApi(23)
    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            }
        } else {
            startLocService();
        }


        if (shouldShowRequestPermissionRationale(
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                //TODO:Display rational dialog
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startLocService();
                }
            }
        }
    }


    public void startLocService() {
        GetLocationService.startGetLocation(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UploadImage.UploadImageRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                makeSnack("Uploading Photo...");
                //TODO: add seperate function to grab object IDs not already in the adapter
            }
        }

    }

    public void displayImages() {

        ParseGeoPoint parseGeoPoint = new ParseGeoPoint(App.getLat(), App.getLon());

        //get all images
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Images");
        query.whereWithinKilometers("location", parseGeoPoint, 100);
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() > 0) {
                    if (e == null) {
                        if (!objects.isEmpty()) {
                            setupAdapter((ArrayList) (objects));
                        }
                    } else if (e.getCode() == ParseException.REQUEST_LIMIT_EXCEEDED) {
                        makeSnack("Error! Reloading images.");
                        displayImages(); // How many times you do this is your business...
                    } else {
                        e.printStackTrace();
                        Log.v("error", e.getMessage());
                    }
                } else {
                    //TODO:// if not images, reregister loclistener
                    makeSnack("No images located near you.");
                }
            }
        });
    }


    public void setupAdapter(ArrayList<ParseObject> pics) {
        //create field so I don't have to pass around objects all the time
        picsField = pics;
        //get all user collections
        urlHashMap = FillAdapter();
        keyList = new ArrayList<>();
        //get each user
        keyList.addAll(urlHashMap.keySet());
        sublist = getKeyRange();
        //setup adapater
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewParent);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RootImageAdapter(this, sublist, linearLayoutManager);
        recyclerView.setAdapter(adapter);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //todo: add rows

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (reachedEndOfLast != true) {
                    if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == sublist.size() - 1) {
                        rowSize = linearLayoutManager.findLastCompletelyVisibleItemPosition() + 1;
                        adapter.addItem(getKeyRange());
                    }
                }
            }
        });


        //  cacheImages();

    }


//    public void cacheImages() {
//        new Thread(new Runnable() {
//            public void run() {
//                for (int i = (start + 3); (i < keyList.size()); i++) {
//                    ArrayList<ParseObject> userRow= new ArrayList<>(urlHashMap.get(keyList.get(i)));
//                    for (int j = 0; j < userRow.size(); j++) {
//                        Glide.with(getApplicationContext())
//                                .load(userRow.get(j).getParseFile("image").getUrl())
//                                .downloadOnly();
//                    }
//                }
//            }
//        }).start();
//    }

    /*
    A method to loop through all the user photos and obtain 1 hashmap row per user
    which contains all the photos by that user.
     */

    public LinkedHashMap<String, ArrayList<ParseObject>> FillAdapter() {
        LinkedHashMap<String, ArrayList<ParseObject>> hashMap = new LinkedHashMap<String, ArrayList<ParseObject>>();
        ArrayList<ParseObject> arrayList;

        for (ParseObject parseObject : picsField) {
            ParseUser createdBy = parseObject.getParseUser("createdBy");
            if (hashMap.containsKey(createdBy.getObjectId())) {
                hashMap.get(createdBy.getObjectId()).add(parseObject);
            } else {
                arrayList = new ArrayList<ParseObject>();
                arrayList.add(parseObject);
                hashMap.put(createdBy.getObjectId(), arrayList);
            }
        }
        return hashMap;
    }


    /*
    a function that uses the fillAdapaters keylist
    and gets the hashmap row returned from filladapater corresponding to a user
    i.e used for pagination to obtain a specific amount of users.
     */
    public ArrayList<ArrayList<ParseObject>> getKeyRange() {
        ArrayList<ArrayList<ParseObject>> userObjectSublist = new ArrayList<>();
        if (reachedEndOfLast != true) {
            if ((end + 3) > keyList.size()) {
                end = end + (keyList.size() % end);
                reachedEndOfLast = true;
            } else {
                end += 3;
            }
            for (int i = start; (i < keyList.size()) && (i < end); i++) {
                userObjectSublist.add(urlHashMap.get(keyList.get(i)));
            }
        }
        start = end;

        return userObjectSublist;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.basemenu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon_uploadimage:
                startActivityForResult(new Intent(this, UploadImage.class), UploadImage.UploadImageRequestCode);
                break;
            case R.id.icon_refresh:
                //keep photos in current location discard rest, and get new photos
                break;
            case R.id.icon_users:
                startActivity(new Intent(this, DisplayUsers.class));
                break;
            case R.id.icon_location:
                if (App.getDisplayingSelf() == true) {
                    //if displaying set to false and change icon
                    App.setDisplayingSelf(false);
                    item.setIcon(R.drawable.locationicon_off);
                    makeSnack("Nearby users will no longer find you.");
                } else {
                    //if not display turn it on and change icon
                    App.setDisplayingSelf(true);
                    item.setIcon(R.drawable.locationicon_on);
                    makeSnack("Nearby users can now find you.");
                }

                break;

        }

        return true;
    }


    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        unregisterReceiver(receiver);
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter("photolistener"));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("location"));

    }

}
