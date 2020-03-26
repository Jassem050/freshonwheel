package greens.amitech.com.greens;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.labters.lottiealertdialoglibrary.ClickListener;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;
import com.vivekkaushik.datepicker.DatePickerTimeline;
import com.vivekkaushik.datepicker.OnDateSelectedListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import greens.amitech.com.greens.model.Item;
import greens.amitech.com.greens.utils.NetworkUtils;
import greens.amitech.com.greens.utils.SessionManager;

public class checkout extends AppCompatActivity {

    private static final String TAG = "checkout";

    private DatePickerTimeline datePickerTimeline;
    private Button proceed;

    private String selected_date;

    private NetworkUtils NetworkUtils;

    private TextView user_address, itemTotal, itemOffer, itemDiscount, itemToPay;
    private SessionManager session_manager;
    private List<Date> dateList;
    private static int index = 0;
    private List<Item> itemModelList;
    private JSONObject jsonObject;
    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        NetworkUtils = new NetworkUtils();
        session_manager = new SessionManager(checkout.this);
        initviews();
        init();
        Load_customer_details(session_manager.getUserId());
        user_address = findViewById(R.id.address);

        // cart details
        itemModelList = new ArrayList<>();
        load_cart_details();

        final Date date = new Date();
        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        SimpleDateFormat month = new SimpleDateFormat("MM");
        SimpleDateFormat cur_date = new SimpleDateFormat("dd");

        int y = Integer.valueOf(year.format(date.getTime()));
        Log.d(TAG, "onCreate: year: " + y);
        int m = Integer.valueOf(month.format(date.getTime())) - 1;
        Log.d(TAG, "onCreate: month: " + m);
        int d = Integer.valueOf(cur_date.format(date.getTime()));

        loadBillDetails(session_manager.getUserId());
        datePickerTimeline = findViewById(R.id.datePickerTimeline);
        datePickerTimeline.setInitialDate(y, m, (d + 1));
        datePickerTimeline.setDateTextColor(Color.RED);

        String startDate = y + "-" + (m + 1) + "-" + (d + 5);
        String endDate = (y + 1) + "-" + (m + 1) + "-" + d;
        dateList = getDates(startDate, endDate);

        Date[] dateArray = new Date[dateList.size()];
        Calendar deactivateDate = Calendar.getInstance();

//        for (Date dates: dateList){
//            deactivateDate.setTime(dates);
//            dateArray[index] = deactivateDate.getTime();
//            index++;
//        }

        for (int i = 0; i < dateList.size(); i++){
            Date dates = dateList.get(i);
            deactivateDate.setTime(dates);
            dateArray[i] = deactivateDate.getTime();
        }
        datePickerTimeline.deactivateDates(dateArray);

        datePickerTimeline.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int dayOfWeek) {

                String s_date;
                String s_month;
                int m = month + 1;

                if (month < 10) {
                    s_month = "0" + m;
                } else {
                    s_month = String.valueOf(m);
                }

                if (day < 10) {
                    s_date = "0" + day;
                } else {
                    s_date = String.valueOf(day);
                }


                selected_date = new StringBuilder().append(year).append("-").append(s_month).append("-").append(s_date).toString();

            }

            @Override
            public void onDisabledDateSelected(int year, int month, int day, int dayOfWeek, boolean isDisabled) {
                selected_date = "";
                Toast.makeText(checkout.this, "Place order on available dates", Toast.LENGTH_SHORT).show();
            }
        });

//        Date[] dates = {Calendar.getInstance().getTime()};
//        datePickerTimeline.deactivateDates(dates);

    }

    private static List<Date> getDates(String dateString1, String dateString2)
    {
        ArrayList<Date> dates = new ArrayList<Date>();
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while(!cal1.after(cal2))
        {
            dates.add(cal1.getTime());
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }

    private void init(){
        itemTotal = findViewById(R.id.item_total);
        itemOffer = findViewById(R.id.item_offer);
        itemDiscount = findViewById(R.id.item_discount);
        itemToPay = findViewById(R.id.item_topay);
    }

    private void Load_customer_details(final String userid) {
        StringRequest show_customer_details = new StringRequest(Request.Method.GET,
                NetworkUtils.BASE_URL +
                        NetworkUtils.USER_PROFILE + userid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("check_response", response);


                        Log.d(TAG, "onResponse: error unpaid: ");
                        try {

                            JSONArray data = new JSONArray(response);

                            for (int i = 0; i < data.length(); i++) {

                                JSONObject c = data.getJSONObject(i);

                                String uaddress = c.getString("uaddress");

                                user_address.setText(uaddress);


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {};

        show_customer_details.setRetryPolicy(new DefaultRetryPolicy(
                6 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(checkout.this).addToRequestQueue(show_customer_details);


    }

    private void loadBillDetails(String userId){
        StringRequest show_bill_details = new StringRequest(Request.Method.GET,
                NetworkUtils.BASE_URL +
                        NetworkUtils.BILL_DETAILS + userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("check_response", response);


                        Log.d(TAG, "onResponse: error unpaid: ");
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String item_Total = jsonObject.getString("actual_amt");
                            String item_Offer = jsonObject.getString("offer_price");

                            int iTotal = Integer.valueOf(item_Total);
                            int iOffer = Integer.valueOf(item_Offer);
                            int item_Discount = (iTotal * iOffer) / 100;
                            int iToPay = iTotal - item_Discount;

                            itemTotal.setText(String.format("%s%s", getString(R.string.rupee_symbol),item_Total));
                            itemOffer.setText(String.format("%s%s", item_Offer, getString(R.string.percentage_symbol)));
                            itemDiscount.setText(String.format("-%s%s", getString(R.string.rupee_symbol), String.valueOf(item_Discount)));
                            itemToPay.setText(String.format("%s%s", getString(R.string.rupee_symbol),String.valueOf(iToPay)));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {};

        show_bill_details.setRetryPolicy(new DefaultRetryPolicy(
                6 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(checkout.this).addToRequestQueue(show_bill_details);
    }

    private void initviews() {


        proceed = findViewById(R.id.proceed);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (TextUtils.isEmpty(selected_date)) {
                    Toast.makeText(checkout.this, "Please select delivery date", Toast.LENGTH_SHORT).show();
                } else {

                    LottieAlertDialog.Builder builder = new LottieAlertDialog.Builder(checkout.this, DialogTypes.TYPE_QUESTION, null);
                    builder.setTitle("Are you sure?");
                    builder.setDescription("Are you sure to proceed");
                    builder.setPositiveText("PROCEED");
                    builder.setPositiveTextColor(Color.WHITE);
                    builder.setPositiveListener(new ClickListener() {
                        @Override
                        public void onClick(LottieAlertDialog lottieAlertDialog) {
                            jsonArray = new JSONArray();
                            for (int i = 0; i < itemModelList.size(); i++){
                                try {
                                    jsonObject = new JSONObject();
                                    int item_id = Integer.valueOf(itemModelList.get(i).getItem_id());
                                    int item_qty = itemModelList.get(i).getSelected_qty();
                                    int item_detail_id = itemModelList.get(i).getItemDetailId();
                                    Log.d(TAG, "onClick: jsonArray: item_id: " + item_id);
                                    jsonObject.put("item_id", item_id);
                                    jsonObject.put("item_qty", item_qty);
                                    jsonObject.put("item_detail_id", item_detail_id);
                                    jsonArray.put(jsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            Log.d(TAG, "onClick: ");
                            Log.d(TAG, "onClick: jsonArray: " + jsonArray.toString());
                            proceed_to_Check_out(session_manager.getUserId(), selected_date, jsonArray);
                            lottieAlertDialog.dismiss();
                        }
                    });
                    Log.d(TAG, "onClick: " + builder.getPositiveButtonColor());
                    builder.setNegativeText("CANCEL");
                    builder.setNegativeTextColor(Color.WHITE);
                    builder.setNegativeButtonColor(Color.parseColor("#ffbb00"));
                    builder.setNegativeListener(new ClickListener() {
                        @Override
                        public void onClick(@NotNull LottieAlertDialog lottieAlertDialog) {
                            lottieAlertDialog.dismiss();
                        }
                    });
                    LottieAlertDialog dialog = builder.build();
                    dialog.setCancelable(false);
                    dialog.show();
//                    new SweetAlertDialog(checkout.this, SweetAlertDialog.SUCCESS_TYPE)
//                            .setTitleText("Are you sure?")
//                            .setContentText("are you sure to proced?")
//                            .setConfirmText("Proceed")
//                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                @Override
//                                public void onClick(SweetAlertDialog sDialog) {
//                                    sDialog.dismissWithAnimation();
//                                    proceed_to_Check_out(session_manager.getUserId(), selected_date);
//                                }
//                            })
//                            .show();

                }
            }
        });
    }

    private void proceed_to_Check_out(final String userid, final String selected_date, final JSONArray jsonArray1) {

        final StringRequest check_out_item = new StringRequest(Request.Method.POST, NetworkUtils.BASE_URL + NetworkUtils.CHECK_OUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("check_response", response);
                        if (response.equals("Unpaid") || response.contains("Unpaid")) {
                            LottieAlertDialog.Builder builder = new LottieAlertDialog.Builder(checkout.this, DialogTypes.TYPE_ERROR, null);
                            builder.setTitle("Order cannot be placed");
                            builder.setDescription("Your Last order amount is pending.");
                            builder.setPositiveText("OK");
                            builder.setPositiveTextColor(ContextCompat.getColor(checkout.this, R.color.white));
                            builder.setPositiveButtonColor(ContextCompat.getColor(checkout.this, R.color.colorPrimary));
                            builder.setPositiveListener(new ClickListener() {
                                @Override
                                public void onClick(LottieAlertDialog lottieAlertDialog) {
                                    lottieAlertDialog.dismiss();
                                }
                            });
                            LottieAlertDialog dialog = builder.build();
                            dialog.setCancelable(false);
                            dialog.show();
                        } else {

                            if (response.contains("checkout successfull")) {
                                LottieAlertDialog.Builder builder = new LottieAlertDialog.Builder(checkout.this, DialogTypes.TYPE_SUCCESS, null);
                                builder.setTitle("Order placed");
                                builder.setDescription("Your order has been placed");
                                final LottieAlertDialog dialog = builder.build();
                                dialog.setCancelable(false);
                                dialog.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        session_manager.setTotalAmount(0.0f);
                                        dialog.dismiss();
                                        startActivity(new Intent(checkout.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        Toast.makeText(checkout.this, "your order has been placed", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }, 2000);

                            }
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(checkout.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("uid", userid);
                params.put("ddate", selected_date);
                params.put("data1", jsonArray1.toString());
                return params;
            }

        };

        check_out_item.setRetryPolicy(new DefaultRetryPolicy(
                6 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(checkout.this).addToRequestQueue(check_out_item);
    }

    private void load_cart_details() {

        StringRequest add_items_to_DB = new StringRequest(Request.Method.POST, NetworkUtils.BASE_URL + NetworkUtils.VIEW_CART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Log.d("view_cart_response", response);


                            try {


                                Item a;
                                JSONArray data = new JSONArray(response);
                                jsonArray = new JSONArray();
                                for (int i = 0; i < data.length(); i++) {

                                    JSONObject c = data.getJSONObject(i);

                                    int selected_qty = 0;

                                    String item_id = c.getString("item_id");

                                    String iname = c.getString("iname");
                                    String i_image = c.getString("item_image");
                                    int cart_qty = c.getInt("cart_qty");

                                    Log.d(TAG, "onResponse: cart_qty: " + cart_qty);
                                    Float pprice = Float.valueOf(c.getString("item_price"));


                                    String subtotal = c.getString("sub_total");
                                    Float item_weight = Float.valueOf(c.getString("item_weight"));
                                    int min_qty = c.getInt("qty_min");
                                    int total_stock = c.getInt("item_qty");
                                    int itemDetailId = c.getInt("item_detail_id");
                                    int weightCount = c.getInt("weight_count");
                                    String netWeight = c.getString("netWeight");

//                                String netweight = c.getString("item_weight");
//                                String stock = c.getString("item_qty");

                                    a = new Item(item_id, iname, item_weight, i_image, cart_qty,
                                            pprice,min_qty, total_stock, itemDetailId, weightCount, netWeight);
                                    itemModelList.add(a);

                                }
                                Log.d(TAG, "onResponse: array: " + jsonArray);
                                Log.d(TAG, "onResponse: list size: " + itemModelList.size());
                                Collections.reverse(itemModelList);



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("uid", session_manager.getUserId());
                return params;
            }
        };

        add_items_to_DB.setRetryPolicy(new DefaultRetryPolicy(
                6 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(this).addToRequestQueue(add_items_to_DB);


    }
}
