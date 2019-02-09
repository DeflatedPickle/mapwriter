package mapwriter;

import java.util.*;

public class CircularHashMap<K, V> {
    /*
     * A hash map where each node is linked to the previous and next nodes in the order of
     * insertion. The 'head' node is the most recently added node. Its next pointer links to
     * the first node added, forming a circle. The getNextEntry and getPrevEntry methods use an
     * internal pointer to the 'current' node, and return either the current nodes 'next' or
     * 'prev' node respectively. The current node becomes the node that was returned, such that
     * repeated calls traverse all nodes in the map. Most methods are similar to those in the
     * java.util.Map interface. The CircularHashMap class does not implement Map however as
     * some of the required methods seemed unnecessary.
     */

    public class Node implements Map.Entry<K, V> {
        private final K key;
        private V value;
        private Node next;
        private Node prev;

        Node(K key, V value) {

            this.key = key;
            this.value = value;
            this.next = this;
            this.prev = this;
        }

        @Override
        public K getKey() {

            return this.key;
        }

        @Override
        public V getValue() {

            return this.value;
        }

        @Override
        public V setValue(V value) {

            final V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }

    private final Map<K, Node> nodeMap = new HashMap<>();
    private Node headNode = null;

    private Node currentNode = null;

    public void clear() {

        for (final Node node : this.nodeMap.values()) {
            node.next = null;
            node.prev = null;
        }
        this.nodeMap.clear();
        this.headNode = null;
        this.currentNode = null;
    }

    public boolean containsKey(Object key) {

        return this.nodeMap.containsKey(key);
    }

    public Collection<Map.Entry<K, V>> entrySet() {

        return new ArrayList<>(this.nodeMap.values());
    }

    public V get(Object key) {

        final Node node = this.nodeMap.get(key);
        return node != null ? node.value : null;
    }

    public Map.Entry<K, V> getNextEntry() {

        if (this.currentNode != null) {
            this.currentNode = this.currentNode.next;
        }
        return this.currentNode;
    }

    public Map.Entry<K, V> getPrevEntry() {

        if (this.currentNode != null) {
            this.currentNode = this.currentNode.prev;
        }
        return this.currentNode;
    }

    public boolean isEmpty() {

        return this.nodeMap.isEmpty();
    }

    public Set<K> keySet() {

        return this.nodeMap.keySet();
    }

    public V put(K key, V value) {

        Node node = this.nodeMap.get(key);
        if (node == null) {
            // add new node
            node = new Node(key, value);
            this.nodeMap.put(key, node);

            if (this.headNode == null) {
                node.next = node;
                node.prev = node;

            } else {
                node.next = this.headNode.next;
                node.prev = this.headNode;

                this.headNode.next.prev = node;
                this.headNode.next = node;
            }

            if (this.currentNode == null) {
                this.currentNode = node;
            }

            this.headNode = node;

        } else {
            // update node
            node.value = value;
        }
        return value;
    }

    public V remove(Object key) {

        final Node node = this.nodeMap.get(key);
        V value = null;
        if (node != null) {
            if (this.headNode == node) {
                this.headNode = node.next;
                if (this.headNode == node) {
                    this.headNode = null;
                }
            }
            if (this.currentNode == node) {
                this.currentNode = node.next;
                if (this.currentNode == node) {
                    this.currentNode = null;
                }
            }

            node.prev.next = node.next;
            node.next.prev = node.prev;
            node.next = null;
            node.prev = null;
            value = node.value;

            this.nodeMap.remove(key);
        }
        return value;
    }

    //
    // interface to traverse circular nodes
    //

    public void rewind() {

        this.currentNode = this.headNode != null ? this.headNode.next : null;
    }

    public boolean setPosition(K key) {

        final Node node = this.nodeMap.get(key);
        if (node != null) {
            this.currentNode = node;
        }
        return node != null;
    }

    public int size() {

        return this.nodeMap.size();
    }

    public Collection<V> values() {

        final Collection<V> list = new ArrayList<>();
        for (final Node node : this.nodeMap.values()) {
            list.add(node.value);
        }
        return list;
    }
}
