package com.example.zlc.finallyclock;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;

/**
 * Created by ZLC on 2016/8/27.
 */
public class PlayAlarmAty extends Activity {

    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_player_aty);
        mp = MediaPlayer.create(this,R.raw.music);
        mp.start();
        Button button =(Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.stop();
                Intent intent = new Intent(PlayAlarmAty.this,WelActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onPause(){
        super.onPause();

        finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        mp.stop();
        mp.release();
    }


}
