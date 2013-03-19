/*
*Author: John Bernier 2/13
*harmonizer.java:
*	builds a harmonized version of a chord progression	
*/

package com.john.bernier.musicGenerator;

import java.util.Random;

class harmonizer{
	
	Random rand;
	noteGetter getter;
	voicingConstants voices;
	chordProgression progression; 
	voiceLeadingProfile voiceLeading;
	
	note[][] harmonization;
	
	harmonizer(chordProgression progression, voicingConstants voices){
		
		this.voices = voices;
		this.progression = progression;
		this.progression.createChordArray();
		this.voiceLeading = new voiceLeadingProfile(progression, progression.harmonization, voices);
		getter = new noteGetter(progression.chords,voices);
		rand = new Random();
		this.harmonization = voiceLeading.harmonization;
		progression.harmonization = voiceLeading.harmonization;
	}
	
	//a backtracking algorithm that tries to find suitable notes
	//for harmonizing the chord progession using the rules defined in
	//voiceLeadingPofile.java
	boolean harmonize(){
		
		//each chord has three chances to be harmonized before the algorithm
		//backtracks to the previous chord
		int MAXITERATIONS = 10;
		int iteration = 0;
		
		int position = 0, voice;
		chord[] chords = progression.chords;
		//this whole big loop just randomly chooses a valid midi note and
		//checks if the note adheres to the counterpoint rules
		while(position < chords.length){
			
			//choose a random voice, if there is one left to pick from
			if(voicesLeft(position,chords)){
				//choose a voice to harmonize
				voice = chooseVoice(position);

				//get all notes from the current chord that are in the voices range
				note[] notesInRange = getter.getValidNotes(position,voice);
				
				//if there are any notes in the range of the voice, randomly choose one
				if(notesLeft(notesInRange)){
					
					//choose a note from all the notes in range of the voice
					note currentNote = chooseNote(notesInRange);
					do{
						//choose midi note version of the note
						int midiNoteIndex = chooseMidiNote(currentNote);
						currentNote.currentValue = currentNote.midiNotesInRange[midiNoteIndex];
						
						//check if the chose note passes all voice leading constraints
						if(voiceLeading.checkNote(currentNote,voice,position)){
							
							//if the note passes, put the note into the harmonization
							harmonization[position][voice] = currentNote;
							
							break;
						}
						else{
							//if the note does not pass, set it to INVALID
							currentNote.midiNotesInRange[midiNoteIndex] = voices.INVALID;
						}	
					}while(notesLeft(currentNote));
				}
				if(!voices.VALID(harmonization[position][voice].currentValue)){
					harmonization[position][voice].currentValue = voices.INVALID;
					if(iteration < MAXITERATIONS){
						resetHarmonization(position);
						iteration++;
					}
				}
			}
			else{
				//if whole chord has been harmonized, continue to next chord
				if(harmonizationComplete(position, chords)){
					position++;
					iteration = 0;
				}
				else{
					//try to harmonize the chord again
					if(iteration < MAXITERATIONS){
						resetHarmonization(position);
						iteration++;
					}
					else{
						//try to backtrack
						if(!backTrack(position)){
							return false;
						}
						else{
							position--;
							iteration = 0;
						}
					}
				}
			}
		}
		return true;
	}
	
	//randomly choose a voice to harmonize
	private int chooseVoice(int position){
		int voice;
		do{
			voice = rand.nextInt(voices.NUMVOICES);
		}while(harmonization[position][voice].currentValue != voices.UNUSED);
		return voice;
	}
	//randomly choose a note object from the array of note objects
	private note chooseNote(note[] notesInRange){
		int noteIndex;
		do{
			noteIndex = rand.nextInt(notesInRange.length);
		}while(!notesLeft(notesInRange[noteIndex]));
		
		return notesInRange[noteIndex];
	}
	//randomly choose a midiNote from the note object
	private int chooseMidiNote(note currentNote){
		int midiNoteIndex;
		do{
			midiNoteIndex = rand.nextInt(currentNote.midiNotesInRange.length);
		}while(!voices.VALID(currentNote.midiNotesInRange[midiNoteIndex]));
		
		return midiNoteIndex;
	}
	//check if there are voices left to be filled in
	private boolean voicesLeft(int position, chord[] chords){
		for(int i = 0; i < voices.NUMVOICES; i++){
			if(harmonization[position][i].currentValue == voices.UNUSED){
				return true;	
			}
		}
		return false;
	}
	//checks if all voices have a valid note at a position 
	private boolean harmonizationComplete(int position, chord[] chords){
		for(int i = 0; i < voices.NUMVOICES; i++){
			if(!voices.VALID(harmonization[position][i].currentValue)){
				return false;	
			}
		}
		return true;
	}
	//checks if a chord has any notes left to be tried
	private boolean notesLeft(note[] notes){
		for(int i = 0; i < notes.length; i++){
			if(notesLeft(notes[i])){
				return true;
			}
		}
		return false;
	}
	//checks if there are any octaves of a note left to try
	private boolean notesLeft(note currentNote){
		for(int i = 0; i < currentNote.midiNotesInRange.length; i++){
			if(voices.VALID(currentNote.midiNotesInRange[i])){
				return true;	
			}
		}
		return false;
	}
	//determines if backtracking is possible
	//the algorithm can not back track if it is already on the first chord
	private boolean backTrack(int position){
		if(position - 1 < 0){
			return false;	
		}
		for(int i = 0; i < voices.NUMVOICES; i++){
			resetHarmonization(position);
			resetHarmonization(position -1);
		}
		return true;
	}
	//resets a single chord position in the harmonization
	private void resetHarmonization(int position){
		for(int i = 0; i < voices.NUMVOICES; i++){
			harmonization[position][i].currentValue = voices.UNUSED;
		}
	}
}
