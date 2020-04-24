package be.isach.filterlib.filters;

import be.uliege.montefiore.oop.audio.Filter;

/**
 * Implements an Echo Filter, as detailed in the project statement.
 */
public class EchoFilter extends CompositeFilter {

    /**
     * Initializes an echo filter, a special type of composite filter.
     *
     * @param gain  The gain of the all-pass filter.
     * @param delay The delay of the all-pass filter.
     */
    public EchoFilter(double gain, int delay) {
        super(1, 1);

        // Build the composite filter:
        Filter gainFilter = new GainFilter(gain);
        Filter delayFilter = new DelayFilter(delay);
        Filter add = new AdditionFilter();

        addBlock(gainFilter);
        addBlock(delayFilter);
        addBlock(add);

        connectInputToBlock(0, add, 0);
        connectBlockToBlock(add, 0, gainFilter, 0);
        //connectBlockToBlock(delayFilter, 0, gainFilter, 0);
        connectBlockToBlock(gainFilter, 0, add, 1);
        connectBlockToOutput(add, 0, 0);
    }
}

