import com.generationjava.io.xml.*;
import java.io.*;

public class Example1 {

    public static void main(String[] args) throws IOException {
        PrintWriter pw = new PrintWriter(System.out);

//        XmlWriter xw = new SimpleXmlWriter(pw);
        XmlWriter xw = new XmlEncXmlWriter(pw);

        xw.writeEntity("unit");
        xw.writeEntity("child");
        xw.writeEntity("grandchild");
        xw.endEntity();
        xw.endEntity();
        xw.endEntity();
        xw.getWriter().write("\n");
        xw.close();
        pw.close();
    }

}
