/**
 * A hack by antony, eleminating evil shell scripts.
 *
 * Known bugs: Does not do projects which are not released in the maven
 * directory. (e.g. TigThreads / Charlotte - in the case of charlotte, 
 * missing pom files).
 */

import java.io.*;
import java.util.zip.*;
import java.util.*;
import java.util.regex.*;
import org.osjava.jardiff.Main;

public class Multidoc {

    public static File maven;
    public static File releases;
    public static File multidoc;

    public static void main(String[] args) throws Exception {
        if(args.length != 2) {
            System.out.println("Usage: Multidoc <maven path> <releases path>");
            System.exit(1);
        }
        maven = new File(args[0]);
        releases = new File(args[1]);
        multidoc = new File(releases, "multidoc-jnr");
        releases = new File(releases, "official");
        if(!maven.exists() || !maven.isDirectory() || !maven.canRead()) {
            System.out.println("maven is not a directory or readable.");
            System.exit(1);
        }
        if(!releases.exists() || !releases.isDirectory() || !releases.canRead()) {
            System.out.println("releases is not a directory or readable.");
            System.exit(1);
        }
        if(!multidoc.exists() || !multidoc.isDirectory() || !multidoc.canRead())
        {
            System.out.println("multidoc is not a directory or readable.");
            System.exit(1);
        }
        // Create a list of packages
        Set packages = new TreeSet();
        String[] tmp = maven.list();
        for(int a=0;a<tmp.length;a++) {
            File tmpFile = new File(maven, tmp[a]);
            tmpFile = new File(tmpFile, "jars");
            if(tmpFile.exists() && tmpFile.isDirectory() && tmpFile.canRead()) {
                packages.add(new OSJPackage(tmp[a]));
            }
        }

        prettyPrint(packages);

        Iterator i = packages.iterator();
        while(i.hasNext()) {
            OSJPackage pkg = (OSJPackage) i.next();
            pkg.extractJavadocs();
            pkg.generateAPIDiffs();
            pkg.generateIndexes();
        }
    }

    private static void prettyPrint(Set packages) {
        System.out.println("Found packages: ");
        Iterator i = packages.iterator();
        while(i.hasNext()) {
            OSJPackage pkg = (OSJPackage) i.next();
            Iterator j = pkg.getSubpackages().entrySet().iterator();
            while(j.hasNext()) {
                Map.Entry entry = (Map.Entry) j.next();
                OSJPackage.OSJSubPackage subPackage = 
                    (OSJPackage.OSJSubPackage) entry.getValue();
                System.out.println(pkg.name+":"+subPackage.subname+": "+subPackage.versions);
            }
        }
    }

    public static class OSJPackage implements Comparable {

        private static final Pattern POMFILE = Pattern.compile("^(.*)-([0-9\\.]+)\\.pom$");

        private String name;
        private TreeMap subpackages = new TreeMap();

        public OSJPackage(String name) {
            this.name = name;
            File mavenJars = new File(maven, name);
            mavenJars = new File(mavenJars, "poms");
            String [] tmp = mavenJars.list();
            for(int i=0;i<tmp.length;i++) {
                Matcher m = POMFILE.matcher(tmp[i]);
                if(m.matches()) {
                    String subname = m.group(1);
                    String version = m.group(2);
                    OSJSubPackage subPackage = 
                        (OSJSubPackage) subpackages.get(subname);
                    if(subPackage == null) {
                        subPackage = new OSJSubPackage(subname);
                        subpackages.put(subname, subPackage);
                    }
                    subPackage.addVersion(new Version(version));
                }
            }
        }

        public Map getSubpackages() {
            return subpackages;
        }

        public String toString() {
            return name;
        }

        public void extractJavadocs() throws Exception {
            Iterator i = subpackages.values().iterator();
            while(i.hasNext()) {
                OSJSubPackage subPackage =
                    (OSJSubPackage) i.next();
                subPackage.extractJavadocs();
            }
        }

        public void generateAPIDiffs() throws Exception {
            Iterator i = subpackages.values().iterator();
            while(i.hasNext()) {
                OSJSubPackage subPackage =
                    (OSJSubPackage) i.next();
                subPackage.generateAPIDiffs();
            }
        }

        public void generateIndexes() throws Exception {
            Iterator i = subpackages.values().iterator();
            while(i.hasNext()) {
                OSJSubPackage subPackage =
                    (OSJSubPackage) i.next();
                subPackage.generateIndexes();
            }
            // TODO: Generate master index.
        }

        public int compareTo(Object o) {
            return compareTo((OSJPackage)o);
        }

        public int compareTo(OSJPackage o) {
            return this.name.compareTo(o.name);
        }

        public class OSJSubPackage implements Comparable {

            private String subname;
            private Set versions = new TreeSet();
            private Set javadocs = new TreeSet();
            private Map diffs = new TreeMap();

            public OSJSubPackage(String subname) {
                this.subname = subname;
            }

            public String toString() {
                return subname;
            }

            public void addVersion(Version version) {
                versions.add(version);
            }

            public void extractJavadocs() throws Exception {
                File multiDocDir = new File(multidoc, subname);
                Iterator i = versions.iterator();
                while(i.hasNext()) {
                    Version v = (Version) i.next();
                    File javaDocDir = new File(multiDocDir, v.toString());
                    if(!javaDocDir.exists()) {
                        ZipFile distZip = findZipRelease(v);
                        try {
                            if(distZip == null) {
                                System.out.println("Skipping javadocs for: "+name+"/"+subname+"-"+v+" (No release zip file)");
                                continue;
                            }
                            String zipBase = findAPIBase(distZip);
                            if(zipBase == null) {
                                System.out.println("Skipping javadocs for: "+name+"/"+subname+"-"+v+" (Could not find javadocs in: "+distZip.getName());
                                continue;
                            }
                            System.out.println("Extracting javadocs for: "+name+"/"+subname+"-"+v);
                            Enumeration e = distZip.entries();
                            while(e.hasMoreElements()) {
                                ZipEntry entry = (ZipEntry) e.nextElement();
                                String entryName = entry.getName();
                                if(entryName.startsWith(zipBase)) {
                                    File extractTo = new File(javaDocDir, entryName.substring(zipBase.length()));
                                    extractZipEntry(distZip, entry, extractTo);
                                }
                            }
                            javadocs.add(v);
                        } finally {
                            if(distZip != null) {
                                distZip.close();
                            }
                        }
                    } else {
                        javadocs.add(v);
                    }
                }
            }

            public void generateAPIDiffs() throws Exception {
                File multiDocDir = new File(multidoc, subname);
                Iterator i = javadocs.iterator();
                if(!i.hasNext()) { return; }
                Version to = (Version) i.next();
                String[] args = new String[] { "-f", null, "-t", null, "-O", null, "-fa", null, "-ta", null, "-s", "../diff.css", "-o", "html", };
                while(i.hasNext()) {
                    Version from = (Version) i.next();
                    File mavenJars = new File(maven, name);
                    mavenJars = new File(mavenJars, "jars");
                    File fromJar = new File(mavenJars, subname+"-"+from+".jar");
                    File toJar = new File(mavenJars, subname+"-"+to+".jar");
                    if(!fromJar.exists() || !toJar.exists()) {
                        System.out.println("Skipping diff for "+name+"/"+subname+"-"+from+"-"+to+" (missing jar file)");
                        continue;
                    }
                    String filename = "diff-report-"+from.toString() + "-" + to.toString() + ".html";

                    args[1] = fromJar.toString();
                    args[3] = toJar.toString();
                    args[5] = new File(multiDocDir, filename).toString();
                    args[7] = from.toString();
                    args[9] = to.toString();
                    System.out.println("Creating diff: "+args[5]);
                    Main.main(args);
                    diffs.put(to, new Object[] { from, to, filename } );
                    to = from;
                }
            }

            public void generateIndexes() throws Exception {
                if(!javadocs.isEmpty()) {
                    System.out.println("Generating indexes for: "+name+"/"+subname);
                    File multiDocDir = new File(multidoc, subname);
                    byte[] buffer = new byte[2048];
                    int len;
                    InputStream in;
                    OutputStream out;
                    BufferedWriter writer;
                    File tmp;
                    boolean first = true;

                    tmp = new File(multiDocDir, "index.html");
                    in = new FileInputStream("index-template.html");
                    out = new FileOutputStream(tmp);
                    while( (len = in.read(buffer)) != -1) {out.write(buffer,0,len);}
                    in.close();
                    out.close();
                    tmp = new File(multiDocDir, "versions.html");
                    in = new FileInputStream("header-template.html");
                    out = new FileOutputStream(tmp);
                    while( (len = in.read(buffer)) != -1) {out.write(buffer,0,len);}
                    in.close();
                    writer = new BufferedWriter(new OutputStreamWriter(out));
                    Iterator i = javadocs.iterator();
                    while(i.hasNext()) {
                        Version v;
                        String javascript;

                        v = (Version) i.next();
                        File versionDir = new File(multiDocDir, v.toString());

                        String packageListFrame = v.toString() + "/" + findPackageListFrame(versionDir);
                        String packageFrame = v.toString() + "/" + findPackageFrame(versionDir);
                        String classFrame = v.toString() + "/" + findClassFrame(versionDir);

                        javascript = "load('packageListFrame', '"+packageListFrame+"', 'packageFrame', '"+packageFrame+"', 'classFrame', '"+classFrame+"')";

                        if(first) {
                            writer.write("<script>"+javascript+"</script>");
                            writer.newLine();
                            first = false;
                        }

                        writer.write("<nobr><a name=\""+v+"\"><font class=\"FrameItemFont\"><a href=\"javascript:"+javascript+"\">"+subname+" "+v+"</a>");
                        writer.newLine();
                        Object[] diff = (Object[]) diffs.get(v);
                        if(diff != null) {
                            writer.write("<a href=\""+diff[2]+"\" target=\"classFrame\">(diff to "+diff[0]+")</a>");
                            writer.newLine();
                        }
                        writer.write("</font></nobr><br/>");
                        writer.newLine();
                    }
                    writer.write("</body></html>");
                    writer.close();
                }
            }

            private ZipFile findZipRelease(Version v) throws Exception {
                File guess;
                File baseReleaseDir = new File(releases, name);
                // Try $releases/$name/$subname-$version.zip
                guess = new File(baseReleaseDir, subname + "-" + v + ".zip");
                if( guess.exists() && guess.isFile() ) {
                    return new ZipFile(guess);
                }
                // Try $releases/$name/$subname/$subname-$version.zip
                baseReleaseDir = new File(baseReleaseDir, subname);
                guess = new File(baseReleaseDir, subname + "-" + v + ".zip");
                if( guess.exists() && guess.isFile() ) {
                    return new ZipFile(guess);
                }
                // Give up.
                return null;
            }

            private String findAPIBase(ZipFile zipFile) {
                Enumeration e = zipFile.entries();
                while(e.hasMoreElements()) {
                    ZipEntry entry = (ZipEntry) e.nextElement();
                    String name = entry.getName();
                    if(name.endsWith("apidocs/") && entry.isDirectory()) {
                        return name;
                    }
                }
                return null;
            }

            public int compareTo(Object o) {
                return compareTo((OSJSubPackage)o);
            }

            public int compareTo(OSJSubPackage o) {
                return this.subname.compareTo(o.subname);
            }
        }
    }

    /**
     * Class to indicate a version number.
     */
    private static class Version implements Comparable {
        List versionParts = new ArrayList();
        public Version(String version) {
            StringBuffer tmp = new StringBuffer();
            for(int i=0;i<version.length();i++) {
                char ch = version.charAt(i);
                switch(ch) {
                    case '.':
                        versionParts.add(new Integer(tmp.toString()));
                        tmp.setLength(0);
                        break;
                    default:
                        tmp.append(ch);
                        break;
                }
            }
            versionParts.add(new Integer(tmp.toString()));
        }

        public int compareTo(Object o) {
            return compareTo((Version) o);
        }

        public int compareTo(Version o) {
            Iterator i = this.versionParts.iterator();
            Iterator j = o.versionParts.iterator();
            while(i.hasNext() && j.hasNext()) {
                Integer thisVal = (Integer) i.next();
                Integer oVal = (Integer) j.next();
                int ret = oVal.compareTo(thisVal);
                if(ret != 0) {
                    return ret;
                }
            }
            if(i.hasNext()) {
                return -1;
            } else if(j.hasNext()) {
                return 1;
            } else {
                return 0;
            }
        }

        public String toString() {
            StringBuffer ret = new StringBuffer();
            Iterator i = versionParts.iterator();
            while(i.hasNext()) {
                ret.append(i.next());
                if(i.hasNext()) {
                    ret.append('.');
                }
            }
            return ret.toString();
        }
    }

    public static void extractZipEntry(ZipFile file, ZipEntry entry, File to) throws Exception {
        if(entry.isDirectory()) {
            to.mkdirs();
        } else {
            byte[] buffer = new byte[2048];
            InputStream in = file.getInputStream(entry);
            FileOutputStream out = new FileOutputStream(to);
            int len;
            while(( len = in.read(buffer) ) != -1) { out.write(buffer,0,len); }
            in.close();
            out.close();
        }
    }

    public static String findPackageListFrame(File dir) {
        File tmp = new File(dir, "overview-frame.html");
        if(tmp.exists()) {
            return "overview-frame.html";
        } else { 
            return findRecursive(dir, "package-frame.html");
        }
    }

    public static String findPackageFrame(File dir) {
        return "allclasses-frame.html";
    }

    public static String findClassFrame(File dir) {
        File tmp = new File(dir, "overview-summary.html");
        if(tmp.exists()) {
            return "overview-summary.html";
        } else {
            return findRecursive(dir, "package-summary.html");
        }
    }

    public static String findRecursive(File dir, String file) {
        File tmp = new File(dir, file);
        if(tmp.exists()) {
            return file;
        } else {
            String[] foo = dir.list();
            for(int i=0;i<foo.length;i++) {
                tmp = new File(dir, foo[i]);
                if(tmp.isDirectory()) {
                    String ret = findRecursive(tmp, file);
                    if(ret != null) {
                        return foo[i] + "/" + ret;
                    }
                }
            }
            return null;
        }
    }
}
