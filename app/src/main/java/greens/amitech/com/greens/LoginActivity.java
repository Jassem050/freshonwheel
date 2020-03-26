package greens.amitech.com.greens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import greens.amitech.com.greens.utils.NetworkUtils;
import greens.amitech.com.greens.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    RelativeLayout bottom_layout, transparentBg;
    TextInputEditText phone, password;
    NetworkUtils NetworkUtils;

    private SessionManager sessionManager;
    Button login_btn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();

        } else {
            setContentView(R.layout.activity_login_1);
            init();

        }
    }


    private void init() {
        phone = findViewById(R.id.phone_number);
        password = findViewById(R.id.password);
        NetworkUtils = new NetworkUtils();
        login_btn = findViewById(R.id.login);
        progressBar = findViewById(R.id.progress_bar);
        transparentBg = findViewById(R.id.transparent_bg);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone.getText().toString().equals("") && password.getText().toString().equals("")){
                    Toast.makeText(LoginActivity.this, "Enter Phone Number and Password", Toast.LENGTH_SHORT).show();
                } else if (phone.getText().toString().trim().length() < 10 || phone.getText().toString().trim().length() > 10){
                    Toast.makeText(LoginActivity.this, "Enter valid Phone number", Toast.LENGTH_SHORT).show();
                } else if (phone.getText().toString().equals("")){
                    Toast.makeText(LoginActivity.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
                } else if (password.getText().toString().equals("")){
                    Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                }  else {
                    transparentBg.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    Check_login();
                }
            }
        });


        bottom_layout = findViewById(R.id.bottom_layout);
//        bottom_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
//            }
//        });

    }

    private void Check_login() {
        Log.d(TAG, "Check_login: ");
        StringRequest user_login = new StringRequest(Request.Method.POST, NetworkUtils.BASE_URL + NetworkUtils.LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("login_response", response);

                        try {

                            JSONObject data = new JSONObject(response);

                            String msg = data.getString("message");


                            if (msg.equals("success")) {
                                transparentBg.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                String user_id = data.getString("user_id");

                                sessionManager.setUserId(user_id);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                sessionManager.setLogin(true);

                                Toast.makeText(LoginActivity.this, "LoginActivity Success", Toast.LENGTH_SHORT).show();
                                finish();

                            } else if (msg.equals("failed")) {
                                transparentBg.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, "phone number or password is incorrect", Toast.LENGTH_SHORT).show();

                            } else {
                                transparentBg.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                Log.d(TAG, "onResponse: login: " + response);
                                Toast.makeText(LoginActivity.this, "something went wrong ", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "onResponse: login: ",e );
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: login: " + error.toString());
                        Log.e(TAG, "onErrorResponse: login: ",error );
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("mob", phone.getText().toString());
                params.put("pass", password.getText().toString());
                return params;
            }
        };

        user_login.setRetryPolicy(new DefaultRetryPolicy(
                6 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(user_login);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
