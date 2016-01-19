package geoimage.ret.geoimage;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class DisplayUsers extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    int start = 0;
    int end = 0;
    LinearLayoutManager linearLayoutManager;
    //sublist of all users , default display first 3, will be used for pagination also.
    UsersListAdapter adapter;
    static View rootview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_users);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(linearLayoutManager);

        rootview = findViewById(R.id.display_all_users_layout);


        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbarlayout object
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);


        displayUsers();
    }


    static void makeSnack(String text) {
        Snackbar.make(rootview, text, Snackbar.LENGTH_LONG).show();
    }

    public void displayUsers() {

        //  ParseGeoPoint parseGeoPoint = new ParseGeoPoint(App.getLat(), App.getLon());

        //get all images
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (objects.size() > 0) {
                    if (e == null) {
                        if (!objects.isEmpty()) {
                            adapter = new UsersListAdapter(objects);
                            recyclerView.setAdapter(adapter);

                        } else if (e.getCode() == ParseException.REQUEST_LIMIT_EXCEEDED) {
                            makeSnack("Error! Reloading images.");

                        } else {
                            e.printStackTrace();
                            Log.v("error", e.getMessage());
                        }
                    } else {
                        //TODO:// if not images, reregister loclistener
                        makeSnack("No images located near you.");
                    }
                }
            }
        });

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
}
