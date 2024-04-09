package fs.javacore.math;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class BigIntegers {

    private BigInteger[] hugeArray, largeArray, smallArray;
    public String[] dummyStringArray;
    public Object[] dummyArr;
    private static final int TESTSIZE = 1000;

    @Setup
    public void setup() {
        Random r = new Random(1123);

        hugeArray = new BigInteger[TESTSIZE]; /*
         * Huge numbers larger than
         * MAX_LONG
         */
        largeArray = new BigInteger[TESTSIZE]; /*
         * Large numbers less than
         * MAX_LONG but larger than
         * MAX_INT
         */
        smallArray = new BigInteger[TESTSIZE]; /*
         * Small number less than
         * MAX_INT
         */

        dummyStringArray = new String[TESTSIZE];
        dummyArr = new Object[TESTSIZE];

        for (int i = 0; i < TESTSIZE; i++) {
            int value = Math.abs(r.nextInt());

            hugeArray[i] = new BigInteger("" + ((long) value + (long) Integer.MAX_VALUE)
                    + ((long) value + (long) Integer.MAX_VALUE));
            largeArray[i] = new BigInteger("" + ((long) value + (long) Integer.MAX_VALUE));
            smallArray[i] = new BigInteger("" + ((long) value / 1000));
        }
    }

    /** Test BigInteger.toString() with huge numbers larger than MAX_LONG */
    @Benchmark
    @OperationsPerInvocation(TESTSIZE)
    public void testHugeToString(Blackhole bh) {
        for (BigInteger s : hugeArray) {
            bh.consume(s.toString());
        }
    }

    /** Test BigInteger.toString() with large numbers less than MAX_LONG but larger than MAX_INT */
    @Benchmark
    @OperationsPerInvocation(TESTSIZE)
    public void testLargeToString(Blackhole bh) {
        for (BigInteger s : largeArray) {
            bh.consume(s.toString());
        }
    }

    /** Test BigInteger.toString() with small numbers less than MAX_INT */
    @Benchmark
    @OperationsPerInvocation(TESTSIZE)
    public void testSmallToString(Blackhole bh) {
        for (BigInteger s : smallArray) {
            bh.consume(s.toString());
        }
    }

    /** Invokes the multiply method of BigInteger with various different values. */
    @Benchmark
    @OperationsPerInvocation(TESTSIZE)
    public void testMultiply(Blackhole bh) {
        BigInteger tmp = null;
        for (BigInteger s : hugeArray) {
            if (tmp == null) {
                tmp = s;
                continue;
            }
            tmp = tmp.multiply(s);
        }
        bh.consume(tmp);
    }

    /** Invokes the multiply method of BigInteger with various different values. */
    @Benchmark
    @OperationsPerInvocation(TESTSIZE)
    public void testAdd(Blackhole bh) {
        BigInteger tmp = null;
        for (BigInteger s : hugeArray) {
            if (tmp == null) {
                tmp = s;
                continue;
            }
            tmp = tmp.add(s);
        }
        bh.consume(tmp);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BigIntegers.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
