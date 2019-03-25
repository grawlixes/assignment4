package com.example.assignment4;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

// I took most of this from my last assignment in Android.
public class GridViewAdapter extends BaseAdapter {
    // Holds the grid.
    private String[][] lstSource;
    // Unique Adapter context.
    private final Context m_context;
    // Have we clicked something yet?
    // If so, make it that position.
    private int clicked = -1;
    // Button that was last clicked,
    // if it exists.
    private Button lastClicked = null;
    // Is it our turn?
    private boolean myTurn = false;
    // Are we red?
    private boolean isRed;
    // GridView in question
    private GridView gv;
    // Shows whose turn it is
    private TextView turnView;
    private String challenger;
    private String challenged;

    private static final int NUM_ELEMENTS = 64;

    GridViewAdapter(String[][] lstSource, Context m_context, GridView gv, TextView turnView,
                    String challenger, String challenged) {
        this.lstSource = lstSource;
        this.m_context = m_context;
        this.gv = gv;
        this.turnView = turnView;
        this.challenger = challenger;
        this.challenged = challenged;
    }

    // The count is always 16. This is the constant total block number
    // used by the Adapter interface, NOT the number of non-empty blocks.
    // Use available_spaces for the total number of empty blocks.
    @Override
    public int getCount() {
        return NUM_ELEMENTS;
    }

    /* Positioning system:
        1  2  3  4  5  6  7  8
        9  10 11 12 13 14 15 16
        ...
     */
    @Override
    public Object getItem(int position) {
        return lstSource[position/8][position%8];
    }

    // Thanks to the above system, we don't need to change the way
    // that the getItemId function works - just give what you have.
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convert_view, ViewGroup parent) {
        final Button button;
        if (convert_view == null) {
            button = new Button(m_context);
            button.setLayoutParams(new GridView.LayoutParams(150,150));
            button.setPadding(0,0,0,0);
            if (((position % 2) == 1) ^ (((position / 8) % 2) == 1)) {
                button.setBackgroundColor(0xFFFFFFFF);
            } else {
                button.setBackgroundColor(0xFF000000);
            }

            button.setText("");
            if ((getItem(position)).equals("rn")) {
                button.setBackground(ContextCompat
                        .getDrawable(this.m_context, R.drawable.redpiece));
            } else if ((getItem(position)).equals("bn")) {
                button.setBackground(ContextCompat
                        .getDrawable(this.m_context, R.drawable.blackpiece));
            } else if ((getItem(position)).equals("rk")) {
                button.setBackground(ContextCompat
                        .getDrawable(this.m_context, R.drawable.redpiece));
                button.setText("K");
                button.setTextColor(Color.WHITE);
            } else if ((getItem(position)).equals("bk")) {
                button.setBackground(ContextCompat
                        .getDrawable(this.m_context, R.drawable.blackpiece));
                button.setText("K");
                button.setTextColor(Color.WHITE);
            }

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (myTurn) {
                        if (clicked != -1) {
                            // Check if the move is valid.
                            int i = position / 8;
                            int j = position % 8;
                            String item = (String) getItem(clicked);
                            String item2 = (String) getItem(position);

                            boolean jump = false;
                            boolean jump2 = false;
                            boolean jump3 = false;
                            boolean jump4 = false;

                            if (clicked >= 9)
                                jump = (((String) getItem(clicked-9)).charAt(0) == 'r'
                                        && !isRed) || (((String) getItem(clicked-9))
                                        .charAt(0) == 'b'
                                        && isRed);
                            if (clicked >= 7)
                                jump2 = (((String) getItem(clicked-7)).charAt(0) == 'r'
                                        && !isRed) || (((String) getItem(clicked-7))
                                        .charAt(0) == 'b'
                                        && isRed);
                            if (clicked+7 < 64)
                                jump3 = (((String) getItem(clicked+7)).charAt(0) == 'r'
                                        && !isRed) || (((String) getItem(clicked+7))
                                        .charAt(0) == 'b'
                                        && isRed);
                            if (clicked+9 < 64)
                                jump4 = (((String) getItem(clicked+9)).charAt(0) == 'r'
                                        && !isRed) || (((String) getItem(clicked+9))
                                        .charAt(0) == 'b'
                                        && isRed);

                            if (((item.charAt(0) == 'r' && isRed) ||
                                (item.charAt(0) == 'b' && !isRed)) &&
                                (item2.equals("na"))) {
                                if (position == clicked-9 ||
                                    position == clicked-7 ||
                                    (item.charAt(1) == 'k' &&
                                     (position == clicked+9 ||
                                      position == clicked+7))) {

                                    int lastI = clicked / 8;
                                    int lastJ = clicked % 8;

                                    lstSource[lastI][lastJ] = "na";
                                    lstSource[i][j] = item;
                                    if (i == 0 || i == 7) {
                                        lstSource[i][j] = ((isRed) ? "r" : "b") + "k";
                                    }

                                    GridViewAdapter.this.notifyDataSetChanged();
                                    gv.setAdapter(GridViewAdapter.this);

                                    turnView.setText("Their turn");
                                    if (isRed) {
                                        turnView.setTextColor(Color.BLACK);
                                    } else {
                                        turnView.setTextColor(Color.RED);
                                    } myTurn = false;

                                    final StringBuilder newBoard = new StringBuilder();
                                    if (isRed) {
                                        for (i = 0; i < 8; i++) {
                                            for (j = 0; j < 8; j++) {
                                                newBoard.append(lstSource[i][j]);
                                            }
                                        }
                                    } else {
                                        for (i = 0; i < 8; i++) {
                                            for (j = 0; j < 8; j++) {
                                                newBoard.append(lstSource[7-i][7-j]);
                                            }
                                        }
                                    }
                                    final String newTurn = (isRed) ? "1" : "0";

                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            HttpURLConnection connect = null;
                                            try {
                                                URL url = new URL("https://cs.binghamton.edu/~kfranke1/" +
                                                        "update.php");

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
                                                arguments.append("&");
                                                arguments.append(
                                                        URLEncoder.encode("newBoard", "UTF-8"));
                                                arguments.append("=");
                                                arguments.append(
                                                        URLEncoder.encode(newBoard.toString(), "UTF-8"));
                                                arguments.append("&");
                                                arguments.append(
                                                        URLEncoder.encode("newTurn", "UTF-8"));
                                                arguments.append("=");
                                                arguments.append(
                                                        URLEncoder.encode(newTurn, "UTF-8"));

                                                bw.write(arguments.toString());
                                                bw.flush();
                                                bw.close();

                                                int responseCode = connect.getResponseCode();
                                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                                    InputStream is = connect.getInputStream();
                                                    BufferedReader br = new BufferedReader(
                                                            new InputStreamReader(is, "UTF-8"));
                                                    StringBuilder msg = new StringBuilder();
                                                    String data = br.readLine();

                                                    while (data != null) {
                                                        Log.d("data", data);
                                                        data = br.readLine();
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

                                } else if ((position == clicked-18 && jump) ||
                                           (position == clicked-14 && jump2) ||
                                           (item.charAt(1) == 'k' &&
                                            ((position == clicked+14 && jump3) ||
                                             (position == clicked+18 && jump4)))) {
                                    int lastI = clicked / 8;
                                    int lastJ = clicked % 8;

                                    lstSource[lastI][lastJ] = "na";
                                    lstSource[i][j] = item;
                                    if (i == 0 || i == 7) {
                                        lstSource[i][j] = ((isRed) ? "r" : "b") + "k";
                                    }

                                    if (position == clicked-18) {
                                        lstSource[lastI-1][lastJ-1] = "na";
                                    } else if (position == clicked-14) {
                                        lstSource[lastI-1][lastJ+1] = "na";
                                    } else if (position == clicked+14) {
                                        lstSource[lastI+1][lastJ-1] = "na";
                                    } else {
                                        lstSource[lastI+1][lastJ+1] = "na";
                                    }

                                    GridViewAdapter.this.notifyDataSetChanged();
                                    gv.setAdapter(GridViewAdapter.this);

                                    turnView.setText("Their turn");
                                    if (isRed) {
                                        turnView.setTextColor(Color.BLACK);
                                    } else {
                                        turnView.setTextColor(Color.RED);
                                    } myTurn = false;

                                    final StringBuilder newBoard = new StringBuilder();
                                    if (isRed) {
                                        for (i = 0; i < 8; i++) {
                                            for (j = 0; j < 8; j++) {
                                                newBoard.append(lstSource[i][j]);
                                            }
                                        }
                                    } else {
                                        for (i = 0; i < 8; i++) {
                                            for (j = 0; j < 8; j++) {
                                                newBoard.append(lstSource[7-i][7-j]);
                                            }
                                        }
                                    }
                                    final String newTurn = (isRed) ? "1" : "0";

                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            HttpURLConnection connect = null;
                                            try {
                                                URL url = new URL("https://cs.binghamton.edu/~kfranke1/" +
                                                        "update.php");

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
                                                arguments.append("&");
                                                arguments.append(
                                                        URLEncoder.encode("newBoard", "UTF-8"));
                                                arguments.append("=");
                                                arguments.append(
                                                        URLEncoder.encode(newBoard.toString(), "UTF-8"));
                                                arguments.append("&");
                                                arguments.append(
                                                        URLEncoder.encode("newTurn", "UTF-8"));
                                                arguments.append("=");
                                                arguments.append(
                                                        URLEncoder.encode(newTurn, "UTF-8"));

                                                bw.write(arguments.toString());
                                                bw.flush();
                                                bw.close();

                                                int responseCode = connect.getResponseCode();
                                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                                    InputStream is = connect.getInputStream();
                                                    BufferedReader br = new BufferedReader(
                                                            new InputStreamReader(is, "UTF-8"));
                                                    StringBuilder msg = new StringBuilder();
                                                    String data = br.readLine();

                                                    while (data != null) {
                                                        Log.d("data", data);
                                                        data = br.readLine();
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
                                } else {
                                    Log.d("Positions", String.valueOf(position) + ' ' +
                                            String.valueOf(clicked) + String.valueOf(jump) +
                                            String.valueOf(jump2) + String.valueOf(jump3) +
                                            String.valueOf(jump4));
                                }
                            }

                            if (!lastClicked.getText().equals("K")) {
                                lastClicked.setText("");
                            }
                            clicked = -1;
                        } else if (!getItem(position).equals("na")) {
                            clicked = position;
                            if (!button.getText().equals("K")) {
                                button.setText("S");
                            }
                            button.setTextColor(Color.YELLOW);

                            lastClicked = button;
                        }
                    }
                }
            });
        } else {
            button = (Button) convert_view;
        }

        return button;
    }

    void setMyTurn(boolean myTurn, boolean isRed) {
        this.myTurn = myTurn;
        this.isRed = isRed;
    }

    void updateGrid(String[][] lstSource) {
        this.lstSource = lstSource.clone();
    }
}
