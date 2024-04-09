package fs.javacore.math;

import java.math.BigDecimal;
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
public class BigDecimals {

    /** Make sure TEST_SIZE is used to size the arrays. We need this constant to parametrize the operations count. */
    private static final int TEST_SIZE = 100;

    /* dummy variables for intermediate results */
    public Object[] dummyArr;
    public String[] dummyStringArray;
    public int dummy;

    /* array to hold the created objects. */
    private BigDecimal[] bigDecimals;
    private String[] stringInputs;
    private double[] doubleInputs;
    private BigDecimal[] hugeArray, largeArray, smallArray;

    @Setup
    public void setup() {
        Random r = new Random(1123);
        dummyArr = new Object[TEST_SIZE];
        bigDecimals = new BigDecimal[TEST_SIZE];
        stringInputs = new String[TEST_SIZE];
        doubleInputs = new double[TEST_SIZE];
        for (int i = 0; i < TEST_SIZE; i++) {
            double value = (double) (i + 1);
            switch (i % 4) {
                case 0:
                    value = -value * 54345.0d;
                    break;
                case 1:
                    value = value * 5434543453454355e100;
                    break;
                case 2:
                    value = -value / 5434543453454355e100;
                    break;
                case 3:
                    break;
            }

            bigDecimals[i] = new BigDecimal(value);
            stringInputs[i] = "" + value;
            doubleInputs[i] = value;
        }

        /*
         * Huge numbers larger than MAX_LONG
         */
        hugeArray = new BigDecimal[TEST_SIZE];

        /*
         * Large numbers less than MAX_LONG but larger than MAX_INT
         */
        largeArray = new BigDecimal[TEST_SIZE];

        /*
         * Small number less than MAX_INT
         */
        smallArray = new BigDecimal[TEST_SIZE];

        dummyStringArray = new String[TEST_SIZE];
        for (int i = 0; i < TEST_SIZE; i++) {
            int value = Math.abs(r.nextInt());
            hugeArray[i] = new BigDecimal("" + ((long) value + (long) Integer.MAX_VALUE)
                    + ((long) value + (long) Integer.MAX_VALUE) + ".55");
            largeArray[i] = new BigDecimal("" + ((long) value + (long) Integer.MAX_VALUE) + ".55");
            smallArray[i] = new BigDecimal("" + ((long) value / 1000) + ".55");
        }
    }

    /** Invokes the (String)-constructor of BigDecimal with various different values. */
    @Benchmark
    @OperationsPerInvocation(TEST_SIZE)
    public void testConstructorWithString(Blackhole bh) {
        for (String s : stringInputs) {
            bh.consume(new BigDecimal(s));
        }
    }

    /** Invokes the (double)-constructor of BigDecimal with various different values. */
    @Benchmark
    @OperationsPerInvocation(TEST_SIZE)
    public void testConstructorWithDouble(Blackhole bh) {
        for (double s : doubleInputs) {
            bh.consume(new BigDecimal(s));
        }
    }

    /** Invokes the toString method of BigDecimal with various different values. */
    @Benchmark
    @OperationsPerInvocation(TEST_SIZE)
    public void testToString(Blackhole bh) {
        for (BigDecimal s : bigDecimals) {
            bh.consume(s.toString());
        }
    }

    /**
     * Invokes the setScale method of BigDecimal with various different values.
     */
    @Benchmark
    @OperationsPerInvocation(TEST_SIZE)
    public void testSetScale(Blackhole bh) {
        for (BigDecimal s : bigDecimals) {
            bh.consume(s.setScale(2, BigDecimal.ROUND_HALF_UP));
        }
    }

    /** Invokes the setScale method of BigDecimal with various different values. */
    @Benchmark
    @OperationsPerInvocation(50 * TEST_SIZE)
    public void testSetScaleVarious(Blackhole bh) {
        for (int scale = 0; scale < 50; scale++) {
            for (BigDecimal s : bigDecimals) {
                bh.consume(s.setScale(scale, BigDecimal.ROUND_HALF_UP));
            }
        }
    }

    /** Invokes the add method of BigDecimal with various different values. */
    @Benchmark
    @OperationsPerInvocation(TEST_SIZE)
    public void testAdd(Blackhole bh) {
        BigDecimal tmp = null;
        for (BigDecimal s : bigDecimals) {
            if (tmp == null) {
                tmp = s;
                continue;
            }
            tmp = tmp.add(s);
        }
        bh.consume(tmp);
    }

    /** Invokes the multiply method of BigDecimal with various different values. */
    @Benchmark
    @OperationsPerInvocation(TEST_SIZE)
    public void testMultiply(Blackhole bh) {
        BigDecimal tmp = null;
        for (BigDecimal s : bigDecimals) {
            if (tmp == null) {
                tmp = s;
                continue;
            }
            tmp = tmp.multiply(s);
        }
        bh.consume(tmp);
    }

    /** Invokes the compareTo method of BigDecimal with various different values. */
    @Benchmark
    @OperationsPerInvocation(TEST_SIZE - 1)
    public void testCompareTo(Blackhole bh) {
        BigDecimal c = bigDecimals[0];
        for (BigDecimal s : bigDecimals) {
            bh.consume(c.compareTo(s));
        }
    }

    /** Test BigDecimal.toString() with huge numbers larger than MAX_LONG */
    @Benchmark
    @OperationsPerInvocation(TEST_SIZE)
    public void testHugeToString(Blackhole bh) {
        for (BigDecimal s : hugeArray) {
            bh.consume(s.toString());
        }
    }

    /** Test BigDecimal.toString() with large numbers less than MAX_LONG but larger than MAX_INT */
    @Benchmark
    @OperationsPerInvocation(TEST_SIZE)
    public void testLargeToString(Blackhole bh) {
        for (BigDecimal s : largeArray) {
            bh.consume(s.toString());
        }
    }

    /** Test BigDecimal.toString() with small numbers less than MAX_INT */
    @Benchmark
    @OperationsPerInvocation(TEST_SIZE)
    public void testSmallToString(Blackhole bh) {
        for (BigDecimal s : smallArray) {
            bh.consume(s.toString());
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BigDecimals.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}