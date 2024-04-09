package fs.custom;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 3)
@Measurement(iterations = 3)
public class StringBufferVsStringBuilderBenchmark {

	private static final int ITERATIONS = 10000000;
	private static final String APPEND_STRING = "abcabcabcabcabcabcabcabcabc";

	private Blackhole bh;

	@Setup
	public void setup(Blackhole bh) {
		this.bh = bh;
	}

	@Benchmark
	public String stringBufferBenchmark() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(APPEND_STRING.repeat(ITERATIONS));
		return stringBuffer.toString();
	}

	@Benchmark
	public String stringBuilderBenchmark() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(APPEND_STRING.repeat(ITERATIONS));
		return stringBuilder.toString();
	}

//	@Benchmark
//	public void stringBufferBenchmark() {
//		StringBuffer stringBuffer = new StringBuffer();
//		stringBuffer.append(APPEND_STRING.repeat(ITERATIONS));
//		bh.consume(stringBuffer.toString());
//	}
//
//	@Benchmark
//	public void stringBuilderBenchmark() {
//		StringBuilder stringBuilder = new StringBuilder();
//		stringBuilder.append(APPEND_STRING.repeat(ITERATIONS));
//		bh.consume(stringBuilder.toString());
//	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(StringBufferVsStringBuilderBenchmark.class.getSimpleName())
				.forks(1)
				.build();

		new Runner(opt).run();
	}
}




