package code316.io;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.File;
import java.io.FilenameFilter;

public class FileMaskFilter implements FilenameFilter {
    public static final String FILE_MASK_SEPARATOR = ",";

    protected String fileMask = "*.*";
    protected boolean acceptAllFiles;
    protected boolean accepting = true;
    protected int maxFiles;
    protected int accepted;
    protected ArrayList masks = new ArrayList();

    public FileMaskFilter(String fileMask) {
        super();

        setFileMask(fileMask);
    }

    public String getFileMask() {
        return fileMask;
    }

    public boolean isAcceptAllFiles() {
        return acceptAllFiles;
    }

    public void setAcceptAllFiles(boolean newAcceptAllFiles) {
        acceptAllFiles = newAcceptAllFiles;
    }

    public boolean accept(File dir, String name) {
        if (!accepting) {
            return false;
        }

        boolean accept = false;

        if (acceptAllFiles || fileMatchesMask(name)) {
            accepted++;
            accept = true;

            if (accepted == maxFiles) {
                accepting = false;
            }
        }

        return accept;
    }

    private void addFileMask(String newFileMask) {
        fileMask = newFileMask;

        if (fileMask.equals("*.*")) {
            acceptAllFiles = true;
        }

        if (fileMask.startsWith("*.")) {
            fileMask = fileMask.substring(1);
        }

        masks.add(fileMask);
    }

    private boolean fileMatchesMask(String name) {
        boolean matches = false;

        int count = masks.size();

        for (int i = 0; i < count; i++) {
            String mask = (String) masks.get(i);
            if (name.endsWith(mask)) {
                matches = true;
                break;
            }
        }

        return matches;
    }

    public int getAccepted() {
        return accepted;
    }

    public int getMaxFiles() {
        return maxFiles;
    }

    public boolean isAccepting() {
        return accepting;
    }

    public void reset() {
        accepted = 0;
        accepting = true;
    }

    public void setFileMask(String fileMask) {
        if (fileMask == null || fileMask.trim().length() == 0) {
            throw new IllegalArgumentException("invalid fileMask value: " + fileMask);
        }

        this.fileMask = fileMask.trim();
        reset();
        masks.clear();

        if (fileMask.indexOf(FILE_MASK_SEPARATOR) == -1) {
            addFileMask(fileMask);
            return;
            // EARLY OUT /////////////////////
        }

        StringTokenizer tokenizer = new StringTokenizer(fileMask, FILE_MASK_SEPARATOR);

        while (tokenizer.hasMoreTokens()) {
            addFileMask(tokenizer.nextToken().trim());
        }       
    }

    public void setMaxFiles(int newMaxFiles) {
        maxFiles = newMaxFiles;
    }
}