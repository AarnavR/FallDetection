package com.example.mobilefalldetector;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    ProgressBar accel_shakeMeter;
    ProgressBar gyro_shakeMeter;

    private int pointsPlottedA = 5;
    private int pointsPlottedG = 5;
    private int graphIntervalCounter= 0;

    private int fallcounter = 0;
    private int withinthenext = 0;
    private boolean check = false;
    boolean min,max;


    private Sensor accelerometer;
    private Sensor gyroscope;
    private boolean isTimerStarted = false;
    private boolean isFalseAlarmClicked = false;
    private static final float THRESHOLD = 2.5f; // Adjust threshold value as needed

    private Viewport viewport;

    private float[] gravity = new float[3];

    LineGraphSeries<DataPoint> seriesA = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0, 0)
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        accel_shakeMeter = findViewById(R.id.AccelProgressBar);
//        gyro_shakeMeter = findViewById(R.id.GyroProgressBar);

        // Initialize sensor manager and sensors
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        viewport = graph.getViewport();
        viewport.setScrollable(true);
        viewport.setXAxisBoundsManual(true);
        graph.addSeries(seriesA);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register sensor listeners
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister sensor listeners to save battery
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            final float alpha = 0.8f;

            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            float x = event.values[0] - gravity[0];
            float y = event.values[1]- gravity[1];
            float z = event.values[2]- gravity[2];

            float magnitude = (float) Math.sqrt(x * x + y * y + z * z) ;
            float x1 = event.values[0] - gravity[0];
            float y1 = event.values[1]- gravity[1];
            float z1 = event.values[2]- gravity[2];

            float magnitude2 = (float) Math.sqrt(x1 * x1 + y1 * y1 + z1 * z1) ;

            // Check if the magnitude crosses the threshold
            float theta = (float) ((float) Math.atan((Math.sqrt(y *y + z * z)/ x)) * (180/ 3.14159265));

            if (min || max){
                fallcounter++;
            }

            if (magnitude2 < 5 && pointsPlottedA > 20) {
                min = true;
            }

            if (magnitude2 > 16.5 && pointsPlottedA > 20) {
                max = true;
            }

            if (fallcounter > 20) {
                min = false;
                max = false;
                fallcounter = 0;
            }

//            min = true;
//            max = true;
//            theta = 69;

            if (min== true  && max == true && theta > 65  && !isTimerStarted && pointsPlottedA > 20){
                isTimerStarted = true;
                // Start the timer activity
                Intent intent = new Intent(MainActivity.this, AlertActivity.class);
                startActivity(intent);
            }

            pointsPlottedA++;
            if (pointsPlottedA > 1000) {
                pointsPlottedA = 1;
                seriesA.resetData(new DataPoint[] {new DataPoint(0,0)});
            }
            seriesA.appendData(new DataPoint(pointsPlottedA, magnitude), true, pointsPlottedA);
            viewport.setMaxX(pointsPlottedA);
            viewport.setMinX(pointsPlottedA - 200);


//        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
//            float x = event.values[0];
//            float y = event.values[1];
//            float z = event.values[2];
//
//            // Calculate the magnitude of the accelerometer values
//            float magnitude = (float) Math.sqrt(x * x + y * y + z * z);
//
////            gyro_shakeMeter.setProgress((int) magnitude);
//            // Check if the magnitude crosses the threshold
//            if (magnitude > THRESHOLD && !isTimerStarted) {
//                isTimerStarted = true;
//                // Start the timer activity
//                Intent intent = new Intent(MainActivity.this, AlertActivity.class);
//                startActivity(intent);
//            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    public void onFalseAlarmClicked(View view) {
        isFalseAlarmClicked = true;
        // Stop the timer and return to the main activity
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
    }
}