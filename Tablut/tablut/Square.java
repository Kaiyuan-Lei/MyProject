package tablut;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;
import java.util.List;

import static tablut.Utils.*;

/** Represents a position on a Tablut board.  Positions are numbered
 *  from 0 (lower-left corner) to BOARD_SIZE * BOARD_SIZE - 1
 *  (upper-right corner). Squares are immutable and unique: there
 *  is precisely one square created for each distinct position.
 *  Clients create squares using the factory method sq, not the constructor.
 *  Because there is a unique Square object for each
 *  position, you can freely use the cheap == operator (rather than the
 *  .equals method) to compare Squares, and the program does not waste time
 *  creating the same square over and over again.
 *  @author Grace Lei
 */
final class Square {

    /** The total number of possible rows or columns. */
    static final int BOARD_SIZE = 9;

    /** The total number of possible squares. */
    static final int NUM_SQUARES = BOARD_SIZE * BOARD_SIZE;

    /** The regular expression for a square designation (e.g.,
     *  a3). For convenience, it is in parentheses to make it a
     *  group.  This subpattern is intended to be incorporated into
     *  other pattern that contain square designations (such as
     *  patterns for moves). */
    static final String SQ = "([a-i][1-9])";

    /** Return my row position, where 0 is the bottom row. */
    int row() {
        return _row;
    }

    /** Return my column position, where 0 is the leftmost column. */
    int col() {
        return _col;
    }

    /** Return my index position (0-80).  0 represents square a1, and 80
     *  is square i9. */
    int index() {
        return _index;
    }

    /** Return true iff THIS - TO is a valid rook move. */
    boolean isRookMove(Square to) {
        return this != to
                && (_row == to._row || _col == to._col);
    }

    /** Return true iff I am on the edge of the board. */
    boolean isEdge() {
        return (_row == 0 || _col == 0
                || _row == BOARD_SIZE - 1 || _col == BOARD_SIZE - 1);
    }

    /** Definitions of direction for rookMove.  DIR[k] = (dcol, drow)
     *  means that to going one step from (col, row) in direction k,
     *  brings us to (col + dcol, row + drow). */
    private static final int[][] DIR = {
        { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 }
    };

    /** Return the Square that is STEPS>0 squares away from me in direction
     *  DIR, or null if there is no such square.
     *  DIR = 0 for north, 1 for east, 2 for south and 3 for west.
     *  If DIR has another value, return null. Thus, unless the
     *  result is null the resulting square is a rook move away from me. */
    Square rookMove(int dir, int steps) {
        if (dir < 0 || dir > 3 || steps <= 0) {
            return null;
        }
        SqList L = ROOK_SQUARES[index()][dir];
        if (L.size() >= steps) {
            return L.get(steps - 1);
        } else {
            return null;
        }
    }

    /** Assuming OTHER is a 2-square rook move away, return the Square
     *  between. */
    Square between(Square other) {
        return sq((_col + other._col) / 2,
                (_row + other._row) / 2);
    }

    /** Return one of the diagonally adjacent square when facing OTHER. */
    Square diag1(Square other) {
        int dir = direction(other),
            turn = (dir + 1) & 3;
        int[] v0 = DIR[dir], v1 = DIR[turn];
        return sq(_col + v0[0] + v1[0], _row + v0[1] + v1[1]);
    }

    /** Return the diagonally adjacent square when facing OTHER that isn't
     *  diag1(OTHER). */
    Square diag2(Square other) {
        int dir = direction(other),
            turn = (dir - 1) & 3;
        int[] v0 = DIR[dir], v1 = DIR[turn];
        return sq(_col + v0[0] + v1[0], _row + v0[1] + v1[1]);
    }

    /** Return the direction (an int as defined in the documentation
     *  for rookMove) of the rook move THIS-TO. */
    int direction(Square to) {
        assert isRookMove(to);
        return row() < to.row() ? 0
            : row() > to.row() ? 2
            : col() > to.col() ? 3
            : 1;
    }

    /** Return true iff OTHER is orthogonally adjacent. */
    boolean adjacent(Square other) {
        return Math.abs(_col - other._col)
                + Math.abs(_row - other._row) == 1;
    }

    @Override
    public String toString() {
        return _str;
    }

    /** Return true iff COL ROW is a legal square. */
    static boolean exists(int col, int row) {
        return row >= 0 && col >= 0 && row < BOARD_SIZE && col < BOARD_SIZE;
    }

    /** Return the (unique) Square denoting COL ROW. */
    static Square sq(int col, int row) {
        if (!exists(row, col)) {
            throw error("row or column out of bounds");
        }
        return sq(row * BOARD_SIZE + col);
    }

    /** Return the (unique) Square denoting the position with index INDEX. */
    static Square sq(int index) {
        assert SQUARES[index] != null;
        return SQUARES[index];
    }

    /** Return the (unique) Square denoting the position COL ROW, where
     *  COL ROW is the standard text format for a square (e.g., a4). */
    static Square sq(String col, String row) {
        if (col.length() != 1 || row.length() != 1) {
            throw error("bad row or column");
        }
        return sq(col.charAt(0) - 'a', row.charAt(0) - '1');
    }

    /** Return the (unique) Square denoting the position in POSN, in the
     *  standard text format for a square (e.g. a4). POSN must be a
     *  valid square designation. */
    static Square sq(String posn) {
        assert posn.matches(SQ);
        return sq(posn.charAt(0) - 'a', posn.charAt(1) - '1');
    }

    /** Return the Square with index INDEX. */
    private Square(int index) {
        _index = index;
        _row = index / BOARD_SIZE;
        _col = index % BOARD_SIZE;
        _str = String.format("%c%d", (char) ('a' + _col), 1 + _row);
    }

    /** A convenience class to represent mutable lists of squares.  This
     *  class may safely be used as the element type in an array. */
    static class SqList extends ArrayList<Square> {
        /** An initially empty list. */
        SqList() {
        }

        /** A list initialize to INIT. */
        SqList(Collection<Square> init) {
            super(init);
        }
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other);
    }

    @Override
    public int hashCode() {
        return _index;
    }

    /** The cache of all created squares, by index. */
    private static final Square[] SQUARES =
        new Square[NUM_SQUARES];

    /** SQUARES viewed as a List. */
    static final List<Square> SQUARE_LIST = Arrays.asList(SQUARES);

    static {
        for (int i = NUM_SQUARES - 1; i >= 0; i -= 1) {
            SQUARES[i] = new Square(i);
        }
    }

    /** An empty list of Squares. */
    static final List<Square> EMPTY_SQUARE_LIST = new SqList();

    /** ROOK_SQUARES[i][d] is a list of all squares one rook move in direction
     *  d from the square with index i. Direction displacements are defined
     *  by DIR, above. Lists are in order of increasing distance from
     *  square i. */
    static final SqList[][] ROOK_SQUARES = new SqList[SQUARES.length][4];

    static {
        for (Square sq : SQUARES) {
            int r0 = sq.row(), c0 = sq.col(), i0 = sq.index();
            for (int d = 0; d < DIR.length; d += 1) {
                SqList L = ROOK_SQUARES[i0][d] = new SqList();
                for (int k = 1; true; k += 1) {
                    int c1 = c0 + k * DIR[d][0], r1 = r0 + k * DIR[d][1];
                    if (!exists(c1, r1)) {
                        break;
                    }
                    L.add(sq(c1, r1));
                }
            }
        }
    }

    /** My index position. */
    private final int _index;

    /** My row and column (redundant, since these are determined by _index). */
    private final int _row, _col;

    /** My String denotation. */
    private final String _str;
}
