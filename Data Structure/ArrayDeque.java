public class ArrayDeque<T> implements Deque<T> {
    private T[] items;
    private int nextFirst;
    private int nextLast;
    private int size;

    public ArrayDeque() {
        this.items = (T[]) new Object[8];
        this.nextFirst = 4;
        this.nextLast = 5;
        this.size = 0;
    }

    private int minusOne(int index) {
        int i = index - 1;
        if (i < 0) {
            i = items.length - 1;
        }
        return i;
    }

    private int plusOne(int index) {
        int i = index + 1;
        if (i == items.length) {
            i = 0;
        }
        return i;
    }

    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        int begin = plusOne(nextFirst);
        int end = minusOne(nextLast);
        if (begin < end) {
            System.arraycopy(items, begin, a, 0, size);
        } else {
            int sizeOfFirstHalf = items.length - begin;
            int sizeOfSecondHalf = size - sizeOfFirstHalf;
            System.arraycopy(items, begin, a, 0, sizeOfFirstHalf);
            System.arraycopy(items, 0, a, sizeOfFirstHalf, sizeOfSecondHalf);
        }
        nextFirst = a.length - 1;
        nextLast = size;
        items = a;
    }

    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextFirst] = item;
        nextFirst = minusOne(nextFirst);
        size++;
    }

    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextLast] = item;
        nextLast = plusOne(nextLast);
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int i = 0, curr = plusOne(nextFirst); i < size; i++) {
            System.out.print(items[curr] + " ");
            curr = plusOne(curr);
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        double usageRatio = size / (double) items.length;
        if (items.length >= 16 && usageRatio < 0.25) {
            resize(items.length / 2);
        }
        int firstIndex = plusOne(nextFirst);
        T firstItem = items[firstIndex];
        items[firstIndex] = null;
        nextFirst = firstIndex;
        size--;
        return firstItem;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        double usageRatio = size / (double) items.length;
        if (items.length >= 16 && usageRatio < 0.25) {
            resize(items.length / 2);
        }
        int lastIndex = minusOne(nextLast);
        T lastItem = items[lastIndex];
        items[lastIndex] = null;
        nextLast = lastIndex;
        size--;
        return lastItem;
    }

    @Override
    public T get(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        int curr = plusOne(nextFirst);
        for (int i = 0; i < index; i++) {
            curr = plusOne(curr);
        }
        return items[curr];
    }


}
