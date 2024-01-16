package tablut;

import java.io.InputStream;
import java.io.IOException;

/** Miscellaneous utilties.
 *  @author P. N. Hilfinger */

class Utils {

    /** Return an exception indicating some kind of error.  MSG and ARGS
     *  are as for String.format and define a message string. */
    static IllegalArgumentException error(String msg, Object... args) {
        return new IllegalArgumentException(String.format(msg, args));
    }

    /** Report a fatal error and exit.  MSG and ARGS are as for String.format
     *  and define a message string. */
    static void fatal(String msg, Object... args) {
        System.err.printf(msg, args);
        System.exit(1);
    }

    /** Return an input stream containing the contents of file NAME in the
     *  directory containing this class.  Throws IOException if no such
     *  file is available.  */
    static InputStream getResource(String name) throws IOException {
        InputStream result = Utils.class.getResourceAsStream(name);
        if (result == null) {
            throw new IOException("could not find " + name);
        }
        return result;
    }

}
