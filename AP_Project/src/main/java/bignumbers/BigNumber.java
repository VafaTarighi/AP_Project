package bignumbers;

import java.math.BigInteger;
import java.util.Arrays;

public class BigNumber implements Comparable<BigNumber> {

    private static final boolean POSITIVE = true;
    private static final boolean NEGATIVE = false;

    public static final BigNumber ZERO = new BigNumber(new byte[] {0}, BigNumber.POSITIVE);
    public static final BigNumber ONE = new BigNumber(new byte[] {1}, BigNumber.POSITIVE);
    public static final BigNumber NEGATIVE_ONE = new BigNumber(new byte[] {1}, BigNumber.NEGATIVE);

    private final boolean sign;

    private final byte[] digits;

    private final String number;

    //// Static Factory Methods
    public static BigNumber fromString(String val) {
        return new BigNumber(val);
    }

    public static BigNumber fromLong(long val) {
        if (val == 0)
            return ZERO;
        if (val == 1)
            return ONE;
        if (val == -1)
            return NEGATIVE_ONE;

        boolean sign;
        if (val < 0)
            sign = BigNumber.NEGATIVE;
        else
            sign = BigNumber.POSITIVE;

        val = Math.abs(val);
        int length = String.valueOf(val).length();
        byte[] digits = new byte[length];

        for (int i = 0; i < length; i++) {
            digits[i] = (byte) (val % 10);
            val /= 10;
        }

        return new BigNumber(digits, sign);
    }

    public static BigNumber fromByteArray(byte[] digits, Sign sign) {
        if (digits == null || sign == null)
            throw new NullPointerException();

        for (byte b : digits) {
            if (b > 9)
                throw new IllegalArgumentException();
        }

        if (sign == Sign.POSITIVE)
            return new BigNumber(digits, BigNumber.POSITIVE);
        else
            return new BigNumber(digits, BigNumber.NEGATIVE);
    }

    public static BigNumber fromByteArray(byte[] digits) {
        return fromByteArray(digits, Sign.POSITIVE);
    }

    public static BigNumber fromIntArray(int[] digits, Sign sign) {
        if (digits == null || sign == null)
            throw new NullPointerException();

        byte[] byteDigits = new byte[digits.length];
        for (int i = 0; i < digits.length; i++) {
            if (digits[i] > 9)
                throw new IllegalArgumentException();

            byteDigits[i] = (byte) digits[i];
        }

        if (sign == Sign.POSITIVE)
            return new BigNumber(byteDigits, BigNumber.POSITIVE);
        else
            return new BigNumber(byteDigits, BigNumber.NEGATIVE);
    }

    public static BigNumber fromIntArray(int[] digits) {
        return fromIntArray(digits, Sign.POSITIVE);
    }

    
    //// Constructors
    private BigNumber(byte[] digits, boolean sign){
        this.digits = digits;
        this.sign = sign;
        StringBuilder builder = new StringBuilder();
        builder.append(sign ? "" : "-");

        for (int i = digits.length - 1; i >= 0; i--){
            builder.append(digits[i]);
        }

        this.number = builder.toString();
    }

    private BigNumber(String val) {
        if (val == null || val.length() == 0)
            throw new NumberFormatException("Zero length BigNumber");

        boolean sign;


        if (val.charAt(0) == '-') {
            val = val.substring(1);
            sign = BigNumber.NEGATIVE;
        }
        else if (val.charAt(0) == '+') {
            val = val.substring(1);
            sign = BigNumber.POSITIVE;
        }
        else {
            sign = BigNumber.POSITIVE;
        }

        if (val.length() == 0)
            throw new NumberFormatException("Zero length BigNumber");

        if (!val.matches("\\d+")) {
            StringBuilder message = new StringBuilder("For input string: \"");
            int charIndex = 0;
            for (int i = 0; i < val.length(); i++) {
                if (!("" + val.charAt(i)).matches("\\d")) {
                    charIndex = i;
                    break;
                }
            }

            String prb = val.substring(Math.max(charIndex - 4, 0),
                    Math.min(charIndex + 4, val.length()));

            message.append(prb).append("\"");

            throw new NumberFormatException(message.toString());
        }


        // remove leading zeros like 00091
        val = val.replaceAll("^0+","");
        if(val.length() == 0){
            val = "0";
            sign = BigNumber.POSITIVE;
        }





        this.sign = sign;

        int length = val.length();
        digits = new byte[length];
        StringBuilder builder = new StringBuilder();
        builder.append(sign ? "" : "-");
        for (int i = 0; i < length; i++) {
            digits[i] = (byte) (val.charAt(length - (i + 1)) - '0');
            builder.append(val.charAt(i));
        }
        this.number = builder.toString();


    }


    //// add, subtract, multiply & divide operation methods
    public BigNumber add(BigNumber val) {
        StringBuilder builder = new StringBuilder();
        int p;
        int carry = 0;
        boolean sign;
        if(this.sign == val.sign){
            sign = this.sign;
            for(int i = 0, j = 0; i < digits.length || j < val.digits.length; i++, j++){
                p = (i < digits.length) ? this.digits[i] : 0;
                p+= (j < val.digits.length) ? val.digits[j] : 0;
                p+= carry;
                builder.append(p%10);
                carry = p/10;
            }
            if(carry > 0){
                builder.append(carry);
            }
            if(this.sign == BigNumber.NEGATIVE){
                builder.append('-');
            }
            return new BigNumber(builder.reverse().toString());
        }


        // different signs


        BigNumber c1 = this.abs();
        BigNumber c2 = val.abs();
        int cmp = c1.compareTo(c2);
        if (cmp == 0)
            return ZERO;

        BigNumber tmp;
        if (cmp > 0) {
            sign = this.sign;
        } else {
            sign = val.sign;
            tmp = c1;
            c1 = c2;
            c2 = tmp;
        }
        int len1 = c1.digits.length;
        int len2 = c2.digits.length;

        int sub;
        int cr = 0;
        byte[] c1digits = c1.digits.clone();

        for (int i = 0, j = 0; i < len1 || j < len2; i++, j++) {
            if (j < len2)
                sub = (c1.digits[i] - c2.digits[j] + cr);
            else {
                sub = c1.digits[i] + cr;
            }

            if (sub < 0) {
                sub += 10;
                cr = -1;
            }
            else cr = 0;

            c1digits[i] = (byte) sub;
        }

        int count  = 0;
        for(int i=c1digits.length-1;i>=0;i--){
            if(c1digits[i] != 0){
                break;
            }
            count ++;
        }
        return new BigNumber(Arrays.copyOfRange(c1digits,0,c1digits.length-count),sign);

    }

    public BigNumber subtract(BigNumber val) {
        return this.add(new BigNumber(val.digits,!val.sign));
    }

    public BigNumber multiply(BigNumber val) {
        if (this.compareTo(ZERO) == 0 || val.compareTo(ZERO) == 0)
            return ZERO;

        if (this.compareTo(ONE) == 0)
            return val;
        if (val.compareTo(ONE) == 0)
            return this;

        if (this.compareTo(NEGATIVE_ONE) == 0)
            return new BigNumber(val.digits, !val.sign);
        if (val.compareTo(NEGATIVE_ONE) == 0)
            return new BigNumber(digits, !sign);

        boolean resultSign = (sign == val.sign)? BigNumber.POSITIVE : BigNumber.NEGATIVE;
        String result = (resultSign == NEGATIVE ? "-" : "") +
                multiplyAlgorithm(this.abs().toString(), val.abs().toString());
        return new BigNumber(result);

    }

    private String multiplyAlgorithm(String x, String y) {
        //base case
        if (x.length() < 5 && y.length() < 5) {
            int xInt = Integer.parseInt(x);
            int yInt = Integer.parseInt(y);

            return Integer.toString(xInt * yInt);
        }

        int lenX = x.length(); // 1234 56789 len 9
        int lenY = y.length();//  0000 00123 len 3
        int n = Math.max(lenX, lenY);
        int m = n - n/2;
        String nZero = String.format("%0" + n + "d", 0);
        String Bm = String.format("%0" + m + "d", 0);

        String formattedX = nZero.substring(lenX, n) + x;
        String a = formattedX.substring(0, n/2);
        String b = formattedX.substring(n/2);

        String formattedY = nZero.substring(lenY, n) + y;
        String c = formattedY.substring(0, n/2);
        String d = formattedY.substring(n/2);

        // x*y = 10^(n)*(ac == z2) + 10^(n/2)*((ad + bc) == z1) + (bd == z0)
        String z2 = multiplyAlgorithm(a, c);
        String z0 = multiplyAlgorithm(b, d);
        String z1 = Util.subtract(multiplyAlgorithm(Util.sum(a, b), Util.sum(c, d)), Util.sum(z2, z0));

        return Util.sum(Util.sum(z2 + Bm + Bm, z1 + Bm), z0);
    }

    public BigNumber divide(BigNumber val) {

        // validation
        if(val.compareTo(ZERO) == 0){
            throw new ArithmeticException("Division by zero");
        }
        if(this.compareTo(ZERO) == 0)
            return ZERO;


        // abs
        String a = this.toString().replaceAll("-","");
        String b = val.toString().replaceAll("-","");


        int cmp = Util.compare(a,b);
        if(cmp < 0)
            return ZERO;
        if(cmp == 0){
            return this.sign == val.sign ? ONE : NEGATIVE_ONE;
        }

        int zeroCount = a.length() - b.length();
        String m = a.substring(0,b.length());
        if(m.compareTo(b) < 0)
            zeroCount--;
        if(zeroCount < 0)
            zeroCount = 0;


        StringBuilder builder = new StringBuilder(zeroCount);
        builder.append(b);
        for(int i=0;i<zeroCount;i++){
            builder.append("0");
        }



        // division
        String q;
        int bLen = builder.length();
        int count=0;
        StringBuilder builder2 = new StringBuilder();
        String result = "";
        for(int i=zeroCount;i>=0;i--){
            count = 0;
            q = builder.substring(0,bLen-zeroCount+i);
            while (Util.compare(a,q) >= 0){
                a = Util.subtract(a,q);
                count++;
            }

            builder2.setLength(0);
            builder2.append(count);

            for(int j=0;j<i;j++){
                builder2.append("0");
            }

            result = Util.sum(builder2.toString(),result);
        }
        if(this.sign != val.sign)
            result = "-" + result;

        return new BigNumber(result);

    }

    //// abs & BigNumber comparison methods
    public BigNumber abs(){
        return this.sign == BigNumber.POSITIVE ? this : new BigNumber(this.digits, BigNumber.POSITIVE);
    }

    public boolean isGreaterThan(BigNumber val){
        return (this.compareTo(val) > 0);
    }

    @Override
    public int compareTo(BigNumber val) {
        if (val == null)
            throw new NullPointerException();

        if (sign == val.sign) {
            if (sign == POSITIVE)
                return this.compareDigits(val);
            else
                return val.compareDigits(this);
        }

        return sign == POSITIVE ? 1 : -1;
    }

    private int compareDigits(BigNumber val) {
        if (digits.length == val.digits.length) {
            for (int i = digits.length - 1; i >= 0; i--) {
                if (digits[i] != val.digits[i])
                    return digits[i] > val.digits[i] ? 1 : -1;
            }
            return 0;
        }

        return digits.length > val.digits.length ? 1 : -1;
    }

    @Override
    public String toString() {
        return this.number;
    }

    //// equals & hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BigNumber bigNumber = (BigNumber) o;
        return sign == bigNumber.sign &&
                Arrays.equals(digits, bigNumber.digits);
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }
}

class Util{
    public static int compare(String s1, String s2){
        int len1 = s1.length();
        int len2 = s2.length();
        if(len1 > len2) return 1;
        if(len1 < len2) return -1;

        return s1.compareTo(s2);

    }
    public static String sum(String s1, String s2){
        int len1 = s1.length();
        int len2 = s2.length();
        StringBuilder builder = new StringBuilder();
        int p=0,carry=0;
        for(int i=len1-1,j=len2-1;i>=0 || j>=0;i--,j--){
            p =  (i>=0) ? (s1.charAt(i) - '0') : 0;
            p+=  (j>=0) ? (s2.charAt(j) - '0') : 0;
            p+= carry;
            carry = p/10;
            builder.append(p%10);
        }
        if(carry > 0)
            builder.append(carry);
        return builder.reverse().toString();
    }

    // s1 must be greater than s2
    public static String subtract(String s1, String s2){

        s1 = s1.replaceAll("^0+","");
        s2 = s2.replaceAll("^0+","");
        s1 = s1.length() == 0 ? "0" : s1;
        s2 = s2.length() == 0 ? "0" : s2;

        int len1 = s1.length();
        int len2 = s2.length();
        StringBuilder builder = new StringBuilder();
        int p=0,carry=0;
        if(s1.equals(s2)) return  "0";
        if(len2 == 1 && s2.charAt(0) == '0') return  s1;
        for(int i=len1-1,j=len2-1;i>=0 || j>=0;i--,j--){
            p = s1.charAt(i) - '0';
            p-= (j >= 0) ? s2.charAt(j) - '0' : 0;
            p+=carry;
            carry = 0;
            if(p<0){
                carry = -1;
                p+=10;
            }
            builder.append(p);
        }
        String result = builder.reverse().toString().replaceAll("^0+","");
        return (result.equals("") ? "0" : result);
    }
}
