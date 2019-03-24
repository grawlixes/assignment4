package com.example.assignment4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static boolean createMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setTitle("Log in");

        // Take user input for login.
        // For my reference: http://www.cs.binghamton.edu/~pmadden/courses/cs441/mysql.pdf
        final EditText inputUsername = findViewById(R.id.inputUsername);
        final EditText inputPassword = findViewById(R.id.inputPassword);

        final Button swapButton = findViewById(R.id.swapButton);
        swapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Button logInButton = findViewById(R.id.logInButton);
                TextView welcome = findViewById(R.id.welcome);
                if (!createMode) {
                    setTitle("Create account");
                    logInButton.setText("CREATE ACCOUNT");
                    welcome.setText("Join the fun.");
                    swapButton.setText("Log in");
                } else {
                    setTitle("Log in");
                    logInButton.setText("LOG IN");
                    welcome.setText("Ready to play?");
                    swapButton.setText("Create account");
                }

                createMode = !createMode;
            }
        });

        final Button logInButton = findViewById(R.id.logInButton);
        logInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try  {
                            String username = inputUsername.getText().toString();
                            String password = inputPassword.getText().toString();

                            HttpURLConnection connect = null;
                            URL url;

                            try {
                                if (createMode) {
                                    url = new URL("https://cs.binghamton.edu/~kfranke1/" +
                                            "create.php");
                                } else {
                                    url = new URL("https://cs.binghamton.edu/~kfranke1/" +
                                            "login.php");
                                }
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

                                // Write arguments: we want to create a new user
                                // with username and password specified by user
                                // Or, if we're logging in, we want to see if the
                                // username and password match
                                StringBuilder arguments = new StringBuilder();
                                arguments.append(
                                        URLEncoder.encode("username", "UTF-8"));
                                arguments.append("=");
                                arguments.append(
                                        URLEncoder.encode(username, "UTF-8"));
                                arguments.append("&");
                                arguments.append(
                                        URLEncoder.encode("password", "UTF-8"));
                                arguments.append("=");
                                arguments.append(
                                        URLEncoder.encode(password, "UTF-8"));
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
                                    TextView welcome = findViewById(R.id.welcome);

                                    if (createMode) {
                                        while (data != null) {
                                            msg.append(data);
                                            data = br.readLine();
                                        }
                                        //Log.d("Debug response", msg.toString());
                                        if (msg.toString().equals("Success")) {
                                            // Let the user move on. The php script
                                            // will take care of making the account.
                                            Intent intent = new Intent(
                                                    MainActivity.this,
                                                    GameActivity.class);
                                            intent.putExtra("wins", "0");
                                            intent.putExtra("losses", "0");
                                            intent.putExtra("username", username);
                                            startActivity(intent);
                                        } else if (msg.toString().equals("Failed")) {
                                            // Account exists already.
                                            welcome.setText("User taken.");
                                        } else {
                                            // Unknown error, probably your connection.
                                            Log.e("Error (PHP)", msg.toString());
                                        }
                                    } else {
                                        if (data.equals("Success")) {
                                            Intent intent = new Intent(
                                                    MainActivity.this,
                                                    GameActivity.class);
                                            data = br.readLine();
                                            intent.putExtra("wins", data);
                                            data = br.readLine();
                                            intent.putExtra("losses", data);
                                            intent.putExtra("username", username);
                                            startActivity(intent);
                                        } else if (data.equals("Failed")) {
                                            welcome.setText("Incorrect user or password.");
                                        } else {
                                            // Unknown error, probably your connection.
                                            Log.e("Error (PHP) unknown", data);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Log.d("Debug Exception", "message " + e);
                                e.printStackTrace();
                            } finally {
                                if (connect != null) {
                                    connect.disconnect();
                                }
                                Log.d("Debug Finished", "Done");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //setTitle(item.getTitle());

        if (id == R.id.nav_profile) {
            // Handle the camera action
        } else if (id == R.id.nav_points) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_overdrive) {

        } else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}