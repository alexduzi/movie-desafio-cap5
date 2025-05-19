package com.devsuperior.dsmovie.tests;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.ScoreEntityPK;
import com.devsuperior.dsmovie.entities.UserEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

public class MovieFactory {
	
	public static MovieEntity createMovieEntity() {
		MovieEntity movie = new MovieEntity(1L, "Test Movie", 0.0, 0, "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
		return movie;
	}

	public static MovieEntity createMovieEntityWithScores() {
		MovieEntity movie = new MovieEntity(1L, "Test Movie", 0.0, 0, "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
		UserEntity user = UserFactory.createUserEntity();

		Set<ScoreEntity> scores = new HashSet<>();

		ScoreEntity score = new ScoreEntity();
		ScoreEntityPK pk = new ScoreEntityPK();
		pk.setMovie(movie);
		pk.setUser(user);
		score.setId(pk);
		score.setMovie(movie);
		score.setUser(user);
		score.setValue((Math.random() * 5) + 1);
		scores.add(score);

		movie.getScores().addAll(scores);

		return movie;
	}

	public static MovieEntity createMovieEntity(Long id) {
		MovieEntity movie = new MovieEntity(id, "Test Movie", 0.0, 0, "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
		return movie;
	}
	
	public static MovieDTO createMovieDTO() {
		MovieEntity movie = createMovieEntity();
		return new MovieDTO(movie);
	}
}
