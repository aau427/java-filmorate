package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User createUser(User user) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource()).withTableName("USERS")
                .usingGeneratedKeyColumns("ID");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("EMAIL", user.getEmail());
        parameters.put("LOGIN", user.getLogin());
        parameters.put("NAME", user.getName());
        parameters.put("BIRTHDATE", user.getBirthday());

        int userId = (int) jdbcInsert.executeAndReturnKey(parameters);
        user.setId(userId);
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE USERS SET " +
                "EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDATE = ? " +
                " WHERE ID = ?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public Optional<User> getUserById(int userId) {
        String sql = "SELECT U.ID USER_ID, U.EMAIL USER_EMAIL, U.LOGIN USER_LOGIN, "
                + "U.NAME  USER_NAME, U.BIRTHDATE USER_DATE, "
                + "FR.ID FRIEND_ID "
                + "FROM USERS U "
                + "LEFT JOIN FRIENDSHIPS FS ON U.ID = FS.USER_ID AND FS.ACCEPTED = TRUE "
                + "LEFT JOIN USERS FR ON FS.FRIEND = FR.ID "
                + "  WHERE U.ID = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
        List<User> userList = commonGetUserList(rowSet);
        if (!userList.isEmpty()) {
            return Optional.of(userList.getFirst());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<User> getUsersList() {
        String sql = "SELECT U.ID USER_ID, U.EMAIL USER_EMAIL, U.LOGIN USER_LOGIN, "
                + "U.NAME  USER_NAME, U.BIRTHDATE USER_DATE, "
                + "FR.ID FRIEND_ID "
                + "FROM USERS U "
                + "LEFT JOIN FRIENDSHIPS FS ON U.ID = FS.USER_ID AND FS.ACCEPTED = TRUE "
                + "LEFT JOIN USERS FR ON FS.FRIEND = FR.ID "
                + "ORDER BY U.ID";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        return commonGetUserList(rowSet);
    }

    @Override
    public void deleteAllUsers() {
        String sqlDeleteFriendShips = "DELETE FROM FRIENDSHIPS";
        String sqlDeleteUsers = "DELETE FROM USERS";
        jdbcTemplate.update(sqlDeleteFriendShips);
        jdbcTemplate.update(sqlDeleteUsers);
    }

    @Override
    public List<User> getFriendsList(User user) {
        String sql = "SELECT U.ID USER_ID, U.EMAIL USER_EMAIL, U.LOGIN USER_LOGIN,"
                + " U.NAME USER_NAME, U.BIRTHDATE USER_DATE, FR1.FRIEND FRIEND_ID "
                + "FROM FRIENDSHIPS FR JOIN USERS U ON FR.FRIEND = U.ID AND FR.ACCEPTED = TRUE "
                + "LEFT JOIN FRIENDSHIPS FR1 ON U.ID = FR1.USER_ID AND FR1.ACCEPTED = TRUE "
                + "WHERE FR.USER_ID = ? AND FR.ACCEPTED = TRUE";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, user.getId());
        return commonGetUserList(rowSet);
    }

    @Override
    public List<User> getCommonFriends(User user, User otherUser) {
        String sql = "SELECT U.ID   USER_ID, " +
                "U.EMAIL USER_EMAIL, " +
                "U.LOGIN USER_LOGIN, " +
                "U.NAME USER_NAME, " +
                "U.BIRTHDATE USER_DATE, " +
                "F.FRIEND FRIEND_ID " +
                "FROM FRIENDSHIPS FS1 JOIN  FRIENDSHIPS FS2 ON FS1.FRIEND = FS2.FRIEND AND FS1.ACCEPTED = TRUE AND FS2.ACCEPTED = TRUE " +
                "JOIN USERS U ON U.ID = FS1.FRIEND " +
                "LEFT JOIN FRIENDSHIPS F ON U.ID = F.USER_ID " +
                "WHERE FS1.USER_ID = ? AND FS2.USER_ID = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, user.getId(), otherUser.getId());
        return commonGetUserList(rowSet);
    }

    @Override
    public void addFriendToUser(User user, int friendId) {
        try {
            String sql = "INSERT INTO FRIENDSHIPS(USER_ID, FRIEND, ACCEPTED) " +
                    "values (?, ?, ?)";
            jdbcTemplate.update(sql,
                    user.getId(),
                    friendId,
                    true);
        } catch (Exception e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    @Override
    public void deleteFriendFromUser(User user, int friendId) {
        String sql = "DELETE FROM FRIENDSHIPS WHERE " +
                "USER_ID = ? AND FRIEND = ?";
        jdbcTemplate.update(sql,
                user.getId(),
                friendId);
    }

    private List<User> commonGetUserList(SqlRowSet rowSet) {
        Map<Integer, User> usersMap = new LinkedHashMap<>();
        while (rowSet.next()) {
            int userId;
            User currentUser;
            userId = rowSet.getInt("USER_ID");
            if (!usersMap.containsKey(userId)) {
                currentUser = createUser(rowSet);
            } else {
                currentUser = usersMap.get(userId);
            }
            int friendId = rowSet.getInt("FRIEND_ID");
            if (friendId != 0) {
                currentUser.addFriend(friendId);
            }
            usersMap.put(userId, currentUser);
        }
        return new LinkedList<>(usersMap.values());
    }

    private User createUser(SqlRowSet rowSet) {
        return User.builder()
                .id(rowSet.getInt("USER_ID"))
                .email(rowSet.getString("USER_EMAIL"))
                .login(rowSet.getString("USER_LOGIN"))
                .name(rowSet.getString("USER_NAME"))
                .birthday(rowSet.getDate("USER_DATE").toLocalDate())
                .build();
    }
}