package be.isach.filterlib.filters;

import be.uliege.montefiore.oop.audio.Filter;
import be.uliege.montefiore.oop.audio.FilterException;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Stores data for an internal filter in a composite filter.
 * Stores connections to adjacent filters, and a buffer input.
 */
public class FilterBlock {

    /**
     * Filter the block is associated to.
     */
    private final Filter filter;

    /**
     * null = empty (unavailable).
     * Contains available inputs.
     * As filters are not all accessed at same time, a buffer stores
     * inputs to ensure it memorizes previous calls.
     */
    private final Double[] inputBuffer;

    /**
     * Filters it takes its inputs from.
     * key: the filter.
     * value: the output of the key that points to this block.
     */
    private final HashMap<FilterBlock, Integer> inputBlocks;

    /**
     * Filters it points to
     * key: the filter.
     * value: the input of the key that this block points to.
     */
    private final HashMap<FilterBlock, Integer> outputBlocks;

    /**
     * owning composite filter
     */
    private final CompositeFilter compositeFilter;

    /**
     * Flag for visited or not.
     */
    private boolean visited;

    /**
     * Initializes a block data for a given filter.
     *
     * @param filter the filter to associate the new block data to.
     */
    public FilterBlock(Filter filter, CompositeFilter compositeFilter) {
        this.filter = filter;
        this.inputBuffer = new Double[filter.nbInputs()];

        // By default, the inputs are unavailable.
        Arrays.fill(inputBuffer, null);

        this.inputBlocks = new HashMap<>();
        this.outputBlocks = new HashMap<>();

        this.compositeFilter = compositeFilter;
        this.visited = false;
    }

    /**
     * Returns the input buffer and clears it.
     *
     * @return input to use
     */
    public double[] consumeBuffer() {
        double[] input = new double[inputBuffer.length];
        for (int i = 0; i < inputBuffer.length; i++)
            input[i] = inputBuffer[i];
        Arrays.fill(inputBuffer, null);
        return input;
    }

    /**
     * @return {@code true} if all inputs are available,
     *         {@code false} otherwise.
     */
    public boolean allInputsAvailable() {
        if (filter instanceof DelayFilter
                && !((DelayFilter) filter).needsUpdating())
            return true;

        for (Double d : inputBuffer)
            if (d == null) return false;
        return true;
    }

    public void compute() throws FilterException {
        if(isVisited()) return;

        // This means the called block is still waiting for another
        // input to come from another call. Cancel this call.
        if (!allInputsAvailable()) {
            return;
        }

        // If true, we're on the first phase of the computing
        // where we pop all delay filters that have technically ready
        // inputs (previous ones) all the time.
        // Enqueuing them happens later.
        boolean poppingADelayFilter = filter instanceof DelayFilter
                && !((DelayFilter) filter).needsUpdating();

        double[] output;

        // If we're not on the first phase
        if (!poppingADelayFilter) {
            double[] input = consumeBuffer();

            // Read the output.
            // If the filter is a delay filter that needs updating (because it's
            // in a loop and was already popped), then just enqueue a new value.
            // Otherwise, just process it normally.
            if (filter instanceof DelayFilter
                    && ((DelayFilter) filter).needsUpdating()) {
                ((DelayFilter) filter).enqueue(input);
                return;
            } else {
                output = filter.computeOneStep(input);
            }

            // Mark the filter as visited in the current cycle.
            setVisited(true);
        } else { // just pop the delay filter otherwise.
            output = new double[]{((DelayFilter) filter).pop()};
        }

        // Call the next filters.
        for (FilterBlock nextBlock : getOutputBlocks().keySet()) {
            Filter next = nextBlock.getFilter();
            if (next != compositeFilter) {

                // Insert our output as an input in the next filter's
                // input buffer
                int outputIndex = nextBlock.getInputBlocks().get(this);
                nextBlock.addToBuffer(output[outputIndex],
                        getOutputBlocks().get(nextBlock));

                nextBlock.compute();
            } else {
                Integer outputIndex = compositeFilter.getSelfBlock()
                        .getOutputBlocks().get(this);

                compositeFilter.insertOutput(outputBlocks.get(nextBlock),
                        output[outputIndex]);
                // end of sub-cycle, reached the final output for this
                // chain.
            }
        }
    }

    /**
     * Adds an input to the input buffer, to mark it available and store it
     * for later usage.
     * @param sample The newly available input.
     * @param slot The input slot it takes place in.
     */
    public void addToBuffer(double sample, int slot) {
        inputBuffer[slot] = sample;
    }

    /**
     * Adds a filter this filter takes its inputs from.
     * @param subBlock the pointing filter.
     * @param slot the pointing filter's output slot to this filter.
     */
    public void setFilterAsInput(FilterBlock subBlock, int slot) {
        this.inputBlocks.putIfAbsent(subBlock, slot);
    }

    /**
     * Adds a filter this filter streams its outputs to.
     * @param subBlock the pointed filter.
     * @param slot the pointed filter's slot.
     */
    public void setFilterAsOutput(FilterBlock subBlock, int slot) {
        this.outputBlocks.putIfAbsent(subBlock, slot);
    }

    /**
     * @return the filter the block data is associated to.
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * @return Filters it takes its inputs from.
     */
    public HashMap<FilterBlock, Integer> getInputBlocks() {
        return inputBlocks;
    }

    /**
     * @return Filters it points to.
     */
    public HashMap<FilterBlock, Integer> getOutputBlocks() {
        return outputBlocks;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }
}
