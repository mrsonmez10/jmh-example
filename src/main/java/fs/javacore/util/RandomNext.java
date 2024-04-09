package fs.javacore.util;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Tests java.util.Random's different flavours of next-methods.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class RandomNext {

    public Random rnd;

    @Setup
    public void setup() {
        rnd = new Random();
    }

    @Benchmark
    public int testNextInt() {
        return rnd.nextInt();
    }

    @Benchmark
    public int testNextInt100() {
        return rnd.nextInt(100);
    }

    @Benchmark
    public int testNextInt128() {
        return rnd.nextInt(128);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(RandomNext.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
