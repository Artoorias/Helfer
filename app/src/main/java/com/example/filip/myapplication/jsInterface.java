package com.example.filip.myapplication;

import android.support.annotation.NonNull;
import android.webkit.JavascriptInterface;

interface jsInterface {
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
    public String getData(@NonNull int req);

    @JavascriptInterface
    public String getData(@NonNull int req, @NonNull String option_1);

    @JavascriptInterface
    public String getData(@NonNull int req, @NonNull String option_1, @NonNull String option_2, @NonNull String option_3);

    /**
     * Add new ro to _Ankieta table
     *
     * @param key   key column value
     * @param value value column value
     * @return String Json operation status (witch error)
     */
    @JavascriptInterface
    public String writeSurveyData(@NonNull String key, @NonNull String value);

    /**
     * Update _Ankieta table
     *
     * @param id    identyfier of database row
     * @param key   key column value
     * @param value value column value
     * @return String Json operation status (witch error)
     */
    @JavascriptInterface
    public String updateSurveyData(@NonNull Integer id, @NonNull String key, @NonNull String value);

    /**
     * Get row form table _Ankieta by id
     *
     * @param id Id value
     * @return String {status: Json operation status, (error:error mesage), (data:database return converted to json)}
     */
    @JavascriptInterface
    public String getSurveyInfoById(@NonNull Integer id);

    /**
     * Get all rows form table _Ankieta by id
     *
     * @return String {status: Json operation status, (error:error mesage), (data:database return converted to json)}
     */
    @JavascriptInterface
    public String getSurveyInfoAll();

    /**
     * Clear and optymalize database
     *
     * @return String Json operation status (witch error)
     */
    @JavascriptInterface
    public String clear();

    /**
     * Get all Profile data
     *
     * @return String Json operation status (witch error)
     */
    @JavascriptInterface
    public String profileGetData();

    /**
     * get lastest profile data
     *
     * @return String Json operation status (witch error)
     */
    @JavascriptInterface
    public String profileGetLastestData();

    /**
     * update lastest profile data
     *
     * @param first_name String
     * @param first_name waga
     * @param first_name wzrost
     * @param first_name drunked_water
     * @return String Json operation status (witch error)
     */
    @JavascriptInterface
    public String profileLastestUpdate(@NonNull String first_name, @NonNull String Waga, @NonNull String Wzrost, @NonNull String drunked_water);

    /**
     * create snapschot form lastest profile state
     *
     * @return String Json operation status (witch error)
     */
    @JavascriptInterface
    public String createNewSnapschot();

    /**
     * get resource file
     *
     * @param tabela String name of table
     * @param row    String witch id of row in tabela
     * @return String Json operation status (witch error)
     */
    @JavascriptInterface
    public String getResources(String tabela, String row);

    /**
     * send exception to Helfer Error handler
     *
     * @param message           String message about error
     * @param additionalmessage String user friendly message about error
     * @return nothing
     */
    @JavascriptInterface
    public void throwNewException(String message, String additionalmessage);

    /**
     * Log
     *
     * @param tag     String tag of message
     * @param message String message
     * @return nothing
     */
    @JavascriptInterface
    public void log(String tag, String message);
}
