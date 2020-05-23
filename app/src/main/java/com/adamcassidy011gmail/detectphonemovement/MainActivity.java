package com.adamcassidy011gmail.detectphonemovement;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "DetectPhoneMovement";
    private SensorManager sensorManager;
    private Sensor linearAccelerometer;
    private PowerManager powerManager;
    private float linearAcceleration;
    private RequestQueue messageQueue;
    private String URL = "https://fcm.googleapis.com/fcm/send";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        messageQueue = Volley.newRequestQueue(this);
        FirebaseMessaging.getInstance().subscribeToTopic("movement");

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
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            linearAcceleration = (float) Math.sqrt(x * x + y * y + z * z);

            // The linear acceleration (m/s^2) can be adjusted for how sensitive you want it to be.
            if (linearAcceleration > 1) {
                JSONObject mainObject = new JSONObject();
                try{
                    mainObject.put("to","movement");
                    JSONObject notificationObject = new JSONObject();
                    notificationObject.put("body", "Phone was moved.");
                    mainObject.put("notification",notificationObject);

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,URL,
                            mainObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }
                    ){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String,String> header = new HashMap<>();
                            header.put("content-type", "application/json");
                            header.put("authorization"," AIzaSyDcurJV2hTDV7-zGfgjONExLZi49kBkSOQ");
                            return header;
                        }
                    };
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
