package be.isach.filterlib.util;

import be.isach.filterlib.filters.CompositeFilter;
import be.uliege.montefiore.oop.audio.Filter;

/**
 * Tool to easily build a cascade of several filters.
 */
public class CascadingFiltersBuilder {

    /**
     * Currently being built composite filter.
     */
    private final CompositeFilter beingBuilt;
    /**
     * Linked chain (cascade)'s tail.
     */
    private Filter lastAdded;

    /**
     * Initializes the builder.
     */
    public CascadingFiltersBuilder() {
        this.beingBuilt = new CompositeFilter(1, 1);
        this.lastAdded = null;
    }

    /**
     * Adds an filter to the cascade.
     */
    public void add(Filter filter) {
        beingBuilt.addBlock(filter);
        if (lastAdded != null)
            beingBuilt.connectBlockToBlock(lastAdded, 0, filter, 0);
        else
            beingBuilt.connectInputToBlock(0, filter, 0);
        lastAdded = filter;
    }

    /**
     * @return the finished composite filter.
     */
    public CompositeFilter build() {
        beingBuilt.connectBlockToOutput(lastAdded, 0, 0);
        return beingBuilt;
    }
}
