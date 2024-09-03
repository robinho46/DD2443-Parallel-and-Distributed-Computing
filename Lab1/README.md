- Group 15
- Ezaz, Isahq and Yurt, Robin

## Task 1: Simple Synchronization

### Task 1a: Race conditions
Source files:

- `task2/MainA.java` (main file)

To compile and execute:
```
javac MainA.java
java Main
```

Run the program locally with 4 threads. What results do you expect?
- Between 1000000 and 4000000.

### Task 1b: Synchronized keyword
Source files:

- `task2/MainB.java` (main file)

To compile and execute:
```
javac MainB.java
java Main
```

### Task 1c: Synchronization performance
Source files:

- `task2/MainC.java` (main file)

To compile and execute:
```
javac MainC.java
java Main <N>
```
Where `N` is number of threads to execute with.

In figure 1, we see how the execution time scaled with the number of threads
...

![My plot for task 2c](data/task2c.png)


## Task 2: Guarded blocks using wait()/notify()

## Task 3: Producer-Consumer Buffer using Condition Variables

## Task 4: Counting Semaphore

## Task 5: Dining Philosophers
