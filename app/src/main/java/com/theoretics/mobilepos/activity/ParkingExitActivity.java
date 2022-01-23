package com.theoretics.mobilepos.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.theoretics.mobilepos.R;
import com.theoretics.mobilepos.bean.CONSTANTS;
import com.theoretics.mobilepos.bean.GLOBALS;
import com.theoretics.mobilepos.bluetooth.BluetoothOperation;
import com.theoretics.mobilepos.permission.EasyPermission;
import com.theoretics.mobilepos.util.DBHelper;
import com.theoretics.mobilepos.util.HexUtil;
import com.theoretics.mobilepos.util.HttpHandler;
import com.theoretics.mobilepos.util.IPrinterOperation;
import com.theoretics.mobilepos.util.NfcAutoCheck;
import com.theoretics.mobilepos.util.PrintUtils;
import com.theoretics.mobilepos.util.ReceiptUtils;
import com.topwise.cloudpos.aidl.AidlDeviceService;
import com.topwise.cloudpos.aidl.rfcard.AidlRFCard;
import com.topwise.cloudpos.data.RFCard;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.theoretics.mobilepos.util.DBHelper.CARD_COLUMN_CARDCODE;
import static com.theoretics.mobilepos.util.DBHelper.CARD_COLUMN_LANE;
import static com.theoretics.mobilepos.util.DBHelper.CARD_COLUMN_PC;
import static com.theoretics.mobilepos.util.DBHelper.CARD_COLUMN_PLATE;
import static com.theoretics.mobilepos.util.DBHelper.CARD_COLUMN_TIMEIN;
import static com.theoretics.mobilepos.util.DBHelper.CARD_COLUMN_VEHICLE;
import static com.theoretics.mobilepos.util.DBHelper.EXIT_TABLE_NAME;
import static com.theoretics.mobilepos.util.DBHelper.NET_MANAGER_COLUMN_LDM;
import static com.theoretics.mobilepos.util.DBHelper.SERVER_NAME;
import static com.theoretics.mobilepos.util.DBHelper.VIP_TABLE_NAME;
import static com.theoretics.mobilepos.util.DBHelper.XREAD_TABLE_NAME;

public class ParkingExitActivity extends BaseActivity implements EasyPermission.PermissionCallback {

    private Context context;

    public static final int CONNECT_DEVICE = 1;             //选择设备
    public static final int ENABLE_BT = 2;                  //启动蓝牙
    public static final int REQUEST_SELECT_FILE = 3;        //选择文件
    public static final int REQUEST_PERMISSION = 4;         //读写权限

    public AidlRFCard rfcard = null;
    private NfcAutoCheck nfcAutoCheck = null;

    private Button regularBtn = null;
    private Button vipBtn = null;
    private Button dialysisBtn = null;
    private Button pwdSeniorBtn = null;

    private TextView cardNum = null;
    private EditText inputPlate = null;
    private TextView datetimeIN = null;
    private TextView datetimeOUT = null;
    private TextView durationElapsed = null;

    private DBHelper dbh;
    private String cardNumber = "";
    private String prevCardNumber = "";
    public String retStr = "";

    public long minutesElapsed = 0;
    public long hrsElapsed = 0;
    public double amountDue = 0;

    private int daysElapsed = 0;
    private int minsRemaining = 0;
    private int hrsRemaining = 0;
    ReceiptUtils rec = null;

    private static boolean isConnected;
    private IPrinterOperation myOperation;
    private PrinterInstance mPrinter;
    private ProgressDialog dialog;

    File internalfile;
    File sdfile;
    File sdfileCheck;
    //String root = Environment.getExternalStorageDirectory().toString();
    //String root = "sdcard/Download";
    String internalPath = "data/data/com.theoretics.mobilepos";
    String sdCardPath = "mnt/m_external_sd/Android/data/com.theoretics.mobilepos";
    String sdPath = "mnt/m_external_sd/Android/data/com.theoretics.mobilepos/files/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_exit);
        context = this;
        initView();
        dbh = new DBHelper(this);

        checkLog();

        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            int EXTERNAL_STORAGE_PERMISSION_CODE = 201;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_PERMISSION_CODE);
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS, Manifest.permission.READ_EXTERNAL_STORAGE};
            // Verify permissions
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //request for access
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }

        }
        initiateFiles();
        /*
        new Thread(new Runnable() {
            public void run() {
                // a potentially time consuming task
                cardNum.setText(cardNumber);
                System.out.println("ANGELO CARD:" + cardNumber);
            }
        }).start();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // Code here will run in UI thread
                cardNum.setText(cardNumber);
                System.out.println("ANGELO CARD:" + cardNumber);
            }
        });*/

    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 25:
                Intent intent = new Intent(ParkingExitActivity.this, LogoutActivity.class);
                //intent.putExtra("cardNum",cardNum.getText());
                startActivity(intent);
                return true;
            default:
                System.out.println("ANGELO :" + keyCode);
                return super.onKeyUp(keyCode, event);
        }
    }

    public String initiateFiles() {
        //file = context.getExternalFilesDir((String) null);
        internalfile = new File(internalPath);
        if(internalfile.exists() == false) {
            internalfile.mkdir();
        }
        sdfile = new File(sdCardPath);
        if(sdfile.exists() == false) {
            boolean b = sdfile.mkdirs();
            System.out.println(sdCardPath + " was created = " + b);
        }
        sdfile = new File(sdPath);
        if(sdfile.exists() == false) {
            boolean b = sdfile.mkdirs();
            System.out.println(sdCardPath + " was created = " + b);
        }
        return sdfile.getPath().toString();

    }

    protected void onStart(){
        super.onStart();

//checkLog();
    }

    private void initView() {
        regularBtn = (Button) findViewById(R.id.regularBtn);
        vipBtn = (Button) findViewById(R.id.vipBtn);
        dialysisBtn = (Button) findViewById(R.id.dialysisBtn);
        regularBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //printBTText("Test");
                GLOBALS.getInstance().setPlateNum(inputPlate.getText().toString());
                GLOBALS.getInstance().setReceiptCopyType("CUSTOMER COPY");
                Intent intent = new Intent(ParkingExitActivity.this, ComputationActivity.class);
                intent.putExtra("cardNum",cardNum.getText());
                intent.putExtra("inputPlate",inputPlate.getText().toString());

                intent.putExtra("isDiscounted",false);
                intent.putExtra("TRType", CONSTANTS.getInstance().getDefaultPType());
                intent.putExtra("daysElapsed", daysElapsed);
                intent.putExtra("hrsRemaining", hrsRemaining);
                intent.putExtra("minsRemaining", minsRemaining);

                startActivity(intent);
            }
        });
        pwdSeniorBtn = (Button) findViewById(R.id.pwdSeniorBtn);
        pwdSeniorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GLOBALS.getInstance().setPlateNum(inputPlate.getText().toString());
                GLOBALS.getInstance().setReceiptCopyType("CUSTOMER COPY");
                Intent intent = new Intent(ParkingExitActivity.this, ComputationActivity.class);
                intent.putExtra("cardNum",cardNum.getText());
                intent.putExtra("inputPlate",inputPlate.getText().toString());

                intent.putExtra("isDiscounted",true);
                intent.putExtra("TRType","S");
                intent.putExtra("daysElapsed", daysElapsed);
                intent.putExtra("hrsRemaining", hrsRemaining);
                intent.putExtra("minsRemaining", minsRemaining);

                startActivity(intent);
            }
        });
        vipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GLOBALS.getInstance().setPlateNum(inputPlate.getText().toString());
                GLOBALS.getInstance().setReceiptCopyType("CUSTOMER COPY");
                Intent intent = new Intent(ParkingExitActivity.this, ComputationActivity.class);
                intent.putExtra("cardNum",cardNum.getText());
                intent.putExtra("inputPlate",inputPlate.getText().toString());

                intent.putExtra("isDiscounted",false);
                intent.putExtra("TRType","V");
                intent.putExtra("daysElapsed", daysElapsed);
                intent.putExtra("hrsRemaining", hrsRemaining);
                intent.putExtra("minsRemaining", minsRemaining);

                startActivity(intent);
            }
        });
        dialysisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GLOBALS.getInstance().setPlateNum(inputPlate.getText().toString());
                GLOBALS.getInstance().setReceiptCopyType("CUSTOMER COPY");
                Intent intent = new Intent(ParkingExitActivity.this, ComputationActivity.class);
                intent.putExtra("cardNum",cardNum.getText());
                intent.putExtra("inputPlate",inputPlate.getText().toString());

                intent.putExtra("isDiscounted",false);
                intent.putExtra("TRType","DS");
                intent.putExtra("daysElapsed", daysElapsed);
                intent.putExtra("hrsRemaining", hrsRemaining);
                intent.putExtra("minsRemaining", minsRemaining);

                startActivity(intent);
            }
        });

        cardNum = (TextView) findViewById(R.id.cardNum);
        datetimeIN = (TextView) findViewById(R.id.datetimeIN);
        datetimeOUT = (TextView) findViewById(R.id.datetimeOUT);
        inputPlate = (EditText) findViewById(R.id.inputPlateNo);
        durationElapsed = (TextView) findViewById(R.id.durationElapsed);
        rec = new ReceiptUtils();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runNetManager();
                System.out.println("ANGELO : [" + new Date().toString() + "]" );
            }
        },25000,60000 * 5);

        initDialog();
        //connClick();
    }


    @Override
    protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        switch (requestCode) {
            case CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    GLOBALS.getInstance().getDialog().show();
                    new Thread(new Runnable(){
                        public void run() {
                            GLOBALS.getInstance().getMyOperation().open(data);
                        }
                    }).start();
                }
                break;
            case ENABLE_BT:
                if (resultCode == Activity.RESULT_OK){
                    GLOBALS.getInstance().getMyOperation().chooseDevice();
                }else{
                    Toast.makeText(this, R.string.bt_not_enabled, Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void openConn(){
        if (!isConnected) {
            //myOperation = (IPrinterOperation) new BluetoothOperation(ParkingExitActivity.this, mHandler);
            //myOperation.chooseDevice();
            GLOBALS.getInstance().setMyOperation((IPrinterOperation) new BluetoothOperation(context, mHandler));
            //GLOBALS.getInstance().getMyOperation().btAutoConn(context,  mHandler);
            GLOBALS.getInstance().getMyOperation().chooseDevice();
        }

    }

    private void resetConn(){
        GLOBALS.getInstance().setMyOperation((IPrinterOperation) new BluetoothOperation(context, mHandler));
        GLOBALS.getInstance().getMyOperation().chooseDevice();

    }

    public void runNetManager()
    {
        Cursor res = null;
        HttpHandler sh = new HttpHandler(dbh);
        String pattern = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        //vipDATA[0] = cardID;
        res = dbh.getLastDateData();
        if (res == null) {
            return;
        }
        else if (res.getCount() == 0) {
            Date now = new Date();
            String n = sdf.format(now);
            System.out.println(n);
            n = "2021-01-01 00:00:00";
            dbh.insertLDCandLDM(VIP_TABLE_NAME, n, n);
            dbh.insertLDCandLDM(XREAD_TABLE_NAME, n, n);
            dbh.insertLDCandLDM(EXIT_TABLE_NAME, n, n);
            //sh.getNewVIPFromServer(SERVER_NAME + "/vipchecknew.php?from=", n);
        }
        else {


            //-----------------Exit Transactions Upload 2 Server

            res = dbh.getLastDateData(EXIT_TABLE_NAME);
            res.moveToFirst();

            while (res.isAfterLast() == false) {

                String ldm = res.getString(res.getColumnIndex(NET_MANAGER_COLUMN_LDM));

                System.out.println("ANGELO : Last Date Modified: " + ldm);

                try {
                    Date ldm_date = sdf.parse(ldm);

                    System.out.println("ANGELO : DATE Last Date Modified: " + ldm_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Cursor data4uploading = dbh.getExitDataForServer(ldm);
                data4uploading.moveToFirst();
                while (data4uploading.isAfterLast() == false) {
                    String a = data4uploading.getString(data4uploading.getColumnIndex("ReceiptNumber"));
                    String b = data4uploading.getString(data4uploading.getColumnIndex("CashierName"));
                    String c = data4uploading.getString(data4uploading.getColumnIndex("EntranceID"));
                    String d = data4uploading.getString(data4uploading.getColumnIndex("ExitID"));
                    String e = data4uploading.getString(data4uploading.getColumnIndex("CardNumber"));
                    String f = data4uploading.getString(data4uploading.getColumnIndex("PlateNumber"));
                    String g = data4uploading.getString(data4uploading.getColumnIndex("ParkerType"));
                    String h = data4uploading.getDouble(data4uploading.getColumnIndex("NetOfDiscount")) + "";
                    String i = data4uploading.getDouble(data4uploading.getColumnIndex("Amount")) + "";
                    String j = data4uploading.getDouble(data4uploading.getColumnIndex("GrossAmount")) + "";
                    String k = data4uploading.getDouble(data4uploading.getColumnIndex("discount")) + "";
                    String l = data4uploading.getDouble(data4uploading.getColumnIndex("vatAdjustment")) + "";
                    String m = data4uploading.getDouble(data4uploading.getColumnIndex("vat12")) + "";
                    String n = data4uploading.getDouble(data4uploading.getColumnIndex("vatsale")) + "";
                    String o = data4uploading.getDouble(data4uploading.getColumnIndex("vatExemptedSales")) + "";
                    String p = data4uploading.getString(data4uploading.getColumnIndex("tendered"));
                    String q = data4uploading.getString(data4uploading.getColumnIndex("changeDue"));
                    String r = data4uploading.getString(data4uploading.getColumnIndex("DateTimeIN"));
                    String s = data4uploading.getString(data4uploading.getColumnIndex("DateTimeOUT"));
                    String t = data4uploading.getString(data4uploading.getColumnIndex("HoursParked"));
                    String u = data4uploading.getString(data4uploading.getColumnIndex("MinutesParked"));
                    String v = data4uploading.getString(data4uploading.getColumnIndex("SettlementRef"));
                    String w = data4uploading.getString(data4uploading.getColumnIndex("SettlementName"));
                    String x = data4uploading.getString(data4uploading.getColumnIndex("SettlementAddr"));
                    String y = data4uploading.getString(data4uploading.getColumnIndex("SettlementTIN"));
                    String z = data4uploading.getString(data4uploading.getColumnIndex("SettlementBusStyle"));
String sql = "INSERT INTO `carpark`.`exit_trans` (`pkid`, `void`, `voidRefID`, `ReceiptNumber`, `CashierName`, `EntranceID`, `ExitID`, `CardNumber`, `PlateNumber`, `ParkerType`, `NetOfDiscount`, `Amount`, `GrossAmount`, `discount`, `vatAdjustment`, `vat12`, `vatsale`, `vatExemptedSales`, `tendered`, `changeDue`, `DateTimeIN`, `DateTimeOUT`, `HoursParked`, `MinutesParked`, `SettlementRef`, `SettlementName`, `SettlementAddr`, `SettlementTIN`, `SettlementBusStyle`) " +
        "VALUES (NULL, '0', NULL, '"+a+"', '"+b+"', '"+c+"', '"+d+"', '"+e+"', '"+f+"', '"+g+"', '"+h+"', '"+i+"', '"+j+"', '"+k+"', '"+l+"', '"+m+"', '"+n+"', '"+o+"', '"+p+"', '"+q+"', '"+r+"', '"+s+"', '"+t+"', '"+u+"', '"+v+"', '"+w+"', '"+x+"', '"+y+"', '"+z+"')";
                    System.out.println("ANGELO :" + sql);
                    String retStr = sh.updateData2Server(SERVER_NAME + "/upload2server.php?sql=", sql, s);

                    System.out.println("ANGELO : returned from Server Uploading : " + retStr);

                    //force
                    dbh.updateLDM(EXIT_TABLE_NAME, s);
                    data4uploading.moveToNext();
                }

                //ONLY RETURNS ONE ROW ANYWAYS
                res.moveToNext();
            }


            //-----------------X Reading Upload 2 Server

            res = dbh.getLastDateData(XREAD_TABLE_NAME);
            res.moveToFirst();

            while (res.isAfterLast() == false) {

                String ldm = res.getString(res.getColumnIndex(NET_MANAGER_COLUMN_LDM));

                System.out.println("ANGELO : Last Date Modified: " + ldm);

                try {
                    Date ldm_date = sdf.parse(ldm);
                    System.out.println("ANGELO : DATE Last Date Modified: " + ldm_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Cursor data4uploading = dbh.getXReadDataForServer(ldm);
                data4uploading.moveToFirst();
                while (data4uploading.isAfterLast() == false) {
                    String a = data4uploading.getString(data4uploading.getColumnIndex("logINID"));
                    String b = data4uploading.getString(data4uploading.getColumnIndex("SentinelID"));
                    String c = data4uploading.getString(data4uploading.getColumnIndex("userCode"));
                    String d = data4uploading.getString(data4uploading.getColumnIndex("userName"));
                    String e = data4uploading.getString(data4uploading.getColumnIndex("loginStamp"));
                    String f = data4uploading.getString(data4uploading.getColumnIndex("logoutStamp"));
                    String g = data4uploading.getString(data4uploading.getColumnIndex("extendedCount"));
                    String h = data4uploading.getDouble(data4uploading.getColumnIndex("extendedAmount")) + "";
                    String i = data4uploading.getString(data4uploading.getColumnIndex("overnightCount")) + "";
                    String j = data4uploading.getDouble(data4uploading.getColumnIndex("overnightAmount")) + "";
                    String k = data4uploading.getString(data4uploading.getColumnIndex("carServed")) + "";
                    String l = data4uploading.getString(data4uploading.getColumnIndex("totalCount")) + "";
                    String m = data4uploading.getDouble(data4uploading.getColumnIndex("totalAmount")) + "";
                    String n = data4uploading.getString(data4uploading.getColumnIndex("grossCount")) + "";
                    String o = data4uploading.getDouble(data4uploading.getColumnIndex("grossAmount")) + "";
                    String p = data4uploading.getString(data4uploading.getColumnIndex("vat12Count")) + "";
                    String q = data4uploading.getDouble(data4uploading.getColumnIndex("vat12Amount")) + "";
                    String r = data4uploading.getString(data4uploading.getColumnIndex("vatsaleCount")) + "";
                    String s = data4uploading.getDouble(data4uploading.getColumnIndex("vatsaleAmount")) + "";
                    String t = data4uploading.getString(data4uploading.getColumnIndex("vatExemptedSalesCount")) + "";
                    String u = data4uploading.getDouble(data4uploading.getColumnIndex("vatExemptedSalesAmount")) + "";
                    String v = data4uploading.getString(data4uploading.getColumnIndex("exemptedVat12Count")) + "";
                    String w = data4uploading.getDouble(data4uploading.getColumnIndex("exemptedVat12Amount")) + "";
                    String x = data4uploading.getString(data4uploading.getColumnIndex("pwdDiscountCount")) + "";
                    String y = data4uploading.getDouble(data4uploading.getColumnIndex("pwdDiscountAmount")) + "";
                    String z = data4uploading.getString(data4uploading.getColumnIndex("seniorDiscountCount")) + "";
                    String A = data4uploading.getDouble(data4uploading.getColumnIndex("seniorDiscountAmount")) + "";
                    String B = data4uploading.getString(data4uploading.getColumnIndex("localseniorDiscountCount")) + "";
                    String C = data4uploading.getDouble(data4uploading.getColumnIndex("localseniorDiscountAmount")) + "";
                    String D = data4uploading.getString(data4uploading.getColumnIndex("vatAdjPWDCount")) + "";
                    String E = data4uploading.getDouble(data4uploading.getColumnIndex("vatAdjPWDAmount")) + "";
                    String F = data4uploading.getString(data4uploading.getColumnIndex("vatAdjSeniorCount")) + "";
                    String G = data4uploading.getDouble(data4uploading.getColumnIndex("vatAdjSeniorAmount")) + "";
                    String H = data4uploading.getString(data4uploading.getColumnIndex("vatAdjLocalSeniorCount")) + "";
                    String I = data4uploading.getDouble(data4uploading.getColumnIndex("vatAdjLocalSeniorAmount")) + "";
                    String J = data4uploading.getString(data4uploading.getColumnIndex("vat12PWDCount")) + "";
                    String K = data4uploading.getDouble(data4uploading.getColumnIndex("vat12PWDAmount")) + "";
                    String L = data4uploading.getString(data4uploading.getColumnIndex("vat12SeniorCount")) + "";
                    String M = data4uploading.getDouble(data4uploading.getColumnIndex("vat12SeniorAmount")) + "";
                    String N = data4uploading.getString(data4uploading.getColumnIndex("vat12LocalSeniorCount")) + "";
                    String O = data4uploading.getDouble(data4uploading.getColumnIndex("vat12LocalSeniorAmount")) + "";
                    String P = data4uploading.getString(data4uploading.getColumnIndex("voidsCount")) + "";
                    String Q = data4uploading.getDouble(data4uploading.getColumnIndex("voidsAmount")) + "";
                    String R = data4uploading.getString(data4uploading.getColumnIndex("refundCount")) + "";
                    String S = data4uploading.getDouble(data4uploading.getColumnIndex("refundAmount")) + "";
                    String T = data4uploading.getString(data4uploading.getColumnIndex("regularCount")) + "";
                    String U = data4uploading.getDouble(data4uploading.getColumnIndex("regularAmount")) + "";
                    String V = data4uploading.getString(data4uploading.getColumnIndex("vipCount")) + "";
                    String W = data4uploading.getDouble(data4uploading.getColumnIndex("vipAmount")) + "";
                    String X = data4uploading.getString(data4uploading.getColumnIndex("graceperiodCount")) + "";
                    String Y = data4uploading.getDouble(data4uploading.getColumnIndex("graceperiodAmount")) + "";
                    String Z = data4uploading.getString(data4uploading.getColumnIndex("lostCount")) + "";
                    String a1= data4uploading.getDouble(data4uploading.getColumnIndex("lostAmount")) + "";
                    String b1= data4uploading.getString(data4uploading.getColumnIndex("promoCount")) + "";
                    String c1 = data4uploading.getDouble(data4uploading.getColumnIndex("promoAmount")) + "";
                    String d1 = data4uploading.getString(data4uploading.getColumnIndex("localseniorCount")) + "";
                    String e1 = data4uploading.getDouble(data4uploading.getColumnIndex("localseniorAmount")) + "";
                    String f1 = data4uploading.getString(data4uploading.getColumnIndex("seniorCount")) + "";
                    String g1 = data4uploading.getDouble(data4uploading.getColumnIndex("seniorAmount")) + "";
                    String h1 = data4uploading.getString(data4uploading.getColumnIndex("pwdCount")) + "";
                    String i1 = data4uploading.getDouble(data4uploading.getColumnIndex("pwdAmount")) + "";
                    String j1 = data4uploading.getString(data4uploading.getColumnIndex("motorcycleCount")) + "";
                    String k1 = data4uploading.getDouble(data4uploading.getColumnIndex("motorcycleAmount")) + "";
                    String l1 = data4uploading.getString(data4uploading.getColumnIndex("jeepCount")) + "";
                    String m1 = data4uploading.getDouble(data4uploading.getColumnIndex("jeepAmount")) + "";
                    String n1 = data4uploading.getString(data4uploading.getColumnIndex("tricycleCount")) + "";
                    String o1 = data4uploading.getDouble(data4uploading.getColumnIndex("tricycleAmount")) + "";
                    String p1 = data4uploading.getString(data4uploading.getColumnIndex("deliveryCount")) + "";
                    String q1 = data4uploading.getDouble(data4uploading.getColumnIndex("deliveryAmount")) + "";
                    String r1 = data4uploading.getString(data4uploading.getColumnIndex("bpoemployeeCount")) + "";
                    String s1 = data4uploading.getDouble(data4uploading.getColumnIndex("bpoemployeeAmount")) + "";
                    String t1 = data4uploading.getString(data4uploading.getColumnIndex("employeesCount")) + "";
                    String u1 = data4uploading.getDouble(data4uploading.getColumnIndex("employeesAmount")) + "";
                    String v1 = data4uploading.getString(data4uploading.getColumnIndex("tenantsCount")) + "";
                    String w1 = data4uploading.getDouble(data4uploading.getColumnIndex("tenantsAmount")) + "";
                    String x1 = data4uploading.getString(data4uploading.getColumnIndex("mabregularCount")) + "";
                    String y1 = data4uploading.getDouble(data4uploading.getColumnIndex("mabregularAmount")) + "";
                    String z1 = data4uploading.getString(data4uploading.getColumnIndex("seniormotorCount")) + "";
                    String A1 = data4uploading.getDouble(data4uploading.getColumnIndex("seniormotorAmount")) + "";
                    String B1 = data4uploading.getString(data4uploading.getColumnIndex("ambulanceCount")) + "";
                    String C1 = data4uploading.getDouble(data4uploading.getColumnIndex("ambulanceAmount")) + "";
                    String D1 = data4uploading.getString(data4uploading.getColumnIndex("inpatientCount")) + "";
                    String E1 = data4uploading.getDouble(data4uploading.getColumnIndex("inpatientAmount")) + "";
                    String F1 = data4uploading.getString(data4uploading.getColumnIndex("dialysisCount")) + "";
                    String G1 = data4uploading.getDouble(data4uploading.getColumnIndex("dialysisAmount")) + "";
                    String H1 = data4uploading.getString(data4uploading.getColumnIndex("ambulatoryCount")) + "";
                    String I1 = data4uploading.getDouble(data4uploading.getColumnIndex("ambulatoryAmount")) + "";

                    String sql = "INSERT INTO `colltrain`.`main` (`logINID`, `SentinelID`, `userCode`, `userName`, `loginStamp`, `logoutStamp`, `extendedCount`, `extendedAmount`, `overnightCount`, `overnightAmount`, `carServed`, `totalCount`, `totalAmount`, `grossCount`, `grossAmount`, `vat12Count`, `vat12Amount`, `vatsaleCount`, `vatsaleAmount`, `vatExemptedSalesCount`, `vatExemptedSalesAmount`, `exemptedVat12Count`, `exemptedVat12Amount`, `pwdDiscountCount`, `pwdDiscountAmount`, `seniorDiscountCount`, `seniorDiscountAmount`, `localseniorDiscountCount`, `localseniorDiscountAmount`, `vatAdjPWDCount`, `vatAdjPWDAmount`, `vatAdjSeniorCount`, `vatAdjSeniorAmount`, `vatAdjLocalSeniorCount`, `vatAdjLocalSeniorAmount`, `vat12PWDCount`, `vat12PWDAmount`, `vat12SeniorCount`, `vat12SeniorAmount`, `vat12LocalSeniorCount`, `vat12LocalSeniorAmount`, `voidsCount`, `voidsAmount`, `refundCount`, `refundAmount`, `regularCount`, `regularAmount`, `vipCount`, `vipAmount`, `graceperiodCount`, `graceperiodAmount`, `lostCount`, `lostAmount`, `promoCount`, `promoAmount`, `localseniorCount`, `localseniorAmount`, `seniorCount`, `seniorAmount`, `pwdCount`, `pwdAmount`, `motorcycleCount`, `motorcycleAmount`, `jeepCount`, `jeepAmount`, `tricycleCount`, `tricycleAmount`, `deliveryCount`, `deliveryAmount`, `bpoemployeeCount`, `bpoemployeeAmount`, `employeesCount`, `employeesAmount`, `tenantsCount`, `tenantsAmount`, `mabregularCount`, `mabregularAmount`, `seniormotorCount`, `seniormotorAmount`, `ambulanceCount`, `ambulanceAmount`, `inpatientCount`, `inpatientAmount`, `dialysisCount`, `dialysisAmount`, `ambulatoryCount`, `ambulatoryAmount`) " +
                            "VALUES ('"+a+"', '"+b+"', '"+c+"', '"+d+"', '"+e+"', '"+f+"', '"+g+"', '"+h+"', '"+i+"', '"+j+"', '"+k+"', '"+l+"', '"+m+"', '"+n+"', '"+o+"', '"+p+"', '"+q+"', '"+r+"', '"+s+"', '"+t+"', '"+u+"', '"+v+"', '"+w+"', '"+x+"','"+y+"', '"+z+"', " +
                            "'"+A+"', '"+B+"', '"+C+"', '"+D+"', '"+E+"', '"+F+"', '"+G+"', '"+H+"', '"+I+"', '"+J+"', '"+K+"', '"+L+"', '"+M+"', '"+N+"', '"+O+"', '"+P+"', '"+Q+"', '"+R+"', '"+S+"', '"+T+"', '"+U+"', '"+V+"', '"+W+"', '"+X+"','"+Y+"', '"+Z+"', " +
                            "'"+a1+"', '"+b1+"', '"+c1+"', '"+d1+"', '"+e1+"', '"+f1+"', '"+g1+"', '"+h1+"', '"+i1+"', '"+j1+"', '"+k1+"', '"+l1+"', '"+m1+"', '"+n1+"', '"+o1+"', '"+p1+"', '"+q1+"', '"+r1+"', '"+s1+"', '"+t1+"', '"+u1+"', '"+v1+"', '"+w1+"', '"+x1+"', '"+y1+"', '"+z1+"', " +
                            "'"+A1+"', '"+B1+"', '"+C1+"', '"+D1+"', '"+E1+"', '"+F1+"', '"+G1+"', '"+H1+"', '"+I1+"');";
                    System.out.println("ANGELO :" + sql);
                    String retStr = sh.updateData2Server(SERVER_NAME + "/upload2server.php?sql=", sql, f);
                    System.out.println("ANGELO : returned from Server Uploading : " + retStr);
                    //FORCE
                    dbh.updateLDM(XREAD_TABLE_NAME, f);
                    data4uploading.moveToNext();
                }

                //ONLY RETURNS ONE ROW ANYWAYS
                res.moveToNext();
            }

            //-----------------ZREADING Upload 2 Server
        }

    }

    private void checkLog() {
        //rec.createNewFile();
        //datetimeOUT.setText(GLOBALS.getInstance().getCashierID() + GLOBALS.getInstance().getCashierName()+GLOBALS.getInstance().getLoginID());
        if(dbh.isDeviceLoggedIN() == false) {
            Intent intent = new Intent(ParkingExitActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        if (dbh.getMaster() == false) {
            dbh.initiateMaster();
        }

        //durationElapsed.setText(GLOBALS.getInstance().getCashierID() + GLOBALS.getInstance().getCashierName()+GLOBALS.getInstance().getLoginID());
        //
        //durationElapsed.setText(rec.getExternalFilesDir(getApplicationContext()));
        //rec.appendToFile("ANGELO");
        openConn();
    }

    @Override
    public void onDeviceConnected(AidlDeviceService serviceManager) {
        try {
            rfcard = AidlRFCard.Stub
                    .asInterface(serviceManager.getRFIDReader());
            setOnCheckListen();
            start_autoRead();

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onDestroy() {
        close();
        super.onDestroy();
    }

    private String getCardType(int cardtype) {
        String ret = "";
        switch (cardtype) {
            case RFCard.RFCardType.UNSUPPORTED:
                ret = "UNSUPPORTED";
                break;
            case RFCard.RFCardType.TYPEA:
                ret = "TYPEA";
                break;
            case RFCard.RFCardType.TYPEB:
                ret = "TYPEB";
                break;
            case RFCard.RFCardType.MIFARE_ONE:
                ret = "MIFARE_ONE";
                break;
            case RFCard.RFCardType.MIFARE_S50:
                ret = "MIFARE_S50";
                break;
            case RFCard.RFCardType.MIFARE_ONE_S70:
                ret = "MIFARE_ONE_S70";
                break;
            case RFCard.RFCardType.MIFARE_ULTRALIGHT_C:
                ret = "MIFARE_ULTRALIGHT_C";
                break;
            case RFCard.RFCardType.MIFARE_PLUS:
                ret = "MIFARE_PLUS";
                break;
            case RFCard.RFCardType.MIFARE_DESFIRE:
                ret = "MIFARE_DESFIRE";
                break;
            case RFCard.RFCardType.MIFARE_CPU:
                ret = "MIFARE_CPU";
                break;
            case RFCard.RFCardType.MIFARE_PRO:
                ret = "MIFARE_PRO";
                break;
            case RFCard.RFCardType.MIFARE_S50_PRO:
                ret = "MIFARE_S50_PRO";
                break;
            case RFCard.RFCardType.MIFARE_S70_PRO:
                ret = "MIFARE_S70_PRO";
                break;

        }
        return ret;
    }

    private void start_autoRead() {
        nfcAutoCheck.startAutoCheck(60);
        System.out.println("ANGELO Start auto check card");
    }

    private void stop_autoRead() {
        if (nfcAutoCheck != null) {
            nfcAutoCheck.stopAutoCheck();
            if (rfcard != null)
                close();
        }
        System.out.println("ANGELO Stoped auto check card");

    }

    public void close() {
        try {
            boolean flag = rfcard.close();
            if (flag) {
                System.out.println("ANGELO " + getStringByid(R.string.nfc_close_success));
            } else {
                System.out.println("ANGELO " + getStringByid(R.string.nfc_close_faile));
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void setOnCheckListen() {
        if (rfcard != null) {
            nfcAutoCheck = new NfcAutoCheck(rfcard);
            nfcAutoCheck.setOnDataReceiveListener(new NfcAutoCheck.OnCheckCardListener() {
                @Override
                public void onCheckCardReceive(boolean isexist, int cardtype) {
                    if (isexist) {
                        //sendmessage("card is exists" + " Card type is " + getCardType(cardtype));
                        System.out.println("ANGELO Card Present:::");
                        cardNumber = getCardId();
                        //cardNum.setText("Checked");
                        stop_autoRead();
                        nfcAutoCheck.startAutoCheck(60 * 1000);
                    } else {
                        System.out.println("ANGELO Card ABSENT:::");
                        //start_autoRead();
                        //sendmessage("card is not exists");
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    datetimeIN.setText("");
                                    datetimeOUT.setText("");
                                    durationElapsed.setText("");
                                    inputPlate.setText("");
                                    cardNum.setText("");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        nfcAutoCheck.startAutoCheck(60 * 1000);
                    }

                }

                @Override
                public void onCheckCardReceive(boolean isexist, int cardtype, String uid) {

                }
            });
        }
    }

    public String getCardId() {
        try {
            Cursor res = null;
            byte[] id = rfcard.getCardCode();
            if (id != null) {
                String cardID = HexUtil.bcd2str(id);
                //sendmessage("Uid:" + cardID);
                cardNumber = cardID;
                System.out.println("ANGELO Uid:" + cardID);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (prevCardNumber.compareToIgnoreCase(cardNumber) != 0) {
                            cardNum.setText(cardNumber);
                            String cardStatus = getExitCard(SERVER_NAME + "/cardCheck.php?rfid=", cardNumber);
                            System.out.println("ANGELO getExitCard:" + retStr + cardStatus);
                        }
                        prevCardNumber = cardNumber;
                    }
                });
                return cardID;
            } else {

            }

        } catch (RemoteException e) {

        }
        return "Empty";
    }

    //this method is actually fetching the json string
    public String getExitCard(final String urlWebService, final String rfid) {

        //final String[] retStr = new String[1];
        /*
         * As fetching the json string is a network operation
         * And we cannot perform a network operation in main thread
         * so we need an AsyncTask
         * The constrains defined here are
         * Void -> We are not passing anything
         * Void -> Nothing at progress update as well
         * String -> After completion it should return a string and it will be the json string
         * */
        class GetJSON extends AsyncTask<Void, Void, String> {

            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some time
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                retStr = s;
            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {

                try {
                    System.out.println("ANGELO : " + urlWebService + rfid);
                    //creating a URL
                    String u = urlWebService + rfid;
                    URL url = new URL(u);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json;
                    int i = 0;
                    //reading until we don't find null

                    while ((json = bufferedReader.readLine()) != null) {
                        i++;
                        //appending it to string builder
                        sb.append(json + "\n");
                        System.out.println("ANGELO : [" + i + "]" + json + "\n");
                        //PROCESS JSON to DB
                        HashMap<String, String> cardData = grabCardFromServer(dbh, json);
                        //dbh.updateLDM(ldm);
                    }
                    if (sb.toString().compareToIgnoreCase("") == 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                datetimeIN.setText("");
                                datetimeOUT.setText("");
                                durationElapsed.setText("");
                                inputPlate.setText("");
                            }
                        });
                    } else {
                        System.out.println("ANGELO : [NO DATA FOUND]");
                    }
                    System.out.println("ANGELO : [" + i + "]" + sb.toString());
                    //finally returning the read string
                    //Date now = new Date();
                    //String n = sdf.format(now);
                    //System.out.println(n);
                    //dbh.updateLDC(ldm);
                    //retJSON[0] = readJSON(json);
                    return "EXISTS";
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
        return "";
    }


    private void connClick(){
        if(GLOBALS.getInstance().isConnected()){
            GLOBALS.getInstance().getMyOperation().close();
            GLOBALS.getInstance().setMyOperation(null);
            GLOBALS.getInstance().setmPrinter(null);
        }else{

            new AlertDialog.Builder(context)
                    .setTitle(R.string.str_message)
                    .setMessage(R.string.str_connlast)
                    .setPositiveButton(R.string.yesconn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            openConn();
                        }
                    })
                    .setNegativeButton(R.string.str_resel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            resetConn();
                        }
                    })
                    .show();


        }
    }

    private void tipUpdate(final String filePath){
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage(filePath + "\n请确认打印机版本是否支持")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        printUpdate(filePath);
                    }
                })
                .setNegativeButton("取消", null)
                .setCancelable(false)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private ParkingExitActivity.MyTask myTask;

    /**
     * wifi机器需要定时读取打印机数据, 如下代码
     * 当连接断开时, 读取数据 read() 会触发断开连接的消息
     *
     * USB 蓝牙 可忽略
     */
    private class MyTask extends java.util.TimerTask{
        @Override
        public void run() {
            if(GLOBALS.getInstance().isConnected() && GLOBALS.getInstance().getmPrinter() != null) {
                byte[] by = GLOBALS.getInstance().getmPrinter().read();
                if (by != null) {
                    System.out.println(GLOBALS.getInstance().getmPrinter().isConnected() + " read byte " + Arrays.toString(by));
                }
            }
        }
    }


    /**
     * 更新界面状态
     */
    private void updateButtonState() {
        if(!GLOBALS.getInstance().isConnected()){
            //connectAddress.setText(R.string.no_conn_address);
            //connectState.setText(R.string.connect);
            //connectName.setText(R.string.no_conn_name);
        }else{
            if( bt_mac!=null && !bt_mac.equals("")){
                //connectAddress.setText(getString(R.string.str_address)+ bt_mac);
                //connectState.setText(R.string.disconnect);
                //connectName.setText(getString(R.string.str_name)+bt_name);
            }else if(bt_mac==null ) {
                //bt_mac= BluetoothPort.getmDeviceAddress();
                //bt_name=BluetoothPort.getmDeviceName();
                //connectAddress.setText(getString(R.string.str_address)+bt_mac);
                //connectState.setText(R.string.disconnect);
                //connectName.setText(getString(R.string.str_name)+bt_name);
            }
        }
    }

    private void initDialog(){
        GLOBALS.getInstance().setDialog(new ProgressDialog(context));
        GLOBALS.getInstance().getDialog().setProgressStyle(ProgressDialog.STYLE_SPINNER);
        GLOBALS.getInstance().getDialog().setTitle("Connecting...");
        GLOBALS.getInstance().getDialog().setMessage("Please Wait...");
        GLOBALS.getInstance().getDialog().setIndeterminate(true);
        GLOBALS.getInstance().getDialog().setCancelable(false);
        //GLOBALS.getInstance().setDialog(dialog);
    }

    private void printBTText(String text){
        if(!GLOBALS.getInstance().isConnected() && GLOBALS.getInstance().getmPrinter() == null) {
            return;
        }
        new Thread(){
            @Override
            public void run() {
                PrintUtils.printBTText(GLOBALS.getInstance().getmPrinter(), "This is a Test");
            }
        }.start();
    }

    private void printUpdate(final String filePath){
        if(!GLOBALS.getInstance().isConnected() && GLOBALS.getInstance().getmPrinter() == null) {
            return;
        }
        GLOBALS.getInstance().getDialog().setTitle(null);
        GLOBALS.getInstance().getDialog().setMessage("正在升级, 请稍后...");
        GLOBALS.getInstance().getDialog().show();
        new Thread(){
            @Override
            public void run() {
                //PrintUtils.printUpdate(context.getResources(), GLOBALS.getInstance().getmPrinter(), filePath);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "数据已发送, 请等待打开机升级完成", Toast.LENGTH_LONG).show();
                        initDialog();
                    }
                });
            }
        }.start();

    }
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PrinterConstants.Connect.SUCCESS:
                    isConnected = true;
                    GLOBALS.getInstance().setmPrinter(GLOBALS.getInstance().getMyOperation().getPrinter());
                    break;
                case PrinterConstants.Connect.FAILED:
                    isConnected = false;
                    Toast.makeText(context, "connect failed...", Toast.LENGTH_SHORT).show();
                    break;
                case PrinterConstants.Connect.CLOSED:
                    isConnected = false;
                    Toast.makeText(context, "connect close...", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }

            //updateButtonState();

            if (GLOBALS.getInstance().getDialog() != null && GLOBALS.getInstance().getDialog().isShowing()) {
                GLOBALS.getInstance().getDialog().dismiss();
            }
        }

    };


    private String bt_mac;
    private String bt_name;



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    //SD卡读写权限
    String[] permisions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private boolean hasSDcardPermissions() {
        //判断是否有权限
        if (EasyPermission.hasPermissions(context, permisions)) {
            return true;
        } else {
            EasyPermission.with(this)
                    .rationale("选择文件需要SDCard读写权限")
                    .addRequestCode(REQUEST_PERMISSION)
                    .permissions(permisions)
                    .request();
        }
        return false;
    }

    private void startSelectFile(){
        if(!GLOBALS.getInstance().isConnected() && GLOBALS.getInstance().getmPrinter() == null) {
            return;
        }

        if(hasSDcardPermissions()){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, REQUEST_SELECT_FILE);
        }
    }

    //有权限
    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
        startSelectFile();
    }


    //没有权限
    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {

        // 是否用户拒绝,不在提示
        boolean isAskAgain = EasyPermission.checkDeniedPermissionsNeverAskAgain(
                this,
                "选择文件需要开启权限，请在应用设置开启权限。",
                R.string.gotoSettings, R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }, perms);
    }


    private HashMap<String, String> grabCardFromServer(DBHelper dbh, String msg) {
        //String url = "http://api.androidhive.info/contacts/";
        //url = "http://192.168.1.116/timecheck.php";
        //HttpHandler sh = new HttpHandler();

        // Making a request to url and getting response
        //String jsonStr = sh.makeServiceCall(url);
        final JSONObject c;
        try {
            c = new JSONObject(msg);

            // tmp hash map for single contact
            HashMap<String, String> cardData = new HashMap<>();

            // adding each child node to HashMap key => value
            cardData.put(CARD_COLUMN_CARDCODE, c.getString(CARD_COLUMN_CARDCODE));
            cardData.put(CARD_COLUMN_LANE, c.getString(CARD_COLUMN_LANE));
            cardData.put(CARD_COLUMN_PC, c.getString(CARD_COLUMN_PC));
            cardData.put(CARD_COLUMN_PLATE, c.getString(CARD_COLUMN_PLATE));
            cardData.put(CARD_COLUMN_VEHICLE, c.getString(CARD_COLUMN_VEHICLE));
            cardData.put(CARD_COLUMN_TIMEIN, c.getString(CARD_COLUMN_TIMEIN));
            cardData.put("TimeOut", c.getString("TimeOut"));

            // adding contact to contact
            //sendmessage(c.getString("id") + " : " + c.getString("name") + " : " + c.getString("email") + " : " + c.getString("mobile"));
            System.out.println("ANGELO: " + c.getString(CARD_COLUMN_CARDCODE) + " : " + c.getString("TimeOut") + " : " + c.getString(CARD_COLUMN_LANE)
                    + " : " + c.getString(CARD_COLUMN_TIMEIN) + " : " + c.getString(CARD_COLUMN_VEHICLE)
                    + " : " + c.getString(CARD_COLUMN_PC) + " : " + c.getString(CARD_COLUMN_PLATE));

            GLOBALS.getInstance().setEntID(c.getString(CARD_COLUMN_PC));

            String pattern = "yyyy-MM-dd HH:mm:ss";
            final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            //final Date now = new Date();
            final Date nowFromServer = sdf.parse(c.getString("TimeOut"));
            Date dtIN = sdf.parse(c.getString(CARD_COLUMN_TIMEIN));
            System.out.println("ANGELO : dtIN=" + dtIN);
            //final String n = sdf.format(c.getString("TimeOut"));
            //System.out.println("ANGELO : now=" + n);
            long diffInMills = nowFromServer.getTime() - dtIN.getTime();
            System.out.println("ANGELO : diffInMills=" + diffInMills);

            //final long diffMins = TimeUnit.MINUTES.convert(diffInMills, TimeUnit.MILLISECONDS);
           // final long diffHours = TimeUnit.HOURS.convert(diffMins, TimeUnit.MINUTES);
           // final long diffDays = TimeUnit.DAYS.convert(diffHours, TimeUnit.HOURS);

            final long secondsElapsed = diffInMills / 1000;
            int minutesElapsed = (int) (secondsElapsed / 60);
            int hrsElapsed = minutesElapsed / 60;
            daysElapsed = hrsElapsed / 24;
            minutesElapsed = minutesElapsed - (hrsElapsed * 60);
            hrsElapsed = hrsElapsed - (daysElapsed * 24);

            minsRemaining = minutesElapsed;
            hrsRemaining = hrsElapsed;

            System.out.println("ANGELO : seconds Elapsed [" + secondsElapsed + "]");
            System.out.println("ANGELO : minutes Elapsed [" + minutesElapsed + "]");

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    try {
                        GLOBALS.getInstance().setNewCard(true);
                        String dtIN = c.getString(CARD_COLUMN_TIMEIN);
                        String dtOUT = sdf.format(nowFromServer);

                        datetimeIN.setText(dtIN);
                        datetimeOUT.setText(dtOUT);
                        durationElapsed.setText(daysElapsed + " days "  + hrsRemaining + " hours " + minsRemaining + " mins ");

                        GLOBALS.getInstance().setDatetimeIN(dtIN);
                        GLOBALS.getInstance().setDatetimeOUT(dtOUT);
                        GLOBALS.getInstance().setDaysElapsed(daysElapsed + "");
                        GLOBALS.getInstance().setHoursElapsed(hrsRemaining + "");
                        GLOBALS.getInstance().setMinutesElapsed(minsRemaining + "");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            //For Rooted Systems only
            //changeSystemTime(nowFromServer.getYear() + "",nowFromServer.getMonth() + "",nowFromServer.getDate() + "",nowFromServer.getHours() + "",
             //       nowFromServer.getMinutes()+"",nowFromServer.getSeconds()+"");
            //String cardID, String parkerType, String plateNumber, String name, String cardNumber, int maxUse, int status, String ldc, String ldm)
            //boolean inserted = dbh.insertParkerCard(c.getString(CARD_COLUMN_CARDCODE), c.getString(CARD_COLUMN_VEHICLE), c.getString(CARD_COLUMN_PLATE),
            //        c.getString(CARD_COLUMN_LANE), c.getString(CARD_COLUMN_PC), c.getString(CARD_COLUMN_TIMEIN));
            return cardData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private void changeSystemTime(String year,String month,String day,String hour,String minute,String second){
        try {

            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            //MMddhhmmyy
            String command = "date "+month+day+hour+minute+year+"\n";
            // Log.e("command",command);
            os.writeBytes(command);
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();

        } catch (InterruptedException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}