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

        BigNumber a = new BigNumber("1234");
        BigNumber b = new BigNumber("1234");
        BigNumber c = new BigNumber("-1234");
        BigNumber d = new BigNumber("+1234");
        BigNumber e = new BigNumber("00000001234");
        BigNumber f = new BigNumber("-000001234");
        BigNumber g = new BigNumber("120000034");
        BigNumber h = new BigNumber("220000034");

        System.out.println(a.compareTo(b)); // 0
        System.out.println(a.compareTo(c)); // 1
        System.out.println(c.compareTo(a)); // -1
        System.out.println(a.compareTo(d)); // 0
        System.out.println(a.compareTo(e)); // 0
        System.out.println(e.compareTo(f)); // 1
        System.out.println(f.compareTo(e)); // -1
        System.out.println(g.compareTo(a)); // 1
        System.out.println(g.compareTo(h)); // -1

//        BigNumber i = new BigNumber("--123");
//        BigNumber j = new BigNumber(" 123");
//        BigNumber k = new BigNumber("1+");
//        BigNumber l = new BigNumber("12 34");
//        BigNumber m = new BigNumber("12b34");
//        BigNumber n = new BigNumber("12-34");
//        BigNumber o = new BigNumber("12~34");
//        BigNumber p = new BigNumber("1234 ");
//        BigNumber q = new BigNumber("");
//        BigNumber r = new BigNumber("+");
//        BigNumber s = new BigNumber("-");
//        BigNumber t = new BigNumber("+ ");
//        BigNumber u = new BigNumber(null);
        BigNumber v = new BigNumber("00+00");

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
}
