package enigma;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Grace Lei
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;

        _position = 0;
        _ringSetting = 0;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _position;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        _position = posn;

    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        _position = alphabet().toInt(cposn);

    }

    /** Set ringSetting.
     * @param cposn is the ringsetting of the rotor. */
    void setRingSetting(char cposn) {
        _ringSetting = _permutation.alphabet().toInt(cposn);
    }

    /** Set ringSetting.
     * @param posn is the ringsetting of the rotor. */
    void setRingSetting(int posn) {

        _ringSetting = posn;
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        int contactEntered = permutation().wrap(p + _position - _ringSetting);
        int contactExited = permutation().permute(contactEntered);
        return permutation().wrap(contactExited - _position + _ringSetting);

    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int contactEntered = permutation().wrap(e + _position - _ringSetting);
        int contactExited = permutation().invert(contactEntered);
        return permutation().wrap(contactExited - _position + _ringSetting);

    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    /** My setting. */
    private int _position;

    /** My ring setting. */
    private int _ringSetting;

}
