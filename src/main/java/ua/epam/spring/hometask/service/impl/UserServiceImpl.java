package ua.epam.spring.hometask.service.impl;

import ua.epam.spring.hometask.domain.User;
import ua.epam.spring.hometask.service.UserService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {

    private volatile static Long CURRENT_ID = 0L;
    private static final Map<Long, User> userMap = new HashMap<>();

    public UserServiceImpl(User admin){
        admin.setAdmin(true);
        admin.setId(++CURRENT_ID);
        admin.setBirthday(LocalDate.now());
        userMap.put(admin.getId(),admin);
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
    public User checkUserCredentials(String login, String password) {
        User user = userMap.entrySet().stream()
                .filter(e -> e.getValue().getEmail().equals(login))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
        return user != null && password.equals(user.getPassword()) ? user: null;
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
