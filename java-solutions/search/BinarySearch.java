package search;

import java.util.Arrays;

public class BinarySearch {

    // ] length = 2n or 2n+1
    // every iteration make 2n or 2n+1 to n,
    // n is 2k or 2k+1 also
    // so to make length = 1 we can go from 2 or 3
    // so this tree contains all numbers

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
                // r - mid = r - (l + r) / 2 = (r - l) / 2 - new length

            } else {
                // Pred ^ Inv ^ l < r - 1 ∧ array[mid] <= x
                // Pred ^ Inv ^ l < l / 2 + r / 2 - 1 ∧ array[mid] <= x
                // Pred ^ Inv ^ l < (l + r) / 2 - 1 ∧ array[mid] <= x
                r = mid;
                // Pred ^ Inv ^ l < r - 1
                // mid - l = (l + r) / 2 - l = (r - l) / 2 - new length
            }
            // length_i = length_i-1 / 2
            // Inv ^ l < r - 1
        }
        // length_i = length_0 / 2^i => 0(i) = O(log(length_0)
        // Inv ^ l = r - 1
        return r;
    }
    // Post: R: array[R] <= x ^ ∀ a < R: array[a] > x

    // Pred: ∀ x1,x2  l < x1 < x2 < r: array[x1] >= array[x2]
    public static int binarySearchRec(int x, int[] array, int l, int r) {
        // Pred
        if (l == r - 1) {
            // Pred ^ l = r - 1 -> R
            // length_i = length_0 / 2^i => 0(i) = O(log(length_0)
            return r;
            // Post
        }
        // Pred ^ l != r - 1
        int mid = (l + r) / 2;
        if (array[mid] > x) {
            // Pred ^ l != r - 1 ^ array[mid] > x
            // Pred ^ l != r - 1 ^ array[mid] > x -> x ∈ array(mid:r]
            // r - mid = r - (l + r) / 2 = (r - l) / 2 - new length
            return binarySearchRec(x, array, mid, r);
            // Post
        } else {
            // Pred ^ l != r - 1 ^ array[mid] <=  x
            // Pred ^ l != r - 1 ^ array[mid] < x -> x ∈ array[l:mid]
            // mid - l = (l + r) / 2 - l = (r - l) / 2 - new length
            return binarySearchRec(x, array, l, mid);
            // Post
        }
        // length_i = length_i-1 / 2

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
