package com.theoretics.mobilepos.util;


import com.theoretics.mobilepos.bean.GLOBALS;

import java.text.DecimalFormat;

public class ComputeAPI {
    DateConversionHandler dch = new DateConversionHandler();
    double flatRate = 50.0;
    double succeedingRate = 20.0;

    float discountPercentage = 20.0f;
    boolean isDiscounted = false;
    public double discount = 0.0;

    public double AmountDue = 0;
    public double AmountGross = 0;
    public double NetOfVAT = 0;
    public double NetOfDiscount = 0;
    public double vat12 = 0;
    public double vatsale = 0;
    public double vatExemptedSales = 0;
    public double vatAdjustment = 0;

    int firstHourCharge = 2;

    public ComputeAPI(boolean discounted, String TRType, int daysElapsed, int hoursElapsed, int minutesElapsed) {

        isDiscounted = discounted;
        //Convert days to hours
        int convHours = daysElapsed * 24;
        hoursElapsed = hoursElapsed + convHours;

        //-----------RETAIL-----------------------
        if (TRType.compareToIgnoreCase("R") == 0 || TRType.compareToIgnoreCase("RM") == 0) {

            if (hoursElapsed < firstHourCharge) {
                AmountDue = 0;
                if (hoursElapsed == 0 && minutesElapsed <= 15) {
                    //  Grace Period
                    AmountDue = 0;
                }
                else {
                    AmountDue = flatRate;
                }
            } else if (hoursElapsed == firstHourCharge) {
                if (minutesElapsed == 0) {
                    AmountDue = flatRate;
                }
                else {
                    //  Succeeding Rates a Fraction Thereof
                    AmountDue = flatRate + succeedingRate + ((hoursElapsed - firstHourCharge) * succeedingRate);
                }
            } else if (hoursElapsed >= firstHourCharge + 1) {
                if (minutesElapsed == 0) {
                    AmountDue = flatRate + ((hoursElapsed - firstHourCharge) * succeedingRate);
                }
                else {
                    AmountDue = flatRate + ((hoursElapsed - firstHourCharge + 1) * succeedingRate);
                }
            }
            vat12 = getVat(AmountDue);
            vatsale = getNonVat(AmountDue);

        }  else if (TRType.compareToIgnoreCase("S") == 0) {

            if (hoursElapsed < firstHourCharge) {
                AmountDue = 0;
                if (hoursElapsed == 0 && minutesElapsed <= 15) {
                    //  Grace Period
                    AmountDue = 0;
                }
                else {
                    AmountDue = flatRate;
                }
            } else if (hoursElapsed == firstHourCharge) {
                if (minutesElapsed == 0) {
                    AmountDue = flatRate;
                }
                else {
                    //  Succeeding Rates a Fraction Thereof
                    AmountDue = flatRate + succeedingRate + ((hoursElapsed - firstHourCharge) * succeedingRate);
                }
            } else if (hoursElapsed >= firstHourCharge + 1) {
                if (minutesElapsed == 0) {
                    AmountDue = flatRate + ((hoursElapsed - firstHourCharge) * succeedingRate);
                }
                else {
                    AmountDue = flatRate + ((hoursElapsed - firstHourCharge + 1) * succeedingRate);
                }
            }
            vat12 = getVat(AmountDue);
            vatsale = getNonVat(AmountDue);
        } else if (TRType.compareToIgnoreCase("V") == 0) {
            AmountDue = 0;
            vat12 = 0;
            vatsale = 0;
        }
        else if (TRType.compareToIgnoreCase("DS") == 0) {

            if (hoursElapsed < firstHourCharge) {
                AmountDue = 0;
                if (hoursElapsed == 0 && minutesElapsed <= 15) {
                    //  Grace Period
                    AmountDue = 0;
                }
                else {
                    AmountDue = flatRate;
                }
            } else if (hoursElapsed == firstHourCharge) {
                if (minutesElapsed == 0) {
                    AmountDue = flatRate;
                }
                else {
                    //  Succeeding Rates a Fraction Thereof
                    AmountDue = flatRate;// + ((hoursElapsed - firstHourCharge) * succeedingRate);
                }
            } else if (hoursElapsed >= firstHourCharge + 1) {
                if (minutesElapsed == 0) {
                    AmountDue = flatRate;// + ((hoursElapsed - firstHourCharge) * succeedingRate);
                }
                else {
                    AmountDue = flatRate;// + ((hoursElapsed - firstHourCharge + 1) * succeedingRate);
                }
            }
            vat12 = getVat(AmountDue);
            vatsale = getNonVat(AmountDue);
        }

        AmountGross = AmountDue;
        GLOBALS.getInstance().setAmountGross(AmountGross);
        GLOBALS.getInstance().setVat12(vat12);
        GLOBALS.getInstance().setVatsale(vatsale);

        //--------- Senior / PWD / Discounted
        if (isDiscounted) {
            discount = getDiscount(AmountGross, discountPercentage);
            //double discountDbl2Str = Math.round(discount * 110D) / 100D;

            vatAdjustment = getVatAdjustment(AmountGross, discountPercentage);
            NetOfVAT = getNonVat(AmountGross);
            NetOfDiscount = NetOfVAT - discount;
            vat12 = NetOfDiscount * 0.12;
            vatsale = 0;
            AmountDue = NetOfDiscount + (NetOfDiscount * .12f);
            GLOBALS.getInstance().setDiscount(discount);
            GLOBALS.getInstance().setVatAdjustment(vatAdjustment);
            GLOBALS.getInstance().setNetOfVAT(NetOfVAT);
            GLOBALS.getInstance().setNetOfDiscount(NetOfDiscount);
            GLOBALS.getInstance().setVat12(vat12);
            GLOBALS.getInstance().setVatsale(vatsale);

        }
        GLOBALS.getInstance().setAmountDue(AmountDue);

        //-------------------------------------------
    }

    private double getDiscount(double AmountDue, float discountPercentage) {
        if (AmountDue == 0) {
            return 0D;
        }

        double discount = (AmountDue * (discountPercentage/100)) / 1.12;
        return discount;
    }

    private double getDiscountFromVat(double AmountDue, float discountPercentage) {
        if (AmountDue == 0) {
            return 0D;
        }

        double discount = (AmountDue / 1.12) * (discountPercentage/100);
        return discount;
    }

    private double getVat(double AmountDue) {
        if (AmountDue == 0) {
            return 0D;
        }

        double adj = (AmountDue / 1.12) * 0.12f;
        return adj;
    }

    private double getVatAdjustment(double AmountDue, float discountPercentage) {
        if (AmountDue == 0) {
            return 0D;
        }

        double adj = (AmountDue * (discountPercentage/100)) - (AmountDue * (discountPercentage/100)) / 1.12;
        return adj;
    }

    private double getNonVat(double AmountDue) {
        if (AmountDue == 0) {
            return 0D;
        }
        DecimalFormat df2 = new DecimalFormat("#0.00");
        double nonVat = AmountDue / 1.12;
        return nonVat;
    }

}
