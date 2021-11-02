package com.example.myawesomequiz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.style.SuperscriptSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class StartingScreenActivity extends AppCompatActivity {

    public static final String EXTRA_DIFFICULTY = "extraDifficulty";
    public static final String EXTRA_CATEGORY_ID = "extraCategoryId";
    public static final String EXTRA_CATEGORY_NAME = "extraCategoryName";


    private static int SCORE_RESULT_ACTIVITY;
    private static final int REQUEST_CODE_QUIZ = 1;



    private TextView highScoreView;

    private static int highScore=0;
    public static final String KEY_HIGH_SCORE = "key highscore";
    public static final String SHARED_PREFS = "shared prefences";


    private Spinner spinnerDifficulty;
    private Spinner spinnerCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_screen);
        Button buttonStartQuiz = findViewById(R.id.button_start_quiz);
        highScoreView = findViewById(R.id.text_view_highscore);
        spinnerDifficulty = findViewById(R.id.spinner_difficulty);
        spinnerCategory = findViewById(R.id.spinner_category);

        loadCategory();
        loadDifficulty();

        loadHighscore();

        buttonStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuiz();
            }
        });
    }
    private void startQuiz() {

        Category category = (Category) spinnerCategory.getSelectedItem();
        String difficulty = spinnerDifficulty.getSelectedItem().toString();
        Intent intent = new Intent(StartingScreenActivity.this, QuizActivity.class);
        intent.putExtra(EXTRA_DIFFICULTY,difficulty);
        intent.putExtra(EXTRA_CATEGORY_NAME,category.getName());
       intent.putExtra(EXTRA_CATEGORY_ID,category.getId());
        startActivityForResult(intent,REQUEST_CODE_QUIZ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==REQUEST_CODE_QUIZ)
        {
            if(resultCode==RESULT_OK) {

                SCORE_RESULT_ACTIVITY = data.getIntExtra(QuizActivity.EXTRA_SCORE, 0);
            }
        }

        if(SCORE_RESULT_ACTIVITY>highScore) {
            updatedHighscore(SCORE_RESULT_ACTIVITY);
        }

    }

    private void updatedHighscore(int updatedHighscore)
    {
        highScore = updatedHighscore;
        highScoreView.setText("Highscore : "+ highScore);

        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_HIGH_SCORE,highScore);
        editor.apply();

    }

    private void loadHighscore()
    {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        highScore = prefs.getInt(KEY_HIGH_SCORE,0);
        highScoreView.setText("Highscore : "+ highScore);
    }

    private void loadCategory()
    {
        QuizDbHelper quizDbHelper = QuizDbHelper.getInstance(this);
        List<Category> categories = quizDbHelper.getAllCategory();

        ArrayAdapter<Category> adapterCategory = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_item,categories);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterCategory);

    }

    private void loadDifficulty()
    {

        String[] difficultyLevels = Question.getAllDifficultyLevels();

        ArrayAdapter<String> adapterDifficulty = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,difficultyLevels);
        adapterDifficulty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(adapterDifficulty);
    }


}