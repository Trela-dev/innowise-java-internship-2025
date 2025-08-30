package dev.trela.testing.learning;

import org.springframework.stereotype.Component;

@Component
public class Calculator {

    public int add(int a, int b){

        System.out.println("Adding two numbers");
        return a + b;
    }

}
