package bignumbers;

import java.math.BigInteger;

public class BigNumber implements Comparable<BigNumber> {

    private static final boolean POSITIVE = true;
    private static final boolean NEGATIVE = false;

    private final boolean sign;

    private final byte[] digits;

    public BigNumber(String val) {

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


        digits = null; //temporary assignment
        this.sign = false; //temporary assignment
    }

    public static void main(String[] args) {

        BigNumber bn = new BigNumber("-11233141235135134");
        // test

        // test 2
    }

    @Override
    public int compareTo(BigNumber o) {
        return 0;
    }
}
