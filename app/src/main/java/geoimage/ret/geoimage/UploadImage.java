package geoimage.ret.geoimage;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadImage extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    static int UploadImageRequestCode = 11;
    private Uri fileUri;
    // ...
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("location")) {
                startCameraIntent(true);
            }
        }
    };

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("location"));
        if (android.os.Build.VERSION.SDK_INT == 23) {
            requestPermission();
        } else {
            GetLocationService.startGetLocation(this);
        }

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

        } else {
            GetLocationService.startGetLocation(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            receieveCameraIntent();
        } else if (resultCode == RESULT_CANCELED) {
            // User cancelled the image capture
            setResult(Activity.RESULT_CANCELED);
            finish();
        }

    }

    private Bitmap setPic() {
        // Get the dimensions of the View
        int targetW = 300;
        int targetH = 150;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileUri.getPath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), bmOptions);
        return bitmap;
    }

    public void receieveCameraIntent() {

        try {
            Bitmap bitmap = setPic();
            // Compress image to lower quality scale 1 - 100
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
            byte[] image = stream.toByteArray();
            stream.close();

            final ParseFile parseFile = new ParseFile("test2.jpeg", image);
            parseFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    ParseObject parseObject = new ParseObject("Images");

                    parseObject.put("location", new ParseGeoPoint(App.getLat(), App.getLon()));
                    parseObject.put("image", parseFile);
                    parseObject.saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Main.makeSnack("Upload Complete");
                            } else {

                            }
                        }
                    });
                }
            });

            setResult(Activity.RESULT_OK);
            finish();

        } catch (Exception e) {

            //TODO://Catch Error6
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (grantResults.length > 0) {
            if (requestCode == 10) {

                GetLocationService.startGetLocation(this);
            }
        } else {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }

    }

    public void startCameraIntent(boolean granted) {


        // If request is cancelled, the result arrays are empty.
        if (granted) {
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
            File mediaFile;

            mediaFile = new File(mediaStorageDir.getPath() + File.separator + timeStamp + ".jpg");
            // create Intent to take a picture and return control to the calling application
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            fileUri = Uri.fromFile(mediaFile);// create a file to save the image
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

            // start the image capture Intent
            startActivityForResult(intent, CAMERA_REQUEST);


        } else {

            // permission denied, boo! Disable the
            // functionality that depends on this permission.
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
        return;
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
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("location"));
    }



}
