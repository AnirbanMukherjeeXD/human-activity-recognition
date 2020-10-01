package ca.simonho.sensorrecord;


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
import android.widget.TextView;

public class RawFragment extends Fragment implements SensorEventListener {
    final short POLL_FREQUENCY = 200; //in milliseconds
    private long lastUpdate = -1;
    private SensorManager sensorManager;
    Sensor sensor;
    private Sensor accelerometer;
    private Sensor linearAccelerometer;
    private Sensor gyroscope;
    private Sensor gravity;
    private Sensor magnetic;

    MainActivity mainActivity;
    TextView accX;
    TextView accY;
    TextView accZ;
    TextView linAccX;
    TextView linAccY;
    TextView linAccZ;
    TextView gyroX;
    TextView gyroY;
    TextView gyroZ;

    float[] accelerometerMatrix = new float[3];
    float[] linearAccelerometerMatrix = new float[3];
    float[] gyroscopeMatrix = new float[3];
    float[] gravityMatrix = new float[3];
    float[] magneticMatrix = new float[3];
    float[] rotationMatrix = new float[9];

    public RawFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_raw, container, false);

        //Set the nav drawer item highlight
        mainActivity = (MainActivity)getActivity();
        mainActivity.navigationView.setCheckedItem(R.id.nav_raw);

        //Set actionbar title
        mainActivity.setTitle("Raw Data");

        //Sensor manager
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        magnetic = sensorManager.getDefaultSensor(MainActivity.TYPE_MAGNETIC);

        //Get text fields
        accX = (TextView) view.findViewById(R.id.raw_value_acc_x);
        accY = (TextView) view.findViewById(R.id.raw_value_acc_y);
        accZ = (TextView) view.findViewById(R.id.raw_value_acc_z);
        linAccX = (TextView) view.findViewById(R.id.raw_value_acc_world_x);
        linAccY = (TextView) view.findViewById(R.id.raw_value_acc_world_y);
        linAccZ = (TextView) view.findViewById(R.id.raw_value_acc_world_z);
        gyroX = (TextView) view.findViewById(R.id.raw_value_gyro_x);
        gyroY = (TextView) view.findViewById(R.id.raw_value_gyro_y);
        gyroZ = (TextView) view.findViewById(R.id.raw_value_gyro_z);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, linearAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetic, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sensor = event.sensor;

        int i = sensor.getType();
        if (i == Sensor.TYPE_ACCELEROMETER) {
            accelerometerMatrix = event.values;
        } else if (i == Sensor.TYPE_LINEAR_ACCELERATION) {
            linearAccelerometerMatrix = event.values;
        } else if (i == Sensor.TYPE_GYROSCOPE) {
            gyroscopeMatrix = event.values;
        } else if (i == Sensor.TYPE_GRAVITY) {
            gravityMatrix = event.values;
        } else if (i == MainActivity.TYPE_MAGNETIC) {
            magneticMatrix = event.values;
        }

        long curTime = System.currentTimeMillis();
        long diffTime = (curTime - lastUpdate);

        // only allow one update every POLL_FREQUENCY.
        if(diffTime > POLL_FREQUENCY) {
            lastUpdate = curTime;

            SensorManager.getRotationMatrix(rotationMatrix, null, gravityMatrix, magneticMatrix);

            accX.setText(String.format("%.2f", accelerometerMatrix[0]));
            accY.setText(String.format("%.2f", accelerometerMatrix[1]));
            accZ.setText(String.format("%.2f", accelerometerMatrix[2]));
            linAccX.setText(String.format("%.2f", linearAccelerometerMatrix[0]));
            linAccY.setText(String.format("%.2f", linearAccelerometerMatrix[1]));
            linAccZ.setText(String.format("%.2f", linearAccelerometerMatrix[2]));
            gyroX.setText(String.format("%.2f", gyroscopeMatrix[0]));
            gyroY.setText(String.format("%.2f", gyroscopeMatrix[1]));
            gyroZ.setText(String.format("%.2f", gyroscopeMatrix[2]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //safe not to implement
    }
}
