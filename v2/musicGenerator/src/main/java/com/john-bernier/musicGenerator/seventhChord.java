/*
*Author: John Bernier 2/13
*seventhChord.java:
*	a seventhChord is a chord conisting of 4 notes, a root, third, fifth and a seventh
*	however, this class does not require those intervals, only that there are 4 notes
*/

package com.john.bernier.musicGenerator;

class seventhChord extends tonalChord{
	
	seventhChord(String name, int[] notes, int root, int relativeKey) throws malformedChordException{
		super(name);
		type = "seventhChord";
		//there are four notes in a seventhChord, this enforces that rule
		chordSize = 4;
		//checks the number of notes in the chord
		if(notes.length != chordSize){
			throw new malformedChordException("seventh chord must contain 4 notes");
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
