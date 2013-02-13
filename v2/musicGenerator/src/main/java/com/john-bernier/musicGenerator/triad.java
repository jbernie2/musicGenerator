/*
*Author: John Bernier 2/13
*triad.java:
*	a triad is a chord conisting of three notes, usually a root and third and a fifth
*	however, this class does not require those intervals, only that there are 3 notes
*/

package com.john.bernier.musicGenerator;

class triad extends tonalChord{
	
	triad(String name, int[] notes, int root, int relativeKey) throws malformedChordException{
		super(name);
		type = "triad";
		//there are three notes in a triad, this enforces that rule
		chordSize = 3;
		//checks the number of notes in the chord
		if(notes.length != chordSize){
			throw new malformedChordException("triad must contain 3 notes");
		}
		else{
			setNotes(notes,root,key);
			this.relativeKey = relativeKey;
		}
	}
	public String toString(){
		return super.toString();
	}
}
