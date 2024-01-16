package signpost;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;
import static org.junit.Assert.*;

import static signpost.Place.pl;
import signpost.Model.Sq;
import static signpost.Utils.msg;

/** Tests of the Model class.
 *  @author P. N. Hilfinger
 */
public class ModelTests {

    /** Given H x W array A, return a W x H array in which the columns of
     *  A, each reversed, are the rows of the result.  That is produces B
     *  so that B[x][y] is A[H - y - 1][x].  This is a convenience method
     *  that allows our test arrays to be arranged on the page to look as
     *  they do when displayed. */
    static int[][] tr(int[][] A) {
        int[][] B = new int[A[0].length][A.length];
        for (int x = 0; x < A[0].length; x += 1) {
            for (int y = 0; y < A.length; y += 1) {
                B[x][y] = A[A.length - y - 1][x];
            }
        }
        return B;
    }

    /** Return true iff the set of elements in EXPECTED is the same as that
     *  in ACTUAL. */
    private <T> void assertSetEquals(String msg,
                                     Collection<T> expected,
                                     Collection<T> actual) {
        assertNotNull(msg, actual);
        assertEquals(msg, new HashSet<T>(expected), new HashSet<T>(actual));
    }

    /** Check that MODEL is a valid representation of the solution in
     *  SOLUTION, and that the fixed numbers in it are precisely FIXED. */
    private void checkNumbers(int[][] solution, Model model,
                              Collection<Integer> fixed) {
        assertEquals("Wrong model width", solution.length, model.width());
        assertEquals("Wrong model height", solution[0].length, model.height());
        assertEquals("Wrong model size", solution.length
                        * solution[0].length,
                     model.size());
        HashSet<Integer> actualFixed = new HashSet<>();
        for (int x = 0; x < model.width(); x += 1) {
            for (int y = 0; y < model.height(); y += 1) {
                Sq sq = model.get(x, y);
                assertEquals(msg("Wrong at (%d, %d)", x, y),
                             solution[x][y], sq.sequenceNum());
                if (sq.hasFixedNum()) {
                    actualFixed.add(sq.sequenceNum());
                }
            }
        }
        assertEquals("Fixed positions differ", new HashSet<Integer>(fixed),
                     actualFixed);
    }

    /** Check that the arrow directions in MODEL agree with DIRS.  The
     *  direction of the arrow at (x, y) in MODEL should be DIRS[x][y].
     */
    private void checkArrows(int[][] dirs, Model model) {
        for (int x = 0; x < model.width(); x += 1) {
            for (int y = 0; y < model.height(); y += 1) {
                assertEquals(msg("Arrows differ at (%d, %d)", x, y),
                             dirs[x][y], model.get(x, y).direction());
            }
        }
    }

    @Test
    public void initTest1() {
        Model model = new Model(tr(SOLN1));
        checkNumbers(tr(BOARD1), model, asList(1, 16));
        System.out.println(model);
    }

    @Test
    public void initTest2() {
        Model model = new Model(SOLN2);
        checkNumbers(BOARD2, model, asList(1, 20));
    }

    @Test
    public void initTest3() {
        int[][] soln = tr(SOLN2);
        Model model = new Model(soln);
        model.solve();
        for (int x = 0; x < model.width(); x += 1) {
            YLoop:
            for (int y = 0; y < model.height(); y += 1) {
                for (Sq sq : model) {
                    if (x == sq.x && y == sq.y) {
                        assertEquals(msg("Wrong number at (%d, %d)", x, y),
                                     soln[x][y], sq.sequenceNum());
                        continue YLoop;
                    }
                }
                fail(msg("Did not find square at (%d, %d)", x, y));
            }
        }
    }

    @Test
    public void allPlacesTest() {
        Model model = new Model(tr(SOLN2));
        for (Sq sq : model) {
            assertEquals(msg("Wrong square at (%d, %d)", sq.x, sq.y),
                         sq, model.get(sq.x, sq.y));
            assertEquals(msg("Wrong square at Place %s", sq.pl),
                         sq, model.get(sq.pl));
            assertEquals(msg("Wrong Place at (%d, %d)", sq.x, sq.y),
                         pl(sq.x, sq.y), sq.pl);
        }
    }

    @Test
    public void arrowTest1() {
        Model model = new Model(tr(SOLN1));
        checkArrows(tr(ARROWS1), model);
    }

    @Test public void copyTest() {
        Model model1 = new Model(tr(SOLN1));
        Model model2 = new Model(model1);
        checkNumbers(tr(BOARD1), model2, asList(1, 16));
    }

    @Test
    public void solvedTest1() {
        Model model = new Model(tr(SOLN1));
        assertFalse("Model not solved yet.", model.solved());
        model.solve();
        assertTrue("Model should be solved.", model.solved());
        checkNumbers(tr(SOLN1), model, asList(1, 16));
    }

    @Test
    public void autoConnectTest1() {
        Model model = new Model(tr(new int[][] { { 1, 2 } }));
        model.autoconnect();
        assertTrue("Trivial puzzle should be solved at birth.", model.solved());
    }

    /* The following array data is written to look on the page like
     * the arrangement of data on the screen, with the first row
     * corresponding to the top row of the puzzle board, etc.  They are
     * transposed by tr into the actual data, in which the first array
     * dimension indexes columns, and the second indexes rows from bottom to
     * top. */

    private static final int[][] SOLN1 = {
        { 1, 13, 3, 2 },
        { 12, 4, 8, 15 },
        { 5, 9, 7, 14 },
        { 11, 6, 10, 16 }
    };

    private static final int[][] ARROWS1 = {
        { 2, 3, 5, 6 },
        { 1, 5, 5, 4 },
        { 3, 3, 8, 8 },
        { 8, 1, 6, 0 }
    };

    private static final int[][] BOARD1 = {
        { 1, 0, 0, 0 },
        { 0, 0, 0, 0 },
        { 0, 0, 0, 0 },
        { 0, 0, 0, 16 } };

    private static final int[][] SOLN2 = {
        { 1, 2, 17, 16, 3 },
        { 9, 7, 15, 6, 8 },
        { 12, 11, 18, 5, 4 },
        { 10, 13, 14, 19, 20 }
    };

    private static final int[][] BOARD2 = {
        { 1, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 20} };

}
