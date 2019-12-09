# ProtectedThreadScope
This library provides a Thread-local scope for the Spring framework.
Contrary to the SimpleThreadScope implementation provided by Spring (https://docs.spring.io/spring-framework/docs/5.2.2.RELEASE/javadoc-api/org/springframework/context/support/SimpleThreadScope.html), this one will clean up objects.
To delimit the use of a thread-scoped bean, the app must enclose the code with a try-with-resource block. The thread-scoped beans will be freed when the last block exits.

Example:
Bean can be annotated as follows:

    @Bean
    @Scope(value = ProtectedThreadScope.SCOPE, proxyMode = ScopedProxyMode.INTERFACES)

The config must be imported in the application context:

    @Import(ProtectedThreadScopeConfig.class)

In the enclosing code that will enclose all calls that need the bean, have the scope manager injected and create a try block:

    @Inject
    private ProtectedThreadScopeManager scopeManager;
    public String getMessage() {
        try (ThreadScopeProtection c = scopeManager.start()){
	          // Do something...
        }
    }
