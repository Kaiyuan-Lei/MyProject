package enigma;

import java.util.Collection;

import java.util.ArrayList;

/** Class that represents a complete enigma machine.
 *  @author Grace Lei
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _myRotors.clear();
        for (String rotorName : rotors) {
            for (Rotor rotor : _allRotors) {
                if (rotor.name().toUpperCase().
                        equals(rotorName.toUpperCase())) {
                    _myRotors.add(rotor);
                    break;
                }
            }
        }

    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int i = 1; i < _myRotors.size(); i++) {
            _myRotors.get(i).set(setting.charAt(i - 1));
        }
    }

    /** Set my rotors' ring setting according to RS. */
    void ringSetRotors(String rs) {
        for (int i = 1; i < _myRotors.size(); i++) {
            _myRotors.get(i).setRingSetting(rs.charAt(i - 1));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advance();
        c = _plugboard.permute(c);
        for (int i = _myRotors.size() - 1; i >= 0; i--) {
            c = _myRotors.get(i).convertForward(c);
        }
        for (int i = 1; i < _myRotors.size(); i++) {
            c = _myRotors.get(i).convertBackward(c);
        }
        return _plugboard.permute(c);
    }

    /** Helper method which advances the machine first. */
    private void advance() {
        boolean aN = false;
        for (int i = _myRotors.size() - 1; i >= 0; i--) {
            Rotor r = _myRotors.get(i);
            if (r instanceof MovingRotor
                    && (i == _myRotors.size() - 1 || aN)) {
                aN = r.atNotch();
                r.advance();
            } else if (r.atNotch() && _myRotors.get(i - 1).rotates()) {
                aN = r.atNotch();
                r.advance();
            }
        }
    }


    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        StringBuilder sb = new StringBuilder();
        msg = msg.replaceAll(" ", "");

        for (int i = 0; i < msg.length(); i++) {
            int in = _alphabet.toInt(msg.charAt(i));
            int convertedIn  = convert(in);
            sb.append(_alphabet.toChar(convertedIn));

        }

        return sb.toString();
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;


    /** Number of rotors. */
    private int _numRotors;

    /** Number of moving rotors. */
    private int _pawls;

    /** All available rotor list. */
    private Collection<Rotor> _allRotors;

    /** Currently using rotors. */
    private ArrayList<Rotor> _myRotors = new ArrayList<>();

    /** Currently using plugboards. */
    private Permutation _plugboard;
}
