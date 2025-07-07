package ru.yandex.practicum.filmorate.manager;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class InMemoryUserManager implements UserManager {
    private static int id = 0;
    private final Map<Integer, User> usersMap = new HashMap<>();

    @Override
    public User createUser(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        usersMap.put(user.getId(), user);
        log.info("Создан новый пользователь {}", user.getId());
        return user.clone();
    }

    @Override
    public User updateUser(User user) {
        if (!usersMap.containsKey(user.getId())) {
            String message = "Ошибка при изменении пользователя: не найден пользователь Id = " + user.getId();
            log.error(message);
            throw new ResourceNotFoundException(message);
        }

        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        usersMap.put(user.getId(), user);
        log.info("Изменил пользователя: {}", user.getId());
        return user.clone();
    }

    @Override
    public List<User> getUsersList() {
        log.info("Отгрузил пользаков в количестве {}", usersMap.size());
        return new ArrayList<>(usersMap.values());
    }

    @Override
    public void deleteAllUsers() {
        log.info("Очистил список пользователей");
        usersMap.clear();
    }

    private int getNextId() {
        return usersMap.size() + 1;
    }
}
