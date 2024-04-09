package fs.javax;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class AES {

    @Param("10000000")
    private int count;

    private Cipher cipher;
    private byte[] src;

    @Setup
    public void setup() throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(new byte[]{-80, -103, -1, 68, -29, -94, 61, -52, 93, -59, -128, 105, 110, 88, 44, 105}, "AES");
        IvParameterSpec iv = new IvParameterSpec(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});

        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

        src = new byte[count];
        new Random(1).nextBytes(src);
    }

    @Benchmark
    @Fork(jvmArgsAppend = {"-XX:+UnlockDiagnosticVMOptions", "-XX:-UseAES", "-XX:-UseAESIntrinsics"})
    public byte[] testBaseline() throws Exception {
        return cipher.doFinal(src);
    }

    @Benchmark
    @Fork(jvmArgsAppend = {"-XX:+UnlockDiagnosticVMOptions", "-XX:+UseAES", "-XX:-UseAESIntrinsics"})
    public byte[] testUseAes() throws Exception {
        return cipher.doFinal(src);
    }

    @Benchmark
    @Fork(jvmArgsAppend = {"-XX:+UnlockDiagnosticVMOptions", "-XX:+UseAES", "-XX:+UseAESIntrinsics"})
    public byte[] testUseAesIntrinsics() throws Exception {
        return cipher.doFinal(src);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(AES.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
