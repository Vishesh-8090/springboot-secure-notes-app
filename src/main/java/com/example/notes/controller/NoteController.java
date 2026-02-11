package com.example.notes.controller;

import com.example.notes.dto.CreateNoteRequest;
import com.example.notes.dto.NoteResponse;
import com.example.notes.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService){
        this.noteService = noteService;
    }

    @PostMapping
    public ResponseEntity<NoteResponse> createNotes(@RequestBody CreateNoteRequest request){
        NoteResponse note = noteService.createNote(request);
        return new ResponseEntity<>(note, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getAllNotes(){
        List<NoteResponse> notes = noteService.getAllNotes();
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getById(@PathVariable long id){
        NoteResponse noteById = noteService.getNoteById(id);
        return ResponseEntity.ok(noteById);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> updateNote(@PathVariable long id, @RequestBody CreateNoteRequest request){

        NoteResponse response = noteService.updateNote(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable long id){
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }
}
