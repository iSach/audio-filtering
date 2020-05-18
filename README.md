# OOP Course (INFO0062-1) Project - Audio Filters Library

- Academic Year: 2019-2020.
- Submission Limit: 26th of April.
- Student: LEWIN Sacha (S181947)
- Grade: `18/20`

## Notes
The OOP implementation is not very good. The composite filter could be improved by distributing more tasks to external classes, especially by making BlockData not only store Data but make it really act as a sub-filter by making them send messages between each other to go through the "graph".

Another even more optimal solution is to use a model of the problem as a directed graph. This could also be implemented, and then select the appropriate traversal through it.

## Running the library
First, you need an input file. Let's consider we have an input file `input.wav`.

Then, you can compile:
`javac -d bin -cp audio.jar src/be/isach/filterlib/*/*.java src/be/isach/filterlib/Demo.java`

Now, there are two demo modes to run the code:
* Echo: `java -cp bin:audio.jar be.isach.filterlib.Demo input.wav output.wav`
* Reverberator: `java -cp bin:audio.jar be.isach.filterlib.Demo Reverb input.wav output.wav`

## Packages Descriptions
### `examples` package
The `example` package contains some example files, demonstrating some
implemented filters, and some combinations of them. For example, the cascade
of 3 all-pass filters as it was suggested to implement it.
They can be run similarly to the echo demo mentioned above;

### `filters` package
It contains all the basic filters, plus some more complex implementations
combining them.

### `reverberator` package
This package contains stuff exclusively implemented for the reverberator bonus.
This was separated from the rest for ease of reading for the corrector.

### `util` package
This package contains some utility stuff used for other classes, such as 
`BlockData.java`, used in `CompositeFilter.java`.
