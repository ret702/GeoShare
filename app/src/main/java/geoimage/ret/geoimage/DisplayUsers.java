package geoimage.ret.geoimage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.parse.ParseObject;

import java.util.ArrayList;

public class DisplayUsers extends AppCompatActivity {
    RecyclerView recyclerView;
    int start = 0;
    int end = 0;
    LinearLayoutManager linearLayoutManager;
    //sublist of all users , default display first 3, will be used for pagination also.
    ArrayList<ArrayList<ParseObject>> sublist;
    RootImageAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_users);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewParent);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RootImageAdapter(this, sublist, linearLayoutManager);
        recyclerView.setAdapter(adapter)
    }
}
