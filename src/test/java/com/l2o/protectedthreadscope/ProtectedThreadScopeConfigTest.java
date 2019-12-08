package com.l2o.protectedthreadscope;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;

@ExtendWith(MockitoExtension.class)
class ProtectedThreadScopeConfigTest {
    @Mock
    private ConfigurableListableBeanFactory beanFactory;
    @Test
    void test() {
	ProtectedThreadScopeConfig testee = new ProtectedThreadScopeConfig();
	ProtectedThreadScopeManager manager = testee.protectedThreadScopeManager();
	Assertions.assertNotNull(manager);
	testee.postProcessBeanFactory(beanFactory);
	ArgumentCaptor<Scope> scopeCaptor = ArgumentCaptor.forClass(Scope.class);
	Mockito.verify(beanFactory).registerScope(Mockito.eq("thread-protected"), scopeCaptor.capture());
	Assertions.assertTrue(scopeCaptor.getValue() instanceof ProtectedThreadScope);
	Assertions.assertFalse(((ProtectedThreadScopeManagerInternal)manager).isActive());
	try (ThreadScopeProtection tsp = manager.start()) {
	    Assertions.assertTrue(((ProtectedThreadScopeManagerInternal)manager).isActive());
	}
	Assertions.assertFalse(((ProtectedThreadScopeManagerInternal)manager).isActive());
    }

}
