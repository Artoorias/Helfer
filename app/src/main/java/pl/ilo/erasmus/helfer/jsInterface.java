package pl.ilo.erasmus.helfer;

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
    public String getData(@NonNull int req) throws Exception;

    @JavascriptInterface
    public String getData(@NonNull int req, @NonNull String option_1) throws Exception;

    @JavascriptInterface
    public String getData(@NonNull int req, @NonNull String option_1, @NonNull String option_2, @NonNull String option_3) throws Exception;

    /**
     * Add new ro to _Ankieta table
     *
     * @param key   key column value
     * @param value value column value
     * @return String Json operation status (witch error)
     */
    @JavascriptInterface
    public String writeSurveyInfo(@NonNull String key, @NonNull String value);

    /**
     * Update _Ankieta table
     *
     * @param id    identyfier of database row
     * @param key   key column value
     * @param value value column value
     * @return String Json operation status (witch error)
     */
    @JavascriptInterface
    public String updateSurveyInfo(@NonNull int id, @NonNull String key, @NonNull String value);

    /**
     * Get row form table _Ankieta by id
     *
     * @param id Id value
     * @return String {status: Json operation status, (error:error mesage), (data:database return converted to json)}
     */
    @JavascriptInterface
    public String getSurveyInfoById(@NonNull Integer id) throws Exception;

    /**
     * Get all rows form table _Ankieta by id
     *
     * @return String {status: Json operation status, (error:error mesage), (data:database return converted to json)}
     */
    @JavascriptInterface
    public String getSurveyInfoAll() throws Exception;

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
    public String profileGetData() throws Exception;

    /**
     * get lastest profile data
     *
     * @return String Json operation status (witch error)
     */
    @JavascriptInterface
    public String profileGetLastestData() throws Exception;

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
     * @param req int witch id of request
     *            0 = artykoly
     *            1 = zywienie
     *            2 = gra_terenowa
     * @param row    String witch id of row in tabela
     * @return String Json operation status (witch error)
     */
    @JavascriptInterface
    public String getResources(@NonNull int req,@NonNull String row);

    /**
     * send exception to Helfer Error handler
     *
     * @param message           String message about error
     * @param additionalmessage String user friendly message about error
     * @return nothing
     */
    @JavascriptInterface
    public void throwNewException(@NonNull String message,@NonNull String additionalmessage);

    /**
     * Log
     *
     * @param tag     String tag of message
     * @param message String message
     * @return nothing
     */
    @JavascriptInterface
    public void log(@NonNull String tag, @NonNull String message);

    /**
     * Opens link in new context
     *
     * @param link webpage url
     * @return boolean
     */
    @JavascriptInterface
    public boolean openWebpage(@NonNull String link);

    /**
     * Clears survey info
     *
     * @return boolen with status
     */
    @JavascriptInterface
    public boolean clearSurveyInfo();
}
