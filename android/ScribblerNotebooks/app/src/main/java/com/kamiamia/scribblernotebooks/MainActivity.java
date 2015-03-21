package com.kamiamia.scribblernotebooks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "Response:";
    JSONArray dealsJson;
    private List<Deals> deals = new ArrayList<Deals>();
    GetDeals getDeals = new GetDeals();
    ListView list;
    String baseURL = "https://scribblernotebooksdev-akasantony.rhcloud.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = (ListView) findViewById(R.id.dealsListView);

        // If internet is available then execute getDeals async task
        if(isNetworkAvaliable()) {
            getDeals.execute();
        }
        else
            Toast.makeText(getApplicationContext(), "No WiFi Connection Detected. Enable network connection and try again.", Toast.LENGTH_LONG).show();
    }

    // Function to check if network is available
    private boolean isNetworkAvaliable(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifi.isConnected())
            return true;
        else
            return false;
    }

    // Set adapter for listview
    private void populateListView(){
        ArrayAdapter<Deals> adapter = new ListAdapter();
        list.setAdapter(adapter);
    }

    // Add members of listview to listview model class
    private void populateDeals(JSONArray dealsJson){
        int length;
        String id,title,desc1,logoPath,category;
        length  = dealsJson.length();

        for(int i=0;i<length;i++){
            try {
                JSONObject dealJson = dealsJson.getJSONObject(i);
                id = (dealJson.getString("AdID"));
                title = dealJson.getString("Title");
                desc1 = dealJson.getString("Desc1");
                category = dealJson.getString("Category");
                logoPath = dealJson.getString("logoPath");
                deals.add(new Deals(id,title,category,desc1,logoPath));
                Log.v(TAG,logoPath);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        populateListView();
    }


    public class GetDeals extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... arg0){
            try {
                String response;
                String concatResponse = "";
                URL url = new URL(baseURL.concat("/deals"));
                URLConnection urlConnection = url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((response = in.readLine()) != null)
                    concatResponse = concatResponse.concat(response);
                Log.v(TAG,concatResponse);
                dealsJson = parseJSON(concatResponse);
                in.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            populateDeals(dealsJson);
            listSelectListener();
        }
    }

    private class ListAdapter extends ArrayAdapter<Deals>{
        private ListAdapter() {

            super(MainActivity.this, R.layout.item_view,deals);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.item_view,parent,false);
            }

            Deals currentDeal = deals.get(position);

            TextView listTitle = (TextView)itemView.findViewById(R.id.title);
            listTitle.setText(currentDeal.getTitle());

            TextView listDesc1 = (TextView)itemView.findViewById(R.id.desc1);
            listDesc1.setText(currentDeal.getDescription1());

            TextView category = (TextView)itemView.findViewById(R.id.category);
            category.setText(currentDeal.getCategory());

            ImageView imgView = (ImageView)itemView.findViewById(R.id.imageView);
            new ImageLoadTask(currentDeal.getLogoPath(), imgView).execute();

            return itemView;
        }
    }

    public JSONArray parseJSON(String jsonString){
        JSONArray jsonObj = null;
        try {
            jsonObj = new JSONArray(jsonString);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return  jsonObj;
    }

    private void listSelectListener(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {

                Deals deal = deals.get(position);
                Intent myIntent = new Intent(MainActivity.this, DescriptionAcitivity.class);
                myIntent.putExtra("AdID", deal.getId()); //Optional parameters
                MainActivity.this.startActivity(myIntent);

            }
        });
    }


    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = baseURL.concat(url);

            Log.v(TAG,this.url);
            this.imageView = imageView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageView.setImageBitmap(null);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }

}

