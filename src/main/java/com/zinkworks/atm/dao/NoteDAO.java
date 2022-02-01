package com.zinkworks.atm.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zinkworks.atm.entities.Note;
import com.zinkworks.atm.repositories.NoteRepository;

@Service
public class NoteDAO {

	@Autowired
	private NoteRepository repo;
	
	public List<Note> getAllNotesAvailable(){		
		return repo.findByAmountGreaterThanOrderByValueDesc(0);
	}
	
	public Integer getTotalAmountAvailable() {
		List<Note> notesAvailable = repo.findByAmountGreaterThanOrderByValueDesc(0);
		return notesAvailable.stream().mapToInt(note -> note.getValue() * note.getAmount()).sum();
	}
	
	public List<Note> updateNotes(List<Note> listUpdated) {
		return repo.saveAll(listUpdated);
	}
	
}
