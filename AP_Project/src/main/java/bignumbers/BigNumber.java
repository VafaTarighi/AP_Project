package bignumbers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class BigNumber implements Comparable<BigNumber> {

    private static final boolean POSITIVE = true;
    private static final boolean NEGATIVE = false;

    public static final BigNumber ZERO = new BigNumber(new byte[] {0}, BigNumber.POSITIVE);
    public static final BigNumber ONE = new BigNumber(new byte[] {1}, BigNumber.POSITIVE);
    public static final BigNumber NEGATIVE_ONE = new BigNumber(new byte[] {-1}, BigNumber.NEGATIVE);

    private final boolean sign;

    private final byte[] digits;

    private final String number;


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

    public BigNumber(String val) {
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





        this.sign = sign; //temporary assignment

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

    @Override
    public String toString() {
        return this.number;
    }




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

        if (c1.compareTo(c2) == 0)
            return ZERO;

        BigNumber tmp;
        if (c1.compareTo(c2) >= 0) {
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
        if (this == ZERO || val == ZERO)
            return ZERO;

        boolean resultSign = (sign == val.sign)? BigNumber.POSITIVE : BigNumber.NEGATIVE;
        //Karatsuba Multiplication Algorithm
        return new BigNumber(karatsubaMultiply(this.abs(), val.abs()).digits, resultSign);

    }

    private BigNumber karatsubaMultiply(BigNumber x, BigNumber y) {
        //base case
        if (x.digits.length < 5 && y.digits.length < 5) {
            int xInt = Integer.parseInt(x.number);
            int yInt = Integer.parseInt(y.number);

            return new BigNumber("" + (xInt * yInt));
        }

        int lenX = x.digits.length; // 1234 56789 len 9
        int lenY = y.digits.length;//  0000 00123 len 3
        int n = Math.max(lenX, lenY);
        String fsn = "%0" + n + "d";
        String fs = "%0" + (n/2) + "d";
        String nZero = String.format(fsn, 0);
        String hnZero = String.format(fs, 0);

        String formattedX = nZero.substring(lenX, n) + x.number;
        BigNumber a = new BigNumber(formattedX.substring(0, n/2));
        BigNumber b = new BigNumber(formattedX.substring(n/2));

        String formattedY = nZero.substring(lenY, n) + y.number;
        BigNumber c = new BigNumber(formattedY.substring(0, n/2));
        BigNumber d = new BigNumber(formattedY.substring(n/2));

        // x*y = 10^(n)*(ac) + 10^(n/2)*(ad + bc) + bd
        BigNumber ac = karatsubaMultiply(a, c);
        BigNumber bd = karatsubaMultiply(b, d);
        BigNumber aPbcPd = karatsubaMultiply(a.add(b), c.add(d));
        BigNumber adPbc = aPbcPd.subtract(ac.add(bd));

        return (new BigNumber(ac.number + nZero)).add(new BigNumber(adPbc.number + hnZero)).add(new BigNumber(bd.number));
    }

    public BigNumber divide(BigNumber val) {
        throw new UnsupportedOperationException();
    }




    public static void main(String[] args) {

//        String s1 = "8517309817418905713409856324870561309587312513458y134876157834651873456391785613875632849756395876435872652438795689743265184975621389756389457658913659834756y134897564871605789563045610345671056341708561y3480756134805761304857657861438057648157365740356843765170348563287045643870156438756382176587341651849623846021893472103895631490856735783180563218075689437561308563297546380563145610356739847563247856340856712398576324056732489756238497563786518051875346598723645897546257823569618032765y7341085612037029137561023561705358947563248956347856985612809326401895109856984756234857623489576103845632748562398475608457623984756324785608457619845761234985762348975634280756329857639845679382457618957632148761481746389231764872165871237456981753681923476589345765689345618756891765389274612983576289357689215762893457602156056013560560150000000005780587134573240857623984563485763187457634897568561871246812346783274563478568745368231479847156198576984387019357130294578431095234089576149057823408572340895710457670471309856130847651";
//        s1 = s1.replaceAll("\\D", "0");
//        BigInteger bi1 = new BigInteger(s1);
//        String s2 = "851730981741890571340985632487056130958734651873456391785613875632849756395876435872652438795689743265184975621389756389457658913659834756y134897564871605789563045610345671056341708561y3480756134805761304857657861438057648157365740356843765170348563287045643870156438756382176587341651849623846021893472103895631490856735783180563218075689437561308563297546380563145610356739847563247856340856712398576324056732489756238497563786518051875346598723645897546257823569618032765y7341085612037029137561023561705358947563248956347856985612809326401895109856984756234857623489576103845632748562398475608457623984756324785608457619845761234985762348975634280756329857639845679382457618957632148761481746389231764872165871237456981753681923476589345765689345618756891765389274612983576289357689215762893457602156056013560560150000000005780587134573240857623984563485763187457634897568561871246812346783274563478568745368231479847156198576984387019357130294578431095234089576149057823408572340895710457670471309856130847651";
//        s2 = s2.replaceAll("\\D", "0");
//        BigInteger bi2 = new BigInteger(s2);
//        System.out.println(bi1.multiply(bi2));
//        int n = 99999;
//        System.out.println(new BigInteger(""+n).multiply(new BigInteger(""+n)));
//        System.out.println(n*n);

////        String a = "1234567891011121314151617181920";
//        String a = "12345";
////        String b = "2019181716151413121110987654321";
//        String b = "56789";
//        BigNumber bigNa = new BigNumber(a);
//        BigNumber bigNb = new BigNumber(b);
//        BigNumber res = bigNa.multiply(bigNb);
//        System.out.println(res.toString());
//
//        BigInteger bigIa = new BigInteger(a);
//        BigInteger bigIb = new BigInteger(b);
//        BigInteger resI = bigIa.multiply(bigIb);
//        System.out.println(resI.toString());

        System.out.println(new BigNumber("123456779").add(new BigNumber("-123456789")));
        System.out.println(new BigInteger("123456779").add(new BigInteger("-123456789")));
        BigNumber test = new BigNumber("1234").add(new BigNumber("-1239"));
        System.out.println(Arrays.toString(test.digits));

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

    public BigNumber abs(){
        if(this.toString().charAt(0) != '-'){
            return this;
        }
        return new BigNumber(this.digits, BigNumber.POSITIVE);
    }
}
