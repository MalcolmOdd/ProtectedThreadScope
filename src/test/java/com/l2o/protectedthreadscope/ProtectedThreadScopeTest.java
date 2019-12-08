package com.l2o.protectedthreadscope;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectFactory;

@ExtendWith(MockitoExtension.class)
class ProtectedThreadScopeTest {
    private static final String BEAN_NAME = "bean-name";
    private static final String BEAN_VALUE = "bean-value";

    @Mock
    private ProtectedThreadScopeManagerInternal manager;
    @Mock
    private ObjectFactory<String> objectFactory;
    
    private ProtectedThreadScope testee;
    
    @BeforeEach
    public void setUp() {
	testee = new ProtectedThreadScope(manager);
    }
    
    @Test
    void testGet() {
	Mockito.when(manager.getBean(BEAN_NAME, objectFactory)).thenReturn(BEAN_VALUE);
	Assertions.assertEquals(BEAN_VALUE, testee.get(BEAN_NAME, objectFactory));
	Mockito.verify(manager).getBean(BEAN_NAME, objectFactory);
    }

    @Test
    void testRemove() {
	testee.remove(BEAN_NAME);
	Mockito.verify(manager).removeBean(BEAN_NAME);
    }

    @Test
    void testRegisterDestructionCallback() {
	Runnable callback = Mockito.mock(Runnable.class);
	testee.registerDestructionCallback(BEAN_NAME, callback);
	Mockito.verify(manager).registerDestructionCallback(BEAN_NAME, callback);
	Mockito.verifyZeroInteractions(callback);
    }

    @Test
    void testResolveContextualObject() {
	Assertions.assertNull(testee.resolveContextualObject(BEAN_NAME));
    }

    @Test
    void testGetConversationId() {
	Assertions.assertNull(testee.getConversationId());
    }
}
