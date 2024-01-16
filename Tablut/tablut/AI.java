package tablut;

import static java.lang.Math.*;
import static tablut.Piece.*;



/** A Player that automatically generates moves.
 *  @author Grace Lei
 */
class AI extends Player {

    /** A position-score magnitude indicating a win (for white if positive,
     *  black if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /** A position-score magnitude indicating a forced win in a subsequent
     *  move.  This differs from WINNING_VALUE to avoid putting off wins. */
    private static final int WILL_WIN_VALUE = Integer.MAX_VALUE - 40;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI with no piece or controller (intended to produce
     *  a template). */
    AI() {
        this(null, null);
    }

    /** A new AI playing PIECE under control of CONTROLLER. */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        String myMove = findMove().toString();
        _controller.reportMove(_lastFoundMove);
        return myMove;
    }

    @Override
    boolean isManual() {
        return false;
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        _lastFoundMove = null;
        if (myPiece() == WHITE) {
            findMove(b, maxDepth(b), true, 1, -INFTY, INFTY);
        } else {
            findMove(b, maxDepth(b), true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {
        //testing here
        System.out.println(board);

        if (depth == 0) {
            return staticScore(board);
        } else if (board.winner() != null) {
            return sense * WINNING_VALUE;
        }
        int bestSoFar = -1 * sense * INFTY;
        Piece p = (sense == 1) ? WHITE : BLACK;
        for (Move m : board.legalMoves(p)) {
            board.makeMove(m);
            int next = staticScore(board);
            int response = findMove(board, depth - 1, false,
                    -1 * sense, alpha, beta);
            if (sense == 1) {
                if (response >= bestSoFar) {
                    bestSoFar = next;
                    alpha = max(alpha, response);
                    if (beta <= alpha) {
                        break;
                    } else if (saveMove) {
                        _lastFoundMove = m;
                    }
                }
            } else {
                if (response <= bestSoFar) {
                    bestSoFar = next;
                    beta = min(beta, response);
                    if (beta <= alpha) {
                        break;
                    } else if (saveMove) {
                        _lastFoundMove = m;
                    }
                }
            }
            board.undo();
        }
        return bestSoFar;
    }


    /** Return a heuristically determined maximum search depth
     *  based on characteristics of BOARD. */
    private static int maxDepth(Board board) {
        return 1;
    }

    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        if (board.winner() != null) {
            if (board.winner() == myPiece().side()) {
                return WINNING_VALUE;
            } else {
                return -WINNING_VALUE;
            }
        }
        return board.countDifference();
    }
}
