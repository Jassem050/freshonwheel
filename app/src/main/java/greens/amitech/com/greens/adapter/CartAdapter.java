package greens.amitech.com.greens.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.labters.lottiealertdialoglibrary.ClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import greens.amitech.com.greens.R;
import greens.amitech.com.greens.utils.SessionManager;
import greens.amitech.com.greens.model.added_items;
import greens.amitech.com.greens.model.Item;
import greens.amitech.com.greens.utils.NetworkUtils;
import greens.amitech.com.greens.VolleySingleton;

import static com.android.volley.VolleyLog.TAG;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private static int qty;
    private int min_qty;
    public Context mcontext;
    public List<Item> Items;
    added_items added_items;
    double total = 0;
    private TextView total_amount;
    private SessionManager session_manager;
    private ClickListener clickListener;


    public CartAdapter(Context mcontext, List<Item> Items, TextView total_amnt_txtview, ClickListener clickListener) {

        this.mcontext = mcontext;
        this.Items = Items;
        this.total_amount = total_amnt_txtview;
        this.clickListener = clickListener;
    }

    public void clearItemList(){
        if (Items != null){
            Items.clear();
        }
    }

    @NonNull
    @Override
    public CartAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout, parent, false);


        return new CartAdapter.MyViewHolder(item_view, clickListener);

    }


    @Override
    public void onBindViewHolder(@NonNull final CartAdapter.MyViewHolder holder, final int position) {

        final Item item = Items.get(position);
        holder.item_name.setText(item.getItem_name());
        holder.selected_qty_txtview.setText(String.valueOf(item.getSelected_qty()));
        holder.item_weight.setText(String.valueOf(item.getItem_weight()) + item.getNetWeight());
        holder.item_min_weight.setText(item.getItem_minqty() + item.getNetWeight());

        if (item.getWeightCount() == 2) {
                ((MyViewHolder) holder).dropdown.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).weightLayout.setVisibility(View.VISIBLE);
        } else {
            ((MyViewHolder) holder).dropdown.setVisibility(View.GONE);
            ((MyViewHolder) holder).weightLayout.setVisibility(View.GONE);
        }


        Log.d(TAG, "onBindViewHolder: item_weight: " + item.getNetWeight());
        ((MyViewHolder) holder).weightLayout.setEnabled(false);
        if (item.getNetWeight().equals("Kg")) {
            ((MyViewHolder) holder).dropdown.setText("KG");
            Log.d(TAG, "onBindViewHolder: item_qty: " + item.getItem_qty());
            Log.d(TAG, "onBindViewHolder: item_selQty: " + item.getSelected_qty());
            if ((item.getSelected_qty() + item.getItem_minqty()) > item.getItem_qty()){
                ((MyViewHolder) holder).selected_qty_txtview.setText(String.valueOf(item.getSelected_qty()));
                ((MyViewHolder) holder).plus_btn.setColorFilter(mcontext.getResources().getColor(R.color.disabled_color));
                ((MyViewHolder) holder).plus_btn.setEnabled(false);
            }

            if (item.getItem_qty() < item.getSelected_qty()){
                holder.btnLayout.setVisibility(View.GONE);
                holder.removeBtn.setVisibility(View.VISIBLE);
                holder.no_stock.setVisibility(View.VISIBLE);
            } else {
                holder.btnLayout.setVisibility(View.VISIBLE);
                holder.removeBtn.setVisibility(View.GONE);
                holder.no_stock.setVisibility(View.GONE);
            }
        }
        if (item.getNetWeight().equals("Grams")){
            ((MyViewHolder) holder).dropdown.setText("GRAM");
            Log.d(TAG, "onBindViewHolder: jjjj");
            if (((Float.parseFloat(String.valueOf(item.getSelected_qty())) + item.getItem_minqty()) / 1000) > item.getItem_qty()){
                ((MyViewHolder) holder).selected_qty_txtview.setText(String.valueOf(item.getSelected_qty()));
                ((MyViewHolder) holder).plus_btn.setColorFilter(mcontext.getResources().getColor(R.color.disabled_color));
                ((MyViewHolder) holder).plus_btn.setEnabled(false);
            }
            if (item.getItem_qty() < (Float.parseFloat(String.valueOf(item.getSelected_qty())) / 1000)){
                holder.btnLayout.setVisibility(View.GONE);
                holder.removeBtn.setVisibility(View.VISIBLE);
                holder.no_stock.setVisibility(View.VISIBLE);
            } else {
                holder.btnLayout.setVisibility(View.VISIBLE);
                holder.removeBtn.setVisibility(View.GONE);
                holder.no_stock.setVisibility(View.GONE);
            }
        }



        if (item.getSelected_qty() > 0) {
            holder.minus_btn.setColorFilter(mcontext.getResources().getColor(R.color.enabled_color));


        }

        holder.item_id.setText(item.getItem_id());

        holder.item_price.setText(String.valueOf(item.getItem_price()));


        if (item.getSelected_qty() == 0) {
            holder.minus_btn.setColorFilter(mcontext.getResources().getColor(R.color.disabled_color));
            holder.minus_btn.setEnabled(false);
        }

        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                less_item_from_DB_remove(item.getItem_id(), session_manager.getUserId(), item.getSelected_qty(),
                        item.getItem_price(), item.getItemDetailId(), holder.item_progress);
                session_manager.setTotalAmount(session_manager.getTotalAmount() - (item.getSelected_qty() * item.getItem_price()));
                clickListener.onRemoveButtonClick(holder.getAdapterPosition());
            }
        });

        View.OnClickListener plusBtnCickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.minus_btn.setEnabled(true);
                holder.item_progress.setVisibility(View.VISIBLE);

                holder.minus_btn.setColorFilter(mcontext.getResources().getColor(R.color.enabled_color));

                double pprice = item.getItem_price();

                total = qty * pprice;

                qty = item.getSelected_qty();
                min_qty = item.getItem_minqty();
                qty = qty + min_qty;

                if (item.getNetWeight().equals("Grams")){

                    float itemStock = item.getItem_qty();
                    float selectQty = (Float.parseFloat(String.valueOf(qty)) / 1000);
                    Log.d(TAG, "onClick: item_weight: Gram" + itemStock);
                    Log.d(TAG, "onClick: item_weight: selectQty: " + selectQty);
                    if (itemStock == selectQty) {
                        item.setSelected_qty(qty);
                        ((MyViewHolder) holder).selected_qty_txtview.setText(String.valueOf(qty));
                        ((MyViewHolder) holder).plus_btn.setColorFilter(mcontext.getResources().getColor(R.color.disabled_color));
                        ((MyViewHolder) holder).plus_btn.setEnabled(false);
                        ((MyViewHolder) holder).plus_btn.setOnClickListener(null);
                        ((MyViewHolder) holder).minus_btn.setEnabled(true);
                    } else if (itemStock < selectQty) {
                        ((MyViewHolder) holder).plus_btn.setColorFilter(mcontext.getResources().getColor(R.color.disabled_color));
                        ((MyViewHolder) holder).plus_btn.setEnabled(false);
                        ((MyViewHolder) holder).minus_btn.setEnabled(false);
                    } else {
                        item.setSelected_qty(qty);
                        ((MyViewHolder) holder).selected_qty_txtview.setText(String.valueOf(qty));
                    }

                    if (((Float.parseFloat(String.valueOf(qty)) + item.getItem_minqty()) / 1000) > item.getItem_qty()){
                        ((MyViewHolder) holder).selected_qty_txtview.setText(String.valueOf(qty));
                        ((MyViewHolder) holder).plus_btn.setColorFilter(mcontext.getResources().getColor(R.color.disabled_color));
                        ((MyViewHolder) holder).plus_btn.setEnabled(false);
                        ((MyViewHolder) holder).plus_btn.setOnClickListener(null);
                    }
                } else if (item.getNetWeight().equals("Kg") || item.getNetWeight().equals("Piece")){
                    if (item.getItem_qty() == qty) {
                        item.setSelected_qty(qty);
                        holder.selected_qty_txtview.setText(String.valueOf(qty));
                        holder.plus_btn.setColorFilter(mcontext.getResources().getColor(R.color.disabled_color));
                        holder.plus_btn.setEnabled(false);
                        ((MyViewHolder) holder).plus_btn.setOnClickListener(null);
                    } else if (item.getItem_qty() < qty) {
                        holder.plus_btn.setColorFilter(mcontext.getResources().getColor(R.color.disabled_color));
                        holder.plus_btn.setEnabled(false);
                    } else {
                        item.setSelected_qty(qty);
                        holder.selected_qty_txtview.setText(String.valueOf(qty));
                    }

                    if ((qty + item.getItem_minqty()) > item.getItem_qty()){
                        ((MyViewHolder) holder).selected_qty_txtview.setText(String.valueOf(qty));
                        ((MyViewHolder) holder).plus_btn.setColorFilter(mcontext.getResources().getColor(R.color.disabled_color));
                        ((MyViewHolder) holder).plus_btn.setEnabled(false);
                        ((MyViewHolder) holder).plus_btn.setOnClickListener(null);
                    }
                }

                holder.selected_qty_txtview.setText(String.valueOf(qty));
                added_items = new added_items(item.getItem_id(), qty, item.getItem_price(), total);

                add_items_to_DB(item.getItem_id(), session_manager.getUserId(), min_qty, item.getItem_price(),
                        holder.item_progress, holder.plus_btn, item.getItemDetailId());


            }
        };

        holder.plus_btn.setOnClickListener(plusBtnCickListener);

        holder.minus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                holder.item_progress.setVisibility(View.VISIBLE);


                qty = item.getSelected_qty();
                min_qty = item.getItem_minqty();
                double pprice = item.getItem_price();
                total = qty * pprice;


                qty = qty - min_qty;
                if (item.getNetWeight().equals("Kg")) {
                    if (item.getItem_qty() > qty) {
                        holder.plus_btn.setEnabled(true);
                        holder.plus_btn.setColorFilter(mcontext.getResources().getColor(R.color.plus_color));
                    }
                } else if (item.getNetWeight().equals("Grams")){
                    if (item.getItem_qty() > (Float.parseFloat(String.valueOf(qty)) / 1000)){
                        holder.plus_btn.setEnabled(true);
                        holder.plus_btn.setColorFilter(mcontext.getResources().getColor(R.color.plus_color));
                    }
                }
                item.setSelected_qty(qty);

                Log.d("qqq", String.valueOf(item.getSelected_qty()));

                if (qty < min_qty) {


                    holder.selected_qty_txtview.setText("0");
                    holder.minus_btn.setEnabled(false);
                    holder.minus_btn.setColorFilter(mcontext.getResources().getColor(R.color.disabled_color));
                    Log.d("lastqty", String.valueOf(item.getSelected_qty()));
                    less_item_from_DB(item.getItem_id(), session_manager.getUserId(), min_qty, item.getItem_price(), item.getItemDetailId(),
                            holder.item_progress, holder.minus_btn);
                    clickListener.onMinusButtonClick(holder.getAdapterPosition());
                } else {

                    holder.minus_btn.setEnabled(true);
                    holder.minus_btn.setColorFilter(mcontext.getResources().getColor(R.color.enabled_color));


                    holder.plus_btn.setOnClickListener(plusBtnCickListener);

                    item.setSelected_qty(qty);

                    holder.selected_qty_txtview.setText(String.valueOf(qty));

                    added_items = new added_items(item.getItem_id(), qty, item.getItem_price(), total);

                    less_item_from_DB(item.getItem_id(), session_manager.getUserId(), min_qty, item.getItem_price(), item.getItemDetailId(),
                            holder.item_progress, holder.minus_btn);


                    Log.d("sekected_i_id", item.getItem_id());
                    Log.d("i_price", String.valueOf(item.getItem_price()));

                }


            }
        });


        Glide.with(mcontext).load(NetworkUtils.IMAGE_URL + NetworkUtils.ITEM_IMAGE + item.getItem_image()).into(holder.item_image);
        clickListener.onCheckStock(holder.getAdapterPosition(), holder.plus_btn);
    }


    private void less_item_from_DB(final String item_id, final String userid, final int qty, final float item_price, int itemDetailId,
                                   final ProgressBar item_progress, final ImageView minusImageView) {

        minusImageView.setEnabled(false);
        StringRequest delete_item_from_DB = new StringRequest(Request.Method.POST, NetworkUtils.BASE_URL + NetworkUtils.MINUS_CART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("items_del_response", response);

                        item_progress.setVisibility(View.INVISIBLE);
                        minusImageView.setEnabled(true);

                        if (response.equals("No itemList") || response.contains("No itemList")){
                            Log.d(TAG, "onResponse: no items");
                        } else {

//
                            try {

                                JSONObject data = new JSONObject(response);

                                String total = data.getString("total_amt");
                                if (total.contains("null")) {
                                    session_manager.setTotalAmount(0.0F);
                                    total_amount.setText("0");
                                } else {
                                    session_manager.setTotalAmount(Float.parseFloat(total));
                                    total_amount.setText(total);
                                }


                                Log.d(TAG, "onResponse:" + total);


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
                params.put("item_id", item_id);
                params.put("iqty", String.valueOf(qty));
                params.put("item_detail_id", String.valueOf(itemDetailId));
                params.put("itemprice", String.valueOf(item_price));
                return params;
            }
        };

        delete_item_from_DB.setRetryPolicy(new DefaultRetryPolicy(
                6 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mcontext).addToRequestQueue(delete_item_from_DB);


    }

    private void less_item_from_DB_remove(final String item_id, final String userid, final int qty, final float item_price, int itemDetailId,
                                   final ProgressBar item_progress) {

        StringRequest delete_item_from_DB = new StringRequest(Request.Method.POST, NetworkUtils.BASE_URL + NetworkUtils.MINUS_CART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("items_del_response", response);

                        item_progress.setVisibility(View.INVISIBLE);

                        if (response.equals("No itemList") || response.contains("No itemList")){
                            Log.d(TAG, "onResponse: no items");
                        } else {

//
                            try {

                                JSONObject data = new JSONObject(response);

                                String total = data.getString("total_amt");
                                if (total.contains("null")) {
                                    session_manager.setTotalAmount(0.0F);
                                    total_amount.setText("0");
                                } else {
                                    session_manager.setTotalAmount(Float.parseFloat(total));
                                    total_amount.setText(total);
                                }

                                Log.d(TAG, "onResponse:" + total);


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
                params.put("item_id", item_id);
                params.put("iqty", String.valueOf(qty));
                params.put("item_detail_id", String.valueOf(itemDetailId));
                params.put("itemprice", String.valueOf(item_price));
                return params;
            }
        };

        delete_item_from_DB.setRetryPolicy(new DefaultRetryPolicy(
                6 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mcontext).addToRequestQueue(delete_item_from_DB);


    }


    private void add_items_to_DB(final String item_id, final String userid, final int qty, final float price,
                                 final ProgressBar item_progress, final ImageView plusImageView, int itemDetailId) {
        plusImageView.setEnabled(false);
        Log.d(TAG, "add_items_to_DB: add_aty: " + qty);
        StringRequest add_items_to_DB = new StringRequest(Request.Method.POST, NetworkUtils.BASE_URL + NetworkUtils.ADDTOCART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("items_response", response);

                        item_progress.setVisibility(View.INVISIBLE);
                        plusImageView.setEnabled(true);

//
                        try {

                            JSONObject data = new JSONObject(response);

                            String total = data.getString("total_amt");

                            if (total.contains("null")) {
                                session_manager.setTotalAmount(0.0F);
                                total_amount.setText("0");
                            } else {
                                session_manager.setTotalAmount(Float.parseFloat(total));
                                total_amount.setText(total);
                            }


                            Log.d(TAG, "onResponse:" + total);


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
                params.put("item_id", item_id);
                params.put("iqty", String.valueOf(qty));
                params.put("item_detail_id", String.valueOf(itemDetailId));
                params.put("itemprice", String.valueOf(price));
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
    public int getItemCount() {
        return Items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface OnItemClickListener {
        void onItemClick(String amount);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView item_name, item_price, item_qty, item_weight, item_id;
        public ImageView item_image;
        public ImageView plus_btn, minus_btn;
        public TextView selected_qty_txtview;
        public ProgressBar item_progress;
        private TextView item_min_weight;
        private LinearLayout btnLayout;
        private TextView no_stock;
        private MaterialButton removeBtn;
        private AutoCompleteTextView dropdown;
        String[] COUNTRIES = new String[] {"KG", "GRAM"};
        public TextInputLayout weightLayout;


        public MyViewHolder(@NonNull View itemView, final ClickListener clickListener) {
            super(itemView);

            session_manager = new SessionManager(mcontext);
            item_name = itemView.findViewById(R.id.item_name);
            item_image = itemView.findViewById(R.id.item_image);
            plus_btn = itemView.findViewById(R.id.plus_btn);
            minus_btn = itemView.findViewById(R.id.minus_btn);
            item_min_weight = itemView.findViewById(R.id.item_min_weight);
            btnLayout = itemView.findViewById(R.id.btn_layout);
            no_stock = itemView.findViewById(R.id.no_stock);
            removeBtn = itemView.findViewById(R.id.remove_btn);

            item_price = itemView.findViewById(R.id.item_price);

            item_id = itemView.findViewById(R.id.item_id);

            selected_qty_txtview = itemView.findViewById(R.id.item_selected_qty_txtview);

            item_price = itemView.findViewById(R.id.item_price);
//            item_qty=itemView.findViewById(R.id.item_selected_qty);
            item_weight = itemView.findViewById(R.id.item_weight);
            item_progress = itemView.findViewById(R.id.item_progress);
            item_progress.setVisibility(View.INVISIBLE);

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(
                            mcontext,
                            R.layout.dropdown_menu_popup_item,
                            COUNTRIES);
            weightLayout = itemView.findViewById(R.id.weight_layout);
            dropdown = itemView.findViewById(R.id.weight_dropdown);
            dropdown.setAdapter(adapter);
            dropdown.setText(COUNTRIES[0], false);
        }

    }

    public interface ClickListener{
        void onMinusButtonClick(int position);
        void onRemoveButtonClick(int position);
        void onCheckStock(int position, ImageView plusbtn);
    }
}
