package org.cyberiantiger.mudclient.input;

import java.util.List;
import java.util.LinkedList;

public class InputStack {

    /**
     * List of unparsed data.
     */
    private List unparsedInput = new LinkedList();

    /**
     * Maximum recursion depth
     */
    private int maxRecursion;

    public InputStack(int maxRecursion) {
	this.maxRecursion = maxRecursion;
    }

    /**
     * Retrieve the next piece of unparsed input.
     */
    public InputItem nextItem() {
	return (InputItem) unparsedInput.remove(0);
    }

    /**
     * Do we have more items to parse.
     */
    public boolean hasMoreItems() {
	return !unparsedInput.isEmpty();
    }

    /**
     * Push an item.
     *
     * @throws RecursionException When the recursion depth exceeds maxRecursion
     */
    public void pushItem(InputItem item) {
	if (item.getDepth() >= maxRecursion) {
	    throw new RecursionException();
	}
	unparsedInput.add(0, item);
    }

    /**
     * Add an item.
     */
    public void addItem(InputItem item) {
	unparsedInput.add(item);
    }
}
