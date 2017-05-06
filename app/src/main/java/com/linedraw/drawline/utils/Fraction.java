package com.linedraw.drawline.utils;

import java.math.BigInteger;

/**
 * Created by byron.
 */
public class Fraction {

    private static final String TAG = Fraction.class.getSimpleName();

    private int numerator;
    private int denominator;

    private int simplifiedNum;
    private int simplifiedDen;

    public Fraction() {

    }

    public Fraction(int numerator, int denominator) {
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
        int gcd = getGcd(numerator, denominator);
        simplifiedNum = (numerator * denominator) >= 0 ?
                Math.abs(numerator / gcd) : (-1) * Math.abs(numerator / gcd);
        simplifiedDen = Math.abs(denominator / gcd);
    }

    public int getSimplifiedNum() {
        return this.simplifiedNum;
    }

    public int getSimplifiedDen() {
        return this.simplifiedDen;
    }

    @Override
    public String toString() {
        return String.format("%s / %s", this.simplifiedNum, this.simplifiedDen);
    }

    public Double toDouble() {
        return (double) this.numerator / (double) this.denominator;
    }

    public int toInt() {
        double res = this.numerator / this.denominator;
        return res >= 0 ? (int) (res + 0.5) : (int) (res - 0.5);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Fraction)) {
            return false;
        }
        Fraction input = (Fraction) obj;
        return this.toInt() == input.toInt();
    }

    private static int getGcd(int a, int b) {
        BigInteger b1 = BigInteger.valueOf(a);
        BigInteger b2 = BigInteger.valueOf(b);
        BigInteger gcd = b1.gcd(b2);
        return gcd.intValue();
    }
}
