package com.theoretics.mobilepos.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.theoretics.mobilepos.bean.CONSTANTS;
import com.theoretics.mobilepos.bean.GLOBALS;
import com.theoretics.mobilepos.bean.XREADING;
import com.theoretics.mobilepos.bean.ZREADING;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {
//
    //public static final String SERVER_NAME = "http://192.168.100.240";
    //public static final String SERVER_NAME = "http://192.168.1.80";
    public static final String DATABASE_NAME = "mobilepos.db";

    public static final int DATABASE_VERSION = 7;

    public static final String NET_MANAGER_TABLE_NAME = "netmanager";
    public static final String NET_MANAGER_COLUMN_ID = "id";
    public static final String NET_MANAGER_COL_TABLETYPE = "tableName";
    public static final String NET_MANAGER_COLUMN_LDC = "LastDateCreated";
    public static final String NET_MANAGER_COLUMN_LDM = "LastDateModified";

    public static final String MASTER_TABLE_NAME = "master";
    public static final String MASTER_COLUMN_ID = "pkid";
    public static final String MASTER_COLUMN_GRANDTOTAL = "grandTotal";
    public static final String MASTER_COLUMN_GROSSTOTAL = "grossTotal";
    public static final String MASTER_COLUMN_RECEIPTNOS = "receiptNos";
    public static final String MASTER_COLUMN_DATETIMERECORDED = "datatimeRecorded";

    public static final String CARD_TABLE_NAME = "timeindb";
    public static final String CARD_COLUMN_VEHICLE = "Vehicle";
    public static final String CARD_COLUMN_TIMEIN = "Timein";
    public static final String CARD_COLUMN_PC = "PC";
    public static final String CARD_COLUMN_PLATE = "Plate";
    public static final String CARD_COLUMN_CARDCODE = "CardCode";
    public static final String CARD_COLUMN_LANE = "Lane";

    public static final String EXIT_TABLE_NAME = "exit_trans";

    public static final String XREAD_TABLE_NAME = "colltrain";
    public static final String ZREAD_TABLE_NAME = "zread";
    public static final String XREAD_COLUMN_LOGINID = "logINID";
    public static final String ZREAD_COLUMN_LOGINID = "logINID";


    public static final String GIN_TABLE_NAME = "gin";
    public static final String GIN_COLUMN_CASHIERID = "cashierID";
    public static final String GIN_COLUMN_LOGID = "logID";
    public static final String GIN_COLUMN_CASHIERNAME = "cashierName";
    public static final String GIN_COLUMN_LOGINDATE = "loginDate";

    public static final String POS_TABLE_NAME = "posusers";
    public static final String POS_COLUMN_USERNAME = "username";
    public static final String POS_COLUMN_PASSWORD = "password";
    public static final String POS_COLUMN_CASHIERNAME = "cashierName";

    public static final String VIP_TABLE_NAME = "vipcontacts";
    public static final String VIP_COLUMN_ID = "id";
    public static final String VIP_COLUMN_CARDID = "CardID";
    public static final String VIP_COLUMN_PARKERTYPE = "ParkerType";
    public static final String VIP_COLUMN_PLATENUMBER = "PlateNumber";
    public static final String VIP_COLUMN_NAME = "Name";
    public static final String VIP_COLUMN_CARDCODE = "CardCode";
    public static final String VIP_COLUMN_MAXUSE = "MaxUse";
    public static final String VIP_COLUMN_STATUS = "Status";
    public static final String VIP_COLUMN_DATECREATED = "DateCreated";
    public static final String VIP_COLUMN_DATEMODIFIED = "DateModified";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
           "create table " + VIP_TABLE_NAME +
                " (id integer primary key, " +
                   VIP_COLUMN_CARDID + " text," +
                   VIP_COLUMN_PARKERTYPE + " text," +
                   VIP_COLUMN_PLATENUMBER + " text, " +
                   VIP_COLUMN_NAME + " text," +
                   VIP_COLUMN_CARDCODE + " text, " +
                   VIP_COLUMN_MAXUSE + " integer, " +
                   VIP_COLUMN_STATUS + " integer, " +
                   VIP_COLUMN_DATECREATED + " DATETIME, " +
                   VIP_COLUMN_DATEMODIFIED + " DATETIME)"
        );

        db.execSQL("create table " + MASTER_TABLE_NAME +
                " ("+ MASTER_COLUMN_ID + " integer primary key, " +
                MASTER_COLUMN_GRANDTOTAL + " text, " +
                MASTER_COLUMN_GROSSTOTAL + " text, " +
                MASTER_COLUMN_RECEIPTNOS + " text, " +
                MASTER_COLUMN_DATETIMERECORDED + " DATETIME)");

        db.execSQL("CREATE TABLE exit_trans " +
                "(id integer primary key, " +
                "void integer, " +
                "voidRefID text,  " +
                "ReceiptNumber text, " +
                "CashierName text, " +
                "EntranceID text, " +
                "ExitID text, " +
                "CardNumber text, " +
                "PlateNumber text, " +
                "ParkerType text, " +
                "NetOfDiscount real, " +
                "Amount real, " +
                "GrossAmount real, " +
                "discount real, " +
                "vatAdjustment real, " +
                "vat12 real, " +
                "vatsale real, " +
                "vatExemptedSales real, " +
                "tendered real, " +
                "changeDue real, " +
                "DateTimeIN DATETIME, " +
                "DateTimeOUT DATETIME, " +
                "HoursParked integer, " +
                "MinutesParked integer, " +
                "SettlementRef text, " +
                "SettlementName text, " +
                "SettlementAddr text, " +
                "SettlementTIN text, " +
                "SettlementBusStyle text)");

        db.execSQL("CREATE TABLE " + XREAD_TABLE_NAME +
                "(logINID text primary key," +
                "SentinelID text," +
                "userCode text," +
                "userName text," +
                "loginStamp DATETIME," +
                "logoutStamp DATETIME," +
                "extendedCount integer," +
                "extendedAmount real," +
                "overnightCount integer," +
                "overnightAmount real," +
                "carServed integer," +
                "totalCount integer," +
                "totalAmount real," +
                "grossCount integer," +
                "grossAmount real," +
                "vat12Count integer," +
                "vat12Amount real," +
                "vatsaleCount integer," +
                "vatsaleAmount real," +
                "vatExemptedSalesCount integer," +
                "vatExemptedSalesAmount real," +
                "exemptedVat12Count integer," +
                "exemptedVat12Amount real," +
                "pwdDiscountCount integer," +
                "pwdDiscountAmount real," +
                "seniorDiscountCount integer," +
                "seniorDiscountAmount real," +
                "localseniorDiscountCount integer," +
                "localseniorDiscountAmount real," +
                "vatAdjPWDCount integer," +
                "vatAdjPWDAmount real," +
                "vatAdjSeniorCount integer," +
                "vatAdjSeniorAmount real," +
                "vatAdjLocalSeniorCount integer," +
                "vatAdjLocalSeniorAmount real," +
                "vat12PWDCount integer," +
                "vat12PWDAmount real," +
                "vat12SeniorCount integer," +
                "vat12SeniorAmount real," +
                "vat12LocalSeniorCount integer," +
                "vat12LocalSeniorAmount real," +
                "voidsCount integer," +
                "voidsAmount real," +
                "refundCount integer," +
                "refundAmount real," +
                "regularCount integer," +
                "regularAmount real," +
                "vipCount integer," +
                "vipAmount real," +
                "graceperiodCount integer," +
                "graceperiodAmount real," +
                "lostCount integer," +
                "lostAmount real," +
                "promoCount integer," +
                "promoAmount real," +
                "localseniorCount integer," +
                "localseniorAmount real," +
                "seniorCount integer," +
                "seniorAmount real," +
                "pwdCount integer," +
                "pwdAmount real," +
                "motorcycleCount integer," +
                "motorcycleAmount real," +
                "jeepCount integer," +
                "jeepAmount real," +
                "tricycleCount integer," +
                "tricycleAmount real," +
                "deliveryCount integer," +
                "deliveryAmount real," +
                "ambulanceCount integer," +
                "ambulanceAmount real," +
                "ambulatoryCount integer," +
                "ambulatoryAmount real," +
                "bpoemployeeCount integer," +
                "bpoemployeeAmount real," +
                "employeesCount integer," +
                "employeesAmount real," +
                "tenantsCount integer," +
                "tenantsAmount real," +
                "mabregularCount integer," +
                "mabregularAmount real," +
                "seniormotorCount integer," +
                "seniormotorAmount real," +
                "inpatientCount integer," +
                "inpatientAmount real," +
                "dialysisCount integer," +
                "dialysisAmount real)");

        db.execSQL("CREATE TABLE lastdate " +
                "  (pkid integer primary key," +
                "  sentinelID text," +
                "  dt DATETIME)");

        db.execSQL("CREATE TABLE " + ZREAD_TABLE_NAME +
                "  (terminalnum text," +
                "  datetimeOut datetime ," +
                "  datetimeIn datetime," +
                "  todaysale real ," +
                "  todaysGross real," +
                "  vatablesale real ," +
                "  vat real ," +
                "  vatExemptedSales real," +
                "  beginOR integer," +
                "  endOR integer," +
                "  beginTrans text," +
                "  endTrans text," +
                "  beginningVoidNo text," +
                "  endingVoidNo text," +
                "  oldGrand double ," +
                "  newGrand double ," +
                "  oldGrossTotal real," +
                "  newGrossTotal real," +
                "  discounts real," +
                "  voids real," +
                "  manualColl real," +
                "  overrage real," +
                "  zCount integer," +
                "  tellerCode text," +
                "  logINID text primary key" +
                ")");
        
        db.execSQL("create table " + NET_MANAGER_TABLE_NAME +
                " (id integer primary key, " +
                NET_MANAGER_COL_TABLETYPE + " text, " +
                NET_MANAGER_COLUMN_LDC + " DATETIME, " +
                NET_MANAGER_COLUMN_LDM + " DATETIME)");

        db.execSQL("create table " + POS_TABLE_NAME +
                " (id integer primary key, " +
                POS_COLUMN_USERNAME + " text, " +
                POS_COLUMN_PASSWORD + " text, " +
                POS_COLUMN_CASHIERNAME + " text)");

        db.execSQL("create table " + GIN_TABLE_NAME +
                " (" + GIN_COLUMN_LOGID + " text primary key, " +
                GIN_COLUMN_CASHIERID + " text, " +
                GIN_COLUMN_CASHIERNAME + " text, " +
                GIN_COLUMN_LOGINDATE + " DATETIME)");

        db.execSQL("create table " + CARD_TABLE_NAME +
                " (" + CARD_COLUMN_CARDCODE + " text primary key, " +
                CARD_COLUMN_PLATE + " tesxt, " +
                CARD_COLUMN_PC + " text, " +
                CARD_COLUMN_LANE + " text, " +
                CARD_COLUMN_VEHICLE + " text, " +
                CARD_COLUMN_TIMEIN + " DATETIME)");

        fillPOSDB(db);
        //newPOSDB(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        //db.execSQL("DROP TABLE IF EXISTS "+ VIP_TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS "+ GIN_TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS "+ MASTER_TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS "+ POS_TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS "+ CARD_TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS "+ NET_MANAGER_TABLE_NAME);
        if (oldVersion < 4) {
            updatePOSDB4(db);
        }
        if (oldVersion < 5) {
            updatePOSDB5(db);
        }
        //onCreate(db);
    }

    public void saveEXTransaction(String ReceiptNumber,String CashierName, String EntranceID,
                                  String ExitID, String CardNumber, String PlateNumber, String ParkerType,
                                  String NetOfDiscount, String Amount, String GrossAmount, String discount,
                                  String vatAdjustment, String vat12, String vatsale, String vatExemptedSales,
                                  String tendered, String changeDue, String DateTimeIN, String DateTimeOUT,
                                  String HoursParked, String MinutesParked, String SettlementRef,
                                  String SettlementName, String SettlementAddr, String SettlementTIN, String SettlementBusStyle) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ReceiptNumber", ReceiptNumber);
        contentValues.put("CashierName", CashierName);
        contentValues.put("EntranceID", EntranceID);
        contentValues.put("ExitID", ExitID);
        contentValues.put("CardNumber", CardNumber);
        contentValues.put("PlateNumber", PlateNumber);
        contentValues.put("ParkerType", ParkerType);
        contentValues.put("NetOfDiscount", NetOfDiscount);
        contentValues.put("Amount", Amount);
        contentValues.put("GrossAmount", GrossAmount);
        contentValues.put("discount", discount);
        contentValues.put("vatAdjustment", vatAdjustment);
        contentValues.put("vat12", vat12);
        contentValues.put("vatsale", vatsale);
        contentValues.put("vatExemptedSales", vatExemptedSales);
        contentValues.put("tendered", tendered);
        contentValues.put("changeDue", changeDue);
        contentValues.put("DateTimeIN", DateTimeIN);
        contentValues.put("DateTimeOUT", DateTimeOUT);
        contentValues.put("HoursParked", HoursParked);
        contentValues.put("MinutesParked", MinutesParked);
        contentValues.put("SettlementRef", SettlementRef);
        contentValues.put("SettlementName", SettlementName);
        contentValues.put("SettlementAddr", SettlementAddr);
        contentValues.put("SettlementTIN", SettlementTIN);
        contentValues.put("SettlementBusStyle", SettlementBusStyle);
        db.insert(EXIT_TABLE_NAME, null, contentValues);
    }

    public boolean createXRead () {
        Date now = new Date();
        String pattern = "yyyy-MM-dd HH:mm:ss";
        final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(VIP_COLUMN_ID, NULL);
       contentValues.put("logINID", GLOBALS.getInstance().getLoginID());
        contentValues.put("SentinelID", CONSTANTS.getInstance().getExitID());
        contentValues.put("userCode", GLOBALS.getInstance().getCashierID());
        contentValues.put("userName", GLOBALS.getInstance().getCashierName());
        contentValues.put("loginStamp", sdf.format(now));

        long res = db.insert(XREAD_TABLE_NAME, null, contentValues);
        if (res >= 0) {
            return true;
        }
        return false;
    }

    public boolean createZRead (String beginOR, String beginTrans, String beginGross, String beginGrand) {
        Date now = new Date();
        String pattern = "yyyy-MM-dd HH:mm:ss";
        final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(VIP_COLUMN_ID, NULL);
        contentValues.put("beginOR", beginOR);
        contentValues.put("beginTrans", beginTrans);
        contentValues.put("oldGrossTotal", beginGross);
        contentValues.put("oldGrand", beginGrand);
        contentValues.put("logINID", GLOBALS.getInstance().getLoginID());
        contentValues.put("terminalnum", CONSTANTS.getInstance().getExitID());
        contentValues.put("tellerCode", GLOBALS.getInstance().getCashierID());
        contentValues.put("datetimeIn", sdf.format(now));

        long res = db.insert(ZREAD_TABLE_NAME, null, contentValues);
        if (res >= 0) {
            return true;
        }
        return false;
    }

    public void updateXRead (String key, String value) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(key, value);
        db.update(XREAD_TABLE_NAME, contentValues, "logINID = ? ", new String[] { GLOBALS.getInstance().getLoginID() }  );

    }


    public void updateZRead (String key, String value) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(key, value);
        db.update(ZREAD_TABLE_NAME, contentValues, "logINID = ? ", new String[] { GLOBALS.getInstance().getLoginID() }  );

    }

    public String getOneZRead(String sql, String column) {
        String data = "0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( sql , null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            data = res.getString(res.getColumnIndex(column));
            res.moveToNext();
        }
        if (null == data) {
            data = "0";
        }
        return data;
    }

    public String getImptString(String fieldName) {
        String data = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String SQL = "SELECT " + fieldName + " FROM "+ XREAD_TABLE_NAME + " WHERE " + XREAD_COLUMN_LOGINID + " = '" + GLOBALS.getInstance().getLoginID() + "'";
        Cursor res =  db.rawQuery( SQL, null );

        res.moveToFirst();

        while(res.isAfterLast() == false){
            data = res.getString(res.getColumnIndex(fieldName));
            res.moveToNext();
        }
        return data;
    }

    public int getImptCount(String fieldName) {
        int data = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String SQL = "SELECT " + fieldName + " FROM "+ XREAD_TABLE_NAME + " WHERE " + XREAD_COLUMN_LOGINID + " = '" + GLOBALS.getInstance().getLoginID() + "'";
        Cursor res =  db.rawQuery( SQL, null );

        res.moveToFirst();

        while(res.isAfterLast() == false){
            data = res.getInt(res.getColumnIndex(fieldName));
            res.moveToNext();
        }
        return data;
    }

    public void setImptCount(String fieldName, int Count) {
        SQLiteDatabase db = this.getReadableDatabase();
        Count++;
        ContentValues contentValues = new ContentValues();
        contentValues.put(fieldName, Count);
        db.update(XREAD_TABLE_NAME, contentValues, XREAD_COLUMN_LOGINID + " = ? ", new String[] { GLOBALS.getInstance().getLoginID() }  );

    }

    public double getImptAmount(String fieldName) {
        double data = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select " + fieldName + " from "+ XREAD_TABLE_NAME + " WHERE " + XREAD_COLUMN_LOGINID + " = '" + GLOBALS.getInstance().getLoginID() + "'", null );

        res.moveToFirst();

        while(res.isAfterLast() == false){
            data = res.getDouble(res.getColumnIndex(fieldName));
            res.moveToNext();
        }
        return data;
    }

    public void setImptAmount(String fieldName, double Amount) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(fieldName, Amount);
        db.update(XREAD_TABLE_NAME, contentValues, XREAD_COLUMN_LOGINID + " = ? ", new String[] { GLOBALS.getInstance().getLoginID() }  );

    }

    public double getImptGrand(String fieldName) {
        double data = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select " + fieldName + " from "+ MASTER_TABLE_NAME + " WHERE " + MASTER_COLUMN_ID + " = 1", null );

        res.moveToFirst();

        while(res.isAfterLast() == false){
            data = res.getInt(res.getColumnIndex(fieldName));
            res.moveToNext();
        }
        return data;
    }

    public void setGrandTotal(double Amount) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MASTER_COLUMN_GRANDTOTAL, Amount);
        db.update(MASTER_TABLE_NAME, contentValues, MASTER_COLUMN_ID + " = ? ", new String[] { Integer.toString(1) }  );

    }

    public void setGrossTotal(double Amount) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MASTER_COLUMN_GROSSTOTAL, Amount);
        db.update(MASTER_TABLE_NAME, contentValues, MASTER_COLUMN_ID + " = ? ", new String[] { Integer.toString(1) }  );

    }


    public void getTodaysXReading() {
        SQLiteDatabase db = this.getReadableDatabase();
        String SQL = "SELECT userCode, loginStamp, logoutStamp, " +
                "SUM(ambulanceCount) AS ambulanceCount, " +
                "SUM(graceperiodCount) AS graceperiodCount, " +
                "SUM(inpatientCount) AS inpatientCount, " +
                "SUM(lostCount) AS lostCount, " +
                "SUM(mabregularCount) AS mabregularCount, " +
                "SUM(motorcycleCount) AS motorcycleCount, " +
                "SUM(pwdCount) AS pwdCount, " +
                "SUM(regularCount) AS regularCount, " +
                "SUM(seniorCount) AS seniorCount, " +
                "SUM(vipCount) AS vipCount, " +
                "SUM(dialysisCount) AS dialysisCount, " +
                "SUM(pwdDiscountCount) AS pwdDiscountCount, " +
                "SUM(seniorDiscountCount) AS seniorDiscountCount, " +
                "SUM(localseniorDiscountCount) AS localseniorDiscountCount, " +

                "SUM(carServed) AS carServed, " +

                "SUM(ambulanceAmount) AS ambulanceAmount, " +
                "SUM(graceperiodAmount) AS graceperiodAmount, " +
                "SUM(inpatientAmount) AS inpatientAmount, " +
                "SUM(lostAmount) AS lostAmount, " +
                "SUM(mabregularAmount) AS mabregularAmount, " +
                "SUM(motorcycleAmount) AS motorcycleAmount, " +
                "SUM(pwdAmount) AS pwdAmount, " +
                "SUM(regularAmount) AS regularAmount, " +
                "SUM(seniorAmount) AS seniorAmount, " +
                "SUM(vipAmount) AS vipAmount, " +
                "SUM(dialysisAmount) AS dialysisAmount, " +
                "SUM(pwdDiscountAmount) AS pwdDiscountAmount, " +
                "SUM(seniorDiscountAmount) AS seniorDiscountAmount, " +
                "SUM(localseniorDiscountAmount) AS localseniorDiscountAmount, " +

                "SUM(totalAmount) AS totalAmount, " +
                "SUM(vatsaleAmount) AS vatsaleAmount, " +
                "SUM(vat12Amount) AS vat12Amount, " +
                "SUM(grossAmount) AS grossAmount, " +
                "SUM(totalAmount) AS totalAmount " +
                " FROM "+ XREAD_TABLE_NAME + " WHERE DATE(logoutStamp) = DATE('now')";
        Cursor res =  db.rawQuery( SQL, null );

        res.moveToFirst();

        while(res.isAfterLast() == false){
            //data = res.getString(res.getColumnIndex(fieldName));

            XREADING.getInstance().setTeller(res.getString(res.getColumnIndex("userCode")));
            XREADING.getInstance().setLogInDateTime(res.getString(res.getColumnIndex("loginStamp")));
            XREADING.getInstance().setLogOutDateTime(res.getString(res.getColumnIndex("logoutStamp")));

            XREADING.getInstance().setAmbulanceCount(res.getInt(res.getColumnIndex("ambulanceCount")));
            XREADING.getInstance().setGracePeriodCount(res.getInt(res.getColumnIndex("graceperiodCount")));
            XREADING.getInstance().setInpatientCount(res.getInt(res.getColumnIndex("inpatientCount")));
            XREADING.getInstance().setLostCount(res.getInt(res.getColumnIndex("lostCount")));
            XREADING.getInstance().setMABRegularCount(res.getInt(res.getColumnIndex("mabregularCount")));
            XREADING.getInstance().setMotorcycleCount(res.getInt(res.getColumnIndex("motorcycleCount")));
            XREADING.getInstance().setPwdCount(res.getInt(res.getColumnIndex("pwdCount")));
            XREADING.getInstance().setRegularCount(res.getInt(res.getColumnIndex("regularCount")));
            XREADING.getInstance().setSeniorCount(res.getInt(res.getColumnIndex("seniorCount")));
            XREADING.getInstance().setVIPCount(res.getInt(res.getColumnIndex("vipCount")));
            XREADING.getInstance().setDialysisCount(res.getInt(res.getColumnIndex("dialysisCount")));
            XREADING.getInstance().setPwdDscCount(res.getInt(res.getColumnIndex("pwdDiscountCount")));
            XREADING.getInstance().setSeniorDscCount(res.getInt(res.getColumnIndex("seniorDiscountCount")));
            XREADING.getInstance().setLocalSeniorDscCount(res.getInt(res.getColumnIndex("localseniorDiscountCount")));
            XREADING.getInstance().setCarsServed(res.getInt(res.getColumnIndex("carServed")));

            XREADING.getInstance().setAmbulanceAmount(res.getDouble(res.getColumnIndex("ambulanceAmount")));
            XREADING.getInstance().setGracePeriodAmount(res.getDouble(res.getColumnIndex("graceperiodAmount")));
            XREADING.getInstance().setInpatientAmount(res.getDouble(res.getColumnIndex("inpatientAmount")));
            XREADING.getInstance().setLostAmount(res.getDouble(res.getColumnIndex("lostAmount")));
            XREADING.getInstance().setMABRegularAmount(res.getDouble(res.getColumnIndex("mabregularAmount")));
            XREADING.getInstance().setMotorcycleAmount(res.getDouble(res.getColumnIndex("motorcycleAmount")));
            XREADING.getInstance().setPwdAmount(res.getDouble(res.getColumnIndex("pwdAmount")));
            XREADING.getInstance().setRegularAmount(res.getDouble(res.getColumnIndex("regularAmount")));
            XREADING.getInstance().setSeniorAmount(res.getDouble(res.getColumnIndex("seniorAmount")));
            XREADING.getInstance().setVIPAmount(res.getDouble(res.getColumnIndex("vipAmount")));
            XREADING.getInstance().setDialysisAmount(res.getDouble(res.getColumnIndex("dialysisAmount")));
            XREADING.getInstance().setPwdDscAmount(res.getDouble(res.getColumnIndex("pwdDiscountAmount")));
            XREADING.getInstance().setSeniorDscAmount(res.getDouble(res.getColumnIndex("seniorDiscountAmount")));
            XREADING.getInstance().setLocalSeniorDscAmount(res.getDouble(res.getColumnIndex("localseniorDiscountAmount")));

            XREADING.getInstance().setTotalAmount(res.getDouble(res.getColumnIndex("totalAmount")));
            XREADING.getInstance().setVATableSales(res.getDouble(res.getColumnIndex("vatsaleAmount")));
            XREADING.getInstance().setVAT12(res.getDouble(res.getColumnIndex("vat12Amount")));
            XREADING.getInstance().setTodaysGrossColl(res.getDouble(res.getColumnIndex("grossAmount")));
            XREADING.getInstance().setTodaysCollection(res.getDouble(res.getColumnIndex("totalAmount")));

            res.moveToNext();
        }
    }

    public void getTodaysZReading() {
        final DecimalFormat df2 = new DecimalFormat("#0.00");
        SQLiteDatabase db = this.getReadableDatabase();
        String SQL = "SELECT datetimeOut, datetimeIn, " +
                "SUM(todaysale) AS todaysale, " +
                "SUM(todaysGross) AS todaysGross, " +
                "SUM(vatablesale) AS vatablesale, " +
                "SUM(vat) AS vat, " +
                "SUM(vatExemptedSales) AS vatExemptedSales, " +
                "MIN(beginOR) AS beginOR, " +
                "MAX(endOR) AS endOR, " +
                "MIN(beginTrans) AS beginTrans, " +
                "MAX(endTrans) AS endTrans, " +
                "MIN(oldGrand) AS oldGrand, " +
                "MAX(newGrand) AS newGrand, " +
                "MIN(oldGrossTotal) AS oldGrossTotal, " +
                "MAX(newGrossTotal) AS newGrossTotal, " +
                "SUM(discounts) AS discounts " +

                " FROM "+ ZREAD_TABLE_NAME + " WHERE DATE(datetimeOut) = DATE('now')";
        Cursor res =  db.rawQuery( SQL, null );

        res.moveToFirst();

        while(res.isAfterLast() == false){
            //data = res.getString(res.getColumnIndex(fieldName));

            ZREADING.getInstance().setZreadingDate(res.getString(res.getColumnIndex("datetimeOut")));
//            ZREADING.getInstance().setzCount(res.getString(res.getColumnIndex("zCount")));

            ZREADING.getInstance().setTodaysSales(res.getDouble(res.getColumnIndex("todaysale")));
            ZREADING.getInstance().setTodaysGross(res.getDouble(res.getColumnIndex("todaysGross")));
            ZREADING.getInstance().setVatableSales(res.getDouble(res.getColumnIndex("vatablesale")));
            ZREADING.getInstance().setVat12Sales(res.getDouble(res.getColumnIndex("vat")));
            ZREADING.getInstance().setVatExemptedSales(res.getDouble(res.getColumnIndex("vatExemptedSales")));

            ZREADING.getInstance().setBeginOR(res.getString(res.getColumnIndex("beginOR")));
            ZREADING.getInstance().setEndOR(res.getString(res.getColumnIndex("endOR")));
            ZREADING.getInstance().setBeginTrans(res.getString(res.getColumnIndex("beginTrans")));
            ZREADING.getInstance().setEndTrans(res.getString(res.getColumnIndex("endTrans")));

            ZREADING.getInstance().setBeginBalance(res.getDouble(res.getColumnIndex("oldGrand")));
            ZREADING.getInstance().setEndingBalance(res.getDouble(res.getColumnIndex("newGrand")));
            ZREADING.getInstance().setBeginGross(res.getDouble(res.getColumnIndex("oldGrossTotal")));
            ZREADING.getInstance().setEndingGross(res.getDouble(res.getColumnIndex("newGrossTotal")));

            res.moveToNext();
        }

        double beginGross = getOneTransDbl("SELECT grossTotal FROM master", "grossTotal");
        double beginGrand = getOneTransDbl("SELECT grandTotal FROM master", "grandTotal");

        ZREADING.getInstance().setAccumulatedGross(df2.format(beginGross));
        ZREADING.getInstance().setAccumulatedGrand(df2.format(beginGrand));
    }

    public boolean insertContact (String cardID, String parkerType, String plateNumber, String name, String cardNumber, String maxUse, String status, String ldc, String ldm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(VIP_COLUMN_ID, NULL);
        contentValues.put(VIP_COLUMN_CARDID, cardID);
        contentValues.put(VIP_COLUMN_PARKERTYPE, parkerType);
        contentValues.put(VIP_COLUMN_PLATENUMBER, plateNumber);
        contentValues.put(VIP_COLUMN_NAME, name);
        contentValues.put(VIP_COLUMN_CARDCODE, cardNumber);
        contentValues.put(VIP_COLUMN_MAXUSE, maxUse);
        contentValues.put(VIP_COLUMN_STATUS, status);
        contentValues.put(VIP_COLUMN_DATECREATED, ldc);
        contentValues.put(VIP_COLUMN_DATEMODIFIED, ldm);
        long res = db.insert(VIP_TABLE_NAME, null, contentValues);
        if (res >= 0) {
            return true;
        }
        return false;
    }

    public boolean updateContact (String cardID, String parkerType, String plateNumber, String name, String cardNumber, String maxUse, String status, String ldc, String ldm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(VIP_COLUMN_ID, NULL);
        contentValues.put(VIP_COLUMN_CARDID, cardID);
        contentValues.put(VIP_COLUMN_PARKERTYPE, parkerType);
        contentValues.put(VIP_COLUMN_PLATENUMBER, plateNumber);
        contentValues.put(VIP_COLUMN_NAME, name);
        contentValues.put(VIP_COLUMN_CARDCODE, cardNumber);
        contentValues.put(VIP_COLUMN_MAXUSE, maxUse);
        contentValues.put(VIP_COLUMN_STATUS, status);
        contentValues.put(VIP_COLUMN_DATECREATED, ldc);
        contentValues.put(VIP_COLUMN_DATEMODIFIED, ldm);
        long res =  db.update(VIP_TABLE_NAME, contentValues, VIP_COLUMN_CARDCODE + " = ? " ,new String[]{String.valueOf(cardNumber)});
        //long res = db.update(VIP_TABLE_NAME, "", contentValues);
        if (res >= 0) {
            return true;
        }
        return false;
    }

    public boolean insertParkerCard (String cardCode, String vehicle, String plateNumber, String lane, String pc, String timein) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(VIP_COLUMN_ID, NULL);
        contentValues.put(CARD_COLUMN_CARDCODE, cardCode);
        contentValues.put(CARD_COLUMN_PLATE, plateNumber);
        contentValues.put(CARD_COLUMN_VEHICLE, vehicle);
        contentValues.put(CARD_COLUMN_LANE, lane);
        contentValues.put(CARD_COLUMN_PC, pc);
        contentValues.put(CARD_COLUMN_TIMEIN, timein);
        long res = db.insert(CARD_TABLE_NAME, null, contentValues);
        if (res >= 0) {
            return true;
        }
        return false;
    }

    public boolean insertLogin (String logID, String parkerType, String plateNumber, String name, String cardNumber, String maxUse, String status, String ldc, String ldm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(VIP_COLUMN_ID, NULL);
        contentValues.put(VIP_COLUMN_CARDID, logID);
        contentValues.put(VIP_COLUMN_PARKERTYPE, parkerType);
        contentValues.put(VIP_COLUMN_PLATENUMBER, plateNumber);
        contentValues.put(VIP_COLUMN_NAME, name);
        contentValues.put(VIP_COLUMN_CARDCODE, cardNumber);
        contentValues.put(VIP_COLUMN_MAXUSE, maxUse);
        contentValues.put(VIP_COLUMN_STATUS, status);
        contentValues.put(VIP_COLUMN_DATECREATED, ldc);
        contentValues.put(VIP_COLUMN_DATEMODIFIED, ldm);
        long res = db.insert(VIP_TABLE_NAME, null, contentValues);
        if (res >= 0) {
            return true;
        }
        return false;
    }

    public boolean initiateMaster() {
        GLOBALS.getInstance().setReceiptNum(0);
        GLOBALS.getInstance().setGrandTotal(0);
        GLOBALS.getInstance().setGrossTotal(0);

        Date now = new Date();
        String pattern = "yyyy-MM-dd HH:mm:ss";
        final SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MASTER_COLUMN_ID, 1);
        //contentValues.put(MASTER_COLUMN_RECEIPTNOS, "1");//AB01000000001165
        contentValues.put(MASTER_COLUMN_RECEIPTNOS, "1166");//AB01000000001165
        contentValues.put(MASTER_COLUMN_GRANDTOTAL, "113477");//113477
        contentValues.put(MASTER_COLUMN_GROSSTOTAL, "117380");//117380
        contentValues.put(MASTER_COLUMN_DATETIMERECORDED, sdf.format(now));
        db.insert(MASTER_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateMaster(String column, String value) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, value);
        db.insert(MASTER_TABLE_NAME, null, contentValues);
        return true;
    }

    public int getRNosData(String fieldName) {
        int data = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from "+ MASTER_TABLE_NAME , null );

        res.moveToFirst();

        while(res.isAfterLast() == false){
            data = res.getInt(res.getColumnIndex(fieldName));
            res.moveToNext();
        }
        return data;
    }

    public boolean updateRNosData (String fieldName, int value) {
        Date now = new Date();
        String pattern = "yyyy-MM-dd HH:mm:ss";
        final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(fieldName, value);
        contentValues.put(MASTER_COLUMN_DATETIMERECORDED, sdf.format(now));
        db.update(MASTER_TABLE_NAME, contentValues, MASTER_COLUMN_ID + " = ? ", new String[] { Integer.toString(1) }  );
        return true;
    }


    public boolean getMaster() {
        String data = "";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from "+ MASTER_TABLE_NAME , null );

        res.moveToFirst();

        while(res.isAfterLast() == false){
            data = res.getString(res.getColumnIndex(MASTER_COLUMN_RECEIPTNOS));
            GLOBALS.getInstance().setReceiptNum(res.getInt(res.getColumnIndex(MASTER_COLUMN_RECEIPTNOS)));
            GLOBALS.getInstance().setGrandTotal(res.getInt(res.getColumnIndex(MASTER_COLUMN_GRANDTOTAL)));
            GLOBALS.getInstance().setGrossTotal(res.getInt(res.getColumnIndex(MASTER_COLUMN_GROSSTOTAL)));

            res.moveToNext();
            if(data.compareTo("") == 0) {
                return false;
            } else {
                return true;
            }
        }
        return false;

    }

    public boolean isDeviceLoggedIN() {
        String data = "";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from "+ GIN_TABLE_NAME , null );

        res.moveToFirst();

        while(res.isAfterLast() == false){
            data = res.getString(res.getColumnIndex(GIN_COLUMN_CASHIERNAME));
            GLOBALS.getInstance().setLoginID(res.getString(res.getColumnIndex(GIN_COLUMN_LOGID)));
            GLOBALS.getInstance().setCashierID(res.getString(res.getColumnIndex(GIN_COLUMN_CASHIERID)));
            GLOBALS.getInstance().setCashierName(res.getString(res.getColumnIndex(GIN_COLUMN_CASHIERNAME)));
            GLOBALS.getInstance().setLoginDate(res.getString(res.getColumnIndex(GIN_COLUMN_LOGINDATE)));

            res.moveToNext();
            if(data.compareTo("") == 0) {
                return false;
            } else {
                return true;
            }
        }
        return false;

    }


    public boolean validateLogout(String username, String password) {
        Date now = new Date();
        String pattern = "yyyy-MM-dd HH:mm:ss";
        final SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        String data = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ POS_TABLE_NAME +" where "+ POS_COLUMN_PASSWORD +"='"+ md5(password) +"' AND "
                + POS_COLUMN_USERNAME + "='"+username+"'", null );

        res.moveToFirst();

        while(res.isAfterLast() == false){
            data = res.getString(res.getColumnIndex(POS_COLUMN_USERNAME));

            if(data.compareTo("") == 0) {
                return false;
            } else {
                return true;
            }
        }
        return false;

    }


    public boolean validateLogin(String username, String password) {
        Date now = new Date();
        String pattern = "yyyy-MM-dd HH:mm:ss";
        final SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        String data = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ POS_TABLE_NAME +" where "+ POS_COLUMN_PASSWORD +"='"+ md5(password) +"' AND "
                + POS_COLUMN_USERNAME + "='"+username+"'", null );

        res.moveToFirst();

        while(res.isAfterLast() == false){
            data = res.getString(res.getColumnIndex(POS_COLUMN_USERNAME));
            String nowStr = now.getTime() + "";
            String loginID = nowStr.substring(0, nowStr.length()/2)+data+ nowStr.substring(nowStr.length()/2, nowStr.length());
            String cashierID = res.getString(res.getColumnIndex(POS_COLUMN_USERNAME));
            String cashierName = res.getString(res.getColumnIndex(POS_COLUMN_CASHIERNAME));
            String loginDate = sdf.format(now);
            GLOBALS.getInstance().setLoginID(loginID);
            GLOBALS.getInstance().setCashierID(cashierID);
            GLOBALS.getInstance().setCashierName(cashierName);
            GLOBALS.getInstance().setLoginDate(loginDate);
            res.moveToNext();
            if(data.compareTo("") == 0) {
                return false;
            } else {
                saveLogin(loginID,cashierID,cashierName,loginDate);
                createXRead();
                String beginOR = getOneTrans("SELECT receiptNos FROM master", "receiptNos");
                String beginTrans = getOneTrans("SELECT MAX(id) AS beginTrans FROM exit_trans", "beginTrans");
                String beginGross = getOneTrans("SELECT grossTotal FROM master", "grossTotal");
                String beginGrand = getOneTrans("SELECT grandTotal FROM master", "grandTotal");
                createZRead( CONSTANTS.getInstance().getExitID() + formatNos(beginOR), beginTrans, beginGross, beginGrand);
                return true;
            }
        }
        return false;

    }

    public String getOneTrans(String sql, String column) {
        String data = "0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( sql , null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            data = res.getString(res.getColumnIndex(column));
            res.moveToNext();
        }
        if (null == data) {
            data = "0";
        }
        return data;
    }

    public double getOneTransDbl(String sql, String column) {
        double data = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( sql , null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            data = res.getDouble(res.getColumnIndex(column));
            res.moveToNext();
        }
        return data;
    }

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getLoginUserName(String loginID) {
        String data = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ GIN_TABLE_NAME +" where "+ GIN_COLUMN_LOGID +"='"+ loginID +"'", null );

        res.moveToFirst();

        while(res.isAfterLast() == false){
            data = res.getString(res.getColumnIndex(GIN_COLUMN_CASHIERNAME));
            res.moveToNext();
        }
        return data;

    }

    public void logoutForced() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + GIN_TABLE_NAME);
    }

    public void saveLogin(String loginID, String cashierid, String cashiername, String logindate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(GIN_COLUMN_CASHIERID, cashierid);
        contentValues.put(GIN_COLUMN_CASHIERNAME, cashiername);
        contentValues.put(GIN_COLUMN_LOGID, loginID);
        contentValues.put(GIN_COLUMN_LOGINDATE, logindate);
        db.insert(GIN_TABLE_NAME, null, contentValues);
    }

    public Cursor getCardData(String cardNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ VIP_TABLE_NAME +" where "+ VIP_COLUMN_CARDCODE +"='"+cardNumber+"'", null );
        return res;
    }

    public Cursor getLastDateData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ NET_MANAGER_TABLE_NAME, null );
        return res;
    }

    public Cursor getLastDateData(String tableType) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ NET_MANAGER_TABLE_NAME + " WHERE " + NET_MANAGER_COL_TABLETYPE + " = '" + tableType + "'", null );
        return res;
    }

    public Cursor updateLastDateData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ NET_MANAGER_TABLE_NAME, null );
        return res;
    }

    public Cursor getExitDataForServer(String ldm) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ EXIT_TABLE_NAME + " WHERE DateTimeOUT > '" + ldm + "'", null );
        return res;
    }

    public Cursor getXReadDataForServer(String ldm) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ XREAD_TABLE_NAME + " WHERE logoutStamp > '" + ldm + "'", null );
        return res;
    }

    public boolean insertLDCandLDM (String tableType, String LDC, String LDM) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NET_MANAGER_COL_TABLETYPE, tableType);
        contentValues.put(NET_MANAGER_COLUMN_LDC, LDC);
        contentValues.put(NET_MANAGER_COLUMN_LDM, LDM);
        db.insert(NET_MANAGER_TABLE_NAME, null, contentValues);
        return true;
    }


    public boolean insertLDC (String LDC) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NET_MANAGER_COLUMN_LDC, LDC);
        db.insert(NET_MANAGER_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateLDC (String tableType, String LDC) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NET_MANAGER_COLUMN_LDC, LDC);
        db.update(NET_MANAGER_TABLE_NAME, contentValues, NET_MANAGER_COL_TABLETYPE + " = ? ", new String[] { tableType }  );
        return true;
    }

    public boolean insertLDM (String tableType, String LDM) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NET_MANAGER_COL_TABLETYPE, tableType);
        contentValues.put(NET_MANAGER_COLUMN_LDM, LDM);
        db.insert(NET_MANAGER_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateLDM (String tableType, String LDM) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NET_MANAGER_COLUMN_LDM, LDM);
        db.update(NET_MANAGER_TABLE_NAME, contentValues, NET_MANAGER_COL_TABLETYPE + " = ? ", new String[] { tableType }  );
        return true;
    }

    public boolean updateExitID (String tableType, String LDC) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ExitID", LDC);
        db.update(EXIT_TABLE_NAME, contentValues, "DateTimeOUT" + " >= ? ", new String[] { tableType }  );
        return true;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, VIP_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact (Integer id, String name, String phone, String email, String street, String place) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.update(VIP_TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteContact (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(VIP_TABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllVIPContacts() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ VIP_TABLE_NAME , null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(VIP_COLUMN_NAME)) +
                    " Plate: " + res.getString(res.getColumnIndex(VIP_COLUMN_PLATENUMBER)) +
                    " Card: " + res.getString(res.getColumnIndex(VIP_COLUMN_CARDCODE)) +
                    " P Type: " + res.getString(res.getColumnIndex(VIP_COLUMN_PARKERTYPE)));
            res.moveToNext();
        }
        return array_list;
    }

    public String formatNos(String newReceipt) {
        int stoploop = 12 - newReceipt.length();
        int i = 0;
        do {
            newReceipt = "0" + newReceipt;
            i++;
        } while (i != stoploop);

        return newReceipt;
    }

    private void updatePOSDB4(SQLiteDatabase db) {
        System.out.println("ANGELO : [updatePOSDB4 new POS Users" + new Date().toString() + "]" );
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'atdv', '" + md5("atdv") + "', 'atdv')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'roda', '" + md5("roda") + "', 'roda')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'mays', '" + md5("mays") + "', 'mays')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'eva', '" + md5("eva") + "', 'eva')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'adj', '" + md5("adj") + "', 'adj')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'lync', '" + md5("lync") + "', 'lync')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'syrell', '" + md5("syrell") + "', 'syrell')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'rgs', '" + md5("rgs") + "', 'rgs')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'mylene', '" + md5("mylene") + "', 'mylene')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'che', '" + md5("che") + "', 'che')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'eme', '" + md5("eme") + "', 'eme')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'cagalsim', '" + md5("cagalsim") + "', 'cagalsim')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'meenriquez', '" + md5("meenriquez") + "', 'meenriquez')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'kjplim', '" + md5("kjplim") + "', 'kjplim')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'mdralimuin', '" + md5("mdralimuin") + "', 'mdralimuin')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'jeanny', '" + md5("jeanny") + "', 'jeanny')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'jvlcarmona', '" + md5("jvlcarmona") + "', 'jvlcarmona')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'mlmllamas', '" + md5("mlmllamas") + "', 'mlmllamas')");
        System.out.println("ANGELO : [Uploaded new POS Users" + new Date().toString() + "]" );
    }

    private void updatePOSDB5(SQLiteDatabase db) {
        System.out.println("ANGELO : [updatePOSDB5 new POS Users" + new Date().toString() + "]" );
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'jinopal', '" + md5("jinopal") + "', 'jinopal')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'smjmanlagnit', '" + md5("smjmanlagnit") + "', 'smjmanlagnit')");
        System.out.println("ANGELO : [Uploaded new POS Users" + new Date().toString() + "]" );
    }

    public void fillPOSDB(SQLiteDatabase db) {
        System.out.println("ANGELO : [Checking new POS Users" + new Date().toString() + "]" );
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'Roseveeh', '" + md5("Roseveeh") + "', 'Roseveeh')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'Joan', '" + md5("Joan") + "', 'Joan')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'claudine', '" + md5("claudine") + "', 'claudine')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'hazel', '" + md5("hazel") + "', 'hazel')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'Xyree', '" + md5("Xyree") + "', 'Xyree')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'Carla', '" + md5("Carla") + "', 'Carla')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'Cielo', '" + md5("Cielo") + "', 'Cielo')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'Claudine2', '" + md5("Claudine2") + "', 'Claudine2')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'Carla2', '" + md5("Carla2") + "', 'Carla2')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'hazel2', '" + md5("hazel2") + "', 'hazel2')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'Joan2', '" + md5("Joan2") + "', 'Joan2')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'Cielo2', '" + md5("Cielo2") + "', 'Cielo2')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'Roseveeh2', '" + md5("Roseveeh2") + "', 'Roseveeh2')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'Xyree2', '" + md5("Xyree2") + "', 'Xyree2')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'roseann2', '" + md5("roseann2") + "', 'roseann2')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'm', '" + md5("m") + "', 'm')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'manuel', '" + md5("manuel") + "', 'manuel')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'mary', '" + md5("mary") + "', 'mary')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'mary2', '" + md5("mary2") + "', 'mary2')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'Cindy', '" + md5("Cindy") + "', 'Cindy')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'Cindy2', '" + md5("Cindy2") + "', 'Cindy2')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'roseann', '" + md5("roseann") + "', 'roseann')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'Joan2', '" + md5("Joan2") + "', 'Joan2')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'Hazel2', '" + md5("Hazel2") + "', 'Hazel2')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'Carla2', '" + md5("Carla2") + "', 'Carla2')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'Bryan', '" + md5("Bryan") + "', 'Bryan')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'Claudine2', '" + md5("Claudine2") + "', 'Claudine2')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'teller1', '" + md5("teller1") + "', 'teller1')");
        db.execSQL("INSERT INTO " + POS_TABLE_NAME + " VALUES(null, 'angelo', '" + md5("angelo") + "', 'angelo')");
        System.out.println("ANGELO : [Uploaded new POS Users" + new Date().toString() + "]" );
    }

}