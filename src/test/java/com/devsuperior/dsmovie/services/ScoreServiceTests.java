package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static com.devsuperior.dsmovie.tests.MovieFactory.createMovieEntity;
import static com.devsuperior.dsmovie.tests.MovieFactory.createMovieEntityWithScores;
import static com.devsuperior.dsmovie.tests.ScoreFactory.createScoreDTO;
import static com.devsuperior.dsmovie.tests.ScoreFactory.createScoreEntity;
import static com.devsuperior.dsmovie.tests.UserFactory.createUserEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ScoreServiceTests {

    @InjectMocks
    private ScoreService service;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ScoreRepository scoreRepository;

    @Mock
    private UserService userService;

    private Long existingMovieId;
    private Long nonExistingMovieId;
    private UserEntity loggedUser;
    private ScoreDTO scoreDto;
    private MovieEntity movie;
    private ScoreEntity score;

    @BeforeEach
    void setUp() {
        existingMovieId = 1L;
        nonExistingMovieId = 500L;

        scoreDto = createScoreDTO();
        loggedUser = createUserEntity();
        movie = createMovieEntityWithScores();
        score = createScoreEntity();

        when(userService.authenticated()).thenReturn(loggedUser);
        when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movie));

        when(scoreRepository.saveAndFlush(any())).thenReturn(score);
        when(movieRepository.save(movie)).thenReturn(movie);
    }

    @Test
    void saveScoreShouldReturnMovieDTO() {
        MovieDTO result = service.saveScore(scoreDto);

        double sum = movie.getScores().stream().mapToDouble(ScoreEntity::getValue).sum();
        double avg =  Double.parseDouble(String.format("%.2f", (sum / movie.getScores().size())));

        assertNotNull(result);
        assertInstanceOf(MovieDTO.class, result);
        assertEquals(avg, result.getScore());
        assertEquals(movie.getScores().size(), result.getCount());
        assertEquals(movie.getTitle(), result.getTitle());
    }

    @Test
    void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
        scoreDto = createScoreDTO(nonExistingMovieId);
        when(movieRepository.findById(scoreDto.getMovieId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {
            service.saveScore(scoreDto);
        });
    }
}
