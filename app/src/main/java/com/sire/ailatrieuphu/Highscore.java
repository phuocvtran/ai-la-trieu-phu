package com.sire.ailatrieuphu;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Highscore extends AppCompatActivity {
    ListView lvScore;
    Button btnReturn;
    ArrayList<Score> scoreList = new ArrayList<Score>();
    ArrayAdapter<Score> scoreArrayAdapter = null;
    private DatabaseReference scoreRef = FirebaseDatabase.getInstance().getReference().child("Score");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highscore);
        lvScore = findViewById(R.id.lvScore);
        btnReturn = findViewById(R.id.btnReturn);

        // Load điểm
        loadScore();
        // Nút quay lại
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Load điểm
    private void loadScore() {
        scoreRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot scoreSnapshot : dataSnapshot.getChildren()) {
                    scoreList.add(scoreSnapshot.getValue(Score.class));
                }
                //Hiển thị list view PS: F@*K ASYNCHRONOUS
                if(!scoreList.isEmpty()) {
                    scoreArrayAdapter = new ArrayAdapter<>(Highscore.this, android.R.layout.simple_list_item_1, scoreList);
                    lvScore.setAdapter(scoreArrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("LoadScoreFailed", databaseError.toException());
            }
        });
    }

    public void add() {

    }
}