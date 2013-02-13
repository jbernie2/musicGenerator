/*
*Author: John Bernier 2/13
*note.java:
*	the atom of the entire chord hierarchy, although not technically part of said
	hierarchy
*/
package com.john.bernier.musicGenerator;

class note{
	
	//constants used throughout package
	musicConstants c;
	
	//a number between 0 and 11, 0 being the root of the scale
	int setClass;
	//all valid octaves of the note's midi values
	int[] midiNotes;
	//the distance away from the root of the chord
	int interval;
	//the root of the chord that note is in
	int root;
	
	//these fields are helpful for voice leading,
	//only a subset of the midiNotes array will be used for voice leading
	int[] midiNotesInRange;
	//the value that is currently being used in the harmonization
	int currentValue;
	
	note(int note, int root){
		
		c = new musicConstants();
		
		setClass = note;
		this.root = root;
		setInterval(note,root);
		findMidiNotes(note);
	}
	note(int note, int root, int key){
		
		c = new musicConstants();
		
		setClass = note;
		this.root = root;
		setInterval(note,root);
		findMidiNotes(note+key);
	}
	note(){
		;//emtpy constructor	
	}
	
	//set the intervals of the note in terms of half steps away 
	//from the root of the chord, which should be a note in the chord, 
	//although i guess technically it does not have to be
	private void setInterval(int note, int root){
		int interval = ((setClass - root)%c.NUMNOTES);
		if(interval < 0){
			interval += c.NUMNOTES;	
		}
		this.interval = interval;
	}
	
	//setting the midi values for the note
	//midi values range from 0 to 127
	//finding all octaves of the note in the midi range
	private void findMidiNotes(int note){
		int length = ((c.MIDIMAX - setClass)/c.NUMNOTES) + 1;
		midiNotes = new int[length];
		for(int i = 0; i < midiNotes.length; i++){
			midiNotes[i] = i*c.NUMNOTES + setClass;
		}
	}
	
	void updateNotes(int key){
		findMidiNotes(setClass+key);
	}
	//creates an identical copy of the note
	note copyNote(note copy){
		copy = new note();
		copy.setClass = this.setClass;
		copy.interval = this.interval;
		copy.root = this.root;
		copy.currentValue = this.currentValue;
		copy.midiNotes = new int[this.midiNotes.length];
		for(int i = 0; i < this.midiNotes.length; i++)
		{
			copy.midiNotes[i] = this.midiNotes[i];
		}
		if(this.midiNotesInRange != null){
			copy.midiNotesInRange = new int[this.midiNotesInRange.length];
			for(int i = 0; i < this.midiNotesInRange.length; i++){
				copy.midiNotesInRange[i] = this.midiNotesInRange[i];
			}
		}
		return copy;
	}
	public String toString(){
		String returnString = "set class: "+setClass+"\n";
		returnString += "\tinterval above root: "+interval+"\n";
		returnString += "\tmidi notes: ";
		for(int i = 0; i<midiNotes.length; i++){
			returnString += midiNotes[i] + " ";
		}
		returnString += "\n";
		return returnString;
	}
}
