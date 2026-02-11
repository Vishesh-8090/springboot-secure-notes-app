package com.example.notes.repository;

import com.example.notes.model.Note;
import com.example.notes.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUser(User user);

    Optional<Note> findByIdAndUser(Long id, User user);

    List<Note> findByTitleAndUser(String title, User user);
}
