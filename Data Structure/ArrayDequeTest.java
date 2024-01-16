import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {

    @Test
    public void addRemoveIsEmptySizeGetTest() {
        System.out.println("Running add/isEmpty/Size test.");

        ArrayDeque<String> lld1 = new ArrayDeque<>();

        assertTrue(lld1.isEmpty());

        lld1.addFirst("A");
        lld1.addFirst("U");
        lld1.addFirst("Y");
        lld1.addFirst("K");
        lld1.addLast("N");
        assertEquals(5, lld1.size());
        lld1.removeFirst();
        lld1.addLast("❤");
        lld1.addLast("B");
        lld1.addLast("O");
        lld1.addLast("!");
        lld1.removeLast();

        assertEquals(7, lld1.size());
        assertEquals("U", lld1.get(1));
        assertEquals("❤", lld1.get(4));

        System.out.println("Printing out deque: ");
        lld1.printDeque();
    }

    @Test
    public void resizeTest() {
        System.out.println("Running resize test.");

        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        lld1.addLast(1);
        lld1.addLast(2);
        lld1.addLast(3);
        lld1.addLast(4);
        lld1.addLast(5);
        lld1.addLast(6);
        lld1.addLast(7);
        lld1.addLast(8);
        assertEquals(8, lld1.size());
        lld1.addLast(9);
        lld1.addLast(10);
        lld1.addLast(11);
        assertEquals(11, lld1.size());
        lld1.removeLast();
        lld1.removeLast();
        lld1.removeLast();
        lld1.removeLast();
        lld1.removeLast();
        lld1.removeLast();
        lld1.removeLast();
        lld1.removeLast();
        lld1.removeLast();
        assertEquals(2, lld1.size());


    }
    @Test
    public void test1() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        lld1.addLast(0);
        lld1.addFirst(1);
        lld1.removeLast();
        lld1.addFirst(3);
        lld1.addLast(4);
        lld1.removeLast();
        lld1.addLast(6);
        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.addFirst(10);
        lld1.removeLast();
        lld1.addFirst(12);
        lld1.removeFirst();
        lld1.addLast(14);
        lld1.addLast(15);
        lld1.addLast(16);

        assertEquals(15, (int) lld1.get(1));

    }

}
