package it.flaviodepedis.mynewsapp;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.app.LoaderManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by flavio.depedis on 21/09/2017.
 */
public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<NewsItem>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    /**
     * Constant value for the newsItem loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_ITEM_LOADER_ID = 1;

    /**
     * URL for news item data from The Guardian API
     */
    private static String OPEN_NEWS_ITEM_REQUEST_URL =
            "https://content.guardianapis.com/search?";

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    /**
     * Adapter for the list of newsItem
     */
    private NewsItemAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        ListView newsItemListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsItemListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of newsItem as input
        mAdapter = new NewsItemAdapter(this, new ArrayList<NewsItem>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsItemListView.setAdapter(mAdapter);

        //Search only if network is available else show proper message
        if (isNetworkWorking()) {

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            Log.i(LOG_TAG, "Log - Before initLoader() call");

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_ITEM_LOADER_ID, null, this);

            Log.i(LOG_TAG, "Log - After initLoader() call");

        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        newsItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // Find the current newsItem that was clicked on
                NewsItem currNewsItem = mAdapter.getItem(position);

                String newsUri = currNewsItem.getmNewsUrl();

                // Create a new intent to view the news item in the browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsUri));

                startActivity(intent);
            }
        });

    }

    @Override
    public Loader<List<NewsItem>> onCreateLoader(int id, Bundle args) {

        Log.i(LOG_TAG, "Log - onCreateLoader() method");

        String section = "sport";

        Uri baseUri = Uri.parse(OPEN_NEWS_ITEM_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("section", section);                            //pref
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("from-date", "2017-01-01");                     //pref
        uriBuilder.appendQueryParameter("page-size", "5");                              //pref
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("show-fields", "headline,thumbnail,short-url");
        uriBuilder.appendQueryParameter("order-by", "relevance");                       //pref
        uriBuilder.appendQueryParameter("api-key", "test");

        // Create a new loader for the given URL
        return new NewsItemLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<NewsItem>> loader, List<NewsItem> data) {

        Log.i(LOG_TAG, "Log - onLoadFinished() method");

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No news item found."
        mEmptyStateTextView.setText(R.string.no_news_item);

        // Clear the adapter of previous news item data
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader) {

        Log.i(LOG_TAG, "Log - onLoaderReset() method");

        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    /**
     * Method to check if network is working
     *
     * @return boolean value
     */
    private boolean isNetworkWorking() {

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }
}

