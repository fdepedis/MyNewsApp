package it.flaviodepedis.mynewsapp;

/**
 * Created by flavio.depedis on 21/09/2017.
 */

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import android.text.TextUtils;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving news item data from TheGuardian API.
 */
public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Context of the caller activity
     */
    private static Context mContext;

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Google NewsItem API dataset and return a list of {@link NewsItem} objects.
     */
    public static List<NewsItem> fetchNewsItemData(String requestUrl, Context context) {

        mContext = context;

        Log.i(LOG_TAG, "Log - fetchNewsItemData() method");

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link NewsItem}s
        List<NewsItem> news = extractNewsItemFromJson(jsonResponse);

        // Return the list of {@link NewsItem}s
        return news;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.w(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link NewsItem} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<NewsItem> extractNewsItemFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        List<NewsItem> news = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            JSONObject newsJsonObjRequest;          // JSON Object for data retrieved
            JSONObject newsJsonObjResponse;         // JSON Object for data retrieved from "response" object
            JSONArray newsJsonArrayResults;         // JSON Object for data retrieved from "result" object

            JSONObject currentNewsItem;        // Array of News Items

            String mNewsTitle = "";
            String mNewsSection = "";
            String mNewsPublishedDate = "";
            String mNewsAuthor = "";
            String mNewsUrl = "";
            String mNewsThumbnail = "";

            newsJsonObjRequest = new JSONObject(newsJSON);
            // verify if "response" exists
            if (newsJsonObjRequest.has("response")) {
                newsJsonObjResponse = newsJsonObjRequest.getJSONObject("response");

                // verify if "results" exists
                if (newsJsonObjResponse.has("results")) {
                    newsJsonArrayResults = newsJsonObjResponse.getJSONArray("results");

                    for (int i = 0; i < newsJsonArrayResults.length(); i++) {
                        currentNewsItem = newsJsonArrayResults.getJSONObject(i);

                        if(currentNewsItem.has("webTitle")){
                            mNewsTitle = currentNewsItem.getString("webTitle");
                        }

                        // Get List of Author if there are more than one, if exist
                    /*
                    if (currVolumeInfo.has("authors")) {
                        authorsArray = currVolumeInfo.getJSONArray("authors");

                        // Verify if the author is one or more then one
                        if (authorsArray.length() > 1) {
                            authorsList = authorsArray.join(", ").replaceAll("\"", "");
                        } else if (authorsArray.length() == 1) {
                            authorsList = authorsArray.getString(0);
                        } else if (authorsArray.length() == 0) {
                            authorsList = mContext.getResources().getString(R.string.no_author);
                        }
                    } else {
                        authorsList = mContext.getResources().getString(R.string.no_author);
                    }
                    */


                        // Create a new {@link NewsItem} object from the JSON response.
                        NewsItem newsItem = new NewsItem(mNewsTitle, mNewsSection, mNewsPublishedDate,
                                mNewsAuthor, mNewsUrl, mNewsThumbnail);

                        // Add the new {@link NewsItem} to the list of news.
                        news.add(newsItem);

                        Log.i(LOG_TAG, "Log - extractNewsItemFromJson() method");
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the NewsItem JSON results", e);
        }

        // Return the list of news
        return news;
    }
}

