import java.util.jar.*;

public class JarCreator {

    public static void main(String[] args) throws IOException {
        String[] targets = new String[args.length - 1];
        System.arraycopy(args, 1, targets, 0, targets.length);
        new JarCreator(false).create(args[0], targets);
    }

    private boolean verbose;

    public JarCreator() {
    }

    public JarCreator(boolean verbose) {
        this.verbose = verbose;
    }

    public void create(String target, String[] filenames) throws IOException {
        File[] files = new File[filenames.length];
        for(int i=0; i<files.length; i++) {
            files[i] = new File(filenames[i]);
        }
        create( new File(target), files );
    }

    public void create(File target, File[] files) throws IOException {
    }

}
