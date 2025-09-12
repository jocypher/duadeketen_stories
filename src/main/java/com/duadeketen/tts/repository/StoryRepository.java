package com.duadeketen.tts.repository;

import com.duadeketen.tts.entity.DuadeketenStory;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface StoryRepository extends JpaRepository<DuadeketenStory, Long> {
    Optional<DuadeketenStory> findByPageNumber(int pageNumber);
}
