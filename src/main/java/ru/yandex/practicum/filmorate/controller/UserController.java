package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.manager.Managers;
import ru.yandex.practicum.filmorate.manager.UserManager;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;


@RequestMapping("/users")
@RestController
public class UserController {
    private final UserManager manager = Managers.getDefaultUserManager();

    @GetMapping
    public List<User> getUserList() {
        return manager.getUsersList();
    }

    @PostMapping()
    public User createUser(@Valid @RequestBody User user) {
        return manager.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return manager.updateUser(user);
    }

    @DeleteMapping()
    public void deleteAllUsers() {
        manager.deleteAllUsers();
    }
}
