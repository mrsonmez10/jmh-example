package fs.javacore.lang.reflect;

import fs.javacore.lang.ArrayCopy;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
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

@EmptyAnnotation
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class GetAnnotation {

    public Class<?> c;
    public Map<Class<?>, Method[]> cmap;
    public Map<Class<?>, HashMap<Method, Annotation>> amap;
    public Annotation noAnnotation;
    public Method[] methods;

    @Setup
    public void setup() {
        c = ArrayCopy.class;
        cmap = new HashMap<>();
        amap = new HashMap<>();
        noAnnotation = GetAnnotation.class.getDeclaredAnnotations()[0];
        methods = c.getDeclaredMethods();
    }

    @Benchmark
    public void doReflection(Blackhole bh) {
        Method[] methods = c.getDeclaredMethods();
        for (Method m : methods) {
            bh.consume(getAnnotation(m, c));
        }
    }

    @Benchmark
    public void doReflectionCached(Blackhole bh) {
        Method[] methods = cmap.get(c);
        if (methods == null) {
            methods = c.getDeclaredMethods();
            cmap.put(c, methods);
        }
        for (Method m : methods) {
            bh.consume(getAnnotationCached(m, c));
        }
    }

    @Benchmark
    public void doReflectionNoMethodAlloc(Blackhole bh) {
        for (Method m : methods) {
            bh.consume(getAnnotation(m, c));
        }
    }

    @Benchmark
    public void doReflectionCachedNoMethodAlloc(Blackhole bh) {
        for (Method m : methods) {
            bh.consume(getAnnotationCached(m, c));
        }
    }

    @SuppressWarnings("unchecked")
    public Annotation getAnnotation(Method m, @SuppressWarnings("rawtypes") Class c) {
        return m.getAnnotation(c);
    }

    @SuppressWarnings("unchecked")
    public Annotation getAnnotationCached(Method m, @SuppressWarnings("rawtypes") Class c) {
        HashMap<Method, Annotation> map = amap.get(c);
        if (map == null) {
            map = new HashMap<>();
            amap.put(c, map);
        }

        Annotation a = map.get(m);
        if (a == null) {
            a = m.getAnnotation(c);

            if (a == null) {
                map.put(m, noAnnotation);
            }
        } else if (a == noAnnotation) {
            return null;
        }

        return a;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(GetAnnotation.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}

