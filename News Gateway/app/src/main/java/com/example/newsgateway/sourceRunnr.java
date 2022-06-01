package com.example.newsgateway;

import android.net.Uri;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.net.ssl.HttpsURLConnection;

public class sourceRunnr implements Runnable {
    private MainActivity mainActivity;
    private String sourceURL = "https://newsapi.org/v2/sources?language=en&country=us&category=&apiKey=5c013eb6738749598c0a62af5ea308ef";
    private ArrayList<source> sourceList = new ArrayList<>();

    public sourceRunnr(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        StringBuilder strBuildr = new StringBuilder();
        try {
            String link = sourceURL;
            Uri dataUri = Uri.parse(link);
            String urlToUse = dataUri.toString();
            URL url = new URL (urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
                strBuildr.append(line);
            }
        } catch (Exception e) {
            Toast.makeText(mainActivity, "Invalid Address", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        handleResults(strBuildr.toString());
    }

    private HashMap<String, HashSet<String>> parseJSON(String s) {
        HashMap<String, HashSet<String>> categoryMap = new HashMap<>();
        try {
            JSONObject jsonArray = new JSONObject(s);
            JSONArray sources = new JSONArray(jsonArray.getString("sources"));

            for (int i=0;i<sources.length();i++) {
                JSONObject jSource = (JSONObject) sources.get(i);
                String category = jSource.getString("category");
                String id = jSource.getString("id");

                if (id.isEmpty()) {
                    id = "Unspecified";
                }
                if (category.isEmpty()) {
                    continue;
                }
                if (!categoryMap.containsKey(category)) {
                    categoryMap.put(category, new HashSet<>());
                }
                HashSet<String> categorySet = categoryMap.get(category);
                if (categorySet != null) {
                    categorySet.add(id);
                }
            }
            return categoryMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void handleResults(String s) {
        final HashMap<String, HashSet<String>> sourceList1 = parseJSON(s);
        if (sourceList1 != null) {
            mainActivity.runOnUiThread(() -> mainActivity.setupCategories(sourceList1));
        }
    }
}
