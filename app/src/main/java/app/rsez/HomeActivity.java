package app.rsez;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public  class HomeActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private FirebaseAuth mAuth;
    final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    final int MY_PERMISSIONS_REQUEST_STORAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        String[] PERMISSIONS = {
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
        };
        if(!checkPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });
        setupDrawerContent(navigationView);

        Class fragmentClass = TabsFragment.class;
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        if(fragment != null)
        setFragment(fragment);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }


    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass = null;
        String args = "";
        switch (menuItem.getItemId()) {
            case R.id.nav_signOut:
                mAuth.signOut();
                finish();

                Intent myIntent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(myIntent);
                break;
            case R.id.nav_event:
                fragmentClass = CreateFragment.class;
                break;

            case R.id.nav_home:
                fragmentClass = TabsFragment.class;
                break;
//            case R.id.nav_eventDetails:
////                Intent myIntent1 = new Intent(HomeActivity.this, EventDetailsActivity.class);
////                startActivity(myIntent1);
////                args ="jOOG3UrsCqs6hkdD76fq";
////                fragment = EventDetailsFragment.newInstance(args);
//                break;
            case R.id.nav_scan:
                Intent myIntent1 = new Intent(HomeActivity.this, QRScanFragment.class);
                startActivity(myIntent1);
                break;
            default:
                fragmentClass = TabsFragment.class;
                break;
        }
        try {
            if (args == ""){
                fragment = (Fragment) fragmentClass.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragment!=null)
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

    }
    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public boolean checkPermissions(Context context, String... permissions){
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
