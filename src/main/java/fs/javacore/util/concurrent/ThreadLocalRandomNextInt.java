package fs.javacore.util.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ThreadLocalRandomNextInt {

    @State(Scope.Benchmark)
    public static class Global {
        public ThreadLocal<Random> tlr;
        private List<ThreadLocal<Integer>> contaminators; // reachable, non-garbage-collectable

        @Setup(Level.Trial)
        public void setup() {
            tlr = new ThreadLocal<Random>() {
                @Override
                protected Random initialValue() {
                    return ThreadLocalRandom.current();
                }
            };

            // contaminate ThreadLocals
            int contaminatorCount = Integer.getInteger("contaminators", 0);
            contaminators = new ArrayList<>(contaminatorCount);
            for (int i = 0; i < contaminatorCount; i++) {
                final int finalI = i;
                ThreadLocal<Integer> tl = new ThreadLocal<Integer>() {
                    @Override
                    protected Integer initialValue() {
                        return finalI;
                    }
                };
                contaminators.add(tl);
                tl.get();
            }
        }
    }

    @State(Scope.Thread)
    public static class Local {
        public ThreadLocalRandom tlr;

        @Setup(Level.Trial)
        public void setup() {
            tlr = ThreadLocalRandom.current();
        }
    }

    @Benchmark
    public int baseline(Local l) {
        return l.tlr.nextInt();
    }

    @Benchmark
    public int testJUC() {
        return ThreadLocalRandom.current().nextInt();
    }

    @Benchmark
    public int testLang(Global g) {
        return g.tlr.get().nextInt();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ThreadLocalRandomNextInt.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
