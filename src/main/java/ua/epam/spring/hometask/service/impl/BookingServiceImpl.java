package ua.epam.spring.hometask.service.impl;

import sun.plugin.dom.exception.InvalidStateException;
import ua.epam.spring.hometask.domain.Auditorium;
import ua.epam.spring.hometask.domain.Event;
import ua.epam.spring.hometask.domain.Ticket;
import ua.epam.spring.hometask.domain.User;
import ua.epam.spring.hometask.service.BookingService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.Set;

public class BookingServiceImpl implements BookingService {

    private Set<Ticket> tickets;

    @Override
    public double getTicketsPrice(@Nonnull Event event, @Nonnull LocalDateTime dateTime, @Nullable User user, @Nonnull Set<Long> seats) {
        if(!event.airsOnDateTime(dateTime)){
            throw new InvalidStateException("Event does not air at this time");
        }

        return seats.stream()
                .map(s -> {
                    Double seatPrice = event.getSeatPrice(s, dateTime);
                    return seatPrice != null? seatPrice : 0.0;
                })
                .reduce(0.0, Double::sum);
    }

    @Override
    public void bookTickets(@Nonnull Set<Ticket> tickets) {

    }

    @Nonnull
    @Override
    public Set<Ticket> getPurchasedTicketsForEvent(@Nonnull Event event, @Nonnull LocalDateTime dateTime) {
        return null;
    }
}
