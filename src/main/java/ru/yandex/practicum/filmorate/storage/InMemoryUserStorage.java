package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("INMEMORYUSERSTORAGE")
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

    @Override
    public void addFriendToUser(User user, int friendId) {
        /* В соответствии с ТЗ 12 спринта
            дружба должна стать односторонней. Теперь, если пользователь отправляет заявку
             в друзья, он добавляет другого человека в свой список друзей,
             но сам в его список не попадает.
         */
        user.addFriend(friendId);
    }

    @Override
    public void deleteFriendFromUser(User user, int friendId) {
        user.deleteFriend(friendId);
    }

    @Override
    public List<User> getFriendsList(User user) {
        return user.getFriendsList().stream().map(id -> getUserById(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(User user, User otherUser) {
        List<User> usersFriends = getFriendsList(user);
        List<User> othersFriends = getFriendsList(otherUser);
        if (usersFriends.size() < othersFriends.size()) {
            return usersFriends.stream().filter(othersFriends::contains)
                    .collect(Collectors.toList());
        } else {
            return othersFriends.stream().filter(usersFriends::contains)
                    .collect(Collectors.toList());
        }
    }


}
