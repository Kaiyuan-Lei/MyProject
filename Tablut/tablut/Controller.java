package tablut;

import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.function.Consumer;

import static tablut.Utils.*;
import static tablut.Square.*;
import static tablut.Piece.*;

/** The input/output and GUI controller for play of Tablut.
 *  @author Grace Lei
 *  */

final class Controller {

    /** Controller for one or more games of Tablut, using
     *  MANUALPLAYERTEMPLATE as an exemplar for manual players
     *  (see the Player.create method) and AUTOPLAYERTEMPLATE
     *  as an exemplar for automated players.  Reports
     *  board changes to VIEW at appropriate points.  Uses REPORTER
     *  to report moves, wins, and errors to user. If LOGFILE is
     *  non-null, copies all commands to it. If STRICT, exits the
     *  program with non-zero code on receiving an erroneous move from a
     *  player. */
    Controller(View view, PrintStream logFile, Reporter reporter,
               Player manualPlayerTemplate, Player autoPlayerTemplate,
               boolean strict) {
        _view = view;
        _playing = false;
        _logFile = logFile;
        _input = new Scanner(System.in);
        _autoPlayerTemplate = autoPlayerTemplate;
        _manualPlayerTemplate = manualPlayerTemplate;
        _nonPlayer = manualPlayerTemplate.create(EMPTY, this);
        _white = _autoPlayerTemplate.create(WHITE, this);
        _black = _manualPlayerTemplate.create(BLACK, this);
        _reporter = reporter;
        _strict = strict;
    }

    /** Play Tablut. */
    void play() {
        _playing = true;
        _winner = null;
        _board.init();
        while (_playing) {
            _view.update(this);
            String command;
            if (_winner == null) {
                if (_board.turn() == WHITE) {
                    command = _white.myMove();
                } else {
                    command = _black.myMove();
                }
            } else {
                command = _nonPlayer.myMove();
                if (command == null) {
                    command = "quit";
                }
            }
            try {
                executeCommand(command);
            } catch (IllegalArgumentException excp) {
                reportError("Error: %s%n", excp.getMessage());
                if (_strict) {
                    System.exit(1);
                }
            }
        }
        if (_logFile != null) {
            _logFile.close();
        }
    }

    /** Return the current board.  The value returned should not be
     *  modified. */
    Board board() {
        return _board;
    }

    /** Return a random integer in the range 0 inclusive to U, exclusive.
     *  Available for use by AIs that use random selections in some cases.
     *  Once setRandomSeed is called with a particular value, this method
     *  will always return the same sequence of values. */
    int randInt(int U) {
        return _randGen.nextInt(U);
    }

    /** Re-seed the pseudo-random number generator (PRNG) that supplies randInt
     *  with the value SEED. Identical seeds produce identical sequences.
     *  Initially, the PRNG is randomly seeded. */
    void setSeed(long seed) {
        _randGen.setSeed(seed);
    }

    /** Return the next line of input, or null if there is no more. First
     *  prompts for the line.  Trims the returned line (if any) of all
     *  leading and trailing whitespace. First issues a prompt iff PROMPT. */
    String readLine(boolean prompt) {
        if (prompt) {
            System.out.print("> ");
            System.out.flush();
        }
        if (_input.hasNextLine()) {
            return _input.nextLine().trim();
        } else {
            return null;
        }
    }

    /** Return true iff white is a manual player. */
    boolean manualWhite() {
        return _white.isManual();
    }

    /** Return true iff black is a manual player. */
    boolean manualBlack() {
        return _black.isManual();
    }

    /** Report error by calling reportError(FORMAT, ARGS) on my reporter. */
    void reportError(String format, Object... args) {
        _reporter.reportError(format, args);
    }

    /** Report note by calling reportNote(FORMAT, ARGS) on my reporter. */
    void reportNote(String format, Object... args) {
        _reporter.reportNote(format, args);
    }

    /** Report move by calling reportMove(MOVE) on my reporter. */
    void reportMove(Move move) {
        _reporter.reportMove(move);
    }

    /** Print a comment in the log.  The arguments FORMAT and ARGS are
     *  as for String.format. */
    void logComment(String format, Object... args) {
        if (_logFile != null) {
            _logFile.printf("# " + format + "%n", args);
            _logFile.flush();
        }
    }

    /** A Command is pair (<pattern>, <processor>), where <pattern> is a
     *  Matcher that matches instances of a particular command, and
     *  <processor> is a functional object whose .accept method takes a
     *  successfully matched Matcher and performs some operation. */
    private static class Command {
        /** A new Command that matches PATN (a regular expression) and uses
         *  PROCESSOR to process commands that match the pattern. */
        Command(String patn, Consumer<Matcher> processor) {
            _matcher = Pattern.compile(patn).matcher("");
            _processor = processor;
        }

        /** A Matcher matching my pattern. */
        protected final Matcher _matcher;
        /** The function object that implements my command. */
        protected final Consumer<Matcher> _processor;
    }

    /** A list of Commands describing the valid textual commands to the
     *  Tablut program and the methods to process them. */
    private Command[] _commands = {
        new Command("quit$", this::doQuit),
        new Command("new$", this::doNew),
        new Command("seed\\s+(\\d+)$", this::doSeed),
        new Command("dump$", this::doDump),
        new Command("undo$", this::doUndo),
        new Command("manual\\s+(white|black)$", this::doManual),
        new Command("auto\\s+(white|black)$", this::doAuto),
        new Command("limit\\s+(\\d+)$", this::doLimit),
        new Command("toggle\\s+" + SQ + "$", this::doToggle),
        new Command(Move.MOVE_PATTERN.pattern(), this::doMove)
    };

    /** A Matcher whose Pattern matches comments. */
    private static final Matcher COMMENT = Pattern.compile("#.*").matcher("");

    /** Check that CMND is one of the valid Tablut commands and execute it, if
     *  so, raising an IllegalArgumentException otherwise. */
    private void executeCommand(String cmnd) {
        if (_logFile != null) {
            _logFile.println(cmnd);
            _logFile.flush();
        }

        COMMENT.reset(cmnd);
        cmnd = COMMENT.replaceFirst("").trim().toLowerCase();

        if (cmnd.isEmpty()) {
            return;
        }
        for (Command parser : _commands) {
            parser._matcher.reset(cmnd);
            if (parser._matcher.matches()) {
                parser._processor.accept(parser._matcher);
                return;
            }
        }
        throw error("Bad command: %s", cmnd);
    }

    /** Command "new". */
    private void doNew(Matcher unused) {
        _board.init();
        _winner = null;
    }

    /** Command "manual <color>", where <color> is COLOR.group(1). */
    private void doManual(Matcher color) {
        switch (color.group(1)) {
        case "black":
            _black = _manualPlayerTemplate.create(BLACK, this);
            break;
        case "white":
            _white = _manualPlayerTemplate.create(WHITE, this);
            break;
        default:
            assert false;
        }
        _view.update(this);
    }

    /** Command "auto <color>", where <color> is COLOR.group(1). */
    private void doAuto(Matcher color) {
        switch (color.group(1)) {
        case "black":
            _black = _autoPlayerTemplate.create(BLACK, this);
            break;
        case "white":
            _white = _autoPlayerTemplate.create(WHITE, this);
            break;
        default:
            assert false;
        }
        _view.update(this);
    }

    /** Command "toggle". The first group of MAT is a square designation.
     *  The contents of the square are modified:
     *  EMPTY -> WHITE -> KING -> BLACK .< ENPTY". Clear Undo information. */
    private void doToggle(Matcher mat) {
        Square sq = sq(mat.group(1));
        Piece piece;
        switch (_board.get(sq)) {
        case EMPTY: piece = WHITE; break;
        case WHITE: piece = KING; break;
        case KING: piece = BLACK; break;
        default:
            piece = EMPTY; break;
        }
        _board.put(piece, sq);
        _board.clearUndo();
    }

    /** Command "quit". */
    private void doQuit(Matcher unused) {
        _playing = false;
    }

    /** Command "seed N" where N is the first group of MAT. */
    private void doSeed(Matcher mat) {
        try {
            setSeed(Long.parseLong(mat.group(1)));
        } catch (NumberFormatException excp) {
            throw error("number too large");
        }
    }

    /** Command "limit N" where N is the first captured group of MAT. */
    private void doLimit(Matcher mat) {
        try {
            _board.setMoveLimit(Integer.parseInt(mat.group(1)));
        } catch (NumberFormatException excp) {
            throw error("number too large");
        }
    }

    /** Execute a move command matched in MAT. */
    private void doMove(Matcher mat) {
        _board.makeMove(Move.mv(mat.group(0)));
        if (_winner == null) {
            _winner = _board.winner();
            if (_winner != null) {
                _view.update(this);
                _reporter.reportNote("%s wins.", _winner.toName());
            }
        }
    }

    /** Dump the contents of the board on standard output. */
    private void doDump(Matcher unused) {
        System.out.printf("===%n%s===%n", _board);
    }

    /** Undo back to before my last move, if there was one (otherwise does
     *  nothing). */
    private void doUndo(Matcher unused) {
        if (_board.moveCount() > 1) {
            _board.undo();
            _board.undo();
            _winner = null;
            _view.update(this);
        }
    }

    /** The board. */
    private Board _board = new Board();

    /** The winning side of the current game. */
    private Piece _winner;

    /** True while game is still active. */
    private boolean _playing;

    /** The object that is displaying the current game. */
    private View _view;

    /** My pseudo-random number generator. */
    private Random _randGen = new Random();

    /** Log file, or null if absent. */
    private PrintStream _logFile;

    /** Input source. */
    private Scanner _input;

    /** The current White and Black players, each created from
     *  _autoPlayerTemplate or _manualPlayerTemplate. */
    private Player _white, _black;

    /** A dummy Player used to return commands but not moves when no
     *  game is in progress. */
    private Player _nonPlayer;

    /** The current templates for manual and automated players. */
    private Player _autoPlayerTemplate, _manualPlayerTemplate;

    /** Reporter for messages and errors. */
    private Reporter _reporter;

    /** If true, command errors cause termination with error exit
     *  code. */
    private boolean _strict;

}
