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
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class Main extends AppCompatActivity {
    static View rootview;
    Toolbar toolbar;
    ImageAdapter imageAdapter;
    RecyclerView mRecyclerView;
    List<ParseObject> subListForScrolling;
    List<ParseObject> picList;
    RecyclerView recyclerView;
    RecyclerView recyclerView2;
    int start = 0;
    int end = 3;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_all);

        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbarlayout object
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);


    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == "location") {
                makeSnack("Location Obtained!");
                PhotoUpdates.startPhotoListener(getApplicationContext());
                displayImages();
            } else if (intent.getAction() == "photolistener") {
                makeSnack("Updating photos.");
                startLocService();
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
                            setupAdapter(objects);
                        }
                    } else {
                        e.printStackTrace();
                        Log.v("error", e.getMessage());
                    }
                } else {
                    //TODO:// if not images, reregister loclistener
                    Toast.makeText(getApplicationContext(), "No Images To Display!", Toast.LENGTH_LONG);
                }
            }
        });
    }

    //params:start index , end index
    public void setupAdapter(final List<ParseObject> pics) {

        RootImageAdapter adapter;
         linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RootImageAdapter(this, pics, linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //todo: add rows
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                if (linearLayoutManager.findFirstCompletelyVisibleItemPosition()==/*user pic collection size- whole size*/ urls.size() - 1) {
//                    curSize = linearLayoutManager.findLastCompletelyVisibleItemPosition() + 1;
//                    end = end + 3;
//                    //   addItem(sublist.subList(end, urls.size() - 1));
//
//                }

            }

        });

    }
//
//    public void FillAdapter( List<ParseObject> pics)
//    {
//        for(ParseObject parseObject:pics)
//        {
//            parseObject.
//        }
//
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.displayallmenu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.uploadimage) {
            startActivityForResult(new Intent(this, UploadImage.class), UploadImage.UploadImageRequestCode);
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
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter("photolistener"));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("location"));

        //snack stuff
        rootview = findViewById(R.id.displayalllayout);
        makeSnack("Obtaining Location...");

        if (android.os.Build.VERSION.SDK_INT == 23) {
            requestPermission();
        } else {
            startLocService();

        }
    }

}
