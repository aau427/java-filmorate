package ru.yandex.practicum.filmorate.manager;


public class Managers {
    public static FilmManager getDefaultFilmManager() {
        return new InMemoryFilmManager();
    }

    public static UserManager getDefaultUserManager() {
        return new InMemoryUserManager();
    }
}
