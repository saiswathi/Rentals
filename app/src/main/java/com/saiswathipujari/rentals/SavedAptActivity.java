package com.saiswathipujari.rentals;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saiswathipujari on 9/17/15.
 */
public class SavedAptActivity extends AppCompatActivity {

    FavouriteDB fdb=null;
    SQLiteDatabase db=null;
    private static final String DATABASE_NAME = "FavDb.db";
    public static final int DATABASE_VERSION = 1;
    byte[] img;

    private SimpleCursorAdapter dataAdapter;
    private ArrayList<MyListItem> items;
    private ImageAdapter imageAdapter;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favourite);


    //        openDB();
    //        Cursor c = fdb.getAllRows();

    // Construct the data source
        items = new ArrayList();
        // Create the adapter to convert the array to views
        imageAdapter = new ImageAdapter(this, items);
        // Attach the adapter to a ListView
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(imageAdapter);

        addItemClickListener(listView);
        initDB();

        }


            private void initDB() {

                for (MyListItem i : getImages()) {
                    items.add(i);
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

    private void addItemClickListener(final ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                MyListItem item = (MyListItem) listView.getItemAtPosition(position);

                Intent intent = new Intent(SavedAptActivity.this, ApartmentActivity.class);
                intent.putExtra("listing_id", item.getListing_id());
                startActivity(intent);


            }
        });
    }

            public List<MyListItem> getImages() {
                List<MyListItem> MyListItems = new ArrayList<>();
                openDB();
                Cursor cursor = fdb.getAllRows();

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    MyListItem MyListItem = cursorToMyListItem(cursor);
                    MyListItems.add(MyListItem);
                    cursor.moveToNext();
                }
                cursor.close();
                return MyListItems;
            }

            private MyListItem cursorToMyListItem(Cursor cursor) {

                MyListItem listitem = new MyListItem();
                listitem.setListing_id(cursor.getString(cursor.getColumnIndex("listing_id")));

                listitem.setPrice(cursor.getString(cursor.getColumnIndex("pricebeds")));
                listitem.setDesc(cursor.getString(cursor.getColumnIndex("desc")));
                listitem.setLocality(cursor.getString(cursor.getColumnIndex("address")));
                listitem.setImg(cursor.getBlob(cursor.getColumnIndex("image")));

                return listitem;
            }


        }
