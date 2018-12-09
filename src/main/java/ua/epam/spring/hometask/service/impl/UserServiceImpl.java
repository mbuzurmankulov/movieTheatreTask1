package ua.epam.spring.hometask.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.epam.spring.hometask.domain.User;
import ua.epam.spring.hometask.service.UserService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private volatile static Long CURRENT_ID = 0L;
    private static final Map<Long, User> userMap = new HashMap<>();

    @Autowired
    public UserServiceImpl(Set<User> users){
        users.forEach(u -> {
            u.setId(++CURRENT_ID);
            userMap.put(u.getId(), u);});
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
