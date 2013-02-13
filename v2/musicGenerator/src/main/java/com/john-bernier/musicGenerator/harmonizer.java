/*
*Author: John Bernier 2/13
*harmonizer.java:
*	builds a harmonized version of a chord progression	
*/

package com.john.bernier.musicGenerator;

import java.util.Random;

class harmonizer{
	
	chordProgression progression; 
	voicingConstants voices;
	voiceLeadingProfile voiceLeading;
	noteGetter getter;
	Random rand;
	harmonizer(chordProgression progression, voicingConstants voices){
		this.progression = progression;
		this.progression.createChordArray();
		this.voices = voices;
		this.voiceLeading = new voiceLeadingProfile(progression.chords,voices);
		getter = new noteGetter(progression.chords,voices);
		rand = new Random();
	}
	
	boolean harmonize(){
		
		//this function needs to be refactored, most of this can be moved
		//into smaller, more readable functions
		chord[] chords = progression.chords;
		int position = 0;
		int voice;
		while(position < chords.length){
			System.out.println("position: "+position);
			//choose a random voice, if there is one left to pick from
			System.out.println("voicesLeft: "+voicesLeft(position,chords));
			if(voicesLeft(position,chords)){
				do{
					voice = rand.nextInt(voices.NUMVOICES);
				}while(voiceLeading.harmonization[position][voice] != voices.UNUSED);
				
				//get all notes from the current chord that are in the voices range
				note[] notesInRange = getter.getValidNotes(position,voice);
				int currentNoteIndex;
				//if there are any notes in the range of the voice, randomly choose one
				System.out.println("notesLeft: "+notesLeft(notesInRange));
				if(notesLeft(notesInRange)){
					do{
						System.out.println("here1");
						currentNoteIndex = rand.nextInt(notesInRange.length);
					}while(!notesLeft(notesInRange[currentNoteIndex]));
					
					//choose midi note version of the note, if there is a valid one to pick
					note currentNote = notesInRange[currentNoteIndex];
					int midiNoteToCheckIndex;
					do{
						System.out.println("here2");
						midiNoteToCheckIndex = rand.nextInt(currentNote.midiNotesInRange.length);
						if(voices.VALID(currentNote.midiNotesInRange[midiNoteToCheckIndex])){
							//setting the chosen midi note to the primary value of the note object
							currentNote.currentValue = currentNote.midiNotesInRange[midiNoteToCheckIndex];
							//check if the note passes all the voice leading rules
							if(voiceLeading.checkNote(currentNote,voice,position)){
								//if the note passes, put the note into the harmonization
								voiceLeading.harmonization[position][voice] = currentNote.currentValue;
								break;
							}
							else{
								//if the note does not pass, set it to INVALID
								currentNote.midiNotesInRange[midiNoteToCheckIndex] = voices.INVALID;
							}
						}
					}while(notesLeft(currentNote));
				}
				if(!voices.VALID(voiceLeading.harmonization[position][voice])){
					voiceLeading.harmonization[position][voice] = voices.INVALID;
				}
			}
			else{
				if(harmonizationComplete(position, chords)){
					position++;		
				}
				else{
					if(!backTrack(position)){
						return false;
					}
					position--;
				}
			}
		}
		return true;
	}
	
	//check if there are voices left to be filled in
	private boolean voicesLeft(int position, chord[] chords){
		for(int i = 0; i < voices.NUMVOICES; i++){
			if(voiceLeading.harmonization[position][i] == voices.UNUSED){
				return true;	
			}
		}
		return false;
	}
	private boolean harmonizationComplete(int position, chord[] chords){
		for(int i = 0; i < voices.NUMVOICES; i++){
			if(!voices.VALID(voiceLeading.harmonization[position][i])){
				return false;	
			}
		}
		return true;
	}
	private boolean notesLeft(note[] notes){
		for(int i = 0; i < notes.length; i++){
			if(notesLeft(notes[i])){
				return true;
			}
		}
		return false;
	}
	private boolean notesLeft(note currentNote){
		for(int i = 0; i < currentNote.midiNotesInRange.length; i++){
			if(voices.VALID(currentNote.midiNotesInRange[i])){
				return true;	
			}
		}
		return false;
	}
	private boolean backTrack(int position){
		if(position - 1 < 0){
			return false;	
		}
		for(int i = 0; i < voices.NUMVOICES; i++){
			voiceLeading.harmonization[position][i] = voices.UNUSED;
			voiceLeading.harmonization[position-1][i] = voices.UNUSED;
		}
		return true;
	}
}
