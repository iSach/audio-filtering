package be.isach.filterlib.util;

import be.isach.filterlib.filters.DelayFilter;
import be.uliege.montefiore.oop.audio.Filter;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Stores data for an internal filter in a composite filter.
 * Stores connections to adjacent filters, and a buffer input.
 */
public class BlockData {

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
    private final HashMap<Filter, Integer> inputFilters;

    /**
     * Filters it points to
     * key: the filter.
     * value: the input of the key that this block points to.
     */
    private final HashMap<Filter, Integer> outputFilters;

    /**
     * Initializes a block data for a given filter.
     *
     * @param filter the filter to associate the new block data to.
     */
    public BlockData(Filter filter) {
        this.filter = filter;
        this.inputBuffer = new Double[filter.nbInputs()];

        // By default, the inputs are unavailable.
        Arrays.fill(inputBuffer, null);

        this.inputFilters = new HashMap<>();
        this.outputFilters = new HashMap<>();
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
                && !((DelayFilter) filter).needsUpdating()
                && !((DelayFilter) filter).isEmpty())
            return true;

        for (Double d : inputBuffer)
            if (d == null) return false;
        return true;
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
     * @param filter the pointing filter.
     * @param slot the pointing filter's output slot to this filter.
     */
    public void setFilterAsInput(Filter filter, int slot) {
        this.inputFilters.putIfAbsent(filter, slot);
    }

    /**
     * Adds a filter this filter streams its outputs to.
     * @param filter the pointed filter.
     * @param slot the pointed filter's slot.
     */
    public void setFilterAsOutput(Filter filter, int slot) {
        this.outputFilters.putIfAbsent(filter, slot);
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
    public HashMap<Filter, Integer> getInputFilters() {
        return inputFilters;
    }

    /**
     * @return Filters it points to.
     */
    public HashMap<Filter, Integer> getOutputFilters() {
        return outputFilters;
    }
}
