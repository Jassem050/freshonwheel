package greens.amitech.com.greens.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import greens.amitech.com.greens.AboutUsActivity;
import greens.amitech.com.greens.ContactActivity;
import greens.amitech.com.greens.LoginActivity;
import greens.amitech.com.greens.MyOrdersActivity;
import greens.amitech.com.greens.R;
import greens.amitech.com.greens.utils.SessionManager;
import greens.amitech.com.greens.utils.NetworkUtils;
import greens.amitech.com.greens.VolleySingleton;

public class ProfileFragment extends Fragment {

    RelativeLayout my_orders,logout_user, about_us, contact_us;
    NetworkUtils NetworkUtils;

    Context mcontext;

    private TextView UserName,Address;
    private SessionManager session_manager;




    public ProfileFragment()
    {
        //empty c0nstructor
    }

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.profile_fragment,container,false);

        init_views(view);
        mcontext=getContext();
        session_manager=new SessionManager(mcontext);
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Account");
        }
        Load_user_details(session_manager.getUserId());


        return view;
    }

    private void Load_user_details(String userid) {

        StringRequest load_profile = new StringRequest(Request.Method.GET, NetworkUtils.BASE_URL + NetworkUtils.USER_PROFILE+userid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {

                            JSONArray data = new JSONArray(response);

                            for (int i = 0; i < data.length(); i++) {

                                JSONObject c = data.getJSONObject(i);


                                String username=c.getString("uname");
                                String user_address = c.getString("uaddress");


                                UserName.setText(username);
                                Address.setText(user_address);


                            }



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
        load_profile.setRetryPolicy(new DefaultRetryPolicy(5 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(mcontext).addToRequestQueue(load_profile);
    }

    private void init_views(View view) {


        NetworkUtils =new NetworkUtils();
        UserName=view.findViewById(R.id.user_name);
        Address=view.findViewById(R.id.address);


        my_orders=view.findViewById(R.id.my_orders);
        logout_user=view.findViewById(R.id.logout_user);
        about_us = view.findViewById(R.id.about_us);
        contact_us = view.findViewById(R.id.contact_us);

        contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ContactActivity.class));
            }
        });

        logout_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                session_manager.setLogin(false);
                session_manager.removeAll();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();

            }
        });



        my_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                startActivity(new Intent(getContext(), MyOrdersActivity.class));

            }
        });

        about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AboutUsActivity.class));
            }
        });
    }
}
