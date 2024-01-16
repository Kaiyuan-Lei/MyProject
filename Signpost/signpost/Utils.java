package signpost;

import static java.lang.System.arraycopy;


/** Various utility methods.
 *  @author P. N. Hilfinger
 */
class Utils {

    /** Returns String.format(FORMAT, ARGS...). */
    static String msg(String format, Object... args) {
        return String.format(format, args);
    }

    /** Copy contents of SRC into DEST.  SRC and DEST must both be
     *  rectangular, with identical dimensions. */
    static void deepCopy(int[][] src, int[][] dest) {
        assert src.length == dest.length && src[0].length == dest[0].length;
        for (int i = 0; i < src.length; i += 1) {
            arraycopy(src[i], 0, dest[i], 0, src[i].length);
        }
    }

    /** Return an IllegalArgumentException whose message is formed from
     *  MSGFORMAT and ARGS as for String.format. */
    static IllegalArgumentException badArgs(String msgFormat, Object... args) {
        return new IllegalArgumentException(String.format(msgFormat, args));
    }

    /** Return integer denoted by NUMERAL. */
    static int toInt(String numeral) {
        return Integer.parseInt(numeral);
    }

    /** Return long integer denoted by NUMERAL. */
    static long toLong(String numeral) {
        return Long.parseLong(numeral);
    }

}

