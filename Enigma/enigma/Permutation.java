package enigma;

import java.util.HashMap;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Grace Lei
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;

        _permuteMap = new HashMap<>();
        _invertMap = new HashMap<>();
        int start = 0;
        for (int i = 0; i < cycles.length(); i++) {
            char s = cycles.charAt(i);
            if (s == "(".charAt(0)) {
                start = i + 1;
            } else if (s == ")".charAt(0)) {
                addCycle(cycles.substring(start, i));
            }
        }
        for (int i = 0; i < alphabet.size(); i++) {
            char self = alphabet.getChars().charAt(i);
            if (!_permuteMap.containsKey(self)) {
                _dearrangement = true;
                _permuteMap.put(self, self);
                _invertMap.put(self, self);
            }
        }

    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {

        for (int i = 0; i < cycle.length() - 1; i++) {
            _permuteMap.put(cycle.charAt(i), cycle.charAt(i + 1));
        }
        _permuteMap.put(cycle.charAt(cycle.length() - 1), cycle.charAt(0));
        for (int i = 1; i < cycle.length(); i++) {
            _invertMap.put(cycle.charAt(i), cycle.charAt(i - 1));
        }
        _invertMap.put(cycle.charAt(0), cycle.charAt(cycle.length() - 1));
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return alphabet().size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        return alphabet().toInt(permute(alphabet().toChar(p)));

    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        return alphabet().toInt(invert(alphabet().toChar(c)));

    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return  _permuteMap.get(p);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        return _invertMap.get(c);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        return _dearrangement;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** The mapping of permutation. */
    private HashMap<Character, Character> _permuteMap;

    /** The mapping of inversion. */
    private HashMap<Character, Character> _invertMap;

    /** Indicates whether there is value not mapping to itself. */
    private boolean _dearrangement;



}
