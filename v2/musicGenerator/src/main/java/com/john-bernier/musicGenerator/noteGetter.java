/*
*Author: John Bernier 2/13
*chooseNote.java:
*	finds all valid notes of a chord within the range of a voice
*/

package com.john.bernier.musicGenerator;

import java.util.Random;
import java.util.LinkedList;

class noteGetter{

	chord[] chords;
	voicingConstants voices;
	noteGetter(chord[] chords, voicingConstants voices){
		this.chords = chords;
		this.voices = voices;
	}
	
	//returns all of the notes of a chord in range of the given voice
	note[] getValidNotes(int position, int voice){
		//using a linked list because we dont know how many valid notes there will be
		LinkedList<note> validNotes = new LinkedList<note>();
		//for each note in the chord
		for(int i = 0; i < chords[position].length(); i++){
			note currentNote = chords[position].getNote(i);
			note noteInRange = null;
			noteInRange = currentNote.copyNote(noteInRange);
			//will contain all valid octaves of the note
			LinkedList<Integer> midiNotesInRange = new LinkedList<Integer>();
			//for each octave of the note, 
			for(int j = 0; j < currentNote.midiNotes.length; j++){
				int midiNote = currentNote.midiNotes[j];
				//check if it is in the voice's range
				if(voices.inRange(voice,midiNote)){
					midiNotesInRange.addFirst(midiNote);
				}
			}
			//if there are notes in range
			if(midiNotesInRange.size() > 0){
				//put valid octaves of the note in the note object
				noteInRange.midiNotesInRange = LinkedListToArray(midiNotesInRange);
				//put note object into list
				validNotes.addFirst(noteInRange);
			}
		}
		//turn list into an array and return
		return LinkedListToArray(validNotes);
	}
	
	//these two methods turn linkedLists into arrays
	//this is done because the rest of the program works
	//with arrays, not lists
	private note[] LinkedListToArray(LinkedList<note> a){
		return a.toArray(new note[0]);
	}
	private int[] LinkedListToArray(LinkedList<Integer> a){
		Integer[] b;
		int[] returnArray;
		
		returnArray = new int[a.size()];
		b = a.toArray(new Integer[0]);
		for(int i = 0; i < returnArray.length; i++){
			returnArray[i] = (int)b[i];
		}
		return returnArray;
	}
}
