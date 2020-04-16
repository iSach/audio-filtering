package be.isach.filterlib;

import be.uliege.montefiore.oop.audio.Filter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BlockData {

    /**
     * Filter the block is associated to.
     */
    private Filter filter;

    /**
     * -1 = empty.
     * Contains available inputs.
     */
    private Double[] inputBuffer;

    /**
     *  Filters it takes its inputs from.
     *  key: the filter.
     *  value: the output of the key that points to this block.
     */
    private HashMap<Filter, Integer> inputFilters;

    /**
     *  Filters it points to
     *  key: the filter.
     *  value: the input of the key that this block points to.
     */
    private HashMap<Filter, Integer> outputFilters;

    public BlockData(Filter filter) {
        this.filter = filter;
        this.inputBuffer = new Double[filter.nbInputs()];
        Arrays.fill(inputBuffer, null);
        this.inputFilters = new HashMap<>();
        this.outputFilters = new HashMap<>();
    }

    /**
     * Returns the input buffer and clears it.
     * @return input to use
     */
    public Double[] consumeBuffer() {
        Double[] input = inputBuffer.clone();
        Arrays.fill(inputBuffer, null);
        return input;
    }

    public boolean allInputsAvailable() {
       for(Double d : inputBuffer)
           if(d == null) return false;
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

    public Double[] getInputBuffer() {
        return inputBuffer;
    }

    public Filter getFilter() {
        return filter;
    }

    public HashMap<Filter, Integer> getInputFilters() {
        return inputFilters;
    }

    public HashMap<Filter, Integer> getOutputFilters() {
        return outputFilters;
    }
}
