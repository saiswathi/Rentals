package com.saiswathipujari.rentals;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleMap mGoogleMap;
    WebView wv;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    ArrayList<LatLng> latlng = new ArrayList<LatLng>();
    ArrayList<Marker> mk = new ArrayList<Marker>();
    LatLng ll;
    String area, listingStatus;
    JSONObject object;
    String locality = null, imagestr = null, price = null, numBeds = null;

    URL imageURL;
    Bitmap bitmap;
    private ViewGroup hiddenPanel;
    private boolean isPanelShown;
    Handler handler;
    double markerlat, markerlng;
    String listing_id;
    String thumb_desc;
    ImageView iv;
    boolean fav = false;
    TextView listing;
    private byte[] img=null;
    Marker previousmarker;
    boolean prevfav = false;
    CameraUpdate update1;

    Marker savemarker;

    FavouriteDB fdb;

    static final String API_KEY = "3fz5vsxvqd5a7ccfsj4cmwae";
    static final String API_URL = "http://api.zoopla.co.uk/api/v1/property_listings.js";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            new RetrieveInfoTask(MainActivity.this).execute();
        }

        if (googleServicesAvailable()) {

            setContentView(R.layout.activity_main);
            openDB();
            fdb.deleteAll();

            getSupportActionBar().setDisplayShowTitleEnabled(false);

            wv = (WebView) findViewById(R.id.webView);
            wv.loadData(getString(R.string.zoopla), "text/html", null);
            hiddenPanel = (ViewGroup) findViewById(R.id.layout2);
            hiddenPanel.setFocusableInTouchMode(true);
            hiddenPanel.setVisibility(View.INVISIBLE);
            isPanelShown = false;

            handler = new Handler();
            if (initMap()) {

                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.setOnCameraChangeListener(new OnCameraChangeListener() {//setting markers when user zoom in
                    @Override
                    public void onCameraChange(CameraPosition position) {
                        LatLngBounds bounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;

                    }
                });

                mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){

                    @Override
                    public void onMapClick(LatLng latLng) {
                        hiddenPanel.setVisibility(View.INVISIBLE);
                        isPanelShown=false;
                    }
                });
                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                    @Override
                    public boolean onMarkerClick(final Marker marker) {

                        iv = (ImageView) hiddenPanel.findViewById(R.id.imageView1);

                        TextView aptPriceBeds = (TextView) hiddenPanel.findViewById(R.id.aptPriceBeds);
                        TextView apt_desc = (TextView) hiddenPanel.findViewById(R.id.apt_desc);
                        TextView tvSnippet = (TextView) hiddenPanel.findViewById(R.id.tv_snippet);
                        listing = (TextView) hiddenPanel.findViewById(R.id.listing_id);
                        Button b = (Button) hiddenPanel.findViewById(R.id.btnFav);

                        savemarker = marker;
                        LatLng ll = marker.getPosition();
                        aptPriceBeds.setText(marker.getTitle());

                        String[] arr = marker.getSnippet().split("@");

                        if (arr.length == 4) {
                            if (arr[1] != null && arr[1].length() > 3) {
                                Picasso.with(getApplicationContext())
                                        .load(arr[1])
                                        .placeholder(R.drawable.sorryimagenotavailable)
                                        .into(iv, new MarkerCallback(marker));
                            }
                            if (arr[2] != null) {
                                listing.setText(arr[2]);
                            }
                            if(arr[3]!=null)
                            {
                                apt_desc.setText(arr[3]);
                            }
                        }

                        tvSnippet.setText(arr[0]);

                        openDB();

                        Cursor c1 =fdb.getRow(listing.getText().toString());
                        if (!(c1.moveToFirst()) || c1.getCount() ==0){
                            //cursor is empty
                            fav = false;
                            b.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.rsz_btn_star_big_off, 0, 0, 0);
                            b.setText(R.string.save);

                        }
                        else
                        {
                            fav = true;
                            b.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.rsz_btn_star_big_on_pressed, 0, 0, 0);
                            b.setText(R.string.saved);

                        }

                        if (!isPanelShown) {
                            //Show the panel
                            Animation bottomUp = AnimationUtils.loadAnimation(MainActivity.this,
                                    R.anim.bottom_up);

                            hiddenPanel.startAnimation(bottomUp);
                            hiddenPanel.setVisibility(View.VISIBLE);

                            if(previousmarker!=null){
                                if(prevfav==false) {
                                    previousmarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.building_city_computer_flat_hardware_town_jcartier));
                                }
                                else
                                {
                                    previousmarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.imageepurple));
                                }
                            }
                            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                            previousmarker = marker;
                            prevfav = fav;
                            hiddenPanel.requestFocus();
                            isPanelShown = true;
                        } else {
                            // Hide the Panel
                            hiddenPanel.setVisibility(View.VISIBLE);
                            if(previousmarker!=null){
                                if(prevfav==false) {
                                    previousmarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.building_city_computer_flat_hardware_town_jcartier));
                                }
                                else
                                {
                                    previousmarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.imageepurple));
                                }
                            }
                            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                            previousmarker = marker;
                            prevfav = fav;

                            isPanelShown = true;
                        }


                        hiddenPanel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View view, boolean b) {

                                if (!b) {
                                    hiddenPanel.setVisibility(View.INVISIBLE);
                                    if(prevfav==false) {
                                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.building_city_computer_flat_hardware_town_jcartier));
                                    }
                                    else
                                    {
                                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.imageepurple));
                                    }

                                }
                            }
                        });

                        return true;
                    }

                });

            } else {
                Toast.makeText(this, "Map not available", Toast.LENGTH_LONG).show();
            }
        } else {
//            setContentView(R.layout.activity_main);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        closeDB();
    }


    private void openDB() {

        fdb = new FavouriteDB(this);
        fdb.open();

    }

    private void closeDB() {

        fdb.close();
    }

    public void changeFav(View view) {

        Button b = (Button) view.findViewById(R.id.btnFav);
        ImageView iv2= (ImageView) view.findViewById(R.id.imageView1);
        TextView aptPriceBeds = (TextView) hiddenPanel.findViewById(R.id.aptPriceBeds);
        TextView apt_desc = (TextView) hiddenPanel.findViewById(R.id.apt_desc);
        TextView tvSnippet = (TextView) hiddenPanel.findViewById(R.id.tv_snippet);
        listing = (TextView) hiddenPanel.findViewById(R.id.listing_id);

        if (fav == false) {
            fav = true;
            prevfav = true;
            b.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.rsz_btn_star_big_on_pressed, 0, 0, 0);
            b.setText(R.string.saved);
            savemarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.imageepurple));
            openDB();

            Bitmap btmp = ((BitmapDrawable)iv.getDrawable()).getBitmap();
            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            btmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
            img=bos.toByteArray();

            ContentValues cv=new ContentValues();
            cv.put("image", img);
            fdb.insertRow(listing.getText().toString(),aptPriceBeds.getText().toString(),apt_desc.getText().toString(),tvSnippet.getText().toString(),img);
        } else {
            fav = false;
            prevfav = false;
            savemarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.building_city_computer_flat_hardware_town_jcartier));
            b.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.rsz_btn_star_big_off, 0, 0, 0);
            b.setText(R.string.save);

            fdb.deleteRow(listing.getText().toString());
        }

    }

    public void goToAptInfo(View view) {

        Intent intent = new Intent(MainActivity.this, ApartmentActivity.class);
        intent.putExtra("listing_id", listing.getText());
        startActivity(intent);


    }


    public class MarkerCallback implements Callback {
        Marker marker = null;

        MarkerCallback(Marker marker) {
            this.marker = marker;
        }

        @Override
        public void onError() {
            Log.e(getClass().getSimpleName(), "Error loading thumbnail!");
        }

        @Override
        public void onSuccess() {
            if (marker != null && marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
                marker.showInfoWindow();
            }
        }
    }

    private boolean initMap() {
        if (mGoogleMap == null) {
            mGoogleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        }
        return (mGoogleMap != null);
    }

    public boolean googleServicesAvailable() {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Cant connect to play services", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favourite) {

            Intent i = new Intent(this,SavedAptActivity.class);
            startActivity(i);

            return true;
        }

        if (id == R.id.action_search) {
            Intent i = new Intent(this, SearchableActivity.class);
            startActivityForResult(i, 123);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {

            String area1 = data.getStringExtra("area");
            String listingStatus1 = data.getStringExtra("listingStatus");
            if((area1==null||area1.equals(area)) && listingStatus.equals(listingStatus1))
            {
                Log.i("info","going in");
            }
            else {
                area = data.getStringExtra("area");
                listingStatus = data.getStringExtra("listingStatus");
                hiddenPanel.setVisibility(View.GONE);
                new RetrieveInfoTask(MainActivity.this).execute();
            }

        }
    }


    private void setMarker() {

        openDB();
        BitmapDescriptor bt;

        Cursor c1 =fdb.getRow(listing_id.toString());
        if (!(c1.moveToFirst()) || c1.getCount() ==0){
            //cursor is empty
            bt =  BitmapDescriptorFactory.fromResource(R.mipmap.building_city_computer_flat_hardware_town_jcartier);

        }
        else
        {
            bt =  BitmapDescriptorFactory.fromResource(R.mipmap.imageepurple);

        }

        MarkerOptions options = new MarkerOptions()
                .title("$"+price+"\t\t "+numBeds+"Beds")
                .snippet(locality + "@" + imagestr + "@" + listing_id + "@" + thumb_desc)
                .position(new LatLng(markerlat, markerlng))
                .draggable(true)
                .icon(bt);

        mk.add(mGoogleMap.addMarker(options));
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {


    }

    @Override
    public void onLocationChanged(Location location) {

        if (location == null) {
            Toast.makeText(this, "Cant get Current location", Toast.LENGTH_LONG).show();
        } else {
            ll = new LatLng(location.getLatitude(), location.getLongitude());
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //Background process to get results from api
    class RetrieveInfoTask extends AsyncTask<Void, Void, String> {

        private ProgressDialog dialog;

        public RetrieveInfoTask(MainActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            dialog.setMessage("Loading listings...");
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... urls) {

            try {

                if (area == null) {
                    area = "oxford";
                }
                if (listingStatus == null) {
                    listingStatus = "rent";
                }
                URL url = new URL(API_URL + "?area=" + area +"&description_style=1&listing_status=" + listingStatus + "&order_by=age&page_size=100" + "&api_key=" + API_KEY);
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
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
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

                HashMap<LatLng,Boolean> hm = new HashMap<>();
                for (int i = 0; i < listings.length(); i++) {

                    JSONObject explrObject = listings.getJSONObject(i);
                    LatLng ll = new LatLng(explrObject.getDouble("latitude"), explrObject.getDouble("longitude"));

                    if(!hm.isEmpty())
                    {
                        if(hm.containsKey(ll))
                        {
                            if(hm.get(ll))
                            {
                                continue;
                            }
                            else
                            {
                                hm.put(ll,true);
                            }
                        }
                        else
                        {
                            hm.put(ll, true);
                        }
                    }
                    else {
                        hm.put(ll, true);
                    }
                    update1 = CameraUpdateFactory.newLatLngZoom(ll, 14);
                    mGoogleMap.moveCamera(update1);
                    Log.i("INFO", explrObject.getString("listing_status"));

                    imagestr = explrObject.getString("thumbnail_url");
                    listing_id = explrObject.getString("listing_id");
                    //Bitmap bt = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    locality = explrObject.getString("displayable_address");
                    markerlat = explrObject.getDouble("latitude");
                    markerlng = explrObject.getDouble("longitude");
                    price = explrObject.getString("price");
                    numBeds=explrObject.getString("num_bedrooms");
                    thumb_desc = explrObject.getString("short_description");
                    Log.i("INFO",explrObject.getString("short_description"));
                    setMarker();

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}

