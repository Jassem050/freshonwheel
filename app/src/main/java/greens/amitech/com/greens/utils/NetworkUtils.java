package greens.amitech.com.greens.utils;

import android.content.SharedPreferences;

import java.util.ArrayList;

import greens.amitech.com.greens.model.added_items;

public class NetworkUtils {

//    public static final String BASE_URL = "http://192.168.43.229:8080/api/";
//    public static final String IMAGE_URL = "http://192.168.43.229:8080/";

    public static final String BASE_URL = "http://www.freshonwheel.in/api/";
    public static final String IMAGE_URL = "http://www.freshonwheel.in/";
    public static String SLIDER_IMAGES = "slideimage";

    public static final String SLIDER_IMAGES_FOLDER = "itemimage/";
    public static final String LOAD_ITEMS = "viewitems/";
    public static final String ITEM_IMAGE = "itemimage/";
    public static final String VIEW_CART = "viewcart";
    public static final String REGISTER = "insertuser";
    public static final String LOGIN = "login";
    public static final String MINUS_CART = "delcrt";
    public static final String USER_PROFILE = "userprofile/";
    public static final String CHECK_OUT = "checkout";
    public static final String MY_RECENT_ORDERS="recentorders/";
    public static final String MY_ORDER_DETAILS="recentordersitems/";
    public static final String ADDTOCART = "addcrt";
    public static final String BILL_DETAILS = "billdetails/";
    public static final String CATEGORIES = "category/";
    public static final String CART_MIN_AMOUNT = "cart_min_amount/";
    public static final String GRAM_DETAILS = "gram_details/";
    public static final String KG_DETAILS = "kg_details/";


    public static ArrayList<added_items> added_items = new ArrayList<>();

    public static String userid="user_id";



}
