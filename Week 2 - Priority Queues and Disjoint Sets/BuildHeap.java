import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Random;
import java.util.Arrays;

public class BuildHeap {
    private int[] data;
    private ArrayList<Swap> swaps;

    private FastScanner in;
    private PrintWriter out;

    public static void main(String[] args) throws IOException {
        new BuildHeap().solve();
        //new BuildHeap().testGen();
    }

    private void readData() throws IOException {
        int n = in.nextInt();
        data = new int[n];
        for (int i = 0; i < n; ++i) {
          data[i] = in.nextInt();
        }
    }

    private void writeResponse() {
        out.println(swaps.size());
        for (Swap swap : swaps) {
          out.println(swap.index1 + " " + swap.index2);
        }
    }

    // the code is changed to produce sample answers
    private void generateSwaps(int[] data2) {
      //swaps = new ArrayList<Swap>();
      // The following naive implementation just sorts 
      // the given sequence using selection sort algorithm
      // and saves the resulting sequence of swaps.
      // This turns the given array into a binary min-heap, 
      // but in the worst case gives a quadratic number of swaps.
      for (int i = 0; i < data2.length; ++i) {
        for (int j = i + 1; j < data2.length; ++j) {
          if (data2[i] > data2[j]) {
            //swaps.add(new Swap(i, j));
            int tmp = data2[i];
            data2[i] = data2[j];
            data2[j] = tmp;
          }
        }
      }
    }

    // **********************************************

    // this converts the array input into binary min-heap
    private void generateSwapsHeap() {
        swaps = new ArrayList<Swap>();
        for (int i = data.length / 2 + 1; i >= 0; i--) {
            heapSiftDown(i, swaps);
        }
    }

    // this sifts down node i in a binary min-heap
    // swaps: records each swapping
    private void heapSiftDown(int i, ArrayList<Swap> swaps) {
        int minIndex = i;
        int l = leftChild(i);
        if (l != -1 && data[l] < data[minIndex]) minIndex = l;
        int r = rightChild(i);
        if (r != -1 && data[r] < data[minIndex]) minIndex = r;
        if (minIndex != i) {  // node i should be sifted down
            swapNodes(i, minIndex);
            swaps.add(new Swap(i, minIndex));  // records swapping
            heapSiftDown(minIndex, swaps);
        }
    }

    // left child of a given node i (0-based indexing)
    private int leftChild(int i) {
        int l = 2 * i + 1;
        if (l < data.length) return l;
        else return -1;  // node i is a leaf
    }

    // right child of a given node (0-based indexing)
    private int rightChild(int i) {
        int r = 2 * i + 2;
        if (r < data.length) return r;
        else return -1;  // node i is a leaf
    }

    // this swaps two nodes in a heap
    private void swapNodes(int i, int minIndex) {
        int temp = data[i];
        data[i] = data[minIndex];
        data[minIndex] = temp;
    }

    // actually does not work because there exists multiple legit min-heap...
    private void testGen() {
        Random r = new Random();
        int cont = 1;
        while (true) {
            int n = r.nextInt(10)+1;  // max: 100000
            System.out.println("Test " + cont++ + ": " + n + " elements.");
            data = new int[n];
            //int[] data2 = new int[n]; // sample array
            for (int i = 1; i <= n; i++) {
                data[i-1] = r.nextInt((int)Math.pow(10, 2)+1);  // max: 10^9
                //data2[i-1] = data[i-1];
            }
            System.out.println(">>Calculating heap...");
            generateSwapsHeap();
            /*
            System.out.println("  >>Calculating sample...");
            generateSwaps(data2);
            if (Arrays.equals(data, data2))
                System.out.println("PASSED\n");
            else {
                System.out.println("FAILED: Arrays are not the same.");
            */
                System.out.println("Heap: " + Arrays.toString(data));
            /*
                System.out.println("Naive: " + Arrays.toString(data2));
                break;
            }
            */
            System.out.println();
        }
    }

    // ***************************************

    public void solve() throws IOException {
        in = new FastScanner();
        out = new PrintWriter(new BufferedOutputStream(System.out));
        readData();

        //generateSwaps();
        generateSwapsHeap();

        writeResponse();
        out.close();
    }

    static class Swap {
        int index1;
        int index2;

        public Swap(int index1, int index2) {
            this.index1 = index1;
            this.index2 = index2;
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
