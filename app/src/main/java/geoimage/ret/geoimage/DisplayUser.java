package geoimage.ret.geoimage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class DisplayUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_alluser);

        Bundle bundle = getIntent().getExtras();


        // ImageView userImage= (ImageView)


//        Glide.with(this) //
//                .load(bundle.getString("url"))
//                .diskCacheStrategy(DiskCacheStrategy.RESULT)
//                .placeholder(R.mipmap.ic_launcher)
//                .error(R.mipmap.ic_launcher)
//                .centerCrop()
//                .into((ImageView) findViewById(R.id.imageView_displayFullImage));


    }
}
