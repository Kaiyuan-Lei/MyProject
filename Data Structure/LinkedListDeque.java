public class LinkedListDeque<T> implements Deque<T> {
    private class IntNode {
        private T item;
        private IntNode prev;
        private IntNode next;

        public IntNode(T item, IntNode prev, IntNode next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    private IntNode sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new IntNode(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;

    }

    @Override
    public void addFirst(T item) {
        IntNode newNode = new IntNode(item, sentinel, sentinel.next);
        sentinel.next.prev = newNode;
        sentinel.next = newNode;
        size++;
    }

    @Override
    public void addLast(T item) {
        IntNode newNode = new IntNode(item, sentinel.prev, sentinel);
        sentinel.prev.next = newNode;
        sentinel.prev = newNode;
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        IntNode ptr = sentinel.next;
        while (ptr != sentinel) {
            System.out.print(ptr.item + " ");
            ptr = ptr.next;
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        } else {
            T firstItem = sentinel.next.item;
            IntNode newFirst = sentinel.next.next;
            newFirst.prev = sentinel;
            sentinel.next = newFirst;
            size--;
            return firstItem;
        }
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        } else {
            T lastItem = sentinel.prev.item;
            IntNode newLast = sentinel.prev.prev;
            newLast.next = sentinel;
            sentinel.prev = newLast;
            size--;
            return lastItem;
        }
    }

    @Override
    public T get(int index) {
        if (size == 0 || index > size) {
            return null;
        }
        IntNode ptr = sentinel.next;
        while (index > 0) {
            ptr = ptr.next;
            index--;
        }
        return ptr.item;
    }

    public T getRecursive(int index) {
        if (size == 0) {
            return null;
        }
        return getRecursiveHelper(sentinel.next, index);
    }

    private T getRecursiveHelper(IntNode L, int index) {
        if (index == 0) {
            return L.item;
        }
        return getRecursiveHelper(L.next, index - 1);
    }


}
