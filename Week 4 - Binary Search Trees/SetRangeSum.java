import java.io.*;
import java.util.*;

public class SetRangeSum {

    BufferedReader br;
    PrintWriter out;
    StringTokenizer st;
    boolean eof;

    // Splay tree implementation

    // Vertex of a splay tree
    class Vertex {
        int key;
        // Sum of all the keys in the subtree - remember to update
        // it after each operation that changes the tree.
        long sum;
        Vertex left;
        Vertex right;
        Vertex parent;

        Vertex(int key, long sum, Vertex left, Vertex right, Vertex parent) {
            this.key = key;
            this.sum = sum;
            this.left = left;
            this.right = right;
            this.parent = parent;
        }
    }

    // this updates the sum field of v and the parent fields of its children
    void update(Vertex v) {
        if (v == null) return;
        v.sum = v.key + (v.left != null ? v.left.sum : 0) + (v.right != null ? v.right.sum : 0);
        if (v.left != null) {
            v.left.parent = v;
        }
        if (v.right != null) {
            v.right.parent = v;
        }
    }

    // Implementation of Zig; v is the node with lowest height
    // Zig: rotation of 2 nodes
    void smallRotation(Vertex v) {
        Vertex parent = v.parent;
        if (parent == null) {  // no need to rotate for just 1 node
            return;
        }
        Vertex grandparent = v.parent.parent;
        if (parent.left == v) {
            Vertex m = v.right;
            v.right = parent;
            parent.left = m;
        } else {
            Vertex m = v.left;
            v.left = parent;
            parent.right = m;
        }
        update(parent);
        update(v);
        v.parent = grandparent;
        if (grandparent != null) {
            if (grandparent.left == parent) {
                grandparent.left = v;
            } else {
                grandparent.right = v;
            }
        }
    }

    // Implementation of Zig-zig & Zig-zag; v is the node with lowest height
    // Zig-zig: the node, its parent and grandparent are on the same side
    // Zig-zag: the parent and grandparent are on the opposite side
    void bigRotation(Vertex v) {
        if (v.parent.left == v && v.parent.parent.left == v.parent) {
            // Zig-zig
            smallRotation(v.parent);
            smallRotation(v);
        } else if (v.parent.right == v && v.parent.parent.right == v.parent) {
            // Zig-zig
            smallRotation(v.parent);
            smallRotation(v);
        } else {
            // Zig-zag
            smallRotation(v);
            smallRotation(v);
        }
    }

    // Makes splay of the given vertex and returns the new root.
    Vertex splay(Vertex v) {
        if (v == null) return null;
        while (v.parent != null) {
            if (v.parent.parent == null) { // Case for Zig
                smallRotation(v);
                break;
            }
            bigRotation(v);  // otherwise Zig-zig or Zig-zag
        }
        return v;
    }

    class VertexPair {
        Vertex left;
        Vertex right;
        VertexPair() {
        }
        VertexPair(Vertex left, Vertex right) {
            this.left = left;
            this.right = right;
        }
    }

    // Searches for the given key in the tree with the given root
    // and calls splay for the deepest visited node after that.
    // 
    // Returns pair of the result and the new root.
    // 
    // If found, result is a pointer to the node with the given key.
    // Otherwise, result is a pointer to the node with the smallest
    // bigger key (next value in the order).
    // If the key is bigger than all keys in the tree,
    // then result is null.
    VertexPair find(Vertex root, int key) {
        Vertex v = root;
        Vertex last = root;
        Vertex next = null;
        while (v != null) {
            if (v.key >= key && (next == null || v.key < next.key)) {
                next = v;
            }
            last = v;
            if (v.key == key) {
                break;
            }
            if (v.key < key) {
                v = v.right;
            } else {
                v = v.left;
            }
        }
        root = splay(last);
        return new VertexPair(next, root);
        // next is not necessarily = root if next = null (the given key is bigger than 
        // all keys in the tree)
    }

    // returns result.left: tree with nodes < key; result.right: tree with nodes >= key
    VertexPair split(Vertex root, int key) {
        VertexPair result = new VertexPair();
        VertexPair findAndRoot = find(root, key); // findAndRoot: [found key] + [tree root]
        root = findAndRoot.right;  // root of the tree after splaying
        result.right = findAndRoot.left;  // the given key or the next smallest bigger key
        if (result.right == null) {  // all nodes < key; no need to split
            result.left = root;
            return result;
        }
        result.right = splay(result.right);  // target key (or the cloest key) splayed to top

        // detaching the smaller subtree (all nodes < key) as result.left
        result.left = result.right.left;
        result.right.left = null;
        if (result.left != null) {
            result.left.parent = null;
        }

        update(result.left);
        update(result.right);

        return result;
    }

    // all keys in left tree < all keys in right tree (if the trees are not null)
    Vertex merge(Vertex left, Vertex right) {
        if (left == null) return right;
        if (right == null) return left;

        // finds the smallest key in the bigger tree and splayed to top
        while (right.left != null) {
            right = right.left;
        }
        right = splay(right);

        // smaller tree merges to the bigger tree
        right.left = left;
        update(right);

        return right;
    }

    // The following is code that uses splay tree to solve the problem

    Vertex root = null;

    void insert(int x) {
        Vertex left = null;
        Vertex right = null;
        Vertex new_vertex = null;
        VertexPair leftRight = split(root, x);
        left = leftRight.left;
        right = leftRight.right;
        if (right == null || right.key != x) {
            new_vertex = new Vertex(x, x, null, null, null);
            // key x is inserted when x is not already in the tree
        }
        root = merge(merge(left, new_vertex), right);
    }

    // implemented by myself
    void erase(int x) {
        if (root == null) return;
        VertexPair firstSplit = split(root, x);
        // if x exists, it's now in firstSplit.right
        VertexPair secondSplit = split(firstSplit.right, x + 1);
        // if x exists, it's now in secondSplit.left
        root = merge(firstSplit.left, secondSplit.right);
    }

    // implemented by myself
    boolean find(int x) {
        if (root == null) return false;
        VertexPair findAndRoot = find(root, x);
        root = findAndRoot.right;  // update root after find operation
        if (findAndRoot.left != null && findAndRoot.left.key == x)
            return true;
        else return false;
    }

    long sum(int from, int to) {
        VertexPair leftMiddle = split(root, from);
        Vertex left = leftMiddle.left;  // tree with all nodes < from
        Vertex middle = leftMiddle.right;  // tree with all nodes >= from
        VertexPair middleRight = split(middle, to + 1);
        middle = middleRight.left;  // tree with all nodes >= from and <= to
        Vertex right = middleRight.right;  // tree with all nodes > to
        long ans = 0;
        
        // the following is implemented by myself
        if (middle != null) ans = middle.sum;
        root = merge(left, merge(middle, right));  // merge back the trees

        return ans;
    }


    public static final int MODULO = 1000000001;
    // the mod and the summation operations are only requirements of the problem set

    void solve() throws IOException {
        int n = nextInt();
        int last_sum_result = 0;
        for (int i = 0; i < n; i++) {
            char type = nextChar();
            switch (type) {
                case '+' : {
                    int x = nextInt();
                    insert((x + last_sum_result) % MODULO);
                } break;
                case '-' : {
                    int x = nextInt();
                    erase((x + last_sum_result) % MODULO);
                } break;
                case '?' : {
                    int x = nextInt();
                    out.println(find((x + last_sum_result) % MODULO) ? "Found" : "Not found");
                } break;
                case 's' : {
                    int l = nextInt();
                    int r = nextInt();
                    long res = sum((l + last_sum_result) % MODULO, (r + last_sum_result) % MODULO);
                    out.println(res);
                    last_sum_result = (int)(res % MODULO);
                }
            }
        }
    }

    SetRangeSum() throws IOException {
        br = new BufferedReader(new InputStreamReader(System.in));
        out = new PrintWriter(System.out);
        solve();
        out.close();
    }

    public static void main(String[] args) throws IOException {
        new SetRangeSum();
    }

    String nextToken() {
        while (st == null || !st.hasMoreTokens()) {
            try {
                st = new StringTokenizer(br.readLine());
            } catch (Exception e) {
                eof = true;
                return null;
            }
        }
        return st.nextToken();
    }

    int nextInt() throws IOException {
        return Integer.parseInt(nextToken());
    }
    char nextChar() throws IOException {
        return nextToken().charAt(0);
    }
}
