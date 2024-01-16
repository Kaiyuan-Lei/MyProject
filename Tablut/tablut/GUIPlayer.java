package tablut;

/** A Player that takes input from a GUI.
 *  @author P. N. Hilfinger
 */
class GUIPlayer extends Player implements Reporter {

    /** A new GUIPlayer that takes moves and commands from GUI.  */
    GUIPlayer(GUI gui) {
        this(null, null, gui);
    }

    /** A new GUIPlayer playing PIECE under control of CONTROLLER, taking
     *  moves and commands from GUI. */
    GUIPlayer(Piece piece, Controller controller, GUI gui) {
        super(piece, controller);
        _gui = gui;
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new GUIPlayer(piece, controller, _gui);
    }

    @Override
    boolean isManual() {
        return true;
    }

    @Override
    String myMove() {
        while (true) {
            String command;
            command = _controller.readLine(false);
            if (command == null) {
                command = _gui.readCommand();
            }
            Move move = Move.mv(command);
            if (move == null || board().isLegal(move)) {
                return command;
            }
        }
    }

    @Override
    public void reportError(String fmt, Object... args) {
        _gui.reportError(fmt, args);
    }

    @Override
    public void reportNote(String fmt, Object... args) {
        _gui.reportNote(fmt, args);
    }

    @Override
    public void reportMove(Move unused) {
    }

    /** The GUI I use for input. */
    private GUI _gui;
}
