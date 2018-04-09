package com.example.filip.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.webkit.JavascriptInterface;

import java.io.Serializable;

/**
 * Created by Tomek on 08.02.2018.
 */

class WebAppInterface implements jsInterface {
    public static SQLiteDatabase sql;
    Context mContext;

    WebAppInterface(Context c, SQLiteDatabase sqla) {
        mContext = c;
        sql = sqla;
    }

    @NonNull
    @JavascriptInterface
    public String getData(@NonNull int req) {
        utils.show_debug_message("get_Data", "Rozpoczynam");
        Cursor c = null;
        try {
            utils.show_debug_message("get_Data", "req = " + req);
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
        } catch (Exception e) {
            utils.show_debug_message("get_Data", e.getMessage());
            return "{ success: false, error: '" + e.getMessage() + "' }";
        } finally {
            utils.show_debug_message("get_Data", utils.cursorToString(c));
            return "{ success: true, data: " + utils.cursorToString(c) + "}";
        }
    }

    @NonNull
    @JavascriptInterface
    public String getData(@NonNull int req, @NonNull String option_1) {
        utils.show_debug_message("get_Data", "Rozpoczynam");
        Cursor c = null;
        try {
            utils.show_debug_message("get_Data", "req = " + req + " option1 = " + option_1);
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
        } catch (Exception e) {
            utils.show_debug_message("get_Data", e.getMessage());
            return "{ success: false, error: '" + e.getMessage() + "' }";
        } finally {
            utils.show_debug_message("get_Data", utils.cursorToString(c));
            return "{ success: true, data: " + utils.cursorToString(c) + "}";
        }
    }

    @NonNull
    @JavascriptInterface
    public String getData(@NonNull int req, @NonNull String option_1, @NonNull String option_2, @NonNull String option_3) {
        utils.show_debug_message("get_Data", "Rozpoczynam");
        Cursor c = null;
        try {
            utils.show_debug_message("get_Data", "req = " + req + " option1 = " + option_1);
            switch (req) {
                case 9:
                    c = sql.rawQuery("SELECT * from zywienie where przeciwskazania = ? and wegetarianizm = ? and weganizm = ?;", new String[]{option_1, option_2, option_3});
                    break;
                default:
                    return "{ success: false, error: 'unknown request' }";
            }
        } catch (Exception e)
        {
            utils.show_debug_message("get_Data", e.getMessage());
            return "{ success: false, error: '" + e.getMessage() + "' }";
        } finally {
            utils.show_debug_message("get_Data", utils.cursorToString(c));
            return "{ success: true, data: " + utils.cursorToString(c) + "}";
        }
    }

    @NonNull
    @JavascriptInterface
    public String writeSurveyData(@NonNull String key, @NonNull String value) {
        utils.show_debug_message("write_Survey_Data", "Rozpoczynam, key: " + key + " value: " + value);
        try {
            sql.execSQL("INSERT INTO _Ankieta (" + String.valueOf((char) 34) + "Pytanie" + String.valueOf((char) 34) + "," + String.valueOf((char) 34) + "Odpowiedz" + String.valueOf((char) 34) + ") VALUES (?,?);", new String[]{key, value});
        } catch (Exception e)
        {
            utils.show_debug_message("write_Survey_Data", "błąd: " + e.getMessage());
            return "{ success: false, error:'" + e.getMessage() + "'}";
        } finally {
            utils.show_debug_message("write_Survey_Data", "ok");
            return "{ success: true}";
        }
    }

    @NonNull
    @JavascriptInterface
    public String updateSurveyData(@NonNull Integer id, @NonNull String key, @NonNull String value) {
        utils.show_debug_message("update_Survey_Data", "Rozpoczynam id = " + id + " key = " + key + " value = " + value);
        try {
            sql.execSQL("UPDATE _Ankieta SET" + String.valueOf((char) 34) + "Pytanie" + String.valueOf((char) 34) + " = ? ," + String.valueOf((char) 34) + "Odpowiedz" + String.valueOf((char) 34) + " = ? WHERE id = " + id.toString() + ";", new String[]{key, value});
        } catch (Exception e)
        {
            utils.show_debug_message("update_Survey_Data", "Błąd " + e.getMessage());
            return "{ success: false, error:'" + e.getMessage() + "'}";
        } finally {
            utils.show_debug_message("update_Survey_Data", "OK");
            return "{ success: true}";
        }
    }

    @NonNull
    @JavascriptInterface
    public String getSurveyInfoById(@NonNull Integer id) {
        utils.show_debug_message("get_Survey_Info_By_Id", "Rozpoczynam id = " + id);
        Cursor c = null;
        try {
            c = sql.rawQuery("SELECT * from _Ankieta where id = " + id.toString() + ";", null);
        } catch (Exception e) // (Exception e) catch-all:s are bad mmkay.
        {
            utils.show_debug_message("get_Survey_Info_By_Id", "błąd: " + e.getMessage());
            return "{ success: false, error: '" + e.getMessage() + "' }"; //:)
        } finally {
            utils.show_debug_message("get_Survey_Info_By_Id", "OK");
            return "{ success: true, data: " + utils.cursorToString(c) + "}";
        }
    }

    @NonNull
    @JavascriptInterface
    public String getSurveyInfoAll() {
        utils.show_debug_message("get_Survey_Info_All", "Zaczynam");
        Cursor c = null;
        try {
            c = sql.rawQuery("SELECT * from _Ankieta;", null);
        } catch (Exception e)
        {
            utils.show_debug_message("get_Survey_Info_All", "błąd: " + e.getMessage());
            return "{ success: false, error: '" + e.getMessage() + "' }";
        } finally {
            utils.show_debug_message("get_Survey_Info_All", "OK");
            return "{ success: true, data: " + utils.cursorToString(c) + "}";
        }
    }

    @NonNull
    @JavascriptInterface
    public String clear() {
        utils.show_debug_message("wyczysc", "Rozpoczynam");
        try {
            sql.execSQL("VACUUM;", null);
        } catch (Exception e) {
            utils.show_debug_message("wyczysc", "Błąd: " + e.getMessage());
            return "{ success: false, error: '" + e.getMessage() + "'}";
        } finally {
            utils.show_debug_message("wyczysc", "OK");
            return "{ success: true}";
        }
    }

    @NonNull
    @JavascriptInterface
    public String profileGetData() {
        utils.show_debug_message("profile_Get_Data", "Zaczynam");
        Cursor c = null;
        try {
            c = sql.rawQuery("SELECT * from _profile;", null);
        } catch (Exception e)
        {
            utils.show_debug_message("profile_Get_Data", "błąd: " + e.getMessage());
            return "{ success: false, error: '" + e.getMessage() + "' }";
        } finally {
            utils.show_debug_message("profile_Get_Data", "OK");
            return "{ success: true, data: " + utils.cursorToString(c) + "}";
        }
    }

    @NonNull
    @JavascriptInterface
    public String profileGetLastestData() {
        utils.show_debug_message("profile_Get_Lastest_Da", "Zaczynam");
        Cursor c = null;
        try {
            c = sql.rawQuery("SELECT * from _profile where data=?;", new String[]{"-1"});
        } catch (Exception e) {
            utils.show_debug_message("profile_Get_Lastest_Da", "błąd: " + e.getMessage());
            return "{ success: false, error: '" + e.getMessage() + "' }";
        } finally {
            utils.show_debug_message("profile_Get_Lastest_Da", "OK");

            return "{ success: true, data: " + utils.cursorToString(c) + "}";
        }
    }

    @NonNull
    @JavascriptInterface
    public String profileLastestUpdate(@NonNull String first_name, @NonNull String Waga, @NonNull String Wzrost, @NonNull String drunked_water) {
        utils.show_debug_message("profile_lastest_update", "Zaczynam");
        utils.show_debug_message("profile_lastest_update", "first_name = " + first_name + " Waga = " + Waga + " Wzrost = " + Wzrost + "wypita woda = " + drunked_water);
        Cursor c = null;
        try {
            sql.execSQL("Update _profil SET first_name = ?, waga = ?, wzrost = ?,drunked_water = ? where data=?;", new String[]{first_name, Waga, Wzrost, drunked_water, "-1"});
        } catch (Exception e)
        {
            utils.show_debug_message("write_survey_data", "błąd: " + e.getMessage());
            return "{ success: false, error:'" + e.getMessage() + "'}";
        } finally {
            utils.show_debug_message("write_survey_data", "ok");
            return "{ success: true}";
        }
    }

    @NonNull
    @JavascriptInterface
    public String createNewSnapschot() {
        utils.show_debug_message("create_new_snapschot", "Zaczynam");
        Cursor c = null;
        try {
            sql.execSQL("insert into _profil (first_name,waga,wzrost,drunked_water,data) select first_name,waga,wzrost,drunked_water,date() from _profil where data = \"-1\";", null);
        } catch (Exception e) // (Exception e) catch-all:s are bad mmkay.
        {
            utils.show_debug_message("create_new_snapschot", "błąd: " + e.getMessage());
            return "{ success: false, error:'" + e.getMessage() + "'}";
        } finally {
            utils.show_debug_message("create_new_snapschot", "ok");
            return "{ success: true}";
        }
    }

    @NonNull
    @JavascriptInterface
    public String getResources(String tabela, String row) {
        utils.show_debug_message("get_resources", "Zaczynam");
        utils.show_debug_message("get_resources", "tabela " + tabela + " row " + row);
        Cursor c = null;
        try {
            c = sql.rawQuery("Select * from zalaczniki where tabela = ? and row = ?", new String[]{tabela, row});
        } catch (Exception e) {
            utils.show_debug_message("get_resources", "błąd: " + e.getMessage());
            return "{ success: false, error:'" + e.getMessage() + "'}";
        } finally {
            utils.show_debug_message("get_resources", "ok");
            return "{ success: true, data: " + utils.cursorToString(c) + "}";
        }
    }

    @JavascriptInterface
    public void throwNewException(String message, String additionalmessage) {
        utils.show_debug_message("thrownewexception", "zaczynam");
        utils.show_debug_message("thrownewexception", "message " + message + " addmsg " + additionalmessage);
        Intent err = new Intent(mContext, Main3Activity.class);
        err.putExtra("exception", (Serializable) new Exception("Sended by layout:" + message));
        err.putExtra("add_info", "Sended by layout:" + additionalmessage);
        mContext.startActivity(err);
        ((Activity) mContext).finish();
        new Exception(message).printStackTrace();
        return;
    }

    @JavascriptInterface
    public void log(String tag, String message) {
        utils.show_debug_message(tag, message);
        return;
    }
}