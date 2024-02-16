package search;

import base.MainChecker;
import base.Runner;
import base.Selector;
import base.TestCounter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class BinarySearchTest {
    public static final int[] SIZES = {5, 4, 2, 1, 10, 100, 300};
    public static final int[] VALUES = new int[]{5, 4, 2, 1, 0, 10, 100, Integer.MAX_VALUE / 2};

    private BinarySearchTest() {
    }

    /* package-private */ static long[] base(final int c, final int x, final int[] a) {
        for (int i = 0; i < a.length; i++) {
            if (Integer.compare(a[i], x) != c) {
                return longs(i);
            }
        }
        return longs(a.length);
    }

    public static long[] longs(final long... longs) {
        return longs;
    }

    public static final Selector SELECTOR = new Selector(BinarySearchTest.class)
            .variant("Base",        Solver.variant("", Kind.DESC, BinarySearchTest::base))
            ;

    public static void main(final String... args) {
        SELECTOR.main(args);
    }

    /* package-private */ static Consumer<TestCounter> variant(final String name, final Consumer<Variant> variant) {
        final String className = "BinarySearch" + name;
        return counter -> variant.accept(new Variant(counter, new MainChecker(Runner.packages("search").args(className))));
    }

    /* package-private */ interface Solver {
        static Consumer<TestCounter> variant(final String name, final Kind kind, final Solver solver) {
            final Sampler sampler = new Sampler(kind, true, true);
            return BinarySearchTest.variant(name, vrt -> {
                solver.test(vrt, kind.d, 0);
                solver.test(vrt, kind.d, 0, 0);
                for (final int s1 : SIZES) {
                    final int size = s1 > 3 * TestCounter.DENOMINATOR ? s1 / TestCounter.DENOMINATOR : s1;
                    for (final int max : VALUES) {
                        final int[] a = sampler.sample(vrt, size, max);
                        final Set<Integer> used = new HashSet<>();
                        for (int i = 0; i < size; i++) {
                            if (used.add(a[i])) {
                                solver.test(vrt, kind.d, a[i], a);
                            }
                            if (i != 0) {
                                final int x = (a[i - 1] + a[i]) / 2;
                                if (used.add(x)) {
                                    solver.test(vrt, kind.d, x, a);
                                }
                            }
                        }
                        solver.test(vrt, kind.d, Integer.MIN_VALUE, a);
                        solver.test(vrt, kind.d, Integer.MAX_VALUE, a);
                    }
                }
            });
        }

        long[] solve(final int c, final int x, final int... a);

        default void test(final Variant variant, final int c, final int x, final int... a) {
            final String expected = Arrays.stream(solve(c, x, a))
                    .mapToObj(Long::toString)
                    .collect(Collectors.joining(" "));
            variant.test(IntStream.concat(IntStream.of(x), IntStream.of(a)), expected);
        }
    }

    public enum Kind {
        ASC(-1), DESC(1);

        public final int d;

        Kind(final int d) {
            this.d = d;
        }
    }

    public static class Variant {
        private final TestCounter counter;
        private final MainChecker checker;

        public Variant(final TestCounter counter, final MainChecker checker) {
            this.counter = counter;
            this.checker = checker;
        }

        void test(final IntStream ints, final String expected) {
            final List<String> input = ints.mapToObj(Integer::toString).collect(Collectors.toUnmodifiableList());
            checker.testEquals(counter, input, List.of(expected));
        }

        public void test(final int expected, final int... a) {
            test(Arrays.stream(a), Integer.toString(expected));
        }
    }

    public static class Sampler {
        private final Kind kind;
        private final boolean dups;
        private final boolean zero;

        public Sampler(final Kind kind, final boolean dups, final boolean zero) {
            this.kind = kind;
            this.dups = dups;
            this.zero = zero;
        }

        public int[] sample(final Variant variant, final int size, final int max) {
            final IntStream sorted = variant.counter.random().getRandom().ints(zero ? size : Math.max(size, 1), -max, max + 1).sorted();
            final int[] ints = (dups ? sorted : sorted.distinct()).toArray();
            if (kind == Kind.DESC) {
                final int sz = ints.length;
                for (int i = 0; i < sz / 2; i++) {
                    final int t = ints[i];
                    ints[i] = ints[sz - i - 1];
                    ints[sz - i - 1] = t;
                }
            }
            return ints;
        }
    }
}