package geoimage.ret.geoimage;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.parse.ParseObject;

import java.util.ArrayList;

class RootImageAdapter extends RecyclerView.Adapter<RootImageAdapter.RootViewHolder> {
    ArrayList<ArrayList<ParseObject>> urls;
    private final LayoutInflater inflater;
    Context context;
    android.support.v7.widget.LinearLayoutManager linearLayoutManager;
    int curSize;
    int start = 0;
    LinearLayoutManager layoutManager;


    public RootImageAdapter(Context context, ArrayList<ArrayList<ParseObject>> URLS, LinearLayoutManager linearLayoutManager) {
        inflater = LayoutInflater.from(context);
        urls = new ArrayList<ArrayList<ParseObject>>();
        urls = URLS;
        curSize = urls.size();
        this.linearLayoutManager = linearLayoutManager;
    }


    @Override
    public RootViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.root_row, viewGroup, false);
        return new RootViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RootViewHolder rootViewHolder, int i) {

        if (start < urls.size()) {
            rootViewHolder.recyclerViewChild.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            rootViewHolder.recyclerViewChild.setLayoutManager(layoutManager);
            rootViewHolder.recyclerViewChild.setAdapter(new ChildAdapter(inflater, urls.get(start++)));
        }

    }


    public void addItem(ArrayList<ArrayList<ParseObject>> items) {
        int lastKnownSize = urls.size();
        for (int i = 0; i < items.size(); i++) {
            urls.add(items.get(i));
        }

        notifyItemRangeInserted(lastKnownSize, items.size());
        //     notifyItemInserted(urls.size()-1);

    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    class RootViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerViewChild;

        public RootViewHolder(View itemView) {
            super(itemView);

            recyclerViewChild = (RecyclerView) itemView.findViewById(R.id.recyclerChild);
        }
    }
}


class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder> {
    private LayoutInflater _inflater;
    Context context;
    ArrayList<ParseObject> urls;

    public ChildAdapter(LayoutInflater inflater, ArrayList<ParseObject> URLS) {
        _inflater = inflater;
        urls = new ArrayList<ParseObject>();
        urls = URLS;
        context = _inflater.getContext();

    }

    @Override
    public ChildViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = _inflater.inflate(R.layout.mycardview, viewGroup, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChildViewHolder childViewHolder, final int i) {
        Glide.with(_inflater.getContext()) //
                .load(urls.get(i).getParseFile("image").getUrl())
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .centerCrop()
                .into(childViewHolder.photoImageView);

        childViewHolder.photoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DisplayUser.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("url", urls.get(i).getParseFile("image").getUrl());
                v.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {
        public ImageView photoImageView;

        public ChildViewHolder(View itemView) {
            super(itemView);
            photoImageView = (ImageView) itemView.findViewById(R.id.userphoto);
        }
    }
}


