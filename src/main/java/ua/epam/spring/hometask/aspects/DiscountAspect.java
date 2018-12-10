package ua.epam.spring.hometask.aspects;

import lombok.Getter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ua.epam.spring.hometask.domain.User;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class DiscountAspect {

    @Getter
    private final Map<Class, Long> discountCallCount = new HashMap<>();
    @Getter
    private final Map<Class, Map<User, Long>> discountCallPerUserCount = new HashMap<>();

    @Pointcut("execution(* *.calculateDiscount(..))")
    public void getDiscountStrategyCalls() {}

    @AfterReturning(value = "getDiscountStrategyCalls()")
    public void countDiscountStrategyCalls(JoinPoint joinPoint){
        User user = (User) joinPoint.getArgs()[0];
        Class discountStrategy = joinPoint.getTarget().getClass();
        discountCallCount.merge(discountStrategy, 1L, (a,b) -> a + b);
        Map<User, Long> map = new HashMap<>();
        map.put(user,1L);
        discountCallPerUserCount.merge(discountStrategy,
                map,
                (a,b) -> {
                    b.entrySet()
                            .forEach(e -> a.merge(e.getKey(), e.getValue(), (x, y) -> x + y));
                    return a;
                    });
    }
}
