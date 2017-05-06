package com.linedraw.drawline.utils;

import android.util.Log;

import java.math.BigInteger;

/**
 * Created by byron.
 */
public class YIntercept {

    private static final String TAG = YIntercept.class.getSimpleName();

    private int numerator;
    private int denominator;

    private int simplifiedNum = 0;
    private int simplifiedDen = 1;

    public YIntercept(Fraction slope, int x, int y) {
        // b = y + (slope * x)
        Log.d(TAG, "slope.getSimplifiedNum() = " + slope.getSimplifiedNum());
        Log.d(TAG, "x = " + x);
        int mx_num = slope.getSimplifiedNum() * x;
        Log.d(TAG, "mx_num = " + mx_num);
        int mx_den = slope.getSimplifiedDen();
        Log.d(TAG, "mx_den = " + mx_den);
        int ymx_num = (y * mx_den) - mx_num;
        Log.d(TAG, "ymx_num = " + ymx_num);
        setValue(ymx_num, mx_den);
    }

    public YIntercept(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator == 0 ? 1 : denominator;

        calculate();
    }

    public void setValue(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator == 0 ? 1 : denominator;

        calculate();
    }

    public void calculate() {
        int mod = this.numerator % this.denominator;
        if (mod == 0) {
            this.simplifiedNum = this.numerator / this.denominator;
            this.simplifiedDen = 1;
        } else {
            int gcd = getGcd(numerator, denominator);
            boolean isPositive = (this.numerator * this.denominator) > 0;
            this.simplifiedNum = Math.abs(numerator / gcd) * (isPositive ? 1 : -1);
            //(mod * denominator) >= 0 ?
            //(mod / gcd) : (-1) * (mod / gcd);
            this.simplifiedDen = Math.abs(denominator / gcd);
        }

    }

    private static int getGcd(int a, int b) {
        BigInteger b1 = BigInteger.valueOf(a);
        BigInteger b2 = BigInteger.valueOf(b);
        BigInteger gcd = b1.gcd(b2);
        return gcd.intValue();
    }

    public Double toDouble() {
        return (double) this.simplifiedNum / (double) this.simplifiedDen;
    }

    public int toInt() {
        return this.simplifiedNum / this.simplifiedDen;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (this.simplifiedDen == 1) {
            builder.append(this.simplifiedNum);
        } else {
            builder.append(String.format("%s / %s", this.simplifiedNum, this.simplifiedDen));
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof YIntercept &&
                this.toDouble().equals(((YIntercept) obj).toDouble());
    }
}
