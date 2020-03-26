package greens.amitech.com.greens.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import greens.amitech.com.greens.MainActivity;
import greens.amitech.com.greens.R;
import greens.amitech.com.greens.utils.SessionManager;
import greens.amitech.com.greens.adapter.CartAdapter;
import greens.amitech.com.greens.model.Item;
import greens.amitech.com.greens.utils.NetworkUtils;
import greens.amitech.com.greens.VolleySingleton;

public class CartFragment extends Fragment implements CartAdapter.ClickListener {
    private static final String TAG = "CartFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    NetworkUtils NetworkUtils;
    Context mcontext;
    Button checkout;

    private List<Item> Items;
    private CartAdapter cartAdapter;
    private RecyclerView cart_recycler_view;
    private TextView cart_total;

    private RelativeLayout empty_layout;
    private RelativeLayout btm_layout;
    private SessionManager session_manager;
    private ShimmerFrameLayout mShimmerViewContainer;
    private RelativeLayout bottom_layout1;
    private String removeString;
    private Float cartMinimumAmount;
    private TextView orderMinLayout;


    Float total_amount;


    public CartFragment() {
        //empty cunstructor
    }


    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.cart_fragment, container, false);
        session_manager = new SessionManager(getActivity());
        NetworkUtils = new NetworkUtils();
        mcontext = getContext();
        empty_layout = view.findViewById(R.id.main_layout);
        btm_layout = view.findViewById(R.id.btm_layout);
        bottom_layout1 = view.findViewById(R.id.btm_layout);
        orderMinLayout = view.findViewById(R.id.order_min_layout);

        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmer();

        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Cart");
        }

        cart_total = view.findViewById(R.id.cart_total);

        checkout = view.findViewById(R.id.checkout);


        Items = new ArrayList<>();

        cart_recycler_view = view.findViewById(R.id.cart_item_recycler);
        cartAdapter = new CartAdapter(getActivity(), Items, cart_total, this);
        cart_recycler_view.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        // item_recycler.addItemDecoration(new HomeFragment.GridSpacingItemDecoration(1, dpToPx(0), false));
        cart_recycler_view.setAdapter(cartAdapter);
        bottom_layout1.setVisibility(View.GONE);
        checkout.setVisibility(View.GONE);
        load_cart_details();
        for (int i = 0; i < Items.size() && Items.get(i).getItem_qty() < 5; i++) {
            if (Items.get(i).getItem_qty() < 5) {

                checkout.setVisibility(View.GONE);
            }
        }
        loadCartMinAmount();
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCartMinAmount();
                Double amount = Double.valueOf(cart_total.getText().toString());
                if (amount < cartMinimumAmount) {
                    orderMinLayout.setVisibility(View.VISIBLE);
                    orderMinLayout.setText("Orders below " + getString(R.string.rupee_symbol) + cartMinimumAmount + " cannot be placed");
                    Toast.makeText(mcontext, "Orders below " + getString(R.string.rupee_symbol) + cartMinimumAmount + " cannot be placed", Toast.LENGTH_SHORT).show();
                } else {
                    orderMinLayout.setVisibility(View.GONE);
                    startActivity(new Intent(mcontext, greens.amitech.com.greens.checkout.class));
                }
            }


        });


        return view;
    }

    private void loadCartMinAmount() {
        StringRequest minAmountRequest = new StringRequest(Request.Method.GET, NetworkUtils.BASE_URL + NetworkUtils.CART_MIN_AMOUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String cartMinAmt = jsonObject.getString("minimum_amount");
                            cartMinimumAmount = Float.parseFloat(cartMinAmt);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: cartMin: ", error);
            }
        });

        minAmountRequest.setRetryPolicy(new DefaultRetryPolicy(
                6 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mcontext).addToRequestQueue(minAmountRequest);

    }

    private void load_cart_details() {

        StringRequest add_items_to_DB = new StringRequest(Request.Method.POST, NetworkUtils.BASE_URL + NetworkUtils.VIEW_CART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        bottom_layout1.setVisibility(View.VISIBLE);

                        Log.d("view_cart_response", response);


                        if (response.equals("[]") || response.equals("")) {
                            session_manager.setCartCount(0);
                            if (getActivity() != null) {
                                bottom_layout1.setVisibility(View.GONE);
                                checkout.setVisibility(View.GONE);
                                View child = getLayoutInflater().inflate(R.layout.empty_cart_view, null);
                                Button continue_button = child.findViewById(R.id.empty_button);
                                continue_button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (getActivity() != null) {
//                                            Fragment fragment = new HomeFragment();
//                                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                                            fragmentTransaction.replace(R.id.container, fragment);
////                                    fragmentTransaction.addToBackStack(null);
//                                            fragmentTransaction.commit();
                                            ((MainActivity) getActivity()).navView.setSelectedItemId(R.id.navigation_home);
                                        }

                                    }
                                });

                                empty_layout.addView(child);
                                btm_layout.setVisibility(View.INVISIBLE);
                            }


                        } else {
                            session_manager.setCartCount(1);
                            try {

                                bottom_layout1.setVisibility(View.VISIBLE);
                                checkout.setVisibility(View.VISIBLE);

                                Item a;
                                JSONArray data = new JSONArray(response);

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
                                    total_amount = Float.valueOf(c.getString("total_amt"));
                                    Float item_weight = Float.valueOf(c.getString("item_weight"));
                                    int min_qty = c.getInt("qty_min");
                                    int total_stock = c.getInt("item_qty");
                                    int itemDetailId = c.getInt("item_detail_id");
                                    int weightCount = c.getInt("weight_count");
                                    String netWeight = c.getString("netWeight");


                                    if (cart_qty > 0) {
                                        selected_qty = cart_qty;
                                    } else {
                                        selected_qty = 0;
                                    }
                                    if (netWeight.equals("Kg")) {
                                        if (total_stock < 5 || total_stock < min_qty) {
                                            Toast.makeText(mcontext, "Remove out of order stock", Toast.LENGTH_SHORT).show();
                                            checkout.setEnabled(false);
                                        }
                                    } else if (netWeight.equals("Grams")){
                                        if (total_stock < (Float.parseFloat(String.valueOf(min_qty)) / 1000)){
                                            Toast.makeText(mcontext, "Remove out of order stock", Toast.LENGTH_SHORT).show();
                                            checkout.setEnabled(false);
                                        }
                                    }


//                                String netweight = c.getString("item_weight");
//                                String stock = c.getString("item_qty");

                                    a = new Item(item_id, iname, item_weight, i_image, selected_qty,
                                            pprice, min_qty, total_stock, itemDetailId, weightCount, netWeight);
                                    Items.add(a);

                                    Log.d("ittt", String.valueOf(Items.get(0)));

                                }
                                Collections.reverse(Items);
                                cart_recycler_view.getRecycledViewPool().clear();
                                cart_recycler_view.invalidate();
                                cart_recycler_view.setAdapter(cartAdapter);
                                cartAdapter.notifyDataSetChanged();
                                cart_total.setText(String.valueOf(total_amount));


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

        VolleySingleton.getInstance(mcontext).addToRequestQueue(add_items_to_DB);


    }


    @Override
    public void onMinusButtonClick(int position) {
        final Item Item = Items.get(position);
        Log.d(TAG, "onMinusButtonClick: item count: " + Item.getSelected_qty());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cartAdapter.clearItemList();
                load_cart_details();
                cartAdapter.notifyDataSetChanged();
            }
        }, 200);

    }

    @Override
    public void onRemoveButtonClick(int position) {
        final Item Item = Items.get(position);
        Log.d(TAG, "onRemoveButtonClick: item count: " + Item.getSelected_qty());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cartAdapter.clearItemList();
                checkout.setEnabled(true);
                load_cart_details();
                cartAdapter.notifyDataSetChanged();
            }
        }, 200);
    }

    @Override
    public void onCheckStock(int position, ImageView plusbtn) {
        Item Item = Items.get(position);
        if (Item.getItem_qty() == Item.getSelected_qty()) {
            plusbtn.setColorFilter(mcontext.getResources().getColor(R.color.disabled_color));
            plusbtn.setEnabled(false);
        }
    }
}
