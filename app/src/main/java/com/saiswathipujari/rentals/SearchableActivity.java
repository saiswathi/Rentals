package com.saiswathipujari.rentals;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class SearchableActivity extends AppCompatActivity {

    SearchView searchView;
    String suggestion ;
    static String qu;
    Button rent,sale;
    String listingStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        rent = (Button) findViewById(R.id.button1);
        sale = (Button) findViewById(R.id.button2);

        rent.setBackgroundColor(Color.parseColor("#ff439d"));
        listingStatus = "rent";

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Because this activity has set launchMode="singleTop", the system calls this method
        // to deliver the intent if this activity is currently the foreground activity when
        // invoked again (when the user executes a search from this activity, we don't create
        // a new instance of this activity, so the system delivers the search intent here)
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // handles a click on a search suggestion; launches activity to show word
//            Intent wordIntent = new Intent(this, WordActivity.class);
//            wordIntent.setData(intent.getData());
//            startActivity(wordIntent);
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
                suggestion = intent.getStringExtra(SearchManager.QUERY);
//            showResults(query);
            //doMySearch(query);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);// Do not iconify the widget; expand it by default
        //searchView.setBaselineAligned(false);
        searchView.setMaxWidth(1000);
        //searchView.setSubmitButtonEnabled(true);

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                suggestion = getSuggestion(position);

                searchView.setQuery(suggestion, true); // submit query now
                return true; // replace default search manager behaviour
            }

            @Override
            public boolean onSuggestionSelect(int position)
            {
                return false;
            }
        });

        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                qu = newText;
                return true;
            }

        public boolean onQueryTextSubmit(String query) {

            //Here u can get the value "query" which is entered in the search box.
            qu = query;
            return true;
        }
        });
        return true;
    }


    private String getSuggestion(int position) {
        Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(
                position);
        String suggest1 = cursor.getString(cursor
                .getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
        return suggest1;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.done) {

            Intent i = new Intent(this,MainActivity.class);

            if(qu!=null) {
                qu = qu.replace(",", "%2C").replace(" ", "+");
            }
            i.putExtra("area", qu);
            i.putExtra("listingStatus", listingStatus);
            setResult(123, i);
            finish();
            //startActivityForResult(i, 123);

//            Bundle b = new Bundle();
//            b.putString("area", qu);
//            b.putString("listingStatus",listingStatus);
//            Intent ib = getIntent(); //gets the intent that called this intent
//            ib.putExtras(b);
//            setResult(Activity.RESULT_OK, ib);
//            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void goRent(View view) {

        rent.setBackgroundColor(Color.parseColor("#ff439d"));
        rent.setTextColor(Color.parseColor("#ffffff"));

        sale.setTextColor(Color.parseColor("#7f7f7f"));
        sale.setBackgroundColor(Color.TRANSPARENT);

        listingStatus = "rent";

    }

    public void goSale(View view) {
        sale.setBackgroundColor(Color.parseColor("#ff439d"));
        sale.setTextColor(Color.parseColor("#ffffff"));

        rent.setTextColor(Color.parseColor("#7f7f7f"));
        rent.setBackgroundColor(Color.TRANSPARENT);

        listingStatus = "sale";
    }
}
