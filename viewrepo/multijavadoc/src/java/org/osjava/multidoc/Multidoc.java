/**
 * A hack by antony, eleminating evil shell scripts.
 *
 * Known bugs: Does not do projects which are not released in the maven
 * directory. (e.g. TigThreads / Charlotte - in the case of charlotte, 
 * missing pom files).
 */

package org.osjava.multidoc;

import java.io.*;
import java.util.zip.*;
import java.util.*;
import java.util.regex.*;
import org.osjava.jardiff.Main;
import org.w3c.dom.*;
import javax.xml.parsers.*;

public class Multidoc {

    public static File maven;
//    public static File releases;
    public static File multidoc;

    public static void main(String[] args) throws Exception {
        if(args.length != 2) {
            System.out.println("Usage: Multidoc <maven path> <multidoc path>");
            System.exit(1);
        }
/*
		System.out.println("Performing version numbers test");
		TreeSet testSet = new TreeSet();
		testSet.add(new Version("0.1"));
		testSet.add(new Version("0.2"));
		testSet.add(new Version("0.2-alpha"));
		testSet.add(new Version("0.2-beta"));
		testSet.add(new Version("0.2-rc1"));
		testSet.add(new Version("0.2-rc2"));
		testSet.add(new Version("0.2.1"));
		testSet.add(new Version("0.3"));
		testSet.add(new Version("0.3.1"));
		System.out.println("The following should be in newest-oldest order");
		System.out.println(testSet);
*/
        maven = new File(args[0]);
/*
        releases = new File(args[1]);
        multidoc = new File(releases, "multidoc-jnr");
        releases = new File(releases, "official");
*/
        multidoc = new File(args[1]);
        if(!maven.exists() || !maven.isDirectory() || !maven.canRead()) {
            System.out.println("maven is not a directory or readable.");
            System.exit(1);
        }
/*
        if(!releases.exists() || !releases.isDirectory() || !releases.canRead()) {
            System.out.println("releases is not a directory or readable.");
            System.exit(1);
        }
*/
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
//            pkg.extractJavadocs();
            pkg.generateJarIndex();
            pkg.loadJavadocs();
            pkg.generateAPIDiffs();
            pkg.generateIndexes();
        }
        generateIndex(packages);
    }

    private static void generateIndex(Set packages) throws Exception {
        System.out.println("Writing master index file.");
        byte[] buffer = new byte[2048];
        InputStream in;
        OutputStream out;
        BufferedWriter writer;
        int len;

        File index = new File(multidoc, "index.html");

        in = new FileInputStream("src/resources/header-index-template.html");
        out = new FileOutputStream(index);
        while( (len = in.read(buffer)) != -1) { out.write(buffer,0,len); }
        in.close();
        writer = new BufferedWriter(new OutputStreamWriter(out));
        Iterator i = packages.iterator();
        while(i.hasNext()) {
            OSJPackage pkg = (OSJPackage) i.next();
            Map subpackages = pkg.getSubpackages();
            Iterator j = subpackages.values().iterator();
            while(j.hasNext()) {
                OSJPackage.OSJSubPackage subpkg = 
                    (OSJPackage.OSJSubPackage) j.next();
                if(subpkg.javadocs.isEmpty()) {
                    continue;
                }
                writer.write("<li><a href=\""+subpkg.subname+"/index.html\">"+subpkg.subname+"</a></li>");
                writer.newLine();
            }
        }
        writer.flush();
        in = new FileInputStream("src/resources/footer-index-template.html");
        while( (len = in.read(buffer)) != -1) { out.write(buffer,0,len); }
        in.close();
        out.close();
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

        private static final Pattern POMFILE = Pattern.compile("^(.*)\\.pom$");

        private String name;
        private TreeMap subpackages = new TreeMap();

        public OSJPackage(String name) throws Exception {
            this.name = name;
            File mavenJars = new File(maven, name);
            mavenJars = new File(mavenJars, "poms");
            String [] tmp = mavenJars.list();
            if( tmp == null ) {
                System.err.println("Empty directory: " + mavenJars);
            } else {
                for(int i=0;i<tmp.length;i++) {
                    Matcher m = POMFILE.matcher(tmp[i]);
                    if(m.matches()) {
                        String nameAndVersion = m.group(1);
                        Document doc = null;
                        try {
                            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(mavenJars, tmp[i]));
                        } catch(Exception e) {
                            System.err.println("Error parsing "+new File(mavenJars, tmp[i]));
                            continue;
                        }

                        Node root = doc.getDocumentElement();
                        root = root.getFirstChild();

                        String version = null;
                        String subname = null;

                        // This is not how to write good java kids.
                        while(root != null) {
                            if("id".equals(root.getNodeName()) || "artifactId".equals(root.getNodeName())) {
                                subname = root.getFirstChild().getNodeValue();
                            } else if("currentVersion".equals(root.getNodeName())) {
                                version = root.getFirstChild().getNodeValue();
                            }
                            root = root.getNextSibling();
                        }

                        if(subname != null) {
                            OSJSubPackage subPackage = 
                                (OSJSubPackage) subpackages.get(subname);
                            if(subPackage == null) {
                                subPackage = new OSJSubPackage(subname);
                                subpackages.put(subname, subPackage);
                            }
                            if(version != null) {
                                subPackage.addVersion(new Version(version));
                            } else {
                                System.err.println("Null version: " + name);
                            }
                        } else {
                            System.err.println("Null subname: " + name);
                        }
                    }
                }
            }
        }

        public Map getSubpackages() {
                return subpackages;
        }

        public String toString() {
            return name;
        }

/*
        public void extractJavadocs() throws Exception {
            Iterator i = subpackages.values().iterator();
            while(i.hasNext()) {
                OSJSubPackage subPackage =
                    (OSJSubPackage) i.next();
                subPackage.extractJavadocs();
            }
        }
*/
        public void loadJavadocs() throws Exception {
            Iterator i = subpackages.values().iterator();
            while(i.hasNext()) {
                OSJSubPackage subPackage =
                    (OSJSubPackage) i.next();
                subPackage.loadJavadocs();
            }
        }

        public void generateJarIndex() throws Exception {
            Iterator i = subpackages.values().iterator();
            while(i.hasNext()) {
                OSJSubPackage subPackage =
                    (OSJSubPackage) i.next();
                subPackage.generateJarIndex();
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

            public void generateJarIndex() throws Exception {
                File mavenJars = new File(maven, name);
                mavenJars = new File(mavenJars, "jars");
                File multiDocDir = new File(multidoc, subname);
                if(!multiDocDir.exists()) {
                    return;
                }
                FileWriter writer = new FileWriter(new File(multiDocDir, "jars.list"));
                List tmp = new ArrayList(versions);
                for(int i=tmp.size()-1; i>=0; i--) {
                    Version v = (Version) tmp.get(i);
                    File jarFile = new File(mavenJars, subname+"-"+v+".jar");
                    if(jarFile.exists()) {
                        String jarFilename = jarFile.toString();
                        writer.write(jarFilename);
                        writer.write("\n");
                    }
                }
                writer.close();
            }

/*
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
*/

            public void loadJavadocs() throws Exception {
                File multiDocDir = new File(multidoc, subname);
                Iterator i = versions.iterator();
                while(i.hasNext()) {
                    Version v = (Version) i.next();
                    File javaDocDir = new File(multiDocDir, v.toString());
                    if(javaDocDir.exists()) {
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
                    in = new FileInputStream("src/resources/index-template.html");
                    out = new FileOutputStream(tmp);
                    while( (len = in.read(buffer)) != -1) {out.write(buffer,0,len);}
                    in.close();
                    out.close();
                    tmp = new File(multiDocDir, "versions.html");
                    in = new FileInputStream("src/resources/header-template.html");
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

                        writer.write("<tr>");
                        writer.newLine();

                        String packageListFrame = v.toString() + "/" + findPackageListFrame(versionDir);
                        String packageFrame = v.toString() + "/" + findPackageFrame(versionDir);
                        String classFrame = v.toString() + "/" + findClassFrame(versionDir);

                        javascript = "load('packageListFrame', '"+packageListFrame+"', 'packageFrame', '"+packageFrame+"', 'classFrame', '"+classFrame+"')";

                        if(first) {
                            writer.write("<script>"+javascript+"</script>");
                            writer.newLine();
                            first = false;
                        }

                        writer.write("<td><a name=\""+v+"\"><font class=\"FrameItemFont\"><a href=\"javascript:"+javascript+"\">"+subname+" "+v+"</a></font></td>");
                        writer.newLine();
                        Object[] diff = (Object[]) diffs.get(v);
                        if(diff != null) {
                            writer.write("<td><a href=\""+diff[2]+"\" target=\"classFrame\"><img src=\"bars/"+diff[1]+".png\" border=\"0\" title=\"diff to "+diff[0]+"\"></a>");
                            writer.newLine();
                        } else {
                            writer.write("<td></td>");
                            writer.newLine();
                        }
                        writer.write("</tr>");
                        writer.newLine();
                    }
                    writer.write("</table></body></html>");
                    writer.close();
                }
            }

/*
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
*/

/*
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
*/

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
					case '-':
						versionParts.add(
								VersionPart.newVersionPart(
									tmp.toString(),
									new Character(ch)
									)
								);
                        tmp.setLength(0);
                        break;
                    default:
                        tmp.append(ch);
                        break;
                }
            }
			versionParts.add(
				VersionPart.newVersionPart(
					tmp.toString(),
					null
					)
				);
        }

        public int compareTo(Object o) {
            return compareTo((Version) o);
        }

        public int compareTo(Version o) {
            Iterator i = this.versionParts.iterator();
            Iterator j = o.versionParts.iterator();
            while(i.hasNext() && j.hasNext()) {
                VersionPart thisVal = (VersionPart) i.next();
                VersionPart oVal = (VersionPart) j.next();
				int ret = thisVal.compareTo(oVal);
				if(ret != 0) {
					return ret;
				}
            }
            if(i.hasNext()) {
				if(((VersionPart)i.next()).isString()) {
					return 1;
				} else {
					return -1;
				}
            } else if(j.hasNext()) {
				if(((VersionPart)j.next()).isString()) {
					return -1;
				} else {
					return 1;
				}
            } else {
                return 0;
            }
        }

        public String toString() {
            StringBuffer ret = new StringBuffer();
            Iterator i = versionParts.iterator();
            while(i.hasNext()) {
                ret.append(i.next());
            }
            return ret.toString();
        }
    }

	private static class VersionPart implements Comparable {

		public static VersionPart 
			newVersionPart(String data, Character seperator) 
		{
			try {
				return new VersionPart(new Integer(data), seperator);
			} catch (NumberFormatException nfe) {
				return new VersionPart(data, seperator);
			}
		}

		private Comparable data;
		private Character separator;

		public VersionPart(Comparable data, Character separator) {
			this.data = data;
			this.separator = separator;
		}

		public int compareTo(Object o) {
			return compareTo((VersionPart)o);
		}

		public int compareTo(VersionPart o) {
			if( 
					(isString() && o.isString()) || 
					(isInteger() && o.isInteger())
			  ) 
			{
				return - data.compareTo(o.data);
			} else if(isString()) {
				return 1;
			} else {
				return -1;
			}
		}

		public boolean isString() {
			return data instanceof String;
		}

		public boolean isInteger() {
			return data instanceof Integer;
		}

		public String toString() {
			if(separator != null) {
				return data.toString() + separator;
			} else {
				return data.toString();
			}
		}
	}

/*
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
*/

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
