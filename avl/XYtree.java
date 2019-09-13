import java.util.Random;

public class XYtree {

    int value;
    int priority;
    XYtree left;
    XYtree right;

    XYtree(int value, int priority, XYtree left, XYtree right) {
        this.value = value;
        this.priority = priority;
        this.left = left;
        this.right = right;
    }

    private XYtree merge(XYtree l, XYtree r) {
        if (r == null)
            return l;
        if (l == null)
            return r;

        if (l.priority > r.priority) {
            XYtree newRight = merge(l.right, r);
            return new XYtree(l.value, l.priority, l.left, newRight);
        } else {
            XYtree newLeft = merge(l, r.left);
            return new XYtree(r.value, r.priority, newLeft, r.right);
        }
    }

    private XYtree[] split(int x) {
        XYtree[] newTrees = new XYtree[] { null, null };
        if (x > value) {
            if (right != null) {
                newTrees = right.split(x);
            }
            return new XYtree[] { new XYtree(value, priority, left, newTrees[0]), newTrees[1] };
        } else {
            if (left != null) {
                newTrees = left.split(x);
            }
            return new XYtree[] { newTrees[0], new XYtree(value, priority, newTrees[1], right) };
        }
    }

    void add(int x) {
        XYtree[] parts = split(x);
        Random random = new Random();
        XYtree m = new XYtree(x, random.nextInt(10000), null, null);
        XYtree newTree = merge(merge(parts[0], m), parts[1]);
        value = newTree.value;
        priority = newTree.priority;
        left = newTree.left;
        right = newTree.right;
    }

    @Override
    public String toString() {
        return "(" + String.valueOf(value) + ", " + String.valueOf(priority) + ")";
    }

    public String getTreeString() {
        return toString("", new StringBuilder());
    }

    private String toString(String indent, StringBuilder builder) {
        builder.append(indent);
        builder.append("+-");
        builder.append(this);
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
}