package com.theoretics.mobilepos.bean;

public class RECEIPT {

    private static RECEIPT instance;
    private String receiptNum;

    private String entID = "";
    private String exitID = "";

    private String cashierName = "";
    private String cashierID = "";
    private String loginDate = "";

    private String cardNumber = "";
    private String plateNum = "";
    private String pType = "";
    private String pTypeName = "";
    private String daysElapsed = "";
    private String hoursElapsed = "";
    private String minutesElapsed = "";
    private String datetimeIN = "";
    private String datetimeOUT = "";
    private String duration = "";

    private String amountGross = "";
    private String discount = "";

    private double amountGrossDbl = 0;
    private double discountDbl = 0;

    boolean isDiscounted = false;
    private String tendered = "";
    private String changeDue = "";
    private String AmountDue = "";
    private String AmountGross = "";
    private String NetOfVAT = "";
    private String NetOfDiscount = "";
    private String vat12 = "";
    private String vatsale = "";
    private String vatExemptedSales = "";
    private String vatAdjustment = "";

    private double AmountDueDbl = 0;
    private double AmountGrossDbl = 0;
    private double NetOfVATDbl = 0;
    private double NetOfDiscountDbl = 0;
    private double vat12Dbl = 0;
    private double vatsaleDbl = 0;
    private double vatExemptedSalesDbl = 0;
    private double vatAdjustmentDbl = 0;

    private String SettlementRef = "";
    private String SettlementName = "";
    private String SettlementAddr = "";
    private String SettlementTIN = "";
    private String SettlementBusStyle = "";

    public RECEIPT(){}

    public static synchronized RECEIPT getInstance() {
        if(instance == null) {
            instance = new RECEIPT();
        }
        return instance;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getReceiptNum() {
        return receiptNum;
    }

    public void setReceiptNum(String receiptNum) {
        this.receiptNum = receiptNum;
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

    public String getCashierName() {
        return cashierName;
    }

    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }

    public String getCashierID() {
        return cashierID;
    }

    public void setCashierID(String cashierID) {
        this.cashierID = cashierID;
    }

    public String getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(String loginDate) {
        this.loginDate = loginDate;
    }

    public String getPlateNum() {
        return plateNum;
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

    public String getAmountGross() {
        return amountGross;
    }

    public void setAmountGross(String amountGross) {
        this.amountGross = amountGross;
    }

    public String getNetOfVAT() {
        return NetOfVAT;
    }

    public void setNetOfVAT(String netOfVAT) {
        NetOfVAT = netOfVAT;
    }

    public String getNetOfDiscount() {
        return NetOfDiscount;
    }

    public void setNetOfDiscount(String netOfDiscount) {
        NetOfDiscount = netOfDiscount;
    }

    public String getVat12() {
        return vat12;
    }

    public void setVat12(String vat12) {
        this.vat12 = vat12;
    }

    public String getVatsale() {
        return vatsale;
    }

    public void setVatsale(String vatsale) {
        this.vatsale = vatsale;
    }

    public String getVatExemptedSales() {
        return vatExemptedSales;
    }

    public void setVatExemptedSales(String vatExemptedSales) {
        this.vatExemptedSales = vatExemptedSales;
    }

    public String getVatAdjustment() {
        return vatAdjustment;
    }

    public void setVatAdjustment(String vatAdjustment) {
        this.vatAdjustment = vatAdjustment;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getAmountDue() {
        return AmountDue;
    }

    public void setAmountDue(String amountDue) {
        AmountDue = amountDue;
    }

    public boolean isDiscounted() {
        return isDiscounted;
    }

    public void setDiscounted(boolean discounted) {
        isDiscounted = discounted;
    }

    public String getTendered() {
        return tendered;
    }

    public void setTendered(String tendered) {
        this.tendered = tendered;
    }

    public String getChangeDue() {
        return changeDue;
    }

    public void setChangeDue(String changeDue) {
        this.changeDue = changeDue;
    }

    public String getSettlementRef() {
        return SettlementRef;
    }

    public void setSettlementRef(String settlementRef) {
        SettlementRef = settlementRef;
    }

    public String getSettlementName() {
        return SettlementName;
    }

    public void setSettlementName(String settlementName) {
        SettlementName = settlementName;
    }

    public String getSettlementAddr() {
        return SettlementAddr;
    }

    public void setSettlementAddr(String settlementAddr) {
        SettlementAddr = settlementAddr;
    }

    public String getSettlementTIN() {
        return SettlementTIN;
    }

    public void setSettlementTIN(String settlementTIN) {
        SettlementTIN = settlementTIN;
    }

    public String getSettlementBusStyle() {
        return SettlementBusStyle;
    }

    public void setSettlementBusStyle(String settlementBusStyle) {
        SettlementBusStyle = settlementBusStyle;
    }

    public double getAmountGrossDbl() {
        return amountGrossDbl;
    }

    public void setAmountGrossDbl(double amountGrossDbl) {
        this.amountGrossDbl = amountGrossDbl;
    }

    public double getDiscountDbl() {
        return discountDbl;
    }

    public void setDiscountDbl(double discountDbl) {
        this.discountDbl = discountDbl;
    }

    public double getAmountDueDbl() {
        return AmountDueDbl;
    }

    public void setAmountDueDbl(double amountDueDbl) {
        AmountDueDbl = amountDueDbl;
    }

    public double getNetOfVATDbl() {
        return NetOfVATDbl;
    }

    public void setNetOfVATDbl(double netOfVATDbl) {
        NetOfVATDbl = netOfVATDbl;
    }

    public double getNetOfDiscountDbl() {
        return NetOfDiscountDbl;
    }

    public void setNetOfDiscountDbl(double netOfDiscountDbl) {
        NetOfDiscountDbl = netOfDiscountDbl;
    }

    public double getVat12Dbl() {
        return vat12Dbl;
    }

    public void setVat12Dbl(double vat12Dbl) {
        this.vat12Dbl = vat12Dbl;
    }

    public double getVatsaleDbl() {
        return vatsaleDbl;
    }

    public void setVatsaleDbl(double vatsaleDbl) {
        this.vatsaleDbl = vatsaleDbl;
    }

    public double getVatExemptedSalesDbl() {
        return vatExemptedSalesDbl;
    }

    public void setVatExemptedSalesDbl(double vatExemptedSalesDbl) {
        this.vatExemptedSalesDbl = vatExemptedSalesDbl;
    }

    public double getVatAdjustmentDbl() {
        return vatAdjustmentDbl;
    }

    public void setVatAdjustmentDbl(double vatAdjustmentDbl) {
        this.vatAdjustmentDbl = vatAdjustmentDbl;
    }
}
