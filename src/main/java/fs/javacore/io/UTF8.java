package fs.javacore.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Benchmark measuring UTF8 char operations.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class UTF8 {

    private String s;
    private BlackholedOutputStream bos;
    private DataOutputStream dos;

    @Setup
    public void setup(Blackhole bh) {
        bos = new BlackholedOutputStream(bh);
        dos = new DataOutputStream(bos);
        s = "abcdefghijklmnopqrstuvxyz0123456789";
    }

    @Benchmark
    public void testCharConversion() throws IOException {
        dos.writeUTF(s);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(UTF8.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}