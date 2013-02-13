/*
*Author: John Bernier 2/13
*chordProgression.java:
*	this will eventually be able to take in a .pgl file that can generate a
*	chord progression, or a user can pass in chords
*/

package com.john.bernier.musicGenerator;

import java.util.LinkedList;

class chordProgression{

	int key;
	LinkedList<chord> chordList;
	chord[] chords;
	chordProgression(int key){
		this.key = key;
		chordList = new LinkedList<chord>();
	}
	void addChord(chord nextChord){
		nextChord.updateNotes(key);
		chordList.addLast(nextChord);
	}
	void createChordArray(){
		chords = chordList.toArray(new chord[0]);
	}
}
