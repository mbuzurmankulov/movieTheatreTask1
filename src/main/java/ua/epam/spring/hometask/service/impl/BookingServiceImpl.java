package ua.epam.spring.hometask.service.impl;

import sun.plugin.dom.exception.InvalidStateException;
import ua.epam.spring.hometask.domain.Auditorium;
import ua.epam.spring.hometask.domain.Event;
import ua.epam.spring.hometask.domain.Ticket;
import ua.epam.spring.hometask.domain.User;
import ua.epam.spring.hometask.service.BookingService;
import ua.epam.spring.hometask.service.DiscountService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.*;

public class BookingServiceImpl implements BookingService {

    private final Map<Event, Map<LocalDateTime, Set<Ticket>>> ticketsMap = new HashMap<>();

    private DiscountService discountService;

    public BookingServiceImpl(DiscountService discountService){
        this.discountService = discountService;
    }

    @Override
    public double getTicketsPrice(@Nonnull Event event, @Nonnull LocalDateTime dateTime, @Nullable User user, @Nonnull Set<Long> seats) {
        if(!event.airsOnDateTime(dateTime)){
            throw new InvalidStateException("Event does not air at this time!");
        }

        double discount = (100 - (double)discountService.getDiscount(user, event, dateTime, seats.size()))/100;

        return seats.stream()
                .map(s -> event.getSeatPrice(s, dateTime).orElse(0.0)*discount)
                .reduce(0.0, Double::sum);
    }

    @Override
    public void bookTickets(@Nonnull Set<Ticket> tickets) {
        tickets.forEach(this::bookTicket);
    }

    private void bookTicket(@Nonnull Ticket ticket) {
        Map<LocalDateTime, Set<Ticket>> timeTicketsMap = ticketsMap.get(ticket.getEvent());
        if (timeTicketsMap == null) {
            timeTicketsMap = new HashMap<>();
            ticketsMap.put(ticket.getEvent(), timeTicketsMap);
        }
        Set<Ticket> ticketSet = timeTicketsMap.get(ticket.getDateTime());
        if(ticketSet == null){
            ticketSet = new HashSet<>();
            timeTicketsMap.put(ticket.getDateTime(), ticketSet);
        }
        if(ticket.getUser() != null){
            ticket.getUser().getTickets().add(ticket);
        }
        ticketSet.add(ticket);
    }


    @Nonnull
    @Override
    public Set<Ticket> getPurchasedTicketsForEvent(@Nonnull Event event, @Nonnull LocalDateTime dateTime) {
        return ticketsMap.get(event).get(dateTime);
    }
}
