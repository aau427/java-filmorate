package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.common.CommonUtility;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    private final String path = "/users";

    @BeforeEach
    public void beforeEach() throws Exception {
        mockMvc.perform(delete(path));
    }

    @Test
    @DisplayName("Корректный пользователь создается")
    public void shouldCorrectUserCreate() throws Exception {
        User user = User.builder()
                .name("Пользователь 1")
                .email("aaa@mail.ru")
                .birthday(LocalDate.now().minusYears(25))
                .login("aaymail")
                .build();

        mockMvc.perform(
                        post(path)
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Пользователь 1"))
                .andExpect(jsonPath("$.login").value("aaymail"))
                .andExpect(jsonPath("$.email").value("aaa@mail.ru"))
                .andExpect(jsonPath("$.birthday").value(LocalDate.now().minusYears(25).toString()));
    }

    @Test
    @DisplayName("Пользователь создается без имени")
    public void shouldCorrectUserCreateWithoutName() throws Exception {
        User user = User.builder()
                .email("aaa@mail.ru")
                .birthday(LocalDate.now().minusYears(25))
                .login("aaymail")
                .build();

        mockMvc.perform(
                        post(path)
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("aaymail"))
                .andExpect(jsonPath("$.login").value("aaymail"))
                .andExpect(jsonPath("$.email").value("aaa@mail.ru"))
                .andExpect(jsonPath("$.birthday").value(LocalDate.now().minusYears(25).toString()));
    }

    @Test
    @DisplayName("Пользователь не создается без email")
    public void shouldUserNotCreateWithoutEmail() throws Exception {
        User user = User.builder()
                .name("пользак")
                .birthday(LocalDate.now().minusYears(25))
                .login("aaymail")
                .build();

        mockMvc.perform(
                        post(path)
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(x -> Objects.requireNonNull(x.getResolvedException()).getClass().equals(ValidationException.class));
    }

    @Test
    @DisplayName("Пользователь не создается c некорректным e-mail без @")
    public void shouldUserNotCreateWithIncorrectEmail() throws Exception {
        User user = User.builder()
                .name("пользак")
                .email("fffff")
                .birthday(LocalDate.now().minusYears(25))
                .login("aaymail")
                .build();

        mockMvc.perform(
                        post(path)
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(x -> Objects.requireNonNull(x.getResolvedException()).getClass().equals(ValidationException.class));
    }

    @Test
    @DisplayName("Пользователь не создается c датой рождения в будущем")
    public void shouldUserNotCreateWithBirthDateInFuture() throws Exception {
        User user = User.builder()
                .name("пользак")
                .email("aaa@mail.ru")
                .birthday(LocalDate.now().plusDays(1))
                .login("aaymail")
                .build();

        mockMvc.perform(
                        post(path)
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(x -> Objects.requireNonNull(x.getResolvedException()).getClass().equals(ValidationException.class));
    }

    @Test
    @DisplayName("Пользователь не создается c пустым login")
    public void shouldUserNotCreateWithoutLogin() throws Exception {
        User user = User.builder()
                .name("пользак")
                .email("aaa@mail.ru")
                .birthday(LocalDate.now().plusDays(1))
                .build();

        mockMvc.perform(
                        post(path)
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(x -> Objects.requireNonNull(x.getResolvedException()).getClass().equals(ValidationException.class));
    }

    @Test
    @DisplayName("Пользователь не создается c пробелами в login")
    public void shouldUserNotCreateWithIncorrectLogin() throws Exception {
        User user = User.builder()
                .name("пользак")
                .email("aaa@mail.ru")
                .login("g d g")
                .birthday(LocalDate.now().plusDays(1))
                .build();

        mockMvc.perform(
                        post(path)
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(x -> Objects.requireNonNull(x.getResolvedException()).getClass().equals(ValidationException.class));
    }

    @Test
    @DisplayName("пользователь изменяется")
    public void shouldUserUpdate() throws Exception {
        User user = User.builder()
                .name("Пользователь 1")
                .email("aaa@mail.ru")
                .birthday(LocalDate.now().minusYears(25))
                .login("aaymail")
                .build();
        MvcResult userResult = mockMvc.perform(
                        post(path)
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        int userId = CommonUtility.getIdFromMvcResult(userResult);

        user = User.builder()
                .id(userId)
                .name("Изменил имя")
                .email("changed@mail.ru")
                .birthday(LocalDate.now().minusYears(25))
                .login("aaymail")
                .build();

        mockMvc.perform(
                        put(path)
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Изменил имя"))
                .andExpect(jsonPath("$.login").value("aaymail"))
                .andExpect(jsonPath("$.email").value("changed@mail.ru"))
                .andExpect(jsonPath("$.birthday").value(LocalDate.now().minusYears(25).toString()));
    }

    @Test
    @DisplayName("пользователь не изменяется, если его нет")
    public void shouldUserNotUpdateIfHeD() throws Exception {
        User user = User.builder()
                .id(1)
                .name("Изменил имя")
                .email("changed@mail.ru")
                .birthday(LocalDate.now().minusYears(25))
                .login("aaymail")
                .build();

        mockMvc.perform(
                        put(path)
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(404));
    }

    @Test
    @DisplayName("Добавляет в друзья")
    public void shouldUserAddFriendsList() throws Exception {
        User user = User.builder()
                .name("Пользователь 1")
                .email("first@mail.ru")
                .birthday(LocalDate.now().minusYears(25))
                .login("aaymail")
                .build();

        User friend = User.builder()
                .name("Друг")
                .email("friend@mail.ru")
                .birthday(LocalDate.now().minusYears(24))
                .login("aabmail")
                .build();
        MvcResult userResult = mockMvc.perform(
                        post(path)
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        int userId = CommonUtility.getIdFromMvcResult(userResult);

        userResult = mockMvc.perform(
                        post(path)
                                .content(objectMapper.writeValueAsString(friend))
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        int friendId = CommonUtility.getIdFromMvcResult(userResult);

        mockMvc.perform(
                        put(path + "/" + userId + "/friends/" + friendId))
                .andExpect(status().is(200));
    }

    @Test
    @DisplayName("Не добавляет в друзья несуществующего друга")
    public void shouldNotAddNotExistFriend() throws Exception {
        User user = User.builder()
                .name("Пользователь 1")
                .email("first@mail.ru")
                .birthday(LocalDate.now().minusYears(25))
                .login("aaymail")
                .build();

        mockMvc.perform(
                post(path)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
        );


        mockMvc.perform(
                put(path + "/1/friends/2")
        ).andExpect(status().is(404));
    }

    @Test
    @DisplayName("Не добавляет друга несуществующему пользователю")
    public void shouldNotAddFriendToNotFoundUser() throws Exception {
        User friend = User.builder()
                .name("Друг")
                .email("friend@mail.ru")
                .birthday(LocalDate.now().minusYears(24))
                .login("aabmail")
                .build();


        mockMvc.perform(
                post(path)
                        .content(objectMapper.writeValueAsString(friend))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        mockMvc.perform(
                put(path + "/33/friends/1")
        ).andExpect(status().is(404));
    }

    @Test
    @DisplayName("Удаляет из списка друзей")
    public void shouldDeleteFromUserList() throws Exception {
        User user = User.builder()
                .name("Пользователь 1")
                .email("first@mail.ru")
                .birthday(LocalDate.now().minusYears(25))
                .login("aaymail")
                .build();

        User friend = User.builder()
                .name("Друг")
                .email("friend@mail.ru")
                .birthday(LocalDate.now().minusYears(24))
                .login("aabmail")
                .build();

        MvcResult userResult = mockMvc.perform(
                        post(path)
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        int userId = CommonUtility.getIdFromMvcResult(userResult);

        userResult = mockMvc.perform(
                        post(path)
                                .content(objectMapper.writeValueAsString(friend))
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        int friendId = CommonUtility.getIdFromMvcResult(userResult);

        mockMvc.perform(
                put(path + "/" + userId + "/friends/" + friendId));

        mockMvc.perform(
                        delete(path + "/" + userId + "/friends/" + friendId))
                .andExpect(status().is(200));
    }

    @Test
    @DisplayName("Не удаляет из списка друзей у несуществующего пользователя")
    public void shouldNotDeleteFriendFromNotFoundUser() throws Exception {
        User friend = User.builder()
                .name("Друг")
                .email("friend@mail.ru")
                .birthday(LocalDate.now().minusYears(24))
                .login("aabmail")
                .build();

        mockMvc.perform(
                post(path)
                        .content(objectMapper.writeValueAsString(friend))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        mockMvc.perform(
                        delete(path + "/33/friends/1"))
                .andExpect(status().is(404));
    }

    @Test
    @DisplayName("Возвращает список друзей")
    public void shouldReturnFriendsList() throws Exception {
        User user = User.builder()
                .name("Пользователь 1")
                .email("first@mail.ru")
                .birthday(LocalDate.now().minusYears(25))
                .login("aaymail")
                .build();

        User friend1 = User.builder()
                .name("Друг1")
                .email("friend1@mail.ru")
                .birthday(LocalDate.now().minusYears(24))
                .login("aabmail1")
                .build();

        User friend2 = User.builder()
                .name("Друг2")
                .email("friend2@mail.ru")
                .birthday(LocalDate.now().minusYears(24))
                .login("aabmail2")
                .build();

        MvcResult resultUser = mockMvc.perform(
                        post(path)
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        int userId = CommonUtility.getIdFromMvcResult(resultUser);

        resultUser = mockMvc.perform(
                        post(path)
                                .content(objectMapper.writeValueAsString(friend1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        int friend1Id = CommonUtility.getIdFromMvcResult(resultUser);

        resultUser = mockMvc.perform(
                        post(path)
                                .content(objectMapper.writeValueAsString(friend2))
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        int friend2Id = CommonUtility.getIdFromMvcResult(resultUser);

        mockMvc.perform(
                put(path + "/" + userId + "/friends/" + friend1Id));

        mockMvc.perform(
                put(path + "/" + userId + "/friends/" + friend2Id));

        mockMvc.perform(
                        get(path + "/" + userId + "/friends"))
                .andExpect(status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(friend1Id))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(friend2Id));
    }
}