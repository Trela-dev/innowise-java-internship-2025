package dev.trela.testing.learning;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.AfterReturning;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class LoggingAspect {

    // === CACHE ===
    private Map<String, Integer> cache = new HashMap<>();

    // === AROUND ADVICE ===
    // Używamy @Around, ponieważ musimy wykonać metodę, ale jednocześnie możemy zablokować
    // wykonanie i zwrócić wynik z cache, jeśli to możliwe.
    @Around("execution(* dev.trela.testing.learning.Calculator.add(..))")
    public Object checkCache(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        // Tworzymy klucz na podstawie argumentów metody
        String key = args[0] + "-" + args[1];

        // Sprawdzamy, czy wynik jest już w cache
        if (cache.containsKey(key)) {
            System.out.println("Returning cached result for: " + key);
            return cache.get(key); // Zwracamy wynik z cache
        }

        // Jeśli nie ma w cache, wykonujemy metodę
        Object result = pjp.proceed();

        // Zapisujemy wynik do cache
        cache.put(key, (Integer) result);

        System.out.println("Caching result for: " + key + " = " + result);
        return result;
    }

    // === AFTER RETURNING ADVICE ===
    // Ta metoda jest zbędna, bo logika związana z cache'owaniem została już zaimplementowana w @Around.
    // Jeśli używasz @Around, nie musisz już używać @AfterReturning do cache'owania.
}












//
//
//
//    // === ADVICE ===
//    // @Before oznacza rodzaj advice: wykonaj *PRZED* metodą
//    // === POINTCUT ===
//    // "execution(* Calculator.*(..))" to Pointcut – mówisz, do jakich metod chcesz to zastosować
//    @Before("execution(* Calculator.*(..))")
//    public void logBefore(JoinPoint jp){
//        // === JOIN POINT ===
//        // JoinPoint reprezentuje moment, w którym metoda z Calculator została wywołana
//        System.out.println("Calling: " + jp.getSignature().getName());
//
//        // === TARGET ===
//        // Obiekt, na którym wywoływana jest metoda (czyli instancja Calculatora)
//        // Można go pobrać przez jp.getTarget() jeśli chcesz
//    }
//
//    // === INTRODUCTION ===
//    // W TYM KODZIE **NIE MA** Introduction
//    // Introduction to byłoby dynamiczne dodanie np. nowego interfejsu/metody do Calculatora
//
//    // === WEAVING ===
//    // Weaving też tu **nie widać** bezpośrednio, bo robi go za Ciebie Spring „w tle”.
//    // Łączy ten aspekt z klasą Calculator automatycznie przez proxy przy uruchamianiu aplikacji
//}