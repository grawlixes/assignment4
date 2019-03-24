package com.example.assignment4;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class MatchActivity extends AppCompatActivity {
    GridView board;
    GridViewAdapter boardAdapter;

    String myUsername;
    String theirUsername;
    String challenger;
    String challenged;
    String turn;

    boolean iChallenged;
    boolean myTurn;
    boolean selected = false;

    String lstSource[][] = new String[8][8];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        String boardSerialized = getIntent().getStringExtra("board");
        myUsername = getIntent().getStringExtra("myUsername");
        theirUsername = getIntent().getStringExtra("theirUsername");
        challenger = getIntent().getStringExtra("challenger");
        challenged = getIntent().getStringExtra("challenged");
        turn = getIntent().getStringExtra("turn");

        iChallenged = challenger.equals(myUsername);
        myTurn = (iChallenged == turn.equals("0"));

        TextView turnView = findViewById(R.id.turnView);
        if (turn.equals("0")) {
            turnView.setTextColor(Color.RED);
        } else {
            turnView.setTextColor(Color.BLACK);
        }
        if (myTurn) {
            turnView.setText("Your turn");
        }

        if (iChallenged) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    lstSource[i][j] = String.valueOf(boardSerialized.charAt(2 * (i * 8 + j))) +
                            String.valueOf(boardSerialized.charAt(2 * (i * 8 + j) + 1));
                }
            }
        } else {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    lstSource[i][j] = String.valueOf(boardSerialized.charAt(127 - (2 * (i * 8 + j) + 1)))
                            + String.valueOf(boardSerialized.charAt(127 - (2 * (i * 8 + j))));
                }
            }
        }

        // Update the grid.
        board = findViewById(R.id.board);
        boardAdapter = new GridViewAdapter(lstSource, this, board, turnView, challenger, challenged);
        boardAdapter.setMyTurn(myTurn, iChallenged);
        board.setAdapter(boardAdapter);

        Button back = findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}
