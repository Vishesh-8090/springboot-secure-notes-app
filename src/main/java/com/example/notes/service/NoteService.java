package com.example.notes.service;

import com.example.notes.dto.CreateNoteRequest;
import com.example.notes.dto.NoteResponse;
import com.example.notes.exception.ResourceNotFoundException;
import com.example.notes.model.Note;
import com.example.notes.model.User;
import com.example.notes.repository.NoteRepository;
import com.example.notes.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteService(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    private User getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            System.out.println("DEBUG: Auth is null or not authenticated");
            throw new ResourceNotFoundException("User not authenticated");
        }

        String username = auth.getName();
        System.out.println("DEBUG: Logged in username from auth: " + username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("DEBUG: User not found in DB for username: " + username);
                    return new ResourceNotFoundException("User not found: " + username);
                });
        System.out.println("DEBUG: Found user in DB: ID=" + user.getId() + ", Username=" + user.getUsername());
        return user;
    }

    public NoteResponse createNote(CreateNoteRequest request) {
        User user = getLoggedInUser();

        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setCreatedAt(LocalDateTime.now());
        note.setUser(user);

        Note savedNote = noteRepository.save(note);
        return mapToResponse(savedNote);
    }

    public List<NoteResponse> getAllNotes() {
        User user = getLoggedInUser();
        List<Note> notes = noteRepository.findByUser(user);
        System.out.println("DEBUG: Found " + notes.size() + " notes for user ID: " + user.getId());
        return notes
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public NoteResponse getNoteById(Long id) {
        User user = getLoggedInUser();
        Note note = noteRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id " + id));

        return mapToResponse(note);
    }

    public NoteResponse updateNote(Long id, CreateNoteRequest request) {
        User user = getLoggedInUser();
        Note note = noteRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id " + id));

        note.setTitle(request.getTitle());
        note.setContent(request.getContent());

        Note updatedNote = noteRepository.save(note);
        return mapToResponse(updatedNote);
    }

    public void deleteNote(Long id) {
        User user = getLoggedInUser();
        Note note = noteRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id " + id));

        noteRepository.delete(note);
    }

    private NoteResponse mapToResponse(Note note) {
        return new NoteResponse(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getCreatedAt());
    }
}
