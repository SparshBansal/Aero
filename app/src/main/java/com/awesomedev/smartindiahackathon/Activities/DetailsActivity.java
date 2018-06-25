package com.awesomedev.smartindiahackathon.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.awesomedev.smartindiahackathon.Fragments.CounterFragment;
import com.awesomedev.smartindiahackathon.Fragments.DetailsActivityFragment;
import com.awesomedev.smartindiahackathon.R;

import butterknife.BindString;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    @BindString(R.string.KEY_AIRPORT)
    String KEY_AIRPORT;

    @BindString(R.string.KEY_CARRIER)
    String KEY_CARRIER;

    @BindString(R.string.KEY_FLIGHT)
    String KEY_FLIGHT;

    @BindString(R.string.KEY_AIRPORT_ID)
    String KEY_AIRPORT_ID;

    @BindString(R.string.KEY_CARRIER_ID)
    String KEY_CARRIER_ID;

    @BindString(R.string.KEY_FLIGHT_ID)
    String KEY_FLIGHT_ID;

    Fragment detailsFragment = null;

    private static NavigationView nvListview = null;
    private static DrawerLayout drawerLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (toolbar!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Navigation and Flight Details");
            toolbar.setNavigationIcon(R.mipmap.ic_menu_black_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        ButterKnife.bind(this);

        nvListview = (NavigationView) findViewById(R.id.nv_listview);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        Bundle args = new Bundle();

        // Put the data in the bundle
        args.putString(KEY_AIRPORT,getIntent().getStringExtra(KEY_AIRPORT));
        args.putString(KEY_CARRIER,getIntent().getStringExtra(KEY_CARRIER));
        args.putString(KEY_FLIGHT,getIntent().getStringExtra(KEY_FLIGHT));

        args.putInt(KEY_AIRPORT_ID,getIntent().getIntExtra(KEY_AIRPORT_ID,0));
        args.putInt(KEY_CARRIER_ID,getIntent().getIntExtra(KEY_CARRIER_ID,0));
        args.putInt(KEY_FLIGHT_ID, getIntent().getIntExtra(KEY_FLIGHT_ID, 0));

        detailsFragment = new DetailsActivityFragment();
        detailsFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction().add(R.id.fl_container,detailsFragment).commit();

        nvListview.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){

                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.flight_details:
                        Bundle args = new Bundle();

                        // Put the data in the bundle
                        args.putString(KEY_AIRPORT,getIntent().getStringExtra(KEY_AIRPORT));
                        args.putString(KEY_CARRIER,getIntent().getStringExtra(KEY_CARRIER));
                        args.putString(KEY_FLIGHT,getIntent().getStringExtra(KEY_FLIGHT));

                        args.putInt(KEY_AIRPORT_ID,getIntent().getIntExtra(KEY_AIRPORT_ID,0));
                        args.putInt(KEY_CARRIER_ID,getIntent().getIntExtra(KEY_CARRIER_ID,0));
                        args.putInt(KEY_FLIGHT_ID, getIntent().getIntExtra(KEY_FLIGHT_ID, 0));

                        detailsFragment = new DetailsActivityFragment();
                        detailsFragment.setArguments(args);

                        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container,detailsFragment).commit();
                        return true;

                    // For rest of the options we just show a toast on click

                    case R.id.counter:
                        args = new Bundle();

                        // Put the data in the bundle
                        args.putString(KEY_AIRPORT,getIntent().getStringExtra(KEY_AIRPORT));
                        args.putString(KEY_CARRIER,getIntent().getStringExtra(KEY_CARRIER));
                        args.putString(KEY_FLIGHT,getIntent().getStringExtra(KEY_FLIGHT));

                        args.putInt(KEY_AIRPORT_ID,getIntent().getIntExtra(KEY_AIRPORT_ID,0));
                        args.putInt(KEY_CARRIER_ID,getIntent().getIntExtra(KEY_CARRIER_ID,0));

                        CounterFragment counterFragment = new CounterFragment();
                        counterFragment.setArguments(args);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container,counterFragment).commit();
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
