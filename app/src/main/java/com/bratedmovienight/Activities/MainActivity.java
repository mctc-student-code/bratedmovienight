package com.bratedmovienight.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bratedmovienight.Fragments.MainFragment;
import com.bratedmovienight.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends MenuActivity {

    private final String TAG = "Main-Activity";

    //layout elements
    private LinearLayout fragmentContainer;
    private ImageView mProfilePicture;
    private TextView mProfileName;
    private TextView mProfileLbl;
    private BottomNavigationView mBottomNavigationView;

    private FirebaseAuth mAuth;
    private Button mSignOut;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentContainer = (LinearLayout) findViewById(R.id.fragment_container);
        mProfileName = (TextView) findViewById(R.id.profile_name);
        mProfilePicture = (ImageView) findViewById(R.id.profile_pic);
        mProfileLbl = (TextView) findViewById(R.id.profile_tv);
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_main);

        // listener for botton navigation menu
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Bundle bundle;
                Fragment movieFragment, tvFragment;
                FragmentManager fragmentManager;
                FragmentTransaction fragmentTransaction;

                switch (item.getItemId()){
                    case R.id.action_search:
                        // start Search Dialog
                        onSearchRequested();

                        break;
                    case R.id.action_favorites:
                        break;
                    // load movies fragment
                    case R.id.action_movie:
                        bundle = new Bundle();
                        bundle.putString("fragment", "movie");
                        movieFragment = new MainFragment();
                        movieFragment.setArguments(bundle);
                        fragmentManager = getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, movieFragment);
                        fragmentTransaction.commit();

                        break;
                }
                return true;
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mSignOut = (Button) findViewById(R.id.main_signout);

        // load main fragment with movies
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, new MainFragment());
        /*
        Line below was trying another layout, to display several options on main screen for user:
        Upcoming movies + Popular movies
         */
        //fragmentTransaction.add(R.id.fragment_container, new MainMoviesFragment());
        fragmentTransaction.commit();

        user = mAuth.getCurrentUser();


    }

    @Override
    protected void onStart() {
        super.onStart();

        mProfileName.setText(user.getDisplayName());

        Glide.with(getApplicationContext())
                .load(user.getPhotoUrl())
                .into(mProfilePicture);

    }


}