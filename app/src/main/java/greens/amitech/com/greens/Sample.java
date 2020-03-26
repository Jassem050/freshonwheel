package greens.amitech.com.greens;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Sample extends AppCompatActivity {

    EditText username,password;
    Button login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        username=findViewById(R.id.user);
        password=findViewById(R.id.pass);
        login=findViewById(R.id.log);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (username.getText().toString().contains("zephyr") && password.getText().toString().equals("123"))
                {
                    Toast.makeText(Sample.this, "LoginActivity success", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(Sample.this, "LoginActivity failed", Toast.LENGTH_SHORT).show();
                }

            }
        });







    }

}
