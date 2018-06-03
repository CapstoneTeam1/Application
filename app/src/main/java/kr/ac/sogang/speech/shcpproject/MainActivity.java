package kr.ac.sogang.speech.shcpproject;

import android.annotation.SuppressLint;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TabHost;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int NUMBER_OF_SOUND = 6;

        ImageButton connect_button = findViewById(R.id.tab_content1);
        ImageButton sound_button[] = new ImageButton[NUMBER_OF_SOUND];


        sound_button[0] = findViewById(R.id.sound0);    sound_button[1] = findViewById(R.id.sound1);    sound_button[2] = findViewById(R.id.sound2);
        sound_button[3] = findViewById(R.id.sound3);    sound_button[4] = findViewById(R.id.sound4);    sound_button[5] = findViewById(R.id.sound5);

        TabHost host = (TabHost)findViewById(R.id.host);
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

        connect_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /* TODO: Socket connect */
            }
        });

        for (int i=0; i<NUMBER_OF_SOUND; i++) {
            sound_button[i].setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    /* TODO: Toast percent of sound */
                }
            });
        }
    }
}
