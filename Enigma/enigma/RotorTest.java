package enigma;

import org.junit.Test;
import static org.junit.Assert.*;

public class RotorTest {

    @Test
    public void testMovingRotor() {
        Permutation p = new Permutation("(AB) (CD) (EF) (GH) (IJ) (KL)"
                + " (MN) (OP) (QR) (ST) (UV) (WX) (YZ)", new Alphabet());
        MovingRotor rotor = new MovingRotor("moving R", p, "IS");
        assertEquals("moving R", rotor.name());
        assertEquals(p.alphabet(), rotor.alphabet());
        assertEquals(p, rotor.permutation());
        assertTrue(rotor.rotates());
        assertFalse(rotor.reflecting());
        assertFalse(rotor.atNotch());
        assertEquals(26, rotor.size());
        rotor.set(2);
        assertEquals(2, rotor.setting());
        rotor.set('A');
        assertEquals(0, rotor.setting());
        assertEquals(1, rotor.convertForward(0));
        assertEquals(0, rotor.convertBackward(1));
        rotor.advance();
        assertEquals(1, rotor.setting());
    }

    @Test
    public void testFixedRotor() {
        Permutation p = new Permutation("(AB) (CD) (EF) (GH) (IJ) (KL)"
                + " (MN) (OP) (QR) (ST) (UV) (WX) (YZ)", new Alphabet());
        FixedRotor rotor = new FixedRotor("fixed R", p);
        assertEquals("fixed R", rotor.name());
        assertEquals(p.alphabet(), rotor.alphabet());
        assertEquals(p, rotor.permutation());
        assertFalse(rotor.rotates());
        assertFalse(rotor.reflecting());
        assertFalse(rotor.atNotch());
        assertEquals(26, rotor.size());
        rotor.set(2);
        assertEquals(2, rotor.setting());
        rotor.set('A');
        assertEquals(0, rotor.setting());
        assertEquals(1, rotor.convertForward(0));
        assertEquals(0, rotor.convertBackward(1));
        rotor.advance();
        assertEquals(0, rotor.setting());
    }

    @Test
    public void testReflector() {
        Permutation p = new Permutation("(AB) (CD) (EF) (GH) (IJ) (KL)"
                + " (MN) (OP) (QR) (ST) (UV) (WX) (YZ)", new Alphabet());
        Reflector rotor = new Reflector("reflector R", p);
        assertEquals("reflector R", rotor.name());
        assertEquals(p.alphabet(), rotor.alphabet());
        assertEquals(p, rotor.permutation());
        assertFalse(rotor.rotates());
        assertTrue(rotor.reflecting());
        assertFalse(rotor.atNotch());
        assertEquals(26, rotor.size());
        assertEquals(0, rotor.setting());
        assertEquals(1, rotor.convertForward(0));
        assertEquals(0, rotor.convertBackward(1));
        rotor.advance();
        assertEquals(0, rotor.setting());
    }
}
