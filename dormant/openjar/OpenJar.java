/**
 * Jar-command API that will have both Sun and improved styles.
 */
public class OpenJar {

    // Usage: jar {ctxu}[vfm0Mi] [jar-file] [manifest-file] [-C dir] files ...
    //
    // TODO: 
    // -c  create new archive
    // -t  list table of contents for archive
    // -x  extract named (or all) files from archive
    // -u  update existing archive
    // -v  generate verbose output on standard output
    // -f  specify archive file name
    // -m  include manifest information from specified manifest file
    // -0  store only; use no ZIP compression
    // -M  do not create a manifest file for the entries
    // -i  generate index information for the specified jar files
    // -C  change to the specified directory and include the following file
    public static void main(String[] args) {

        boolean verbose = false;

        // flag handling
        // flags are in one block, which may be preceded by a -
        String flags = args[0];

        String illegalFlags = findIllegalChars( flags, {'c', 't', 'x', 'u', 'v', 'f', 'm', '0', 'M', 'i', 'C'} );
        if(illegalFlags != null) {
            System.err.println("Illegal flags: "+illegalFlags);
        }

        // expect one of c, t, x or u
        int cCount = count(flags, 'c');
        int tCount = count(flags, 't');
        int xCount = count(flags, 'x');
        int uCount = count(flags, 'u');

        int sumCounts = cCount + tCount + xCount + uCount;

        if( sumCounts == 0 ) {
            System.err.println("Expected one of the following flags 'c', 't', 'x' or 'u'. ");
            System.exit(1);
        }

        if( sumCounts > 1 ) {
            System.err.println("Expected only one of the following flags 'c', 't', 'x' or 'u'. ");
            System.exit(1);
        }

        // always accept vf
        int vCount = count(flags, 'v');
        int fCount = count(flags, 'f');

        if(fCount > 1) {
            // TODO: error
        }
        if(vCount > 1) {
            // TODO: error
        }

        if(vCount == 1) {
            verbose = true;
        }

        if(fCount == 0) {
            in = System.in;
        } else {
            // next argument, in correct order with manifest
            in = args[1];
        }

        if(cCount == 1) {
            // if c: m, 0, M, i are legal
            int mCount = count(flags, 'm');
            int zeroCount = count(flags, '0');
            int MCount = count(flags, 'M');
            int iCount = count(flags, 'i');

            // -C is a later flag
        }

        // if x:

        // if t:

        // if u: C is legal, later on

        // various options are focused on:
        //   creating jars
        //     including a manifest
        //     no compression
        //     no Manifest
        //     change to specified directory
        //     generate index information
        //   listing jars
        //   extracting jars
        //   updating in jars
        //     change to specified directory
        // globals:
        //   verbose output
        //   specify jar name
    }

    // API
    // create
    // list (Writer)
    // enumerate
    // update

    private int count(String str, int char) {
        char[] chars = str.charArray();
        int count = 0;
        for(int i=0; i<chars.length; i++) {
            if(chars[i] == char) {
                count++;
            }
        }
        return count;
    }

    private String findIllegalChars(String str, char[] chars) {
        char[] strChars = str.charArray();
        String illegalChars = "";
        for(int i=0; i<strChars.length; i++) {
            boolean found = false;
            for(int j=0; j<chars.length; j++) {
                if(strChars[i] == chars[j]) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                illegalChars += strChars[i];
            }
        }
        if("".equals(illegalChars)) {
            return null;
        }
        return illegalChars;
    }

}

/*
If any file is a directory then it is processed recursively.
The manifest file name and the archive file name needs to be specified
in the same order the 'm' and 'f' flags are specified.
If no -f, then output to STDOUT, expect input from STDIN etc
*/
