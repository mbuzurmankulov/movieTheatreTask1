package ua.epam.spring.hometask.discounts;

import lombok.NoArgsConstructor;
import ua.epam.spring.hometask.domain.Event;
import ua.epam.spring.hometask.domain.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@NoArgsConstructor
public class BirthdayDiscountStrategy extends DiscountStrategy {

    public BirthdayDiscountStrategy(byte discount){
        this.discount = discount;
    }

    @Override
    public byte calculateDiscount(@Nullable User user, @Nonnull Event event, @Nonnull LocalDateTime airDateTime, long numberOfTickets) {
        if(user ==null) return 0;
        Period period = Period.between(user.getBirthday().withYear(LocalDate.now().getYear()), airDateTime.toLocalDate());
        return Math.abs(period.getDays()) <= 5 ? discount : 0;
    }
}
