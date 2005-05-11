import java.util.Enumeration;
import java.util.jar.*;
import java.io.*;

public class JarExtractor {

    public static void main(String[] args) throws IOException {
        new JarExtractor().extract(args[0], args[1]);
    }

    private boolean verbose;

    public JarExtractor() {
    }

    public JarExtractor(boolean verbose) {
        this.verbose = verbose;
    }

    public void extract(String filename, String targetDir) throws IOException {
        extract(new File(filename), new File(targetDir));
    }

    public void extract(File file, File targetDir) throws IOException {
        if(!targetDir.isDirectory()) {
            throw new IllegalArgumentException("Target must be a directory. ");
        }

        if(!targetDir.exists()) {
            throw new IllegalArgumentException("Target directory must exist. ");
        }

        JarFile jar = new JarFile(file);
        try {
            Enumeration enumeration = jar.entries();
            while(enumeration.hasMoreElements()) {
                JarEntry entry = (JarEntry) enumeration.nextElement();
                File target = new File(targetDir, entry.getName());
                target.getParentFile().mkdirs();
                if(!entry.isDirectory()) {
                    InputStream in = jar.getInputStream(entry);
                    OutputStream out = new FileOutputStream(target);
                    copy(in, out);
                    in.close();
                    out.close();
                }
            }
        } finally {
            jar.close();
        }
    }

    // From Commons IO: org.apache.commons.io.IOUtils
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    private static int copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

}
