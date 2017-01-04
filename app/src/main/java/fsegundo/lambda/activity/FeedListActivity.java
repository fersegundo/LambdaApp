package fsegundo.lambda.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fsegundo.lambda.AppController;
import fsegundo.lambda.R;
import fsegundo.lambda.adapter.FeedListAdapter;
import fsegundo.lambda.data.FeedItem;
import fsegundo.lambda.data.FeedResult;
import fsegundo.lambda.utils.GsonRequest;
import fsegundo.lambda.view.EndlessRecyclerViewScrollListener;
import fsegundo.lambda.view.RecyclerTouchListener;


/**
 * Created by baia on 14-9-21.
 */
public class FeedListActivity extends BaseActivity  implements SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = "APP FeedListActivity";

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private EndlessRecyclerViewScrollListener scrollListener;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems, getFeedItemsTemp;
    private List<FeedItem> feedItemsTemp;
    //   private String URL_BASE = "http://api.androidhive.info/feed/feed.json?offset=";
    //private String URL_BASE = "http://192.168.2.108/prueba/probando.php?offset=";
    //private String URL_BASE = "https://raw.githubusercontent.com/fersegundo/ISPM/master/Lambda.php?offset=";
    private String URL_BASE = "https://raw.githubusercontent.com/fersegundo/ISPM/master/feed.json?offset=";
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_list);
        setTitle(R.string.feed_list_demo);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.container);
        recyclerView = (RecyclerView) findViewById(R.id.feed_list);
        feedItems = new ArrayList<FeedItem>();
//        feedItemsTemp = new ArrayList<FeedItem>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        listAdapter = new FeedListAdapter(this, feedItems);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(listAdapter);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            // totalItemsCount - items currently available in the adapter
            public void onLoadMore(int current_page, int totalItemsCount) {
                // Page size is controlled by server
                // Notification to adapter is done in OnResponse
                makeRequest(totalItemsCount);
            }
            public int getFooterViewType(int defaultNoFooterViewType) {
                // TODO que se puede devolver aqui?
                return defaultNoFooterViewType;
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        swipeRefreshLayout.setOnRefreshListener(this);

        // Showing initial Swipe Refresh animation with runnable is used
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                    }
                                });
        // esto tambien funciona, pero mejor que este en adapter, no?
//        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                FeedItem item = feedItems.get(position);
//                Toast.makeText(getApplicationContext(), item.getId() + " is selected!", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//                FeedItem item = feedItems.get(position);
//                Toast.makeText(getApplicationContext(), item.getId() + " is long clicked!", Toast.LENGTH_SHORT).show();
//
//            }
//        }));

        makeRequest(0);// making fresh volley request and getting json
    }

//     <!--android:onClick="OnClickCalendar"-->
//    public void OnClickCalendar(View view) {
//        // Se ha presionado el boton
//    }

    // This method is called when swipe refresh is pulled down
    @Override // SwipeRefreshLayout
    public void onRefresh() {
        Refresh();
    }
    public void Refresh (){
        swipeRefreshLayout.setRefreshing(true);
        // In case there is a volley request, cancel it
        AppController.getInstance().cancelPendingRequest(TAG);

        scrollListener.pause(); //prevent triggering requests by normal scrolling
        feedItems.clear();
        listAdapter.notifyDataSetChanged();
        makeRequest(0);
    }

    // making fresh volley request and getting json
    private List<FeedItem> makeRequest(int totalItemsCount){
        String URL_FEED = URL_BASE + totalItemsCount;
        GsonRequest<FeedResult> gsonRequest = new GsonRequest<FeedResult>(URL_FEED, FeedResult.class,
                new Response.Listener<FeedResult>() {
                    @Override
                    public void onResponse(FeedResult response) {
                        feedItemsTemp = response.getFeedItems();
                        if (feedItemsTemp.isEmpty()){
                            // There are no more items in the database
                            // disable scrollListener for optimization only
                            scrollListener.pause(); //prevent triggering requests by normal scrolling
                            return;
                        }

                        //Check id consistency (id of first item received = last item previously received -1)
                        if (!(feedItems.isEmpty() || feedItems.get(feedItems.size()-1).getId()-1 == feedItemsTemp.get(0).getId())) {
                            // There is an unknown ordering error or there are new items (server updated)
                            Log.i(TAG, "OnResponse : refreshing on new Json, id of first item received:" + feedItemsTemp.get(0).getId() + "id of last item previously received:"+  feedItems.get(feedItems.size()-1).getId() + " They should be consecutive" );
                            feedItemsTemp.clear();
                            Refresh();
                        }
                        feedItems.addAll(feedItemsTemp);
                        listAdapter.notifyItemRangeInserted(feedItems.size() - feedItemsTemp.size(), feedItems.size() - 1);
                        // if it's the first page, start and reset normal scroll listener
                        //(in case it was paused by refresh scroll)
                        if (feedItems.size() - feedItemsTemp.size() == 0) {
                            scrollListener.init();
                            swipeRefreshLayout.setRefreshing(false);  // This hides the spinner
                        }
                        feedItemsTemp.clear();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error: " + error.getMessage());
                        Toast.makeText(FeedListActivity.this, "ERROR CONNECTING TO SERVER", Toast.LENGTH_SHORT).show();
                        //TODO: show cached information?
                    }
                });

        // Adding request to volley request queue
        /// GSon automatically fills a FeedResult object and delivers it on onResponse
        AppController.getInstance().addRequest(gsonRequest, TAG);

        return feedItemsTemp;
    }

    /**
     * Parsing json reponse and passing the data to feed view list adapter
    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("feed");

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                FeedItem item = new FeedItem();
                item.setId(feedObj.getInt("id"));
                item.setName(feedObj.getString("name"));

                // Image might be null sometimes
                String image = feedObj.isNull("image") ? null : feedObj
                        .getString("image");
                item.setImageUrl(image);
                item.setStatus(feedObj.getString("status"));
                item.setProfilePic(feedObj.getString("profilePic"));
                item.setTimeStamp(feedObj.getString("timeStamp"));

                // url might be null sometimes
                String feedUrl = feedObj.isNull("url") ? null : feedObj
                        .getString("url");
                item.setUrl(feedUrl);

                feedItems.add(item);
            }

            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    */


}
