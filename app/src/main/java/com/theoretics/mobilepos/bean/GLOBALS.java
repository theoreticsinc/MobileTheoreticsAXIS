package com.theoretics.mobilepos.bean;

import android.app.ProgressDialog;

import com.android.print.sdk.PrinterInstance;
import com.theoretics.mobilepos.util.IPrinterOperation;

public class GLOBALS {
    private static GLOBALS instance;

    private static boolean isConnected;                 //是否已经建立了连接
    protected static IPrinterOperation myOperation;
    private PrinterInstance mPrinter;
    private ProgressDialog dialog;

    private boolean newCard = true;
    private String receiptCopyType = "";

    private String loginID = ""; //Unique ID for saving
    private String entID = "";
    private String exitID = "";

    private String cardNumber = "";

    private String cashierName = "";
    private String cashierID = "";
    private String loginDate = "";

    private String plateNum = "";
    private String pType = "";
    private String pTypeName = "";
    private String daysElapsed = "";
    private String hoursElapsed = "";
    private String minutesElapsed = "";
    private String datetimeIN = "";
    private String datetimeOUT = "";
    private String duration = "";

    private int receiptNum = 0;
    private double grandTotal = 0;
    private double grossTotal = 0;
    private double AmountDue = 0;
    private double AmountGross = 0;
    private double NetOfVAT = 0;
    private double NetOfDiscount = 0;
    private double discount = 0;
    private double vat12 = 0;
    private double vatsale = 0;
    private double vatExemptedSales = 0;
    private double vatAdjustment = 0;

    private boolean isPressed = false;

    public GLOBALS(){}

    public static IPrinterOperation getMyOperation() {
        return myOperation;
    }

    public static void setMyOperation(IPrinterOperation myOperation) {
        GLOBALS.myOperation = myOperation;
    }

    public static boolean isConnected() {
        return isConnected;
    }

    public static void setIsConnected(boolean isConnected) {
        GLOBALS.isConnected = isConnected;
    }

    public PrinterInstance getmPrinter() {
        return mPrinter;
    }

    public void setmPrinter(PrinterInstance mPrinter) {
        this.mPrinter = mPrinter;
    }

    public ProgressDialog getDialog() {
        return dialog;
    }

    public void setDialog(ProgressDialog dialog) {
        this.dialog = dialog;
    }

    public boolean isNewCard() {
        return newCard;
    }

    public void setNewCard(boolean newCard) {
        this.newCard = newCard;
    }

    public void setLoginID(String loginID){
        this.loginID = loginID;
    }
    public String getLoginID() {
        return this.loginID;
    }

    public void setCashierName(String cashierName){
        this.cashierName = cashierName;
    }
    public String getCashierName() {
        return this.cashierName;
    }

    public void setCashierID(String cashierID){
        this.cashierID = cashierID;
    }
    public String getCashierID() {
        return this.cashierID;
    }

    public void setLoginDate(String loginDate){
        this.loginDate = loginDate;
    }
    public String getLoginDate() {
        return this.loginDate;
    }
    public String getPlateNum() {
        return plateNum;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setPlateNum(String plateNum) {
        this.plateNum = plateNum;
    }

    public String getpType() {
        return pType;
    }

    public void setpType(String pType) {
        this.pType = pType;
    }

    public String getpTypeName() {
        return pTypeName;
    }

    public void setpTypeName(String pTypeName) {
        this.pTypeName = pTypeName;
    }

    public String getDatetimeIN() {
        return datetimeIN;
    }

    public void setDatetimeIN(String datetimeIN) {
        this.datetimeIN = datetimeIN;
    }

    public String getDatetimeOUT() {
        return datetimeOUT;
    }

    public void setDatetimeOUT(String datetimeOUT) {
        this.datetimeOUT = datetimeOUT;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDaysElapsed() {
        return daysElapsed;
    }

    public void setDaysElapsed(String daysElapsed) {
        this.daysElapsed = daysElapsed;
    }

    public String getHoursElapsed() {
        return hoursElapsed;
    }

    public void setHoursElapsed(String hoursElapsed) {
        this.hoursElapsed = hoursElapsed;
    }

    public String getMinutesElapsed() {
        return minutesElapsed;
    }

    public void setMinutesElapsed(String minutesElapsed) {
        this.minutesElapsed = minutesElapsed;
    }

    public String getEntID() {
        return entID;
    }

    public void setEntID(String entID) {
        this.entID = entID;
    }

    public String getExitID() {
        return exitID;
    }

    public void setExitID(String exitID) {
        this.exitID = exitID;
    }

    public int getReceiptNum() {
        return receiptNum;
    }

    public void setReceiptNum(int receiptNum) {
        this.receiptNum = receiptNum;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public double getGrossTotal() {
        return grossTotal;
    }

    public void setGrossTotal(double grossTotal) {
        this.grossTotal = grossTotal;
    }

    public double getAmountDue() {
        return AmountDue;
    }

    public void setAmountDue(double amountDue) {
        AmountDue = amountDue;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getAmountGross() {
        return AmountGross;
    }

    public void setAmountGross(double amountGross) {
        AmountGross = amountGross;
    }

    public double getNetOfVAT() {
        return NetOfVAT;
    }

    public void setNetOfVAT(double netOfVAT) {
        NetOfVAT = netOfVAT;
    }

    public double getNetOfDiscount() {
        return NetOfDiscount;
    }

    public void setNetOfDiscount(double netOfDiscount) {
        NetOfDiscount = netOfDiscount;
    }

    public double getVat12() {
        return vat12;
    }

    public void setVat12(double vat12) {
        this.vat12 = vat12;
    }

    public double getVatsale() {
        return vatsale;
    }

    public void setVatsale(double vatsale) {
        this.vatsale = vatsale;
    }

    public double getVatExemptedSales() {
        return vatExemptedSales;
    }

    public void setVatExemptedSales(double vatExemptedSales) {
        this.vatExemptedSales = vatExemptedSales;
    }

    public double getVatAdjustment() {
        return vatAdjustment;
    }

    public void setVatAdjustment(double vatAdjustment) {
        this.vatAdjustment = vatAdjustment;
    }

    public String getReceiptCopyType() {
        return receiptCopyType;
    }

    public void setReceiptCopyType(String receiptCopyType) {
        this.receiptCopyType = receiptCopyType;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public void setPressed(boolean pressed) {
        isPressed = pressed;
    }

    public static synchronized GLOBALS getInstance() {
        if(instance == null) {
            instance = new GLOBALS();
        }
        return instance;
    }

}
