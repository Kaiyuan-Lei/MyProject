package tablut;

import java.util.List;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static tablut.Utils.error;

import ucb.util.CommandArgs;

/** The main class for the Tablut game.
 *  @author P. N. Hilfinger
 */
public class Main {

    /** The main program.  ARGS may contain the option --display. */
    public static void main(String... args) {

        CommandArgs options =
            new CommandArgs("--display --testing --strict --log={0,1} --={0,2}",
                            args);
        if (!options.ok()) {
            System.err.println("Usage: java tablut.Main [--display]"
                               + " [--log=FILE] [--strict] [INPUT [OUTPUT]]");
            System.exit(1);
        }

        List<String> files = options.get("--");
        if (!files.isEmpty()) {
            try {
                System.setIn(new FileInputStream(files.get(0)));
                if (files.size() > 1) {
                    FileOutputStream out = new FileOutputStream(files.get(1));
                    System.setOut(new PrintStream(out, true));
                }
            } catch (IOException excp) {
                System.err.printf("Could not open file: %s%n",
                                  excp.getMessage());
                System.exit(1);
            }
        } else if (options.contains("--display")) {
            try {
                System.in.close();
            } catch (IOException dummy) {
                /* Ignore IOException. */
            }
        }

        Controller control = getController(options);
        System.out.println("Tablut 61B, staff version 1.0");

        try {
            control.play();
            if (!options.contains("--display")) {
                System.exit(0);
            }
        } catch (IllegalStateException excp) {
            System.err.printf("Internal error: %s%n", excp.getMessage());
            System.exit(1);
        }

    }

    /** Return an appropriate Controller as indicated by OPTIONS. */
    private static Controller getController(CommandArgs options) {
        Player manualPlayer;
        GUI gui;
        PrintStream log;
        View view;
        Reporter reporter;

        if (options.contains("--display")) {
            gui = new GUI("Tablut 61B");
            reporter = gui;
            gui.display(true);
            view = gui;
        } else {
            gui = null;
            reporter = new TextReporter();
            view = new NullView();
        }
        if (!options.contains("--testing") && options.contains("--display")) {
            manualPlayer = new GUIPlayer(gui);
        } else {
            manualPlayer = new TextPlayer();
        }

        log = null;
        if (options.contains("--log")) {
            try {
                log = new PrintStream(options.getFirst("--log"));
            } catch (IOException excp) {
                throw error("Could not open log file");
            }
        }

        return new Controller(view, log, reporter, manualPlayer,
                              new AI(), options.contains("--strict"));
    }
}
