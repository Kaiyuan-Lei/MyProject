package signpost;

import org.junit.Test;
import static org.junit.Assert.*;

/** Tests of the Model class.
 *  @author P. N. Hilfinger
 */
public class PuzzleGeneratorTests {

    /** Check that SOLN is a valid WIDTH x HEIGHT puzzle that has fixed ends
     *  unless ALLOWLOOSE. */
    private void checkPuzzle(int[][] soln,
                             int width, int height, boolean allowLoose) {
        assertTrue("Bad size",
                   soln.length == width && soln[0].length == height);
        int last = width * height;
        for (int x0 = 0; x0 < width; x0 += 1) {
            for (int y0 = 0; y0 < width; y0 += 1) {
                int v = soln[x0][y0];
                if (v == last) {
                    continue;
                }
                assertTrue("Value out of range", v >= 1 && v <= last);
                int c;
                for (int x1 = c = 0; x1 < width; x1 += 1) {
                    for (int y1 = 0; y1 < height; y1 += 1) {
                        if (soln[x1][y1] == v + 1) {
                            assertTrue("Values not in line",
                                       x0 == x1 || y0 == y1
                                       || Math.abs(x0 - x1)
                                       == Math.abs(y0 - y1));
                            c += 1;
                        }
                    }
                }
                assertEquals("Duplicate or unconnected values", 1, c);
            }
        }
        if (!allowLoose) {
            assertTrue("End points incorrect",
                       soln[0][height - 1] == 1 && soln[width - 1][0] == last);
        }
    }

    @Test
    public void puzzleTest() {
        PuzzleGenerator puzzler = new PuzzleGenerator(314159);
        Model model;
        model = puzzler.getPuzzle(5, 5, false);
        checkPuzzle(model.solution(), 5, 5, false);
        model = puzzler.getPuzzle(4, 6, false);
        checkPuzzle(model.solution(), 4, 6, false);
        model = puzzler.getPuzzle(5, 5, true);
        checkPuzzle(model.solution(), 5, 5, true);
    }

    @Test
    public void uniquePuzzleTest() {
        PuzzleGenerator puzzler = new PuzzleGenerator(314159);
        Model model;
        model = puzzler.getPuzzle(1, 2, false);
        checkPuzzle(model.solution(), 1, 2, false);
        model = puzzler.getPuzzle(1, 3, false);
        checkPuzzle(model.solution(), 1, 3, false);
        model = puzzler.getPuzzle(3, 3, false);
        checkPuzzle(model.solution(), 3, 3, false);
    }


}
