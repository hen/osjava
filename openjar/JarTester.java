import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.Enumeration;
import java.util.Date;

public class JarTester {

    public static void main(String[] args) throws IOException {
        // tmp
        new JarTester().test(args[0]);
        new JarTester(true).test(args[0]);
    }

    private boolean verbose;

    public JarTester() {
    }

    public JarTester(boolean verbose) {
        this.verbose = verbose;
    }

    public void test(String filename) throws IOException {
        test(new File(filename));
    }

    public void test(File file) throws IOException {
        test( file, new PrintWriter(System.out) );
    }

    public void test(File file, Writer writer) throws IOException {
        StringBuffer buffer = new StringBuffer();
        PrintWriter printer = new PrintWriter(writer, true);
        JarFile jar = null;
        try {
            try {
                jar = new JarFile(file, true);
            } catch(IOException ioe) {
                // how to figure out that this is an invalid verification
                throw ioe;
            }
            Enumeration enumeration = jar.entries();
            while(enumeration.hasMoreElements()) {
                JarEntry entry = (JarEntry) enumeration.nextElement();
                if(verbose) {
                    // TODO: Sun's jar tool pads size to 6 chars. Could be dynamic.
                    String size = ""+entry.getSize();
                    while(size.length() < 6) {
                        size = " "+size;
                    }
                    buffer.append(size);
                    buffer.append(" ");
                    buffer.append(new Date(entry.getTime()));
                    buffer.append(" ");
                }
                buffer.append(entry.getName());
                printer.println(buffer.toString());
                buffer.setLength(0);
            }
        } finally {
            if(jar != null) {
                jar.close();
            }
        }
    }
    
    public void test(InputStream in, Writer writer) throws IOException {
        // need to catch IOException for the verify here
        JarInputStream jis = new JarInputStream(in, true);

    }

}
