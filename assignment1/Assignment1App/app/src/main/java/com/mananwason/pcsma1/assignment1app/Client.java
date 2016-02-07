package com.mananwason.pcsma1.assignment1app;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class Client extends AsyncTask<Void, Void, Void> {
    private Socket socket = null;
    private ObjectOutputStream outputStream = null;
    private View callingView;
    private boolean isConnected = false;
    String serverIp = "192.168.56.157";
    private final String fileName = "accelerometer.csv";

    public Client(View view) {
        callingView = view;
    }

    @Override
    protected Void doInBackground(Void... params) {
        connect();
        return null;
    }


    public void connect() {
        while (!isConnected) {
            try {
                InetAddress server = InetAddress.getByName(serverIp);
                socket = new Socket(server, 6300);
                socket.setSoTimeout(300);
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                isConnected = true;
                sendFile();

            } catch (SocketTimeoutException | UnknownHostException e) {
                Log.d("abc", "timeout | unknownhost");
                Snackbar.make(callingView, "No Host with Ip : " + serverIp + " found", Snackbar.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendFile() {

        String fileSource = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName;

        File file = new File(fileSource);
        try {
            byte[] bytes = new byte[(int) file.length()];
            BufferedInputStream bis;
            bis = new BufferedInputStream(new FileInputStream(file));
            bis.read(bytes, 0, bytes.length);
            OutputStream os = socket.getOutputStream();
            os.write(bytes, 0, bytes.length);
            os.flush();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

