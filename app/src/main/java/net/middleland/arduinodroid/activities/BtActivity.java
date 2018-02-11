package net.middleland.arduinodroid.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import net.middleland.arduinodroid.Constants;
import net.middleland.arduinodroid.R;
import net.middleland.arduinodroid.bluetooth.BluetoothArduinoService;
import net.middleland.arduinodroid.bluetooth.DeviceListActivity;
import net.middleland.arduinodroid.fragments.message.BtMessageHandler;

/**
 * Created by Antonio on 11/02/2018.
 */

public abstract class BtActivity  extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    /** Logger TAG */
    protected static final String TAG = "BtActivity";

    private BtMessageHandler btMessageHandler = new BtMessageHandler();

    private BluetoothAdapter mBluetoothAdapter = null;

    private BluetoothArduinoService mBtArduinoService = null;

    /**
     * Bluetooth connected state
     */
    private int btState;
    /**
     * Bluetooth connected device name
     */
    private static String mConnectedDeviceName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (getBluetoothAdapter() == null) {

            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            this.finish();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();


        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!getBluetoothAdapter().isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (getmBtArduinoService() == null) {
            // Initialize the BluetoothArduinoService to perform bluetooth connections
            mBtArduinoService = new BluetoothArduinoService(this, mHandler);
            setupChat();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getmBtArduinoService() != null) {
            getmBtArduinoService().stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (getmBtArduinoService() != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (getmBtArduinoService().getState() == BluetoothArduinoService.STATE_NONE) {
                // Start the Bluetooth chat services
                getmBtArduinoService().start();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.bluetooth_arduino, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, Constants.REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case R.id.insecure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, Constants.REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            }
            case R.id.discoverable: {
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }
        }
        return false;
    }


    /**
     * The Handler that gets information back from the BluetoothArduinoService
     */
    protected final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothArduinoService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, geConnectedDeviceName()));
                            clearMessageList();
                            setBtConnectedState(Constants.BT_CONNECTED);
                            break;
                        case BluetoothArduinoService.STATE_CONNECTING:
                            setStatus(getString(R.string.title_connecting));
                            setBtConnectedState(Constants.BT_CONNECTING);
                            break;
                        case BluetoothArduinoService.STATE_LISTEN:
                        case BluetoothArduinoService.STATE_NONE:
                            setStatus(getString(R.string.title_not_connected));
                            setBtConnectedState(Constants.BT_SEARCHING);

                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    addBtMessage("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    addBtMessage(geConnectedDeviceName() + ":  " + readMessage);

                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);

                    Toast.makeText(getApplicationContext(), "Connected to "
                            + geConnectedDeviceName(), Toast.LENGTH_SHORT).show();

                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();

                    break;
            }
        }


        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode) {
                case Constants.REQUEST_CONNECT_DEVICE_SECURE:
                    // When DeviceListActivity returns with a device to connect
                    if (resultCode == Activity.RESULT_OK) {
                        connectDevice(data, true);
                    }
                    break;
                case Constants.REQUEST_CONNECT_DEVICE_INSECURE:
                    // When DeviceListActivity returns with a device to connect
                    if (resultCode == Activity.RESULT_OK) {
                        connectDevice(data, false);
                    }
                    break;
                case Constants.REQUEST_ENABLE_BT:
                    // When the request to enable Bluetooth returns
                    if (resultCode == Activity.RESULT_OK) {
                        // Bluetooth is now enabled, so set up a chat session
                        setupChat();
                    } else {
                        // User did not enable Bluetooth or an error occurred
                        Log.d(TAG, "BT not enabled");
                        Toast.makeText(getApplicationContext(), R.string.bt_not_enabled_leaving,Toast.LENGTH_SHORT).show();
                        finish();
                    }
            }
        }

        /**
         * Establish connection with other device
         *
         * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
         * @param secure Socket Security type - Secure (true) , Insecure (false)
         */
        private void connectDevice(Intent data, boolean secure) {
            // Get the device MAC address
            String address = data.getExtras()
                    .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
            // Get the BluetoothDevice object
            BluetoothDevice device = getBluetoothAdapter().getRemoteDevice(address);
            // Attempt to connect to the device
            getmBtArduinoService().connect(device, secure);
        }

    };

    protected abstract void addBtMessage(String s);

    protected abstract void setStatus(String string);

    protected abstract void clearMessageList();

    /**
     * Local Bluetooth adapter
     */
    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    /**
     * Member object for the chat services
     */
    public BluetoothArduinoService getmBtArduinoService() {
        return mBtArduinoService;
    }

    /**
     * Bluetooth state
     */
    public int getBtState() {
        return btState;
    }

    /**
     * Bt connected device name
     */
    public static String geConnectedDeviceName() {
        return mConnectedDeviceName;
    }

    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }


    public void setBtConnectedState(int state) {
        btState = state;
    }


    public void performFragmentCommand() {
        // The user selected the headline of an article from the HeadlinesFragment
        // Do something here to display that article


        sendMessage( btMessageHandler.getCommandPDU( BtMessageHandler.REFRESH_COMMAND_INFO, null));

    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    public void sendMessageBt(String message) {


        // Check that we're actually connected before trying anything
        if (mBtArduinoService.getState() != BluetoothArduinoService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothArduinoService to write
            byte[] send = message.getBytes();
            mBtArduinoService.write(send);


        }
    }


    protected abstract void setupChat();
    protected abstract void sendMessage(String commandPDU);
}
