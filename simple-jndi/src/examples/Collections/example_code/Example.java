package example_code;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.util.List;

public class Example {

    public static void main(String[] args) {
        try {
            Context ctxt =  new InitialContext();
            List list = (List) ctxt.lookup("shopping/item");
            System.out.println("Items are: "+list);
        } catch(NamingException ne) {
            System.err.println("NamingException: "+ne.getMessage());
        }
    }

}
