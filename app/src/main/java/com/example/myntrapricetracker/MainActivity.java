package com.example.myntrapricetracker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText productCodeInput;
    private TextView dataTextView;

    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productCodeInput = findViewById(R.id.productCodeInput);
        dataTextView = findViewById(R.id.dataTextView);
        Button submitButton = findViewById(R.id.submitButton);

        client = new OkHttpClient.Builder().build();

        submitButton.setOnClickListener(v -> {
            String code = productCodeInput.getText().toString().trim();
            if (!code.isEmpty()) {
                fetchProductData(code);
            } else {
                Toast.makeText(MainActivity.this, "Please enter a product code", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchProductData(String code) {
        String url = "http://192.168.29.114:6969/chartData?productCode=" + code; // Replace with your server URL

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        List<DataItem> productList = parseProductData(responseData);
                        runOnUiThread(() -> {
                            displayProductData(productList);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Server error: " + response.message(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private List<DataItem> parseProductData(String responseData) throws JSONException {
        Map<String, List<DataItem>> productMap = new HashMap<>();
        JSONArray jsonArray = new JSONArray(responseData);
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String title = jsonObject.getString("title");
            String priceInt = jsonObject.getString("priceInt");
            String time = jsonObject.getString("time");

            Date date;
            try {
                date = inputFormat.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
                continue; // Skip this entry if date parsing fails
            }

            DataItem item = new DataItem(title, priceInt, date);
            if (!productMap.containsKey(title)) {
                List<DataItem> items = new ArrayList<>();
                items.add(item);
                productMap.put(title, items);
            } else {
                List<DataItem> items = productMap.get(title);
                items.add(item);
                productMap.put(title, items);
            }
        }

        // Sort entries by date (descending) for each product
        for (List<DataItem> items : productMap.values()) {
            Collections.sort(items, Comparator.comparing(DataItem::getTime).reversed());
        }

        // Flatten map into a list of DataItem objects
        List<DataItem> productList = new ArrayList<>();
        for (List<DataItem> items : productMap.values()) {
            productList.addAll(items);
        }

        return productList;
    }

    private void displayProductData(List<DataItem> productList) {
        StringBuilder stringBuilder = new StringBuilder();

        // Group and display data by product name
        Map<String, List<DataItem>> productGroups = groupByProductName(productList);

        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy 'at' hh:mm a", Locale.getDefault());

        for (Map.Entry<String, List<DataItem>> entry : productGroups.entrySet()) {
            stringBuilder.append("Product: ").append(entry.getKey()).append("\n");

            // Display each price with its time, starting from the latest
            List<DataItem> items = entry.getValue();
            for (DataItem item : items) {
                stringBuilder.append("(").append(outputFormat.format(item.getTime())).append(")").append("   ")
                        .append(item.getPrice()).append("\n");
            }
            stringBuilder.append("\n");
        }

        dataTextView.setText(stringBuilder.toString());
    }

    private Map<String, List<DataItem>> groupByProductName(List<DataItem> productList) {
        Map<String, List<DataItem>> productGroups = new HashMap<>();

        for (DataItem item : productList) {
            if (!productGroups.containsKey(item.getTitle())) {
                List<DataItem> items = new ArrayList<>();
                items.add(item);
                productGroups.put(item.getTitle(), items);
            } else {
                List<DataItem> items = productGroups.get(item.getTitle());
                items.add(item);
                productGroups.put(item.getTitle(), items);
            }
        }

        return productGroups;
    }

    private static class DataItem {
        private String title;
        private String price;
        private Date time;

        public DataItem(String title, String price, Date time) {
            this.title = title;
            this.price = price;
            this.time = time;
        }

        public String getTitle() {
            return title;
        }

        public String getPrice() {
            return price;
        }

        public Date getTime() {
            return time;
        }
    }
}












