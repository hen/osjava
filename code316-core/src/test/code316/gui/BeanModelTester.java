package code316.gui;


import java.util.Arrays;

import junit.framework.TestCase;


public class BeanModelTester extends TestCase {
    public void testparsePropertyNames() {
        String names[] = BeanTableModel.parsePropertyNames("message.sender.address");
        
        assertEquals(3, names.length);
        assertEquals("message", names[0]);
        assertEquals("sender", names[1]);
        assertEquals("address", names[2]);

        names = BeanTableModel.parsePropertyNames("message");
        
        assertEquals(1, names.length);
        assertEquals("message", names[0]);
    }
    
    public void testGetValueAt() throws Exception {
        class Deeper {
            public int getNumber() {
                return 7;
            }
            
            public String getCategory() {
                return "box";
            }
        }

        class SampleItem {
            public SampleItem getItem() {
                return this;
            }
            
            public String getName() {
                return "tom";
            }
            
            public int getNumber() {
                return 10;
            }
            
            public boolean isOk() {
                return false;
            }
            
            public Deeper getDeeper() {
                return new Deeper();
            }
        }
        
        ModelDescription md = 
            new ModelDescription(
                new String[]{
                    "item.name",                     "item.number", 
                    "ok",
                    "deeper.number",
                    "deeper.category",
                    "deeper.category.length()"
                    });
        BeanTableModel model = new BeanTableModel(md);
        model.add(new SampleItem());
        
        Object o = model.getValueAt(0, 0);
        assertEquals("tom", o);
        
        o = model.getValueAt(0, 1);
        assertEquals(new Integer(10), o);
        
        o = model.getValueAt(0, 0);
        assertEquals("tom", o);
        
        o = model.getValueAt(0, 2);
        assertEquals(Boolean.FALSE, o);
        
        o = model.getValueAt(0, 3);
        assertEquals(new Integer(7), o);

        o = model.getValueAt(0, 4);
        assertEquals("box", o);

        o = model.getValueAt(0, 5);
        assertEquals(new Integer(3), o);
    }
    
    public void testAddList() {
        String [] names = new String[]{"dave", "jakob", "bethany", "betty"};
        ModelDescription md = new ModelDescription(new String[]{"toString()"});
        BeanTableModel model = new BeanTableModel(md);
        
        model.add(Arrays.asList(names));
        
        assertEquals(4, model.getRowCount());
        assertEquals("dave", model.get(0));
        assertEquals("jakob", model.get(1));
        assertEquals("bethany", model.get(2));
        assertEquals("betty", model.get(3));
        
        assertEquals(1, model.getColumnCount());
        
        assertEquals(4, model.getBeans().size());
        model.clear();
        assertEquals(0, model.getRowCount());
        
        model.setData(Arrays.asList(names));        
        assertEquals(4, model.getRowCount());
        assertEquals("dave", model.get(0));
        assertEquals("jakob", model.get(1));
        assertEquals("bethany", model.get(2));
        assertEquals("betty", model.get(3));
        
        assertEquals(1, model.getColumnCount());
        
        assertEquals(4, model.getBeans().size());
        model.clear();
        assertEquals(0, model.getRowCount());
        
    }
}
