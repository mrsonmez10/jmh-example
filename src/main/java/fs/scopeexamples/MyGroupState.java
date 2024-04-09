package fs.scopeexamples;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

// @State annotation ile işaretlenen sınıf, benchmark grupları arasında paylaşılan durumu temsil eder
@State(Scope.Group)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1, time = 1)
@BenchmarkMode(Mode.AverageTime)
public class MyGroupState {

	// Benchmark grupları arasında paylaşılan bir değişken
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

	// Benchmark grubu içindeki bir metot
	@Benchmark
	@Group("myGroup")
	public void incrementSharedVariable() {
		System.out.println("Incrementing " + sharedVariable);
		sharedVariable++;
		System.out.println("Incremented " + sharedVariable);
	}

	// Benchmark grubu içindeki başka bir metot
	@Benchmark
	@Group("myGroup")
	public int getSharedVariable() {
		System.out.println("getSharedVariable  " + sharedVariable);
		return sharedVariable;
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(MyGroupState.class.getSimpleName())
				.forks(1)
				.build();

		new Runner(opt).run();
	}
}
