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

        mockMvc.perform(
                post(path)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        user = User.builder()
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
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value("1"))
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
}