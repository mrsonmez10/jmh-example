package fs.vm;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class Alloc {

    public static final int LENGTH = 400;
    public static final int ARR_LEN = 100;
    public int largeLen = 100;
    public int smalllen = 6;

    @Benchmark
    public void testLargeConstArray(Blackhole bh) throws Exception {
        int localArrlen = ARR_LEN;
        for (int i = 0; i < LENGTH; i++) {
            Object[] tmp = new Object[localArrlen];
            bh.consume(tmp);
        }
    }

    @Benchmark
    public void testLargeVariableArray(Blackhole bh) throws Exception {
        int localArrlen = largeLen;
        for (int i = 0; i < LENGTH; i++) {
            Object[] tmp = new Object[localArrlen];
            bh.consume(tmp);
        }
    }

    @Benchmark
    public void testSmallConstArray(Blackhole bh) throws Exception {
        int localArrlen = largeLen;
        for (int i = 0; i < LENGTH; i++) {
            Object[] tmp = new Object[localArrlen];
            bh.consume(tmp);
        }
    }

    @Benchmark
    public void testSmallObject(Blackhole bh) throws Exception {
        FortyBytes localDummy = null;
        for (int i = 0; i < LENGTH; i++) {
            FortyBytes tmp = new FortyBytes();
            tmp.next = localDummy;
            localDummy = tmp;
            bh.consume(tmp);
        }
    }

    @Benchmark
    public void testSmallVariableArray(Blackhole bh) throws Exception {
        int localArrlen = smalllen;
        for (int i = 0; i < LENGTH; i++) {
            Object[] tmp = new Object[localArrlen];
            bh.consume(tmp);
        }
    }

    final class FortyBytes {
        Object next;
        int y, z, k, f, g, e, t;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Alloc.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}

