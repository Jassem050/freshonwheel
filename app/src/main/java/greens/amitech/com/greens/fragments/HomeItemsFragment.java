package greens.amitech.com.greens.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import greens.amitech.com.greens.VolleySingleton;
import greens.amitech.com.greens.adapter.ItemAdapter;
import greens.amitech.com.greens.model.Category;
import greens.amitech.com.greens.model.Item;
import greens.amitech.com.greens.utils.NetworkUtils;
import greens.amitech.com.greens.utils.SessionManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeItemsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeItemsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeItemsFragment extends Fragment implements ItemAdapter.OnItemClickListener, ItemAdapter.OnPagerInterface {
    private static final String TAG = "HomeItemsFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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

    private OnFragmentInteractionListener mListener;
    private RecyclerView vegRecyclerView, FruitsRecyclerView;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    private SessionManager sessionManager;
    private List<Category> categoryList;
    private RelativeLayout bottomTotalLayout;
    private TextView totalTextView;

    public HomeItemsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeItemsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeItemsFragment newInstance(String param1, String param2) {
        HomeItemsFragment fragment = new HomeItemsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home_items, container, false);

        sessionManager = new SessionManager(getActivity());
        itemList = new ArrayList<>();
        categoryList = new ArrayList<>();

        vegRecyclerView = rootView.findViewById(R.id.veg_recycler_view);
        vegRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        vegRecyclerView.setHasFixedSize(true);
        totalTextView = rootView.findViewById(R.id.total);
        bottomTotalLayout = rootView.findViewById(R.id.bottom_layout1);
        itemAdapter = new ItemAdapter(getActivity(), itemList, totalTextView, this, this, bottomTotalLayout);
        load_items_from_DB();
        return rootView;
    }

    private void load_items_from_DB() {
        StringRequest load_item_request = new StringRequest(Request.Method.GET,
                NetworkUtils.BASE_URL + NetworkUtils.LOAD_ITEMS + sessionManager.getUserId() + "/" + 1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        swipeRefreshLayout.setRefreshing(false);
                        if (response.equals("not found") || response.contains("not found")){
                            Log.d(TAG, "onResponse: not found: " + sessionManager.getUserId());

                            if (getActivity() != null) {
                                LottieAlertDialog.Builder builder = new LottieAlertDialog.Builder(getActivity(), DialogTypes.TYPE_ERROR, null);
                                builder.setTitle("Your Account has been blocked or deleted.");
                                builder.setPositiveText("LOGOUT");
                                builder.setPositiveTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                                builder.setPositiveButtonColor(ContextCompat.getColor(getActivity(), R.color.main_green_color));
                                builder.setPositiveListener(lottieAlertDialog -> {
                                    sessionManager.setLogin(false);
                                    startActivity(new Intent(getActivity(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    getActivity().finish();
                                    lottieAlertDialog.dismiss();
                                });
                                LottieAlertDialog dialog = builder.build();
                                dialog.setCancelable(false);
                                dialog.show();
                            }

                        } else {

//                            mShimmerViewContainer.stopShimmer();
//                            mShimmerViewContainer.setVisibility(View.GONE);

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

                                    sessionManager.setTotalAmount(Float.parseFloat(total));


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
                                vegRecyclerView.setAdapter(itemAdapter);
                                itemAdapter.notifyDataSetChanged();
                                Log.d(TAG, "onResponse: count: " + vegRecyclerView.getAdapter().getItemCount());
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
//                        Toast.makeText(getActivity(), "Something went wrong try again", Toast.LENGTH_SHORT).show();

                    }
                }
        );
        load_item_request.setRetryPolicy(new DefaultRetryPolicy(5 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(load_item_request);

    }


    private void getCategory() {
        StringRequest load_category_request = new StringRequest(Request.Method.GET, NetworkUtils.BASE_URL + NetworkUtils.CATEGORIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: category: " + response);
                        JSONArray data = null;
                        try {
                            data = new JSONArray(response);
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject jsonObject = data.getJSONObject(i);
                                int categoryId = jsonObject.getInt("category_id");
                                String categoryName = jsonObject.getString("category_name");

                                Category category = new Category(categoryId, categoryName);
                                categoryList.add(category);

                            }
//                            categoryAdapter.setCategoryList(categoryList);
//                            viewPager.setAdapter(categoryAdapter);
//                            categoryAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: category: ",error );
                    }
                });
        load_category_request.setRetryPolicy(new DefaultRetryPolicy(5 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(load_category_request);
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

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);



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
    public void onCheckStock(int position, ImageView plusbtn) {
        if (position > 0) {
            Item item = itemList.get(position - 1);

            if (item.getItem_qty() == item.getSelected_qty()) {
                plusbtn.setColorFilter(getActivity().getResources().getColor(R.color.disabled_color));
                plusbtn.setEnabled(false);
            }
        }
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
