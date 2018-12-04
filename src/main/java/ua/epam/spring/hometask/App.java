package ua.epam.spring.hometask;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sun.plugin.dom.exception.InvalidStateException;
import ua.epam.spring.hometask.domain.User;
import ua.epam.spring.hometask.service.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class App {

    private UserService userService;
    private EventService eventService;
    private BookingService bookingService;

    private User currentUser;
    private int currentMenu = 0;
    private String userInfo;

    public App(UserService userService, EventService eventService, BookingService bookingService){
        this.userService = userService;
        this.eventService = eventService;
        this.bookingService = bookingService;
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");
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
            default:
                displayLoginMenu();
                break;
        }
    }

    private void displayMainMenu(){
        System.out.println(userInfo + " Please choose one of the actions below.\n");

        Set<Integer> allowedCommands = new HashSet<>();

        allowedCommands.addAll(Arrays.asList(3,6));
        if(currentUser == null) {
            System.out.println("Login -  press 1");
            allowedCommands.add(1);
        }
        if(currentUser == null) {
            System.out.println("Register - press 2");
            allowedCommands.add(2);
        }
        System.out.println("Book tickets - press 3");
        if(currentUser != null && currentUser.isAdmin()){
            System.out.println("Create Event - press 4");
            allowedCommands.add(4);
        }
        if(currentUser != null){
            System.out.println("Logout - press 5");
            allowedCommands.add(5);
        }
        System.out.println("Exit - press 6");

        Integer command = 0;
        Scanner scanner = new Scanner(System.in);
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
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                currentUser = null;
                userInfo = "You are visiting as Anonymous user.";
                break;
            case 6:
                currentMenu = -1;
                break;
        }
    }

    private void displayLoginMenu(){
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Type in your email:");
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
                System.out.println("User with such email and password does not exist. You want to continue y/n:");
                String command  = scanner.nextLine();
                if (command.equals("y")) continue;
                else {
                    currentMenu = 0;
                    break;
                }
            }
        }
    }

}
