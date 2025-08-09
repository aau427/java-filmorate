package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage storage;

    public User createUser(final User user) {
        User newUser = storage.createUser(user);
        log.info("Service: Создан новый пользователь {}", newUser.getId());
        return newUser.clone();
    }

    public User updateUser(final User user) {
        User userTmp = getUserById(user.getId());
        userTmp = storage.updateUser(user);
        log.info("Service: Изменил пользователя: {}", userTmp.getId());
        return userTmp.clone();
    }

    public List<User> getUsersList() {
        List<User> userList = storage.getUsersList();
        log.info("Service: Отгрузил пользователй в количестве {}", userList.size());
        return userList;
    }

    public void deleteAllUsers() {
        log.info("Service: Очистил список пользователей");
        storage.deleteAllUsers();
    }

    public User getUserById(int userId) {
        Optional<User> userOptional = storage.getUserById(userId);
        if (!userOptional.isPresent()) {
            String msg = "Не нашел пользователя с Id = " + userId;
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        return userOptional.get();
    }

    public void addFriendToUser(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        storage.addFriendToUser(user, friendId);
    }

    public List<User> getFriendsList(int userId) {
        User user = getUserById(userId);
        return storage.getFriendsList(user);
    }

    public void deleteFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        storage.deleteFriendFromUser(user, friendId);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        User user = getUserById(id);
        User otherUser = getUserById(otherId);
        return storage.getCommonFriends(user, otherUser);
    }
}