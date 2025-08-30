package dev.trela.microservices.book.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingAndCachingAspect {

    private final Map<String, Object> cache = new HashMap<>();
    private final MessageService messageService;


    @Around("execution(* dev.trela.microservices.book.service..*(..)) && !within(dev.trela.microservices.book.service.MessageService)")
    public Object logAndCache(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        Object[] args = joinPoint.getArgs();
        String key = className + "." + methodName + Arrays.toString(args);

        if (methodName.equals("findAuthorByName")) {
            return joinPoint.proceed(); // skip
        }


        log.info(messageService.getMessage("logging.calling", key));

        // If the method has arguments and a non-void return type, check the cache
        if (args.length > 0
                && !methodSignature.getReturnType().equals(void.class)
                && cache.containsKey(key)) {

            String cachedMessage = messageService.getMessage("logging.cached.result", key);
            log.info(cachedMessage);
            return cache.get(key); // Return cached result
        }

        long startTime = System.currentTimeMillis();


        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long duration = endTime-startTime;

        String executionMessage;
        if(methodSignature.getReturnType().equals(void.class)){
            executionMessage = messageService.getMessage("logging.void", methodName, duration);

        }else{
            executionMessage = messageService.getMessage("logging.returned", methodName, result, duration);
        }


        log.info(executionMessage);



        return result;
    }


}
