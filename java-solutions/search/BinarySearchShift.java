package search;

public class BinarySearchShift {
    // Pred:
    // ∃ bound: ∀ x1, x2: x1 < x2 < bound: array[x1] < array[x2]
    //       ∀ x1, x2: bound < x1 < x2: array[x1] < array[x2] < array[bound - 1]
    public static int findElementIterative(int x, int[] array) {
        //Pred
        int l = -1;
        int r = array.length;
        // Pred ^ Inv: ∀ a <= l array[a] > array[array.length - 1] ^ ∀ b >= r array[b] <= array[array.length - 1]
        while (l < r - 1) {
            //Pred ^ Inv ^ l < r - 1
            int mid = (l + r) / 2;
            if (array[mid] > array[array.length - 1]) {
                // Pred ^ l != r - 1 ^ array[mid] > array[array.length - 1]
                // Pred ^ l != r - 1 ^ array[mid] > array[array.length - 1] - > bound ∈ ((l + r) / 2, r]
                // Pred ^ Inv ^ l < r - 1 ∧ array[mid] > array[array.length - 1]
                // Pred ^ Inv ^ l / 2 + r / 2 < r - 1 ∧ array[(l + r) / 2] > array[array.length - 1]
                // Pred ^ Inv ^ (l + r) / 2 < r - 1 ∧ array[(l + r) / 2] > array[array.length - 1]
                l = mid;
                //Pred ^ Inv ^ l < r - 1
            } else {
                // Pred ^ l != r - 1  ^ array[mid] <= array[array.length - 1]
                // Pred ^ l != r - 1  ^ array[mid] <= array[array.length - 1] - > bound ∈ [l, (l + r) / 2]
                // Pred ^ Inv ^ l < r - 1 ∧ array[(l + r) / 2] <= array[array.length - 1]
                // Pred ^ Inv ^ l < l / 2 + r / 2 - 1 ∧ array[(l + r) / 2] <= array[array.length - 1]
                // Pred ^ Inv ^ l < (l + r) / 2 - 1 ∧ array[(l + r) / 2] <= array[array.length - 1]
                r = mid;
                // Pred ^ Inv ^ l < r - 1
            }
            // Pred ^ Inv ^ l < r - 1
        }
        // Pred ^ Inv ^ l = r - 1 => r - bound
        int bound;
        if (r == 0) {
            // Pred ^ r == 0
            // if r == 0 then array.length - also bound a-priory
            bound = array.length;
            // Pred ^ bound ∈ [1, array.length]
        } else {
            // Pred ^ r != 0
            bound = r;
            // Pred ^ bound ∈ [1, array.length]
        }
        // Pred ^ bound ∈ [1, array.length]
        if (x >= array[0]) {
            // Pred ^ bound is bound ^ bound ∈ [1, array.length] ^ x >= array[0]
            // x >= array[0] -> ∀ x1 ∈ [bound, array.length) array[x] > array[x1]
            // -> x ∈ [0, bound)
            // Pred ^ bound is bound -> array[0:bound] - sorted
            return binarySearchIterative(x, array, -1, bound);
            // Post
        } else if (x <= array[array.length - 1]) {
            //  Pred ^ bound is bound ^ bound ∈ [1, array.length] ^ x <= array[array.length - 1]
            // x >= array[array.length - 1] -> ∀ x1 ∈ [0, bound) array[x] < array[x1]
            // -> x ∈ [bound, array.length)
            // Pred ^ bound is bound -> array[bound, array.length] - sorted
            return binarySearchIterative(x, array, bound - 1, array.length);
            // Post
        } else {
            // V: x < array[0] ^ x > array[array.length - 1]
            // Pred ^ V => ∀ el: el < bound: array[x] < array[el] ^
            //             ∀ el: el >= bound: array[x] > array[el]
            //          => ∄ el: array[el] = x
            return -1;
            // Post
        }
    }
    // Post: R: array[R] = x
    // | -1 if ∄ el: array[el] = x

    // Pred: ∀ x1,x2 l < x1 < x2 < r: array[x1] < array[x2]
    public static int binarySearchIterative(int x, int[] array, int l, int r) {
        // Pred ^ Inv: ∀ a <= l array[a] < x ^ ∀ b >= r array[b] > x
        while (l < r - 1) {
            // Pred ^ Inv ^ l < r - 1
            int mid = (l + r) / 2;
            if (array[mid] < x) {
                // Pred ^ Inv ^ l < r - 1 ^ array[(l + r) / 2] < x
                // Pred ^ Inv ^ l / 2 + r / 2 < r - 1 ∧ array[(l + r) / 2] < x
                // Pred ^ Inv ^ (l + r) / 2 < r - 1 ∧ array[(l + r) / 2] < x
                l = mid;
                // Pred ^ Inv ^ l < r - 1
            } else if (array[mid] > x) {
                // Pred ^ Inv ^ l < r - 1 ^ array[(l + r) / 2] > x
                // Pred ^ Inv ^ l < l / 2 + r / 2 - 1 ∧ array[(l + r) / 2] > x
                // Pred ^ Inv ^ l < (l + r) / 2 - 1 ∧ array[(l + r) / 2] > x
                r = mid;
                // Pred ^ Inv ^ l < r - 1
            } else {
                // Pred ^ Inv ^ l < r - 1 ^ array[mid] = x -> R = mid
                return mid;
                // Post
            }
            // Pred ^ Inv ^ array[mid] != x
        }
        // Pred ^ Inv ^ (l = r - 1) ^ (not found yet) => ∀ a <= r - 1 array[a] < x ^ ∀ b >= r array[b] > x
        //                                     => ∄ el: array[el] = x
        return -1;
        // Post
    }
    // Post: R: array[R] = x |-1 if ∄ i: l < i < r: array[i] = x

    // Pred:
    // ∃ bound: ∀ x1, x2: x1 < x2 < bound: array[x1] < array[x2]
    //       ∀ x1, x2: bound < x1 < x2: array[x1] < array[x2]
    // bound - index for sorted(array)[n - 1]
    public static int findElementRec(int x, int[] array) {
        // Pred
        int bound = findBoundRec(array, -1, array.length);
        // bound is bound
        if (bound == 0) {
            // Pred ^ bound = 0
            // if bound == 0 then array.length - also bound a-priory
            bound = array.length;
            // Pred
        } else {
            // Pred ^ bound != 0
        }
        // Pred ^ bound is bound ^ bound ∈ [1, array.length]
        if (x >= array[0]) {
            // Pred ^ bound is bound ^ bound ∈ [1, array.length] ^ x >= array[0]
            // x >= array[0] -> ∀ x1 ∈ [bound, array.length) array[x] > array[x1]
            // -> x ∈ [0, bound)
            // Pred ^ bound is bound -> array[0:bound] - sorted
            return binarySearchRec(x, array, -1, bound);
            // Post
        } else if (x <= array[array.length - 1]) {
            //  Pred ^ bound is bound ^ bound ∈ [1, array.length] ^ x <= array[array.length - 1]
            // x >= array[array.length - 1] -> ∀ x1 ∈ [0, bound) array[x] < array[x1]
            // -> x ∈ [bound, array.length)
            // Pred ^ bound is bound -> array[bound, array.length] - sorted
            return binarySearchRec(x, array, bound - 1, array.length);
            // Post
        } else {
            // C: x < array[0] ^ x > array[array.length - 1]
            // Pred ^ C => ∀ el: el < bound: array[x] < array[el] ^
            //             ∀ el: el >= bound: array[x] > array[el]
            //          => ∄ el: array[el] = x
            return -1;
            // Post
        }
        // ∀ x1,x2  x1 < x2: array[x1] < array[x2]
    }
    // Post: R: array[R] = x |-1 if ∄ el: array[el] = x

    // Pred:
    // ∃ bound: ∀ x1, x2: l < x1 < x2 < bound < r: array[x1] < array[x2]
    //       ∀ x1, x2: l < bound < x1 < x2 < r: array[x1] < array[x2] < array[bound - 1]
    // bound - index for sorted(array)[n - 1]
    public static int findBoundRec(int[] array, int l, int r) {
        // Pred
        if (l == r - 1) {
            // Pred ^ l = r - 1 -> ∀ x: x < r : array[x] > array[n - 1] ^
            //                     ∀ x1: x1 >= r : array[x1] <= array[n - 1] < array[x]
            return r;
            // Post
        }
        // Pred ^ l != r - 1
        int mid = (l + r) / 2;
        if (array[mid] > array[array.length - 1]) {
            // Pred ^ l != r - 1 ^ array[mid] > array[array.length - 1]
            // Pred ^ l != r - 1 ^ array[mid] > array[array.length - 1] - > bound ∈ ((l + r) / 2, r]
            return findBoundRec(array, mid, r);
            // Post
        } else {
            // Pred ^ l != r - 1  ^ array[mid] <= array[array.length - 1]
            // Pred ^ l != r - 1  ^ array[mid] <= array[array.length - 1] - > bound ∈ [l, (l + r) / 2]
            return findBoundRec(array, l, mid);
            // Post
        }
    }
    // Post: R: R = bound

    // Pred: ∀ x1,x2 l < x1 < x2 < r: array[x1] < array[x2]
    public static int binarySearchRec(int x, int[] array, int l, int r) {
        // Pred
        if (l == r - 1) {
            // Pred ^ l = r - 1 -> ∀ el ∈ array: el != x
            return -1;
            // Post
        }
        // Pred ^ l != r - 1
        int mid = (l + r) / 2;
        if (array[mid] < x) {
            // Pred ^ array[(l + r) / 2] < x -> x ∈ ((l + r) / 2, r]
            return binarySearchRec(x, array, mid, r);
            // Post
        } else if (array[mid] > x){
            // Pred ^ array[(l + r) / 2] > x -> x ∈ [l, (l + r) / 2)
            return binarySearchRec(x, array, l, mid);
            // Post
        } else {
            // Pred ^ array[(l + r) / 2] == x
            return mid;
            // Post
        }
    }
    // Post: R: array[R] = x |-1 if ∄ i: l < i < r: array[i] = x

    // Pred: args[0] - x,
    // ∃ el: ∀ x1, x2: 1 < x1 < x2 < bound < args.length - 1: args[x1] < args[x2]
    //       ∀ x1, x2: bound < x1 < x2 < : args[x1] < args[x2] < args[bound - 1]

    public static void main(String[] args) {
        int x = Integer.parseInt(args[0]);
        int[] array = new int[args.length - 1];
        for (int i = 0; i < args.length - 1; i++) {
            array[i] = Integer.parseInt(args[1 + i]);
        }
        // Pred
        System.out.println(findElementIterative(x, array));
        // Post: R: array[R] = x |-1 if ∄ i: l < i < r: array[i] = x
//        System.out.println(findElementRec(x, array));
    }
}
