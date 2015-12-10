package geoimage.ret.geoimage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends android.support.v7.widget.RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    ArrayList<ParseObject> urls;
    Context context;


    public ImageAdapter(Context context,List<ParseObject> URLS) {
        this.context = context;
        // Ensure we get a different ordering of images on each run.
        urls = new ArrayList<ParseObject>();

        this.urls.addAll(URLS.subList(0,3));

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.mycardview, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Glide.with(context) //
                .load(urls.get(position).getParseFile("image").getUrl())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .centerCrop()
                .into(holder.photoImageView);
        holder.titleTextView.setText("");

    }

    @Override
    public int getItemCount() {
        return urls.size();
    }


    public void addItem(List<ParseObject> items) {
        int lastKnownSize=urls.size();
        for(ParseObject item :items )
        {
            urls.add(item);
        }

      notifyItemRangeInserted(lastKnownSize, items.size());
   //     notifyItemInserted(urls.size()-1);

    }



    class ViewHolder extends RecyclerView.ViewHolder

    {
        public ImageView photoImageView;
        public TextView titleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            photoImageView = (ImageView) itemView.findViewById(R.id.userphoto);
        }
    }



}