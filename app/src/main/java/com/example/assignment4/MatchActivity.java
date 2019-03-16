package com.example.assignment4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

public class MatchActivity extends AppCompatActivity {
    GridView board;
    GridViewAdapter boardAdapter;
    String myUsername;
    String theirUsername;
    String lstSource[][] = new String[8][8];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        myUsername = getIntent().getStringExtra("myUsername");
        theirUsername = getIntent().getStringExtra("theirUsername");
        String boardSerialized = getIntent().getStringExtra("board");

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                lstSource[i][j] = String.valueOf(boardSerialized.charAt(2*(i*8 + j))) +
                                  String.valueOf(boardSerialized.charAt(2*(i*8 + j) + 1));
            }
        }

        // Update the grid.
        board = findViewById(R.id.board);
        boardAdapter = new GridViewAdapter(lstSource, this);
        board.setAdapter(boardAdapter);

        Button back = findViewById(R.id.button2);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}
