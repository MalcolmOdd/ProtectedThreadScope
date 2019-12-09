package com.l2o.protectedthreadscope;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

class ProtectedThreadScopeGlobalTest {
    private interface Storage {
	void setValue(String value);
	String getValue();
    }

    public static class StorageImpl implements Storage {
	String value;
	@Override
	public String getValue() {
	    return value;
	}
	@Override
	public void setValue(String value) {
	    this.value = value;
	}
    }

    public static class StorageWrapper {
	@Autowired
	private Storage storage;

	public String getValue() {
	    return storage.getValue();
	}
    }

    @Configuration
    @Import(ProtectedThreadScopeConfig.class)
    public static class Config {
	@Bean
	@Scope(value = ProtectedThreadScope.SCOPE, proxyMode = ScopedProxyMode.INTERFACES)
	public Storage converter() {
	    return new StorageImpl();
	}

	@Bean
	public StorageWrapper converterWrapper() {
	    return new StorageWrapper();
	}
    }

    @Test
    void testIntegration() {
	try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class)) {
	    StorageWrapper storageWrapper = context.getBean(StorageWrapper.class);
	    Storage storage = context.getBean(Storage.class);
	    ProtectedThreadScopeManager psm = context.getBean(ProtectedThreadScopeManager.class);
	    try (ThreadScopeProtection tsp = psm.start()) {
		storage.setValue("Bob");
		Assert.assertEquals("Bob", storageWrapper.getValue());
	    }
	}
    }

    @Test
    void testNoBlock() {
	Assertions.assertThrows(BeanCreationException.class, () -> {
	try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class)) {
	    Storage storage = context.getBean(Storage.class);
	    storage.setValue("Bob");
	}});
    }
}
