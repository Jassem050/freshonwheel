package greens.amitech.com.greens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import greens.amitech.com.greens.utils.NetworkUtils;
import greens.amitech.com.greens.utils.SessionManager;

public class RegisterActivity extends AppCompatActivity {

   EditText name,email,phone,address,password,c_password;
   Button signup;
   NetworkUtils NetworkUtils;

   SessionManager session_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        NetworkUtils =new NetworkUtils();


        session_manager=new SessionManager(this);



        name=findViewById(R.id.user_name);
        email=findViewById(R.id.user_email);
        phone=findViewById(R.id.user_phone);
        address=findViewById(R.id.user_address);
        password=findViewById(R.id.password);
        c_password=findViewById(R.id.c_password);

        signup=findViewById(R.id.signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (name.getText().toString().equals(""))
                {
                    Toast.makeText(RegisterActivity.this, "Enter name", Toast.LENGTH_SHORT).show();
                }
                else if (email.getText().toString().equals(""))
                {
                    Toast.makeText(RegisterActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                }
                else if (phone.getText().toString().equals(""))
                {
                    Toast.makeText(RegisterActivity.this, "Enter Phone", Toast.LENGTH_SHORT).show();
                }
                else if (address.getText().toString().equals(""))
                {
                    Toast.makeText(RegisterActivity.this, "Enter address", Toast.LENGTH_SHORT).show();
                }
                else if (password.getText().toString().equals(""))
                {
                    Toast.makeText(RegisterActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                }
                else if (c_password.getText().toString().equals(""))
                {
                    Toast.makeText(RegisterActivity.this, "Enter Confirm password", Toast.LENGTH_SHORT).show();
                }
                else if (!password.getText().toString().equals(c_password.getText().toString()))
                {
                    Toast.makeText(RegisterActivity.this, "both password should be equal", Toast.LENGTH_SHORT).show();
                }
                else if (password.getText().toString().length()< 6)
                {
                    Toast.makeText(RegisterActivity.this, "password is too short", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Register_user();
                }
            }
        });




    }

    private void Register_user() {

            StringRequest user_register = new StringRequest(Request.Method.POST, NetworkUtils.BASE_URL + NetworkUtils.REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("reister_respnse", response);

                        try {

                            JSONObject data = new JSONObject(response);

                            String status=data.getString("status");
                            String message=data.getString("msg");


                        if (status.equals("success"))
                        {
                            String user_id=data.getString("user_id");
                            session_manager.setUserId(user_id);
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            Toast.makeText(RegisterActivity.this, "Registration Success", Toast.LENGTH_SHORT).show();
                            session_manager.setLogin(true);

                            Log.d("saved_user_id",user_id);

                            finish();
                        }
                        else if (status.contains("error"))
                        {
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
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

                params.put("name", name.getText().toString());
                params.put("phone", phone.getText().toString());
                params.put("email", email.getText().toString());
                params.put("address", address.getText().toString());
                params.put("pass", password.getText().toString());
                return params;
            }
        };

        user_register.setRetryPolicy(new DefaultRetryPolicy(
                6 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(RegisterActivity.this).addToRequestQueue(user_register);

    }
}
