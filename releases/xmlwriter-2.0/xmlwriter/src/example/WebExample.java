import java.io.*;
import com.generationjava.io.xml.*;

public class WebExample {

    public static void main(String[] args) throws IOException {
        System.out.println("Example code: ");
        example1();
        example2();
    }

    public static void example1() throws IOException {
        Writer writer = new java.io.StringWriter();
        XmlWriter xmlwriter = new SimpleXmlWriter(writer);
        xmlwriter.writeXmlVersion();
        xmlwriter.writeComment("Example of XmlWriter running");
        xmlwriter.writeEntity("person");
        xmlwriter.writeAttribute("name", "fred");
        xmlwriter.writeAttribute("age", "12");
        xmlwriter.writeEntity("phone");
        xmlwriter.writeText("4254343");
        xmlwriter.endEntity();
        xmlwriter.writeComment("Examples of empty tags");
//        xmlwriter.setDefaultNamespace("test");
        xmlwriter.writeEntity("friends");
        xmlwriter.writeEmptyEntity("bob");
        xmlwriter.writeEmptyEntity("jim");
        xmlwriter.endEntity();
        xmlwriter.writeEntityWithText("foo","This is an example.");
        xmlwriter.endEntity();
        xmlwriter.close();
        System.out.println(writer.toString());
    }
    
    public static void example2() throws IOException {
        Writer writer = new java.io.StringWriter();
        XmlWriter xmlwriter = new SimpleXmlWriter(writer);
        xmlwriter.writeEntity("person").writeAttribute("name", "fred")
                                       .writeAttribute("age", "12")
                      .writeEntity("phone").writeText("4254343").endEntity()
                      .writeEntity("friends").writeEntity("bob").endEntity()
                                             .writeEntity("jim").endEntity()
                      .endEntity()
                 .endEntity();
        xmlwriter.close();
        System.out.println(writer.toString());
    }

}
