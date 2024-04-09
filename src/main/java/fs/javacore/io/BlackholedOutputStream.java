package fs.javacore.io;

import java.io.IOException;
import java.io.OutputStream;
import org.openjdk.jmh.infra.Blackhole;

public class BlackholedOutputStream extends OutputStream {

    private final Blackhole bh;

    public BlackholedOutputStream(Blackhole bh) {
        this.bh = bh;
    }

    @Override
    public void write(int b) throws IOException {
        bh.consume(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        bh.consume(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        bh.consume(b);
    }
}
