package it.flaviodepedis.mynewsapp;

/**
 * Created by flavio.depedis on 21/09/2017.
 */

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import android.text.TextUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
            JSONObject newsJsonObjRequest;          // JSONObject for data retrieved
            JSONObject newsJsonObjResponse;         // JSONObject for data retrieved from "response" object
            JSONObject newsJsonObjFields;           // JSONObject for data retrieved from "fields" object
            JSONObject currentNewsItem;             // Current News Items
            JSONObject currentNewsItemTags;         // Current News Items Tags

            JSONArray newsJsonArrayTags;            // JSONObject for data retrieved from "tags" object
            JSONArray newsJsonArrayResults;         // JSONArray for data retrieved from "result" object

            String mNewsTitle;
            String mNewsSection;
            String mNewsPublishedDate;
            String mNewsAuthor = "";
            String mNewsUrl;
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

                        // verify if "webTitle" exists
                        if (currentNewsItem.has("webTitle")) {
                            mNewsTitle = currentNewsItem.getString("webTitle");
                        } else {
                            mNewsTitle = mContext.getResources().getString(R.string.no_title);
                        }

                        // verify if "sectionName" exists
                        if (currentNewsItem.has("sectionName")) {
                            mNewsSection = currentNewsItem.getString("sectionName");
                        } else {
                            mNewsSection = mContext.getResources().getString(R.string.no_section);
                        }

                        // verify if "webPublicationDate" exists
                        if (currentNewsItem.has("webPublicationDate")) {
                            mNewsPublishedDate = currentNewsItem.getString("webPublicationDate");
                            // convert the String into Date
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                            try {
                                Date date = format.parse(mNewsPublishedDate);

                                // format the date and cast it to String again
                                mNewsPublishedDate = (String) DateFormat.format("MMM" + " " + "dd" + ", " + "yyyy", date);
                            } catch (ParseException e) {
                                Log.e(LOG_TAG, "An exception was encountered while trying to parse a date " + e);
                                mNewsPublishedDate = mContext.getResources().getString(R.string.no_date);
                            }
                        } else {
                            mNewsPublishedDate = mContext.getResources().getString(R.string.no_date);
                        }

                        // verify if "webUrl" exists
                        if (currentNewsItem.has("webUrl")) {
                            mNewsUrl = currentNewsItem.getString("webUrl");
                        } else {
                            mNewsUrl = mContext.getResources().getString(R.string.no_web_url);
                        }

                        // verify if "fields" exists
                        if (currentNewsItem.has("fields")) {
                            newsJsonObjFields = currentNewsItem.getJSONObject("fields");

                            // verify if "thumbnail" exists
                            if (newsJsonObjFields.has("thumbnail")) {
                                mNewsThumbnail = newsJsonObjFields.getString("thumbnail");
                            } else {
                                mNewsThumbnail = "";
                            }
                        }

                        // verify if "authors" exists
                        if (currentNewsItem.has("tags")) {
                            newsJsonArrayTags = currentNewsItem.getJSONArray("tags");

                            for (int j = 0; j < newsJsonArrayTags.length(); j++) {
                                currentNewsItemTags = newsJsonArrayTags.getJSONObject(j);

                                // verify if "webTitle" for "authors" exists
                                if (currentNewsItemTags.has("webTitle")) {
                                    mNewsAuthor = currentNewsItemTags.getString("webTitle");
                                } else {
                                    mNewsAuthor = mContext.getResources().getString(R.string.no_author);
                                }
                            }
                        } else {
                            mNewsAuthor = mContext.getResources().getString(R.string.no_author);
                        }

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

