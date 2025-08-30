package dev.trela.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Aspect for logging method calls and caching results of service methods.
 * This class intercepts method calls in the service layer (excluding MessageService)
 * to log input/output and cache method results based on arguments.
 */
@Aspect
@Component
public class LoggingAndCachingAspect {

    // Cache map to store method results based on unique method signature + arguments
    private final Map<String, Object> cache = new HashMap<>();

    private final MessageService messageService;

    // Constructor-based dependency injection of the MessageService
    public LoggingAndCachingAspect(MessageService messageService){
        this.messageService = messageService;
    }

    /**
     * Around advice that intercepts calls to any method in the service package
     * (excluding MessageService). It logs method calls, checks for cached results,
     * proceeds with execution if needed, logs the result, and caches it.
     *
     * @param joinPoint the join point representing the intercepted method call
     * @return the method result (cached or freshly computed)
     * @throws Throwable if the underlying method throws an exception
     */
    @Around("execution(* dev.trela.service..*(..)) && !within(dev.trela.service.MessageService)")
    public Object logAndCache(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        Object[] args = joinPoint.getArgs();
        String key = className + "." + methodName + Arrays.toString(args);

        if (methodName.equals("findAuthorByName")) {
            return joinPoint.proceed(); // skip
        }


        System.out.println(messageService.getMessage("logging.calling", key));

        // If the method has arguments and a non-void return type, check the cache
        if (args.length > 0 && !methodSignature.getReturnType().equals(void.class)) {
            if (cache.containsKey(key)) {
                String cachedMessage = messageService.getMessage("logging.cached.result", key);
                System.out.println(cachedMessage);
                return cache.get(key); // Return cached result
            }
        }

        long startTime = System.currentTimeMillis();

        // Proceed with method execution
        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long duration = endTime-startTime;

        String executionMessage;
        if(methodSignature.getReturnType().equals(void.class)){
            executionMessage = messageService.getMessage("logging.void",methodName,duration);

        }else{
            executionMessage = messageService.getMessage("logging.returned", methodName, result,duration);
        }

        // Log the returned result
        System.out.println(executionMessage);


        // Cache the result if it's not null and the method had arguments
        if (args.length > 0 && result != null) {
            String cachingResultMessage = messageService.getMessage("logging.caching.result", result, methodName, Arrays.toString(args));
            System.out.println(cachingResultMessage);
            cache.put(key, result);
        }

        return result;
    }


}
