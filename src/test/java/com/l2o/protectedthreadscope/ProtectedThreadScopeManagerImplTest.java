package com.l2o.protectedthreadscope;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectFactory;

@ExtendWith(MockitoExtension.class)
class ProtectedThreadScopeManagerImplTest {
    private static final String BEAN_NAME = "bean-name";
    private static final String BEAN_VALUE = "bean-value";
    private ProtectedThreadScopeManagerImpl testee;
    @Mock
    private ObjectFactory<String> objectFactory;

    @BeforeEach
    public void setUp() {
	testee = new ProtectedThreadScopeManagerImpl();
    }

    @Test
    public void testStartAndGetBean() {
	Mockito.when(objectFactory.getObject()).thenReturn(BEAN_VALUE);
	try (ThreadScopeProtection tsp1 = testee.start()) {
	    Assertions.assertSame(BEAN_VALUE, testee.getBean(BEAN_NAME, objectFactory));
	    try (ThreadScopeProtection tsp2 = testee.start()) {
		Assertions.assertSame(BEAN_VALUE, testee.getBean(BEAN_NAME, objectFactory));
	    }
	    Assertions.assertSame(BEAN_VALUE, testee.getBean(BEAN_NAME, objectFactory));
	    Assertions.assertTrue(testee.isActive());
	}
	expectException(() -> testee.getBean(BEAN_NAME, objectFactory));
	Assertions.assertFalse(testee.isActive());
    }

    @Test
    public void testGetBeanNoScope() {
	expectException(() -> testee.getBean(BEAN_NAME, objectFactory));
    }

    @Test
    public void testRemoveBeanNoScope() {
	expectException(() -> testee.removeBean(BEAN_NAME));
    }

    @Test
    public void testRemoveBean() {
	Runnable runnable = Mockito.mock(Runnable.class);
	Mockito.when(objectFactory.getObject()).thenReturn(BEAN_VALUE);
	try (ThreadScopeProtection tsp = testee.start()) {
	    testee.registerDestructionCallback(BEAN_NAME, runnable);
	    Assertions.assertSame(BEAN_VALUE, testee.getBean(BEAN_NAME, objectFactory));
	    testee.removeBean(BEAN_NAME);
	    Assertions.assertSame(BEAN_VALUE, testee.getBean(BEAN_NAME, objectFactory));
	    testee.removeBean(BEAN_NAME);
	}
	Mockito.verifyZeroInteractions(runnable);
	Mockito.verify(objectFactory, Mockito.times(2)).getObject();
    }

    @Test
    public void testRegisterDestructionCallback() {
	Runnable runnable = Mockito.mock(Runnable.class);
	try (ThreadScopeProtection tsp = testee.start()) {
	    testee.registerDestructionCallback(BEAN_NAME, runnable);
	    Mockito.verifyZeroInteractions(runnable);
	}
	Mockito.verify(runnable).run();
    }

    private void expectException(Runnable runnable) {
	try {
	    runnable.run();
	    fail("Should have thrown an IllegalStateException");
	} catch (IllegalStateException iex) {
	    Assertions.assertEquals("Thread scope bean used outside of protected block. Proxy or scope might be missing.",
		    iex.getLocalizedMessage());
	}
    }
}
