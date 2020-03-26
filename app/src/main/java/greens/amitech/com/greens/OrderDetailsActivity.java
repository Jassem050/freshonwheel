package greens.amitech.com.greens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import greens.amitech.com.greens.adapter.MyOrdersDetailAdapter;
import greens.amitech.com.greens.model.MyOrdersDetail;
import greens.amitech.com.greens.utils.NetworkUtils;
import greens.amitech.com.greens.utils.SessionManager;

public class OrderDetailsActivity extends AppCompatActivity {


    private static final String TAG = "OrderDetailsActivity";

    private MyOrdersDetailAdapter myOrdersDetailAdapter;
    private List<MyOrdersDetail> myOrdersDetailList;
    private RecyclerView myOrderDetailsRecycler;
    private SessionManager sessionManager;

    private TextView totalAmntTxtview, discountTextView;
    private String totalAmt, offerPrice;


    String oid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order__details);


        oid=getIntent().getStringExtra("oid");
        sessionManager =new SessionManager(this);

        Log.d(TAG, "onCreate:uid "+ sessionManager.getUserId());
        Log.d(TAG, "onCreate:oid "+oid);

        myOrderDetailsRecycler =findViewById(R.id.ordered_items_recycler);
        totalAmntTxtview =findViewById(R.id.item_total);
        discountTextView = findViewById(R.id.item_discount);

        myOrdersDetailList =new ArrayList<>();
        myOrdersDetailAdapter =new MyOrdersDetailAdapter(OrderDetailsActivity.this, myOrdersDetailList);
        myOrderDetailsRecycler.setLayoutManager(new GridLayoutManager(OrderDetailsActivity.this,1));
        myOrderDetailsRecycler.setAdapter(myOrdersDetailAdapter);

        Load_my_order_details(sessionManager.getUserId(), oid);

    }

    private void Load_my_order_details(String userid, String order_id) {

        StringRequest recent_order_items = new StringRequest(Request.Method.GET,
                NetworkUtils.BASE_URL + NetworkUtils.MY_ORDER_DETAILS+userid+"/"+order_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("load_order_details", response);
                        MyOrdersDetail a;

                        try {

                            JSONArray data = new JSONArray(response);

                            for (int i = 0; i < data.length(); i++) {

                                JSONObject c = data.getJSONObject(i);


                                String item_name=c.getString("iname");
                                String item_qty = c.getString("cart_qty");
                                String subtotal = c.getString("sub_total");
                                totalAmt = c.getString("total_amt");
                                offerPrice = c.getString("offer_amt");


                                a = new MyOrdersDetail(item_name,item_qty,subtotal);
                                myOrdersDetailList.add(a);

                            }

                            myOrderDetailsRecycler.setAdapter(myOrdersDetailAdapter);
                            myOrdersDetailAdapter.notifyDataSetChanged();
                            discountTextView.setText("- \u20B9" + offerPrice);
                            totalAmntTxtview.setText("\u20B9 " + totalAmt);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("volley_error", error.toString());

                    }
                }
        );
        recent_order_items.setRetryPolicy(new DefaultRetryPolicy(5 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(OrderDetailsActivity.this).addToRequestQueue(recent_order_items);
    }
}
