package io.swapastack.gomoku;

/**
 * A simple generic class to return two values of the same type from a function.
 * @param <T>
 * @author Dennis Jehle
 */
public class Tuple<T> {

    public T first;
    public T second;

    Tuple(T first, T second) {
        this.first = first;
        this.second = second;
    }

}
