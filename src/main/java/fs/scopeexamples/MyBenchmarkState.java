package fs.scopeexamples;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

// @State annotation ile işaretlenen sınıf, benchmark metodları arasında paylaşılan durumu temsil eder
@State(Scope.Benchmark)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1, time = 1)
@BenchmarkMode(Mode.AverageTime)
public class MyBenchmarkState {

	// Benchmark metodlarında paylaşılan bir değişken
	private int sharedVariable;

	// Benchmark'in başlamadan önce yapılacak ayarlar
	@Setup
	public void setup() {
		sharedVariable = 0;
		System.out.println("Setting up " + sharedVariable);
	}

	// Benchmark'in sonunda yapılacak temizlik işlemleri
	@TearDown
	public void tearDown() {
		// Gerekirse kaynakları serbest bırakabiliriz
	}

	// Paylaşılan değişken üzerinde bir işlem gerçekleştiren benchmark metodu
	@Benchmark
	public void incrementSharedVariable() {
		System.out.println("Incrementing " + sharedVariable);
		sharedVariable++;
		System.out.println("Incremented " + sharedVariable);
	}

	// Paylaşılan değişkenin değerini döndüren benchmark metodu
	@Benchmark
	public int getSharedVariable() {
		System.out.println("getSharedVariable  " + sharedVariable);
		return sharedVariable;
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(MyBenchmarkState.class.getSimpleName())
				.forks(1)
				.build();

		new Runner(opt).run();
	}
}
