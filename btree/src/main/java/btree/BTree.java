package btree;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class BTree {
    private final int minDegree;
    private Node root;

    @Contract(pure = true)
    public BTree(int minDegree) {
        this.minDegree = minDegree;
        root = new Node();
    }

    private class Node {
        int size = 0;
        Node parent;
        int[] keys = new int[minDegree * 2];
        Node[] children = null;

        @Contract(pure = true)
        private Node() {
        }

        @Contract(pure = true)
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
            return size == minDegree * 2;
        }

        void add(int key, Node child) {
            if (size == keys.length) throw new IllegalStateException("node is already overflown");
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

        int findKeyIndex(int key) {
            for (int i = 0; i < size; i++) {
                if (keys[i] == key) {
                    return i;
                }
            }

            return -1;
        }

        int findChildIndex(Node child) {
            for (int i = 0; i <= size; i++) {
                if (children[i].equals(child)) {
                    return i;
                }
            }

            return -1;
        }

        private void removeKeyByIndex(int index) {
            if (index < size - 1) {
                System.arraycopy(keys, index + 1, keys, index, size - 1 - index);
            }
            size--;
        }

        @Contract(value = "null -> false", pure = true)
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Arrays.equals(keys, node.keys);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(keys);
        }
    }

    public void add(int key) {
        findLeafAndInsert(key, root);
    }

    private void findLeafAndInsert(int key, @NotNull Node node) {
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

    private void insert(int key, @NotNull Node node, Node child) {
        node.add(key, child);
        if (node.isOverflown()) {
            int[] newKeys = new int[minDegree * 2];
            int goToParent = node.keys[minDegree];

            System.arraycopy(node.keys, minDegree, newKeys, 0, minDegree);

            Node[] newChildren = null;

            if (!node.isLeaf()) {
                newChildren = new Node[minDegree * 2 + 1];
                System.arraycopy(node.children, minDegree + 1, newChildren, 0, minDegree + 1);
            }

            Node newChild = new Node(minDegree, node.parent, newKeys, newChildren);
            node.size = minDegree;

            if (newChildren != null) {
                for (Node c : newChildren) {
                    if (c == null) break;
                    c.parent = newChild;
                }
            }

            if (node.parent == null) {
                Node[] children = new Node[minDegree * 2 + 1];
                children[0] = node;
                children[1] = newChild;
                int[] keys = new int[minDegree * 2];
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
        return find(root, value) != null;
    }

    @Nullable
    private Node find(@NotNull Node node, int key) {
        for (int i = 0; i < node.size; i++) {
            if (node.keys[i] == key) return node;
            if (node.keys[i] > key) {
                if (!node.isLeaf()) {
                    return find(node.children[i], key);
                } else {
                    return null;
                }
            } else if (i == node.size - 1) {
                if (!node.isLeaf()) {
                    return find(node.children[i + 1], key);
                } else {
                    return null;
                }
            }
        }

        return null;
    }

    public boolean delete(int key) {
        Node node = find(root, key);
        if (node == null) return false;
        delete(node, node.findKeyIndex(key));
        return true;
    }

    private void delete(@NotNull Node node, int index) {
        if (node.isLeaf()) {
            node.removeKeyByIndex(index);
            balanceSize(node);
        } else {
            Node left = node.children[index];
            node.keys[index] = left.keys[left.size - 1];
            if (left.isLeaf()) {
                left.removeKeyByIndex(left.size - 1);
                balanceSize(left);
            } else {
                delete(left, left.size - 1);
            }
        }
    }

    private void balanceSize(@NotNull Node node) {
        if (node == root || node.size >= minDegree) return;

        Node parent = node.parent;

        int indexInParent = parent.findChildIndex(node);
        boolean hasLeftNeighbor = indexInParent > 0;
        boolean canGetFromLeft = hasLeftNeighbor && parent.children[indexInParent - 1].size >= minDegree;
        boolean hasRightNeighbor = indexInParent < parent.size;
        boolean canGetFromRight = hasRightNeighbor && parent.children[indexInParent + 1].size >= minDegree;

        if (canGetFromLeft) {
            Node neighbor = parent.children[indexInParent - 1];
            System.arraycopy(node.keys, 0, node.keys, 1, node.size);
            node.keys[0] = parent.keys[indexInParent - 1];
            parent.keys[indexInParent - 1] = neighbor.keys[neighbor.size - 1];
            if (node.children != null && neighbor.children != null) {
                System.arraycopy(node.children, 0, node.children, 1, node.size + 1);
                node.children[0] = neighbor.children[neighbor.size];
            }
            node.size++;
            neighbor.size--;
        } else if (canGetFromRight) {
            Node neighbor = parent.children[indexInParent + 1];
            node.keys[node.size] = parent.keys[indexInParent];
            parent.keys[indexInParent] = neighbor.keys[0];
            System.arraycopy(neighbor.keys, 1, neighbor.keys, 0, neighbor.size - 1);
            if (node.children != null && neighbor.children != null) {
                node.children[node.size + 1] = neighbor.children[0];
                System.arraycopy(neighbor.children, 1, neighbor.children, 0, neighbor.size);
            }
            node.size++;
            neighbor.size--;
        } else {
            Node neighbor;
            if (hasRightNeighbor) {
                neighbor = parent.children[indexInParent + 1];
            } else {
                neighbor = node;
                node = parent.children[indexInParent - 1];
            }

            node.keys[node.size] = parent.keys[indexInParent];
            System.arraycopy(neighbor.keys, 0, node.keys, node.size + 1, neighbor.size);
            if (node.children != null && neighbor.children != null) {
                System.arraycopy(neighbor.children, 0, node.children, node.size + 1, neighbor.size + 1);
            }

            node.size = node.size + neighbor.size + 1;

            if (indexInParent < parent.size) {
                System.arraycopy(
                        parent.keys,
                        indexInParent + 1,
                        parent.keys,
                        indexInParent,
                        parent.size - indexInParent - 1);
                System.arraycopy(
                        parent.children,
                        indexInParent + 1,
                        parent.children,
                        indexInParent,
                        parent.size - indexInParent);
            }

            if (parent.size == 1) {
                if (parent != root) throw new IllegalStateException("tree is not in bounds, parent node is empty");
                root = node;
            } else {
                parent.size--;
                balanceSize(parent);
            }
        }
    }
}