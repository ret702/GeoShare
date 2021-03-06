package geoimage.ret.geoimage;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import java.util.List;

/**
 * Created by ret70 on 11/20/2015.
 */
public class App extends Application {
    static private Double latitude;
    static private Double longitude;
    static private List<ParseObject> imageIDs;
    static private boolean isCameraStarted;
    static boolean locationStatus;

    static public Double getLat() {

        return latitude;
    }

    static public void setLat(Double lat) {
        latitude = lat;
    }

    static public Double getLon() {
        return longitude;
    }

    static public void setLon(Double lon) {
        longitude = lon;
    }

    static public List<ParseObject> getImageIDs() {
        return imageIDs;
    }

    static public void setImageIDs(List<ParseObject> ids) {
        imageIDs = ids;
    }

    static public Boolean getIsCameraStarted() {
        return isCameraStarted;
    }

    static public void setIsCameraStarted(Boolean isStarted) {

        isCameraStarted = isStarted;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "k9Jby5AwGmxHwr5seb9uF2JfU8WxWkbxk6ttGgVY", "RS5absrPZljMeuKueyCEZREBgfl56d0QerUVPakJ");
        locationStatus = false;
    }


    static public boolean getDisplayingSelf() {

        return locationStatus;
    }


    public static void setDisplayingSelf(boolean status) {
        locationStatus = status;
    }
}
