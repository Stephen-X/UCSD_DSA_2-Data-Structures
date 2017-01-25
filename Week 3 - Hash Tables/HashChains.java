import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.LinkedList;

public class HashChains {

    private FastScanner in;
    private PrintWriter out;
    // store all strings in one list
    private List<String> elems;
    // hash table uses in fast algorithm
    private HList[] table;
    private int bucketCount;  // value m
    private int prime = 1000000007;  // value p
    private int multiplier = 263;  // value x

    public static void main(String[] args) throws IOException {
        new HashChains().processQueries();
    }

    // uses polynomial hash function to hash strings
    private int hashFunc(String s) {
        long hash = 0;
        for (int i = s.length() - 1; i >= 0; --i)
            hash = (hash * multiplier + s.charAt(i)) % prime;
        return (int)hash % bucketCount;  // fixing cardinality from p to m
    }

    private Query readQuery() throws IOException {
        String type = in.next();
        if (!type.equals("check")) {  // add/find/del
            String s = in.next();
            return new Query(type, s);
        } else {
            int ind = in.nextInt();
            return new Query(type, ind);
        }
    }

    private void writeSearchResult(boolean wasFound) {
        out.println(wasFound ? "yes" : "no");
        // Uncomment the following if you want to play with the program interactively.
        // out.flush();
    }

    // naive simulation: scanning the whole table for each operation
    private void processQuery(Query query) {
        switch (query.type) {
            case "add":
                if (!elems.contains(query.s))
                    elems.add(0, query.s);  // same as addFirst()
                break;
            case "del":
                if (elems.contains(query.s))
                    elems.remove(query.s);
                break;
            case "find":
                writeSearchResult(elems.contains(query.s));
                break;
            case "check":
                for (String cur : elems)
                    if (hashFunc(cur) == query.ind)
                        out.print(cur + " ");
                out.println();
                // Uncomment the following if you want to play with the program interactively.
                // out.flush();
                break;
            default:
                throw new RuntimeException("Unknown query: " + query.type);
        }
    }

    // uses hash table with chaining scheme for each operation
    private void processQueryFast(Query query) {
        int hash;
        switch (query.type) {
            case "add":
                hash = hashFunc(query.s);
                if (!table[hash].list.contains(query.s)) {
                    table[hash].list.addFirst(query.s);
                }
                break;
            case "del":
                hash = hashFunc(query.s);
                int index = table[hash].list.indexOf(query.s);
                if (index != -1) {
                    table[hash].list.remove(index);
                }
                break;
            case "find":
                hash = hashFunc(query.s);
                writeSearchResult(table[hash].list.contains(query.s));
                break;
            case "check":
                for (String s : table[query.ind].list) {
                    out.print(s + " ");
                }
                out.println();
                // Uncomment the following if you want to play with the program interactively.
                // out.flush();
                break;
            default:
                throw new RuntimeException("Unknown query: " + query.type);
        }
    }

    public void processQueries() throws IOException {
        //elems = new ArrayList<>();
        in = new FastScanner();
        out = new PrintWriter(new BufferedOutputStream(System.out));
        bucketCount = in.nextInt();  // cardinality m
        table = new HList[bucketCount];  // for fast algorithm
        for (int i = 0; i < bucketCount; i++) {
            table[i] = new HList();
        }
        int queryCount = in.nextInt();
        for (int i = 0; i < queryCount; ++i) {
            //processQuery(readQuery());
            processQueryFast(readQuery());
        }
        out.close();
    }

    // linked list object for each cell of the main array in Hash Table solution
    static class HList {
        LinkedList<String> list;

        public HList() {
            this.list = new LinkedList<String>();
        }
    }

    static class Query {
        String type;
        String s;
        int ind;  // index

        // add/find/del [string]
        public Query(String type, String s) {
            this.type = type;
            this.s = s;
        }

        // check [index]
        public Query(String type, int ind) {
            this.type = type;
            this.ind = ind;
        }
    }

    static class FastScanner {
        private BufferedReader reader;
        private StringTokenizer tokenizer;

        public FastScanner() {
            reader = new BufferedReader(new InputStreamReader(System.in));
            tokenizer = null;
        }

        public String next() throws IOException {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                tokenizer = new StringTokenizer(reader.readLine());
            }
            return tokenizer.nextToken();
        }

        public int nextInt() throws IOException {
            return Integer.parseInt(next());
        }
    }
}
