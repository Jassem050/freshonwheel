package greens.amitech.com.greens.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import greens.amitech.com.greens.LoginActivity;
import greens.amitech.com.greens.R;
import greens.amitech.com.greens.adapter.ItemAdapter;
import greens.amitech.com.greens.model.Item;
import greens.amitech.com.greens.utils.SessionManager;
import greens.amitech.com.greens.adapter.CategoryAdapter;
import greens.amitech.com.greens.model.Category;
import greens.amitech.com.greens.utils.NetworkUtils;
import greens.amitech.com.greens.VolleySingleton;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    Context mcontext;


    //ends
    public TextView total_amnt_txtview;
    private RelativeLayout bottom_layout1;
    private SessionManager session_manager;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;

    public HomeFragment() {
        // Required empty public constructor
    }

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        session_manager = new SessionManager(getActivity());
        Log.d(TAG, "onCreateView: user_id: " + session_manager.getUserId());
        total_amnt_txtview = view.findViewById(R.id.total);
        bottom_layout1 = view.findViewById(R.id.bottom_layout1);
        bottom_layout1.setVisibility(View.GONE);
        total_amnt_txtview.setText(String.valueOf(session_manager.getTotalAmount()));
        // tab layout
        viewPager = view.findViewById(R.id.view_pager);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        tabLayout = view.findViewById(R.id.tab_layout);
        categoryAdapter = new CategoryAdapter(getActivity());
        categoryAdapter.setTotalTextView(total_amnt_txtview);
        categoryAdapter.setBottomLayoutView(bottom_layout1);
        categoryList = new ArrayList<>();

        if (session_manager.getTotalAmount() != 0){
            bottom_layout1.setVisibility(View.VISIBLE);
        }
//        categoryList.add(new Category(1, "Vegetables"));
//        categoryList.add(new Category(2, "Fruits"));
//        categoryAdapter.setCategoryList(categoryList);
        viewPager.setAdapter(categoryAdapter);
        viewPager.setCurrentItem(0, true);
//        tabLayout.setupWithViewPager(viewPager);
        viewPager.setUserInputEnabled(false);
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(categoryList.get(position).getCategoryName());
                    }
                }).attach();


        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home");
            ((AppCompatActivity) getActivity()).getSupportActionBar().setElevation(0.0f);
        }

        getCategory();

        mcontext = getContext();

        return view;

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
                            categoryAdapter.setCategoryList(categoryList);
                            viewPager.setAdapter(categoryAdapter);
                            categoryAdapter.notifyDataSetChanged();

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


}
