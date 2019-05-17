package com.sire.ailatrieuphu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PlayActivity extends AppCompatActivity {
    RadioButton optionA, optionB, optionC, optionD;
    RadioGroup optionGroup;
    TextView tvTimer, tvQuestion, tvScore;
    Button btnSwap, btnAsk, btnConfirm, btn50;
    User user;
    CountDownTimer timer;

    MediaPlayer playSound;
    int questionID = 1;
    String[] reward = {"0", "200", "400", "600", "1000", "2000", "3000", "6000", "10000", "14000", "22000", "30000", "40000", "60000", "85000", "100000"};
    int correctAnswer = 0;

    private DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("Questions");

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

        // Hiển thị và cập nhât câu hỏi
        showQuestion();
    }

    // Hiển thị và cập nhât câu hỏi
    private void showQuestion() {
        optionGroup.clearCheck();
        DatabaseReference questionRef = dataRef.child(String.valueOf(questionID));

        questionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Question question = dataSnapshot.getValue(Question.class);
                tvQuestion.setText(question.getQuestion());
                optionA.setText(question.getOption1());
                optionB.setText(question.getOption2());
                optionC.setText(question.getOption3());
                optionD.setText(question.getOption4());
                tvScore.setText("Số Tiền Hiện Tại: " + reward[correctAnswer]);

                // Kiểm tra đáp án
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkAnswer(question);
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
    private void checkAnswer(Question question) {
        RadioButton selectedRadioButton = findViewById(optionGroup.getCheckedRadioButtonId());
        String answer = selectedRadioButton.getText().toString();
        if(answer.equals(question.getAnswer())) {
            if(correctAnswer < 14) {
                playSound = MediaPlayer.create(PlayActivity.this, R.raw.dung);
                playSound.start();

                AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Chúc mừng");
                builder.setMessage("Một Câu Trả Lời Chính Xác");
                AlertDialog dialog = builder.create();
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Câu Tiếp Theo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showQuestion();
                        questionID++;
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        }
    }
}
