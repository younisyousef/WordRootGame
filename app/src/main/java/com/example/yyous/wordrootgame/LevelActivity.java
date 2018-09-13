package com.example.yyous.wordrootgame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class LevelActivity extends AppCompatActivity {
    CountDownTimer countDownTimer;
    ArrayList<String> possibleAnswers;
    MediaPlayer mp;
    MyRecyclerViewAdapter adapter;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    TextView personalBest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //pans the entire screen down when user clicks on soft keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        //initializes rating system element
        final RatingBar ratingBar = findViewById(R.id.ratingBar);
        //creates a shared preferences object to store key information like high scores for each level
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        //initializes text view element that holds the user's high score
        personalBest = (TextView) findViewById(R.id.personalBestTextView);
        final int levelNumber = getIntent().getIntExtra("levelNumber", 0 );
        //sets high score for level if there is one
        personalBest.setText(sharedPref.getString("pB" + levelNumber, "Best: 0 ★"));
        final TextView wordRoot = (TextView) findViewById(R.id.wordRootTextView);
        final TextView countdownTimer = (TextView) findViewById(R.id.countDownTextView);
        Button answerButton = (Button) findViewById(R.id.answerBtn);
        final RecyclerView listOfWords = (RecyclerView) findViewById(R.id.recyclerView);
        mp = new MediaPlayer().create(this, R.raw.jazz);
        mp.setLooping(true);
        //checks if music switch was turned on or off in the main menu activity and plays music accordingly
        if(getIntent().getBooleanExtra("musicOn", true)){
            mp.start();
        }
        else{
            mp.pause();
        }
        //array of all possible roots for each level
        String[] possibleRoots = {"-SHIP", "MULTI-", "MICRO-", "DI-", "MACRO-", "IM-", "HYDR-", "INTER-", "TRANS-", "THERM-", "ASTRO-", "AUTO-"};
        possibleAnswers = new ArrayList<>();
        listOfWords.setHasFixedSize(true);
        listOfWords.setLayoutManager(new LinearLayoutManager(this));
        //creates a recycler view with an array of user inputted answers
        adapter = new MyRecyclerViewAdapter(this, possibleAnswers);
        listOfWords.setAdapter(adapter);
        //uses level number provided to set the  current word root for the level
        for (int i = 0; i<possibleRoots.length; i++){
            if(i+1==levelNumber){
                wordRoot.setText(possibleRoots[levelNumber - 1]);
            }
        }
        //creates a 60 second countdown timer
        countDownTimer = new CountDownTimer(60000, 1000) {
            int num = levelNumber;
            //changes the countdown timer text view to reflect what's happening visually
            public void onTick(long millisUntilFinished) {
                countdownTimer.setText("" +millisUntilFinished / 1000);
            }

            public void onFinish() {
                //if its the last level in the array, takes user back to the main menu
                if(num==12){
                    Intent mainMenu = new Intent(getApplicationContext(), MainMenu.class);
                    mainMenu.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(mainMenu);
                }
                //has the user redo the level if they dont get at least one star
                else if(ratingBar.getRating()<1){
                    Toast toast = Toast.makeText(getApplicationContext(), "Failed level!", Toast.LENGTH_SHORT);
                    toast.show();
                    startActivity(getIntent());

                }
                //has the user move on to the next level if they pass
                else {
                    Intent intent = getIntent();
                    intent.putExtra("levelNumber", num + 1);
                    startActivity(intent);
                }
                //calls the finish method so the user cant return to a completed level
                finish();
            }
        }.start();

        answerButton.setOnClickListener(new View.OnClickListener() {
            EditText userInput = (EditText) findViewById(R.id.answerEditText);
            RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
            int numWordsCorrect = 0;
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            TextView personalBest = (TextView) findViewById(R.id.personalBestTextView);
            //array list that stores correct user inputted answers
            ArrayList<String> store = new ArrayList<>();
            String wordStart;
            @Override
            public void onClick(View view) {

                InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                //hides soft keyboard after submit button is clicked
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                //stores user input as a string
                String answer = userInput.getText().toString();
                //resets the text view  to an empty string
                userInput.setText("");
                //removes the hyphen from the beginning or end of the word root
                wordStart = wordRoot.getText().toString().toLowerCase();
                if(wordStart.substring(0,1).equals("-")){
                    wordStart = wordStart.substring(1);
                }
                else{
                    wordStart = wordStart.substring(0, wordStart.length()-1);
                }
                //adds divider line between each item in the recycler view
                listOfWords.addItemDecoration(new DividerItemDecoration(listOfWords.getContext(), DividerItemDecoration.VERTICAL));
                //checks if the user's answer contains the word root and is an actual word
                if(answer.contains(wordStart) && contains(answer)){
                    //checks if the user has used this answer before
                    if(!store.contains(answer)) {
                        store.add(answer);
                        //adds answer to recyler view  with a checkmark to show its correct
                        possibleAnswers.add(answer + "✔");
                        //updates how many words the user has gotten correct
                        numWordsCorrect++;
                    }
                    else {
                        possibleAnswers.add(answer + "✘");
                    }
                    //updates the recycler view with new word
                    adapter.notifyItemInserted(possibleAnswers.size() - 1);
                }
                //does nothing if the user clicks submit before typing anything
                else if(answer.equals("")){ }
                else{
                    //shows the user the answer is incorrect if does not contain the word root or if it is not a valid word
                    possibleAnswers.add(answer + "✘");
                    adapter.notifyItemInserted(possibleAnswers.size() - 1);
                }

                if(numWordsCorrect==3 ){
                    //gives the user one star if they get 3 words correct
                    ratingBar.setRating(1);
                    //checks to see if a higher high score is  already saved in shared preferences
                    if( (!sharedPref.getString("pB" + levelNumber, "Best: 0 ★").equals("Best: 2 ★")&&
                            !sharedPref.getString("pB" + levelNumber, "Best: 0 ★").equals("Best: 3 ★"))) {
                        //saves the highest score as one star for the level
                        editor.putString("pB" + levelNumber, "Best: 1 ★");
                    }
                }
                else if(numWordsCorrect==6 ){
                    //gives the user 2 stars if they get 6 words correct
                    ratingBar.setRating(2);
                    //checks to see if the   user has  already achieved a higher score on this level
                    if(!sharedPref.getString("pB" + levelNumber, "Best: 0 ★").equals("Best: 3 ★")) {
                        //saves the highest score as two stars for the level
                        editor.putString("pB" + levelNumber, "Best: 2 ★");
                    }
                }
                else if(numWordsCorrect==9){
                    //gives the user 3 stars if they get 9 words
                    ratingBar.setRating(3);
                    //saves the highest score as 3  stars for the level
                    editor.putString("pB" + levelNumber, "Best: 3 ★");

                }
                //commits the saved changes
                editor.commit();



            }
        });

    }
    //method that returns true or false if the passed word is a valid word
    public boolean contains(String word) {
        try {
            //buffered reader that reads a file with over 500,000 words from the english dictionary
            BufferedReader in = new BufferedReader(new InputStreamReader(getAssets().open("words.txt")));
            String str;
            //goes through the entire text file, line by line, until reaching the end of  the file
            while ((str = in.readLine()) != null) {
                //checks line by line whether the text file contains the passed  word
                if (str.contains(word)) {
                    return true;
                }
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
    //clears the activity and pauses the music if the user leaves the activity
    public void onPause() {
        super.onPause();
        possibleAnswers.clear();
        adapter.notifyDataSetChanged();
        mp.pause();
        countDownTimer.cancel();
    }
    //restarts the music and the starts the countdown timer
    public void onResume(){
        super.onResume();
        mp.start();
        countDownTimer.start();

    }

}
