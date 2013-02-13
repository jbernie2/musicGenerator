/*
*Author: John Bernier 2/13
*chord.java:
*	abstract chord class whose functions set up the basic atributes for the chord
*	object and its subclasses
*
*/
package com.john.bernier.musicGenerator;

abstract class chord{

	musicConstants c;
	String name;
	String type;
	note[] notes;
	int chordSize;
	//key is the key of the whole progression
	//relative key is the key of the chord, V/V for example would have 
	//a relative key of 7, which is the 5th scale degree.
	int key, relativeKey;
	
	//constructors
	//basic constructor, just name
	chord(String name){
		this.name = name;
		key = 0;
		relativeKey = 0;
		c =new musicConstants();
	}
	//this constructor assumes that the first note int the array is the root
	chord(String name, int[] notes){
		this.name = name;
		setNotes(notes,notes[0],key);
	}
	//full constructor name, notes in chord, and root of chord
	chord(String name, int[] notes, int root){
		this.name = name;
		setNotes(notes,root,key);
	}
	
	//setter methods
	void setNotes(int[] notes,int root, int key){
		this.notes = new note[notes.length];
		for(int i = 0; i < notes.length; i++){
			this.notes[i] = new note(notes[i],root,key);
		}
	}
	void updateNotes(int key){
		this.key = key;
		for(int i = 0; i < notes.length; i++){
			notes[i].updateNotes(key);
		}
	}
	
	//getter methods
	String getName(){
		return name;
	}
	String getType(){
		return type;	
	}
	note getNote(int i){
		return notes[i];	
	}
	//Query methods
	boolean noteInChord(note x){
		for(int i =0; i < notes.length; i++){
			if(notes[i].setClass == x.setClass){
				return true;
			}
		}
		return false;
	}
	boolean noteInChord(int x){
		for(int i =0; i < notes.length; i++){
				if(notes[i].setClass == x){
					return true;
				}
			}
			return false;
	}
	//returns the number of notes in the chord
	int length(){
		return chordSize;	
	}
	
	//toString method, for printing
	public String toString(){
		String returnString;
		returnString = "name: "+name+"\n";
		returnString = "notes: \n";
		for(int i = 0; i < notes.length; i++){
			returnString += notes[i].toString();
		}
		return returnString;
	}
}
