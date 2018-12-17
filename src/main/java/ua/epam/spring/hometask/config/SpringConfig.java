package ua.epam.spring.hometask.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ua.epam.spring.hometask.domain.Auditorium;
import ua.epam.spring.hometask.domain.Event;
import ua.epam.spring.hometask.domain.EventRating;
import ua.epam.spring.hometask.domain.User;
import ua.epam.spring.hometask.service.EventService;
import ua.epam.spring.hometask.service.impl.EventServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@Import(DiscountStrategiesConfig.class)
@PropertySource({
        "classpath:auditorium.properties",
        "classpath:users.properties" //if same key, this will 'win'
        ,"classpath:jdbc.properties"
        })
@ComponentScan(basePackages = {"ua.epam.spring.hometask"})
@EnableAspectJAutoProxy
public class SpringConfig {

    @Autowired
    private Environment env;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(name = "mainAuditorium")
    public Auditorium mainAuditorium(){
        Auditorium auditorium = new Auditorium();
        auditorium.setName(env.getProperty("mainAuditorium.name"));
        auditorium.setNumberOfSeats(Integer.parseInt(env.getProperty("mainAuditorium.numberOfSeats")));
        String vipSeatsStr = env.getProperty("mainAuditorium.vipSeats");
        Set<Long> vipSeats = vipSeatsStr.isEmpty() ?
                                    new HashSet<>() :
                                    (vipSeatsStr.contains(",") ?
                                            Arrays.stream(vipSeatsStr.split(","))
                                                .map(Long::parseLong).collect(Collectors.toSet()) :
                                            Arrays.asList(Long.parseLong(vipSeatsStr)).stream().collect(Collectors.toSet()));
        auditorium.setVipSeats(vipSeats);
        return  auditorium;
    }

    @Bean(name = "mainEvent")
    public Event mainEvent(){
        Event event = new Event();
        event.setId(1L);
        event.setName("Avengers");
        NavigableSet<LocalDateTime> airDates = new TreeSet<>();
        airDates.add(LocalDateTime.now().plusDays(1));
        event.setAirDates(airDates);
        event.setBasePrice(10);
        event.setRating(EventRating.HIGH);
        NavigableMap<LocalDateTime, Auditorium> dateTimeAuditoriumMap = new TreeMap<>();
        dateTimeAuditoriumMap.put(airDates.iterator().next(),mainAuditorium());
        event.setAuditoriums(dateTimeAuditoriumMap);
        return event;
    }

    @Bean
    public EventService eventService() {
        Event event = mainEvent();
        Map<Long,Event> eventMap = new HashMap<>();
        eventMap.put(event.getId(), event);
        return new EventServiceImpl(event.getId(),eventMap);
    }

    @Bean
    public User adminUser(){
        User admin = new User();
        admin.setFirstName(env.getProperty("admin.firstName"));
        admin.setLastName(env.getProperty("admin.lastName"));
        admin.setEmail(env.getProperty("admin.email"));
        admin.setPassword(env.getProperty("admin.password"));
        admin.setBirthday(LocalDate.parse(env.getProperty("admin.birthday")));
        admin.setAdmin(true);
        return admin;
    }

    @Bean
    public User firstUser(){
        User admin = new User();
        admin.setFirstName(env.getProperty("user1.firstName"));
        admin.setLastName(env.getProperty("user1.lastName"));
        admin.setEmail(env.getProperty("user1.email"));
        admin.setPassword(env.getProperty("user1.password"));
        admin.setBirthday(LocalDate.parse(env.getProperty("user1.birthday")));
        return admin;
    }

    @Bean
    public DriverManagerDataSource dataSource(){
        DriverManagerDataSource source = new DriverManagerDataSource();
        source.setDriverClassName(env.getProperty("jdbc.driverClassName"));
        source.setUsername(env.getProperty("jdbc.username"));
        source.setPassword(env.getProperty("jdbc.password"));
        source.setUrl(env.getProperty("jdbc.url"));
        return  source;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(){
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:migrations/liquibase-changeLog.xml");
        liquibase.setDataSource(dataSource());
        return liquibase;
    }
}
