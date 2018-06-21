package pl.ilo.erasmus.helfer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import org.json.JSONArray;
import java.sql.*;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomek on 08.02.2018.
 */

class WebAppInterface implements jsInterface {
    public static Connection sql ;
    Context mContext;

    WebAppInterface(Context c, Connection sqla) {
        mContext = c;
        sql = sqla;
    }

    //getData function for 1-8
    @NonNull
    @JavascriptInterface
    public String getData(@NonNull int req) throws Exception {
        utils.show_debug_message("get_Data", "Rozpoczynam");
        ResultSet c = null;
        PreparedStatement state;
        try {
            utils.show_debug_message("get_Data", "req = " + req);
            switch (req) {
                case 1: //get_all_dish
                    state = sql.prepareStatement("SELECT * from zywienie;");
                    c = state.executeQuery();
                    break;
                case 2: //get_all_sport
                    state = sql.prepareStatement("SELECT * from sport;");
                    c = state.executeQuery();
                    break;
                case 3: //get_all_artykoly
                    state = sql.prepareStatement("SELECT * from artykoly;");
                    c = state.executeQuery();
                    break;
                case 4: //get_all_maps_position
                    state = sql.prepareStatement("SELECT * from gra_terenowa;");
                    c = state.executeQuery();
                    break;
                case 5: //get_all_slogans
                    state = sql.prepareStatement("SELECT * from slogan;");
                    c = state.executeQuery();
                    break;
                case 6: //get_default_filtred_dish
                    state = sql.prepareStatement("select z.* from zywienie z where  ((not exists (select * from _Ankieta where Pytanie=\"12-input-0\"))or(z.Wegetarianizm=\"TAK\")) and ((not exists (select * from _Ankieta where Pytanie=\"12-input-1\"))or(z.Weganizm=\"TAK\"));");
                    c = state.executeQuery();
                    break;
                case 7: //get_default_filtred_artykoly
                    state = sql.prepareStatement("Select * from artykoly where Grupa_wiekowa like \"%brak%\" or Grupa_wiekowa like (Select Odpowiedz from _Ankieta where Pytanie =\"1\");");
                    c = state.executeQuery();
                    break;
                case 8: //get_default_filtred_sport
                    state = sql.prepareStatement("select z.* from zywienie z where  ((not exists (select * from _Ankieta where Pytanie=\"12-input-0\"))or(z.Wegetarianizm=\"TAK\")) and ((not exists (select * from _Ankieta where Pytanie=\"12-input-1\"))or(z.Weganizm=\"TAK\"));");
                    c = state.executeQuery();
                    break;
                default:
                    return utils.returnError("Unknown request");
            }
        } catch (Exception e) {
            utils.show_debug_message("get_Data", e.getMessage());
            return utils.returnError(e.getMessage());
        }
        JSONArray tmp = utils.convertToJSON(c);
        utils.show_debug_message("get_Data", tmp.toString());
        return utils.returnData(tmp.toString());
    }

    //getData for req 10,11
    @NonNull
    @JavascriptInterface
    public String getData(@NonNull int req, @NonNull String option_1) throws Exception {
        utils.show_debug_message("get_Data", "Rozpoczynam");
        ResultSet c = null;
        PreparedStatement state;
        try {
            utils.show_debug_message("get_Data", "req = " + req + " option1 = " + option_1);
            switch (req) {
                case 10: //get_custom_filtred_artukoly
                    state = sql.prepareStatement("SELECT * from artykoly where grupa_wiekowa = ?;");
                    state.setString(1,option_1);
                    c = state.executeQuery();
                    break;
                case 11: //get_custom_filtred_sport
                    state = sql.prepareStatement("SELECT * from sport where przeciwskazania = ?;");
                    state.setString(1,option_1);
                    c = state.executeQuery();
                    break;
                default:
                    return utils.returnError("Unknown request");
            }
        } catch (Exception e) {
            utils.show_debug_message("get_Data", e.getMessage());
            return utils.returnError(e.getMessage());
        }
        JSONArray tmp = utils.convertToJSON(c);
        utils.show_debug_message("get_Data", tmp.toString());
        return utils.returnData(tmp.toString());
    }

    //getData for req 9
    @NonNull
    @JavascriptInterface
    public String getData(@NonNull int req, @NonNull String option_1, @NonNull String option_2, @NonNull String option_3) throws Exception {
        utils.show_debug_message("get_Data", "Rozpoczynam");
        ResultSet c = null;
        PreparedStatement state;
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

                    state = sql.prepareStatement(query);
                    String [] arra = params.toArray(new String[0]);
                    for (int i = 1 ; i<=arra.length;i++){
                        state.setString(i,arra[i]);
                    }
                    c = state.executeQuery();
                    break;
                default:
                    return utils.returnError("Unknown request");
            }
        } catch (Exception e) {
            utils.show_debug_message("get_Data", e.getMessage());
            return utils.returnError(e.getMessage());
        }
        JSONArray tmp = utils.convertToJSON(c);
        utils.show_debug_message("get_Data", tmp.toString());
        return utils.returnData(tmp.toString());
    }

    //write to _Ankieta table
    @NonNull
    @JavascriptInterface
    public String writeSurveyInfo(@NonNull String key, @NonNull String value) {
        utils.show_debug_message("write_Survey_Data", "Rozpoczynam, key: " + key + " value: " + value);
        try {
            PreparedStatement a = sql.prepareStatement("INSERT INTO _Ankieta (" + String.valueOf((char) 34) + "Pytanie" + String.valueOf((char) 34) + "," + String.valueOf((char) 34) + "Odpowiedz" + String.valueOf((char) 34) + ") VALUES (?,?);");
            a.setString(1,key);
            a.setString(2,value);
            a.execute();
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
            PreparedStatement a = sql.prepareStatement("UPDATE _Ankieta SET" + String.valueOf((char) 34) + "Pytanie" + String.valueOf((char) 34) + " = ? ," + String.valueOf((char) 34) + "Odpowiedz" + String.valueOf((char) 34) + " = ? WHERE id = " + id + ";");
            a.setString(1,key);
            a.setString(2,value);
            a.execute();
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
    public String getSurveyInfoById(@NonNull Integer id) throws Exception {
        utils.show_debug_message("get_Survey_Info_By_Id", "Rozpoczynam id = " + id);
        ResultSet c = null;
        PreparedStatement state;
        try {
            state = sql.prepareStatement("SELECT * from _Ankieta where id = " + id.toString() + ";");
            c = state.executeQuery();
        } catch (Exception e) // (Exception e) catch-all:s are bad mmkay.
        {
            utils.show_debug_message("get_Survey_Info_By_Id", "błąd: " + e.getMessage());
            return utils.returnError(e.getMessage());
        }
        utils.show_debug_message("get_Survey_Info_By_Id", "OK");
        return utils.returnData(utils.convertToJSON(c).toString());

    }

    // Select * from _Ankieta;
    @NonNull
    @JavascriptInterface
    public String getSurveyInfoAll() throws Exception {
        utils.show_debug_message("get_Survey_Info_All", "Zaczynam");

        ResultSet c = null;
        PreparedStatement state;
        try {
            state = sql.prepareStatement("SELECT * from _Ankieta;");
            c = state.executeQuery();
        } catch (Exception e) {
            utils.show_debug_message("get_Survey_Info_All", "błąd: " + e.getMessage());
            return utils.returnError(e.getMessage());
        }
        utils.show_debug_message("get_Survey_Info_All", "OK");
        return utils.returnData(utils.convertToJSON(c).toString());

    }

    //Clear and optymalize database
    @NonNull
    @JavascriptInterface
    public String clear() {
        utils.show_debug_message("wyczysc", "Rozpoczynam");
        try {
            PreparedStatement a = sql.prepareStatement("VACUUM;");
            a.execute();
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
    public String profileGetData() throws Exception {
        utils.show_debug_message("profile_Get_Data", "Zaczynam");

        ResultSet c = null;
        PreparedStatement state;
        try {
            state = sql.prepareStatement("SELECT * from _profile;");
            c = state.executeQuery();
        } catch (Exception e) {
            utils.show_debug_message("profile_Get_Data", "błąd: " + e.getMessage());
            return utils.returnError(e.getMessage());
        }
        utils.show_debug_message("profile_Get_Data", "OK");
        return utils.returnData(utils.convertToJSON(c).toString());

    }

    //get lastest profile data
    @NonNull
    @JavascriptInterface
    public String profileGetLastestData() throws Exception {
        utils.show_debug_message("profile_Get_Lastest_Da", "Zaczynam");

        ResultSet c = null;
        PreparedStatement state;
        try {
            state = sql.prepareStatement("SELECT * from _profile where data=?;");
state.setString(1,"-1");
            c = state.executeQuery();
        } catch (Exception e) {
            utils.show_debug_message("profile_Get_Lastest_Da", "błąd: " + e.getMessage());
            return utils.returnError(e.getMessage());
        }
        utils.show_debug_message("profile_Get_Lastest_Da", "OK");

        return utils.returnData(utils.convertToJSON(c).toString());

    }

    //update lastest profile data
    @NonNull
    @JavascriptInterface
    public String profileLastestUpdate(@NonNull String first_name, @NonNull String Waga, @NonNull String Wzrost, @NonNull String drunked_water) {
        utils.show_debug_message("profile_lastest_update", "Zaczynam");
        utils.show_debug_message("profile_lastest_update", "first_name = " + first_name + " Waga = " + Waga + " Wzrost = " + Wzrost + "wypita woda = " + drunked_water);

        ResultSet c = null;
        PreparedStatement state;        try {
            state = sql.prepareStatement("Update _profil SET first_name = ?, waga = ?, wzrost = ?,drunked_water = ? where data=?;");
            state.setString(1,first_name);
            state.setString(2,Waga);
            state.setString(3,Wzrost);
            state.setString(4,drunked_water);
            state.setString(5,"-1");
            c = state.executeQuery();
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

        ResultSet c = null;
        PreparedStatement state;        try {
            state = sql.prepareStatement("insert into _profil (first_name,waga,wzrost,drunked_water,data) select first_name,waga,wzrost,drunked_water,date() from _profil where data = \"-1\";");
            c = state.executeQuery();
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

        ResultSet c = null;
        PreparedStatement state;
        try {
            state = sql.prepareStatement("Select * from zalaczniki where tabela = ? and row = ?;");
            state.setString(1,tabela);
            state.setString(2,row);
            c = state.executeQuery();
        } catch (Exception e) {
            utils.show_debug_message("get_resources", "błąd: " + e.getMessage());
            return utils.returnError(e.getMessage());
        }

        try {
            if (c != null) {
                if (c.getRow() == 0) {
                    return utils.returnError("Resource not found");
                } else {
                    utils.show_debug_message("get_resources", "ok");
                    return utils.returnData(utils.convertToJSON(c).toString());
                }
            } else {
                return utils.returnError("Resource not found");

            }
        }catch (Exception e){
             utils.show_debug_message("get_resources", "błąd: " + e.getMessage());
            return utils.returnError(e.getMessage());
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
            PreparedStatement a = sql.prepareStatement("DELETE FROM _Ankieta;");
            a.execute();
        } catch (Exception e) {
            utils.show_debug_message("clearSurveyInfo", e.getMessage());
            return false;
        }
        utils.show_debug_message("clearSurveyInfo", "kończe");
        return true;
    }
}