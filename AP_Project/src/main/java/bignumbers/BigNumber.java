package bignumbers;

import java.math.BigInteger;
import java.util.Arrays;

public class BigNumber implements Comparable<BigNumber> {

    private static final boolean POSITIVE = true;
    private static final boolean NEGATIVE = false;

    private final boolean sign;

    private final byte[] digits;

    private final String number;

    public BigNumber(String val) {
        if (val == null || val.length() == 0)
            throw new NumberFormatException("Zero length BigNumber");

        boolean sign;
        val = val.toString();


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

        int valLength = val.length();
        digits = new byte[valLength];
        StringBuilder builder = new StringBuilder();
        for (int i=0;i<valLength;i++){
            digits[i] = (byte)(val.charAt(valLength - i - 1) - '0');
            builder.append(digits[i]);
        }
        builder.append(sign ? "" : "-");
        builder.reverse();

        this.number = builder.toString();



    }

    @Override
    public String toString() {
        return this.number;
    }




    public BigNumber add(BigNumber val) {
        throw new UnsupportedOperationException();
    }

    public BigNumber subtract(BigNumber val) {
        throw new UnsupportedOperationException();
    }

    public BigNumber multiply(BigNumber val) {
        throw new UnsupportedOperationException();
    }

    public BigNumber divide(BigNumber val) {
        throw new UnsupportedOperationException();
    }




    public static void main(String[] args) {

        BigNumber bn = new BigNumber("");
        System.out.println(bn.toString());

        // test 2
    }

    @Override
    public int compareTo(BigNumber o) {
        return 0;
    }
}
