package be.isach.filterlib.util;

import be.isach.filterlib.filters.AllPassFilter;

/**
 * Tool to easily build a cascade of several all-pass filters.
 */
public class CascadingAllPassFiltersBuilder extends CascadingFiltersBuilder {

    /**
     * Adds an all pass filter to the cascade, with the given parameters.
     * @param gain The new filter's gain.
     * @param delay The new filter's delay.
     */
    public void add(double gain, int delay) {
        AllPassFilter newFilter = new AllPassFilter(gain, delay);
        super.add(newFilter);
    }
}
