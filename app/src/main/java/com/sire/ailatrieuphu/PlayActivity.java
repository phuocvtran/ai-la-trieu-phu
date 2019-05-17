package com.sire.ailatrieuphu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class PlayActivity extends AppCompatActivity {
    RadioButton optionA, optionB, optionC, optionD;
    RadioGroup optionGroup;
    TextView tvTimer, tvQuestion, tvScore;
    Button btnSwap, btnAsk, btnConfirm, btn50;
    User user;
    CountDownTimer timer;

    MediaPlayer playSound;
    String[] reward = {"0", "200", "400", "600", "1000", "2000", "3000", "6000", "10000", "14000", "22000", "30000", "40000", "60000", "85000", "100000"};
    ArrayList<String> questionsId = new ArrayList<>();
    int correctAnswer = 0;

    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    private long sumQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        optionGroup = findViewById(R.id.rdGroup);
        optionA = findViewById(R.id.rdbA);
        optionB = findViewById(R.id.rdbB);
        optionC = findViewById(R.id.rdbC);
        optionD = findViewById(R.id.rdbD);
        tvTimer = findViewById(R.id.tvTimer);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvScore = findViewById(R.id.tvScore);
        btnSwap = findViewById(R.id.btnSwap);
        btnAsk = findViewById(R.id.btnAsk);
        btn50 = findViewById(R.id.btn50);
        btnConfirm = findViewById(R.id.btnConfirm);

        user = new User();
        Bundle bd = getIntent().getExtras();
        if(bd != null) {
            String  strUsername = bd.getString("strUsername");
            user.setUser(strUsername);
        }

        // Lấy câu hỏi
        getQuestionFromDatabase();
    }

    // Lấy câu hỏi
    private void getQuestionFromDatabase() {
        optionGroup.clearCheck();
        final DatabaseReference questionRef = databaseRef.child("Questions");
        questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sumQuestion = dataSnapshot.getChildrenCount();
                Log.d("ALLQUESTIONS", String.valueOf(sumQuestion));
                // Lấy 15 câu hỏi random
                getRandomIds(1, sumQuestion);
                Log.d("ALLQUESTIONS", String.valueOf(questionsId));
                showQuestion(questionRef, questionsId.get(correctAnswer));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        /**/
    }

    // Lấy 15 câu hỏi Random lưu vào questionsId
    private void getRandomIds(long min, long max) {
        HashSet hs = new HashSet();
        while(hs.size()<15) {
            String result = String.valueOf((long)(Math.random() * max) + 1);
            hs.add(result);
        }

        Iterator it = hs.iterator();
        while(it.hasNext()){
            questionsId.add(it.next().toString());
        }
    }

    // Hiển thị và cập nhật câu hỏi
    private void showQuestion(final DatabaseReference questionRef, String id) {
        DatabaseReference idRef = questionRef.child(id);
        idRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Question question = dataSnapshot.getValue(Question.class);
                tvQuestion.setText(question.getQuestion());
                optionA.setText(question.getOption1());
                optionB.setText(question.getOption2());
                optionC.setText(question.getOption3());
                optionD.setText(question.getOption4());
                tvScore.setText("Số Điểm Hiện Tại: " + reward[correctAnswer]);

                // Kiểm tra đáp án
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkAnswer(questionRef, question);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("LoadQuestionFailed", databaseError.toException());
            }
        });
    }
    // Kiểm tra đáp án
    private void checkAnswer(DatabaseReference questionRef, Question question) {
        RadioButton selectedRadioButton = findViewById(optionGroup.getCheckedRadioButtonId());
        String answer = selectedRadioButton.getText().toString();
        if(answer.equals(question.getAnswer())) {
            // Xử lý khi đúng
            isCorrect(questionRef);
        }
        else {
            // Xử lý khi sai
            isWrong(questionRef);
        }
    }

    // Xử lý khi đúng
    private void isCorrect(final DatabaseReference questionRef) {
        if(correctAnswer < 14) {
            playSound = MediaPlayer.create(PlayActivity.this, R.raw.dung);
            playSound.start();
            correctAnswer++;
            Log.d("CORRECT_ANSWER", String.valueOf(correctAnswer));

            AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Chúc mừng");
            builder.setMessage("Một Câu Trả Lời Chính Xác");
            AlertDialog dialog = builder.create();
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Câu Tiếp Theo", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tvScore.setText("Số Điểm Đạt Được: " + reward[correctAnswer]);
                    showQuestion(questionRef, questionsId.get(correctAnswer));
                    dialog.cancel();
                }
            });
            dialog.show();
        }
        else {
            AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Chiến Thắng");
            builder.setMessage("Bạn Đã Kiếm Được: " + reward[15]);
            AlertDialog dialog = builder.create();
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    timer.cancel();
                    // Xử lý lưu điểm
                    user.setScore(reward[15]);
                    saveHighscore();

                    // Trở lại menu
                    playSound.stop();
                    finish();
                }
            });
            dialog.show();
        }
    }

    // Xử lý khi sai
    private void isWrong(DatabaseReference questionRef) {
        playSound = MediaPlayer.create(PlayActivity.this, R.raw.sai);
        playSound.start();

        AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Sai Rồi");
        builder.setMessage("Thật Đáng Tiếc!\nSố Điểm Đạt Được: " + reward[correctAnswer]);
        AlertDialog dialog = builder.create();
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Trở lại", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Xử lý lưu điểm
                user.setScore(reward[correctAnswer]);
                saveHighscore();

                // Trở lại menu
                playSound.stop();
                finish();
            }
        });
        dialog.show();
    }

    // Xử lý lưu điểm
    private void saveHighscore() {

    }
}
