package com.example.myawesomequiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompatSideChannelService;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    private static final String KEY_SCORE = "keyScore";
    private static final String KEY_QUESTION_COUNTER = "keyQuestionCounter";
    private static final String KEY_MILLIS_LEFT = "keyMillisLeft";
    private static final String KEY_ANSWERED = "keyAnswered";
    private static final String KEY_QUESTIONS_LIST = "KeyQuestionsList";

    private TextView textViewdifficulty;
    private TextView textViewCategory;

    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewQuestionCount;
    private TextView textViewCountDown;
    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private Button buttonConfirmNext;
    private static final long COUNTDOWN_IN_MILLIS = 30000;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;


    private ColorStateList textColorDefaultRb;
    private ColorStateList getTextColorDefaultCd;

    private int questionCounter = 0;
    private int questionCountTotal;
    private Question currentQuestion;

    private int score;
    private boolean answered;

    private ArrayList<Question> questionList;

    public static final String EXTRA_SCORE = "extra score";

    private long backPressedTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        textViewdifficulty = findViewById(R.id.text_view_difficulty);
        textViewCategory = findViewById(R.id.text_view_category);

        textViewQuestion = findViewById(R.id.text_view_question);
        textViewScore = findViewById(R.id.text_view_score);
        textViewQuestionCount = findViewById(R.id.text_view_question_count);
        textViewCountDown = findViewById(R.id.text_view_countdown);
        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_button1);
        rb2 = findViewById(R.id.radio_button2);
        rb3 = findViewById(R.id.radio_button3);


        buttonConfirmNext = findViewById(R.id.button_confirm_next);
        textColorDefaultRb = rb1.getTextColors();
        getTextColorDefaultCd = textViewCountDown.getTextColors();

        Intent intent = getIntent();
        String difficulty = intent.getStringExtra(StartingScreenActivity.EXTRA_DIFFICULTY);
        int categoryId = intent.getIntExtra(StartingScreenActivity.EXTRA_CATEGORY_ID,0);
        String categoryName = intent.getStringExtra(StartingScreenActivity.EXTRA_CATEGORY_NAME);
        textViewdifficulty.setText("Difficulty : "+ difficulty);
        textViewCategory.setText("Category : "+ categoryName);

        if (savedInstanceState==null) {

            QuizDbHelper dbHelper = QuizDbHelper.getInstance(this);
            questionList = dbHelper.getQuestion(categoryId,difficulty);

            questionCountTotal = questionList.size();
            Collections.shuffle(questionList);

            showNextQuestion();
        }
        else
        {
            questionList = savedInstanceState.getParcelableArrayList(KEY_QUESTIONS_LIST);
            questionCounter = savedInstanceState.getInt(KEY_QUESTION_COUNTER);
            currentQuestion = questionList.get(questionCounter-1);
            questionCountTotal = questionList.size();
            score = savedInstanceState.getInt(KEY_SCORE);
            answered = savedInstanceState.getBoolean(KEY_ANSWERED);
            timeLeftInMillis = savedInstanceState.getLong(KEY_MILLIS_LEFT);


            if(!answered)
            {
                startCountdown();
            }
            else
            {
                updateCountdownText();
                showSolution();
            }
        }


        buttonConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!answered)
                {
                    if(rb1.isChecked() || rb2.isChecked() || rb3.isChecked())
                    {
                        checkAnswer();
                    }
                    else
                    {
                        Toast.makeText(QuizActivity.this,"Please select an option",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    showNextQuestion(); }
            }
        });



    }

    private void showNextQuestion()
    {

        rb1.setTextColor(textColorDefaultRb);
        rb2.setTextColor(textColorDefaultRb);
        rb3.setTextColor(textColorDefaultRb);
        rbGroup.clearCheck();

        if(questionCounter<questionCountTotal)
        {
            currentQuestion = questionList.get(questionCounter);
            textViewQuestion.setText(currentQuestion.getQuestion());
            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());

            questionCounter++;

            textViewQuestionCount.setText("Question :" + questionCounter + " / " + questionCountTotal);
            buttonConfirmNext.setText("CONFIRM");
            answered = false;

        }else
        {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_SCORE,score);
            setResult(RESULT_OK,intent);
            finishQuiz();
        }

        timeLeftInMillis = COUNTDOWN_IN_MILLIS;
        startCountdown();

    }


    private void startCountdown()
    {
        countDownTimer = new CountDownTimer(timeLeftInMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                timeLeftInMillis = millisUntilFinished;
                updateCountdownText();


            }

            @Override
            public void onFinish() {

                checkAnswer();
            }
        }.start();
    }

    private void updateCountdownText()
    {
        int minutes = (int) (timeLeftInMillis/1000)/60;
        int secounds = (int) (timeLeftInMillis/1000)%60;

        String timeFormat = String.format(Locale.getDefault(),"%02d:%02d",minutes,secounds);

        textViewCountDown.setText(timeFormat);

        if(timeLeftInMillis<10000)
        {
            textViewCountDown.setTextColor(Color.RED);
        }
        else
        {
            textViewCountDown.setTextColor(getTextColorDefaultCd);
        }
    }

    private void checkAnswer()
    {
        answered = true;
        countDownTimer.cancel();

        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        int answerNr = rbGroup.indexOfChild(rbSelected) + 1;

        if (answerNr==currentQuestion.getAnswerNr())
        {
            score++;
            textViewScore.setText("Score : "+ score);
        }
            showSolution();

    }

    private void showSolution()
    {
        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);

        switch(currentQuestion.getAnswerNr())
        {
            case 1:
                rb1.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer 1 is the correct answer");
                break;

                case 2:
                rb2.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer 2 is the correct answer");
                break;

                case 3:
                rb3.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer 3 is the correct answer");
                    break;


        }
        if(questionCounter < questionCountTotal)
        {
            buttonConfirmNext.setText("NEXT");

        }
        else
        {
            buttonConfirmNext.setText("FINISH");
        }

    }

    @Override
    public void onBackPressed() {

        if(backPressedTime+2000 > System.currentTimeMillis())
        {
            finishQuiz();

        }else {
            Toast.makeText(this,"Press back again to finish.",Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }



    private void finishQuiz()
    {
       finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(countDownTimer!=null)
        {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_SCORE,score);
        outState.putInt(KEY_QUESTION_COUNTER,questionCounter);
        outState.putBoolean(KEY_ANSWERED,answered);
        outState.putLong(KEY_MILLIS_LEFT,timeLeftInMillis);
        outState.putParcelableArrayList(KEY_QUESTIONS_LIST,questionList);
    }
}
