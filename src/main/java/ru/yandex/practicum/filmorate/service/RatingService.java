package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingStorage storage;


    public Rating getRatingById(int ratingId) {
        return storage.getRatingById(ratingId)
                .orElseThrow(() -> {
                    String msg = "Не нашел рейтинг с Id = " + ratingId;
                    log.warn(msg);
                    throw new ResourceNotFoundException(msg);
                });
    }

    public List<Rating> getAllRatings() {
        return storage.getAllRatings();
    }

}
