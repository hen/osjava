package com.generationjava.logview.source;

import org.apache.commons.collections.ResettableIterator;
import org.apache.commons.collections.IteratorUtils;
import com.generationjava.io.FileW;
import org.apache.commons.lang.StringUtils;
import com.generationjava.logview.LogSource;

public class FileLogSource implements LogSource {

    private ResettableIterator src;
    private String current;

    public FileLogSource(String filename) {
        String contents = FileW.loadFile(filename);
        contents = StringUtils.chopNewline(contents);
        String[] lines = StringUtils.split(contents, "\n");
        src = IteratorUtils.arrayIterator(lines);
    }

    public void reset() {
        src.reset();
    }

    public void remove() {
        src.remove();
    }

    public Object next() {
        return src.next();
    }

    public boolean hasNext() {
        return src.hasNext();
    }

    public String nextEntry() {
        this.current = (String)next();
        return this.current;
    }

    public String currentEntry() {
        return this.current;
    }

}
