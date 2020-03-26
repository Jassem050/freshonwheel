package greens.amitech.com.greens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import greens.amitech.com.greens.adapter.MyOrdersAdapter;
import greens.amitech.com.greens.model.MyOrders;
import greens.amitech.com.greens.utils.NetworkUtils;
import greens.amitech.com.greens.utils.SessionManager;

public class MyOrdersActivity extends AppCompatActivity {


    private List<MyOrders> myOrdersList;
    private MyOrdersAdapter myOrdersAdapter;

    RecyclerView myOrdersRecyclerView;
    private LinearLayout empty_view;

    NetworkUtils NetworkUtils;
    private SessionManager sessionManager;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        NetworkUtils = new NetworkUtils();
        empty_view = findViewById(R.id.orders_main_layout);
        sessionManager = new SessionManager(MyOrdersActivity.this);

        myOrdersRecyclerView = findViewById(R.id.my_order_recycler);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        }


        myOrdersList = new ArrayList<>();
        myOrdersAdapter = new MyOrdersAdapter(MyOrdersActivity.this, myOrdersList);

        myOrdersRecyclerView.setLayoutManager(new GridLayoutManager(MyOrdersActivity.this, 1));

        myOrdersRecyclerView.setAdapter(myOrdersAdapter);


        Load_my_orders(sessionManager.getUserId());


    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void Load_my_orders(final String userid) {

        StringRequest load_recent_orders = new StringRequest(Request.Method.GET, NetworkUtils.BASE_URL + NetworkUtils.MY_RECENT_ORDERS + userid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("items_del_response", response);


                        if (response.equals("[]") || response.equals("")) {

                            View child = getLayoutInflater().inflate(R.layout.empty_order_view, null);
                            Button continue_button = child.findViewById(R.id.empty_button);
                            continue_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    startActivity(new Intent(getApplicationContext(), MainActivity.class)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                                }
                            });

                            empty_view.addView(child);

                        } else {


                            MyOrders a;
//
                            try {

                                JSONArray data = new JSONArray(response);

                                for (int i = 0; i < data.length(); i++) {

                                    JSONObject c = data.getJSONObject(i);


                                    String order_number = c.getString("order_number");
                                    String total_amt = c.getString("total_amt");
                                    String order_date = c.getString("order_date");
                                    String ddate = c.getString("d_date");
                                    String itemTotal = c.getString("actual_amt");
                                    String itemOffer = c.getString("offer_price");
                                    String totalDiscount = c.getString("offer_amt");


                                    a = new MyOrders(order_number, total_amt, order_date, ddate, itemTotal, itemOffer, totalDiscount);
                                    myOrdersList.add(a);

                                }
                                Collections.reverse(myOrdersList);
                                myOrdersRecyclerView.setAdapter(myOrdersAdapter);
                                myOrdersAdapter.notifyDataSetChanged();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        // error
                        Log.d("Error.Response", error.toString());

                        Toast.makeText(MyOrdersActivity.this, "Something went wrong! Try again", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {

        };

        load_recent_orders.setRetryPolicy(new DefaultRetryPolicy(
                6 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(MyOrdersActivity.this).addToRequestQueue(load_recent_orders);


    }
}
