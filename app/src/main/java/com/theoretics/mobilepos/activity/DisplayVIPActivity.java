package com.theoretics.mobilepos.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.theoretics.mobilepos.R;
import com.theoretics.mobilepos.util.DBHelper;

import java.util.Date;

public class DisplayVIPActivity extends AppCompatActivity {

    private ImageButton closeBtn;

    private TextView cardNum;
    private TextView firstName;
    private TextView lastName;
    private TextView plates;
    private TextView vehicletypes;
    private TextView Timein;
    private EditText loginPassword;

    private DBHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_v_i_p);
        dbh = new DBHelper(this);
        initView();
    }

    @Override
    public void onBackPressed() {

    }

    private void initView() {
        cardNum = (TextView) findViewById(R.id.cardNumTV);
        Timein = (TextView) findViewById(R.id.timeinTV);
        firstName = (TextView) findViewById(R.id.firstNameTV);
        lastName = (TextView) findViewById(R.id.lastNameTV);
        plates = (TextView) findViewById(R.id.platesTV);
        vehicletypes = (TextView) findViewById(R.id.vehicleTypesTV);
        Intent myIntent = getIntent();
        String cardNumStr = myIntent.getStringExtra("cardNum");
        String firstNameStr = myIntent.getStringExtra("firstName");
        String lastNameStr = myIntent.getStringExtra("lastName");
        String platesStr = myIntent.getStringExtra("plates");
        String vehicletypesStr = myIntent.getStringExtra("vehicletypes");
        String timeInStr = myIntent.getStringExtra("Timein");
        cardNum.setText(cardNumStr);
        firstName.setText(firstNameStr);
        lastName.setText(lastNameStr);
        plates.setText(platesStr);
        vehicletypes.setText(vehicletypesStr);
        if (timeInStr != null) {
            Timein.setText(timeInStr);
        } else {
            Timein.setText("VIP / DOCTOR has no Entry Record");
        }
            closeBtn = (ImageButton) findViewById(R.id.closeBtn);
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //setResult(Activity.RESULT_OK, ParkingExitActivity.class);
                    finish();
                    //isExists();
                    //start_autoRead();
                /*
                System.out.println("ANGELO : CLICKED [" + new Date().toString() + "]" );
                if(dbh.validateLogin(loginUsername.getText().toString(), loginPassword.getText().toString())) {
                    loginUsername.setText("success");
                    loginPassword.setText("success");
                    //dbh.saveLogin();
                    finish();
                } else {
                    loginUsername.setText("");
                    loginPassword.setText("");
                    Toast.makeText(getApplicationContext(), "Wrong Username/Password. Please Try again.", Toast.LENGTH_SHORT).show();
                }
                */
                }
            });
        }


}