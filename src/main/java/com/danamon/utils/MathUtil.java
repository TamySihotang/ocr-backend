package com.danamon.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

public class MathUtil {

    public static double ceilToNearest(double number, double unit) {
        return Math.ceil(number / unit) * unit;
    }

    public static double floorToNearest(double number, double unit) {
        return Math.floor(number / unit) * unit;
    }

    public static double decimalFormat(double x){
        DecimalFormat df = new DecimalFormat("#.##");
        df.format(x);
        return x;
    }
    public static Double removeDecimal(double x){
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        Number num = null;
        try {
            num = nf.parse(nf.format(x));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return num.doubleValue();
    }
}