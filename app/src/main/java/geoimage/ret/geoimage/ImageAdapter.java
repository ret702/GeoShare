package geoimage.ret.geoimage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends android.support.v7.widget.RecyclerView.Adapter<ImageAdapter.ViewHolder> {
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

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mycardview, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Picasso.with(context) //
                .load(urls.get(position).getParseFile("image").getUrl())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .tag("test")
                .fit()
                .into(holder.photoImageView);
    }

    @Override
    public int getItemCount() {

        return urls.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder

    {
        public ImageView photoImageView;
        public TextView textImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            photoImageView = (ImageView) itemView.findViewById(R.id.userphoto);
            textImageView = (TextView) itemView.findViewById(R.id.imagetitle);
        }
    }


}