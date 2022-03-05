package com.theoretics.mobilepos.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.theoretics.mobilepos.R;
import com.theoretics.mobilepos.adapter.OperatorInfoAdapter;
import com.theoretics.mobilepos.bean.OperatorInfo;
import com.theoretics.mobilepos.util.BaseUtils;
import com.theoretics.mobilepos.util.ByteUtils;
import com.theoretics.mobilepos.util.DBHelper;
import com.theoretics.mobilepos.util.HexUtil;
import com.theoretics.mobilepos.util.HttpHandler;
import com.theoretics.mobilepos.util.NfcAutoCheck;
import com.theoretics.mobilepos.util.PBOCLIB;
import com.topwise.cloudpos.aidl.AidlDeviceService;
import com.topwise.cloudpos.aidl.rfcard.AidlRFCard;
import com.topwise.cloudpos.data.RFCard;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.theoretics.mobilepos.bean.CONSTANTS.SERVER_NAME;
import static com.theoretics.mobilepos.util.DBHelper.NET_MANAGER_COLUMN_LDC;
import static com.theoretics.mobilepos.util.DBHelper.NET_MANAGER_COLUMN_LDM;
import static com.theoretics.mobilepos.util.DBHelper.VIP_COLUMN_CARDCODE;
import static com.theoretics.mobilepos.util.DBHelper.VIP_COLUMN_NAME;
import static com.theoretics.mobilepos.util.DBHelper.VIP_COLUMN_PARKERTYPE;
import static com.theoretics.mobilepos.util.DBHelper.VIP_COLUMN_PLATENUMBER;
import static com.theoretics.mobilepos.util.DBHelper.VIP_COLUMN_STATUS;
import static com.theoretics.mobilepos.util.DBHelper.VIP_TABLE_NAME;

public class VIPCardActivity extends BaseActivity {
    private ListView listView;
    private ListView listView_cardtype;
    private List<OperatorInfo> operatorInfoList_cardtype = new ArrayList<>();
    private OperatorInfoAdapter adapter_cardtype = null;

    public AidlRFCard rfcard = null;
    private DBHelper dbh;

    private List<OperatorInfo> operatorInfoList = new ArrayList<>();
    private OperatorInfoAdapter adapter = null;
    private TextView txt_log = null;
    private TextView tv_cardNumber = null;
    private TextView tv_vipName, tv_vipStatus;
    private TextView tv_vipType;
    private TextView tv_plateNumber;
    private Button ttlM1 = null;
    private Button buttonBack = null;

    private final int SHOW_MESSAGE = 0;
    private String TAG = "SwipeCardActivity";
    private String strmess = "";
    private String activity_name = "";

    //pboc
    private PBOCLIB pboclib = new PBOCLIB();
    byte[] lastapdu = null;
    private boolean isReadCardId = false;

    private NfcAutoCheck nfcAutoCheck = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        initView();
        //initData();
        dbh = new DBHelper(this);

    }

    @Override
    public void onDeviceConnected(AidlDeviceService serviceManager) {
        try {
            rfcard = AidlRFCard.Stub
                    .asInterface(serviceManager.getRFIDReader());
            setOnCheckListen();
            //start_autoRead();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy(){
        close();
        super.onDestroy();
    }

    private void initView()
    {
        String title = "";
        Intent intent = this.getIntent();
        if(intent!=null)
        {
            title = "mustafa";
            activity_name = "cards";
        }
        //listView = (ListView)findViewById(R.id.list);
        //listView.setOnItemClickListener(onItemClickListener);

        //listView_cardtype= (ListView)findViewById(R.id.list_cardtype);
        ///listView_cardtype.setOnItemClickListener(onItemClickListener_cardtype);
        ttlM1 = (Button)findViewById(R.id.ttlM1);
        ttlM1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isExists();
            }
        });
        //buttonBack = (Button)findViewById(R.id.buttonBack);
        //buttonBack.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        AsyncDataInit initDB = new AsyncDataInit();
                //initDB.execute();
                //readJSON();
                //getJSON("http://192.168.1.116/timecheck.php");


                //readJSON(sh.retStr);
        //    }
        //});

       // new Timer().scheduleAtFixedRate(new TimerTask() {
        //    @Override
        //    public void run() {
                //getNewVIPS();
                //System.out.println("ANGELO : [" + new Date().toString() + "]" );
        //    }
        //},5000,60000 * 2);

        txt_log = (TextView)findViewById(R.id.status);
        tv_cardNumber = (TextView)findViewById(R.id.cardNumber);
        tv_plateNumber = (TextView) findViewById(R.id.loginPassword);
        tv_vipName = (TextView) findViewById(R.id.vipName);
        tv_vipType = (TextView) findViewById(R.id.vipType);
        tv_vipStatus = (TextView) findViewById(R.id.vipStatus);
        txt_log.setMovementMethod(ScrollingMovementMethod.getInstance());
        //findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        finish();
        //    }
        //});
        //((TextView)findViewById(R.id.header_title)).setText(title);

    }

    private void setOnCheckListen()
    {
        if(rfcard!=null){
            nfcAutoCheck = new NfcAutoCheck(rfcard);
            nfcAutoCheck.setOnDataReceiveListener(new NfcAutoCheck.OnCheckCardListener() {
                @Override
                public void onCheckCardReceive(boolean isexist,int cardtype) {
                    if(isexist)
                    {
                        sendmessage("card is exists" + " Card type is " + getCardType(cardtype));
                        getCardId();
                        //stop_autoRead();
                        //start_autoRead();
                    }else
                    {
                        sendmessage("card is not exists");
                    }
                    nfcAutoCheck.startAutoCheck(60);
                }

                @Override
                public void onCheckCardReceive(boolean isexist, int cardtype, String uid) {

                }
            });
        }
    }

    private void initData()
    {
        operatorInfoList_cardtype.clear();
        if(activity_name.equals(BaseUtils.ACTIVITY_NAME_NFC)) {
            operatorInfoList_cardtype.add(new OperatorInfo(this.getString(R.string.card_type_m1), "card_type_m1".toLowerCase()));
            operatorInfoList_cardtype.add(new OperatorInfo(this.getString(R.string.card_type_nfc_cpu), "card_type_nfc_cpu".toLowerCase()));
            operatorInfoList_cardtype.add(new OperatorInfo(this.getString(R.string.card_type_auto), "card_type_auto".toLowerCase()));
            operatorInfoList_cardtype.add(new OperatorInfo(this.getString(R.string.ntag), "card_type_ntag".toLowerCase()));
        }
        adapter_cardtype = new OperatorInfoAdapter(this,operatorInfoList_cardtype);
        adapter_cardtype.setData(operatorInfoList_cardtype);
        listView_cardtype.setAdapter(adapter_cardtype);
    }

    private final AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position > operatorInfoList.size()) return;
            String operatorname = operatorInfoList.get(position).get_OperatorName();
            String operatorvalue = operatorInfoList.get(position).get_OperatorValue();
            if(activity_name.equals(BaseUtils.ACTIVITY_NAME_NFC)) {
                if (operatorvalue.toLowerCase().equals("nfc_open".toLowerCase())) {
                    open();
                }else if (operatorvalue.toLowerCase().equals("nfc_reset".toLowerCase())) {
                    reset();
                }else if (operatorvalue.toLowerCase().equals("nfc_exists".toLowerCase())) {
                    isExists();
                }else if (operatorvalue.toLowerCase().equals("nfc_cardtype".toLowerCase())) {
                    readCardType();
                }else if (operatorvalue.toLowerCase().equals("nfc_auth".toLowerCase())) {
                    auth();
                }else if (operatorvalue.toLowerCase().equals("nfc_readblock".toLowerCase())) {
                    readBlockData();
                }else if (operatorvalue.toLowerCase().equals("nfc_writeblock".toLowerCase())) {
                    writeBlock();
                }else if (operatorvalue.toLowerCase().equals("nfc_halt".toLowerCase())) {
                    halt();
                }else if (operatorvalue.toLowerCase().equals("nfc_apdu".toLowerCase())) {
                    apduComm();
                }else if (operatorvalue.toLowerCase().equals("nfc_pboc".toLowerCase())) {
                    nfc_pboc();
                }else if (operatorvalue.toLowerCase().equals("nfc_autoread".toLowerCase())) {
                    start_autoRead();
                }else if (operatorvalue.toLowerCase().equals("ntag_open".toLowerCase())) {
                    open();
                }else if (operatorvalue.toLowerCase().equals("ntag_reset".toLowerCase())) {
                    reset();
                }else if (operatorvalue.toLowerCase().equals("ntag_exists".toLowerCase())) {
                    isExists();
                }else if (operatorvalue.toLowerCase().equals("ntag_cardtype".toLowerCase())) {
                    readCardType();
                }else if (operatorvalue.toLowerCase().equals("ntag_auth".toLowerCase())) {
                    auth(4);
                }else if (operatorvalue.toLowerCase().equals("ntag_readblock".toLowerCase())) {
                    readBlockData(4);
                }else if (operatorvalue.toLowerCase().equals("ntag_writeblock".toLowerCase())) {
                    writeBlock(4);
                }else if (operatorvalue.toLowerCase().equals("ntag_halt".toLowerCase())) {
                    halt();
                }
            }


        }
    };

    private final AdapterView.OnItemClickListener onItemClickListener_cardtype = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if(position>operatorInfoList_cardtype.size()) return;
            String operatorname = operatorInfoList_cardtype.get(position).get_OperatorName();
            String operatorvalue = operatorInfoList_cardtype.get(position).get_OperatorValue();

            if (activity_name.equals(BaseUtils.ACTIVITY_NAME_NFC)) {
                if (operatorvalue.toLowerCase().equals("card_type_m1".toLowerCase())) {
                    init_data_listView(operatorvalue.toLowerCase());
                } else if (operatorvalue.toLowerCase().equals("card_type_nfc_cpu".toLowerCase())) {
                    init_data_listView(operatorvalue.toLowerCase());
                }else if (operatorvalue.toLowerCase().equals("card_type_auto".toLowerCase())) {
                    init_data_listView(operatorvalue.toLowerCase());
                }else if (operatorvalue.toLowerCase().equals("card_type_ntag".toLowerCase())) {
                    init_data_listView(operatorvalue.toLowerCase());
                }
            }




        }

    };

    private void init_data_listView(String val)
    {
        if (val.toLowerCase().equals("card_type_m1".toLowerCase())) {
            operatorInfoList.clear();
            operatorInfoList.add(new OperatorInfo(this.getString(R.string.nfc_open), "nfc_open".toLowerCase()));
            operatorInfoList.add(new OperatorInfo(this.getString(R.string.nfc_exists), "nfc_exists".toLowerCase()));
            operatorInfoList.add(new OperatorInfo(this.getString(R.string.nfc_cardtype), "nfc_cardtype".toLowerCase()));
            //operatorInfoList.add(new OperatorInfo(this.getString(R.string.nfc_reset), "nfc_reset".toLowerCase()));
            operatorInfoList.add(new OperatorInfo(this.getString(R.string.nfc_auth), "nfc_auth".toLowerCase()));
            operatorInfoList.add(new OperatorInfo(this.getString(R.string.nfc_readblock), "nfc_readblock".toLowerCase()));
            operatorInfoList.add(new OperatorInfo(this.getString(R.string.nfc_writeblock), "nfc_writeblock".toLowerCase()));
            operatorInfoList.add(new OperatorInfo(this.getString(R.string.nfc_halt), "nfc_halt".toLowerCase()));
        }else if (val.toLowerCase().equals("card_type_nfc_cpu".toLowerCase())) {
            operatorInfoList.clear();
            operatorInfoList.add(new OperatorInfo(this.getString(R.string.nfc_open), "nfc_open".toLowerCase()));
            operatorInfoList.add(new OperatorInfo(this.getString(R.string.nfc_cardtype), "nfc_cardtype".toLowerCase()));
            operatorInfoList.add(new OperatorInfo(this.getString(R.string.nfc_reset), "nfc_reset".toLowerCase()));
            operatorInfoList.add(new OperatorInfo(this.getString(R.string.nfc_apdu), "nfc_apdu".toLowerCase()));
            operatorInfoList.add(new OperatorInfo(this.getString(R.string.nfc_pboc), "nfc_pboc".toLowerCase()));
        }else if (val.toLowerCase().equals("card_type_auto".toLowerCase())) {
            operatorInfoList.clear();
            operatorInfoList.add(new OperatorInfo(this.getString(R.string.nfc_autoread), "nfc_autoread".toLowerCase()));
            operatorInfoList.add(new OperatorInfo(this.getString(R.string.nfc_autoread_stop), "nfc_autoread_stop".toLowerCase()));
        }else if (val.toLowerCase().equals("card_type_ntag".toLowerCase())) {
            operatorInfoList.clear();
            operatorInfoList.add(new OperatorInfo(this.getString(R.string.ntag_open), "ntag_open".toLowerCase()));
            operatorInfoList.add(new OperatorInfo(this.getString(R.string.ntag_reset), "ntag_reset".toLowerCase()));
            operatorInfoList.add(new OperatorInfo(this.getString(R.string.ntag_exists), "ntag_exists".toLowerCase()));
            operatorInfoList.add(new OperatorInfo(this.getString(R.string.ntag_cardtype), "ntag_cardtype".toLowerCase()));
            //operatorInfoList.add(new OperatorInfo(this.getString(R.string.ntag_auth), "ntag_auth".toLowerCase()));
            operatorInfoList.add(new OperatorInfo(this.getString(R.string.ntag_readblock), "ntag_readblock".toLowerCase()));
            operatorInfoList.add(new OperatorInfo(this.getString(R.string.ntag_writeblock), "ntag_writeblock".toLowerCase()));
            operatorInfoList.add(new OperatorInfo(this.getString(R.string.ntag_halt), "ntag_halt".toLowerCase()));
        }
        adapter = new OperatorInfoAdapter(this,operatorInfoList);
        adapter.setData(operatorInfoList);
        listView.setAdapter(adapter);

    }

    public void writeJSON() {
        JSONObject object = new JSONObject();
        try {
            object.put("name", "Jack Hack");
            object.put("score", new Integer(200));
            object.put("current", new Double(152.32));
            object.put("nickname", "Hacker");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(object);
    }

    private void readJSON(String msg) {
        //String url = "http://api.androidhive.info/contacts/";
        //url = "http://192.168.1.116/timecheck.php";
        //HttpHandler sh = new HttpHandler();

        // Making a request to url and getting response
        //String jsonStr = sh.makeServiceCall(url);

        try {
            JSONObject c = new JSONObject(msg);
            String sec = c.getString("sec");
            String min = c.getString("min");
            String hr = c.getString("hr");
            String date = c.getString("date");
            String mon = c.getString("mon");

            // tmp hash map for single contact
            HashMap<String, String> contact = new HashMap<>();

            // adding each child node to HashMap key => value
            contact.put("id", sec);
            contact.put("name", min);
            contact.put("email", hr);
            contact.put("mobile", date);

            // adding contact to contact
            //sendmessage(contact.get("id") + " : " + contact.get("name") + " : " + contact.get("email") + " : " + contact.get("mobile"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);

                // Getting JSON Array node
                JSONArray contacts = jsonObj.getJSONArray("contacts");

                // looping through All Contacts
                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject c = contacts.getJSONObject(i);

                    String id = c.getString("id");
                    String name = c.getString("name");
                    String email = c.getString("email");
                    String address = c.getString("address");
                    String gender = c.getString("gender");

                    // Phone node is JSON Object
                    JSONObject phone = c.getJSONObject("phone");
                    String mobile = phone.getString("mobile");
                    String home = phone.getString("home");
                    String office = phone.getString("office");


                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Json parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }
        } else {
            Log.e(TAG, "Couldn't get json from server.");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Couldn't get json from server. Check LogCat for possible errors!",
                            Toast.LENGTH_LONG)
                            .show();
                }
            });

        }
        */
    }

    private void sendmessage(String mess)
    {
        Message msg = mHandler.obtainMessage();
        msg.what =SHOW_MESSAGE;
        msg.obj=mess;
        mHandler.sendMessage(msg);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case SHOW_MESSAGE:
                    String mess = msg.obj.toString();
                    txt_log.append(mess + "\n");
                    int offset=txt_log.getLineCount()*txt_log.getLineHeight();
                    if(offset>txt_log.getHeight()){
                        txt_log.scrollTo(0,offset-txt_log.getHeight());
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    // 打开设备
    public void open() {
        try {
            boolean flag = rfcard.open();
            if (flag) {
                sendmessage(getStringByid(R.string.nfc_open_success));
            } else {
                sendmessage(getStringByid(R.string.nfc_open_faile));
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 关闭设备
    public void close() {
        try {
            boolean flag = rfcard.close();
            if (flag) {
                sendmessage(getStringByid(R.string.nfc_close_success));
            } else {
                sendmessage(getStringByid(R.string.nfc_close_faile));
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 检测是否为在位
    public void isExists() {
        try {
            boolean flag = rfcard.isExist();
            if (flag) {
                sendmessage(getStringByid(R.string.nfc_card_exist));
            } else {
                sendmessage(getStringByid(R.string.nfc_card_notexist));
            }
            getCardId();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
            sh.getNewVIPFromServer(SERVER_NAME + "/vipchecknew.php?from=", n);
        }
        else {
            res.moveToFirst();

            while (res.isAfterLast() == false) {

                String ldc = res.getString(res.getColumnIndex(NET_MANAGER_COLUMN_LDC));
                String ldm = res.getString(res.getColumnIndex(NET_MANAGER_COLUMN_LDM));

                System.out.println("ANGELO : Last Date Created: " + ldc);
                System.out.println("ANGELO : Last Date Modified: " + ldm);

                try {
                    Date ldc_date = sdf.parse(ldc);
                    Date ldm_date = sdf.parse(ldm);

                    System.out.println("ANGELO : DATE Last Date Created: " + ldc_date);
                    System.out.println("ANGELO : DATE Last Date Modified: " + ldm_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                sh.getNewVIPFromServer(SERVER_NAME + "/vipchecknew.php?from=", ldc);
                sh.getModifiedVIPFromServer(SERVER_NAME + "/vipcheckmodified.php?from=", ldm);

                res.moveToNext();
            }
        }

    }

    public void getNewVIPS()
    {
            Cursor res = null;
            HttpHandler sh = new HttpHandler(dbh);
                String pattern = "yyyy-MM-dd hh:mm:ss";
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);

                //vipDATA[0] = cardID;
                res = dbh.getLastDateData();
                if (res == null) {

                }
                else if (res.getCount() == 0) {
                    Date now = new Date();
                    String n = sdf.format(now);
                    System.out.println(n);
                    n = "2021-01-01 00:00:00";
                    dbh.insertLDCandLDM(VIP_TABLE_NAME, n, n);
                    sh.getNewVIPFromServer(SERVER_NAME + "/vipchecknew.php?from=", n);
                }
                else {
                    res.moveToFirst();

                    while (res.isAfterLast() == false) {

                        String ldc = res.getString(res.getColumnIndex(NET_MANAGER_COLUMN_LDC));
                        String ldm = res.getString(res.getColumnIndex(NET_MANAGER_COLUMN_LDM));

                        System.out.println("ANGELO : Last Date Created: " + ldc);
                        System.out.println("ANGELO : Last Date Modified: " + ldm);

                        try {
                            Date ldc_date = sdf.parse(ldc);
                            Date ldm_date = sdf.parse(ldm);

                            System.out.println("ANGELO : DATE Last Date Created: " + ldc_date);
                            System.out.println("ANGELO : DATE Last Date Modified: " + ldm_date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        sh.getNewVIPFromServer(SERVER_NAME + "/vipchecknew.php?from=", ldc);
                        sh.getModifiedVIPFromServer(SERVER_NAME + "/vipcheckmodified.php?from=", ldm);

                        res.moveToNext();
                    }
                }

    }


    public void getCardId()
    {
        try
        {
            String[] vipDATA = {"","","","", ""};
            Cursor res = null;
            byte[] id = rfcard.getCardCode();
            if(id!=null){
                String cardID = HexUtil.bcd2str(id);
                //sendmessage("Uid:" + cardID);
                System.out.println("Uid:" + cardID);
                vipDATA[0] = cardID;
                res = dbh.getCardData(cardID.toUpperCase());
                if (res.getCount() == 0) {
                    vipDATA[1] = "";
                    vipDATA[2] = "NO Data Found";
                    vipDATA[3] = "";
                    vipDATA[4] = "";
                }
                else {
                    res.moveToFirst();

                    while (res.isAfterLast() == false) {
                        String cardNum = res.getString(res.getColumnIndex(VIP_COLUMN_CARDCODE));
                        String plateNum = res.getString(res.getColumnIndex(VIP_COLUMN_PLATENUMBER));
                        String vipName = res.getString(res.getColumnIndex(VIP_COLUMN_NAME));
                        String vipType = res.getString(res.getColumnIndex(VIP_COLUMN_PARKERTYPE));
                        String vipStatus = res.getString(res.getColumnIndex(VIP_COLUMN_STATUS));


                        vipDATA[1] = plateNum;
                        vipDATA[2] = vipName;
                        vipDATA[3] = vipType;
                        vipDATA[4] = vipStatus;
                        res.moveToNext();
                    }
                }

            }else
            {
                sendmessage("Uid is null");
            }
            tv_cardNumber.setText(vipDATA[0]);
            tv_plateNumber.setText(vipDATA[1]);
            tv_vipName.setText(vipDATA[2]);
            tv_vipType.setText(vipDATA[3]);
            tv_vipStatus.setText(vipDATA[4]);

        }catch(RemoteException e){

        }
    }

    // 上电复位
    public void reset() {
        try {
            int type = rfcard.getCardType();
            byte[] data = rfcard.reset(type);
            if (null != data) {
                sendmessage(getStringByid(R.string.ic_reset_result)+ HexUtil.bcd2str(data));
            } else {
                sendmessage(getStringByid(R.string.ic_reset_faile));
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 断开
    public void halt() {
        try {
            int ret = rfcard.halt();
            if (ret == 0x00) {
                sendmessage(getStringByid(R.string.nfc_halt_success));
            } else {
                sendmessage(getStringByid(R.string.nfc_halt_faile));
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 读取卡类型
    public void readCardType() {
        try {
            int type = rfcard.getCardType();
            if(type == 0) {
                sendmessage(getStringByid(R.string.nfc_read_cardtype_faile));
            } else {
                sendmessage(getStringByid(R.string.nfc_read_cardtype_success) + type);
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            sendmessage(getStringByid(R.string.nfc_read_cardtype_faile));
        }
    }

    // apdu通讯
    public void apduComm() {
        try {
            byte[] apdu = HexUtil
                    .hexStringToByte("00A404000E315041592E5359532E4444463031");
            byte[] data = rfcard.apduComm(apdu);
            if (data != null) {
                sendmessage(getStringByid(R.string.nfc_apdu_success) + HexUtil.bcd2str(data));
            } else {
                sendmessage(getStringByid(R.string.nfc_apdu_faile));
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    // 认证
    public void auth() {
        try {
            int cardType = rfcard.getCardType();
            byte[] resetData = rfcard.reset(cardType);
            if (resetData==null){
                sendmessage(getStringByid(R.string.nfc_card_notexist));
                return;
            }
            int retCode = rfcard.auth((byte) 0x00, (byte) 0x01, new byte[] {
                    (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF }, resetData);
            sendmessage(getStringByid(R.string.nfc_auth_result)+ retCode);
            if (0x00 == retCode) {
                sendmessage(getStringByid(R.string.nfc_auth_success));
            } else {
                sendmessage(getStringByid(R.string.nfc_auth_faile));
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void auth(int block) {
        try {
            int cardType = rfcard.getCardType();
            byte[] resetData = rfcard.reset(cardType);
            if (resetData==null){
                sendmessage(getStringByid(R.string.nfc_card_notexist));
                return;
            }
            int retCode = rfcard.auth((byte) 0x00, (byte) block, new byte[] {
                    (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF }, resetData);
            sendmessage(getStringByid(R.string.nfc_auth_result)+ retCode);
            if (0x00 == retCode) {
                sendmessage(getStringByid(R.string.nfc_auth_success));
            } else {
                sendmessage(getStringByid(R.string.nfc_auth_faile));
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    // 读取块数据
    public void readBlockData() {
        try {
            byte[] data = new byte[16];
            int length = rfcard.readBlock((byte) 0x01, data);
            if (length == 0x00) {
                //showMessage("读取块数据成功" + HexUtil.bcd2str(data));
                sendmessage(getStringByid(R.string.nfc_read_success) + HexUtil.bcd2str(data));
            } else {
                sendmessage(getStringByid(R.string.nfc_read_faile));
            }

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 读取块数据
    public void readBlockData(int block) {
        try {
            byte[] data = new byte[16];
            int length = rfcard.readBlock((byte) block, data);
            if (length == 0x00) {
                //showMessage("读取块数据成功" + HexUtil.bcd2str(data));
                sendmessage(getStringByid(R.string.nfc_read_success) + HexUtil.bcd2str(data));
            } else {
                sendmessage(getStringByid(R.string.nfc_read_faile));
            }

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    // 写块数据
    public void writeBlock() {
        try {
            int retCode = rfcard.writeBlock((byte) 0x01, new byte[] {
                    (byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,
                    (byte)0xFF,(byte)0xFE,(byte)0xFF,(byte)0xFF,
                    (byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,
                    (byte)0x01,(byte)0xFE,(byte)0x01,(byte)0xFE
            });
            sendmessage(getStringByid(R.string.nfc_write_result)+retCode);
            if (retCode == 0x00) {
                sendmessage(getStringByid(R.string.nfc_write_success));
            } else {
                sendmessage(getStringByid(R.string.nfc_write_faile));
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeBlock(int block) {
        try {
            int retCode = rfcard.writeBlock((byte) block, new byte[] {
                    (byte)0x00,(byte)0x01,(byte)0x02,(byte)0x03,
                    (byte)0xFF,(byte)0xFE,(byte)0xFF,(byte)0xFF,
                    (byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,
                    (byte)0x01,(byte)0xFE,(byte)0x01,(byte)0xFE
            });
            sendmessage(getStringByid(R.string.nfc_write_result)+retCode);
            if (retCode == 0x00) {
                sendmessage(getStringByid(R.string.nfc_write_success));
            } else {
                sendmessage(getStringByid(R.string.nfc_write_faile));
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void nfc_pboc()
    {
        boolean flag = false;
        try {
            flag = rfcard.open();
            if (flag) {
                sendmessage(getStringByid(R.string.nfc_open_success));
            } else {
                sendmessage(getStringByid(R.string.nfc_open_faile));
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(flag)
        {
            flag = false;
            //OPEN success
            try {
                int type = rfcard.getCardType();
                byte[] data = rfcard.reset(type);
                if (null != data) {
                    sendmessage(getStringByid(R.string.ic_reset_result)+ HexUtil.bcd2str(data));
                    flag = true;
                } else {
                    sendmessage(getStringByid(R.string.ic_reset_faile));
                }
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else return;
//        if(flag)
//        {
//            flag = false;
//            //reset success
//            try {
//                flag = rfcard.isExist();
//                if (flag) {
//                    sendmessage(getStringByid(R.string.ic_card_exist));
//                } else {
//                    sendmessage(getStringByid(R.string.ic_card_notexist));
//                }
//            } catch (RemoteException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }else return;
        if(flag)
        {
            //CARD ISEXIST
            isReadCardId = true;
            pboclib.init();
            lastapdu = null;
            IC_SEND(pboclib.getsenddata());

        }
    }

    private void IC_SEND(String str)
    {
        byte[] result = null;
        byte[] mCmd = ByteUtils.hexString2ByteArray(str);
        int length = mCmd.length;
        Log.e(TAG, "send = " + ByteUtils.byteArray2HexString(mCmd));
        try {
            result = rfcard.apduComm(mCmd);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        Log.e(TAG, "rec = " + ByteUtils.byteArray2HexString(result));

        if (!TextUtils.isEmpty(ByteUtils.byteArray2HexString(result))) {
            Log.e(TAG, "ATR = " + ByteUtils.byteArray2HexString(result));
            if(isReadCardId)
            {
                String data = pboclib.parsedata(result);
                if(data.length()>0&&pboclib.CARDID.length()<1)
                {
                    IC_SEND(data);
                    return;
                }
                if(pboclib.CARDID.length()>0)
                {
                    Log.d(TAG, "cardNo = " + pboclib.CARDID);
                    sendmessage("CardNo：" + pboclib.CARDID);
                    return;
                }
            }

        }


    }


    private void start_autoRead()
    {
        nfcAutoCheck.startAutoCheck(60);
        sendmessage("Start auto check card");
    }

    private void stop_autoRead()
    {
        if(nfcAutoCheck!=null)
        {
            nfcAutoCheck.stopAutoCheck();
            if(rfcard!=null)
                close();
        }
        sendmessage("Stoped auto check card");

    }


    private String getCardType(int cardtype)
    {
        String ret = "";
        switch (cardtype)
        {
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



    private class AsyncDataInit extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("initializing DB..."); // Calls onProgressUpdate()
            dbh.initCardDatabase();
            return "Done";
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();
            //mynum.setText(result);
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(VIPCardActivity.this,
                    "Init Database...",
                    "Wait for "+ " seconds");
        }


        @Override
        protected void onProgressUpdate(String... text) {
            //mynum.setText(text[0]);

        }
    }


    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        private String hr, min, sec;
        ProgressDialog progressDialog;
        String apiUrl = "http://192.168.1.80/timecheck.php";

        @Override
        protected String doInBackground(String... params) {
            publishProgress("retrieving..."); // Calls onProgressUpdate()
			/*
			try {
				int time = Integer.parseInt(params[0])*1000;

				Thread.sleep(time);
				resp = "Slept for " + params[0] + " seconds";
			} catch (InterruptedException e) {
				e.printStackTrace();
				resp = e.getMessage();
			} catch (Exception e) {
				e.printStackTrace();
				resp = e.getMessage();
			}
			*/

            // implement API in background and store the response in current variable
            String current = "";
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    printTextLine("Retrieving Data from Server...");
                    url = new URL(apiUrl);

                    urlConnection = (HttpURLConnection) url
                            .openConnection();

                    InputStream in = urlConnection.getInputStream();

                    InputStreamReader isw = new InputStreamReader(in);

                    int data = isw.read();
                    while (data != -1) {
                        current += (char) data;
                        data = isw.read();

                    }
                    System.out.print(current);
                    // return the data to onPostExecute method
                    //printTextLine(current);
                    //return "2";

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
            try {
                JSONObject jsonObj = new JSONObject(current);
                hr = jsonObj.getString("hr");
                min = jsonObj.getString("min");
                sec = jsonObj.getString("sec");

                printTextLine("JSON The Hour: " + hr + " Min: " + min + " Sec: " +sec);
                System.out.println("JSON The Hour: " + hr + " Min: " + min + " Sec: " +sec);

                printTextLine(current);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "3";
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();
            //mynum.setText(result);
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(VIPCardActivity.this,
                    "Checking Card...",
                    "Wait for "+ " seconds");
        }


        @Override
        protected void onProgressUpdate(String... text) {
            //mynum.setText(text[0]);

        }
    }


    private void printTextLine(final String text2Print) {
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }


}
