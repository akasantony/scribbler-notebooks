package com.kamiamia.scribblernotebooks;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


public class DescriptionAcitivity extends ActionBarActivity {

    String param;
    String TAG = "Process 2:";
    JSONArray dealJsonArray;
    TextView title;
    TextView category;
    TextView desc2;
    ImageView imgView;
    String url;
    String baseURL = "https://scribblernotebooksdev-akasantony.rhcloud.com";
    GetDeal getDeal = new GetDeal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        param = intent.getStringExtra("AdID");
        Log.v(TAG,param);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_acitivity);
        title = (TextView) findViewById(R.id.title);
        category = (TextView) findViewById(R.id.category);
        desc2 = (TextView) findViewById(R.id.desc3);
        imgView = (ImageView) findViewById(R.id.coverImg);

        getDeal.execute();
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_description_acitivity, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public class GetDeal extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                String response;
                String concatResponse = "";
                URL url = new URL(baseURL.concat("/deal/").concat(param));
                Log.v(TAG,"Converted url "+url.toString());
                URLConnection urlConnection = url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((response = in.readLine()) != null)
                    concatResponse = concatResponse.concat(response);
                Log.v(TAG, concatResponse);
                dealJsonArray = parseJSON(concatResponse);
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setAttributes(dealJsonArray);
        }


    }

    private void setAttributes(JSONArray deal){

        int length  = dealJsonArray.length();

        for(int i=0;i<length;i++){
            try {
                Log.v(TAG, "Entering setAtt()");
                JSONObject dealJson = deal.getJSONObject(i);
                title.setText(dealJson.getString("Title"));
                category.setText(dealJson.getString("Category"));
                url = dealJson.getString("coverPath");
//                new ImageLoadTask(url,imgView).execute();
                desc2.setText(dealJson.getString("Desc2"));
                Log.v(TAG, dealJson.getString("Title"));

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        new ImageLoadTask(url,imgView).execute();
    }


    public JSONArray parseJSON(String jsonString) {
        JSONArray jsonObj = null;
        try {
            jsonObj = new JSONArray(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObj;
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
