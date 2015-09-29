
/**
 * Created by saiswathipujari on 9/17/15.
 */

    package com.saiswathipujari.rentals;

/**
 * Created by saiswathipujari on 9/17/15.
 */
    import android.content.ContentValues;
    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;
    import android.util.Log;

    import java.util.Arrays;
    import java.util.List;


    // TO USE:
// Change the package (at top) to match your project.
// Search for "TODO", and make the appropriate changes.
    public class FavouriteDB {

        /////////////////////////////////////////////////////////////////////
        // Constants & Data
        /////////////////////////////////////////////////////////////////////
        // For logging:
        private static final String TAG = "DBAdapter";


        // DB Fields
        public static final String KEY_ID = "_id";
        public static final int COL_ROWID = 0;
        /*
         * CHANGE 1:
         */
        // TODO: Setup your fields here:
        public static final String KEY_LISTINGID = "listing_id";
        public static final String KEY_PRICEBEDS = "pricebeds";
        public static final String KEY_DESC = "desc";
        public static final String KEY_ADDRESS = "address";
        public static final String KEY_IMAGE = "image";

        // TODO: Setup your field numbers here (0 = KEY_ROWID, 1=...)
        public static final int COL_NAME = 1;
        public static final int COL_AISLENUM = 2;


        public static final String[] ALL_KEYS = new String[] {KEY_ID,KEY_LISTINGID, KEY_PRICEBEDS,KEY_DESC,KEY_ADDRESS,KEY_IMAGE};



        // DB info: it's name, and the table we are using (just one).
        public static final String DATABASE_NAME = "MyDb";
        public static final String DATABASE_TABLE = "favourites";
        // Track DB version if a new version of your app changes the format.
        public static final int DATABASE_VERSION = 1;

        //_db.execSQL("CREATE TABLE favourites (listing_id INTEGER PRIMARY KEY, pricebeds TEXT, desc TEXT, address TEXT,image blob);");

        private static final String DATABASE_CREATE_SQL =
                "create table " + DATABASE_TABLE
                        + " (" + KEY_ID + " INTEGER PRIMARY KEY autoincrement, "

                            + KEY_LISTINGID + " TEXT, "

         /*
          * CHANGE 2:
          */
                        // TODO: Place your fields here!
                        // + KEY_{...} + " {type} not null"
                        // - Key is the column name you created above.
                        // - {type} is one of: text, integer, real, blob
                        //    (http://www.sqlite.org/datatype3.html)
                        //  - "not null" means it is a required field (must be given a value).
                        // NOTE: All must be comma separated (end of line!) Last one must have NO comma!!
                        + KEY_PRICEBEDS + " text, "
                        + KEY_DESC + " text,"
                        + KEY_ADDRESS + " text,"
                        + KEY_IMAGE + " blob"

                        // Rest  of creation:
                        + ");";

        // Context of application who uses us.
        private final Context context;

        private DatabaseHelper myDBHelper;
        private SQLiteDatabase db;

        /////////////////////////////////////////////////////////////////////
        // Public methods:
        /////////////////////////////////////////////////////////////////////

        public FavouriteDB(Context ctx) {
            this.context = ctx;
            myDBHelper = new DatabaseHelper(context);
        }

        // Open the database connection.
        public FavouriteDB open() {
            db = myDBHelper.getWritableDatabase();
            return this;
        }

        // Close the database connection.
        public void close() {
            myDBHelper.close();
        }
            // Add a new set of values to the database.
        public long insertRow(String listingid, String pricebeds,String desc, String address,byte[] image) {
      /*
       * CHANGE 3:
       */
            // TODO: Update data in the row with new fields.
            // TODO: Also change the function's arguments to be what you need!
            // Create row's data:
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_LISTINGID,listingid);
            initialValues.put(KEY_PRICEBEDS,pricebeds);
            initialValues.put(KEY_DESC,desc);
            initialValues.put(KEY_ADDRESS,address);
            initialValues.put(KEY_IMAGE,image);


            // Insert it into the database.
            return db.insert(DATABASE_TABLE, null, initialValues);
        }

        // Delete a row from the database, by rowId (primary key)
        public boolean deleteRow(String listingid) {
            String where = KEY_LISTINGID + "=" + listingid;
            return db.delete(DATABASE_TABLE, where, null) != 0;
        }

        public void deleteAll() {
            Cursor c = getAllRows();
            long rowId = c.getColumnIndexOrThrow(KEY_ID);
            if (c.moveToFirst()) {
                do {

                    String where = KEY_ID + "=" + rowId;
                    db.delete(DATABASE_TABLE, where, null);
                    //deleteRow(c.getLong((int) rowId));
                } while (c.moveToNext());
            }
            c.close();
        }

        // Return all data in the database.
        public Cursor getSpecificColumn(String name1, Integer cnt) {

            List<String> elephantList = Arrays.asList(name1.split(", "));

            String where = null;

            for(int i=0;i<elephantList.size();i++){

//                if(i==0)
//                {
//                    where = KEY_NAME + "='" + elephantList.get(i) + "'";
//
//                }
//                else
//                {
//                    where += "OR " + KEY_NAME + "='" + elephantList.get(i) + "'";
//
//                }
            }

            //Search.cnt = elephantList.size();
            Log.v(TAG, "chosen: "+cnt);
            Cursor c =     db.query(true, DATABASE_TABLE, ALL_KEYS,
                    where, null, null, null, null, null);

            //new String[]{KEY_AISLENUM

            if (c != null) {
                c.moveToFirst();
            }
            return c;
        }

        // Return all data in the database.
        public Cursor getAllRows() {
            String where = null;
            Cursor c =     db.query(true, DATABASE_TABLE, ALL_KEYS,
                    where, null, null, null, null, null);
            if (c != null) {
                c.moveToFirst();
            }
            return c;
        }


        // Get a specific row (by rowId)
        public Cursor getRow(String listingid) {
            String where = KEY_LISTINGID + "=" + listingid;
            Log.i("INFO",where);
            Cursor c = db.query(true, DATABASE_TABLE, ALL_KEYS,
                    where, null, null, null, null, null);
            if (c != null) {
                c.moveToFirst();
            }
            return c;
        }

        // Change an existing row to be equal to new data.
        public boolean updateRow(long rowId, String listingid, String pricebeds,String desc, String address,Byte[] image) {
            String where = KEY_ID + "=" + rowId;

      /*
       * CHANGE 4:
       */
            // TODO: Update data in the row with new fields.
            // TODO: Also change the function's arguments to be what you need!
            // Create row's data:
            ContentValues newValues = new ContentValues();
            newValues.put(KEY_LISTINGID, listingid);
            newValues.put(KEY_PRICEBEDS, pricebeds);
            newValues.put(KEY_DESC,desc);
            newValues.put(KEY_ADDRESS,address);
            newValues.put(KEY_IMAGE,image.toString());
            // Insert it into the database.
            return db.update(DATABASE_TABLE, newValues, where, null) != 0;
        }



        /////////////////////////////////////////////////////////////////////
        // Private Helper Classes:
        /////////////////////////////////////////////////////////////////////

        /**
         * Private class which handles database creation and upgrading.
         * Used to handle low-level database access.
         */
        private static class DatabaseHelper extends SQLiteOpenHelper
        {
            DatabaseHelper(Context context) {
                super(context, DATABASE_NAME, null, DATABASE_VERSION);
            }

            @Override
            public void onCreate(SQLiteDatabase _db) {
                _db.execSQL(DATABASE_CREATE_SQL);
            }

            @Override
            public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
                Log.w(TAG, "Upgrading application's database from version " + oldVersion
                        + " to " + newVersion + ", which will destroy all old data!");

                // Destroy old database:
                _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

                // Recreate new database:
                onCreate(_db);
            }
        }
    }
