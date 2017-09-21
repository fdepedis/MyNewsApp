package it.flaviodepedis.mynewsapp;

import java.io.Serializable;

/**
 * Created by flavio.depedis on 21/09/2017.
 */

/**
 * An {@link NewsItem} object contains information related to a single News.
 */
public class NewsItem implements Serializable {

    /** Title of the News */
    private String mNewsTitle;

    /** Section of the News */
    private String mNewsSection;

    /** Published Date of the News */
    private String mNewsPublishedDate;

    /** Author of the News */
    private String mNewsAuthor;

    /** Web URL of the News */
    private String mNewsUrl;

    /** Thumbnail of the News */
    private String mNewsThumbnail;

    public NewsItem(String mNewsTitle, String mNewsSection, String mNewsPublishedDate,
                    String mNewsAuthor, String mNewsUrl, String mNewsThumbnail) {
        this.mNewsTitle = mNewsTitle;
        this.mNewsSection = mNewsSection;
        this.mNewsPublishedDate = mNewsPublishedDate;
        this.mNewsAuthor = mNewsAuthor;
        this.mNewsUrl = mNewsUrl;
        this.mNewsThumbnail = mNewsThumbnail;
    }

    /**
     * Returns the title of the news.
     */
    public String getmNewsTitle() {
        return mNewsTitle;
    }

    /**
     * Returns the section of the news.
     */
    public String getmNewsSection() {
        return mNewsSection;
    }

    /**
     * Returns the published date of the news.
     */
    public String getmNewsPublishedDate() {
        return mNewsPublishedDate;
    }

    /**
     * Returns the author of the news.
     */
    public String getmNewsAuthor() {
        return mNewsAuthor;
    }

    /**
     * Returns the url of the news.
     */
    public String getmNewsUrl() {
        return mNewsUrl;
    }

    /**
     * Returns the thumbanil of the news.
     */
    public String getmNewsThumbnail() {
        return mNewsThumbnail;
    }


}
