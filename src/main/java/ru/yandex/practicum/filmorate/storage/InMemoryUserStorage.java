package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> usersMap = new HashMap<>();

    @Override
    public User createUser(User user) {
        usersMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        usersMap.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getUsersList() {
        return new ArrayList<>(usersMap.values());
    }

    @Override
    public void deleteAllUsers() {
        usersMap.clear();
    }

    @Override
    public Optional<User> getUserById(int id) {
        return Optional.ofNullable(usersMap.get(id));
    }
}
