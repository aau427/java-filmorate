package ru.yandex.practicum.filmorate.manager;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserManager {
    User createUser(User user);

    User updateUser(User user);

    List<User> getUsersList();
}
