package greens.amitech.com.greens.adapter;

import android.app.Notification;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import greens.amitech.com.greens.R;
import greens.amitech.com.greens.checkout;
import greens.amitech.com.greens.utils.SessionManager;
import greens.amitech.com.greens.model.Item;
import greens.amitech.com.greens.model.added_items;
import greens.amitech.com.greens.utils.NetworkUtils;
import greens.amitech.com.greens.VolleySingleton;
import greens.amitech.com.greens.view_pager_adapter.MyPagerAdapter;

import static com.android.volley.VolleyLog.TAG;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static int qty;
    private int min_qty;
    public Context mcontext;
    public List<Item> itemList;
    public NetworkUtils NetworkUtils;
    added_items added_items;
    double total = 0;
    private TextView total_amount;
    private SessionManager session_manager;
    private OnItemClickListener onItemClickListener;
    private OnPagerInterface onPagerInterface;
    private List<String> imageList;
    private RelativeLayout bottomLayout;
    private Item itemGramDetails;


    public ItemAdapter(Context mcontext, List<Item> itemList, TextView total_amnt_txtview,
                       OnItemClickListener onItemClickListener, OnPagerInterface onPagerInterface, RelativeLayout bottomLayout) {

        this.mcontext = mcontext;
        this.itemList = itemList;
        this.total_amount = total_amnt_txtview;
        this.onItemClickListener = onItemClickListener;
        this.onPagerInterface = onPagerInterface;
        this.bottomLayout = bottomLayout;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == 0) {
            View item_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_image_layout, parent, false);
            return new PagerViewHolder(item_view, onPagerInterface);
        } else {
            View item_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout, parent, false);
            return new MyViewHolder(item_view, onItemClickListener);
        }

    }

    public void setItemList(List<Item> itemList){
        this.itemList = itemList;
    }

    public void setImageList(List<String> imageList){
        this.imageList = imageList;
    }

    PagerAdapter adapter;
    int currentPage = 0;
    Timer timer;
    //slideing image delay
    final long DELAY_MS = 300;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000;
    //ends
    private void setViewPager(PagerViewHolder pagerViewHolder){
        if (imageList != null && imageList.size() > 0){
            adapter = new MyPagerAdapter(mcontext, imageList);
            pagerViewHolder.viewPager.setAdapter(adapter);

            pagerViewHolder.viewPager.setOffscreenPageLimit(adapter.getCount());
            //A little space between pages
            pagerViewHolder.viewPager.setPageMargin(15);

            pagerViewHolder.viewPager.setClipToPadding(false);

            //If hardware acceleration is enabled, you should also remove
            // clipping on the pager for its children.
            pagerViewHolder.viewPager.setClipChildren(false);
            final Handler handler = new Handler();
            final Runnable Update = new Runnable() {
                public void run() {
                    int NUM_PAGES = adapter.getCount() + 1;
                    if (currentPage == NUM_PAGES - 1) {
                        currentPage = 0;

                    } else if (adapter.getCount() == adapter.getItemPosition(NUM_PAGES)) {
                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)
                                pagerViewHolder.viewPager.getLayoutParams();
                        lp.rightMargin += 0;
                    }

                    pagerViewHolder.viewPager.setCurrentItem(currentPage++, true);

                }
            };

            timer = new Timer(); // This will create a new Thread
            timer.schedule(new TimerTask() { // task to be scheduled

                @Override
                public void run() {
                    handler.post(Update);
                }
            }, DELAY_MS, PERIOD_MS);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        if (position == 0){

            setViewPager((PagerViewHolder) holder);
        } else {
            final Item item = itemList.get(position - 1);
            ((MyViewHolder) holder).item_name.setText(item.getItem_name());
            ((MyViewHolder) holder).selected_qty_txtview.setText(String.valueOf(item.getSelected_qty()));
            ((MyViewHolder) holder).item_weight.setText(String.valueOf(item.getItem_weight()) + item.getNetWeight());
            ((MyViewHolder) holder).item_min_weight.setText(item.getItem_minqty() + item.getNetWeight());

//            Log.d(TAG, "onBindViewHolder: min_qtyyText: " + ((MyViewHolder) holder).selected_qty_txtview.getText().toString());
            if (item.getWeightCount() == 2) {
                ((MyViewHolder) holder).dropdown.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).weightLayout.setVisibility(View.VISIBLE);
            } else {
                ((MyViewHolder) holder).dropdown.setVisibility(View.GONE);
                ((MyViewHolder) holder).weightLayout.setVisibility(View.GONE);
            }

            if (item.getSelected_qty() == 0){
                ((MyViewHolder) holder).weightLayout.setEnabled(true);
                ((MyViewHolder) holder).selected_qty_txtview.setText("0");
            } else {
                ((MyViewHolder) holder).weightLayout.setEnabled(false);
                if (item.getNetWeight().equals("kg")) {
                    ((MyViewHolder) holder).dropdown.setText("KG");
//                    getKgDetails(Integer.valueOf(item.getItem_id()), ((MyViewHolder) holder), item);

                } else if (item.getNetWeight().equals("Grams")){
                    ((MyViewHolder) holder).dropdown.setText("GRAM");
                    getGramDetails(Integer.valueOf(item.getItem_id()), ((MyViewHolder) holder), item);
                    Log.d(TAG, "onBindViewHolder: itemSelcted: " + item.getSelected_qty());
                }
            }
            ((MyViewHolder) holder).dropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        Log.d(TAG, "onItemClick: kg_details");
                        getKgDetails(Integer.valueOf(item.getItem_id()), ((MyViewHolder) holder), item);
                    } else if (position == 1){
                        Log.d(TAG, "onItemClick: gram_details");
                        getGramDetails(Integer.valueOf(item.getItem_id()), ((MyViewHolder) holder), item);
                    }
                }
            });
            if (item.getNetWeight().equals("Grams")){
                Log.d(TAG, "onBindViewHolder: ddddd");
                if (item.getItem_qty() < 1 || item.getItem_qty() < (Float.parseFloat(String.valueOf(item.getItem_minqty())) / 1000)
                        || item.getItem_qty() < (Float.parseFloat(String.valueOf(item.getSelected_qty())) / 1000)) {
                    ((MyViewHolder) holder).btnLayout.setVisibility(View.GONE);
                    ((MyViewHolder) holder).no_stock.setVisibility(View.VISIBLE);
                } else {
                    ((MyViewHolder) holder).btnLayout.setVisibility(View.VISIBLE);
                    ((MyViewHolder) holder).no_stock.setVisibility(View.GONE);
                }

            } else if (item.getNetWeight().equals("Kg") || item.getNetWeight().equals("Piece")) {
                if (item.getItem_qty() < 5 || item.getItem_qty() < item.getItem_minqty() || item.getItem_qty() < item.getSelected_qty()) {
                    ((MyViewHolder) holder).btnLayout.setVisibility(View.GONE);
                    ((MyViewHolder) holder).no_stock.setVisibility(View.VISIBLE);
                } else {
                    ((MyViewHolder) holder).btnLayout.setVisibility(View.VISIBLE);
                    ((MyViewHolder) holder).no_stock.setVisibility(View.GONE);
                }

            }
            if (item.getSelected_qty() > 0) {
                ((MyViewHolder) holder).minus_btn.setColorFilter(mcontext.getResources().getColor(R.color.enabled_color));

            }

            Log.d(TAG, "onBindViewHolder: selection: " + ((MyViewHolder) holder).dropdown.getListSelection());
            ((MyViewHolder) holder).item_id.setText(item.getItem_id());

            ((MyViewHolder) holder).item_price.setText(String.valueOf(item.getItem_price()));


            if (item.getSelected_qty() == 0) {

                ((MyViewHolder) holder).minus_btn.setColorFilter(mcontext.getResources().getColor(R.color.disabled_color));
                ((MyViewHolder) holder).minus_btn.setEnabled(false);


            }
            if ((qty + item.getItem_minqty()) > item.getItem_qty()){
                ((MyViewHolder) holder).selected_qty_txtview.setText(String.valueOf(qty));
                ((MyViewHolder) holder).plus_btn.setColorFilter(mcontext.getResources().getColor(R.color.disabled_color));
                ((MyViewHolder) holder).plus_btn.setEnabled(false);
            }

            View.OnClickListener plusClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MyViewHolder) holder).weightLayout.setEnabled(false);
                    ((MyViewHolder) holder).minus_btn.setEnabled(true);
                    ((MyViewHolder) holder).item_progress.setVisibility(View.VISIBLE);

                    ((MyViewHolder) holder).minus_btn.setColorFilter(mcontext.getResources().getColor(R.color.enabled_color));

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
                            ((MyViewHolder) holder).minus_btn.setEnabled(false);
                            ((MyViewHolder) holder).plus_btn.setOnClickListener(null);
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
                        added_items = new added_items(item.getItem_id(), qty, item.getItem_price(), total);

                        add_items_to_DB(item.getItem_id(), session_manager.getUserId(), min_qty, item.getItem_price(), item.getItemDetailId(),
                                ((MyViewHolder) holder).item_progress, ((MyViewHolder) holder).plus_btn, ((MyViewHolder) holder).minus_btn);
                    } else if (item.getNetWeight().equals("Kg") || item.getNetWeight().equals("Piece")){

                        Log.d(TAG, "onClick: item_weight: kg: " + item.getItem_qty());
                        if (item.getItem_qty() == qty) {
                            item.setSelected_qty(qty);
                            ((MyViewHolder) holder).selected_qty_txtview.setText(String.valueOf(qty));
                            ((MyViewHolder) holder).plus_btn.setColorFilter(mcontext.getResources().getColor(R.color.disabled_color));
                            ((MyViewHolder) holder).plus_btn.setEnabled(false);
                            ((MyViewHolder) holder).plus_btn.setOnClickListener(null);
                            ((MyViewHolder) holder).minus_btn.setEnabled(true);
                        } else if (item.getItem_qty() < qty) {
                            ((MyViewHolder) holder).plus_btn.setColorFilter(mcontext.getResources().getColor(R.color.disabled_color));
                            ((MyViewHolder) holder).plus_btn.setEnabled(false);
                            ((MyViewHolder) holder).minus_btn.setEnabled(false);
                        } else {
                            item.setSelected_qty(qty);
                            ((MyViewHolder) holder).selected_qty_txtview.setText(String.valueOf(qty));
                        }

                        if ((qty + item.getItem_minqty()) > item.getItem_qty()){
                            ((MyViewHolder) holder).selected_qty_txtview.setText(String.valueOf(qty));
                            ((MyViewHolder) holder).plus_btn.setColorFilter(mcontext.getResources().getColor(R.color.disabled_color));
                            ((MyViewHolder) holder).plus_btn.setEnabled(false);
                            ((MyViewHolder) holder).plus_btn.setOnClickListener(null);
                        }

                        added_items = new added_items(item.getItem_id(), qty, item.getItem_price(), total);

                        add_items_to_DB(item.getItem_id(), session_manager.getUserId(), min_qty, item.getItem_price(), item.getItemDetailId(),
                                ((MyViewHolder) holder).item_progress, ((MyViewHolder) holder).plus_btn, ((MyViewHolder) holder).minus_btn);
                    }




                }
            };
            ((MyViewHolder) holder).plus_btn.setOnClickListener(plusClickListener);

            ((MyViewHolder) holder).minus_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    ((MyViewHolder) holder).item_progress.setVisibility(View.VISIBLE);

                    ((MyViewHolder) holder).plus_btn.setOnClickListener(plusClickListener);
                    qty = item.getSelected_qty();
                    min_qty = item.getItem_minqty();
                    double pprice = item.getItem_price();
                    total = qty * pprice;

                    qty = qty - min_qty;

                    if (item.getItem_qty() > qty) {
                        ((MyViewHolder) holder).plus_btn.setEnabled(true);
                        ((MyViewHolder) holder).plus_btn.setColorFilter(mcontext.getResources().getColor(R.color.plus_color));
                    }
                    item.setSelected_qty(qty);

                    Log.d("qqq", String.valueOf(item.getSelected_qty()));

                    if (item.getSelected_qty() < 1) {


                        ((MyViewHolder) holder).selected_qty_txtview.setText("0");


                        ((MyViewHolder) holder).minus_btn.setEnabled(false);
                        ((MyViewHolder) holder).minus_btn.setColorFilter(mcontext.getResources().getColor(R.color.disabled_color));
                        Log.d("lastqty", String.valueOf(item.getSelected_qty()));

                        less_item_from_DB(item.getItem_id(), session_manager.getUserId(), min_qty,
                                item.getItem_price(), item.getItemDetailId(), ((MyViewHolder) holder).item_progress,
                                ((MyViewHolder) holder).minus_btn);


                        ((MyViewHolder) holder).weightLayout.setEnabled(true);

                    } else {

                        ((MyViewHolder) holder).minus_btn.setEnabled(true);
                        ((MyViewHolder) holder).minus_btn.setColorFilter(mcontext.getResources().getColor(R.color.enabled_color));
                        ((MyViewHolder) holder).plus_btn.setEnabled(true);
                        ((MyViewHolder) holder).plus_btn.setColorFilter(mcontext.getResources().getColor(R.color.plus_color));
                        ((MyViewHolder) holder).plus_btn.setOnClickListener(plusClickListener);

                        item.setSelected_qty(qty);

                        ((MyViewHolder) holder).selected_qty_txtview.setText(String.valueOf(qty));

                        added_items = new added_items(item.getItem_id(), qty, item.getItem_price(), total);

                        less_item_from_DB(item.getItem_id(), session_manager.getUserId(), min_qty,
                                item.getItem_price(), item.getItemDetailId(),((MyViewHolder) holder).item_progress,
                                ((MyViewHolder) holder).minus_btn);


                        Log.d("sekected_i_id", item.getItem_id());
                        Log.d("i_price", String.valueOf(item.getItem_price()));

                    }


                }
            });


            Glide.with(mcontext).load(NetworkUtils.IMAGE_URL + NetworkUtils.ITEM_IMAGE + item.getItem_image())
                    .into(((MyViewHolder) holder).item_image);
            Log.d(TAG, "onBindViewHolder: min_qtyy: " + item.getSelected_qty());
            ((MyViewHolder) holder).selected_qty_txtview.setText(String.valueOf(item.getSelected_qty()));
            if (item.getSelected_qty() == 0){
                ((MyViewHolder) holder).plus_btn.setColorFilter(mcontext.getResources().getColor(R.color.plus_color));
                ((MyViewHolder) holder).plus_btn.setEnabled(true);
                ((MyViewHolder) holder).plus_btn.setOnClickListener(plusClickListener);
                ((MyViewHolder) holder).minus_btn.setEnabled(false);
            } else {
                ((MyViewHolder) holder).plus_btn.setColorFilter(mcontext.getResources().getColor(R.color.plus_color));
                ((MyViewHolder) holder).plus_btn.setEnabled(true);
                ((MyViewHolder) holder).plus_btn.setOnClickListener(plusClickListener);
                ((MyViewHolder) holder).minus_btn.setColorFilter(mcontext.getResources().getColor(R.color.red_btn_bg_pressed_color));
                ((MyViewHolder) holder).minus_btn.setEnabled(true);
                if (item.getNetWeight().equals("Grams")){
                    if (((Float.parseFloat(String.valueOf(item.getSelected_qty())) + item.getItem_minqty()) / 1000) > item.getItem_qty()){
                        ((MyViewHolder) holder).selected_qty_txtview.setText(String.valueOf(item.getSelected_qty()));
                        ((MyViewHolder) holder).plus_btn.setColorFilter(mcontext.getResources().getColor(R.color.disabled_color));
                        ((MyViewHolder) holder).plus_btn.setEnabled(false);
                        ((MyViewHolder) holder).plus_btn.setOnClickListener(null);
                        Log.d(TAG, "onBindViewHolder: plus");
                    }
                } else if (item.getNetWeight().equals("Kg") || item.getNetWeight().equals("Piece")){
                    if ((item.getSelected_qty() + item.getItem_minqty()) > item.getItem_qty()){
                        ((MyViewHolder) holder).selected_qty_txtview.setText(String.valueOf(item.getSelected_qty()));
                        ((MyViewHolder) holder).plus_btn.setColorFilter(mcontext.getResources().getColor(R.color.disabled_color));
                        ((MyViewHolder) holder).plus_btn.setEnabled(false);
                        ((MyViewHolder) holder).plus_btn.setOnClickListener(null);
                    }
                }
            }
            Log.d(TAG, "onBindViewHolder: min_qtyyText: " + ((MyViewHolder) holder).selected_qty_txtview.getText().toString());
            onItemClickListener.onCheckStock(((MyViewHolder) holder).getAdapterPosition(), ((MyViewHolder) holder).plus_btn);
        }
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

//
                        try {

                            JSONObject data = new JSONObject(response);

                            String total = data.getString("total_amt");
                            if (total.contains("null")) {
                                session_manager.setTotalAmount(0.0F);
                                total_amount.setText("0");
                                bottomLayout.setVisibility(View.GONE);
                            } else {
                                session_manager.setTotalAmount(Float.parseFloat(total));
                                total_amount.setText(total);
                                bottomLayout.setVisibility(View.VISIBLE);
                            }

                            if (session_manager.getTotalAmount() == 0){
                                bottomLayout.setVisibility(View.GONE);
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


    private void add_items_to_DB(final String item_id, final String userid, final int qty, final float price, int itemDetailId,
                                 final ProgressBar item_progress, final ImageView plusImageView, final ImageView minusImageView) {

        plusImageView.setEnabled(false);
        StringRequest add_items_to_DB = new StringRequest(Request.Method.POST, NetworkUtils.BASE_URL + NetworkUtils.ADDTOCART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("items_response", response);

                        item_progress.setVisibility(View.INVISIBLE);
                        plusImageView.setEnabled(true);
                        minusImageView.setEnabled(true);

//
                        try {

                            if (response.equals("no stock") || response.contains("no stock")){

                            } else {
                                JSONObject data = new JSONObject(response);
                                String total = data.getString("total_amt");

                                if (total.contains("null")) {
                                    total_amount.setText("0");
                                    session_manager.setTotalAmount(0.0F);
                                    bottomLayout.setVisibility(View.GONE);
                                } else {
                                    total_amount.setText(total);
                                    session_manager.setTotalAmount(Float.parseFloat(total));
                                    bottomLayout.setVisibility(View.VISIBLE);
                                }


                                Log.d(TAG, "onResponse:" + total);

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
        return itemList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return 0;
        } else {
            return position;
        }
    }

    public interface OnItemClickListener {
        void onCheckStock(int position, ImageView plusbtn);
    }

    public class PagerViewHolder extends RecyclerView.ViewHolder{
        private ViewPager viewPager;
        public PagerViewHolder(@NonNull View itemView, OnPagerInterface onPagerInterface) {
            super(itemView);
            viewPager = itemView.findViewById(R.id.view_pager);
            onPagerInterface.onSetPager(viewPager);
        }
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
        private AutoCompleteTextView dropdown;
        String[] COUNTRIES = new String[] {"KG", "GRAM"};
        public TextInputLayout weightLayout;


        public MyViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            NetworkUtils = new NetworkUtils();
            session_manager = new SessionManager(mcontext);
            item_name = itemView.findViewById(R.id.item_name);
            item_image = itemView.findViewById(R.id.item_image);
            plus_btn = itemView.findViewById(R.id.plus_btn);
            minus_btn = itemView.findViewById(R.id.minus_btn);
            item_min_weight = itemView.findViewById(R.id.item_min_weight);
            btnLayout = itemView.findViewById(R.id.btn_layout);
            no_stock = itemView.findViewById(R.id.no_stock);

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

    public void getKgDetails(int item_id, MyViewHolder myViewHolder, Item item){
        final int[] selected_qty = new int[1];
        final Float[] item_weight = new Float[1];
        final String[] netWeight = new String[1];
        final int[] min_qty = new int[1];
        final Float[] pprice = new Float[1];
        StringRequest stringRequest = new StringRequest(Request.Method.GET, greens.amitech.com.greens.utils.NetworkUtils.BASE_URL +
                greens.amitech.com.greens.utils.NetworkUtils.KG_DETAILS + session_manager.getUserId() + "/" + item_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: kg_details: " + response);
                        if (!response.contains("[]") && !response.equals("")){
                            try {
                                JSONObject c = new JSONObject(response);

                                String item_id = c.getString("item_id");
                                String pname = c.getString("item_name");
                                pprice[0] = Float.valueOf(c.getString("item_price"));
                                String pimage = c.getString("item_image");
                                item_weight[0] = Float.valueOf(c.getString("item_weight"));
                                min_qty[0] = c.getInt("item_minqty");
                                int total_stock = c.getInt("item_qty");
                                int itemDetailId = c.getInt("item_detail_id");
                                int weightCount = 2;
                                netWeight[0] = c.getString("item_netWeight");
                                Log.d(TAG, "onResponse: netWeight: " + netWeight[0]);
                                int cart_qty = c.getInt("cart_qty");

                                selected_qty[0] = cart_qty;
                                item.setItem_minqty(min_qty[0]);
                                item.setItem_price(pprice[0]);
                                item.setItemDetailId(itemDetailId);
                                item.setNetWeight(netWeight[0]);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            myViewHolder.selected_qty_txtview.setText(String.valueOf(selected_qty[0]));
                            myViewHolder.item_weight.setText(String.valueOf(item_weight[0]) + netWeight[0]);
                            myViewHolder.item_min_weight.setText(min_qty[0] + netWeight[0]);
                            myViewHolder.item_price.setText(String.valueOf(pprice[0]));
                        } else {
                            Log.d(TAG, "onResponse: gram_details: " + response);
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: kg_details: ", error );
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                6 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mcontext).addToRequestQueue(stringRequest);
    }

    public void getGramDetails(int item_id, MyViewHolder myViewHolder, Item item){
        final int[] selected_qty = new int[1];
        final Float[] item_weight = new Float[1];
        final String[] netWeight = new String[1];
        final int[] min_qty = new int[1];
        final Float[] pprice = new Float[1];
        final int[] total_stock = new int[1];
        StringRequest stringRequest = new StringRequest(Request.Method.GET, greens.amitech.com.greens.utils.NetworkUtils.BASE_URL +
                greens.amitech.com.greens.utils.NetworkUtils.GRAM_DETAILS + session_manager.getUserId() + "/" + item_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: gram_details: " + response);
                        if (!response.contains("[]") && !response.equals("")){
                            try {
                                JSONObject c = new JSONObject(response);

                                String item_id = c.getString("item_id");
                                String pname = c.getString("item_name");
                                pprice[0] = Float.valueOf(c.getString("item_price"));
                                String pimage = c.getString("item_image");
                                item_weight[0] = Float.valueOf(c.getString("item_weight"));
                                min_qty[0] = c.getInt("item_minqty");
                                total_stock[0] = c.getInt("item_qty");
                                int itemDetailId = c.getInt("item_detail_id");
                                int weightCount = 2;
                                netWeight[0] = c.getString("item_netWeight");
                                Log.d(TAG, "onResponse: netWeight: " + netWeight[0]);
                                int cart_qty = c.getInt("cart_qty");
                                selected_qty[0] = cart_qty;
                                item.setItem_minqty(min_qty[0]);
                                item.setItem_price(pprice[0]);
                                item.setItemDetailId(itemDetailId);
                                item.setNetWeight(netWeight[0]);
//                                item.setItem_qty(total_stock[0]);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            myViewHolder.selected_qty_txtview.setText(String.valueOf(selected_qty[0]));
                            myViewHolder.item_weight.setText(String.valueOf(item_weight[0]) + netWeight[0]);
                            myViewHolder.item_min_weight.setText(min_qty[0] + netWeight[0]);
                            myViewHolder.item_price.setText(String.valueOf(pprice[0]));
                        } else {
                            Log.d(TAG, "onResponse: gram_details: " + response);
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: gram_details: ", error );
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                6 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mcontext).addToRequestQueue(stringRequest);
    }


    public interface OnPagerInterface{
        void onSetPager(ViewPager pager);
    }
}
