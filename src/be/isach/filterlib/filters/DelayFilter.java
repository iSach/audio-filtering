package be.isach.filterlib.filters;

import be.uliege.montefiore.oop.audio.Filter;
import be.uliege.montefiore.oop.audio.FilterException;

/**
 * Implements a delay filter.
 * <p>
 * When applied, delays the samples by k (represented by delay) values.
 * This means that the nth output is 0 if n <= k, or the (n-k)th input
 * otherwise.
 * </p>
 */
public class DelayFilter implements Filter {

    /**
     * The amount of samples by which new input samples are delayed until
     * being output.
     */
    private int delay;

    /**
     * Stores the delay delayed values, as a queue, considering how values
     * need to be read and added.
     */
    private double[] queue;

    private int counter;

    private boolean needsUpdate;

    /**
     * Initializes a new Delay Filter with the specified delay.
     *
     * @param delay The *number of samples* by which new samples are delayed
     *              before being output. (NOT IN MILLISECONDS)
     */
    public DelayFilter(int delay) {
        this.delay = delay;
        this.queue = new double[delay];
        this.counter = 0;

        this.needsUpdate = false;

        // Initialize the queue with delay 0 values.
        reset();
    }

    /**
     * A delay filter requires only 1 input.
     *
     * @return the number of inputs of the filter.
     */
    @Override
    public int nbInputs() {
        return 1;
    }

    /**
     * A delay filter produces only 1 output.
     *
     * @return the number of outputs of the filter.
     */
    @Override
    public int nbOutputs() {
        return 1;
    }

    /**
     * Pops a value from the queue and returns it.
     *
     * @return the popped double.
     */
    public double pop() {
        double d = queue[counter];
        this.needsUpdate = true;
        return d;
    }

    /**
     * Adds a value to the queue.
     *
     * @param sample the value to add to the queue.
     */
    public void enqueue(double[] sample) {
        queue[counter] = sample[0];

        this.needsUpdate = false;

        if (counter == delay - 1) {
            counter = 0;
        } else {
            counter++;
        }
    }

    /**
     * Computes one step of the filtering.
     * The instance variable queue contains the current queued and delayed
     * values to output later.
     * Once a new output is requested, we simply pop a value from that queue,
     * add the new input to the queue and return the popped value.
     *
     * @param input contains the input sample to compute the filter on.
     *              Has to contain only one input for this filter.
     * @return an array containing one output: the input, delayed by delay
     * values.
     * @throws FilterException if the input array is null or of wrong length.
     */
    @Override
    public double[] computeOneStep(double[] input) throws FilterException {
        if (input == null) {
            throw new FilterException("Specified input array points to null.");
        }

        if (input.length != nbInputs()) {
            throw new FilterException("Invalid number of inputs. Expected: "
                    + nbInputs() + ", Got: " + input.length);
        }

        double output = pop();
        enqueue(input);

        return new double[]{output};
    }

    /**
     * Resets the filter.
     * Clears the current delay queue, and re-initialize it with
     * delay times the 0 value.
     */
    @Override
    public void reset() {
        queue = new double[delay];

        for (int i = 0; i < delay; i++)
            queue[i] = 0;
    }

    /**
     * @return the delay parameter of the filter.
     */
    public int getDelay() {
        return delay;
    }

    /**
     * @return {@code true} if the filter needs to be updated,
     * {@code false} otherwise.
     */
    public boolean needsUpdating() {
        return needsUpdate;
    }
}
