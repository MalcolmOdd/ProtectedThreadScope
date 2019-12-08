package com.l2o.protectedthreadscope;

import org.springframework.beans.factory.ObjectFactory;

/**
 * Interface for library-internal features of ProtectedThreadScopeManager.
 */
public interface ProtectedThreadScopeManagerInternal extends ProtectedThreadScopeManager {
    /**
     * Gets a bean from the scope. This might throw an IllegalStateException if no block is active.
     * @param name The name of the bean
     * @param objectFactory The object factory to use for new beans
     * @return The bean
     */
    Object getBean(String name, ObjectFactory<?> objectFactory);
    /**
     * Remove a bean from the scope. This might throw an IllegalStateException if no block is active.
     * @param name The name of the bean
     * @return The bean
     */
    Object removeBean(String name);
    /**
     * Register a desctruction callback for a bean
     * @param name The name of the bean
     * @param callback The callback runnable
     */
    void registerDestructionCallback(String name, Runnable callback);
    /**
     * Test if the scope is active.
     * @return true if the scope is active, false otherwise.
     */
    boolean isActive();
}
