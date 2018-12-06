package ua.epam.spring.hometask.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.epam.spring.hometask.discounts.BirthdayDiscountStrategy;
import ua.epam.spring.hometask.discounts.DiscountStrategy;
import ua.epam.spring.hometask.discounts.LoyaltyDiscountStrategy;

@Configuration
public class DiscountStrategiesConfig {
    @Bean
    public DiscountStrategy birthdayDiscountStrategy(){
        return new BirthdayDiscountStrategy((byte) 10);
    }

    @Bean
    public DiscountStrategy loyaltyDiscountStrategy(){
        return new LoyaltyDiscountStrategy((byte) 50, 10);
    }
}
