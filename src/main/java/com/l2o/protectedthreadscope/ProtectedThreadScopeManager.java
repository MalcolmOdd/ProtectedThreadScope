package com.l2o.protectedthreadscope;

/**
 * A manager that is used to delimit blocks where the protected thread scope is valid.
 * Nested usage is supported through a reference count.
 */
public interface ProtectedThreadScopeManager {
    /**
     * Start a block where the thread scope is active. The returned ThreadScopeProtection object
     *  must be closed at some point to prevent memory leaks or data spills (if the same thread is reused).
     *  This is designed to be used in try with blocks (where the close method is silently called at the end of the block).
     *  Please note that the thread scope might already be active because of enclosing blocks. The data
     *  will only be collected until all blocks are closed.
     * @return
     */
    ThreadScopeProtection start();
 }
