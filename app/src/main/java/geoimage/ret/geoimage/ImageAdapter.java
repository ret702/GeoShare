package geoimage.ret.geoimage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    int layoutID;
    ArrayList<String> titles;
    ArrayList<ParseObject> urls;
    Context context;

    public ImageAdapter(Context context, List<ParseObject> URLS) {

        this.titles = titles;
        this.context = context;
        // Ensure we get a different ordering of images on each run.
        urls = new ArrayList<ParseObject>();
        this.urls.addAll(URLS);
        Collections.shuffle(this.urls);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        MyImageView view = (MyImageView) convertView;
        if (view == null) {
            view = new MyImageView(context);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        // Get the image URL for the current position.
        String url = getItem(position);
        if (!url.isEmpty()) {
            // Trigger the download of the URL asynchronously into the image view.
            Picasso.with(context) //
                    .load(url) //
                    .placeholder(R.mipmap.ic_launcher) //
                    .error(R.mipmap.ic_launcher) //
                    .fit() //
                    .tag(context) //
                    .into(view);
        }

        return view;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public String getItem(int position) {
        try {
            return urls.get(position).getParseFile("test.png").getUrl();
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        ImageView image;
        TextView text;
        String id;
    }


}