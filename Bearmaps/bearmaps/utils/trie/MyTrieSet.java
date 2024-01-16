package bearmaps.utils.trie;

import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;

public class MyTrieSet implements TrieSet61BL {


    private Node root;

    public MyTrieSet() {
        root = new Node(false);
    }

    private static class Node {
        private boolean isKey;
        private HashMap<Character, Node> next;
        private Node(boolean b) {
            isKey = b;
            next = new HashMap<>();
        }
    }

    @Override
    public void clear() {
        root = new Node(false);
    }

    private Node find(String key) {
        Node curr = root;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (!curr.next.containsKey(c)) {
                return null;
            }
            curr = curr.next.get(c);
        }
        return curr;
    }

    @Override
    public boolean contains(String key) {
        Node n = find(key);
        return n != null && n.isKey;
    }


    @Override
    public void add(String key) {
        Node curr = root;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (!curr.next.containsKey(c)) {
                curr.next.put(c, new Node(false));
            }
            curr = curr.next.get(c);
        }
        curr.isKey = true;
    }


    @Override
    public List<String> keysWithPrefix(String prefix) {
        Node n = find(prefix);
        List<String> res = new LinkedList<>();
        collect(n, prefix, res);
        return res;
    }

    private void collect(Node n, String s, List<String> x) {
        if (n == null) {
            return;
        }
        if (n.isKey) {
            x.add(s);
        }
        for (char c : n.next.keySet()) {
            collect(n.next.get(c), s + c, x);
        }
    }


    @Override
    public String longestPrefixOf(String key) {
        throw new UnsupportedOperationException();
    }
}

