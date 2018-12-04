package ua.epam.spring.hometask.discounts;

import sun.plugin.dom.exception.InvalidStateException;
import ua.epam.spring.hometask.domain.Event;
import ua.epam.spring.hometask.domain.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;

public class LoyaltyDiscountStrategy extends DiscountStrategy{

    private int discountTicketCount;

    public LoyaltyDiscountStrategy(byte discount, int discountTicketCount){
        if(discount > 100 || discount < 0){
            throw new InvalidStateException("Discount should be between ");
        }
        this.discount = discount;
        this.discountTicketCount = discountTicketCount;
    }

    /**
     * The math used in calculations guaranties that returned value is never greater than 100
     */
    @Override
    public byte calculateDiscount(@Nullable User user, @Nonnull Event event, @Nonnull LocalDateTime airDateTime, long numberOfTickets) {
        int leftover = user != null ? user.getTickets().size()%discountTicketCount : 0;
        long numberOfDiscountedTickets = (leftover+numberOfTickets) / discountTicketCount;
        return (byte)((numberOfDiscountedTickets*discount)/numberOfTickets);
    }
}
