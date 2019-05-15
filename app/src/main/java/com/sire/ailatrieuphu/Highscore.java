package com.sire.ailatrieuphu;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    ListView lsScore;
    Button btnReturn;
    ArrayList<Score> scores = new ArrayList<Score>();
    ArrayAdapter<Score> userArrayAdapter = null;
    private DatabaseReference scoreRef = FirebaseDatabase.getInstance().getReference().child("Score").child("Sire");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highscore);
        lsScore = findViewById(R.id.listScore);
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
                Score score = dataSnapshot.getValue(Score.class);

                Toast.makeText(Highscore.this, score.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
