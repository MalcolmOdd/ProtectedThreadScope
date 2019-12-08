package com.l2o.protectedthreadscope;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

/**
 * A Spring scope that uses thread-local storage but will garbage-collect the beans when all use-blocks are closed.
 * This must be combined with a proxy annotation to work properly in singletons: Spring will inject a stable proxy
 * which will delegate calls to scoped objects.
 */
public class ProtectedThreadScope implements Scope {
    public static final String SCOPE = "thread-protected";
    private ProtectedThreadScopeManagerInternal manager;
    
    public ProtectedThreadScope(ProtectedThreadScopeManagerInternal manager) {
	this.manager = manager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
	return manager.getBean(name, objectFactory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object remove(String name) {
	return manager.removeBean(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
	manager.registerDestructionCallback(name, callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object resolveContextualObject(String key) {
	return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConversationId() {
	return null;
    }
}
