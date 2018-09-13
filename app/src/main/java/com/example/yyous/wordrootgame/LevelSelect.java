package com.example.yyous.wordrootgame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class LevelSelect extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);
        //initializes buttons for each of the different levels
        Button levelOne =  (Button) findViewById(R.id.levelOneBtn);
        Button levelTwo =  (Button) findViewById(R.id.levelTwoBtn);
        Button levelThree =  (Button) findViewById(R.id.levelThreeBtn);
        Button levelFour =  (Button) findViewById(R.id.levelFourBtn);
        Button levelFive =  (Button) findViewById(R.id.levelFiveBtn);
        Button levelSix =  (Button) findViewById(R.id.levelSixBtn);
        Button levelSeven =  (Button) findViewById(R.id.levelSevenBtn);
        Button levelEight =  (Button) findViewById(R.id.levelEightBtn);
        Button levelNine =  (Button) findViewById(R.id.levelNineBtn);
        Button levelTen =  (Button) findViewById(R.id.levelTen);
        Button levelEleven =  (Button) findViewById(R.id.levelEleven);
        Button levelTwelve =  (Button) findViewById(R.id.levelTwelve);
        Button[] buttons = {levelOne, levelTwo, levelThree, levelFour, levelFive, levelSix, levelSeven, levelEight, levelNine, levelTen, levelEleven, levelTwelve};
        //starts a level activity with a different "access code" that determines what level it is
        for(int i=0; i<12; i++){
            final int access = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent startLevel = new Intent(getApplicationContext(), LevelActivity.class);
                    startLevel.putExtra("levelNumber", access+1);
                    startActivity(startLevel);
                }
            });
        }
    }
}
