package algo;

public class RBTree {
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    int value;
    boolean isBlack;
    RBTree parent;
    RBTree left;
    RBTree right;

    public static RBTree create(int value) {
        return new RBTree(value, true);
    }

    private RBTree(int value, boolean isBlack) {
        this.value = value;
        this.isBlack = isBlack;
    }

    public void insert(int item) {
        if (parent != null) {
            throw new IllegalArgumentException("insertion must be done only on root");
        }
        RBTree newNode = new RBTree(item, false);
        insert(newNode);
    }

    public boolean find(int item) {
        if (item == value) {
            return true;
        } 
        
        if (item < value) {
            if (left != null) {
                return left.find(item);
            }
        } else {
            if (right != null) {
                return right.find(item);
            }
        }

        return false;
    }

    private void insert(RBTree node) {
        if (node.value < value) {
            if (left == null) {
                left = node;
                node.parent = this;
            } else {
                left.insert(node);
            }
        } else {
            if (right == null) {
                right = node;
                node.parent = this;
            } else {
                right.insert(node);
            }
        }

        node.checkStructure();
    }

    private void checkStructure() {
        if (parent == null) {
            isBlack = true;
            return;
        }

        if (parent.isBlack)
            return;

        RBTree uncle = getUncle();

        if (uncle == null || uncle.isBlack) {
            if (parent.right == this) {
                if (parent.parent.left == parent) {
                    rotateLeft();
                    left.checkStructure();
                } else {
                    parent.rotateLeft();
                    parent.left.reColor();
                    parent.reColor();
                    parent.checkStructure();
                }
            } else {
                if (parent.parent.right == parent) {
                    rotateRight();
                    right.checkStructure();
                } else {
                    parent.rotateRight();
                    parent.right.reColor();
                    parent.reColor();
                    parent.checkStructure();
                }
            }
        } else {
            parent.reColor();
            uncle.reColor();
            parent.parent.reColor();
            parent.parent.checkStructure();
        }
    }

    private void rotateRight() {
        RBTree granny = parent.parent;
        if (granny != null) {
            if (granny.left == parent) {
                granny.left = this;
            } else {
                granny.right = this;
            }    
        }
        
        parent.left = this.right;
        parent.parent = this;        
        right = parent;
        parent = granny;
    }

    private void rotateLeft() {
        RBTree granny = parent.parent;
        if (granny != null) {
            if (granny.left == parent) {
                granny.left = this;
            } else {
                granny.right = this;
            }    
        }
        
        parent.right = this.left;
        parent.parent = this;
        left = parent;
        parent = granny;
    }

    private void reColor() {
        if (parent == null) {
            isBlack = true;
        } else {
            isBlack = !isBlack;
        }
    }

    private RBTree getUncle() {
        if (parent.parent.left == parent) {
            return parent.parent.right;
        } else {
            return parent.parent.left;
        }
    }

    @Override
    public String toString() {
        return (isBlack ? "black" : "red") + ", " + String.valueOf(value);
    }

    public String getTreeString() {
        return toString("", new StringBuilder());
    }

    private String toString(String indent, StringBuilder builder) {
        builder.append(indent);
        builder.append("+-");
        builder.append(colored());
        builder.append("\n");
        indent += "   ";
        if (right != null) {
            right.toString(indent + "| ", builder);
        } else {
            builder.append(indent + "+- null");
            builder.append("\n");
        }
        if (left != null) {
            left.toString(indent, builder);
        } else {
            builder.append(indent + "+- null");
            builder.append("\n");
        }
        return builder.toString();
    }

    private String colored() {
        String textColor = isBlack ? ANSI_BLUE : ANSI_RED;
        return textColor + toString() + ANSI_RESET;
    }
}