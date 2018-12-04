package ua.epam.spring.hometask.service.impl;

import ua.epam.spring.hometask.domain.Ticket;
import ua.epam.spring.hometask.domain.User;
import ua.epam.spring.hometask.service.UserService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {

    private volatile static Long CURRENT_ID = 0L;
    private static final Map<Long, User> userMap;

    static {
        //Todo replace with properties file load
        userMap = new HashMap<>();
        User user = new User();
        user.setId(++CURRENT_ID);
        user.setEmail("user_1@gmail.com");
        user.setFirstName("Max");
        user.setLastName("Wasowski");
        user.setBirthday(LocalDate.of(1989,12,23));
        user.setTickets(new TreeSet<>());
        userMap.put(user.getId(), user);
    }

    @Nullable
    @Override
    public User
    getUserByEmail(@Nonnull String email) {
        return  (User) userMap.entrySet().stream()
                .filter(e -> e.getValue().equals(email))
                .findFirst()
                .orElse(null);

    }

    @Override
    public User save(@Nonnull User object) {
        if(object.getId() == null) {
            object.setId(++CURRENT_ID);
        }
        userMap.put(object.getId(), object);
        return object;
    }

    @Override
    public void remove(@Nonnull User object) {
        userMap.remove(object.getId());
    }

    @Override
    public User getById(@Nonnull Long id) {
        return userMap.get(id);
    }

    @Nonnull
    @Override
    public Collection<User> getAll() {
        return userMap.entrySet().stream()
                .map(e -> e.getValue())
                .collect(Collectors.toList());
    }
}
