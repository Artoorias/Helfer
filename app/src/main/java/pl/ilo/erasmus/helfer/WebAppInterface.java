package pl.ilo.erasmus.helfer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    //getData function for 1-8
    @NonNull
    @JavascriptInterface
    public String getData(@NonNull int req) {
        utils.show_debug_message("get_Data", "Rozpoczynam");
        Cursor c = null;
        try {
            utils.show_debug_message("get_Data", "req = " + req);
            switch (req) {
                case 1: //get_all_dish
                    c = sql.rawQuery("SELECT * from zywienie;", null);
                    break;
                case 2: //get_all_sport
                    c = sql.rawQuery("SELECT * from sport;", null);
                    break;
                case 3: //get_all_artykoly
                    c = sql.rawQuery("SELECT * from artykoly;", null);
                    break;
                case 4: //get_all_maps_position
                    c = sql.rawQuery("SELECT * from gra_terenowa;", null);
                    break;
                case 5: //get_all_slogans
                    c = sql.rawQuery("SELECT * from slogan;", null);
                    break;
                case 6: //get_default_filtred_dish
                    c = sql.rawQuery("select z.* from zywienie z where  ((not exists (select * from _Ankieta where Pytanie=\"12-input-0\"))or(z.Wegetarianizm=\"TAK\")) and ((not exists (select * from _Ankieta where Pytanie=\"12-input-1\"))or(z.Weganizm=\"TAK\"));", null);
                    break;
                case 7: //get_default_filtred_artykoly
                    c = sql.rawQuery("Select * from artykoly where Grupa_wiekowa like \"%brak%\" or Grupa_wiekowa like (Select Odpowiedz from _Ankieta where Pytanie =\"1\");", null);
                    break;
                case 8: //get_default_filtred_sport
                    c = sql.rawQuery("select z.* from zywienie z where  ((not exists (select * from _Ankieta where Pytanie=\"12-input-0\"))or(z.Wegetarianizm=\"TAK\")) and ((not exists (select * from _Ankieta where Pytanie=\"12-input-1\"))or(z.Weganizm=\"TAK\"));", null);
                    break;
                default:
                    return utils.returnError("Unknown request");
            }
        } catch (Exception e) {
            utils.show_debug_message("get_Data", e.getMessage());
            return utils.returnError(e.getMessage());
        }
        String tmp = utils.cursorToString(c);
        utils.show_debug_message("get_Data", tmp);
        return utils.returnData(tmp);
    }

    //getData for req 10,11
    @NonNull
    @JavascriptInterface
    public String getData(@NonNull int req, @NonNull String option_1) {
        utils.show_debug_message("get_Data", "Rozpoczynam");
        Cursor c = null;
        try {
            utils.show_debug_message("get_Data", "req = " + req + " option1 = " + option_1);
            switch (req) {
                case 10: //get_custom_filtred_artukoly
                    c = sql.rawQuery("SELECT * from artykoly where grupa_wiekowa = ?;", new String[]{option_1});
                    break;
                case 11: //get_custom_filtred_sport
                    c = sql.rawQuery("SELECT * from sport where przeciwskazania = ?;", new String[]{option_1});
                    break;
                default:
                    return utils.returnError("Unknown request");
            }
        } catch (Exception e) {
            utils.show_debug_message("get_Data", e.getMessage());
            return utils.returnError(e.getMessage());
        }
        String tmp = utils.cursorToString(c);
        utils.show_debug_message("get_Data", tmp);
        return utils.returnData(tmp);
    }

    //getData for req 9
    @NonNull
    @JavascriptInterface
    public String getData(@NonNull int req, @NonNull String option_1, @NonNull String option_2, @NonNull String option_3) {
        utils.show_debug_message("get_Data", "Rozpoczynam");
        Cursor c = null;
        try {
            utils.show_debug_message("get_Data", "req = " + req + " option1 = " + option_1);
            switch (req) {
                case 9: //get_custom_filtred_dish
                    String query ="SELECT * from zywienie ";
                    List<String> arr = new ArrayList<String>();
                    List<String> params = new ArrayList<String>();
                    if (!option_1.equals("*")) {
                        arr.add("przeciwskazania = ? ");
                        params.add(option_1);
                    }
                    if (!option_2.equals("*")) {
                        arr.add("wegetarianizm = ? ");
                        params.add(option_2);
                    }
                    if (!option_3.equals("*")) {
                        arr.add("weganizm = ? ");
                        params.add(option_3);
                    }

                    if (arr.size() > 0) {
                        query += "where ";
                    }

                    query += TextUtils.join(" and ", arr) + ';';

                    c = sql.rawQuery(query, params.toArray(new String[0]));
                    break;
                default:
                    return utils.returnError("Unknown request");
            }
        } catch (Exception e) {
            utils.show_debug_message("get_Data", e.getMessage());
            return utils.returnError(e.getMessage());
        }
        String tmp = utils.cursorToString(c);
        utils.show_debug_message("get_Data", tmp);
        return utils.returnData(tmp);
    }

    //write to _Ankieta table
    @NonNull
    @JavascriptInterface
    public String writeSurveyInfo(@NonNull String key, @NonNull String value) {
        utils.show_debug_message("write_Survey_Data", "Rozpoczynam, key: " + key + " value: " + value);
        try {
            sql.execSQL("INSERT INTO _Ankieta (" + String.valueOf((char) 34) + "Pytanie" + String.valueOf((char) 34) + "," + String.valueOf((char) 34) + "Odpowiedz" + String.valueOf((char) 34) + ") VALUES (?,?);", new String[]{key, value});
        } catch (Exception e) {
            utils.show_debug_message("write_Survey_Data", "błąd: " + e.getMessage());
            return utils.returnError(e.getMessage());
        }
        utils.show_debug_message("write_Survey_Data", "ok");
        return utils.returnData("");

    }

    //update _Ankieta by id row
    @NonNull
    @JavascriptInterface
    public String updateSurveyInfo(@NonNull int id, @NonNull String key, @NonNull String value) {
        utils.show_debug_message("update_Survey_Data", "Rozpoczynam id = " + id + " key = " + key + " value = " + value);
        try {
            sql.execSQL("UPDATE _Ankieta SET" + String.valueOf((char) 34) + "Pytanie" + String.valueOf((char) 34) + " = ? ," + String.valueOf((char) 34) + "Odpowiedz" + String.valueOf((char) 34) + " = ? WHERE id = " + id + ";", new String[]{key, value});
        } catch (Exception e) {
            utils.show_debug_message("update_Survey_Data", "Błąd " + e.getMessage());
            return utils.returnError(e.getMessage());
        }
        utils.show_debug_message("update_Survey_Data", "OK");
        return utils.returnData("");

    }

    //read from _Ankieta by id
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
            return utils.returnError(e.getMessage());
        }
        utils.show_debug_message("get_Survey_Info_By_Id", "OK");
        return utils.returnData(utils.cursorToString(c));

    }

    // Select * from _Ankieta;
    @NonNull
    @JavascriptInterface
    public String getSurveyInfoAll() {
        utils.show_debug_message("get_Survey_Info_All", "Zaczynam");
        Cursor c = null;
        try {
            c = sql.rawQuery("SELECT * from _Ankieta;", null);
        } catch (Exception e) {
            utils.show_debug_message("get_Survey_Info_All", "błąd: " + e.getMessage());
            return utils.returnError(e.getMessage());
        }
        utils.show_debug_message("get_Survey_Info_All", "OK");
        return utils.returnData(utils.cursorToString(c));

    }

    //Clear and optymalize database
    @NonNull
    @JavascriptInterface
    public String clear() {
        utils.show_debug_message("wyczysc", "Rozpoczynam");
        try {
            sql.execSQL("VACUUM;", null);
        } catch (Exception e) {
            utils.show_debug_message("wyczysc", "Błąd: " + e.getMessage());
            return utils.returnError(e.getMessage());
        }
        utils.show_debug_message("wyczysc", "OK");
        return utils.returnData("");
    }

    //get profile witch history
    @NonNull
    @JavascriptInterface
    public String profileGetData() {
        utils.show_debug_message("profile_Get_Data", "Zaczynam");
        Cursor c = null;
        try {
            c = sql.rawQuery("SELECT * from _profile;", null);
        } catch (Exception e) {
            utils.show_debug_message("profile_Get_Data", "błąd: " + e.getMessage());
            return utils.returnError(e.getMessage());
        }
        utils.show_debug_message("profile_Get_Data", "OK");
        return utils.returnData(utils.cursorToString(c));

    }

    //get lastest profile data
    @NonNull
    @JavascriptInterface
    public String profileGetLastestData() {
        utils.show_debug_message("profile_Get_Lastest_Da", "Zaczynam");
        Cursor c = null;
        try {
            c = sql.rawQuery("SELECT * from _profile where data=?;", new String[]{"-1"});
        } catch (Exception e) {
            utils.show_debug_message("profile_Get_Lastest_Da", "błąd: " + e.getMessage());
            return utils.returnError(e.getMessage());
        }
        utils.show_debug_message("profile_Get_Lastest_Da", "OK");

        return utils.returnData(utils.cursorToString(c));

    }

    //update lastest profile data
    @NonNull
    @JavascriptInterface
    public String profileLastestUpdate(@NonNull String first_name, @NonNull String Waga, @NonNull String Wzrost, @NonNull String drunked_water) {
        utils.show_debug_message("profile_lastest_update", "Zaczynam");
        utils.show_debug_message("profile_lastest_update", "first_name = " + first_name + " Waga = " + Waga + " Wzrost = " + Wzrost + "wypita woda = " + drunked_water);
        Cursor c = null;
        try {
            sql.execSQL("Update _profil SET first_name = ?, waga = ?, wzrost = ?,drunked_water = ? where data=?;", new String[]{first_name, Waga, Wzrost, drunked_water, "-1"});
        } catch (Exception e) {
            utils.show_debug_message("write_survey_data", "błąd: " + e.getMessage());
            return utils.returnError(e.getMessage());
        }
        utils.show_debug_message("write_survey_data", "ok");
        return utils.returnData("");

    }

    //copy lastest profile data to history snap shot
    @NonNull
    @JavascriptInterface
    public String createNewSnapschot() {
        utils.show_debug_message("create_new_snapschot", "Zaczynam");
        Cursor c = null;
        try {
            sql.execSQL("insert into _profil (first_name,waga,wzrost,drunked_water,data) select first_name,waga,wzrost,drunked_water,date() from _profil where data = \"-1\";", null);
        } catch (Exception e) {
            utils.show_debug_message("create_new_snapschot", "błąd: " + e.getMessage());
            return utils.returnError(e.getMessage());
        }
        utils.show_debug_message("create_new_snapschot", "ok");
        return utils.returnData("");

    }

    //get attatchment
    @NonNull
    @JavascriptInterface
    public String getResources(@NonNull int req, @NonNull String row) {
        utils.show_debug_message("get_rces", "Zaczynam");
        String tabela = "";
        switch (req) {
            case 0:
                tabela = "artykoly";
                break;
            case 1:
                tabela = "zywienie";
                break;
            case 2:
                tabela = "gra_terenowa";
                break;
            default:
                return utils.returnError("Unknown request");
        }
        utils.show_debug_message("get_resources", "tabela " + tabela + " row " + row);
        Cursor c = null;
        try {
            c = sql.rawQuery("Select * from zalaczniki where tabela = ? and row = ?;", new String[]{tabela, row});
        } catch (Exception e) {
            utils.show_debug_message("get_resources", "błąd: " + e.getMessage());
            return utils.returnError(e.getMessage());
        }
        if (c.getCount() == 0) {
            return utils.returnError("Resource not found");
        } else {
            utils.show_debug_message("get_resources", "ok");
            return utils.returnData(utils.cursorToString(c));
        }

    }

    //show error page, prepair to send notice to developer and stop application
    @JavascriptInterface
    public void throwNewException(@NonNull String message, @NonNull String additionalmessage) {
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

    //save data to log and logcat
    @JavascriptInterface
    public void log(@NonNull String tag, @NonNull String message) {
        utils.show_debug_message(tag, message);
        return;
    }

    //open web page in new window
    @JavascriptInterface
    public boolean openWebpage(@NonNull String link) {
        utils.show_debug_message("openWebpage", "zaczynam");
        utils.show_debug_message("openWebpage", "link");
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(link));
            mContext.startActivity(i);
        } catch (Exception e) {
            utils.show_debug_message("openWebpage", e.getMessage());
            return false;
        }
        utils.show_debug_message("openWebpage", "kończe");
        return true;
    }

    //clear _Ankieta table
    @JavascriptInterface
    public boolean clearSurveyInfo() {
        utils.show_debug_message("clearSurveyInfo", "zaczynam");
        try {
            sql.execSQL("DELETE FROM _Ankieta;");
        } catch (Exception e) {
            utils.show_debug_message("clearSurveyInfo", e.getMessage());
            return false;
        }
        utils.show_debug_message("clearSurveyInfo", "kończe");
        return true;
    }
}