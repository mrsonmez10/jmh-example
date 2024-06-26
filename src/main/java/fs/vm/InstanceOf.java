package fs.vm;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Tests various usages of instanceof.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class InstanceOf {

    private static final int NOOFOBJECTS = 100;
    private static final int NULLRATIO = 3;

    public Date[] dateArray;
    public Object[] objectArray;

    @Setup
    public void setup() {
        dateArray = new Date[NOOFOBJECTS * NULLRATIO];
        for (int i = 0; i < NOOFOBJECTS * NULLRATIO; i += NULLRATIO) {
            dateArray[i] = new Date();
        }
        objectArray = dateArray;
    }

    /**
     * Performs "instanceof Cloneable" on objects that definitely are of that interface. It is not clear however whether
     * the objects are null or not, therefore a simple nullcheck is all that should be left of here.
     */
    @Benchmark
    @OperationsPerInvocation((NOOFOBJECTS * NULLRATIO))
    public int instanceOfInterfacePartialRemove() {
        int dummy = 0;
        Date[] localArray = dateArray;
        for (int i = 0; i < NOOFOBJECTS * NULLRATIO; i++) {
            if (localArray[i] instanceof Cloneable) {
                dummy++;
            }
        }
        return dummy;
    }

    /**
     * Performs three serial instanceof statements on the same object for three different interfaces. The objects are
     * 50% null, and all non-null are instanceof the last interface.
     */
    @Benchmark
    @OperationsPerInvocation((NOOFOBJECTS * NULLRATIO))
    public int instanceOfInterfaceSerial() {
        int dummy = 0;
        Object[] localArray = objectArray;
        for (int i = 0; i < NOOFOBJECTS * NULLRATIO; i++) {
            if (localArray[i] instanceof Runnable) {
                dummy += 1000;
            } else if (localArray[i] instanceof CharSequence) {
                dummy += 2000;
            } else if (localArray[i] instanceof Serializable) {
                dummy++;
            }
        }
        return dummy;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(InstanceOf.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}

// visualvm.exe --jdkhome "C:\Users\PC\.jdks\corretto-17.0.9"
