package geoimage.ret.geoimage;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by ret70 on 11/20/2015.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "k9Jby5AwGmxHwr5seb9uF2JfU8WxWkbxk6ttGgVY", "RS5absrPZljMeuKueyCEZREBgfl56d0QerUVPakJ");


    }
}
