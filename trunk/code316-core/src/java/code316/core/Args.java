package code316.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Command line argument helper class.
 *
 * @author david.petersheim
 *
 */

public class Args {
    private static final String SWITCH_CHAR = "-";
    static class Argument {
        private String name;
        private String[] vals = {""};
        private short valCount = 0;

        public Argument(String name) {
            if (name == null) {
                throw new IllegalArgumentException(
                    "invalid value for name: " + name);
            }

            this.name = name;
        }

        public Argument(String name, String value) {
            this(name);
            addValue(value);
        }

        public void addValue(String val) {
            if ( this.valCount == vals.length ) {
                String na[] = new String[vals.length + 1];
                System.arraycopy(this.vals, 0, na, 0, this.vals.length);
                this.vals = na;
            }

            this.vals[this.valCount] = val;
            this.valCount++;
        }


        /**
         * Returns the first element of the backing values array.
         * If there is only one value, then this is what you want.
         * If there are multiple values and you want them all, call
         * getValues to get the whole array values.
         * <p>
         * To check how many values this argument has, call valCount
         */
        public String getValue() {
            return this.vals[0];
        }


        /**
         * Returns the values of this argument as an array of
         * of Strings.
         */
        public String[] getValues() {
            return this.vals;
        }

        /**
         * Returns a space separated list of this Arguments values.
         */
        public String getValueString() {
            StringBuffer line = new StringBuffer();
            for (int i = 0; i < this.vals.length; i++) {
                line.append(this.vals[i]).append(" ");
            }
            return line.toString().trim();
        }

        public int valueCount() {
            return this.valCount;
        }

        public String getName() {
            return this.name;
        }

        public String toString() {
            StringBuffer me = new StringBuffer();
            me.append("[").append(super.toString())
            .append(",vals=").append(this.vals)
            .append(",valCount=").append(this.valCount)
            .append(",name=").append(this.name)
            .append("]");

            return me.toString();
        }
    }

    /**
     * Parses the command line arguments contained in the supplied array. If the
     * array is empty an empty Properties object is returned.  If the Properties
     * object is null null is returned.
     *
     * @param args - array to parse
     * @param offset - index at which parsing should begin
     * @param - max properties to parse
     * 
     * @exception ArrayIndexOutOfBoundsException if offset >= length
     * @exception RuntimeException If there is an error while parsing
     */
    public static Properties parse(String args[], int offset, int length) {
        if ( args == null ) {
            // ltdawpndwi
            return null;
        }

        Properties props = new Properties();

        if ( length == 0 ) {
            return props;
        }
        
        if ( offset >= length ) {
            throw new ArrayIndexOutOfBoundsException();
        }

        List parsed = new ArrayList();
        Argument arg = null;

        for (int i = offset; i < length; i++) {
            String a = args[i].trim();

            if ( a.startsWith(SWITCH_CHAR) ) {
                String name = a.substring(1);

                arg = new Argument(name);
                parsed.add(arg);

                // yes we put it in twice, but the first put
                // ensures that the last
                // arg is put in, even if it's a flag.
                props.put(arg.getName(), arg.getValue());
                continue;
            }
            else {
                if ( arg == null ) {
                    System.err.println("ignoring argument with no '" + SWITCH_CHAR + "'");
                    continue;
                }
            }

            arg.addValue(a);

            Iterator tor = parsed.iterator();
            while (tor.hasNext()) {
                Argument key = (Argument) tor.next();
                if ( arg.valCount == 1) {
                    props.put(arg.getName(), arg.getValue());
                }
                else {
                    props.put(arg.getName(), arg.getValues());
                }
            }
        }

        return props;
    }

    /**
     * Parses the command line arguments contained in the supplied array. If the
     * array is empty an empty Properties object is returned.  If the Properties
     * object is null null is returned.
     *
     * @param args - array to parse
     * 
     * @exception RuntimeException If there is an error while parsing
     */
    public static Properties parse(String args[]) {
        if ( args == null ) {
            // ltdawpndwi
            return null;
        }        
        
        return parse(args, 0, args.length);
    }
}
