package com.adamcassidy011gmail.detectphonemovement;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.Tag;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "DetectPhoneMovement";
    private SensorManager sensorManager;
    private Sensor linearAccelerometer;
    private PowerManager powerManager;
    private float linearAcceleration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /*PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"DetectMovement: WakelockTag");
        wakeLock.acquire();*/

        linearAcceleration = 0.00f;
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(MainActivity.this, linearAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, linearAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            /*NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "DetectPhoneMovement")
                    .setContentTitle("Phone movement was detected.");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);*/
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            linearAcceleration = (float) Math.sqrt(x*x + y*y + z*z);

            // The linear acceleration (m/s^2) can be adjusted for how sensitive you want it to be.
            if(linearAcceleration > 1){
                Log.d(TAG, "Moved: Add this in protected apps to stay on while phone locked," +
                        "or turn off screentimeout and lower brightness.");
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
