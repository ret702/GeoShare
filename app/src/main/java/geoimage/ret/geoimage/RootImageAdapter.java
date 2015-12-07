package geoimage.ret.geoimage;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
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

class RootImageAdapter extends RecyclerView.Adapter<RootImageAdapter.RootViewHolder> {
    ArrayList<ParseObject> urls;
    private final LayoutInflater inflater;
    Context context;
    android.support.v7.widget.LinearLayoutManager linearLayoutManager;
    int curSize;
    int start = 0;
    int end = 3;
    List<ParseObject> sublist;
    LinearLayoutManager layoutManager;


    public RootImageAdapter(Context context, List<ParseObject> URLS,LinearLayoutManager linearLayoutManager) {
        inflater = LayoutInflater.from(context);
        urls = new ArrayList<ParseObject>();
        sublist=URLS;
        this.urls.addAll(URLS.subList(start,end));
        this.linearLayoutManager=linearLayoutManager;
    }

    @Override
    public RootViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.root_row, viewGroup, false);
        RootViewHolder rvi = new RootViewHolder(view);
        return rvi;
    }

    @Override
    public void onBindViewHolder(RootViewHolder rootViewHolder, int i) {


        rootViewHolder.recyclerViewChild.setHasFixedSize(true);
         layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rootViewHolder.recyclerViewChild.setLayoutManager(layoutManager);
        rootViewHolder.recyclerViewChild.setAdapter(new ChildAdapter(inflater, urls));
        rootViewHolder.recyclerViewChild.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (layoutManager.findLastCompletelyVisibleItemPosition() == urls.size() - 1) {
                    curSize = layoutManager.findLastCompletelyVisibleItemPosition() + 1;
                    end = end + 3;
                 //   addItem(sublist.subList(end, urls.size() - 1));

                }

            }

        });
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

    public ChildAdapter(LayoutInflater inflater, List<ParseObject> URLS) {
        _inflater = inflater;
        urls = new ArrayList<ParseObject>();
        this.urls.addAll(URLS);

    }

    @Override
    public ChildViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = _inflater.inflate(R.layout.mycardview, viewGroup, false);
        ChildViewHolder rvi = new ChildViewHolder(view);

        return rvi;
    }

    @Override
    public void onBindViewHolder(ChildViewHolder childViewHolder, int i) {
        Glide.with(_inflater.getContext()) //
                .load(urls.get(i).getParseFile("image").getUrl())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .centerCrop()
                .into(childViewHolder.photoImageView);
        childViewHolder.titleTextView.setText("");
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {
        public ImageView photoImageView;
        public TextView titleTextView;

        public ChildViewHolder(View itemView) {
            super(itemView);
            photoImageView = (ImageView) itemView.findViewById(R.id.userphoto);
            titleTextView = (TextView) itemView.findViewById(R.id.imagetitle);
        }
    }
}


