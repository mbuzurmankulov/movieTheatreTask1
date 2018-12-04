package ua.epam.spring.hometask;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ua.epam.spring.hometask.service.AuditoriumService;
import ua.epam.spring.hometask.service.BookingService;
import ua.epam.spring.hometask.service.DiscountService;
import ua.epam.spring.hometask.service.EventService;

public class App {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");
        App app =  (App)ctx.getBean("app");
        AuditoriumService auditoriumService = (AuditoriumService)ctx.getBean("auditoriumService");
        auditoriumService.getByName("Grand Hall").getAllSeats();

        EventService eventService = (EventService) ctx.getBean("eventService");
        eventService.getAll();

        DiscountService discountService = (DiscountService) ctx.getBean("discountService");
        discountService.toString();

        BookingService bookingService = (BookingService) ctx.getBean("bookingService");
        bookingService.toString();
    }
}
