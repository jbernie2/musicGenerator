/*
*Author: John Bernier 2/13
*tonalChord.java:
*	tonalChord is the super class for all chords that have keys
*
*/
package com.john.bernier.musicGenerator;

abstract class tonalChord extends chord{
	
	tonalChord(String name){
		super(name);
		relativeKey = 0;
	}
	tonalChord(String name, int[] notes, int root, int relativeKey) throws malformedChordException{
		
		super(name);
		//checks to make sure the relative key is valid
		if(!validKey(relativeKey)){
			throw new malformedChordException("relativeKey needs to be between 0 and 11");
		}
		//makes sure the root of the chord is in the chord
		else if(!noteInChord(notes,root)){
			throw new malformedChordException("chord must contain root");
		}
		//sets all the chord's information
		else{
			setNotes(notes,root,key);
			this.relativeKey = relativeKey;
		}
	}
	//checks for valid key
	boolean validKey(int key){
		if(key >= 0 && key < 12){
			return true;	
		}
		else{
			return false;	
		}
	}
	//checks if a given note is in a chord
	boolean noteInChord(int[] notes, int note){
		for(int i = 0; i < notes.length; i++){
			if(notes[i] == note){
				return true;	
			}
		}
		return false;
	}
	void setKey(int key){
		this.key = key;	
	}
	public String toString(){
		String returnString = super.toString();
		returnString += "Notes in Chord: "+chordSize+"\n";
		returnString +="Relative Key: "+relativeKey+"\n";
		return returnString;
	}
}

