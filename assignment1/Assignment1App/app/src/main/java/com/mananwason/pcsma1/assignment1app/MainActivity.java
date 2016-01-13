package com.mananwason.pcsma1.assignment1app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private List<AccelerometerData> sessionData;
    private final String fileName = "accelerometer.csv";
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private Button startButton;
    private Button stopButton;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sessionData = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            requestAccountPermissions();
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } else {
            Snackbar.make(findViewById(R.id.mainActivity), "No accelerometer found", Snackbar.LENGTH_LONG).show();

        }


        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

        stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        stopButton.setClickable(false);
        fab.setClickable(false);
        String csv = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName;
        File file = new File(csv);
        if (file.exists()) {
            fab.setClickable(true);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        sensorManager.unregisterListener(this, accelerometer);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("abc", "sensor data" + event.values[0]);
        AccelerometerData data = new AccelerometerData(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2]);
        sessionData.add(data);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, accelerometer);

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.unregisterListener(this, accelerometer);

    }

    public void writeToCSV(List<AccelerometerData> accelerometerDataList) {

        List<String[]> finalData = new ArrayList<>();

        for (AccelerometerData data : accelerometerDataList) {
            finalData.add(new String[]{data.getTimestamp() + " " + data.getX() + " " + data.getY() + " " + data.getZ()});
        }
        String csv = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName;
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(csv));
            writer.writeAll(finalData);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void requestAccountPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.GET_ACCOUNTS)) {
            Log.i("permission",
                    "Displaying contacts permission rationale to provide additional context.");

            Snackbar.make(MainActivity.this.findViewById(R.id.mainActivity), R.string.permission_account_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(MainActivity.this, PERMISSIONS_STORAGE,
                                            1);
                        }
                    })
                    .show();
        } else {
            // Storage permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startButton:
                sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                stopButton.setClickable(true);
                startButton.setClickable(false);
                break;
            case R.id.stopButton:
                sensorManager.unregisterListener(MainActivity.this, accelerometer);
                writeToCSV(sessionData);
                stopButton.setClickable(false);
                startButton.setClickable(true);
                break;
            case R.id.fab:
                Client client = new Client(findViewById(R.id.mainActivity));
                client.execute();
                Snackbar.make(v, "File sent to server with IP : " + client.serverIp, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
        }
    }
}
