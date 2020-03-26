package greens.amitech.com.greens;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import greens.amitech.com.greens.fragments.DynamicTabFragment;
import greens.amitech.com.greens.fragments.CartFragment;
import greens.amitech.com.greens.fragments.HomeFragment;
import greens.amitech.com.greens.fragments.HomeItemsFragment;
import greens.amitech.com.greens.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        DynamicTabFragment.OnFragmentInteractionListener, HomeItemsFragment.OnFragmentInteractionListener {
    private TextView mTextMessage;

    private static String TAG = "home_activity";

    public TextView cart_counter;
    public BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setSelectedItemId(R.id.navigation_home);


//Setting badge for cart counter
//        BottomNavigationMenuView bottomNavigationMenuView =
//                (BottomNavigationMenuView) navView.getChildAt(0);
//        View v = bottomNavigationMenuView.getChildAt(1); // number of menu from left
//        new QBadgeView(this)
//                .bindTarget(v)
////                .setBadgeNumber(5)
//                .setBadgeGravity(Gravity.TOP | Gravity.CENTER)
//                .setShowShadow(true);



        //ends here

        loadFragment(HomeFragment.newInstance("home","home"));


    }

    private void initializeCountDrawer() {

        //Gravity property aligns the text
        cart_counter.setGravity(Gravity.CENTER_VERTICAL);
        cart_counter.setTypeface(null, Typeface.BOLD);
        cart_counter.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        cart_counter.setText("99+");

//count is added
    }


    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        //
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {


            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //toolbar.setTitle("Shop");
                    if (!(getSupportFragmentManager().findFragmentById(R.id.container) instanceof HomeFragment)) {
                        fragment = HomeFragment.newInstance("home", "home");
                        loadFragment(fragment);
                    }
                    return true;

                case R.id.cart:
                    if (!(getSupportFragmentManager().findFragmentById(R.id.container) instanceof CartFragment)) {
                        fragment = CartFragment.newInstance("cart", "cart");
                        loadFragment(fragment);
                    }
                    return true;

                case R.id.profile:
                    if (!(getSupportFragmentManager().findFragmentById(R.id.container) instanceof ProfileFragment)) {
                        fragment = ProfileFragment.newInstance("profile", "profile");
                        loadFragment(fragment);

                    }
                    return true;



            }

            return false;
        }
    };


    public void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
//        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    @Override
    public void onBackPressed()
    {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStack();
        }
        Toast toast = new Toast(getApplicationContext());
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            toast.cancel();
            super.onBackPressed();
            return;
        }
        else { toast.makeText(getBaseContext(), "Please click BACK again to exit", Toast.LENGTH_SHORT).show(); }

        mBackPressed = System.currentTimeMillis();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
