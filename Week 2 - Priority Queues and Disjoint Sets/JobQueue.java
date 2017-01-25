import java.io.*;
import java.util.StringTokenizer;
import java.util.Random;
import java.util.Arrays;

public class JobQueue {
    private int numWorkers;  // number of threads
    private int[] jobs;  // number of jobs

    private int[] assignedWorker;  // assigned thread for each job
    private long[] startTime;  // start process time for each job

    private FastScanner in;
    private PrintWriter out;

    public static void main(String[] args) throws IOException {
        new JobQueue().solve();
        //new JobQueue().testGen();
    }

    public void solve() throws IOException {
        in = new FastScanner();
        out = new PrintWriter(new BufferedOutputStream(System.out));
        readData();
        assignJobsHeap();
        writeResponse();
        out.close();
    }

    private void readData() throws IOException {
        numWorkers = in.nextInt();  // n independent threads
        int m = in.nextInt();
        jobs = new int[m];  // process time for each job (sec.)
        for (int i = 0; i < m; ++i) {
            jobs[i] = in.nextInt();
        }
    }

    private void writeResponse() {
        for (int i = 0; i < jobs.length; ++i) {
            out.println(assignedWorker[i] + " " + startTime[i]);
        }
    }

    // O(n^2) sample method
    private void assignJobs(int[] assignedWorker2, long[] startTime2) {
        long[] nextFreeTime = new long[numWorkers];  // next free time of each thread
        int duration, bestWorker;
        for (int i = 0; i < jobs.length; i++) {
            duration = jobs[i];
            bestWorker = 0;
            for (int j = 0; j < numWorkers; ++j) {
                if (nextFreeTime[j] < nextFreeTime[bestWorker])
                    bestWorker = j;
            }
            assignedWorker2[i] = bestWorker;
            startTime2[i] = nextFreeTime[bestWorker];
            nextFreeTime[bestWorker] += duration;
        }
    }

    // this uses priority queue / binary heap to process job assignment faster
    private void assignJobsHeap() {
        assignedWorker = new int[jobs.length];
        startTime = new long[jobs.length];
        Thread[] nextFreeTime = new Thread[numWorkers]; 
        // next free time of each thread; stored as binary min-heap
        for (int i = 0; i < numWorkers; i++)
            nextFreeTime[i] = new Thread(i, 0);

        for (int i = 0; i < jobs.length; i++) {
            assignedWorker[i] = nextFreeTime[0].index;
            startTime[i] = nextFreeTime[0].runtime;
            nextFreeTime[0].runtime += jobs[i];
            // thread with the smallest next free time (and the smallest index if
            //  next_free_time is the same) takes the job
            heapSiftDown(nextFreeTime, 0);
        }
    }

    // this sifts down node nextFreeTime[i] in a binary min-heap
    // note: if nextFreeTime[].runtime is the same, node with bigger index will be
    //         sifted down
    private void heapSiftDown(Thread[] nextFreeTime, int i) {
        int minIndex = i;

        int l = leftChild(nextFreeTime, i);
        if (l != -1 && nextFreeTime[l].runtime <= nextFreeTime[minIndex].runtime) {
            if (nextFreeTime[l].runtime < nextFreeTime[minIndex].runtime)
                minIndex = l;
            else if (nextFreeTime[l].index < nextFreeTime[minIndex].index)
                minIndex = l;
        }

        int r = rightChild(nextFreeTime, i);
        if (r != -1 && nextFreeTime[r].runtime <= nextFreeTime[minIndex].runtime) {
            if (nextFreeTime[r].runtime < nextFreeTime[minIndex].runtime)
                minIndex = r;
            else if (nextFreeTime[r].index < nextFreeTime[minIndex].index)
                minIndex = r;
        }

        if (minIndex != i) {  // node i should be sifted down
            swap(nextFreeTime[i], nextFreeTime[minIndex]);
            heapSiftDown(nextFreeTime, minIndex);
        }
    }

    // left child of a given node i (0-based indexing)
    private int leftChild(Thread[] nextFreeTime, int i) {
        int l = 2 * i + 1;
        if (l < nextFreeTime.length) return l;
        else return -1;  // node i is a leaf
    }

    // right child of a given node (0-based indexing)
    private int rightChild(Thread[] nextFreeTime, int i) {
        int r = 2 * i + 2;
        if (r < nextFreeTime.length) return r;
        else return -1;  // node i is a leaf
    }

    // this swaps (the contents of) two Thread objects
    private void swap(Thread a, Thread b) {
        int a_index = a.index;
        long a_runtime = a.runtime;
        a.index = b.index;
        a.runtime = b.runtime;
        b.index = a_index;
        b.runtime = a_runtime;
    }

    class Thread {
        int index;
        long runtime; // total running time of the thread

        public Thread(int index, long runtime) {
            this.index = index;
            this.runtime = runtime;
        }

        public Thread() {}

    }

    // generate test cases
    private void testGen() {
        Random r = new Random();
        long cont = 1;
        while (true) {
            numWorkers = r.nextInt((int)Math.pow(10, 5)) + 1;  // max: 10^5
            int m = r.nextInt((int)Math.pow(10, 5)) + 1;  // max: 10^5
            System.out.println("Test " + cont++ +": n = " + numWorkers + ", m = " + m);
            jobs = new int[m];
            int[] jobsSample = new int[m];
            for (int i = 0; i < m; i++) {
                jobs[i] = r.nextInt((int)Math.pow(10, 9) + 1);  // max: 10^9
                jobsSample[i] = jobs[i];
            }

            int[] assignedWorker2 = new int[jobs.length];
            long[] startTime2 = new long[jobs.length];
            assignJobsHeap();
            assignJobs(assignedWorker2, startTime2);

            if (Arrays.equals(assignedWorker, assignedWorker2) && Arrays.equals(startTime, startTime2))
                System.out.println("PASSED");
            else {
                System.out.println("FAILED");
                break;
            }
            System.out.println();
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
