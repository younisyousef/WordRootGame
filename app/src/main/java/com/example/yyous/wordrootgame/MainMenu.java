package com.example.yyous.wordrootgame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
//first screen user is greeted with
public class MainMenu extends AppCompatActivity {
    MediaPlayer player;
    Switch musicSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        //initializes button that automatically sends user to "level one"
        Button startBtn = (Button) findViewById(R.id.startBtn);
        //initializes button that allows user to select any level they want
        Button levelSelect = (Button) findViewById(R.id.lvlSelectBtn);
        //initializes the music player object
        player = MediaPlayer.create(this, R.raw.mp);
        //has song playing loop
        player.setLooping(true);
        //initializes switch to control whether the music plays or not
        musicSwitch = (Switch) findViewById(R.id.musicSwitch);
        //starts the music player
        player.start();
        musicSwitch.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
                //pauses the music if the switch is in the off position
               if(!musicSwitch.isChecked()){
                   player.pause();
               }
               //resumes the player if the music switch is in the on position
               if(musicSwitch.isChecked()){
                   player.start();

               }
           }
       });

        //starts level select screen on button click
        levelSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startLvlSelect = new Intent(getApplicationContext(), LevelSelect.class);
                startLvlSelect.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(startLvlSelect);
            }
        });
        //takes user to first level
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startLevel = new Intent(getApplicationContext(), LevelActivity.class);
                //tells level activity to start on level 1
                startLevel.putExtra("levelNumber", 1);
                //tells level screen whether user  has  the music muted or not
                startLevel.putExtra("musicOn", musicSwitch.isChecked());
                startActivity(startLevel);
            }
        });
    }
    //pauses the music player if the user leaves the app
    public void onPause(){
        super.onPause();
        player.pause();
    }
    //resumes  the music if the user returns to the app
    public void onResume(){
        super.onResume();
        if(musicSwitch.isChecked()) {
            player.start();
        }
    }
}

