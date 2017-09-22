package it.flaviodepedis.mynewsapp;

import android.content.Context;
import com.squareup.picasso.Picasso;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by flavio.depedis on 15/09/2017.
 */
public class NewsItemAdapter extends ArrayAdapter<NewsItem> {

    /**
     * Constructs a new {@link NewsItemAdapter}.
     *
     * @param context   of the app
     * @param newsItems is the list of news item, which is the data source of the adapter
     */
    public NewsItemAdapter(Context context, List<NewsItem> newsItems) {
        super(context, 0, newsItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String imageIcon;

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

        // Set authors of the news if available
        holder.tvNewsAuthors.setText(currentNewsItem.getmNewsAuthor());

        // Set image icon of the news if available.
        // Use Picasso library to load url thumbnail
        imageIcon = currentNewsItem.getmNewsThumbnail();
        if (imageIcon != null && imageIcon.length() > 0) {
            Picasso.with(getContext()).load(currentNewsItem.getmNewsThumbnail()).into(holder.imgNewsIcon);
        } else {
            Picasso.with(getContext()).load(R.drawable.image_not_found).into(holder.imgNewsIcon);
        }

        // Set the published date of the news if available
        holder.tvNewsPublishedDate.setText(currentNewsItem.getmNewsPublishedDate());

        // Set the section name of the news if available
        holder.tvNewsSection.setText(currentNewsItem.getmNewsSection());

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
}
