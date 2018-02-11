package net.middleland.arduinodroid.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import net.middleland.arduinodroid.Constants;
import net.middleland.arduinodroid.R;
import net.middleland.arduinodroid.bluetooth.BluetoothArduinoService;
import net.middleland.arduinodroid.bluetooth.DeviceListActivity;
import net.middleland.arduinodroid.fragments.message.BluetoothMessageFragment;
import net.middleland.arduinodroid.fragments.thermometer.ThermometerFragment;

/**
 *  Main BT Activity
 *
 */
public class MainActivity extends BtActivity
        implements NavigationView.OnNavigationItemSelectedListener, BluetoothMessageFragment.OnBtMessageListener, ThermometerFragment.OnThermometerFragmentListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getBtState() == Constants.BT_CONNECTED){
                    performFragmentCommand();
                }else {
                    Snackbar.make(view, "Not connected ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //above part is to determine which fragment is in your frame_container
        if (savedInstanceState == null) {
            setFragment(new BluetoothMessageFragment(), BluetoothMessageFragment.TAG);
        }

    }


    @Override
    protected void onStart() {
        super.onStart();



    }
    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();

    }


    // This could be moved into an abstract BaseActivity
    // class for being re-used by several instances
    protected void setFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_main, fragment , tag);
        fragmentTransaction.commit();
    }
    // This could be moved into an abstract BaseActivity
    // class for being re-used by several instances
    protected void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_main, fragment , tag);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    protected void setStatus(String string) {

    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_temperature) {
            // Handle the camera action
            replaceFragment(new ThermometerFragment(), ThermometerFragment.TAG);

        } else if (id == R.id.nav_command) {
            // Handle the camera action
            replaceFragment(new BluetoothMessageFragment(), BluetoothMessageFragment.TAG);


        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void setupChat() {

        BluetoothMessageFragment btFrag = getBluetoothMessageFragment();

        if (btFrag != null && btFrag.isVisible()) {
            // If article frag is available, we're in two-pane layout...
          btFrag.setupChat();
        }
    }


    @Override
    protected void clearMessageList() {
        // The user selected the headline of an article from the HeadlinesFragment
        // Do something here to display that article

        BluetoothMessageFragment btFrag = getBluetoothMessageFragment();

        if (btFrag != null && btFrag.isVisible()) {
            // If article frag is available, we're in two-pane layout...

            // Call a method in the ArticleFragment to update its content
            btFrag.clearMessageList();
        }

    }

    private BluetoothMessageFragment getBluetoothMessageFragment() {
        return (BluetoothMessageFragment)
                    getSupportFragmentManager().findFragmentByTag(BluetoothMessageFragment.TAG);
    }

    @Override
    public void onTemperatureChangeFragment(float temperature) {

    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    @Override
    public void sendMessage(String message) {
        super.sendMessageBt(message);
        Toast.makeText(this, "-->:"+message, Toast.LENGTH_LONG).show();

        BluetoothMessageFragment btFrag = getBluetoothMessageFragment();

        if (btFrag != null && btFrag.isVisible()) {
            // If article frag is available, we're in two-pane layout...

            // Call a method in the ArticleFragment to update its content
            btFrag.addBtMessage(message);
        }
    }

    @Override
    protected void addBtMessage(String message) {
        Toast.makeText(this, "<--:"+message, Toast.LENGTH_LONG).show();
        BluetoothMessageFragment btFrag = getBluetoothMessageFragment();

        if (btFrag != null && btFrag.isVisible()) {
            // If article frag is available, we're in two-pane layout...

            // Call a method in the ArticleFragment to update its content
            btFrag.addBtMessage(message);
        }
    }


}
