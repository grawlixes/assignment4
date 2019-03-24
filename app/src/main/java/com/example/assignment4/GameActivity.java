package com.example.assignment4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import javax.net.ssl.HttpsURLConnection;

public class GameActivity extends AppCompatActivity {
    static int wins;
    static int losses;
    static String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        wins = Integer.parseInt(getIntent().getStringExtra("wins"));
        losses = Integer.parseInt(getIntent().getStringExtra("losses"));
        username = getIntent().getStringExtra("username");

        Button logOutButton = findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // Look for the user and start the game.
                        EditText userField = findViewById(R.id.findUser);
                        String theirUsername = userField.getText().toString();
                        TextView question = findViewById(R.id.question);

                        if (username.equals(theirUsername)) {
                            question.setText("You can't challenge yourself. Idiot.");
                            return;
                        }

                        HttpURLConnection connect = null;
                        try {
                            URL url = new URL("https://cs.binghamton.edu/~kfranke1/" +
                                    "find.php");

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

                            StringBuilder arguments = new StringBuilder();
                            arguments.append(
                                    URLEncoder.encode("theirUsername", "UTF-8"));
                            arguments.append("=");
                            arguments.append(
                                    URLEncoder.encode(theirUsername, "UTF-8"));
                            arguments.append("&");
                            arguments.append(
                                    URLEncoder.encode("myUsername", "UTF-8"));
                            arguments.append("=");
                            arguments.append(
                                    URLEncoder.encode(username, "UTF-8"));

                            bw.write(arguments.toString());
                            bw.flush();
                            bw.close();

                            int responseCode = connect.getResponseCode();
                            Log.d("Debug response: ", String.valueOf(responseCode));

                            if (responseCode == HttpsURLConnection.HTTP_OK) {
                                InputStream is = connect.getInputStream();
                                BufferedReader br = new BufferedReader(
                                        new InputStreamReader(is, "UTF-8"));
                                StringBuilder msg = new StringBuilder();
                                String data = br.readLine();

                                if (data.equals("Success")) {
                                    String board = br.readLine();
                                    String challenger = br.readLine();
                                    String challenged = br.readLine();
                                    String turn = br.readLine();
                                    Log.d("Board", board);
                                    Intent intent = new Intent(
                                            GameActivity.this,
                                            MatchActivity.class);
                                    intent.putExtra("myUsername", username);
                                    intent.putExtra("theirUsername", theirUsername);
                                    intent.putExtra("board", board);
                                    intent.putExtra("challenger", challenger);
                                    intent.putExtra("challenged", challenged);
                                    intent.putExtra("turn", turn);
                                    startActivity(intent);
                                } else if (data.equals("Failed")) {
                                    question.setText("User not found.");
                                } else {
                                    Log.d("Data unknown", data);
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
        });
    }
}
