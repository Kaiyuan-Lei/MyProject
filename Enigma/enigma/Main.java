package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.HashSet;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Grace Lei
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or"
                    + " 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine m = readConfig();
        boolean hasConfig = false;
        while (_input.hasNextLine()) {
            String s = _input.nextLine();
            if (!hasConfig && !s.startsWith("*")) {
                throw error("No configuration");
            }
            if (s.startsWith("*")) {
                setUp(m, s);
                hasConfig = true;
            } else {
                String res = m.convert(s);
                printMessageLine(res);
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = new Alphabet(_config.nextLine().trim());
            int numRotors = _config.nextInt();
            int movingRotors = _config.nextInt();
            ArrayList<Rotor> rotors = new ArrayList<>();
            while (_config.hasNext()) {
                rotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, movingRotors, rotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String rotorName = _config.next();
            String rotorType = _config.next();
            StringBuilder s = new StringBuilder();
            while (_config.hasNext("\\s*[(].*[)]")) {
                s.append(_config.next());
            }
            Permutation perm = new Permutation(s.toString(), _alphabet);
            _allRotorNames.add(rotorName);
            if (rotorType.startsWith("M")) {
                _allMovingRotorNames.add(rotorName);
                return new MovingRotor(rotorName,
                        perm, rotorType.substring(1));
            } else if (rotorType.startsWith("N")) {
                return new FixedRotor(rotorName, perm);
            } else {
                _reflectors.add(rotorName);
                return new Reflector(rotorName, perm);
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        Scanner s = new Scanner(settings);
        s.next();
        String[] myRotors = new String[M.numRotors()];
        ArrayList<String> addedRotors = new ArrayList<>();
        ArrayList<String> addedMovingRotors = new ArrayList<>();
        for (int i = 0; i < M.numRotors(); i++) {
            String r = s.next();
            if (i == 0 && !_reflectors.contains(r)) {
                throw error("Reflector in wrong place");
            }

            if (!_allRotorNames.contains(r)) {
                throw error("Bad rotor name");
            }
            if (addedRotors.contains(r)) {
                throw error("Duplicate rotor name");
            }
            if (_allMovingRotorNames.contains(r)) {
                addedMovingRotors.add(r);
            }
            addedRotors.add(r);
            myRotors[i] = r;
        }
        if (addedRotors.size() != M.numRotors()
                || addedMovingRotors.size() != M.numPawls()) {
            throw error("Wrong number of arguments");
        }

        String setting = s.next();
        if (setting.length() < M.numRotors() - 1) {
            throw error("Wheel settings too short");
        }
        if (setting.length() > M.numRotors() - 1) {
            throw error("Wheel settings too long");
        }
        for (int i = 0; i < setting.length(); i++) {
            if (!_alphabet.getChars().
                    contains(String.valueOf(setting.charAt(i)))) {
                throw error("Bad character in wheel settings");
            }
        }

        String ringSetting = null;
        if ((s.hasNext()) && !(s.hasNext("[(].*[)]"))) {
            ringSetting = s.next();
        }

        StringBuilder sb = new StringBuilder();
        while (s.hasNext()) {
            sb.append(s.next());
        }
        M.insertRotors(myRotors);
        M.setRotors(setting);
        if (ringSetting != null) {
            M.ringSetRotors(ringSetting);
        }
        M.setPlugboard(new Permutation(sb.toString(), _alphabet));
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < msg.length(); i++) {
            if (i >= 5 && (i - 5) % 5 == 0) {
                sb.append(" ");
            }
            sb.append(msg.charAt(i));
        }
        _output.print(sb + "\n" + "");
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** All available reflector list from configuration. */
    private HashSet<String> _reflectors = new HashSet<>();

    /** All available rotor list from configuration. */
    private HashSet<String> _allRotorNames = new HashSet<>();

    /** All available moving rotor list from configuration. */
    private HashSet<String> _allMovingRotorNames = new HashSet<>();
}
