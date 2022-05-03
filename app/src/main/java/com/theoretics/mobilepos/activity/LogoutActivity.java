package com.theoretics.mobilepos.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.theoretics.mobilepos.R;
import com.theoretics.mobilepos.bean.CONSTANTS;
import com.theoretics.mobilepos.bean.GLOBALS;
import com.theoretics.mobilepos.bean.XREADING;
import com.theoretics.mobilepos.bean.ZREADING;
import com.theoretics.mobilepos.util.DBHelper;
import com.theoretics.mobilepos.util.ReceiptUtils;
import com.topwise.cloudpos.aidl.AidlDeviceService;
import com.topwise.cloudpos.aidl.buzzer.AidlBuzzer;
import com.topwise.cloudpos.aidl.led.AidlLed;
import com.topwise.cloudpos.aidl.printer.AidlPrinter;
import com.topwise.cloudpos.aidl.printer.AidlPrinterListener;
import com.topwise.cloudpos.aidl.printer.PrintItemObj;
import com.topwise.cloudpos.data.PrinterConstant;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LogoutActivity extends BaseActivity {
    private Button btnYes = null;
    private Button btnNo = null;
    View containme = null;
    private boolean alreadyXPrinted = false;
    private boolean alreadyZPrinted = false;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog alertDialog;

    private AidlPrinter printerDev = null;
    private AidlDeviceService deviceManager = null;
    private AidlBuzzer iBeeper;
    private AidlLed iLed;
    private boolean isLedOn = false;

    private ArrayList<PrintItemObj> obj2Print;
    final DecimalFormat df2 = new DecimalFormat("#0.00");

    private EditText loginUsername;
    private EditText loginPassword;
    private ImageView printZReadbtn;

    ReceiptUtils rec = null;
    DBHelper dbh = null;

    String pattern2 = "yyyy-MM-dd HH:mm:ss";
    final SimpleDateFormat sdf2 = new SimpleDateFormat(pattern2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        dialogBuilder = new AlertDialog.Builder(this);
        buildAlertDialog();

        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        dbh = new DBHelper(getApplicationContext());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                new CheckServer().execute("logout");
            }
        });
        initView();
    }

    private void initView() {
        containme = findViewById(R.id.containme);
        loginUsername = (EditText) containme.findViewById(R.id.logoutUsername);
        loginPassword = (EditText) containme.findViewById(R.id.logoutPassword);
        printZReadbtn = containme.findViewById(R.id.printZReadbtn);

        btnYes = (Button) containme.findViewById(R.id.buttonYes);
        btnNo = (Button) containme.findViewById(R.id.buttonNo);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dbh = new DBHelper(getApplicationContext());
                if (GLOBALS.getInstance().getCashierName().compareTo(loginUsername.getText().toString()) == 0) {
                    if (dbh.validateLogout(loginUsername.getText().toString(), loginPassword.getText().toString())) {
                        loginUsername.setText("success");
                        loginPassword.setText("success");
                        //dbh.saveLogin();
                        Toast.makeText(getApplicationContext(), "PRINTING XREAD...Please wait.", Toast.LENGTH_SHORT).show();
                        logoutIfValid(false);

                    } else {
                        loginUsername.setText("");
                        loginPassword.setText("");
                        Toast.makeText(getApplicationContext(), "Wrong Username/Password. Please Try again.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loginUsername.setText("");
                    loginPassword.setText("");
                    Toast.makeText(getApplicationContext(), "You logged in as a different User. Please Try again.", Toast.LENGTH_SHORT).show();

                }


            }
        });
        printZReadbtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        printZRead();
                    }
                }
        );

        rec = new ReceiptUtils();
        rec.initiate(getApplicationContext());
        obj2Print = new ArrayList<PrintItemObj>();
    }

    private void logoutIfValid(boolean forZPrinting) {
        dbh = new DBHelper(getApplicationContext());
        Date now = new Date();
        String pattern = "yyyy-MM-dd HH:mm:ss";
        final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        dbh.updateXRead("logoutStamp", sdf.format(now));
        String LastOR = dbh.getOneTrans("SELECT receiptNos FROM master", "receiptNos");

        int lastOR = Integer.parseInt(LastOR) - 1;
        String beginOR = dbh.getOneZRead("SELECT beginOR FROM zread", "beginOR");
        String beginTrans = dbh.getOneZRead("SELECT beginTrans FROM zread", "beginTrans");
        String beginBal = dbh.getOneZRead("SELECT oldGrand FROM zread", "oldGrand");
        String beginGross = dbh.getOneZRead("SELECT oldGrossTotal FROM zread", "oldGrossTotal");

        String endTrans = dbh.getOneTrans("SELECT MAX(id) AS beginTrans FROM exit_trans", "beginTrans");
        String endGross = dbh.getOneTrans("SELECT grossTotal FROM master", "grossTotal");
        String endGrand = dbh.getOneTrans("SELECT grandTotal FROM master", "grandTotal");

        double todaysale = dbh.getImptAmount("totalAmount");
        double todayGross = dbh.getImptAmount("grossAmount");
        double vatsaleAmount = dbh.getImptAmount("vatsaleAmount");
        double vat12Amount = dbh.getImptAmount("vat12Amount");
        double vatExemptedSalesAmount = dbh.getImptAmount("vatExemptedSalesAmount");

        double localseniorDiscountAmount = dbh.getImptAmount("localseniorDiscountAmount");
        double seniorDiscountAmount = dbh.getImptAmount("seniorDiscountAmount");
        double pwdDiscountAmount = dbh.getImptAmount("pwdDiscountAmount");
        double totalDiscounts = seniorDiscountAmount + localseniorDiscountAmount + pwdDiscountAmount;

        String endOR = CONSTANTS.getInstance().getExitID() + dbh.formatNos(lastOR + "");
        dbh.updateZRead("endOR", endOR);
        dbh.updateZRead("endTrans", endTrans);
        dbh.updateZRead("newGrossTotal", endGross);
        dbh.updateZRead("newGrand", endGrand);
        dbh.updateZRead("datetimeOut", sdf.format(now));
        dbh.updateZRead("todaysale", todaysale + "");
        dbh.updateZRead("todaysGross", todayGross + "");
        dbh.updateZRead("vatablesale", vatsaleAmount + "");
        dbh.updateZRead("vat", vat12Amount + "");
        dbh.updateZRead("vatExemptedSales", vatExemptedSalesAmount + "");
        dbh.updateZRead("discounts", totalDiscounts + "");


        ZREADING.getInstance().setBeginOR(beginOR);
        ZREADING.getInstance().setBeginTrans(beginTrans);
        ZREADING.getInstance().setEndOR(endOR);
        ZREADING.getInstance().setEndTrans(endTrans);

        ZREADING.getInstance().setBeginBalance(Double.parseDouble(beginBal));
        ZREADING.getInstance().setEndingBalance(Double.parseDouble(endGrand));
        ZREADING.getInstance().setBeginGross(Double.parseDouble(beginGross));
        ZREADING.getInstance().setEndingGross(Double.parseDouble(endGross));

        //if (forPrinting)
        printXRead(forZPrinting);

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public void onDeviceConnected(AidlDeviceService serviceManager) {
        deviceManager = serviceManager;
        try {
            printerDev = AidlPrinter.Stub.asInterface(serviceManager.getPrinter());
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            iBeeper = AidlBuzzer.Stub.asInterface(serviceManager.getBuzzer());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        try {
            Bundle bundle = new Bundle();
            bundle.putInt("LED_ID", 1);
            iLed = AidlLed.Stub.asInterface(serviceManager.getLed());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        try {
            iLed.setLed(false);
            isLedOn = false;
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void printXRead(final boolean autoLogOut) {
        if (alreadyXPrinted == false) {
            saveXRead2Memory();
        }
        try {
            DecimalFormat df2 = new DecimalFormat("#0.00");

            this.printerDev.setPrinterGray(12);
            printNsave("CHINESE GENERAL HOSPITAL MEDICAL CTR", PrinterConstant.FontSize.SMALL, true, PrintItemObj.ALIGN.CENTER);
            printNsave("286 BLUMENTRITT ST. STA. CRUZ MANILA", PrinterConstant.FontSize.SMALL, true, PrintItemObj.ALIGN.CENTER);
            printNsave(CONSTANTS.getInstance().getREGTIN(), PrinterConstant.FontSize.SMALL, true, PrintItemObj.ALIGN.CENTER);
            printNsave(CONSTANTS.getInstance().getMIN(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.CENTER);
            printNsave(CONSTANTS.getInstance().getPTU(), PrinterConstant.FontSize.SMALL, false, PrintItemObj.ALIGN.CENTER);
            printNsave("", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.CENTER);
            printNsave("--- X READING ---", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.CENTER);
            printNsave("", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("------------------------------------------", PrinterConstant.FontSize.SMALL, false, PrintItemObj.ALIGN.CENTER);
            printNsave("Teller   : " + GLOBALS.getInstance().getCashierID(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Log In   : " + XREADING.getInstance().getLogInDateTime(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Log Out  : " + XREADING.getInstance().getLogOutDateTime(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("------------------------------------------", PrinterConstant.FontSize.SMALL, false, PrintItemObj.ALIGN.CENTER);
            printNsave("", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Count    Amount          ", PrinterConstant.FontSize.SMALL, false, PrintItemObj.ALIGN.RIGHT);
            printNsave("Ambulance Parkers   :" + XREADING.getInstance().getAmbulanceCount() + "   " + df2.format(XREADING.getInstance().getAmbulanceAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("GracePeriod Parkers :" + XREADING.getInstance().getGracePeriodCount() + "   " + df2.format(XREADING.getInstance().getGracePeriodAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Inpatient Parkers   :" + XREADING.getInstance().getInpatientCount() + "   " + df2.format(XREADING.getInstance().getInpatientAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Lost Parkers        :" + XREADING.getInstance().getLostCount() + "   " + df2.format(XREADING.getInstance().getLostAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("MABRegular Parkers  :" + XREADING.getInstance().getMABRegularCount() + "   " + df2.format(XREADING.getInstance().getMABRegularAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Motorcycle Parkers  :" + XREADING.getInstance().getMotorcycleCount() + "   " + df2.format(XREADING.getInstance().getMotorcycleAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("PWD Parkers         :" + XREADING.getInstance().getPwdCount() + "   " + df2.format(XREADING.getInstance().getPwdAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Regular Parkers     :" + XREADING.getInstance().getRegularCount() + "   " + df2.format(XREADING.getInstance().getRegularAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Senior Parkers      :" + XREADING.getInstance().getSeniorCount() + "   " + df2.format(XREADING.getInstance().getSeniorAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("VIP Parkers         :" + XREADING.getInstance().getVIPCount() + "   " + df2.format(XREADING.getInstance().getVIPAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Dialysis Parkers    :" + XREADING.getInstance().getDialysisCount() + "   " + df2.format(XREADING.getInstance().getDialysisAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);

            printNsave("Teller   : " + GLOBALS.getInstance().getCashierID(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("**Log In : " + XREADING.getInstance().getLogInDateTime(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Log Out  : " + XREADING.getInstance().getLogOutDateTime(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("D/T Print: " + XREADING.getInstance().getLogOutDateTime(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);

            printNsave("VATable Sales       :" + df2.format(XREADING.getInstance().getVATableSales()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("VAT Amount(12%)     :" + df2.format(XREADING.getInstance().getVAT12()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("VAT Exempt Sales    :" + "0.00", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Zero-Rated Sales    :" + "0.00", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("PWD DSC Count       :" + XREADING.getInstance().getPwdDscCount(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("PWD DSC Amount      :" + df2.format(XREADING.getInstance().getPwdDscAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Senior DSC Count    :" + XREADING.getInstance().getSeniorDscCount(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Senior DSC Amount   :" + df2.format(XREADING.getInstance().getSeniorDscAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("LocalSenior DSC Cnt :" + XREADING.getInstance().getLocalSeniorDscCount(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("LocalSenior DSC Amt :" + df2.format(XREADING.getInstance().getLocalSeniorDscAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);

            printNsave("Teller   : " + GLOBALS.getInstance().getCashierID(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("**Log In : " + XREADING.getInstance().getLogInDateTime(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Log Out  : " + XREADING.getInstance().getLogOutDateTime(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("D/T Print: " + XREADING.getInstance().getLogOutDateTime(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);

            printNsave("Total GROSS Amt:" + df2.format(XREADING.getInstance().getTodaysGrossColl()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Total Cash Coll:" + df2.format(XREADING.getInstance().getTodaysCollection()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Total Cars Served:" + XREADING.getInstance().getCarsServed(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Total Collection :" + df2.format(XREADING.getInstance().getTodaysCollection()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);

            printNsave("Beginning OR No:" + ZREADING.getInstance().getBeginOR(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Ending OR No   :" + ZREADING.getInstance().getEndOR(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Beginning Bal. :" + df2.format(ZREADING.getInstance().getBeginBalance()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Ending Balance :" + df2.format(ZREADING.getInstance().getEndingBalance()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Beginning Gross:" + df2.format(ZREADING.getInstance().getBeginGross()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Ending Gross   :" + df2.format(ZREADING.getInstance().getEndingGross()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);

            printNsave("Teller   : " + GLOBALS.getInstance().getCashierID(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("**Log In : " + XREADING.getInstance().getLogInDateTime(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Log Out  : " + XREADING.getInstance().getLogOutDateTime(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("D/T Print: " + XREADING.getInstance().getLogOutDateTime(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);

            printNsave("Teller Sign   : _____________________", PrinterConstant.FontSize.SMALL, false, PrintItemObj.ALIGN.CENTER);
            printNsave("Supervisor    : _____________________", PrinterConstant.FontSize.SMALL, false, PrintItemObj.ALIGN.CENTER);

            printNsave("", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);

            printerDev.printText(obj2Print, new AidlPrinterListener.Stub() {

                @Override
                public void onPrintFinish() throws RemoteException {
                    //String endTime = getCurTime();
                    //sendmessage(getStringByid(R.string.print_end) + endTime);

                    if (autoLogOut) {
                        printZRead();
                    }

                    try {
                        iLed.setLed(false);
                        isLedOn = false;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }


                    alreadyXPrinted = true;
                    if (autoLogOut == false)
                        dbh.logoutForced();

                    GLOBALS.getInstance().setLoginID("");
                    GLOBALS.getInstance().setCashierID("");
                    GLOBALS.getInstance().setCashierName("");
                    GLOBALS.getInstance().setLoginDate("");
                    Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(int arg0) throws RemoteException {
                    //sendmessage(getStringByid(R.string.print_faile_errcode) + arg0);
                    alreadyXPrinted = false;
                    try {
                        iLed.setLed(false);
                        isLedOn = false;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void printZRead() {
        if (alreadyZPrinted == false) {
            saveXnZRead2Memory();
        }
        try {
            DecimalFormat df2 = new DecimalFormat("#0.00");
            Date now = new Date();
            this.printerDev.setPrinterGray(12);
            printNsave("CHINESE GENERAL HOSPITAL MEDICAL CTR", PrinterConstant.FontSize.SMALL, true, PrintItemObj.ALIGN.CENTER);
            printNsave("286 BLUMENTRITT ST. STA. CRUZ MANILA", PrinterConstant.FontSize.SMALL, true, PrintItemObj.ALIGN.CENTER);
            printNsave(CONSTANTS.getInstance().getREGTIN(), PrinterConstant.FontSize.SMALL, true, PrintItemObj.ALIGN.CENTER);
            printNsave(CONSTANTS.getInstance().getMIN(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.CENTER);
            printNsave(CONSTANTS.getInstance().getPTU(), PrinterConstant.FontSize.SMALL, false, PrintItemObj.ALIGN.CENTER);
            printNsave("", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.CENTER);
            printNsave("--- CURRENT Z READING ---", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.CENTER);
            printNsave("Terminal No     : " + CONSTANTS.getInstance().getExitID(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.CENTER);
            printNsave("ZREADING DATE      : " + ZREADING.getInstance().getZreadingDate(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Today's Sales      : " + df2.format(ZREADING.getInstance().getTodaysSales()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("VAT Sales          : " + df2.format(ZREADING.getInstance().getVatableSales()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("12% VAT Sales      : " + df2.format(ZREADING.getInstance().getVat12Sales()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Beginning OR : " + ZREADING.getInstance().getBeginOR(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Ending OR   : " + ZREADING.getInstance().getEndOR(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Beginning Trans No : " + ZREADING.getInstance().getBeginTrans(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Ending Trans No    : " + ZREADING.getInstance().getEndTrans(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Old Grand Total    : " + ZREADING.getInstance().getBeginBalance(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("New Grand Total    : " + ZREADING.getInstance().getEndingBalance(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("------------------------------------------", PrinterConstant.FontSize.SMALL, false, PrintItemObj.ALIGN.CENTER);
            printNsave("--- TODAY's X READINGS ---", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.CENTER);
            printNsave("--Log Out Collection--", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.CENTER);
            printNsave("Terminal No     : " + CONSTANTS.getInstance().getExitID(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.CENTER);
            printNsave("", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.CENTER);
            printNsave("Count    Amount          ", PrinterConstant.FontSize.SMALL, false, PrintItemObj.ALIGN.RIGHT);
            printNsave("Ambulance Parkers   :" + XREADING.getInstance().getAmbulanceCount() + "   " + df2.format(XREADING.getInstance().getAmbulanceAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("GracePeriod Parkers :" + XREADING.getInstance().getGracePeriodCount() + "   " + df2.format(XREADING.getInstance().getGracePeriodAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Inpatient Parkers   :" + XREADING.getInstance().getInpatientCount() + "   " + df2.format(XREADING.getInstance().getInpatientAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Lost Parkers        :" + XREADING.getInstance().getLostCount() + "   " + df2.format(XREADING.getInstance().getLostAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("MABRegular Parkers  :" + XREADING.getInstance().getMABRegularCount() + "   " + df2.format(XREADING.getInstance().getMABRegularAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Motorcycle Parkers  :" + XREADING.getInstance().getMotorcycleCount() + "   " + df2.format(XREADING.getInstance().getMotorcycleAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("PWD Parkers         :" + XREADING.getInstance().getPwdCount() + "   " + df2.format(XREADING.getInstance().getPwdAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Regular Parkers     :" + XREADING.getInstance().getRegularCount() + "   " + df2.format(XREADING.getInstance().getRegularAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Senior Parkers      :" + XREADING.getInstance().getSeniorCount() + "   " + df2.format(XREADING.getInstance().getSeniorAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("VIP Parkers         :" + XREADING.getInstance().getVIPCount() + "   " + df2.format(XREADING.getInstance().getVIPAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Dialysis Parkers    :" + XREADING.getInstance().getDialysisCount() + "   " + df2.format(XREADING.getInstance().getDialysisAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);

            printNsave("Total Cars Served   : " + XREADING.getInstance().getCarsServed(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Total Collection    : " + df2.format(XREADING.getInstance().getTotalAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Date Printed : " + sdf2.format(now), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Accumulated Grand Total : " + ZREADING.getInstance().getAccumulatedGrand(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Accumulated Gross Total : " + ZREADING.getInstance().getAccumulatedGross(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);

            /*
            Date Printed       : 2020-10-27 11:05
            Accumulated Grand Total    : 3522.00
            Accumulated Gross Total    : 3580.00
            */
            /*
            printNsave("VATable Sales       :" + df2.format(XREADING.getInstance().getVATableSales()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("VAT Amount(12%)     :" + df2.format(XREADING.getInstance().getVAT12()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("VAT Exempt Sales    :" + "0.00", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Zero-Rated Sales    :" + "0.00", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("PWD DSC Count       :" + XREADING.getInstance().getPwdDscCount(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("PWD DSC Amount      :" + df2.format(XREADING.getInstance().getPwdDscAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Senior DSC Count    :" + XREADING.getInstance().getSeniorDscCount(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Senior DSC Amount   :" + df2.format(XREADING.getInstance().getSeniorDscAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("LocalSenior DSC Cnt :" + XREADING.getInstance().getLocalSeniorDscCount(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("LocalSenior DSC Amt :" + df2.format(XREADING.getInstance().getLocalSeniorDscAmount()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);

            printNsave("Total GROSS Amt:" + df2.format(XREADING.getInstance().getTodaysGrossColl()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Total Cash Coll:" + df2.format(XREADING.getInstance().getTodaysCollection()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Total Cars Served:" + XREADING.getInstance().getCarsServed(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Total Collection :" + df2.format(XREADING.getInstance().getTodaysCollection()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);

            printNsave("Beginning OR No:" + ZREADING.getInstance().getBeginOR(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Ending OR No   :" + ZREADING.getInstance().getEndOR(), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Beginning Bal. :" + df2.format(ZREADING.getInstance().getBeginBalance()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Ending Balance :" + df2.format(ZREADING.getInstance().getEndingBalance()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Beginning Gross:" + df2.format(ZREADING.getInstance().getBeginGross()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("Ending Gross   :" + df2.format(ZREADING.getInstance().getEndingGross()), PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            */

            printNsave("Teller Sign   : _____________________", PrinterConstant.FontSize.SMALL, false, PrintItemObj.ALIGN.CENTER);
            printNsave("Supervisor    : _____________________", PrinterConstant.FontSize.SMALL, false, PrintItemObj.ALIGN.CENTER);

            printNsave("\n", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);
            printNsave("\n", PrinterConstant.FontSize.NORMAL, false, PrintItemObj.ALIGN.LEFT);

            printerDev.printText(obj2Print, new AidlPrinterListener.Stub() {

                @Override
                public void onPrintFinish() throws RemoteException {
                    //String endTime = getCurTime();
                    //sendmessage(getStringByid(R.string.print_end) + endTime);
                    try {
                        iLed.setLed(false);
                        isLedOn = false;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    alreadyZPrinted = true;
                    dbh.logoutForced();

                    GLOBALS.getInstance().setLoginID("");
                    GLOBALS.getInstance().setCashierID("");
                    GLOBALS.getInstance().setCashierName("");
                    GLOBALS.getInstance().setLoginDate("");
                    Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(int arg0) throws RemoteException {
                    //sendmessage(getStringByid(R.string.print_faile_errcode) + arg0);
                    alreadyZPrinted = false;
                    try {
                        iLed.setLed(false);
                        isLedOn = false;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void saveXRead2Memory() {
        DBHelper dbh = new DBHelper(getApplicationContext());
        XREADING.getInstance().setTeller(dbh.getImptString("userCode"));
        XREADING.getInstance().setLogInDateTime(dbh.getImptString("loginStamp"));
        XREADING.getInstance().setLogOutDateTime(dbh.getImptString("logoutStamp"));

        XREADING.getInstance().setAmbulanceCount(dbh.getImptCount("ambulanceCount"));
        XREADING.getInstance().setGracePeriodCount(dbh.getImptCount("graceperiodCount"));
        XREADING.getInstance().setInpatientCount(dbh.getImptCount("inpatientCount"));
        XREADING.getInstance().setLostCount(dbh.getImptCount("lostCount"));
        XREADING.getInstance().setMABRegularCount(dbh.getImptCount("mabregularCount"));
        XREADING.getInstance().setMotorcycleCount(dbh.getImptCount("motorcycleCount"));
        XREADING.getInstance().setPwdCount(dbh.getImptCount("pwdCount"));
        XREADING.getInstance().setRegularCount(dbh.getImptCount("regularCount"));
        XREADING.getInstance().setSeniorCount(dbh.getImptCount("seniorCount"));
        XREADING.getInstance().setVIPCount(dbh.getImptCount("vipCount"));
        XREADING.getInstance().setDialysisCount(dbh.getImptCount("dialysisCount"));
        XREADING.getInstance().setPwdDscCount(dbh.getImptCount("pwdDiscountCount"));
        XREADING.getInstance().setSeniorDscCount(dbh.getImptCount("seniorDiscountCount"));
        XREADING.getInstance().setLocalSeniorDscCount(dbh.getImptCount("localseniorDiscountCount"));
        XREADING.getInstance().setCarsServed(dbh.getImptCount("carServed"));

        XREADING.getInstance().setAmbulanceAmount(dbh.getImptAmount("ambulanceAmount"));
        XREADING.getInstance().setGracePeriodAmount(dbh.getImptAmount("graceperiodAmount"));
        XREADING.getInstance().setInpatientAmount(dbh.getImptAmount("inpatientAmount"));
        XREADING.getInstance().setLostAmount(dbh.getImptAmount("lostAmount"));
        XREADING.getInstance().setMABRegularAmount(dbh.getImptAmount("mabregularAmount"));
        XREADING.getInstance().setMotorcycleAmount(dbh.getImptAmount("motorcycleAmount"));
        XREADING.getInstance().setPwdAmount(dbh.getImptAmount("pwdAmount"));
        XREADING.getInstance().setRegularAmount(dbh.getImptAmount("regularAmount"));
        XREADING.getInstance().setSeniorAmount(dbh.getImptAmount("seniorAmount"));
        XREADING.getInstance().setVIPAmount(dbh.getImptAmount("vipAmount"));
        XREADING.getInstance().setDialysisAmount(dbh.getImptAmount("dialysisAmount"));
        XREADING.getInstance().setPwdDscAmount(dbh.getImptAmount("pwdDiscountAmount"));
        XREADING.getInstance().setSeniorDscAmount(dbh.getImptAmount("seniorDiscountAmount"));
        XREADING.getInstance().setLocalSeniorDscAmount(dbh.getImptAmount("localseniorDiscountAmount"));

        XREADING.getInstance().setVATableSales(dbh.getImptAmount("vatsaleAmount"));
        XREADING.getInstance().setVAT12(dbh.getImptAmount("vat12Amount"));
        XREADING.getInstance().setTodaysGrossColl(dbh.getImptAmount("grossAmount"));
        XREADING.getInstance().setTodaysCollection(dbh.getImptAmount("totalAmount"));

    }

    private void saveXnZRead2Memory() {
        DBHelper dbh = new DBHelper(getApplicationContext());

        dbh.getTodaysXReading();
        dbh.getTodaysZReading();

        Date now = new Date();
        String pattern = "yyyy-MM-dd";
        final SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        ZREADING.getInstance().setZreadingDate(sdf.format(now));
    }

    private void printNsave(String text, int fontSize, boolean isBold, PrintItemObj.ALIGN align) {
        obj2Print.add(new PrintItemObj(text, fontSize, isBold, align));
        rec.appendToFile(text);
        try {
            if (isLedOn) {
                iLed.setLed(false);
                isLedOn = false;
            } else {
                iLed.setLed(true);
                isLedOn = true;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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

    private class CheckServer extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String my = params[0];
            int i = 0;
            publishProgress(i);
            try {
                //CONSTANTS.SERVER_NAME
                if (isReachable(CONSTANTS.SERVER_ADDR, 80, 1000)) {

                    if (dbh.validateLogout(loginUsername.getText().toString(), loginPassword.getText().toString())) {
                        loginUsername.setText("success");
                        loginPassword.setText("success");
                        //dbh.saveLogin();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "PRINTING Zread...Please wait.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        logoutIfValid(true);
                        //printout12thZRead();
                    } else {
                        loginUsername.setText("");
                        loginPassword.setText("");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Wrong Username/Password. Please Try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!alertDialog.isShowing())
                                alertDialog.show();
                        }
                    });
                    //Toast.makeText(getApplicationContext(), "You are not connected to the network. Please Try again.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "Checked";
        }

        protected void onProgressUpdate(Integer... values) {

        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    private void buildAlertDialog() {
        dialogBuilder.setMessage("You are not connected to the server. Try Again?");
        dialogBuilder.setTitle("Network is Needed");
        dialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Toast.makeText(getApplicationContext(), "Yes is clicked", Toast.LENGTH_LONG).show();
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