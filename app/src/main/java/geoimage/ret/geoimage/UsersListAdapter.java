package geoimage.ret.geoimage;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.List;

/**
 * Created by ret70 on 1/12/2016.
 */
public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.ParentViewHolder> {

    List<ParseUser> users;

    LinearLayoutManager linearLayoutManager;
    int curSize;
    int start = 0;
    LinearLayoutManager layoutManager;


    public UsersListAdapter(List<ParseUser> param_users) {
        curSize = param_users.size();
        users = param_users;

    }

    @Override
    public ParentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.usersview, viewGroup, false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ParentViewHolder parentViewHolder, int i) {


    }


    public void addItem(List<ParseUser> items) {
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

    class ParentViewHolder extends RecyclerView.ViewHolder {
        ImageView online_status;
        ImageView users_image;
        TextView statusDisplayed;

        public ParentViewHolder(View itemView) {
            super(itemView);
            statusDisplayed = (TextView) itemView.findViewById(R.id.textView_status);
            users_image = (ImageView) itemView.findViewById(R.id.imageView_usersImage);

        }
    }
}
