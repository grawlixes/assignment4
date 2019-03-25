package com.example.assignment4;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.os.Handler;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

public class MatchActivity extends AppCompatActivity {
    GridView board;
    GridViewAdapter boardAdapter;

    String myUsername;
    String theirUsername;
    String challenger;
    String challenged;
    String turn;
    String boardSerialized;

    boolean iChallenged;
    boolean myTurn;

    String lstSource[][] = new String[8][8];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        boardSerialized = getIntent().getStringExtra("board");
        myUsername = getIntent().getStringExtra("myUsername");
        theirUsername = getIntent().getStringExtra("theirUsername");
        challenger = getIntent().getStringExtra("challenger");
        challenged = getIntent().getStringExtra("challenged");
        turn = getIntent().getStringExtra("turn");

        iChallenged = challenger.equals(myUsername);
        myTurn = (iChallenged == turn.equals("0"));

        final TextView turnView = findViewById(R.id.turnView);
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

        final Handler handler = new Handler();
        Timer timer = new Timer(false);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!myTurn) {
                            Thread thread = new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    HttpURLConnection connect = null;
                                    try {
                                        URL url = new URL("https://cs.binghamton.edu/~kfranke1/" +
                                                "check.php");

                                        connect = (HttpURLConnection) url
                                                .openConnection();
                                        Log.d("Debug", "Start");

                                        connect.setReadTimeout(15000);
                                        connect.setConnectTimeout(15000);
                                        connect.setRequestMethod("POST");
                                        connect.setDoInput(true);
                                        connect.setDoOutput(true);

                                        OutputStream os = connect.getOutputStream();
                                        BufferedWriter bw = new BufferedWriter(
                                                new OutputStreamWriter(os, "UTF-8"));

                                        String challenger = myUsername;
                                        String challenged = theirUsername;

                                        if (!iChallenged) {
                                            challenger = theirUsername;
                                            challenged = myUsername;
                                        }

                                        StringBuilder arguments = new StringBuilder();
                                        arguments.append(
                                                URLEncoder.encode("challenger", "UTF-8"));
                                        arguments.append("=");
                                        arguments.append(
                                                URLEncoder.encode(challenger, "UTF-8"));
                                        arguments.append("&");
                                        arguments.append(
                                                URLEncoder.encode("challenged", "UTF-8"));
                                        arguments.append("=");
                                        arguments.append(
                                                URLEncoder.encode(challenged, "UTF-8"));

                                        bw.write(arguments.toString());
                                        bw.flush();
                                        bw.close();

                                        int responseCode = connect.getResponseCode();
                                        Log.d("Debug response: ", String.valueOf(responseCode));

                                        if (responseCode == HttpsURLConnection.HTTP_OK) {
                                            InputStream is = connect.getInputStream();
                                            BufferedReader br = new BufferedReader(
                                                    new InputStreamReader(is, "UTF-8"));
                                            String data = br.readLine();

                                            if (!data.equals(boardSerialized)) {
                                                Log.d("Data", data);
                                                Log.d("BS", boardSerialized);
                                                boardSerialized = data;
                                                Log.d("BS", boardSerialized);


                                                if (iChallenged) {
                                                    for (int i = 0; i < 8; i++) {
                                                        for (int j = 0; j < 8; j++) {
                                                            lstSource[i][j] = String.valueOf(
                                                                    boardSerialized.charAt(2 * (i * 8 + j))) +
                                                                    String.valueOf(
                                                                            boardSerialized.charAt(2 * (i * 8 + j) + 1));
                                                        }
                                                    }
                                                } else {
                                                    for (int i = 0; i < 8; i++) {
                                                        for (int j = 0; j < 8; j++) {
                                                            lstSource[i][j] = String.valueOf(
                                                                    boardSerialized.charAt(127 - (2 * (i * 8 + j) + 1)))
                                                                    + String.valueOf(
                                                                    boardSerialized.charAt(127 - (2 * (i * 8 + j))));
                                                        }
                                                    }
                                                }

                                                boardAdapter.setMyTurn(true, iChallenged);
                                                boardAdapter.updateGrid(lstSource);
                                                myTurn = true;
                                                turnView.setText("My turn");
                                                if (iChallenged) {
                                                    turnView.setTextColor(Color.RED);
                                                } else {
                                                    turnView.setTextColor(Color.BLACK);
                                                }
                                            } else {
                                                Log.d("Unchanged", "data");
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } finally {
                                        if (connect != null) {
                                            connect.disconnect();
                                        }
                                    }
                                }
                            });
                            thread.start();

                            boardAdapter.notifyDataSetChanged();
                            board.setAdapter(boardAdapter);
                        } else {
                            // Not your turn.
                            Thread thread = new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    HttpURLConnection connect = null;
                                    try {
                                        URL url = new URL("https://cs.binghamton.edu/~kfranke1/" +
                                                "check.php");

                                        connect = (HttpURLConnection) url
                                                .openConnection();
                                        Log.d("Debug", "Start");

                                        connect.setReadTimeout(15000);
                                        connect.setConnectTimeout(15000);
                                        connect.setRequestMethod("POST");
                                        connect.setDoInput(true);
                                        connect.setDoOutput(true);

                                        OutputStream os = connect.getOutputStream();
                                        BufferedWriter bw = new BufferedWriter(
                                                new OutputStreamWriter(os, "UTF-8"));

                                        String challenger = myUsername;
                                        String challenged = theirUsername;

                                        if (!iChallenged) {
                                            challenger = theirUsername;
                                            challenged = myUsername;
                                        }

                                        StringBuilder arguments = new StringBuilder();
                                        arguments.append(
                                                URLEncoder.encode("challenger", "UTF-8"));
                                        arguments.append("=");
                                        arguments.append(
                                                URLEncoder.encode(challenger, "UTF-8"));
                                        arguments.append("&");
                                        arguments.append(
                                                URLEncoder.encode("challenged", "UTF-8"));
                                        arguments.append("=");
                                        arguments.append(
                                                URLEncoder.encode(challenged, "UTF-8"));

                                        bw.write(arguments.toString());
                                        bw.flush();
                                        bw.close();

                                        int responseCode = connect.getResponseCode();
                                        Log.d("Debug response: ", String.valueOf(responseCode));

                                        if (responseCode == HttpsURLConnection.HTTP_OK) {
                                            InputStream is = connect.getInputStream();
                                            BufferedReader br = new BufferedReader(
                                                    new InputStreamReader(is, "UTF-8"));
                                            br.readLine();
                                            String data = br.readLine();
                                            String expected = iChallenged ? "0" : "1";

                                            if (!data.equals(expected)) {
                                                myTurn = false;
                                                boardAdapter.setMyTurn(false, iChallenged);
                                                turnView.setText("Their turn");
                                                if (iChallenged) {
                                                    turnView.setTextColor(Color.BLACK);
                                                } else {
                                                    turnView.setTextColor(Color.RED);
                                                }
                                            } else {
                                                Log.d("Expected", "it's my turn");
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } finally {
                                        if (connect != null) {
                                            connect.disconnect();
                                        }
                                    }
                                }
                            });
                            thread.start();
                        }
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 2000, 2000);
    }
}
