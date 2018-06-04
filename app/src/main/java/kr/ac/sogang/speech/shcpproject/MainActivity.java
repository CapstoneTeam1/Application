package kr.ac.sogang.speech.shcpproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    Handler handler;

    private final int NUMBER_OF_SOUND = 6;
    ImageButton connect_button;
    ImageButton[] sound_button;


    private Socket socket;
    private DataInputStream networkReader;
    private boolean isConnected = false;

    private final String SERVER_IP = "163.239.22.136";
    //private final String SERVER_IP = "192.168.0.34";
    private final int SERVER_PORT = 52703;

    String result;
    String[] list = {"0", "0", "0", "0", "0", "0"};

    @Override
    protected void onStop() {
        super.onStop();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connect_button = findViewById(R.id.tab_content1);
        sound_button = new ImageButton[NUMBER_OF_SOUND];
        TabHost host = (TabHost)findViewById(R.id.host);

        sound_button[0] = findViewById(R.id.sound0);    sound_button[1] = findViewById(R.id.sound1);    sound_button[2] = findViewById(R.id.sound2);
        sound_button[3] = findViewById(R.id.sound3);    sound_button[4] = findViewById(R.id.sound4);    sound_button[5] = findViewById(R.id.sound5);

        host.setup();

        TabHost.TabSpec spec = host.newTabSpec("tab1");
        spec.setIndicator(null, ResourcesCompat.getDrawable(getResources(), R.drawable.tab_icon1, null));
        spec.setContent(R.id.tab_content1);
        host.addTab(spec);

        spec = host.newTabSpec("tab2");
        spec.setIndicator(null, ResourcesCompat.getDrawable(getResources(), R.drawable.tab_icon2, null));
        spec.setContent(R.id.tab_content2);
        host.addTab(spec);

        spec = host.newTabSpec("tab3");
        spec.setIndicator(null, ResourcesCompat.getDrawable(getResources(), R.drawable.tab_icon3, null));
        spec.setContent(R.id.tab_content3);
        host.addTab(spec);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.d("Handler", "Index:" + msg.what);

                for (int i=0; i<NUMBER_OF_SOUND; i++) {
                    if (i == msg.what)
                        sound_button[msg.what].setBackgroundColor(Color.RED);
                    else
                        sound_button[i].setBackgroundColor(getResources().getColor(R.color.buttonColor));
                }

                Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                if (msg.what != 5)
                    vibrator.vibrate(1000);
            }
        };

        connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Connect_Button", "Connect button_pushed");


                SocketThread connect_thread = new SocketThread();
                connect_thread.start();

                Log.d("Connect_Button", "isConnected:" + isConnected);
            }
        });

        for (int i=0; i<NUMBER_OF_SOUND; i++) {
            sound_button[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for (int i=0; i<NUMBER_OF_SOUND; i++) {
                        if (v == sound_button[i])
                            Toast.makeText(getApplicationContext(), "Probability: " + list[i] + "%", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    class SocketThread extends Thread {

        @Override
        public void run() {
            Log.d("Socket_Thread", "Socket thread start");

            if (!isConnected) {
                try {
                    socket = new Socket(SERVER_IP, SERVER_PORT);
                    networkReader = new DataInputStream(socket.getInputStream());
                    isConnected = true;
                    Log.d("CONNECT_THREAD", "isConnected true");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (isConnected) {
                RecvThread recv_thread = new RecvThread();
                recv_thread.start();
            }
            Log.d("Socket_Thread", "Socket thread end");
        }
    }

    class RecvThread extends Thread {

        @Override
        public void run() {
            String server_result;
            int index = 0;

            Log.d("Recv_Thread", "Recv thread start");
            while(isConnected) {
                try {
                    byte[] temp = new byte[200];
                    int num = networkReader.read(temp);

                    server_result = new String(temp).trim();
                    Log.d("Recv_Thread", "Recv:" + server_result);
                    list = server_result.split(",");

                    for (int i = 0; i < NUMBER_OF_SOUND; i++) {
                        index = (Double.parseDouble(list[index]) > Double.parseDouble(list[i])) ? index : i;
                    }
                    result = list[index];
                    Message msg = handler.obtainMessage();
                    msg.what = index;
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
