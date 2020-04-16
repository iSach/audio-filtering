package be.isach.filterlib;

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
    private Filter filter;

    /**
     * null = empty (unavailable).
     * Contains available inputs.
     * As filters are not all accessed at same time, a buffer stores
     * inputs to ensure it memorizes previous calls.
     */
    private Double[] inputBuffer;

    /**
     * Filters it takes its inputs from.
     * key: the filter.
     * value: the output of the key that points to this block.
     */
    private HashMap<Filter, Integer> inputFilters;

    /**
     * Filters it points to
     * key: the filter.
     * value: the input of the key that this block points to.
     */
    private HashMap<Filter, Integer> outputFilters;

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

    public boolean allInputsAvailable() {
        for (Double d : inputBuffer)
            if (d == null) return false;
        return true;
    }

    public void addToBuffer(double sample, int pos) {
        inputBuffer[pos] = sample;
    }

    public void setFilterAsInput(Filter f, int pos) {
        this.inputFilters.putIfAbsent(f, pos);
    }

    public void setFilterAsOutput(Filter f, int pos) {
        this.outputFilters.putIfAbsent(f, pos);
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
