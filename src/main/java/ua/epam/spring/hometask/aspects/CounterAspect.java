package ua.epam.spring.hometask.aspects;

import lombok.Getter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ua.epam.spring.hometask.domain.Event;
import ua.epam.spring.hometask.domain.Ticket;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Aspect
@Component
public class CounterAspect {

    @Getter
    private final Map<Event,Long> eventAccessByNameCounter = new HashMap<>();
    @Getter
    private final Map<Event,Long> eventQueryByPriceCounter = new HashMap<>();
    @Getter
    private final Map<Event,Long> eventTicketBookingCount = new HashMap<>();

    @Pointcut("execution(* ua.epam.spring.hometask.service.impl.EventServiceImpl.getByName(String))")
    public void eventAccessByName(){}

    @Pointcut("execution(* ua.epam.spring.hometask.service.impl.BookingServiceImpl.getTicketsPrice(..))")
    public void eventQueryByPrice(){}

    @Pointcut("execution(* ua.epam.spring.hometask.service.impl.BookingServiceImpl.bookTickets(..))")
    public void eventBookTicket(){}

    @AfterReturning(
            value = "eventAccessByName()",
            returning = "retVal")
    public void countAccesesByName(Object retVal) {
        Event event = (Event) retVal;
        eventAccessByNameCounter.merge(event, 1L, (a,b) -> a + b);
    }

    @Before(value = "eventQueryByPrice()")
    public void countQueryByPrice(JoinPoint joinPoint) {
        Event event = (Event) joinPoint.getArgs()[0];
        eventQueryByPriceCounter.merge(event, 1L, (a,b) -> a + b);
    }

    @Before(value = "eventBookTicket()")
    public void countTicketBooking(JoinPoint joinPoint) {
        ((Set<Ticket>) joinPoint.getArgs()[0]).stream()
                        .map(t -> t.getEvent())
                        .distinct()
                        .forEach( e -> eventTicketBookingCount.merge(e, 1L, (a,b) -> a + b));
    }
}
