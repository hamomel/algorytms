package algo;

public class RBTree {

    private static class Node {
        public static final String ANSI_BLUE = "\u001B[34m";
        public static final String ANSI_RED = "\u001B[31m";
        public static final String ANSI_RESET = "\u001B[0m";

        int value;
        boolean isBlack = false;
        Node parent;
        Node left;
        Node right;

        public Node(int value) {
            this.value = value;
        }

        private Node getUncle() {
            if (parent.parent.left == parent) {
                return parent.parent.right;
            } else {
                return parent.parent.left;
            }
        }

        @Override
        public String toString() {
            return String.valueOf(value) + ", " + (isBlack ? "black" : "red");
        }

        private String colored() {
            String textColor = isBlack ? ANSI_BLUE : ANSI_RED;
            return textColor + toString() + ANSI_RESET;
        }
    }

    private Node root;

    public RBTree(int value) {
        Node newNode = new Node(value);
        newNode.isBlack = true;
        root = newNode;
    }

    public void insert(int value) {
        Node newNode = new Node(value);
        Node current = root;

        while (true) {
            if (newNode.value == current.value) return;

            if (newNode.value < current.value) {
                if (current.left == null) {
                    current.left = newNode;
                    newNode.parent = current;
                    break;
                } else {
                    current = current.left;
                }
            } else {
                if (current.right == null) {
                    current.right = newNode;
                    newNode.parent = current;
                    break;
                } else {
                    current = current.right;
                }
            }
        }

        checkStructure(newNode);
    }

    public boolean find(int value) {
        return find(value, root);
    }

    private boolean find(int value, Node node) {
        if (value == node.value) {
            return true;
        }

        if (value < node.value) {
            if (node.left != null) {
                return find(value, node.left);
            }
        } else {
            if (node.right != null) {
                return find(value, node.right);
            }
        }

        return false;
    }

    private void checkStructure(Node node) {
        if (node.parent == null) {
            node.isBlack = true;
            return;
        }

        if (node.parent.isBlack)
            return;

        Node uncle = node.getUncle();

        if (uncle == null || uncle.isBlack) {
            if (node.parent.right == node) {
                if (node.parent.parent.left == node.parent) {
                    rotateLeft(node);
                    checkStructure(node.left);
                } else {
                    rotateLeft(node.parent);
                    node.parent.left.isBlack = false;
                    node.parent.isBlack = true;
                }
            } else {
                if (node.parent.parent.right == node.parent) {
                    rotateRight(node);
                    checkStructure(node.right);
                } else {
                    rotateRight(node.parent);
                    node.parent.right.isBlack = false;
                    node.parent.isBlack = true;
                }
            }
        } else {
            node.parent.isBlack = true;
            uncle.isBlack = true;
            node.parent.parent.isBlack = false;
            checkStructure(node.parent.parent);
        }
    }

    private void rotateRight(Node node) {
        Node parent = node.parent;
        Node granny = parent.parent;
        if (granny != null) {
            if (granny.left == parent) {
                granny.left = node;
            } else {
                granny.right = node;
            }
        } else {
            root = node;
        }

        parent.left = node.right;
        if (node.right != null) {
            node.right.parent = parent;
        }
        parent.parent = node;
        node.right = parent;
        node.parent = granny;
    }

    private void rotateLeft(Node node) {
        Node parent = node.parent;
        Node granny = parent.parent;
        if (granny != null) {
            if (granny.left == parent) {
                granny.left = node;
            } else {
                granny.right = node;
            }
        } else {
            root = node;
        }

        parent.right = node.left;
        if (node.left != null) {
            node.left.parent = parent;
        }
        parent.parent = node;
        node.left = parent;
        node.parent = granny;
    }

    @Override
    public String toString() {
        return toString(root, "", new StringBuilder());
    }

    private String toString(Node node, String indent, StringBuilder builder) {
        builder.append(indent);
        builder.append("+-");
        builder.append(node.colored());
        builder.append("\n");
        indent += "   ";
        if (node.right != null) {
            toString(node.right, indent + "| ", builder);
        } else {
            builder.append(indent + "+- null");
            builder.append("\n");
        }
        if (node.left != null) {
            toString(node.left, indent, builder);
        } else {
            builder.append(indent + "+- null");
            builder.append("\n");
        }
        return builder.toString();
    }
}