package com.l2o.protectedthreadscope;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A configuration that must be enable to use the protected thread scope.
 */
@Configuration
public class ProtectedThreadScopeConfig implements BeanFactoryPostProcessor {
    private ProtectedThreadScopeManagerImpl manager = new ProtectedThreadScopeManagerImpl();
    @Bean
    public ProtectedThreadScopeManager protectedThreadScopeManager() {
	return manager;
    }
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	beanFactory.registerScope(ProtectedThreadScope.SCOPE, new ProtectedThreadScope(manager));
    }
}
