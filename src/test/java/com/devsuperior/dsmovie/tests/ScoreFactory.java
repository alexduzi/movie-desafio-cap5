package com.devsuperior.dsmovie.tests;

import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;

public class ScoreFactory {

    public static Double scoreValue = 4.5;

    public static ScoreEntity createScoreEntity() {
        MovieEntity movie = MovieFactory.createMovieEntity();
        UserEntity user = UserFactory.createUserEntity();
        ScoreEntity score = new ScoreEntity();

        score.setMovie(movie);
        score.setUser(user);
        score.setValue(scoreValue);
        movie.getScores().add(score);
        return score;
    }

    public static ScoreEntity createScoreEntity(Long movieId) {
        MovieEntity movie = MovieFactory.createMovieEntity(movieId);
        UserEntity user = UserFactory.createUserEntity();
        ScoreEntity score = new ScoreEntity();

        score.setMovie(movie);
        score.setUser(user);
        score.setValue(scoreValue);
        movie.getScores().add(score);
        return score;
    }

    public static ScoreDTO createScoreDTO() {
        ScoreEntity score = createScoreEntity();
        return new ScoreDTO(score.getId().getMovie().getId(), score.getValue());
    }

    public static ScoreDTO createScoreDTO(Long movieId) {
        ScoreEntity score = createScoreEntity(movieId);
        return new ScoreDTO(score.getId().getMovie().getId(), score.getValue());
    }
}
