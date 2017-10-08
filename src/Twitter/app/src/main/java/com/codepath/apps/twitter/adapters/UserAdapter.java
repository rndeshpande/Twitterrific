package com.codepath.apps.twitter.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.activities.DetailsActivity;
import com.codepath.apps.twitter.activities.ProfileActivity;
import com.codepath.apps.twitter.databinding.ListTweetBinding;
import com.codepath.apps.twitter.databinding.ListUserBinding;
import com.codepath.apps.twitter.fragments.CreateDialogFragment;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.models.TweetRequest;
import com.codepath.apps.twitter.models.User;
import com.codepath.apps.twitter.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by rdeshpan on 9/25/2017.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>  {
    private ArrayList<User> mUsers;
    private Context mContext;
    private TwitterClient mClient;
    private FragmentManager mFragmentManager;

    public UserAdapter(Context context, TwitterClient client, ArrayList<User> users, FragmentManager fragmentManager) {
        mContext = context;
        mUsers = users;
        mClient = client;
        mFragmentManager = fragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        final ViewHolder viewHolder;
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView;

        itemView = inflater.inflate(R.layout.list_user, parent, false);
        viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        User user = mUsers.get(position);
        viewHolder.bind(user);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final ListUserBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = ListUserBinding.bind(itemView);

            binding.ivProfile.setOnClickListener(this::onProfileImageClick);
        }

        public void bind(User user) {
            binding.setUser(user);
            binding.executePendingBindings();
        }

        public void onProfileImageClick(View view) {
            Intent intent = new Intent(view.getContext(), ProfileActivity.class);
            intent.putExtra("username", binding.getUser().getScreenName());
            view.getContext().startActivity(intent);
        }
    }
}
