package btree;

public class BTree {
    private final int nodeSize;
    private Node root;

    public BTree(int nodeSize) {
        this.nodeSize = nodeSize;
        root = new Node();
    }

    private class Node {
        int size = 0;
        Node parent;
        int[] keys = new int[nodeSize];
        Node[] children = null;

        private Node() { }

        private Node(int size, Node parent, int[] keys, Node[] children) {
            this.size = size;
            this.parent = parent;
            this.keys = keys;
            this.children = children;
        }

        boolean isLeaf() {
            return children == null;
        }
        boolean isOverflown() {
            return size == nodeSize;
        }

        void add(int key, Node child) {
            if (size == keys.length) throw new IllegalStateException("node is already full");
            int index = 0;
            if (size == 0) {
                keys[0] = key;
            } else {
                for (int i = size; i > 0; i--) {
                    if (keys[i - 1] <= key) {
                        keys[i] = key;
                        index = i + 1;
                        break;
                    } else {
                        keys[i] = keys[i - 1];
                    }
                }
            }

            size++;
            if (child != null) {
                if (children == null) children = new Node[keys.length + 1];
                System.arraycopy(children, index, children, index + 1, size - index);
                children[index] = child;
                child.parent = this;
            }
        }
    }

    public void add(int key) {
        findLeafAndInsert(key, root);
    }

    private void findLeafAndInsert(int key, Node node) {
        if (node.isLeaf()) {
            insert(key, node, null);
            return;
        }

        int i = 0;
        while (i < node.size) {
            if (node.keys[i] >= key) {
                findLeafAndInsert(key, node.children[i]);
                break;
            }

            i++;
            if (i == node.size) {
                findLeafAndInsert(key, node.children[i]);
            }
        }
    }

    private void insert(int key, Node node, Node child) {
        node.add(key, child);
        if (node.isOverflown()) {
            int[] newKeys = new int[nodeSize];
            int remains = nodeSize / 2;
            int newSize = remains;
            int goToParent = node.keys[remains];

            if (nodeSize % 2 == 0) {
                newSize = remains - 1;
            }

            System.arraycopy(node.keys, remains + 1, newKeys, 0, newSize);

            Node[] newChildren = null;

            if (!node.isLeaf()) {
                newChildren = new Node[nodeSize + 1];
                System.arraycopy(node.children, remains + 1, newChildren, 0, newSize + 1);
            }

            Node newChild = new Node(newSize, node.parent, newKeys, newChildren);
            node.size = remains;

            if (newChildren != null) {
                for (Node c : newChildren) {
                    if (c == null) break;
                    c.parent = newChild;
                }
            }

            if (node.parent == null) {
                Node[] children = new Node[nodeSize + 1];
                children[0] = node;
                children[1] = newChild;
                int[] keys = new int[nodeSize];
                keys[0] = goToParent;
                root = new Node(1, null, keys, children);
                node.parent = root;
                newChild.parent = root;
            } else {
                insert(goToParent, node.parent, newChild);
            }
        }
    }

    public boolean find(int value) {
        return find(root, value);
    }

    private boolean find(Node node, int key) {
        for (int i = 0; i < node.size; i++) {
            if (node.keys[i] == key) return true;
            if (node.keys[i] > key) {
                if (!node.isLeaf()) {
                    return find(node.children[i], key);
                } else {
                    return false;
                }
            } else if (i == node.size - 1) {
                if (!node.isLeaf()) {
                    return find(node.children[i + 1], key);
                } else {
                    return false;
                }
            }
        }

        return false;
    }
}