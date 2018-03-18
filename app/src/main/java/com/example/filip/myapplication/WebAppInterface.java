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

    private static String cursorToString(Cursor crs) {
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
     *            req = 1 get_all_dish
     *            req = 2 get_all_sport
     *            req = 3 get_all_artykoly (tak niewiem jak jest po angielsku)
     *            req = 4 get_all_maps_position
     *            req = 5 get_all_slogans
     *            req = 6 get_default_filtred_dish
     *            req = 7 get_default_filtred_artykoly
     *            req = 8 get_default_filtred_sport
     *            req = 9 get_custom_filtred_dish option_1=Przeciwskazania option_2=Wegetarianizm option_3=Weganizm
     *            req = 10 get_custom_filtred_artukoly option_1=Grupa Wiekowa
     *            req = 11 get_custom_filtred_sport option_1=Przeciwskazania
     * @return String {status: Json operation status, (error:error mesage), (data:database return converted to json)}
     */
    @JavascriptInterface
    public static String get_Data(@NonNull int req) {
        if (config.debug == true) {
            Log.d("get_Data", "Rozpoczynam");
        }
        Cursor c = null;
        try {
            if (config.debug == true) {
                Log.d("get_Data", "req = " + req);
            }
            switch (req) {
                case 1:
                    c = sql.rawQuery("SELECT * from zywienie;", null);
                    break;
                case 2:
                    c = sql.rawQuery("SELECT * from sport;", null);
                    break;
                case 3:
                    c = sql.rawQuery("SELECT * from artykoly;", null);
                    break;
                case 4:
                    c = sql.rawQuery("SELECT * from gra_terenowa;", null);
                    break;
                case 5:
                    c = sql.rawQuery("SELECT * from slogan;", null);
                    break;
                case 6:
                    c = sql.rawQuery("SELECT * from gra_terenowa;", null);
                    break;
                default:
                    return "{ success: false, error: 'unknown request' }";
            }
        } catch (Exception e) // (Exception e) catch-all:s are bad mmkay.
        {
            if (config.debug) {
                Log.e("get_Data", e.getMessage());
            }
            return "{ success: false, error: '" + e.getMessage() + "' }";
        } finally {
            if (config.debug) {
                Log.d("get_Data", cursorToString(c));
            }
            return "{ success: true, data: " + cursorToString(c) + "}";
        }
    }

    @JavascriptInterface
    public static String get_Data(@NonNull int req, @NonNull String option_1) {
        if (config.debug == true) {
            Log.d("get_Data", "Rozpoczynam");
        }
        Cursor c = null;
        try {
            if (config.debug == true) {
                Log.d("get_Data", "req = " + req + " option1 = " + option_1);
            }
            switch (req) {
                case 10:
                    c = sql.rawQuery("SELECT * from artykoly where grupa_wiekowa = ?;", new String[]{option_1});
                    break;
                case 11:
                    c = sql.rawQuery("SELECT * from sport; where przeciwskazania = ?", new String[]{option_1});
                    break;
                default:
                    return "{ success: false, error: 'unknown request' }";
            }
        } catch (Exception e) // (Exception e) catch-all:s are bad mmkay.
        {
            if (config.debug == true) {
                Log.e("get_Data", e.getMessage());
            }
            return "{ success: false, error: '" + e.getMessage() + "' }";
        } finally {
            if (config.debug == true) {
                Log.d("get_Data", cursorToString(c));
            }
            return "{ success: true, data: " + cursorToString(c) + "}";
        }
    }

    @JavascriptInterface
    public static String get_Data(@NonNull int req, @NonNull String option_1, @NonNull String option_2, @NonNull String option_3) {
        if (config.debug == true) {
            Log.d("get_Data", "Rozpoczynam");
        }
        Cursor c = null;
        try {

            if (config.debug == true) {
                Log.d("get_Data", "req = " + req + " option1 = " + option_1);
            }
            switch (req) {
                case 9:
                    c = sql.rawQuery("SELECT * from zywienie where przeciwskazania = ? and wegetarianizm = ? and weganizm = ?;", new String[]{option_1, option_2, option_3});
                    break;
                default:
                    return "{ success: false, error: 'unknown request' }";
            }
        } catch (Exception e) // (Exception e) catch-all:s are bad mmkay.
        {
            if (config.debug == true) {
                Log.e("get_Data", e.getMessage());
            }
            return "{ success: false, error: '" + e.getMessage() + "' }";
        } finally {
            if (config.debug == true) {
                Log.d("get_Data", cursorToString(c));
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
    public static String write_Survey_Data(@NonNull String key, @NonNull String value) {
        if (config.debug == true) {
            Log.d("write_Survey_Data", "Rozpoczynam, key: " + key + " value: " + value);
        }
        try {
            sql.execSQL("INSERT INTO _Ankieta (" + String.valueOf((char) 34) + "Pytanie" + String.valueOf((char) 34) + "," + String.valueOf((char) 34) + "Odpowiedz" + String.valueOf((char) 34) + ") VALUES (?,?);", new String[]{key, value});
        } catch (Exception e) // (Exception e) catch-all:s are bad mmkay.
        {
            if (config.debug == true) {
                Log.e("write_Survey_Data", "błąd: " + e.getMessage());
            }
            return "{ success: false, error:'" + e.getMessage() + "'}";
        } finally {
            if (config.debug == true) {
                Log.d("write_Survey_Data", "ok");
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
        if (config.debug == true) {
            Log.d("update_Survey_Data", "Rozpoczynam id = " + id + " key = " + key + " value = " + value);
        }
        try {
            sql.execSQL("UPDATE _Ankieta SET" + String.valueOf((char) 34) + "Pytanie" + String.valueOf((char) 34) + " = ? ," + String.valueOf((char) 34) + "Odpowiedz" + String.valueOf((char) 34) + " = ? WHERE id = " + id.toString() + ";", new String[]{key, value});
        } catch (Exception e) // (Exception e) catch-all:s are bad mmkay.
        {
            if (config.debug == true) {
                Log.d("update_Survey_Data", "Błąd " + e.getMessage());
            }
            return "{ success: false, error:'" + e.getMessage() + "'}";
        } finally {
            if (config.debug == true) {
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
        if (config.debug == true) {
            Log.d("get_Survey_Info_By_Id", "Rozpoczynam id = " + id);
        }
        Cursor c = null;
        try {
            c = sql.rawQuery("SELECT * from _Ankieta where id = " + id.toString() + ";", null);
        } catch (Exception e) // (Exception e) catch-all:s are bad mmkay.
        {
            if (config.debug == true) {
                Log.e("get_Survey_Info_By_Id", "błąd: " + e.getMessage());
            }
            return "{ success: false, error: '" + e.getMessage() + "' }"; //:)
        } finally {
            if (config.debug == true) {
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
        if (config.debug == true) {
            Log.d("get_Survey_Info_All", "Zaczynam");
        }
        Cursor c = null;
        try {
            c = sql.rawQuery("SELECT * from _Ankieta;", null);
        } catch (Exception e) // (Exception e) catch-all:s are bad mmkay.
        {
            if (config.debug == true) {
                Log.e("get_Survey_Info_All", "błąd: " + e.getMessage());
            }
            return "{ success: false, error: '" + e.getMessage() + "' }"; //:)
        } finally {
            if (config.debug == true) {
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
        if (config.debug == true) {
            Log.d("wyczysc", "Rozpoczynam");
        }
        try {
            sql.execSQL("VACUUM;", null);
        } catch (Exception e) {
            if (config.debug == true) {
                Log.e("wyczysc", "Błąd: " + e.getMessage());
            }
            return "{ success: false, error: '" + e.getMessage() + "'}";
        } finally {
            if (config.debug == true) {
                Log.d("wyczysc", "OK");
            }
            return "{ success: true}";
        }
    }

    /**
     * Get all Profile data
     *
     * @return String Json operation status (witch error)
     */
    @JavascriptInterface
    public static String profile_Get_Data() {
        if (config.debug == true) {
            Log.d("profile_Get_Data", "Zaczynam");
        }
        Cursor c = null;
        try {
            c = sql.rawQuery("SELECT * from _profile;", null);
        } catch (Exception e) // (Exception e) catch-all:s are bad mmkay.
        {
            if (config.debug == true) {
                Log.e("profile_Get_Data", "błąd: " + e.getMessage());
            }
            return "{ success: false, error: '" + e.getMessage() + "' }"; //:)
        } finally {
            if (config.debug == true) {
                Log.d("profile_Get_Data", "OK");
            }
            return "{ success: true, data: " + cursorToString(c) + "}";
        }
    }

    /**
     * get lastest profile data
     *
     * @return String Json operation status (witch error)
     */
    @JavascriptInterface
    public static String profile_Get_Lastest_Data() {
        if (config.debug == true) {
            Log.d("profile_Get_Lastest_Da", "Zaczynam");
        }
        Cursor c = null;
        try {
            c = sql.rawQuery("SELECT * from _profile where data=?;", new String[]{"-1"});
        } catch (Exception e)
        {
            if (config.debug == true) {
                Log.e("profile_Get_Lastest_Da", "błąd: " + e.getMessage());
            }
            return "{ success: false, error: '" + e.getMessage() + "' }"; //:)
        } finally {
            if (config.debug == true) {
                Log.d("profile_Get_Lastest_Da", "OK");
            }
            return "{ success: true, data: " + cursorToString(c) + "}";
        }
    }
    /**
     * update lastest profile data
     *
     * @param first_name String
     * @param first_name waga
     * @param first_name wzrost
     * @param first_name drunked_water
     *
     * @return String Json operation status (witch error)
     */
    @JavascriptInterface
    public static String profile_lastest_update(@NonNull String first_name,@NonNull String Waga,@NonNull String Wzrost, @NonNull String drunked_water) {
        if (config.debug == true) {
            Log.d("profile_lastest_update", "Zaczynam");
        }
        if (config.debug == true) {
            Log.d("profile_lastest_update", "first_name = "+first_name+" Waga = "+Waga+" Wzrost = " + Wzrost + "wypita woda = "+drunked_water);
        }
        Cursor c = null;
        try {
            sql.execSQL("Update _profil SET first_name = ?, waga = ?, wzrost = ?,drunked_water = ? where data=?;", new String[]{first_name,Waga,Wzrost,drunked_water,"-1"});
        } catch (Exception e) // (Exception e) catch-all:s are bad mmkay.
        {
            if (config.debug == true) {
                Log.e("write_survey_data", "błąd: " + e.getMessage());
            }
            return "{ success: false, error:'" + e.getMessage() + "'}";
        } finally {
            if (config.debug == true) {
                Log.d("write_survey_data", "ok");
            }
            return "{ success: true}";
        }
    }
    /**
     * create snapschot form lastest profile state
     *
     * @return String Json operation status (witch error)
     */
    @JavascriptInterface
    public static String create_new_snapschot() {
        if (config.debug == true) {
            Log.d("create_new_snapschot", "Zaczynam");
        }
        Cursor c = null;
        try {
            sql.execSQL("insert into _profil (first_name,waga,wzrost,drunked_water,data) select first_name,waga,wzrost,drunked_water,date() from _profil where data = \"-1\";", null);
        } catch (Exception e) // (Exception e) catch-all:s are bad mmkay.
        {
            if (config.debug == true) {
                Log.e("create_new_snapschot", "błąd: " + e.getMessage());
            }
            return "{ success: false, error:'" + e.getMessage() + "'}";
        } finally {
            if (config.debug == true) {
                Log.d("create_new_snapschot", "ok");
            }
            return "{ success: true}";
        }

    }
}