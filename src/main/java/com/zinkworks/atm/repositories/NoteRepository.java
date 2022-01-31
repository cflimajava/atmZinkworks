package com.zinkworks.atm.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zinkworks.atm.entities.Note;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer> {
	
	public List<Note> findByAmountGreaterThanOrderByValueDesc(int noteAmount);
}
