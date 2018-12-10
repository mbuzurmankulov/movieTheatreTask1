package ua.epam.spring.hometask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import sun.plugin.dom.exception.InvalidStateException;
import ua.epam.spring.hometask.aspects.CounterAspect;
import ua.epam.spring.hometask.aspects.DiscountAspect;
import ua.epam.spring.hometask.config.SpringConfig;
import ua.epam.spring.hometask.domain.Event;
import ua.epam.spring.hometask.domain.Ticket;
import ua.epam.spring.hometask.domain.User;
import ua.epam.spring.hometask.service.*;

import javax.annotation.PreDestroy;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class App {

    private UserService userService;
    private EventService eventService;
    private BookingService bookingService;

    private User currentUser;
    private int currentMenu = 0;
    private String userInfo;
    private Scanner scanner;

    @Autowired
    private CounterAspect counterAspect;
    @Autowired
    private DiscountAspect discountAspect;

    @Autowired
    public App(UserService userService, EventService eventService, BookingService bookingService){
        this.userService = userService;
        this.eventService = eventService;
        this.bookingService = bookingService;
        scanner = new Scanner(System.in);
    }

    @PreDestroy
    public void destroy(){
        scanner.close();
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
        App app =  (App)ctx.getBean("app");
        app.start();
        ctx.close();
    }

    public void start(){
        userInfo = "You are visiting as Anonymous user.";
        while (currentMenu >= 0) {
            dispatchManu();
        }

        System.out.println("Goodbye! Application is shutting down");
    }

    private void dispatchManu(){
        switch (currentMenu){
            case 0 :
                displayMainMenu();
                break;
            case 1:
                displayLoginMenu();
                break;
            case 2:
                displayRegisterMenu();
                break;
            case 3:
                displayBookTicketMenu();
                break;
            case 4:
                displayCreateEventMenu();
                break;
            case 5:
                displayMyTickets();
                break;
            case 6:
                displayStatistics();
                break;
        }
    }

    private void displayMainMenu(){
        System.out.println(userInfo + " Please choose one of the actions below.\n");

        Set<Integer> allowedCommands = new HashSet<>();

        if(currentUser == null) {
            System.out.println("Login -  press 1");
            allowedCommands.add(1);
        }
        if(currentUser == null) {
            System.out.println("Register - press 2");
            allowedCommands.add(2);
        }
        if(currentUser == null || !currentUser.isAdmin()) {
            System.out.println("Book tickets - press 3");
            allowedCommands.add(3);
        }
        if(currentUser != null && currentUser.isAdmin()){
            System.out.println("Create Event - press 4");
            allowedCommands.add(4);
        }
        if(currentUser != null && !currentUser.isAdmin()){
            System.out.println("My tickets - press 5");
            allowedCommands.add(5);
        }
        if(currentUser != null){
            System.out.println("Logout - press 6");
            allowedCommands.add(6);
        }
        System.out.println("Exit - press 7");
        allowedCommands.add(7);
        System.out.println("Show statistics - press 8");
        allowedCommands.add(8);

        Integer command;
        while (true) {
            String commandStr = scanner.nextLine();
            try {
                command = Integer.parseInt(commandStr);
                if(!allowedCommands.contains(command)){
                    throw new InvalidStateException("");
                }
            }catch (Exception ex){
                System.out.println("Invalid command. Ty again!");
                continue;
            }
            break;
        }

        switch (command) {
            case 1:
                currentMenu = 1;
                break;
            case 2:
                currentMenu = 2;
                break;
            case 3:
                currentMenu = 3;
                break;
            case 4:
                currentMenu = 4;
                break;
            case 5:
                currentMenu = 5;
                break;
            case 6:
                currentUser = null;
                currentMenu = 0;
                userInfo = "You are visiting as Anonymous user.";
                break;
            case 7:
                currentMenu = -1;
                break;
            case 8:
                currentMenu = 6;
        }
    }

    private void displayLoginMenu(){
        while (true) {
            System.out.println("\nType in your email:");
            String email = scanner.nextLine();
            System.out.println("Type in password:");
            String password = scanner.nextLine();
            User user = userService.checkUserCredentials(email, password);
            if (user != null) {
                currentUser = user;
                userInfo = "You are logged in as " + user.getFirstName() + " " + user.getLastName() + ".";
                currentMenu = 0;
                break;
            } else {
                System.out.println("User with such email and password does not exist. You want to continue? y/n:");
                String command  = scanner.nextLine();
                if (command.equals("y")) continue;
                else {
                    currentMenu = 0;
                    break;
                }
            }
        }
    }

    private void displayRegisterMenu(){
        String email = fillInStrInfo(
                "Fill in your email:",
                "email cannot be empty! You want to try again? y/n:",
                0);
        if (email == null) return;

        String password = fillInStrInfo(
                "Fill in password:",
                "password cannot be empty! You want to try again? y/n:",
                0);
        if (password == null) return;

        String firstName = fillInStrInfo(
                "Fill in your first name:",
                "first name cannot be empty! You want to try again? y/n:",
                0);
        if (firstName == null) return;

        System.out.println("Fill in your last name:");
        String lastName = scanner.nextLine();

        LocalDate birthday = fillInDateInfo(
                "Fill in your birthday in fallowing format year-month-date, for example 2018-12-23 :",
                "Invalid birthday! You want to try again? y/n:",
                0);
        if (birthday == null) return;

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setBirthday(birthday);

        userService.save(newUser);
        currentUser = newUser;
        userInfo = "You are logged in as " + newUser.getFirstName() + " " + newUser.getLastName() + ".";
        currentMenu = 0;
    }

    private void displayBookTicketMenu(){
        System.out.println("\nAll available events:\n");
        List<Event> events = eventService.getAfterDateTime(LocalDateTime.now()).stream().collect(Collectors.toList());
        if(events == null || events.size() == 0){
            System.out.println("Unfortunately, there are no upcoming events! Please, come back later.\n");
            currentMenu = 0;
            return;
        }
        for(int i = 0; i<events.size(); i++ ){
            System.out.println(events.get(i).getName() + " - press " + i);
        }

        Integer eventIndex = fillInIntInfo(
                "\nChoose an event number:",
                "Invalid event number! You want to try again? y/n:",
                 0,
                 events.size(),
                 0);
        if(eventIndex ==null) return;

        Event event = events.get(eventIndex);

        System.out.println("\n Choose time slot for event \"" + event.getName() + "\".\n");
        List<LocalDateTime> airDates = event.getAirDates().stream().collect(Collectors.toList());

        for(int i = 0; i<airDates.size(); i++ ){
            System.out.println(airDates.get(i) + " - press " + i);
        }

        Integer airDateIndex = fillInIntInfo(
                "\nChoose date by typing in the number next to the date:",
                "Invalid number! You want to try again? y/n:",
                0,
                events.size(),
                0);
        if(airDateIndex == null) return;
        LocalDateTime dateTime = airDates.get(airDateIndex);
        Set<Long> availableRegularSeats = bookingService.getRegularAvailableSeats(event, dateTime);
        Set<Long> availableVipSeats = bookingService.getVipAvailableSeats(event, dateTime);

        System.out.println("\nAvailable regular seats:");
        availableRegularSeats.forEach(s -> System.out.print(s + ","));

        System.out.println("\nAvailable vip seats:");
        availableVipSeats.forEach(s -> System.out.print(s + ","));

        System.out.println("\nChoose seats by listing them in one line separated by comma, e.g. 3,4,10");
        Set<Long> selectedSeats;
        while (true) {
            String strInput = scanner.nextLine();
            try{
                String[] tmpSeats = strInput.split(",");
                if(tmpSeats.length == 0) {
                    tmpSeats = new String[1];
                    tmpSeats[0] = strInput;
                }
                selectedSeats = Arrays.stream(tmpSeats)
                .map(Long::parseLong)
                .filter(s -> availableRegularSeats.contains(s) || availableVipSeats.contains(s))
                .collect(Collectors.toSet());
            }catch (Exception ex) {
                System.out.println("Invalid list of seats! Want to try again! y/n:");
                String command = scanner.nextLine();
                if(command.equals("y")){
                    continue;
                }else {
                    currentMenu = 0;
                    return;
                }
            }
            break;
        }
        Double price = bookingService.getTicketsPrice(event, dateTime, currentUser, selectedSeats);
        System.out.println("Total price is " + price + ". Are you sure you want to buy tickets? y/n:");
        String command = scanner.nextLine();
        if(command.equals("y")){
            bookingService.bookTickets(selectedSeats.stream()
                    .map(s -> new Ticket(currentUser, event, dateTime, s)).collect(Collectors.toSet()));
        }
        currentMenu = 0;
    }

    private void displayCreateEventMenu(){
        System.out.println("\n!!!Event creation is not implemented!\n");
        currentMenu = 0;
    }

    private void displayMyTickets(){
        System.out.println("\nAll tickets booked by you:\n");
        if(!currentUser.getTickets().isEmpty()) {
            for (Ticket ticket : currentUser.getTickets()) {
                System.out.println("*********************************");
                System.out.println("Event: " + ticket.getEvent().getName());
                System.out.println("Date: " + ticket.getDateTime());
                System.out.println("Seat: " + ticket.getSeat());
                System.out.println("*********************************\n");
            }
        }else{
            System.out.println("\nYou have no purchased tickets!\n");
        }
        currentMenu = 0;
    }

    private void displayStatistics(){
        System.out.println("\n**************Events statistics*****************");
        System.out.println("\n\tEvent access by name counter:\n");
        if (counterAspect.getEventAccessByNameCounter().size() > 0) {
            for (Map.Entry entry : counterAspect.getEventAccessByNameCounter().entrySet()) {
                System.out.println("\t\tEvent " + ((Event)entry.getKey()).getName() + " " + entry.getValue() + " times");
            }
        } else {
            System.out.println("\t\tN/A");
        }

        System.out.println("\n\tEvent query by price:\n");
        if (counterAspect.getEventQueryByPriceCounter().size() > 0) {
            for (Map.Entry entry : counterAspect.getEventQueryByPriceCounter().entrySet()) {
                System.out.println("\t\tEvent " + ((Event)entry.getKey()).getName() + " " + entry.getValue() + " times");
            }
        } else {
            System.out.println("\t\tN/A");
        }

        System.out.println("\n\tHow many times event's tickets were booked:\n");
        if (counterAspect.getEventTicketBookingCount().size() > 0) {
            for (Map.Entry entry : counterAspect.getEventTicketBookingCount().entrySet()) {
                System.out.println("\t\tEvent " + ((Event)entry.getKey()).getName() + " " + entry.getValue() + " times");
            }
        } else {
            System.out.println("\t\tN/A");
        }

        System.out.println("\n************************************************");

        System.out.println("\n*************Discounts statistics***************");
        System.out.println("\tDiscount strategies:");

        if(!discountAspect.getDiscountCallCount().isEmpty()) {
            for (Class c : discountAspect.getDiscountCallCount().keySet()) {
                System.out.println("\t\t" + c.getSimpleName() + ":");
                System.out.println("\t\t\tTotal calls: " + discountAspect.getDiscountCallCount().get(c));
                Map<User, Long> tmpMap =discountAspect.getDiscountCallPerUserCount().get(c);
                for (User u : tmpMap.keySet()){
                    System.out.println("\t\t\tDiscount for user " +
                            u.getFullName() +
                            " was requested " +
                            tmpMap.get(u) +
                            " times");
                }
            }
        } else {
            System.out.println("\t\tN/A");
        }

        System.out.println("**************************************************");

        currentMenu = 0;
    }

    private String fillInStrInfo(String instructions, String error, int switchMenu) {
        String strInput;
        while (true) {
            System.out.println(instructions);
            strInput = scanner.nextLine();
            if(strInput.isEmpty()){
                System.out.println(error);
                String command = scanner.nextLine();
                if(command.equals("y")){
                    continue;
                }else {
                    currentMenu = switchMenu;
                    return null;
                }
            }
            break;
        }
        return strInput;
    }

    private Integer fillInIntInfo(String instructions, String error,Integer validFrom, Integer validTo, int switchMenu) {
        Integer intInput;
        while (true) {
            System.out.println(instructions);
            String strInput = scanner.nextLine();
            try{
                intInput = Integer.parseInt(strInput);
                if((validFrom != null && intInput < validFrom)
                    || (validTo != null && intInput > validTo)) {
                    throw new InvalidStateException("");
                }
            }catch (Exception ex) {
                System.out.println(error);
                String command = scanner.nextLine();
                if(command.equals("y")){
                    continue;
                }else {
                    currentMenu = switchMenu;
                    return null;
                }
            }
            break;
        }
        return intInput;
    }

    private LocalDate fillInDateInfo(String instructions, String error, int switchMenu) {
        LocalDate dateInput;
        while (true) {
            System.out.println(instructions);
            String strInput = scanner.nextLine();
            try{
                dateInput = LocalDate.parse(strInput);
            }catch (Exception ex) {
                System.out.println(error);
                String command = scanner.nextLine();
                if(command.equals("y")){
                    continue;
                }else {
                    currentMenu = switchMenu;
                    return null;
                }
            }
            break;
        }
        return dateInput;
    }

}
