package geoimage.ret.geoimage;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadImage extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    static int UploadImageRequestCode = 11;
    Uri fileUri;
    boolean receivedLocaiton = false;
    View rootview;
    byte[] f_image;
    // ...
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("location")) {
                receivedLocaiton = true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
    }

    public void requestPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA}, 10);

        } else { //had to add these conditions because the camera activity recreates my
            //activity causing an infinite loop
            if (App.getIsCameraStarted() != true) {
                startCameraIntent();
            }
        }
    }

    public void makeSnack() {

        Snackbar.make(rootview, "Obtaining Location Please Wait.", Snackbar.LENGTH_LONG);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (grantResults.length > 0) {
            if (requestCode == 10) {

                if (App.getIsCameraStarted() != true) {
                    startCameraIntent();
                }
            }
        } else {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }

    }

    public void startLocService() {
        GetLocationService.startGetLocation(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            receieveCameraIntent();
            startLocService();

        } else if (resultCode == RESULT_CANCELED) {
            // User cancelled the image capture
            setResult(Activity.RESULT_CANCELED);
            finish();
        }

    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String path,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }


    public void saveImage(final String filename, byte[] image) {
        if (receivedLocaiton) {
            final ParseFile parseFile = new ParseFile(filename.concat(".png"), image);
            parseFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        saveObject(parseFile, filename);
                    }
                }
            });

            App.setIsCameraStarted(false);

        } else {
            makeSnack();
            startLocService();

        }
    }

    public void saveObject(ParseFile parsefile, String title) {
        ParseObject parseObject = new ParseObject("Images");
        ParseUser creator = ParseUser.getCurrentUser();
        parseObject.put("location", new ParseGeoPoint(App.getLat(), App.getLon()));
        parseObject.put("image", parsefile);
        parseObject.put("title", title);
        parseObject.put("createdBy", creator);
        parseObject.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Main.makeSnack("Upload Complete");
                } else {
                    e.printStackTrace();
                }
            }
        });

        setResult(Activity.RESULT_OK);
        finish();
    }

    public void receieveCameraIntent() {

        new Thread(new Runnable() {
            public void run() {
                try {
                    Resources r = getResources();
                    Bitmap bitmap = decodeSampledBitmapFromResource(fileUri.getPath(), 350, 300);
                    // Compress image to lower quality scale 1 - 100
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                    f_image = stream.toByteArray();
                    stream.close();
                    ImageView preview = (ImageView) findViewById(R.id.imageView_preview);
                    Glide.with(getApplicationContext()) //
                            .load(f_image)
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .centerCrop()
                            .into(preview);
                } catch (Exception e) {

                    //TODO://Catch Error6
                }

            }
        }).start();

        findViewById(R.id.button_subtitle).setOnClickListener(new View.OnClickListener() {
                                                                  @Override
                                                                  public void onClick(View v) {
                                                                      EditText title = (EditText) findViewById(R.id.editText_preview);
                                                                      saveImage(title.getText().toString(), f_image);
                                                                  }
                                                              }
        );


    }


    public void startCameraIntent() {


        // If request is cancelled, the result arrays are empty.

        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "test");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        final File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator + timeStamp + ".png");
//        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        fileUri = Uri.fromFile(mediaFile);// create a file to save the image
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        intent.putExtra("return-data", true);
        // start the image capture Intent
        App.setIsCameraStarted(true);
        startActivityForResult(intent, CAMERA_REQUEST);


    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }


    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("location"));
        rootview = findViewById(R.id.uploadimagelayout);
        if (android.os.Build.VERSION.SDK_INT == 23) {
            requestPermission();
        } else if (App.getIsCameraStarted() != true) {
            startCameraIntent();
        }

    }
}
