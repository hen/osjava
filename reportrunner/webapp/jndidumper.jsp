<%@ page import="javax.sql.DataSource" %>
<%@ page import="javax.naming.*" %>
<%
        try {
            Context ctxt = new InitialContext();
            Context envCtxt = (Context) ctxt.lookup("java:comp/env");
            out.write("ds: "+envCtxt.lookup("jdbc/EuroDev2DS"));
            /*
            Context root = (Context) ctxt.lookup("java:"); // jdbc/EuroDev2DS");
            out.write("r: "+root+"<br>");
            NamingEnumeration ne = root.list("");
            while(ne.hasMore()) {
                Object o = ne.next();
                out.write("o: "+o+"<br>");
                if(o instanceof Context) {
                    Context c = (Context) o;
                    NamingEnumeration n2e = c.list("");
                    while(n2e.hasMore()) {
                        Object o2 = ne.next();
                        out.write("o2: "+o2+"<br>");
                    }
                }
            }
            out.write("n? "+ctxt.lookup("java:jdbc/EuroDev2DS"));
            */
        } catch(NamingException ne) {
            out.write(""+ne);
        }
%>
