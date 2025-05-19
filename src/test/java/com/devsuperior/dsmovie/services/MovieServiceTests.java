package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static com.devsuperior.dsmovie.tests.MovieFactory.createMovieDTO;
import static com.devsuperior.dsmovie.tests.MovieFactory.createMovieEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class MovieServiceTests {

    @InjectMocks
    private MovieService service;

    @Mock
    private MovieRepository repository;

    private String movieTitle;
    private PageImpl<MovieEntity> pageResult;
    private Pageable pageable;
    private MovieEntity movie;
    private Long existingMovieId;
    private Long nonExistingMovieId;

    @BeforeEach
    void setUp() {
        existingMovieId = 1L;
        nonExistingMovieId = 500L;
        movieTitle = "Test Movie";
        pageable = PageRequest.of(0, 10);

        movie = createMovieEntity();
        pageResult = new PageImpl<>(List.of(movie));
    }

    @Test
    void findAllShouldReturnPagedMovieDTO() {
        when(repository.searchByTitle(movieTitle, pageable)).thenReturn(pageResult);

        Page<MovieDTO> result = service.findAll(movieTitle, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertInstanceOf(MovieDTO.class, result.getContent().get(0));
    }

    @Test
    void findByIdShouldReturnMovieDTOWhenIdExists() {
        when(repository.findById(existingMovieId)).thenReturn(Optional.of(movie));

        MovieDTO result = service.findById(existingMovieId);

        assertNotNull(result);
        assertInstanceOf(MovieDTO.class, result);
        assertEquals(movie.getTitle(), result.getTitle());
    }

    @Test
    void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        when(repository.findById(nonExistingMovieId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingMovieId));
    }

    @Test
    void insertShouldReturnMovieDTO() {
        MovieDTO dto = createMovieDTO();
        when(repository.save(any())).thenReturn(movie);

        MovieDTO result = service.insert(dto);

        assertNotNull(result);
        assertInstanceOf(MovieDTO.class, result);
        assertEquals(movie.getTitle(), result.getTitle());
    }

    @Test
    void updateShouldReturnMovieDTOWhenIdExists() {
        when(repository.getReferenceById(existingMovieId)).thenReturn(movie);
        when(repository.save(any())).thenReturn(movie);

        MovieDTO dto = createMovieDTO();

        MovieDTO result = service.update(existingMovieId, dto);

        assertNotNull(result);
        assertInstanceOf(MovieDTO.class, result);
        assertEquals(movie.getTitle(), result.getTitle());
    }

    @Test
    void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        when(repository.getReferenceById(existingMovieId)).thenThrow(EntityNotFoundException.class);

        MovieDTO dto = createMovieDTO();

        assertThrows(ResourceNotFoundException.class, () -> service.update(existingMovieId, dto));
    }

    @Test
    void deleteShouldDoNothingWhenIdExists() {
        when(repository.existsById(existingMovieId)).thenReturn(true);
        doNothing().when(repository).deleteById(existingMovieId);

        service.delete(existingMovieId);

        verify(repository).existsById(existingMovieId);
        verify(repository).deleteById(existingMovieId);
    }

    @Test
    void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        when(repository.existsById(nonExistingMovieId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingMovieId));

        verify(repository, never()).deleteById(nonExistingMovieId);
    }

    @Test
    void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        when(repository.existsById(nonExistingMovieId)).thenReturn(true);
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(nonExistingMovieId);

        assertThrows(DatabaseException.class, () -> service.delete(nonExistingMovieId));

        verify(repository).existsById(nonExistingMovieId);
        verify(repository).deleteById(nonExistingMovieId);
    }
}
