package com.canciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.canciones.model.Song;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    // Custom query methods can be added here if needed
}
