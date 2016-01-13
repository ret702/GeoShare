package geoimage.ret.geoimage;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.parse.ParseObject;

import java.util.ArrayList;

/**
 * Created by ret70 on 1/12/2016.
 */
public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.RootViewHolder> {

    ArrayList<ArrayList<ParseObject>> users;
    Context context;
    LinearLayoutManager linearLayoutManager;
    int curSize;
    int start = 0;
    LinearLayoutManager layoutManager;

    public UsersListAdapter(ArrayList<ArrayList<ParseObject>> users) {
        curSize = users.size();


    }

    @Override
    public RootViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.usersview, viewGroup, false);
        return new RootViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RootViewHolder rootViewHolder, int i) {

        Uri uri = Uri.parse("android.resource://geoimage.ret.geoimage/drawable/onlinestatus.gif");
        Glide.with(rootViewHolder.recycler_users.getContext()) //
                .load(R.drawable.onlinestatus)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .error(R.mipmap.ic_launcher)
                .centerCrop()
                .into(rootViewHolder.recycler_users);

    }

    public void addItem(ArrayList<ArrayList<ParseObject>> items) {
        int lastKnownSize = users.size();
        for (int i = 0; i < items.size(); i++) {
            users.add(items.get(i));
        }

        notifyItemRangeInserted(lastKnownSize, items.size());
        //     notifyItemInserted(urls.size()-1);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class RootViewHolder extends RecyclerView.ViewHolder {
        ImageView recycler_users;
        TextView statusDisplayed;

        public RootViewHolder(View itemView) {
            super(itemView);
            statusDisplayed = (TextView) itemView.findViewById(R.id.textView_status);
            recycler_users = (ImageView) itemView.findViewById(R.id.imageView_usersImage);
        }
    }
}
