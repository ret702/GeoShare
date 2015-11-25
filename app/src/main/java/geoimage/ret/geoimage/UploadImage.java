package geoimage.ret.geoimage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadImage extends Activity {

    private static final int CAMERA_REQUEST = 1888;
    final private int RESULT_ACCESS = 1;
    RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.uploadimagelayout);
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        requestPermission();

    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, RESULT_ACCESS);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

            try {


                Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath());
                // Convert it to byte
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Compress image to lower quality scale 1 - 100
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] image = stream.toByteArray();
                stream.close();


                final ParseFile parseFile = new ParseFile("test2.jpeg", image);
                parseFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        ParseObject parseObject = new ParseObject("Images");
                        parseObject.put("image", parseFile);
                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(getApplicationContext(), "Upload Successful!", Toast.LENGTH_LONG);
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                } else {
                                    relativeLayout.setBackgroundResource(R.mipmap.error);
                                }
                            }
                        });

                    }
                });
            } catch (Exception e) {
                relativeLayout.setBackgroundResource(R.mipmap.error);

            }


        } else if (resultCode == RESULT_CANCELED) {
            // User cancelled the image capture
            setResult(Activity.RESULT_CANCELED);
            finish();
        } else {
            // Image capture failed, advise user
        }

    }


    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RESULT_ACCESS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
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

                    mediaFile = new File(mediaStorageDir.getPath() + File.separator + "test" + ".jpg");
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

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
