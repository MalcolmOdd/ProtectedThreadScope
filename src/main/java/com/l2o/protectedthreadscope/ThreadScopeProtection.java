package com.l2o.protectedthreadscope;

/**
 * AutoCloseable interface that will guarantee that thread scope data will be kept alive until closed.
 */
public interface ThreadScopeProtection extends AutoCloseable {
    /**
     * Hide the throws clause in the super-interface.
     */
    public void close();
}
