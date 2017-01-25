import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Random;
import java.util.Arrays;

public class HashSubstring {

    private static FastScanner in;
    private static PrintWriter out;
    private static final long prime = 250000000013L; // used for hashing; p >> |T||P|; "L" stands for "long"

    public static void main(String[] args) throws IOException {
        in = new FastScanner();
        out = new PrintWriter(new BufferedOutputStream(System.out));
        printOccurrences(getOccurrencesFast(readInput()));
        out.close();
    }

    private static Data readInput() throws IOException {
        String pattern = in.next();
        String text = in.next();
        return new Data(pattern, text);
    }

    private static void printOccurrences(List<Integer> ans) throws IOException {
        for (Integer cur : ans) {
            out.print(cur);
            out.print(" ");
        }
    }

    private static List<Integer> getOccurrences(Data input) {
        String s = input.pattern, t = input.text;
        int m = s.length(), n = t.length();
        List<Integer> occurrences = new ArrayList<Integer>();
        for (int i = 0; i + m <= n; ++i) {
    	    boolean equal = true;
    	    for (int j = 0; j < m; ++j) {
        		if (s.charAt(j) != t.charAt(i + j)) {
        		    equal = false;
         		    break;
        		}
    	    }
            if (equal)
                occurrences.add(i);
	    }
        return occurrences;
    }

    // searches for pattern using Rabin-Karp's algorithm
    private static List<Integer> getOccurrencesFast(Data input) {
        Random r = new Random();
        // determining the hash function from the family; x = [1, p-1], though watch out for negative
        //   values in modulo operations in precomputeHashes()
        int x = r.nextInt(10000) + 1;
        //System.out.println("x = " + x);  // for debugging
        int t = input.text.length();
        int p = input.pattern.length();
        List<Integer> occurrences = new ArrayList<Integer>();

        long patternHash = hashFunc(input.pattern, x);  // hash of the pattern
        //System.out.println("Pattern Hash = " + patternHash);  // for debugging

        /*
        long[] textHash = precomputeHashes(input, x);
        //System.out.println("Text Hash = " + Arrays.toString(textHash));  // for debugging
        for (int i = 0; i <= t-p; i++) {
            if (patternHash != textHash[i])
                continue;
            else if (input.pattern.equals(input.text.substring(i, i+p))) {  // Las Vegas
                occurrences.add(i);
            }
        }
        */
       

        // ***** the following gets occurences without creating hash array using precomputeHashes() *****
        ///*
        long textHash = hashFunc(input.text.substring(0, p), x);
        if (patternHash == textHash && input.pattern.equals(input.text.substring(0, p))) { // Las Vegas
            occurrences.add(0);  // First substring matches
        }

        // compute x^|P| mod prime
        long xExp = 1;
        for (int i = 1; i <= p; i++) {
            xExp = (xExp * x) % prime;
        }

        //System.out.print("Substring Hash = " + textHash + " ");  // for debugging
        for (int i = 1; i <= t-p; i++) {
            textHash = (x*textHash + prime - xExp*input.text.charAt(i-1) % prime + input.text.charAt(i+p-1)) % prime;
            //System.out.print(textHash + " ");  // for debugging
            if (patternHash != textHash)
                continue;
            else if (input.pattern.equals(input.text.substring(i, i+p))) {  // Las Vegas
                occurrences.add(i);
            }
        }
        //System.out.println();  // for debugging
        //*/

        return occurrences;
    }

    // precompute hashes for each |P|-long substring in text
    private static long[] precomputeHashes(Data input, long x) {
        int t = input.text.length();
        int p = input.pattern.length();
        long[] hash = new long[t-p+1];
        hash[0] = hashFunc(input.text.substring(0, p), x); // hash of first substring in text

        // compute x^|P| mod prime
        long xExp = 1;
        for (int i = 1; i <= p; i++) {
            xExp = (xExp * x) % prime;
        }

        for (int i = 1; i <= t-p; i++) {
            hash[i] = (x*hash[i-1] + prime - xExp*input.text.charAt(i-1) % prime + input.text.charAt(i+p-1)) % prime;
        }

        return hash;
    }

    // uses polynomial hash function to hash strings
    // h(s) = sum(s[i] * x^(|s|-1-i)) mod prime
    private static long hashFunc(String s, long x) {
        long hash = 0;
        for (int i = 0; i < s.length(); i++) {
            hash = (hash * x + s.charAt(i)) % prime;
        }
        return hash;
    }

    static class Data {
        String pattern;
        String text;
        public Data(String pattern, String text) {
            this.pattern = pattern;
            this.text = text;
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
