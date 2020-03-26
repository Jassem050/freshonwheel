package greens.amitech.com.greens.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    //    shared prefence mode
    static final int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "greens.amitech.com.greens.freshOnwheel";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_LOGGED_IN = "false";
    private static final String USER_ID = "userid";
    private static final String TOTAL_AMOUNT = "total";
    private static final String CART_COUNT = "cartCount";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }


    public void setLogin(boolean isLoggedIn){
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGGED_IN, false);
    }

    public void setUserId(String id){
        editor.putString(USER_ID, id);
        editor.apply();
    }

    public String getUserId(){
        return pref.getString(USER_ID, "userid");
    }

    public void removeAll(){
        editor.clear();
    }

    public void setTotalAmount(Float totalAmount){
        editor.putFloat(TOTAL_AMOUNT, totalAmount);
        editor.apply();
    }

    public Float getTotalAmount(){
        return pref.getFloat(TOTAL_AMOUNT, 0.0F);
    }

    public void setCartCount(int cartCount){
        editor.putInt(CART_COUNT, cartCount);
        editor.apply();
    }

    public int getCartCount(){
        return pref.getInt(CART_COUNT, 0);
    }

}
