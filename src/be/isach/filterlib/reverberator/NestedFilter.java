package be.isach.filterlib.reverberator;

import be.isach.filterlib.filters.*;
import be.uliege.montefiore.oop.audio.Filter;

/**
 * Implements a nested filter, as required for better structuring the
 * reverberator filter. This represents a given filter inside an
 * all-pass filter encompassing it.
 */
public class NestedFilter extends CompositeFilter {

    /**
     * Initializes a new simply nested filter with the given parameters.
     *
     * @param nestedFilter The nest filter
     * @param delay        The delay following the nest delay.
     * @param gain         The gain around the nested filter.
     */
    public NestedFilter(Filter nestedFilter, int delay, double gain) {
        super(1, 1);

        AdditionFilter startAdd = new AdditionFilter();
        DelayFilter delayFilter = new DelayFilter(delay);
        GainFilter gainForward = new GainFilter(-gain);
        GainFilter gainBackward = new GainFilter(gain);
        AdditionFilter endAdd = new AdditionFilter();

        addBlock(startAdd);
        addBlock(nestedFilter);
        addBlock(delayFilter);
        addBlock(gainForward);
        addBlock(gainBackward);
        addBlock(endAdd);

        connectInputToBlock(0, startAdd, 0);
        connectBlockToBlock(gainBackward, 0, startAdd, 1);
        connectInputToBlock(0, gainForward, 0);
        connectBlockToBlock(gainForward, 0, endAdd, 0);
        connectBlockToBlock(endAdd, 0, gainBackward, 0);
        connectBlockToBlock(startAdd, 0, nestedFilter, 0);
        connectBlockToBlock(nestedFilter, 0, delayFilter, 0);
        connectBlockToBlock(delayFilter, 0, endAdd, 1);
        connectBlockToOutput(endAdd, 0, 0);
    }
}
