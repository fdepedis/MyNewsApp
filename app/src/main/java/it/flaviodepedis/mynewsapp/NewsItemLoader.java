package it.flaviodepedis.mynewsapp;

/**
 * Created by flavio.depedis on 21/09/2017.
 */

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by flavio.depedis on 21/09/2017.
 */
public class NewsItemLoader extends AsyncTaskLoader<List<NewsItem>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = NewsItemLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link NewsItemLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public NewsItemLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {

        Log.i(LOG_TAG, "Log - onStartLoading() method");

        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<NewsItem> loadInBackground() {

        Log.i(LOG_TAG, "Log - loadInBackground() method");

        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of news.
        List<NewsItem> news = QueryUtils.fetchNewsItemData(mUrl, getContext());
        return news;
    }
}

