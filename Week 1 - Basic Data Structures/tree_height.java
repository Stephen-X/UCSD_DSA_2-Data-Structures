import java.util.*;
import java.io.*;

public class tree_height {
	public class TreeHeight {
		int n;
		int parent[];
		
		void read() throws IOException {
			FastScanner in = new FastScanner();
			n = in.nextInt();
			parent = new int[n];
			for (int i = 0; i < n; i++) {
				parent[i] = in.nextInt();
			}
		}

		int computeHeight() {
			int maxHeight = 0;
			for (int vertex = 0; vertex < n; vertex++) {
				int height = 0;
				for (int i = vertex; i != -1; i = parent[i])
					height++;
				maxHeight = Math.max(maxHeight, height);
			}
			return maxHeight;
		}

		// calculate height for node pointer and all its ancestors and store them in height[]
		void calculateHeight(int[] height, int pointer) {
			if (height[pointer] > 0) return;  // height has already been calculated
			if (parent[pointer] == -1) height[pointer] = 1;  // root node found
			else {
				calculateHeight(height, parent[pointer]);  // going for the parent
				height[pointer] = height[parent[pointer]] + 1;
			}
		}

		int computeHeightFast() {
			int maxHeight = 0;
			int[] height = new int[n];
			for (int i = 0; i < n; i++) {
				if (height[i] == 0) calculateHeight(height, i);
				// save some recursion if height's been calculated
				maxHeight = Math.max(maxHeight, height[i]);
			}
			return maxHeight;
		}
	}

	static public void main(String[] args) throws IOException {
            new Thread(null, new Runnable() {
                    public void run() {
                        try {
                            new tree_height().run();
                        } catch (IOException e) {
                        }
                    }
                }, "1", 1 << 26).start();
	}
	public void run() throws IOException {
		TreeHeight tree = new TreeHeight();
		tree.read();
		System.out.println(tree.computeHeightFast());
	}

	class FastScanner {
		StringTokenizer tok = new StringTokenizer("");
		BufferedReader in;

		FastScanner() {
			in = new BufferedReader(new InputStreamReader(System.in));
		}

		String next() throws IOException {
			while (!tok.hasMoreElements())
				tok = new StringTokenizer(in.readLine());
			return tok.nextToken();
		}
		int nextInt() throws IOException {
			return Integer.parseInt(next());
		}
	}

}
