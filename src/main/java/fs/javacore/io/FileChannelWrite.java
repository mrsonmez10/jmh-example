package fs.javacore.io;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Tests the overheads of I/O API.
 * This test is known to depend heavily on disk subsystem performance.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class FileChannelWrite {

    @Param("1000000")
    private int fileSize;

    private File f;
    private FileChannel fc;
    private ByteBuffer bb;
    private int count;

    @Setup(Level.Trial)
    public void beforeRun() throws IOException {
        f = File.createTempFile("FileChannelWriteBench", ".bin");
        bb = ByteBuffer.allocate(1);
        bb.put((byte) 47);
        bb.flip();
    }

    @TearDown(Level.Trial)
    public void afterRun() throws IOException {
        f.delete();
    }

    @Setup(Level.Iteration)
    public void beforeIteration() throws IOException {
        fc = FileChannel.open(f.toPath(), StandardOpenOption.WRITE);
        count = 0;
    }

    @TearDown(Level.Iteration)
    public void afterIteration() throws IOException {
        fc.close();
    }

    @Benchmark
    public void test() throws IOException {
        fc.write(bb);
        bb.flip();
        count++;
        if (count >= fileSize) {
            // start over
            fc.position(0);
            count = 0;
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(FileChannelWrite.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
