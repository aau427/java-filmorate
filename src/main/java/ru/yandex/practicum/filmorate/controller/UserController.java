package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;


@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    public List<User> getUserList() {
        return service.getUsersList();
    }

    @PostMapping()
    public User createUser(@Valid @RequestBody User user) {
        return service.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return service.updateUser(user);
    }

    @DeleteMapping()
    public void deleteAllUsers() {
        service.deleteAllUsers();
    }


    @PutMapping("/{id}/friends/{friendId}")
    public void addFriendToUser(@PathVariable("id") int userId, @PathVariable int friendId) {
        service.addFriendToUser(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") int userId, @PathVariable int friendId) {
        service.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsList(@PathVariable("id") int userId) {
        return service.getFriendsList(userId);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return service.getCommonFriends(id, otherId);
    }
}

