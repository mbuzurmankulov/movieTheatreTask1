package ua.epam.spring.hometask.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.epam.spring.hometask.discounts.DiscountStrategy;
import ua.epam.spring.hometask.domain.Event;
import ua.epam.spring.hometask.domain.User;
import ua.epam.spring.hometask.service.DiscountService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.Set;

@Service
public class DiscountServiceImpl implements DiscountService {

    private Set<DiscountStrategy> discountStrategies;

    @Autowired
    public  DiscountServiceImpl(Set<DiscountStrategy> discountStrategies){
        this.discountStrategies = discountStrategies;
    }

    @Override
    public byte getDiscount(@Nullable User user, @Nonnull Event event, @Nonnull LocalDateTime airDateTime, long numberOfTickets) {
        return discountStrategies.stream()
                .map(ds -> ds.calculateDiscount(user, event, airDateTime, numberOfTickets))
                .max(Byte::compareTo)
                .orElse((byte) 0);
    }
}
