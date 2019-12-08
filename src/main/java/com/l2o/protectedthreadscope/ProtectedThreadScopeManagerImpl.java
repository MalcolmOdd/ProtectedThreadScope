package com.l2o.protectedthreadscope;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.ObjectFactory;

public class ProtectedThreadScopeManagerImpl implements ProtectedThreadScopeManagerInternal {
    private ThreadLocal<ThreadData> currentThreadData = new ThreadLocal<>();

    /**
     * Gets the current ThreadLocal. If not active, throws an exception.
     * @return
     */
    private ThreadData getCurrent() {
	ThreadData retVal = currentThreadData.get();
	if (retVal == null) {
	    throw new IllegalStateException(
		    "Thread scope bean used outside of protected block. Proxy or scope might be missing.");
	}
	return retVal;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isActive() {
	return currentThreadData.get() != null;
    }

    /**
     * {@inheritDoc}
     */
    public ThreadScopeProtection start() {
	ThreadData data = currentThreadData.get();
	if (data == null) {
	    data = new ThreadData();
	    currentThreadData.set(data);
	}
	data.ref();
	return this::end;
    }

    /**
     * End a block. If all the blocks are closed, discard all the data.
     */
    private void end() {
	ThreadData data = currentThreadData.get();
	if (data.unRef()) {
	    for (Runnable callback : data.getDestructionCallbacks().values()) {
		callback.run();
	    }
	    currentThreadData.remove();
	}
    }

    /**
     * {@inheritDoc}
     */
    public Object getBean(String name, ObjectFactory<?> objectFactory) {
	ThreadData data = getCurrent();
	Map<String, Object> beans = data.getBeans();
	Object retVal = beans.get(name);
	if (retVal == null) {
	    beans.put(name, retVal = objectFactory.getObject());
	}
	return retVal;
    }

    /**
     * {@inheritDoc}
     */
    public Object removeBean(String name) {
	ThreadData data = getCurrent();
	data.getDestructionCallbacks().remove(name);
	return data.getBeans().remove(name);
    }

    /**
     * {@inheritDoc}
     */
    public void registerDestructionCallback(String name, Runnable callback) {
	ThreadData data = getCurrent();
	data.getDestructionCallbacks().put(name, callback);
    }

    /**
     * Private class to hold the data for a specific thread.
     */
    private static class ThreadData {
	private Map<String, Object> beans = new HashMap<>();
	private Map<String, Runnable> destructionCallbacks = new HashMap<>();
	private int refCount = 0;

	Map<String, Object> getBeans() {
	    return beans;
	}

	Map<String, Runnable> getDestructionCallbacks() {
	    return destructionCallbacks;
	}

	public void ref() {
	    ++refCount;
	}

	public boolean unRef() {
	    return --refCount == 0;
	}
    }
}
