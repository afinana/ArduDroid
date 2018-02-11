package net.middleland.arduinodroid.fragments.thermometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.middleland.arduinodroid.R;
import net.middleland.arduinodroid.views.Thermometer;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnThermometerFragmentListener} interface
 * to handle interaction events.
 * Use the {@link ThermometerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThermometerFragment extends Fragment implements SensorEventListener{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAMETERS = "parameters";
    public static final String TAG = "ThermometerFragment" ;


    // TODO: Rename and change types of parameters
    private ArrayList<String> mParameters;


    private OnThermometerFragmentListener mListener;
    private SensorManager sensorManager;
    private Thermometer thermometer;
    private float temperature;

    public ThermometerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param ARG_PARAMETERS Parameter list.
     * @return A new instance of fragment ThermometerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThermometerFragment newInstance(ArrayList<String> parameters) {
        ThermometerFragment fragment = new ThermometerFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_PARAMETERS, parameters);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParameters = getArguments().getStringArrayList(ARG_PARAMETERS);

        }
        thermometer =  getActivity().findViewById(R.id.thermometer);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_thermometer, container, false);
    }



    @Override
    public void onResume() {
        super.onResume();
        loadAmbientTemperature();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterAll();
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.values.length > 0) {
            temperature = sensorEvent.values[0];
            thermometer.setCurrentTemp(temperature);
            if (mListener != null) {
                mListener.onTemperatureChangeFragment(temperature);
            }
            getActivity().getActionBar().setTitle(getString(R.string.app_name) + " : " + temperature);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void loadAmbientTemperature() {
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            Toast.makeText(getContext(), "No Ambient Temperature Sensor !", Toast.LENGTH_LONG).show();
        }
    }

    private void unregisterAll() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnThermometerFragmentListener) {
            mListener = (OnThermometerFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnThermometerFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

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
}
