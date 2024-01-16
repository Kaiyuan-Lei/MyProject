package tablut;

import static tablut.Move.mv;


/** A Player that takes input as text commands from its Controller.
 *  @author Grace Lei
 */
class TextPlayer extends Player {

    /** A new TextPlayer with no piece or controller (intended to produce
     *  a template). */
    TextPlayer() {
        this(null, null);
    }

    /** A new TextPlayer playing PIECE under control of CONTROLLER. */
    private TextPlayer(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new TextPlayer(piece, controller);
    }

    @Override
    boolean isManual() {
        return true;
    }

    @Override
    String myMove() {
        while (true) {
            String line = _controller.readLine(true);
            if (line == null) {
                return "quit";
            } else if (Move.isGrammaticalMove(line)) {
                if (board().winner() != null || board().turn() != myPiece()) {
                    _controller.reportError("misplaced move");
                    continue;
                } else {
                    Move move = mv(line);
                    if (move == null || !board().isLegal(move)) {
                        _controller.reportError("Invalid move. "
                                                + "Please try again.");
                        continue;
                    }
                }
            }
            return line;
        }
    }
}
