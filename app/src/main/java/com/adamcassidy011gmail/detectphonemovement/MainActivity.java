package com.adamcassidy011gmail.detectphonemovement;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
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
            if (linearAcceleration > 13.75) {
                JSONObject mainObject = new JSONObject();
                try{
                    mainObject.put("to","/topics/"+"movement");
                    JSONObject notificationObject = new JSONObject();
                    notificationObject.put("body", "ALERT");
                    notificationObject.put("body", FirebaseInstanceId.getInstance().toString().substring(43));
                    mainObject.put("notification", notificationObject);
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
                            header.put("authorization","key=AAAA2p-y2J4:APA91bHZmn-LMuxZ-RA0-sJxAmQ-ExIPQknLzcjFDOM3VHXBRtzlydjZIzVzBo44Uk9-SGLzZK-zD2eL2lAkvQeb_KUgEdo7_NPoXsBd2cwJCkTkIjcLpYfe0HKMyJCZbsoatAs2qtNYy");
                            return header;
                        }
                    };
                    messageQueue.add(request);
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
