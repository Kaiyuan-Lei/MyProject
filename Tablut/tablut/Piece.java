package tablut;

/** The contents of a cell on the board.
 *  @author P. N. Hilfinger
 */
enum Piece {

    /* EMPTY: empty square. WHITE, BLACK, and KING: pieces. */
    EMPTY("-", null), WHITE("W", "White"), BLACK("B", "Black"),
    KING("K", "King");

    /** A Piece whose board symbol is SYMBOL and that is called NAME in
     *  messages. */
    Piece(String symbol, String name) {
        _symbol = symbol;
        _name = name;
    }

    @Override
    public String toString() {
        return _symbol;
    }

    /** Return the class of PIECE.  This is either BLACK, WHITE, or
     *  EMPTY.  Usually the identify, except for the King, which is a DEFENGER.
     */
    Piece side() {
        return this == KING ? WHITE : this;
    }

    /** Return the Piece of opposing color, or null if this is not a
     *  player piece. */
    Piece opponent() {
        switch (this) {
        case WHITE: case KING:
            return BLACK;
        case BLACK:
            return WHITE;
        default:
            return null;
        }
    }

    /** Return my printed form for use in messages. */
    String toName() {
        return _name;
    }

    /** The symbol used for the piece in textual board printouts. */
    private final String _symbol;
    /** The name in used in messages. */
    private final String _name;

}
