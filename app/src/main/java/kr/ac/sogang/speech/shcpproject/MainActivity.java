package kr.ac.sogang.speech.shcpproject;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    Handler handler;

    private final int NUMBER_OF_SOUND = 6;
    ImageButton connect_button;
    ImageButton[] sound_button;


    private Socket socket;
    private BufferedReader networkReader;
    private boolean isConnected = false;

    private final String SERVER_IP = "163.239.22.105";
    private final int SERVER_PORT = 5000;

    String result;

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
                sound_button[msg.what].setBackgroundColor(Color.RED);
            }
        };

        connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SocketThread connect_thread = new SocketThread();
                Log.d("CONNECT_THREAD", "Connect button_pushed");
                connect_thread.start();
                if (isConnected) {
                    RecvThread recv_thread = new RecvThread();
                    recv_thread.start();
                }
            }
        });

        for (int i=0; i<NUMBER_OF_SOUND; i++) {
            sound_button[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    class SocketThread extends Thread {

        @Override
        public void run() {
            if (!isConnected) {
                try {
                    socket = new Socket(SERVER_IP, SERVER_PORT);
                    networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    isConnected = true;
                    Log.d("CONNECT_THREAD", "isConnected true");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    socket.close();
                    isConnected = false;
                    Log.d("CONNECT_THREAD", "isConnected false");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    class RecvThread extends Thread {

        @Override
        public void run() {
            String server_result;
            String[] list;
            int index = 0;

            while(isConnected) {
                try {
                    server_result = networkReader.readLine();
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
