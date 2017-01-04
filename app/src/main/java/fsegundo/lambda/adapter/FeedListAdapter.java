package fsegundo.lambda.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import fsegundo.lambda.AppController;
import fsegundo.lambda.view.FeedImageView;
import fsegundo.lambda.R;
import fsegundo.lambda.data.FeedItem;

/**
 * Created by baia on 14-9-13.
 */
public class FeedListAdapter extends RecyclerView.Adapter<FeedListAdapter.MyViewHolder> {

    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public class MyViewHolder   extends RecyclerView.ViewHolder
                                implements View.OnClickListener, View.OnLongClickListener {
        public TextView name, timestamp, statusMsg,url;
        public NetworkImageView profilePic;
        public FeedImageView feedImageView;
        public Button       buttonCalendar;

        public MyViewHolder(View view) {
            super(view);
             name = (TextView) view.findViewById(R.id.name);
             timestamp = (TextView) view
                    .findViewById(R.id.timestamp);
             statusMsg = (TextView) view
                    .findViewById(R.id.txtStatusMsg);
             url = (TextView) view.findViewById(R.id.txtUrl);
             profilePic = (NetworkImageView) view
                    .findViewById(R.id.profilePic);
             feedImageView = (FeedImageView) view
                    .findViewById(R.id.feedImage1);

            buttonCalendar = (Button) view.findViewById(R.id.buttonCalendar);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            buttonCalendar.setOnClickListener(this);
        }
        // onClick Listener for view
        @Override
        public void onClick(View v) {
            if (v.getId() == buttonCalendar.getId()){
                FeedItem item = feedItems.get(getAdapterPosition());
               // AppController.getInstance().getCalendar().getCalendars();
                AppController.getInstance().getCalendar().Add(Long.parseLong(item.getTimeStamp()), name.getText().toString(), statusMsg.getText().toString() );
            } else {
                Toast.makeText(v.getContext(), "ITEM PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            }
        }

        //onLongClickListener for view
        @Override
        public boolean onLongClick(View v) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle ("Hello Dialog")
                    .setMessage ("LONG CLICK DIALOG WINDOW " + String.valueOf(getAdapterPosition()))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.create().show();
            return true;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.feed_item, parent, false);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.d("APP", "onBindViewHolder " + position + " " + holder);

        FeedItem item = feedItems.get(position);

        if (item.getName() != null)
        holder.name.setText(item.getName());

        if (item.getTimeStamp() != null) {
            // Converting timestamp into x ago format
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                    Long.parseLong(item.getTimeStamp()),
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            holder.timestamp.setText(timeAgo);
        }

        // Chcek for empty status message
        if (!TextUtils.isEmpty(item.getStatus())) {
            holder.statusMsg.setText(item.getStatus());
            holder.statusMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            holder.statusMsg.setVisibility(View.GONE);
        }

        // Checking for null feed url
        if (item.getUrl() != null) {
            holder.url.setText(Html.fromHtml("<a href=\"" + item.getUrl() + "\">"
                    + item.getUrl() + "</a> "));
            // Making url clickable
            holder.url.setMovementMethod(LinkMovementMethod.getInstance());
            holder.url.setVisibility(View.VISIBLE);
        } else {
            // url is null, remove from the view
            holder.url.setVisibility(View.GONE);
        }

        // user profile pic
        holder.profilePic.setImageUrl(item.getProfilePic(), imageLoader);

        // Feed image
        if (item.getImageUrl() != null) {
            holder.feedImageView.setImageUrl(item.getImageUrl(), imageLoader);
            holder.feedImageView.setVisibility(View.VISIBLE);
            holder.feedImageView
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                            // TODO: quitar imagen (quizas recolocaciones molestas si se ha scroleado hacia abajo. O poner una imagen de error
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
        } else {
            holder.feedImageView.setVisibility(View.GONE);
        }
    }

    public FeedListAdapter(Activity activity, List<FeedItem> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }

    //@Override
    //public Object getItem(int location) {
     //   return feedItems.get(location);
    //}

    /**
     * 更新数据
     * @param data 列表
     * notifyDataSetChanged must be done by caller
     */
    public void setData(List<FeedItem> data) {

        this.feedItems = data;
    }

/*    @Override
    public long getItemId(int position) {
        return position;
    }
*/
}
