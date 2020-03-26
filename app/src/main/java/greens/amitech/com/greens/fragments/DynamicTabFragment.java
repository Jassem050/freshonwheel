package greens.amitech.com.greens.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import greens.amitech.com.greens.LoginActivity;
import greens.amitech.com.greens.R;
import greens.amitech.com.greens.utils.SessionManager;
import greens.amitech.com.greens.adapter.ItemAdapter;
import greens.amitech.com.greens.model.Item;
import greens.amitech.com.greens.utils.NetworkUtils;
import greens.amitech.com.greens.VolleySingleton;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DynamicTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DynamicTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DynamicTabFragment extends Fragment implements ItemAdapter.OnItemClickListener, ItemAdapter.OnPagerInterface {
    private static final String TAG = "DynamicTabFragment";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAB_POSITION = "param1";
    private static final String CATEGORY_ID = "param2";

    private int mTabPosition;
    private int mCategoryId;

    private OnFragmentInteractionListener mListener;

    Context mcontext;

    //view pager items declaration starts
    ViewPager pager;
    List<String> images;
    PagerAdapter adapter;
    int currentPage = 0;
    Timer timer;

    String total;


    //slideing image delay
    final long DELAY_MS = 300;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000;
    //ends


    private List<Item> itemList;
    private ItemAdapter itemAdapter;
    private RecyclerView item_recycler;

    static TextView total_amnt_txtview;

    private ShimmerFrameLayout mShimmerViewContainer;

    private static RelativeLayout bottom_layout1;
    private SessionManager session_manager;
    private SwipeRefreshLayout swipeRefreshLayout;

    public DynamicTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DynamicTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DynamicTabFragment newInstance(int param1, int param2, TextView totalTextView, RelativeLayout bottom_layout) {
        DynamicTabFragment fragment = new DynamicTabFragment();
        Bundle args = new Bundle();
        args.putInt(TAB_POSITION, param1);
        args.putInt(CATEGORY_ID, param2);
        fragment.setArguments(args);
        total_amnt_txtview = totalTextView;
        bottom_layout1 = bottom_layout;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTabPosition = getArguments().getInt(TAB_POSITION);
            mCategoryId = getArguments().getInt(CATEGORY_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_dynamic_tab, container, false);

        session_manager = new SessionManager(getActivity());
        Log.d(TAG, "onCreateView: user_id: " + session_manager.getUserId());
//        total_amnt_txtview=rootView.findViewById(R.id.total);
//        bottom_layout1=rootView.findViewById(R.id.bottom_layout1);
//        bottom_layout1.setVisibility(View.INVISIBLE);

//        pager = rootView.findViewById(R.id.view_pager);
        onSetPager(pager);
        mShimmerViewContainer =rootView.findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmer();
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("MainActivity");
        }
        Log.d(TAG, "onCreateView: amt: " + session_manager.getTotalAmount());
        if (session_manager.getTotalAmount() == 0){
            bottom_layout1.setVisibility(View.GONE);
        }

        mcontext = getContext();

        images = new ArrayList<>();

        load_view_pager_images();

        item_recycler = rootView.findViewById(R.id.item_recycler);
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh);

        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home");
        }

//        RelativeLayout item = view.findViewById(R.id.main);
//        View child = getLayoutInflater().inflate(R.layout.loader_layout, null);
//        item.addView(child);
        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(getActivity(), itemList,total_amnt_txtview, this, this, bottom_layout1);
        item_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        item_recycler.setHasFixedSize(true);
        // item_recycler.addItemDecoration(new HomeFragment.GridSpacingItemDecoration(1, dpToPx(0), false));
        item_recycler.setAdapter(itemAdapter);
//
        load_items_from_DB();


        swipeRefreshLayout.setOnRefreshListener(() -> {
            itemList.clear();
            images.clear();
            load_items_from_DB();
            load_view_pager_images();
        });
        return rootView;
    }

    private void load_items_from_DB() {
        Log.d(TAG, "load_items_from_DB: tabPosition: " + mTabPosition);
        Log.d(TAG, "load_items_from_DB: categoryId: " + mCategoryId);
        StringRequest load_item_request = new StringRequest(Request.Method.GET,
                NetworkUtils.BASE_URL + NetworkUtils.LOAD_ITEMS+session_manager.getUserId() + "/" + mCategoryId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (response.equals("not found") || response.contains("not found")){
                            Log.d(TAG, "onResponse: not found: " + session_manager.getUserId());

                            if (getActivity() != null) {
                                LottieAlertDialog.Builder builder = new LottieAlertDialog.Builder(getActivity(), DialogTypes.TYPE_ERROR, null);
                                builder.setTitle("Your Account has been blocked or deleted.");
                                builder.setPositiveText("LOGOUT");
                                builder.setPositiveTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                                builder.setPositiveButtonColor(ContextCompat.getColor(getActivity(), R.color.main_green_color));
                                builder.setPositiveListener(lottieAlertDialog -> {
                                    session_manager.setLogin(false);
                                    startActivity(new Intent(getActivity(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    getActivity().finish();
                                    lottieAlertDialog.dismiss();
                                });
                                LottieAlertDialog dialog = builder.build();
                                dialog.setCancelable(false);
                                dialog.show();
                            }

                        } else {

                            mShimmerViewContainer.stopShimmer();
                            mShimmerViewContainer.setVisibility(View.GONE);

                            Log.d("item_load_response", response);
                            Item a;

                            try {

                                JSONArray data = new JSONArray(response);

                                for (int i = 0; i < data.length(); i++) {

                                    JSONObject c = data.getJSONObject(i);

                                    int selected_qty = 0;

                                    String item_id = c.getString("item_id");
                                    String pname = c.getString("item_name");
                                    Float pprice = Float.valueOf(c.getString("item_price"));
                                    String pimage = c.getString("item_image");
                                    Float item_weight = Float.valueOf(c.getString("item_weight"));
                                    int cart_qty = c.getInt("cart_qty");
                                    int min_qty = c.getInt("item_minqty");
                                    total = c.getString("total_amt");
                                    int total_stock = c.getInt("item_qty");
                                    int itemDetailId = c.getInt("item_detail_id");
                                    int weightCount = c.getInt("weight_count");
                                    String netWeight = c.getString("item_netWeight");

                                    Log.d(TAG, "onResponse: stock: " + total_stock);
                                    Log.d(TAG, "onResponse: item_name: " + pname);
                                    Log.d(TAG, "onResponse: qty" + cart_qty);

                                    session_manager.setTotalAmount(Float.parseFloat(total));


                                    if (cart_qty > 0) {
                                        selected_qty = cart_qty;
                                    } else {
                                        selected_qty = 0;
                                    }

                                    a = new Item(item_id, pname, item_weight, pimage, selected_qty, pprice,
                                            min_qty, total_stock, itemDetailId, weightCount, netWeight);
                                    itemList.add(a);

                                }
                                Log.d(TAG, "onResponse: list" + itemList.size());
//                                Log.d(TAG, "onResponse: get: " + itemList.get(1).getItem_name());
                                itemAdapter.setItemList(itemList);
                                item_recycler.setAdapter(itemAdapter);
                                itemAdapter.notifyDataSetChanged();
                                Log.d(TAG, "onResponse: count: " + item_recycler.getAdapter().getItemCount());
//                                total_amnt_txtview.setText(total);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("volley_error", error.toString());
                        Toast.makeText(mcontext, "Something went wrong try again", Toast.LENGTH_SHORT).show();

                    }
                }
        );
        load_item_request.setRetryPolicy(new DefaultRetryPolicy(5 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mcontext).addToRequestQueue(load_item_request);

    }

    private void load_view_pager_images() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, NetworkUtils.BASE_URL + NetworkUtils.SLIDER_IMAGES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("sliderresponse", response);

                        try {

                            JSONArray data = new JSONArray(response);

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject c = data.getJSONObject(i);
                                String image_name = c.getString("img_url");
                                images.add(image_name);
                            }
                            itemAdapter.setImageList(images);
                            itemAdapter.notifyDataSetChanged();

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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mcontext).addToRequestQueue(stringRequest);



    }


    @Override
    public void onCheckStock(int position, ImageView plusbtn) {
        if (position > 0 && position < itemList.size()) {
            Item item = itemList.get(position - 1);

            if (item.getItem_qty() == item.getSelected_qty()) {
                plusbtn.setColorFilter(mcontext.getResources().getColor(R.color.disabled_color));
                plusbtn.setEnabled(false);
            }
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSetPager(ViewPager pager) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}


