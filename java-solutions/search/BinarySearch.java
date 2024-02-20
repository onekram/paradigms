package search;

import java.util.Arrays;

public class BinarySearch {

    // Pred: ∀ x1,x2 x1 < x2: array[x1] >= array[x2]
    public static int binarySearchIterative(int x, int[] array) {
        int l = -1;
        int r = array.length;
        // Pred ^ Inv: ∀ a <= l array[a] > x ^ ∀ b >= r array[b] <= x
        while (l < r - 1) {
            // Pred ^  Inv ^ l < r - 1
            int mid = (l + r) / 2;
            if (array[mid] > x) {
                // Pred ^ Inv ^ l < r - 1 ∧ array[mid] > x
                // Pred ^ Inv ^ l / 2 + r / 2 < r - 1 ∧ array[mid] > x
                // Pred ^ Inv ^ (l + r) / 2 < r - 1 ∧ array[mid] > x
                l = mid;
                // Pred ^ Inv ^ l < r - 1
            } else {
                // Pred ^ Inv ^ l < r - 1 ∧ array[mid] <= x
                // Pred ^ Inv ^ l < l / 2 + r / 2 - 1 ∧ array[mid] <= x
                // Pred ^ Inv ^ l < (l + r) / 2 - 1 ∧ array[mid] <= x
                r = mid;
                // Pred ^ Inv ^ l < r - 1
            }
            // Inv ^ l < r - 1
        }
        // Inv ^ l = r - 1
        return r;
    }
    // Post: R: array[R] <= x ^ ∀ a < R: array[a] > x

    // Pred: ∀ x1,x2 x1 < x2: array[x1] >= array[x2]
    public static int binarySearchRec(int x, int[] array, int l, int r) {
        // Pred
        if (l == r - 1) {
            // Pred ^ l = r - 1 -> R
            return r;
            // Post
        }
        // Pred ^ l != r - 1
        int mid = (l + r) / 2;
        if (array[mid] > x) {
            // Pred ^ l != r - 1 ^ array[mid] > x
            // Pred ^ l != r - 1 ^ array[mid] > x -> x ∈ array(mid:r]
            return binarySearchRec(x, array, mid, r);
            // Post
        } else {
            // Pred ^ l != r - 1 ^ array[mid] <=  x
            // Pred ^ l != r - 1 ^ array[mid] < x -> x ∈ array[l:mid]
            return binarySearchRec(x, array, l, mid);
            // Post
        }
    }
    // Post: R: array[R] <= x ^ ∀ a < R: array[a] > x

    // Pred: args[0] - x, ∀ i,j 1 < i < j: args[i] >= args[j]
    public static void main(String[] args) {
        int x = Integer.parseInt(args[0]);
        int[] array = new int[args.length - 1];
        for (int i = 0; i < args.length - 1; i++) {
            array[i] = Integer.parseInt(args[1 + i]);
        }
//        System.out.println(binarySearchIterative(x, array));
        // Pred
        System.out.println(binarySearchRec(x, array, -1, args.length - 1));
        // R: array[R] <= x ^ ∀ a < R: array[a] > x
    }
}
