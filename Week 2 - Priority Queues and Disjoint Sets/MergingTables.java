import java.io.*;
import java.util.Arrays;
import java.util.Locale;
import java.util.StringTokenizer;

public class MergingTables {
    private final InputReader reader;
    private final OutputWriter writer;

    public MergingTables(InputReader reader, OutputWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    public static void main(String[] args) {
        InputReader reader = new InputReader(System.in);
        OutputWriter writer = new OutputWriter(System.out);
        new MergingTables(reader, writer).run();
        writer.writer.flush();
    }

    class Table {
        Table parent;
        int rank;
        int numberOfRows;

        Table(int numberOfRows) {
            this.numberOfRows = numberOfRows;
            rank = 0;  // max. height of subtrees rooted under this table
            parent = this;
        }

        // O(log*n)
        Table getParent() {
            if (parent != this) {
                parent = parent.getParent();
            }  // path compression
            return parent;
        }

    }

    int maximumNumberOfRows = -1;

    void merge(Table destination, Table source) {
        Table realDestination = destination.getParent();
        Table realSource = source.getParent();
        if (realDestination == realSource) {
            return;
        }  // same table or already merged

        // union by rank heuristic: tree with lower height merges to higher tree
        if (realDestination.rank < realSource.rank) {
            realDestination.parent = realSource;
            realSource.numberOfRows += realDestination.numberOfRows;
            maximumNumberOfRows = Math.max(maximumNumberOfRows, realSource.numberOfRows);
            realDestination.numberOfRows = 0;  // Destination now contains only symbolic link to Source
        } else {
            realSource.parent = realDestination;
            realDestination.numberOfRows += realSource.numberOfRows;
            maximumNumberOfRows = Math.max(maximumNumberOfRows, realDestination.numberOfRows);
            realSource.numberOfRows = 0;  // Source now contains only symbolic link to Destination

            if (realDestination.rank == realSource.rank)
                realDestination.rank++;  // both trees are in the same height; height + 1 after merge
        }
    }

    public void run() {
        int n = reader.nextInt();  // number of tables
        int m = reader.nextInt();  // number of merges
        Table[] tables = new Table[n];
        for (int i = 0; i < n; i++) {  // number of rows in each table
            int numberOfRows = reader.nextInt();
            tables[i] = new Table(numberOfRows);
            maximumNumberOfRows = Math.max(maximumNumberOfRows, numberOfRows);
        }
        for (int i = 0; i < m; i++) {  // m merges
            int destination = reader.nextInt() - 1;
            int source = reader.nextInt() - 1;
            merge(tables[destination], tables[source]);
            writer.printf("%d\n", maximumNumberOfRows);  // print max. size after each merge
        }
    }

    static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;

        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream), 32768);
            tokenizer = null;
        }

        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    tokenizer = new StringTokenizer(reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }

        public double nextDouble() {
            return Double.parseDouble(next());
        }

        public long nextLong() {
            return Long.parseLong(next());
        }
    }

    static class OutputWriter {
        public PrintWriter writer;

        OutputWriter(OutputStream stream) {
            writer = new PrintWriter(stream);
        }

        public void printf(String format, Object... args) {
            writer.print(String.format(Locale.ENGLISH, format, args));
        }
    }
}
