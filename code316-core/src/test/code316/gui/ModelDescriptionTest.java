package code316.gui;

import code316.beans.BeanProperty;
import code316.beans.BeanUtil;
import junit.framework.TestCase;


public class ModelDescriptionTest extends TestCase {
    static class Test {
        private int id;
        private String name;
        
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }
    public void testCreation() {
        BeanProperty props[] = BeanUtil.getProperties(Test.class);
        
        ModelDescription md = new ModelDescription(props);
    }
}
