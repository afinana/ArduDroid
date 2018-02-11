/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.middleland.arduinodroid.fragments.message;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import net.middleland.arduinodroid.R;
import net.middleland.arduinodroid.activities.MainActivity;


/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class BluetoothMessageFragment extends Fragment {

    public static final String TAG = "BluetoothMessageFragment";

    // Layout Views
    private TextView mTemperatureView;
    private TextView mHumidityView;
    private EditText mBTCommand;
    private Button mSendButton;

    private String mConnectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mBtMessageArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Conversation list
     */
    private ListView mConversationView = null;




    // Container Activity must implement this interface
    public interface OnBtMessageListener {
        public void setBtConnectedState(int state);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onStart() {
        super.onStart();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bluetooth_message, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mTemperatureView = (TextView) view.findViewById(R.id.temperature_view);
        mHumidityView = (TextView) view.findViewById(R.id.humidity_view);
        mSendButton = (Button) view.findViewById(R.id.bt_send_button);
        mBTCommand = (EditText) view.findViewById(R.id.bt_command_edit);
        mConversationView = (ListView) view.findViewById(R.id.bt_message_list);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Connect", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    /**
     * Set up the UI and background operations for chat.
     */
    public void setupChat() {
        Log.d(TAG, "setupChat()");


        // Initialize the array adapter for the conversation thread
        mBtMessageArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);

        mConversationView.setAdapter(mBtMessageArrayAdapter);

        // Initialize the compose field with a listener for the return key
        mBTCommand.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                View view = getView();
                if (null != view) {
                    EditText textView = view.findViewById(R.id.bt_command_edit);
                    String message = textView.getText().toString();
                    ((MainActivity) getActivity()).sendMessage(message);
                }
            }
        });


        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }



    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                ((MainActivity) getActivity()).sendMessage(message);
            }
            return true;
        }
    };


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnThermometerFragmentListener {
        // TODO: Update argument type and name
        void onTemperatureChangeFragment(float temperature);
    }

    /**
     * Clears message List field
     */
    public void clearMessageList() {
        mBtMessageArrayAdapter.clear();
    }

    /**
     * Clear Bt command text field
     */
    public void clearBtCommand() {
        // Reset out string buffer to zero and clear the edit text field
        mOutStringBuffer.setLength(0);
        mBTCommand.setText(mOutStringBuffer);
    }
    /**
     * Add message to Bt List
     */
    public void addBtMessage(String message){
        mBtMessageArrayAdapter.add(message);
    }

    /**
     * Name of the connected device
     */
    public String getmConnectedDeviceName() {
        return mConnectedDeviceName;
    }

    public void setmConnectedDeviceName(String mConnectedDeviceName) {
        this.mConnectedDeviceName = mConnectedDeviceName;
    }


}
