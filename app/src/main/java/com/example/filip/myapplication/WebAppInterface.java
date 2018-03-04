package com.example.filip.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tomek on 08.02.2018.
 */

class WebAppInterface {
    /**
     * Instantiate the interface and set the context
     */
    public static SQLiteDatabase sql;
    Context mContext;

    WebAppInterface(Context c, SQLiteDatabase sqla) {
        mContext = c;
        sql = sqla;
    }

    private  static String cursorToString(Cursor crs) {
        JSONArray arr = new JSONArray();
        crs.moveToFirst();
        while (!crs.isAfterLast()) {
            int nColumns = crs.getColumnCount();
            JSONObject row = new JSONObject();
            for (int i = 0; i < nColumns; i++) {
                String colName = crs.getColumnName(i);
                if (colName != null) {
                    String val = "";
                    try {
                        switch (crs.getType(i)) {
                            case Cursor.FIELD_TYPE_BLOB:
                                row.put(colName, crs.getBlob(i).toString());
                                break;
                            case Cursor.FIELD_TYPE_FLOAT:
                                row.put(colName, crs.getDouble(i));
                                break;
                            case Cursor.FIELD_TYPE_INTEGER:
                                row.put(colName, crs.getLong(i));
                                break;
                            case Cursor.FIELD_TYPE_NULL:
                                row.put(colName, null);
                                break;
                            case Cursor.FIELD_TYPE_STRING:
                                row.put(colName, crs.getString(i));
                                break;
                        }
                    } catch (JSONException e) {
                    }
                }
            }
            arr.put(row);
            if (!crs.moveToNext())
                break;
        }
        crs.close(); // close the cursor
        return arr.toString();
    }

    /**
     * Get information from
     *
     * @param req value of request
     *            req = 1 get_dish_name
     *            req = 2 get_target_of_sport
     *            req = 3 get_all_dish
     *            req = 4 get_all_sport
     * @return String {status: Json operation status, (error:error mesage), (data:database return converted to json)}
     */
    @JavascriptInterface
    public static String get_Data(@NonNull int req) {
        if (config.debug==true) {
            Log.d("get_Data","Rozpoczynam");
        }
        Cursor c = null;
        try {
            if (config.debug==true) {
                Log.d("get_Data","req = "+req);
            }
            switch (req) {
                case 1:
                    c = sql.rawQuery("SELECT Dish from Przepisy_do_aplikacji_Erasmus;", null);
                    break;
                case 2:
                    c = sql.rawQuery("SELECT cel from Sport;", null);
                    break;
                case 3:
                    c = sql.rawQuery("SELECT * from Przepisy_do_aplikacji_Erasmus;", null);
                    break;
                case 4:
                    c = sql.rawQuery("SELECT * from Sport;", null);
                    break;
            }
        } catch (Exception e) // (Exception e) catch-all:s are bad mmkay.
        {
            if (config.debug==true) {
                Log.e("get_Data",e.getMessage());
            }
            return "{ success: false, error: '" + e.getMessage() + "' }"; //:)
        } finally {
            if (config.debug==true) {
                Log.d("get_Data",cursorToString(c));
            }
            return "{ success: true, data: " + cursorToString(c) + "}";
        }
    }

    /**
     * Add new ro to _Ankieta table
     *
     * @param key   key column value
     * @param value value column value
     * @return String Json operation status (witch error)
     */
    @JavascriptInterface
    public static String write_survey_data(@NonNull String key,@NonNull String value) {
        if (config.debug==true) {
            Log.d("write_survey_data","Rozpoczynam, key: "+key+" value: "+value);
        }
        try {
            sql.execSQL("INSERT INTO _Ankieta (" + String.valueOf((char) 34) + "Pytanie" + String.valueOf((char) 34) + "," + String.valueOf((char) 34) + "Odpowiedz" + String.valueOf((char) 34) + ") VALUES (?,?);", new String[]{key, value});
        } catch (Exception e) // (Exception e) catch-all:s are bad mmkay.
        {
            if (config.debug==true) {
                Log.e("write_survey_data","błąd: "+e.getMessage());
            }
            return "{ success: false, error:'" + e.getMessage() + "'}";
        } finally {
            if (config.debug==true) {
                Log.d("write_survey_data","ok");
            }
            return "{ success: true}";
        }
    }

    /**
     * Update _Ankieta table
     *
     * @param id    identyfier of database row
     * @param key   key column value
     * @param value value column value
     * @return String Json operation status (witch error)
     */
    @JavascriptInterface
    public static String update_Survey_Data(@NonNull Integer id, @NonNull String key, @NonNull String value) {
        if (config.debug==true) {
            Log.d("update_Survey_Data","Rozpoczynam id = "+id+" key = "+key+" value = "+value);
        }
        try {
            sql.execSQL("UPDATE _Ankieta SET" + String.valueOf((char) 34) + "Pytanie" + String.valueOf((char) 34) + " = ? ," + String.valueOf((char) 34) + "Odpowiedz" + String.valueOf((char) 34) + " = ? WHERE id = " + id.toString() + ";", new String[]{key, value});
        } catch (Exception e) // (Exception e) catch-all:s are bad mmkay.
        {
            if (config.debug==true) {
                Log.d("update_Survey_Data", "Błąd "+e.getMessage());
            }
            return "{ success: false, error:'" + e.getMessage() + "'}";
        } finally {
            if (config.debug==true) {
                Log.d("update_Survey_Data", "OK");
            }
            return "{ success: true}";
        }
    }
    /**
     * Get row form table _Ankieta by id
     *
     * @param id Id value
     * @return String {status: Json operation status, (error:error mesage), (data:database return converted to json)}
     */
    @JavascriptInterface
    public static String get_Survey_Info_By_Id(@NonNull Integer id) {
        if (config.debug==true) {
            Log.d("get_Survey_Info_By_Id", "Rozpoczynam id = "+id);
        }
        Cursor c = null;
        try {
            c = sql.rawQuery("SELECT * from _Ankieta where id = " + id.toString() + ";", null);
        } catch (Exception e) // (Exception e) catch-all:s are bad mmkay.
        {
            if (config.debug==true) {
                Log.e("get_Survey_Info_By_Id", "błąd: "+e.getMessage());
            }
            return "{ success: false, error: '" + e.getMessage() + "' }"; //:)
        } finally {
            if (config.debug==true) {
                Log.d("get_Survey_Info_By_Id", "OK");
            }
            return "{ success: true, data: " + cursorToString(c) + "}";
        }
    }
    /**
     * Get all rows form table _Ankieta by id
     *
     * @return String {status: Json operation status, (error:error mesage), (data:database return converted to json)}
     */
    @JavascriptInterface
    public static String get_Survey_Info_All() {
        if (config.debug==true) {
            Log.d("get_Survey_Info_All", "Zaczynam");
        }
        Cursor c = null;
        try {
            c = sql.rawQuery("SELECT * from _Ankieta;", null);
        } catch (Exception e) // (Exception e) catch-all:s are bad mmkay.
        {
            if (config.debug==true) {
                Log.e("get_Survey_Info_All", "błąd: "+e.getMessage());
            }
            return "{ success: false, error: '" + e.getMessage() + "' }"; //:)
        } finally {
            if (config.debug==true) {
                Log.d("get_Survey_Info_All", "OK");
            }
            return "{ success: true, data: " + cursorToString(c) + "}";
        }
    }

    /**
     * Clear and optymalize database
     *
     * @return String Json operation status (witch error)
     */
    @JavascriptInterface
    public static String wyczysc() {
        if (config.debug==true) {
            Log.d("wyczysc", "Rozpoczynam");
        }
        try {
            sql.execSQL("VACUUM;", null);
        } catch (Exception e) {
            if (config.debug==true) {
                Log.e("wyczysc", "Błąd: "+e.getMessage());
            }
            return "{ success: false, error: '" + e.getMessage() + "'}";
        } finally {
            if (config.debug==true) {
                Log.d("wyczysc", "OK");
            }
            return "{ success: true}";
        }
    }

}
