package code316.beans;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import code316.gui.BeanTable;
import code316.gui.BeanTableModel;

import junit.framework.TestCase;


public class BeanTableTest extends TestCase {
    public static class Filter {
        private String name;
        private String text;
        private String type;
        private int priority;
        
        
        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
        
        public String toString() {
            StringBuffer me = new StringBuffer();
            me.append("[").append(super.toString()) 
            
            .append(",name=").append(this.name)
            .append(",type=").append(this.type)
            .append(",priority=").append(this.priority)

            .append("]");

            return me.toString();
        }

    }
    public static class MyBean {
        private int id;
        private boolean ok;
        private String name;
        public boolean isOk() {
            return ok;
        }

        public void setOk(boolean ok) {
            this.ok = ok;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;            
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
        
        public int getAge() {
            return 0;
        }
        
        public void setAge(long age) {
            
        }
    }
    public static void main(String[] args) {
        
        JFrame f = new JFrame();
        final BeanTable table = new BeanTable(Filter.class);
        table.setEditable(true);
        
        for (int i = 0; i < 50; i++) {
            Filter fi = new Filter();
            fi.setName("filter-" + i);
            fi.setText("text-" + i);
            fi.setType("class-name?-" + i);
            
            ((BeanTableModel) table.getModel()).add(fi);
        }
        
        f.getContentPane().add(new JScrollPane(table));
        f.setSize(300, 200);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.show();
        
        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run() {
                List l = ((BeanTableModel) table.getModel()).getBeans();
                
                Iterator tor = l.iterator();
                while (tor.hasNext()) {
                    System.out.println(tor.next());
                }
            }
        });
    }
    
    public void testExtractPropertyName() {
        assertEquals("Name", BeanUtil.extractPropertyName("getName"));    
        assertEquals("Name", BeanUtil.extractPropertyName("setName"));
        assertEquals("Name", BeanUtil.extractPropertyName("isName"));

        assertEquals("X", BeanUtil.extractPropertyName("getX"));    
        assertEquals("X", BeanUtil.extractPropertyName("setX"));
        assertEquals("X", BeanUtil.extractPropertyName("isX"));
    }
    
    public void testGetProperties() {
        BeanProperty properties[] = BeanUtil.getProperties(MyBean.class);
        
        assertEquals(3, properties.length);        
    }
    
    public void testGetFirstParameter() throws Exception {
        Method methods[] = MyBean.class.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if ( method.getName().equals("setAge") ) {
                assertEquals(long.class, BeanUtil.getFirstParameterType(method));
                return;
            }               
            
        }   
        assertTrue(false);     
    }
    
    public void testGetReturnType() throws Exception {
        Method methods[] = MyBean.class.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if ( method.getName().equals("getAge") ) {
                assertEquals(int.class, BeanUtil.getReturnType(method));
                return;
            }  
        }   
        assertTrue(false);     
    }
}
