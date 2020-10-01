package ca.simonho.sensorrecord;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "MainActivity";

    NavigationView navigationView;
    Menu optionsMenu;
    Toolbar toolbar;
    DrawerLayout drawer;
    LayoutInflater inflater;
    ActionBarDrawerToggle hamburger;
    FragmentManager fragmentManager;
    SensorManager mSensorManager;
    DBHelper dbHelper;
    public Logger logger;

    //App flags
    public static Boolean dataRecordStarted;
    public static Boolean dataRecordCompleted;
    public static Boolean heightUnitSpinnerTouched;
    public static Boolean subCreated;

    //Set the specific sensors to be used throughout the app
    public final static short TYPE_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;
    public final static short TYPE_GYROSCOPE = Sensor.TYPE_GYROSCOPE;
    public final static short TYPE_GRAVITY = Sensor.TYPE_GRAVITY;
    public final static short TYPE_MAGNETIC = Sensor.TYPE_MAGNETIC_FIELD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate called");

        setContentView(R.layout.activity_main);

        inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Create the logger
        String pathToExternalStorage = Environment.getExternalStorageDirectory().toString();
        File logFileDir = new File(pathToExternalStorage, "/SensorRecord/");
        logger = new Logger(logFileDir);

        //Get fragment manager
        fragmentManager = getSupportFragmentManager();

        //First fragment is blank. So set initial fragment to 'new' instead
        addFragment(new NewFragment(), true);

        //Set the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set navigation drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        hamburger = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(hamburger);
        hamburger.syncState();

        //Disable menu items that should display when a user exists
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.nav_start).setEnabled(false);
        navigationView.getMenu().findItem(R.id.nav_save).setEnabled(false);

        //Create dbHelper
        dbHelper = DBHelper.getInstance(this);

        //Set app flags on create/recreate
        dataRecordStarted = false;
        dataRecordCompleted = false;
        heightUnitSpinnerTouched = false;
        subCreated = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_settings, menu);
        optionsMenu = menu;
        return true;
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy called");
        dbHelper.closeDB();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //If we just logged in, prevent back button
            //Allowing back button here will destroy the app, as MainActivity is the only activity
            if (fragmentManager.getBackStackEntryCount() != 0) {
                fragmentManager.popBackStack();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.action_device_info:
                // Inflate the view from a popup layout
                View deviceLayout = inflater.inflate(R.layout.window_device_info,
                        (ViewGroup) findViewById(R.id.window_device_info));

                // Set values
                TextView deviceModel = (TextView) deviceLayout.findViewById(R.id.device_model_content);
                deviceModel.setText(android.os.Build.MODEL);

                TextView deviceAndroidVersion = (TextView) deviceLayout.findViewById(R.id.device_android_version_content);
                deviceAndroidVersion.setText(Build.VERSION.RELEASE);

                // Get sensor availability
                mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

                TextView deviceAccelerometer = (TextView) deviceLayout.findViewById(R.id.device_accelerometer_content);
                deviceAccelerometer.setText(getSensorAvailable(TYPE_ACCELEROMETER));

                TextView deviceGyroscope = (TextView) deviceLayout.findViewById(R.id.device_gyroscope_content);
                deviceGyroscope.setText(getSensorAvailable(TYPE_GYROSCOPE));

                TextView deviceGravity = (TextView) deviceLayout.findViewById(R.id.device_gravity_content);
                deviceGravity.setText(getSensorAvailable(TYPE_GRAVITY));

                TextView deviceMagnetometer = (TextView) deviceLayout.findViewById(R.id.device_magnetic_content);
                deviceMagnetometer.setText(getSensorAvailable(TYPE_MAGNETIC));

                // Display the popup in the center
                final PopupWindow popupDeviceInfo = new PopupWindow(deviceLayout, 800, 850, true);
                popupDeviceInfo.showAtLocation(deviceLayout, Gravity.CENTER, 0, 0);

                // Close popup on button click
                Button deviceInfoCloseButton = (Button) deviceLayout.findViewById(R.id.device_info_close);
                deviceInfoCloseButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View popupView) {
                        popupDeviceInfo.dismiss();
                    }
                });
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_new:
                //If a subject is created, go to Subject Info, otherwise, go to New fragment
                if (!subCreated) {
                    addFragment(new NewFragment(), true);
                } else {
                    addFragment(new SubjectInfoFragment(), true);
                }
                break;
            case R.id.nav_start:
                addFragment(new StartFragment(), true);
                break;
            case R.id.nav_save:
                addFragment(new SaveFragment(), true);
                break;
            case R.id.nav_raw:
                addFragment(new RawFragment(), true);
                break;
            case R.id.nav_accelerometer:
                addFragment(new AccelerometerFragment(), true);
                break;
            case R.id.nav_gyroscope:
                addFragment(new GyroscopeFragment(), true);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addFragment(Fragment fragment, Boolean addToBackStack) {
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            //Check getFragments() == null to prevent the initial blank
            //fragment (before 'New' fragment is displayed) from being added to the backstack
            if (fragmentManager.getFragments() == null || !addToBackStack) {
                fragmentTransaction.replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName())
                        .commit();
            } else {
                fragmentTransaction.replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName())
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    public String getSensorAvailable(short sensor_type){
        Sensor curSensor = mSensorManager.getDefaultSensor(sensor_type);
        if (curSensor != null){
            return("Yes  " + "(" + curSensor.getVendor() + ")");
        } else {
            return("No");
        }
    }
}
