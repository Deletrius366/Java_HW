package search;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

public class BinarySearchSpan {
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		int x = Integer.parseInt(args[0]);
		int n = args.length - 1;
		int[] a = new int[1000000];
		Arrays.fill(a, Integer.MAX_VALUE);
		int size = 0;
		for (int i = 0; i < n; i++) {
			a[i] = Integer.parseInt(args[i + 1]);
			size++;
		}
		if (size == 0) {
			System.out.println(0 + " " + 0);
		} else {
			// int r = 0;
			int r = binaryIter(x, a, -1, n);
			int l = binaryRecur(x, a, -1, n);
			if (l != n && a[l] == x) {
				System.out.println(l + " " + (r - l + 1));
			} else {
				System.out.println(l + " " + 0);
			}
		}
	}

	// Pre: a[i] >= a[i+1] for all i : (l <= i) && (i <= r) && (i >=0) && (i <=
	// <= size) && size > 0
	// I: (r' == size || r' < size && a[r'] < x) && (l' == -1 || l' > -1 &&
	// && a[l'] >= x) && (r'-l' >= 1) && (r''-l'' < r'-l')
	public static int binaryIter(int x, int[] a, int l, int r) {
		while (r - l > 1) {
			// A: Pre && I && r'-l' > 1
			int mid = (l + r) / 2;
			// A && r'-l' > 1 -> l' < mid < r'
			if (a[mid] >= x) {
				// A && l' < mid < r' && a[mid] >= x
				l = mid;
				// r'' = r' -> (r' == size || r' < size && a[r'] <= x)
				// l'' = mid && mid > l' && a[mid] >= x -> mid > -1 && a[mid] >=
				// >= x -> l''>-1 && a[l''] >= x
				// mid = (l' + r') / 2 && r' - l' > 1 -> r''-l'' = (r'-l')/2 >=
				// 1
			} else {
				// A && l' < mid < r' && a[mid] < x
				r = mid;
				// l'' = l' -> (l' == -1 || l' > -1 && a[l'] >= x)
				// r'' = mid && mid < r' && a[mid] < x -> mid < size && a[mid] <
				// < x -> r'' < size && a[r''] < x
			}
		}
		// !((r'-l') > 1) && (r'-l') >= 1 -> r' = l'+ 1;
		// l' = -1 -> r = 0 -> a[0] < x
		// r' = size -> l' = size-1 -> a[size-1] >= x;
		// l' != -1 && r' != size-1 && r' = l' + 1 -> a[l'] >= x && a[l'+1] < x
		return l;
	}

	// Post: (a[size-1] >= x && ans == size-1) || (a[0] < x && ans == -1) ||
	// || (a[size-1] < x && a[0] >= x && a[ans] >= x && a[ans+1] < x)

	// Pre: a[i] >= a[i+1] for all i : (l <= i) && (i <= r) && (i >=0) && (i <=
	// <= size) && size > 0
	// I: (r' == size || r' < size && a[r'] <= x) && (l' == -1 || l' > -1 &&
	// && a[l'] > x) && (r'-l' >= 1) && (r''-l'' < r'-l')
	public static int binaryRecur(int x, int[] a, int l, int r) {
		while (r - l > 1) {
			// A: Pre && I && r'-l' > 1
			int mid = (l + r) / 2;
			// A && r'-l' > 1 -> l' < mid < r'
			if (a[mid] > x) {
				// A && l' < mid < r' && a[mid] > x
				// r'' = r' -> (r' == size || r' < size && a[r'] < x)
				// l'' = mid && mid > l' && a[mid] > x -> mid > -1 && a[mid] >
				// > x -> l''>-1 && a[l''] > x
				return binaryRecur(x, a, mid, r);
			} else {
				// A && l' < mid < r' && a[mid] <= x
				// l'' = l' -> (l' == -1 || l' > -1 && a[l'] > x)
				// r'' = mid && mid < r' && a[mid] <= x -> mid < size && a[mid]
				// <=
				// <= x -> r'' < size && a[r''] <= x
				// mid = (l' + r') / 2 && r' - l' > 1 -> r''-l'' = (r'-l')/2 >=
				// 1
				return binaryRecur(x, a, l, mid);
			}
		}
		// !((r'-l') > 1) && (r'-l') >= 1 -> r' = l'+ 1;
		// l' = -1 -> r = 0 -> a[0] < x
		// r' = size -> l' = size-1 -> a[size-1] >= x;
		// l' != -1 && r' != size-1 && r' = l' + 1 -> a[l'] > x && a[l'+1] <= x
		return r;
	}
	// Post: (a[size-1] > x && ans == size-1) || (a[0] < x && ans == -1) ||
	// || (a[size-1] <= x && a[0] > x && a[ans] > x && a[ans+1] <= x)
}
