package ua.epam.spring.hometask.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ua.epam.spring.hometask.domain.User;

import java.util.Random;

@Aspect
@Component
public class LuckyWinnerAspect {
    @Pointcut("execution(* ua.epam.spring.hometask.service.impl.DiscountServiceImpl.getDiscount(..))")
    public void discountTickets(){}

    @Around("discountTickets()")
    public byte findLucky(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        User user = (User)args[0];
        Random random = new Random(100);
        if(random.nextInt() < 50) {
            return 100;
        }
        return (byte)joinPoint.proceed(args);
    }
}
