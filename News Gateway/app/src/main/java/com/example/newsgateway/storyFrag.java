package com.example.newsgateway;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link storyFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class storyFrag extends Fragment implements View.OnClickListener{
    private static story a;
    ImageView image;

    public storyFrag() {
    }

    public static storyFrag newInstance(story article, int index, int max) {
        storyFrag fragment = new storyFrag();
        a = article;
        Bundle args = new Bundle();
        args.putSerializable("ARTICLE_DATA", article);
        args.putSerializable("INDEX", index);
        args.putSerializable("TOTAL_COUNT", max);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment_layout = inflater.inflate(R.layout.fragment_story, container, false);
        Bundle args = getArguments();
        if (args != null) {
            final story currentArticle = (story) args.getSerializable("ARTICLE_DATA");
            if (currentArticle == null) {
                return null;
            }
            int index = args.getInt("INDEX");
            int total = args.getInt("TOTAL_COUNT");

            TextView title = fragment_layout.findViewById(R.id.title_text);
            title.setOnClickListener(this);
            title.setText(currentArticle.getTitle());
            TextView date = fragment_layout.findViewById(R.id.date_text);
            DateTimeFormatter inputFormatter;
            if (currentArticle.getPubAt().contains("Z")) {
                inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
            } else if (currentArticle.getPubAt().contains("+")) {
                inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+'SS:SS", Locale.ENGLISH);
            }
            else {
                inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
            }
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("LLLL dd, yyy HH:ss", Locale.ENGLISH);
            LocalDateTime dates = LocalDateTime.parse(currentArticle.getPubAt(), inputFormatter);
            String formattedDate = outputFormatter.format(dates);
            date.setText(formattedDate);
            TextView author = fragment_layout.findViewById(R.id.authors_text);
            if (currentArticle.getAuthor().equals("null")) {
                author.setTextAppearance(View.INVISIBLE);
            } else {
                author.setText(currentArticle.getAuthor());
            }
            image = fragment_layout.findViewById(R.id.image);
            image.setOnClickListener(this);
            if (currentArticle.getUrlImg().equals("") || currentArticle.getUrlImg().equals("None")) {
            }
            else {
                Picasso.get().load(currentArticle.getUrlImg()).into(image);
            }
            TextView desc = fragment_layout.findViewById(R.id.description_text);
            desc.setOnClickListener(this);
            desc.setText(currentArticle.getDesc());
            TextView pageNum = fragment_layout.findViewById(R.id.page_num);

            pageNum.setText(String.format(Locale.US, "%d of %d", index, total));

            return fragment_layout;
        }
        else {
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        Bundle args = getArguments();
        if (args != null) {
            final story currentArticle = (story) args.getSerializable("ARTICLE_DATA");
            switch (v.getId()) {
                case R.id.description_text:
                case R.id.image:
                case R.id.title_text:
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String url = currentArticle.getUrl();
                    intent.setData(Uri.parse(url));
                    if (url != null) {
                        startActivity(intent);
                    }
                    break;
            }
        }
    }
}