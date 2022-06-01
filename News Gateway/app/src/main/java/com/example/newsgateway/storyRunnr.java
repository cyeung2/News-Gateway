package com.example.newsgateway;

import android.net.Uri;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class storyRunnr implements Runnable {
    private MainActivity mainActivity;
    private String APIKey = "&language=en&apiKey=5c013eb6738749598c0a62af5ea308ef";
    private String articleURL = "https://newsapi.org/v2/top-headlines?sources=";
    private ArrayList<story> articleList = new ArrayList<>();
    private final String selectedSource;

    public storyRunnr(MainActivity mainActivity, String selectedSource) {
        this.mainActivity = mainActivity;
        this.selectedSource = selectedSource;
    }

    @Override
    public void run() {
        StringBuilder sb = new StringBuilder();
        try {
            String link = articleURL + selectedSource + APIKey ;
            Uri dataUri = Uri.parse(link);
            String urlToUse = dataUri.toString();
            URL url = new URL (urlToUse);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.addRequestProperty("User-Agent", "");
            conn.connect();

            if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                handleResults(null);
                return;
            }

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            Toast.makeText(mainActivity, "Invalid Address", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        handleResults(sb.toString());
    }

    private void handleResults(String s) {

        if (s == null) {
            mainActivity.runOnUiThread(mainActivity::downloadFailedA);
            return;
        }
        articleList.clear();
        final ArrayList<story> articleList1 = parseJSON(s);
        for(int i=0;i<articleList1.size();i++) {
            articleList.add(articleList1.get(i));
        }
        mainActivity.runOnUiThread(() -> {
            if (articleList1 != null) {
                mainActivity.setArticles(articleList1);
                mainActivity.setTitle(articleList1.get(0).getName());
                Toast.makeText(mainActivity, "Loaded " + articleList1.size() + " articles.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private ArrayList<story> parseJSON(String s) {
        ArrayList<story> articleList1 = new ArrayList<>();
        try {
            JSONObject jsonArray = new JSONObject(s);
            JSONArray articles = new JSONArray(jsonArray.getString("articles"));

            for (int i=0;i<articles.length();i++) {
                story a = new story();
                JSONObject jSource = (JSONObject) articles.get(i);
                if (jSource.has("author")) {
                    a.setAuthor(jSource.getString("author"));
                }
                if (jSource.has("title")) {
                    a.setTitle(jSource.getString("title"));
                }
                if (jSource.has("description")) {
                    a.setDesc((jSource.getString("description")));
                }
                if (jSource.has("url")) {
                    a.setUrl((jSource.getString("url")));
                }
                if (jSource.has("urlToImage")) {
                    a.setUrlImg((jSource.getString("urlToImage")));
                }
                if (jSource.has("publishedAt")) {
                    a.setPubAt((jSource.getString("publishedAt")));
                }
                if (jSource.has("source")) {
                    JSONObject source = (JSONObject) jSource.getJSONObject("source");
                    a.setName(source.getString("name"));
                }
                articleList1.add(a);
            }
            return articleList1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articleList1;
    }
}
