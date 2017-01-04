package fsegundo.lambda;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import fsegundo.lambda.activity.BaseActivity;
import fsegundo.lambda.activity.FeedListActivity;
import fsegundo.lambda.utils.ViewFinder;


public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);
        actionBar.setDisplayHomeAsUpEnabled(false);

        ViewFinder finder = new ViewFinder(this);
        finder.onClick(R.id.btn_feed_list_demo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FeedListActivity.class));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
