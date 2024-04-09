package fs.scopeexamples;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

// @State annotation ile işaretlenen sınıf, her bir thread için ayrı bir durumu temsil eder
@State(Scope.Thread)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1, time = 1)
@BenchmarkMode(Mode.AverageTime)
public class MyThreadState {

	// Her bir thread için ayrı bir değişken
	private int threadSpecificVariable;

	// Benchmark'in başlamadan önce yapılacak ayarlar
	@Setup
	public void setup() {
		// Her bir thread için başlangıç değeri
		threadSpecificVariable = 10;
		System.out.println("Setting up" + threadSpecificVariable);
	}

	// Benchmark'in sonunda yapılacak temizlik işlemleri
	@TearDown
	public void tearDown() {
		// Gerekirse kaynakları serbest bırakabiliriz
	}

	// Her bir thread için ayrı bir metot
	@Benchmark
	public void incrementThreadSpecificVariable() {
		System.out.println("Incrementing " + threadSpecificVariable);
		threadSpecificVariable++;
		System.out.println("Incremented " + threadSpecificVariable);
	}

	// Her bir thread için ayrı bir metot
	@Benchmark
	public int getThreadSpecificVariable() {
		System.out.println("getSharedVariable  " + threadSpecificVariable);
		return threadSpecificVariable;
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(MyThreadState.class.getSimpleName())
				.forks(1)
				.build();

		new Runner(opt).run();
	}
}
