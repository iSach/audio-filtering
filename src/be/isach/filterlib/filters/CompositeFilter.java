package be.isach.filterlib.filters;

import be.isach.filterlib.BlockData;
import be.uliege.montefiore.oop.audio.Filter;
import be.uliege.montefiore.oop.audio.FilterException;

import java.util.*;

public class CompositeFilter implements Filter {

    /**
     * Stores the amount of inputs of the whole composite filter.
     */
    private int inputsAmount;

    /**
     * Stores the amount of outputs of the whole composite filter.
     */
    private int outputsAmount;

    /**
     * Stores the different filters in the composite filter, and their
     * corresponding block data.
     */
    private Map<Filter, BlockData> filtersData;

    /**
     * Filters the inputs of the composite filter point to.
     * key: the filter.
     * value: the input of the block pointed by the input of the composite.
     */
    private HashMap<Filter, Integer> inputFilters;

    /**
     * Filters it points to
     * key: the filter.
     * value: the output of the block that points to the outputs
     * of the composite.
     */
    private HashMap<Filter, Integer> outputFilters;

    /**
     * Current output being built.
     */
    private Double[] output;

    private Map<Filter, Map<DelayFilter, Integer>> loopsData;

    private Set<Filter> visited;

    private Set<DelayFilter> poppedDelayFilters;

    public CompositeFilter(int inputsAmount, int outputsAmount) {
        this.inputsAmount = inputsAmount;
        this.outputsAmount = outputsAmount;

        this.filtersData = new HashMap<>();

        this.inputFilters = new HashMap<>();
        this.outputFilters = new HashMap<>();

        this.visited = new HashSet<>();
        this.poppedDelayFilters = new HashSet<>(); // TODO Check by length queue
        this.loopsData = new HashMap<>();
    }

    public void addBlock(Filter f) {
        if (filtersData.containsKey(f)) return;

        BlockData blockData = new BlockData(f);
        filtersData.put(f, blockData);
    }

    public void connectBlockToBlock(Filter f1, int o1, Filter f2, int i2) {
        // Check if both filters are already added, otherwise stop.
        if (!filtersData.containsKey(f1) || !filtersData.containsKey(f2)) {
            return;
        }

        BlockData data1 = filtersData.get(f1);
        BlockData data2 = filtersData.get(f2);
        data1.setFilterAsOutput(f2, i2);
        data2.setFilterAsInput(f1, o1);
    }

    public void connectBlockToOutput(Filter f1, int o1, int o2) {
        // Check if the filter is already added, otherwise stop.
        if (!filtersData.containsKey(f1)) {
            return;
        }

        BlockData data = filtersData.get(f1);
        data.setFilterAsOutput(this, o2);
        outputFilters.putIfAbsent(f1, o1);
    }

    public void connectInputToBlock(int i1, Filter f2, int i2) {
        // Check if the filter is already added, otherwise stop.
        if (!filtersData.containsKey(f2)) {
            return;
        }

        BlockData data = filtersData.get(f2);
        data.setFilterAsInput(this, i1);
        inputFilters.putIfAbsent(f2, i2);
    }

    private void checkForLoops(Filter f) {
        if (loopsData.containsKey(f)
                && loopsData.get(f) != null) {

        }
    }

    private void checkForLoopsAux(Filter sourceFilter, Filter f) {

    }

    @Override
    public double[] computeOneStep(double[] input) throws FilterException {
        if (input == null) {
            throw new FilterException("Specified input array points to null.");
        }

        if (input.length != nbInputs()) {
            throw new FilterException("Invalid number of inputs. Expected: "
                    + nbInputs() + ", Got: " + input.length);
        }

        if (this.output == null) {
            this.output = new Double[nbOutputs()];
        } else {
            Arrays.fill(this.output, null);
        }

        for (Filter f : inputFilters.keySet()) {
            BlockData data = filtersData.get(f);
            int compositeInputIndex = data.getInputFilters().get(this);
            data.addToBuffer(input[compositeInputIndex], inputFilters.get(f));
            computeAux(f);
        }

        for (int i = 0; i < nbOutputs(); i++) {
            if (output[i] == -1) {
                //  System.out.println("?????????");
                //  System.out.println(Collections.singletonList(output));
            }
        }

        visited.clear();

        double[] outtt = new double[output.length];
        for (int i = 0; i < outtt.length; i++)
            outtt[i] = output[i];

        return outtt;
    }

    public void computeAux(Filter f) throws FilterException {
        if (visited.contains(f)) return;

        BlockData data = filtersData.get(f);

        // This means the called block is still waiting for another
        // input to come from another call. Cancel this call.
        if (!data.allInputsAvailable()) {
            fetchMissingInputsFromDelays(f);
            return;
        }

        Double[] doub = data.consumeBuffer();
        double[] input = new double[f.nbInputs()];
        for (int i = 0; i < input.length; i++)
            input[i] = doub[i];

        double[] output;
        if (f instanceof DelayFilter && poppedDelayFilters.contains((DelayFilter) f)) {
            ((DelayFilter) f).enqueue(input);
            return;
        } else {
            output = f.computeOneStep(input);
        }

        visited.add(f);

        for (Filter next : data.getOutputFilters().keySet()) {
            if (next != this) {
                BlockData nextData = filtersData.get(next);
                int outputIndex = nextData.getInputFilters().get(f);
                nextData.addToBuffer(output[outputIndex],
                        data.getOutputFilters().get(next));
                computeAux(next);
            } else {
                Integer outputIndex = outputFilters.get(f);
                this.output[data.getOutputFilters().get(next)] =
                        output[outputIndex];
                // end of sub-cycle
            }
        }
    }

    /**
     * Calls inputs from delay filters pointing to this block.
     * Should only happen when a loop points back to this block.
     * <p>
     * Does nothing if all inputs are available.
     */
    public void fetchMissingInputsFromDelays(Filter f) throws FilterException {
        BlockData data = filtersData.get(f);

        if (data.allInputsAvailable()) return;

        for (Filter delay : filtersData.keySet()) {
            if (delay instanceof DelayFilter) {
                BlockData delayData = filtersData.get(delay);
                Filter next = delayData.getOutputFilters().keySet().
                        iterator().next();

                while (next != f && !(next instanceof CompositeFilter)) {
                    BlockData d = filtersData.get(next);
                    next = d.getOutputFilters().keySet().iterator().next();
                }

                if (next == f) {
                    next = delayData.getOutputFilters().keySet().
                            iterator().next();
                    BlockData d = filtersData.get(next);
                    //System.out.println("\n\n");
                    //System.out.println(next);
                    //System.out.println( d.allInputsAvailable());
                    int bufferIndex = delayData.getOutputFilters().get(next);
                    //System.out.println(bufferIndex);
                    double popped = ((DelayFilter) delay).pop();
                    poppedDelayFilters.add((DelayFilter) delay);
                    //System.out.println(popped);
                    //System.out.println(Arrays.toString(d.getInputBuffer()));
                    d.addToBuffer(popped, bufferIndex);
                    //System.out.println(Arrays.toString(d.getInputBuffer()));
                    //System.out.println(d.allInputsAvailable() + "\n\n");
                    computeAux(next);
                }
            }
        }

       /*for (int i = 0; i < data.getInputBuffer().length; i++) {
            if (data.getInputBuffer()[i] != -1) continue;

            for (Filter fil : data.getInputFilters().keySet()) {
                if(fil instanceof CompositeFilter) continue;

                if (!(fil instanceof DelayFilter)) continue;

                BlockData blockData = filtersData.get(fil);

                if (blockData.getOutputFilters().containsKey(f)
                        && blockData.getOutputFilters().get(f) == i) {
                    data.addToBuffer(((DelayFilter) fil).pop(), i);
                }
            }
        }*/
    }

    @Override
    public int nbInputs() {
        return inputsAmount;
    }

    @Override
    public int nbOutputs() {
        return outputsAmount;
    }

    @Override
    public void reset() {
        for (Filter filter : filtersData.keySet())
            filter.reset();
        output = new Double[nbOutputs()];
        visited.clear();
    }
}
