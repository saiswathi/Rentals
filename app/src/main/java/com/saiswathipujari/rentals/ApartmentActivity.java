package com.saiswathipujari.rentals;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by saiswathipujari on 9/14/15.
 */
public class ApartmentActivity extends AppCompatActivity{

    String listing_id;
    TextView tv1,tv2,tv3,tv4,tv5;
    ImageView iv1;
    JSONObject object;
    boolean b = false;

    static final String API_KEY = "3fz5vsxvqd5a7ccfsj4cmwae";
    static final String API_URL = "http://api.zoopla.co.uk/api/v1/property_listings.js";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apartment);
        tv1 = (TextView) findViewById(R.id.textView1);
        tv2 = (TextView) findViewById(R.id.textView2);
        tv3 = (TextView) findViewById(R.id.textView3);
        tv4 = (TextView) findViewById(R.id.textView4);
        tv5 = (TextView) findViewById(R.id.textView5);

        tv5.setVisibility(View.GONE);

        iv1 = (ImageView) findViewById(R.id.imageView1);

        listing_id = getIntent().getStringExtra("listing_id");
        new RetrieveInfoTask().execute();


    }

    public void showText(View view) {

        if(b == false) {
            tv3.setVisibility(View.GONE);
            tv5.setVisibility(View.VISIBLE);
            b=true;
        }
        else
        {
            tv5.setVisibility(View.GONE);
            tv3.setVisibility(View.VISIBLE);
            b= false;
        }
    }

//    public void changeToTv5(View view) {
//        if(b==false)
//        {
//            tv3.setVisibility(View.GONE);
//            tv5.setVisibility(View.VISIBLE);
//
//        }
//    }
//
//    public void changeToTv3(View view) {
//        if(b==true)
//        {
//            tv5.setVisibility(View.VISIBLE);
//            tv3.setVisibility(View.GONE);
//            b=false;
//        }
//    }


    //Background process to get results from api
    class RetrieveInfoTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... urls) {

            try {

                URL url = new URL(API_URL+ "?listing_id="+listing_id+"&api_key=" + API_KEY);
//                "&property_type=flats"+"page_number=1&page_size=50"
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }


        protected void onPostExecute(String response) {
            if (response == null) {
                response = "THERE WAS AN ERROR";
            }

            Log.i("INFO", response);
            // do something with the feed

            try {
                object = new JSONObject(response);

                JSONArray listings = object.getJSONArray("listing");

                for (int i = 0; i < listings.length(); i++) {

                    JSONObject explrObject = listings.getJSONObject(i);

                    Log.i("INFO", explrObject.getString("listing_status"));

                    tv1.setText(explrObject.getString("displayable_address"));
                    tv2.setText("$" + explrObject.getString("price") + "\n" + explrObject.getString("num_bedrooms") + "Bed");

                    String iurl= explrObject.getString("image_url");
                    if (iurl != null && iurl.length() > 3) {
                        Picasso.with(getApplicationContext())
                                .load(iurl)
                                .placeholder(R.drawable.sorryimagenotavailable)
                                .into(iv1);
                    }

                    tv3.setText(explrObject.getString("description"));
                    tv5.setText(explrObject.getString("description"));

                    String linkText = "For Further Information Please Visit the <a href='" + explrObject.getString("details_url") +"'>Zoopla Listing</a> web page.";
                    tv4.setText(Html.fromHtml(linkText));
                    tv4.setMovementMethod(LinkMovementMethod.getInstance());

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
