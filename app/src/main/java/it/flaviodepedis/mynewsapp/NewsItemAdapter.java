package it.flaviodepedis.mynewsapp;

import android.content.Context;

import com.squareup.picasso.Picasso;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by flavio.depedis on 15/09/2017.
 */
public class NewsItemAdapter extends ArrayAdapter<NewsItem> {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = NewsItemAdapter.class.getSimpleName();

    /**
     * Context of the caller activity
     */
    private static Context mContext;

    /**
     * Constructs a new {@link NewsItemAdapter}.
     *
     * @param context   of the app
     * @param newsItems is the list of news item, which is the data source of the adapter
     */
    public NewsItemAdapter(Context context, List<NewsItem> newsItems) {
        super(context, 0, newsItems);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String imageIcon;
        String date;

        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is not null, then inflate a new list item layout.
        View listItemView = convertView;
        ViewHolder holder;
        if (listItemView != null) {
            holder = (ViewHolder) listItemView.getTag();
        } else {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.activity_news_item_list, parent, false);
            holder = new ViewHolder(listItemView);
            listItemView.setTag(holder);
        }

        // Find the news at the given position in the list of news
        NewsItem currentNewsItem = getItem(position);

        // Set title of the news
        holder.tvNewsTitle.setText(currentNewsItem.getmNewsTitle());

        // Set authors of the news
        holder.tvNewsAuthors.setText(String.format(
                mContext.getResources().getString(R.string.label_author)
                        + " "
                        + currentNewsItem.getmNewsAuthor()));

        // Set image icon of the news if available.
        // Use Picasso library to load url thumbnail
        imageIcon = currentNewsItem.getmNewsThumbnail();
        if (imageIcon != null && imageIcon.length() > 0) {
            Picasso.with(getContext()).load(currentNewsItem.getmNewsThumbnail()).into(holder.imgNewsIcon);
        } else {
            Picasso.with(getContext()).load(R.drawable.image_not_found).into(holder.imgNewsIcon);
        }

        // Set the published date of the news
        date = currentNewsItem.getmNewsPublishedDate();
        date = formatDate(date);
        holder.tvNewsPublishedDate.setText(String.format(
                mContext.getResources().getString(R.string.label_date)
                        + " "
                        + date));

        // Set the section name of the news
        holder.tvNewsSection.setText(String.format(
                mContext.getResources().getString(R.string.label_section)
                        + " "
                        + currentNewsItem.getmNewsSection()));

        return listItemView;
    }

    static class ViewHolder {

        @BindView(R.id.tv_news_title)
        TextView tvNewsTitle;
        @BindView(R.id.tv_news_authors)
        TextView tvNewsAuthors;
        @BindView(R.id.img_news_icon)
        ImageView imgNewsIcon;
        @BindView(R.id.tv_news_section)
        TextView tvNewsSection;
        @BindView(R.id.tv_news_date)
        TextView tvNewsPublishedDate;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /**
     * Method to format Date fo published news item
     */
    public String formatDate(String dateNewItem) {

        String dateFormatted = "";
        //String dateNew = dateNewItem.substring(0, 10);

        // Format dateNew
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        SimpleDateFormat newFormat = new SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.ENGLISH);
        try {
            Date date = inputFormat.parse(dateNewItem);
            dateFormatted = newFormat.format(date);
        }
        catch(ParseException e) {
            Log.e(LOG_TAG, mContext.getResources().getString(R.string.no_date_parsing), e);
        }

        return dateFormatted;
    }
}