package com.theoretics.mobilepos.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.theoretics.mobilepos.R;
import com.theoretics.mobilepos.bean.CONSTANTS;
import com.theoretics.mobilepos.util.DBHelper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    private ImageButton loginBtn;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog alertDialog;
    private EditText loginUsername;
    private EditText loginPassword;

    private DBHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dialogBuilder = new AlertDialog.Builder(this);
        buildAlertDialog();

        dbh = new DBHelper(this);
        initView();
    }

    @Override
    public void onBackPressed() {

    }

    private void initView() {
        loginUsername = (EditText) findViewById(R.id.cardNumber);
        loginPassword = (EditText) findViewById(R.id.loginPassword);

        loginBtn = (ImageButton) findViewById(R.id.logINBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //isExists();
                //start_autoRead();
                System.out.println("ANGELO : CLICKED [" + new Date().toString() + "]");
                CheckServer checker = new CheckServer(new MyInterface() {
                    @Override
                    public void myMethod(boolean result) {
                        if (result == true) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "Connection Succesful",
                                            Toast.LENGTH_LONG).show();

                                    System.out.println("ANGELO : CLICKED [" + new Date().toString() + "]");
                                    if (dbh.validateLogin(loginUsername.getText().toString(), loginPassword.getText().toString())) {
                                        loginUsername.setText("success");
                                        loginPassword.setText("success");
                                        //dbh.saveLogin();
                                        finish();
                                    } else {
                                        loginUsername.setText("");
                                        loginPassword.setText("");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(LoginActivity.this, "Wrong Username/Password",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                            Toast.makeText(LoginActivity.this, "Connection Failed:", Toast.LENGTH_LONG).show();
                                    if (!alertDialog.isShowing())
                                        alertDialog.show();
                                }
                            });
                        }
                    }
                });
                checker.execute("login");

            }
        });
    }

    private static boolean isReachable(String addr, int openPort, int timeOutMillis) {
        // Any Open port on other machine
        // openPort =  22 - ssh, 80 or 443 - webserver, 25 - mailserver etc.
        try (Socket soc = new Socket()) {
            soc.connect(new InetSocketAddress(addr, openPort), timeOutMillis);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public interface MyInterface {
        public void myMethod(boolean result);
    }

    private class CheckServer extends AsyncTask<String, Integer, Boolean> {
        private MyInterface mListener;

        public CheckServer(MyInterface mListener) {
            this.mListener = mListener;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String my = params[0];
            int i = 0;
            publishProgress(i);
            try {
                //CONSTANTS.SERVER_NAME
                if (isReachable(CONSTANTS.SERVER_ADDR, 80, 1000)) {
                    if (mListener != null)
                        mListener.myMethod(true);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!alertDialog.isShowing())
                                alertDialog.show();
                        }
                    });
                    return false;
                    //Toast.makeText(getApplicationContext(), "You are not connected to the network. Please Try again.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        protected void onProgressUpdate(Integer... values) {

        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected void onPostExecute(boolean result) {
            if (mListener != null)
                mListener.myMethod(result);
            //super.onPostExecute(result);
        }
    }

    private void buildAlertDialog() {
        dialogBuilder.setMessage("You are not connected to the server. Try Again?");
        dialogBuilder.setTitle("Network is Needed");
        dialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Toast.makeText(getApplicationContext(), "Please ask assistance from TSS", Toast.LENGTH_LONG).show();
                    }
                });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Turn On WIFI / Find a better signal", Toast.LENGTH_LONG).show();
            }
        });
        alertDialog = dialogBuilder.create();

    }


}