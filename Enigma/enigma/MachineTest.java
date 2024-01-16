package enigma;
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.ArrayList;

public class MachineTest {

    @Test
    public void testMachine() {
        Alphabet alpha = new Alphabet();
        MovingRotor i = new MovingRotor("I",
                new Permutation("(AELTPHQXRU) (BKNW) (CMOY) "
                        + "(DFG) (IV) (JZ) (S)", alpha),
                "Q");
        MovingRotor ii = new MovingRotor("II",
                new Permutation("(FIXVYOMW) (CDKLHUP) (ESZ) (BJ) (GR) "
                        + "(NT) (A) (Q)", alpha),
                "E");
        MovingRotor iii = new MovingRotor("III",
                new Permutation("(ABDHPEJT) (CFLVMZOYQIRWUKXSG) (N)", alpha),
                "V");
        MovingRotor iv = new MovingRotor("IV",
                new Permutation("(AEPLIYWCOXMRFZBSTGJQNH) (DV) (KU)", alpha),
                "J");
        MovingRotor v = new MovingRotor("V",
                new Permutation("(AVOLDRWFIUQ)(BZKSMNHYC) (EGTJPX)", alpha),
                "Z");
        FixedRotor beta = new FixedRotor("Beta",
                new Permutation("(ALBEVFCYODJWUGNMQTZSKPR) (HIX)", alpha));
        FixedRotor gamma = new FixedRotor("Gamma",
                new Permutation("(AFNIRLBSQWVXGUZDKMTPCOYJHE)", alpha));
        Reflector b = new Reflector("B",
                new Permutation("(AE) (BN) (CK) (DQ) (FU) "
                        + "(GY) (HW) (IJ) (LO) (MP) (RX) (SZ) (TV)", alpha));
        Reflector c = new Reflector("C",
                new Permutation("(AR) (BD) (CO) (EJ) (FN) "
                        + "(GT) (HK) (IV) (LM) (PW) (QZ) (SX) (UY)", alpha));

        ArrayList<Rotor> allRotors = new ArrayList<>();
        allRotors.add(i);
        allRotors.add(ii);
        allRotors.add(iii);
        allRotors.add(iv);
        allRotors.add(v);
        allRotors.add(beta);
        allRotors.add(gamma);
        allRotors.add(b);
        allRotors.add(c);

        Machine m = new Machine(alpha, 5, 3, allRotors);
        m.insertRotors(new String[] {"B", "BETA", "III", "IV", "I"});
        m.setRotors("AXLE");
        m.setPlugboard(new Permutation("(HQ) (EX) (IP) (TR) (BY)", alpha));

        assertEquals(5, m.numRotors());
        assertEquals(3, m.numPawls());
        assertEquals("QVPQSOKOILPUBKJZPISFXDW",
                m.convert("FROM HIS SHOULDER HIAWATHA"));
    }

}
