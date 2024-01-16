package tablut;

import org.checkerframework.checker.units.qual.K;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Formatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import static tablut.Move.ROOK_MOVES;
import static tablut.Move.MoveList;
import static tablut.Piece.*;
import static tablut.Square.*;


/** The state of a Tablut Game.
 *  @author Grace Lei
 */
class Board {

    /** The number of squares on a side of the board. */
    static final int SIZE = 9;

    /** The throne (or castle) square and its four surrounding squares.. */
    static final Square THRONE = sq(4, 4),
        NTHRONE = sq(4, 5),
        STHRONE = sq(4, 3),
        WTHRONE = sq(3, 4),
        ETHRONE = sq(5, 4);

    /** Initial positions of attackers. */
    static final Square[] INITIAL_ATTACKERS = {
        sq(0, 3), sq(0, 4), sq(0, 5), sq(1, 4),
        sq(8, 3), sq(8, 4), sq(8, 5), sq(7, 4),
        sq(3, 0), sq(4, 0), sq(5, 0), sq(4, 1),
        sq(3, 8), sq(4, 8), sq(5, 8), sq(4, 7)
    };

    /** Initial positions of defenders of the king. */
    static final Square[] INITIAL_DEFENDERS = {
        NTHRONE, ETHRONE, STHRONE, WTHRONE,
        sq(4, 6), sq(4, 2), sq(2, 4), sq(6, 4)
    };

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        if (model == this) {
            return;
        }
        init();
        _board.clear();
        for (int i = 0; i < SIZE * SIZE; i++) {
            Square sq = Square.sq(i);
            Piece p = model.get(sq);
            put(p, sq);
        }
        _turn = model.turn();

        _boardStack.push(encodedBoardPlus());
    }

    /** Clears the board to the initial position. */
    void init() {
        _board = new HashMap<>();
        _boardStack = new LinkedList<>();
        _boardStack.clear();
        put(KING, THRONE);
        for (Square sq : INITIAL_DEFENDERS) {
            put(WHITE, sq);
        }
        for (Square sq : INITIAL_ATTACKERS) {
            put(BLACK, sq);
        }
        for (int i = 0; i < SIZE * SIZE; i++) {
            Square sq = sq(i);
            if (!_board.containsKey(sq)) {
                put(EMPTY, sq);
            }
        }
        _turn = BLACK;
        _boardStack.push(encodedBoardPlus());
        _winner = null;
        _limit = 0;
    }

    /** Set the move limit to LIM.  It is an error if 2*LIM <= moveCount().
     * @param  n limit */
    void setMoveLimit(int n) {
        _limit = n;
    }

    /** Return a Piece representing whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the winner in the current position, or null if there is no winner
     *  yet. */
    Piece winner() {
        return _winner;
    }

    /** Returns true iff this is a win due to a repeated position. */
    boolean repeatedPosition() {
        return _repeated;
    }

    /** Record current position and set winner() next mover if the current
     *  position is a repeat. */
    private void checkRepeated() {
        String curr = encodedBoardPlus();
        if (_boardStack.subList(1, _boardStack.size()).contains(curr)) {
            _winner = turn();
            _repeated = true;
        }
    }

    /** Return the number of moves since the initial position that have not been
     *  undone. */
    int moveCount() {
        return _moveCount;
    }

    /** Return location of the king. */
    Square kingPosition() {
        for (int i = 0; i < SIZE * SIZE; i++) {
            Square sq = Square.sq(i);
            if (_board.get(sq) == KING) {
                return sq;
            }
        }
        return null;
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        return _board.get(Square.sq(col, row));
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {

        return get(col - 'a', row - '1');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        _board.put(s, p);
    }

    /** Set square S to P and record for undoing. */
    final void revPut(Piece p, Square s) {
        _board.put(s, p);
    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, sq(col - 'a', row - '1'));
    }

    /** Return true iff FROM - TO is an unblocked rook move on the current
     *  board.  For this to be true, FROM-TO must be a rook move and the
     *  squares along it, other than FROM, must be empty. */
    boolean isUnblockedMove(Square from, Square to) {
        if (!from.isRookMove(to)) {
            return false;
        }
        int dir = from.direction(to);
        int dis = 0;
        if (from.col() - to.col() != 0) {
            dis = Math.abs(from.col() - to.col());
        } else if (from.row() - to.row() != 0) {
            dis = Math.abs(from.row() - to.row());
        }
        for (int i = 1; i <= dis; i++) {
            if (get(from.rookMove(dir, i)) != EMPTY) {
                return false;
            }
        }
        return true;
    }

    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        return get(from).side() == _turn;
    }

    /** Return true iff FROM-TO is a valid move. */
    boolean isLegal(Square from, Square to) {
        if (!isLegal(from) || !isUnblockedMove(from, to)) {
            return false;
        }
        if (to == THRONE && get(from) != KING) {
            return false;
        }
        if ((from.row() == THRONE.row() && to.row() == THRONE.row()
                && from.col() < THRONE.col() && THRONE.col() < to.col())
                || (from.col() == THRONE.col() && to.col() == THRONE.col()
                && from.row() < THRONE.row() && THRONE.row() < to.row())) {
            return get(THRONE) == EMPTY;
        }
        return true;
    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to());
    }

    /** Check whether the square meets the condition for being captured.
     * @param sq a square.
     * @param lastmove last moving square. */
    private void captureHelper(Square sq, Square lastmove) {
        Piece pc = get(sq);

        if (pc == EMPTY || pc.side() == _turn) {
            return;
        }
        Square n = exists(sq.col(), sq.row() + 1)
                ? sq.rookMove(0, 1) : null;
        Square e = exists(sq.col() + 1, sq.row())
                ? sq.rookMove(1, 1) : null;
        Square s = exists(sq.col(), sq.row() - 1)
                ? sq.rookMove(2, 1) : null;
        Square w = exists(sq.col() - 1, sq.row())
                ? sq.rookMove(3, 1) : null;
        if (pc == KING && (sq == THRONE || sq == NTHRONE
                || sq == STHRONE || sq == WTHRONE || sq == ETHRONE)) {
            if (isHostile(sq, n, lastmove) && isHostile(sq, e, lastmove)
                    && isHostile(sq, s, lastmove)
                    && isHostile(sq, w, lastmove)) {
                capture(n, s);
            }
        } else if (n != null && s != null && isHostile(sq, n, lastmove)
                && isHostile(sq, s, lastmove)
                && (n == lastmove || s == lastmove)) {
            capture(n, s);
        } else if (e != null & w != null  && isHostile(sq, e, lastmove)
                && isHostile(sq, w, lastmove)
                && (w == lastmove || e == lastmove)) {
            capture(e, w);
        }
    }

    /** Move FROM-TO, assuming this is a legal move. */
    void makeMove(Square from, Square to) {
        assert isLegal(from, to);
        Piece moving = get(from);
        _board.put(from, EMPTY);
        revPut(moving, to);
        _moveCount++;

        for (Square sq : _board.keySet()) {
            captureHelper(sq, to);
        }
        _turn = _turn.opponent();
        checkWin(to);
        _boardStack.push(encodedBoardPlus());
    }

    /** Check if one side satisfies the condition to win.
     * @param lastmove last moving square. */
    private void checkWin(Square lastmove) {
        if (lastmove.isEdge() && get(lastmove) == KING) {
            _winner = WHITE;
        } else if (!_board.containsValue(KING)) {
            _winner = BLACK;
        } else if (!hasMove(_turn)) {
            _winner = _turn.opponent();
        } else if (_limit != 0 && moveCount() >= (2 * _limit)) {
            _winner = _turn.opponent();
        } else {
            checkRepeated();
        }
    }

    /** check if sq2 is a hostile square to sq1.
     * @param sq1 a nonempty square
     * @param sq2 another square
     * @param lastmove last moving square
     * @return whether sq1 and sq2 are hostile. */
    boolean isHostile(Square sq1, Square sq2, Square lastmove) {
        Piece pc1 = get(sq1).side();
        Piece pc2 = get(sq2).side();
        if (pc1.opponent() == pc2) {
            return true;
        } else if (sq2 == THRONE) {
            if (pc2 == EMPTY) {
                return true;
            } else if (pc1 == WHITE) {
                int count = 0;
                for (int i = 0; i < 4; i++) {
                    if (INITIAL_DEFENDERS[i] != sq2 && get(INITIAL_DEFENDERS[i])
                            == BLACK) {
                        count++;
                    }
                }
                return count == 3 && lastmove != NTHRONE && lastmove != STHRONE
                        && lastmove != WTHRONE && lastmove != ETHRONE;
            }
        }
        return false;
    }


    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to());
    }

    /** Capture the piece between SQ0 and SQ2, assuming a piece just moved to
     *  SQ0 and the necessary conditions are satisfied. */
    private void capture(Square sq0, Square sq2) {
        Piece p = get(sq0.between(sq2));
        revPut(EMPTY, sq0.between(sq2));
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (_moveCount > 0) {
            undoPosition();
            String curr = _boardStack.getFirst();
            String pieceName = String.valueOf(curr.charAt(0));
            if (pieceName.equals("B")) {
                _turn = BLACK;
            } else {
                _turn = WHITE;
            }
            String winner = String.valueOf(curr.charAt(1));
            if (winner.equals("B")) {
                _winner = BLACK;
            } else if (winner.equals("W")) {
                _winner = WHITE;
            } else {
                _winner = null;
            }
            _board.clear();
            String place = curr.substring(2);

            for (int i = 0; i < SIZE * SIZE; i++) {
                String pieceSymbol = String.valueOf(place.charAt(i));
                if (pieceSymbol.equals("-")) {
                    put(EMPTY, sq(i));
                } else if (pieceSymbol.equals("K")) {
                    put(KING, sq(i));
                } else if (pieceSymbol.equals("B")) {
                    put(BLACK, sq(i));
                } else if (pieceSymbol.equals("W")) {
                    put(WHITE, sq(i));
                }
            }
        }
    }


    /** Remove record of current position in the set of positions encountered,
     *  unless it is a repeated position or we are at the first move. */
    private void undoPosition() {
        _repeated = false;
        checkRepeated();
        if (!_repeated && _moveCount >= 1) {
            _boardStack.removeFirst();
        }
    }


    /** Clear the undo stack and board-position counts. Does not modify the
     *  current position or win status. */
    void clearUndo() {
        _boardStack.clear();
        _moveCount = 0;
    }

    /** Return a new mutable list of all legal moves on the current board for
     *  SIDE (ignoring whose turn it is at the moment). */
    List<Move> legalMoves(Piece side) {
        ArrayList<Move> l = new ArrayList<>();
        HashSet<Square> sideLocations = pieceLocations(side.side());
        Iterator<Square> iter = sideLocations.iterator();
        while (iter.hasNext()) {
            Square sq = iter.next();
            int index = sq.index();
            MoveList[] mlAllDir = ROOK_MOVES[index];
            for (MoveList ml : mlAllDir) {
                for (Move m : ml) {
                    if (isLegal(m)) {
                        if (get(m.from()) == KING) {
                            l.add(0, m);
                        } else {
                            l.add(m);
                        }
                    }
                }
            }

        }
        return l;
    }

    /** Return true iff SIDE has a legal move. */
    boolean hasMove(Piece side) {
        return !legalMoves(side).isEmpty();
    }

    @Override
    public String toString() {
        return toString(true);
    }

    /** Return a text representation of this Board.  If COORDINATES, then row
     *  and column designations are included along the left and bottom sides.
     */
    String toString(boolean coordinates) {
        Formatter out = new Formatter();
        for (int r = SIZE - 1; r >= 0; r -= 1) {
            if (coordinates) {
                out.format("%2d", r + 1);
            } else {
                out.format("  ");
            }
            for (int c = 0; c < SIZE; c += 1) {
                out.format(" %s", get(c, r));
            }
            out.format("%n");
        }
        if (coordinates) {
            out.format("  ");
            for (char c = 'a'; c <= 'i'; c += 1) {
                out.format(" %c", c);
            }
            out.format("%n");
        }
        return out.toString();
    }

    /** Return the locations of all pieces on SIDE. */
    private HashSet<Square> pieceLocations(Piece side) {
        assert side != EMPTY;
        HashSet<Square> hs = new HashSet<>();
        for (Square sq : _board.keySet()) {
            if (_board.get(sq).side() == side) {
                hs.add(sq);
            }
        }
        return hs;
    }

    /** Return the contents of _board in the order of SQUARE_LIST as a sequence
     *  of characters: the toString values of the current turn and Pieces. */
    String encodedBoard() {
        char[] result = new char[Square.SQUARE_LIST.size() + 1];
        result[0] = turn().toString().charAt(0);
        for (Square sq : SQUARE_LIST) {
            result[sq.index() + 1] = get(sq).toString().charAt(0);
        }
        return new String(result);
    }

    /**  Return the contents of _board in the order of SQUARE_LIST as a sequence
     *  of characters: the toString values of the current turn, the winner
     *  and Pieces. */
    String encodedBoardPlus() {
        char[] result = new char[Square.SQUARE_LIST.size() + 2];
        result[0] = turn().toString().charAt(0);
        if (winner() == null) {
            result[1] = "N".charAt(0);
        } else {
            result[1] = winner().toString().charAt(0);
        }
        for (Square sq : SQUARE_LIST) {
            result[sq.index() + 2] = get(sq).toString().charAt(0);
        }
        return new String(result);
    }

    /** Return the different between the number of white pieces and
     * black pieces, make King weigh 50 .*/
    int countDifference() {
        int whiteCount = 0;
        int blackCount = 0;
        for (int i = 0; i < SIZE * SIZE; i++) {
            Piece p = get(sq(i));
            if (p == KING) {
                whiteCount = whiteCount + (10 * 5);
            } else if (p == WHITE) {
                whiteCount++;
            } else if (p == BLACK) {
                blackCount++;
            }
        }
        return whiteCount - blackCount;
    }


    /** Piece whose turn it is (WHITE or BLACK). */
    private Piece _turn;
    /** Cached value of winner on this board, or EMPTY if it has not been
     *  computed. */
    private Piece _winner;
    /** Number of (still undone) moves since initial position. */
    private int _moveCount;
    /** True when current board is a repeated position (ending the game). */
    private boolean _repeated;

    /** A concrete representation of which piece each square contains.  */
    private HashMap<Square, Piece> _board;

    /** The maximum number of moves that a player may
     * make during the current game.  */
    private int _limit;

    /** The stack to keep track of the history of board.  */
    private LinkedList<String> _boardStack;
}
