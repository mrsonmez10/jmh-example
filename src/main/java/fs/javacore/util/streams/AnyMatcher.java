package fs.javacore.util.streams;

import java.util.concurrent.TimeUnit;
import java.util.function.LongPredicate;
import java.util.stream.LongStream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Benchmark for checking different "anyMatch" schemes.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class AnyMatcher {

    /**
     * Implementation notes:
     *   - operations are explicit inner classes to untangle unwanted lambda effects
     *   - all operations have similar semantics
     */

    @Param("100000")
    private int size;

    private LongPredicate op;

    @Setup
    public void setup() {
        op = new LongPredicate() {
            @Override
            public boolean test(long x) {
                return false;
            }
        };
    }

    @Benchmark
    public boolean seq_anyMatch() {
        return LongStream.range(0, size).anyMatch(op);
    }

    @Benchmark
    public boolean seq_filter_findFirst() {
        return LongStream.range(0, size).filter(op).findFirst().isPresent();
    }

    @Benchmark
    public boolean seq_filter_findAny() {
        return LongStream.range(0, size).filter(op).findAny().isPresent();
    }

    @Benchmark
    public boolean par_anyMatch() {
        return LongStream.range(0, size).parallel().anyMatch(op);
    }

    @Benchmark
    public boolean par_filter_findFirst() {
        return LongStream.range(0, size).parallel().filter(op).findFirst().isPresent();
    }

    @Benchmark
    public boolean par_filter_findAny() {
        return LongStream.range(0, size).parallel().filter(op).findAny().isPresent();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(AnyMatcher.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
