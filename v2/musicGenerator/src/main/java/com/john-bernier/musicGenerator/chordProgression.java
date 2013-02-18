/*
*Author: John Bernier 2/13
*chordProgression.java:
*	contains the chord progression to be harmonized and the harmonized version
*	of the progression	
*
*	this will eventually be able to take in a .pgl file that can generate a
*	chord progression, or a user can pass in chords
*/

package com.john.bernier.musicGenerator;

import java.util.LinkedList;

class chordProgression{

	int key;
	chord[] chords;
	int[][] midiHarmonization;
	note[][] harmonization;
	harmonizer harmonizer;
	voicingConstants voices;
	
	private LinkedList<chord> chordList;
	
	//constructor
	chordProgression(int key){
		this.key = key;
		chordList = new LinkedList<chord>();
	}
	//add chord to progression
	void addChord(chord nextChord){
		nextChord.updateNotes(key);
		chordList.addLast(nextChord);
	}
	//turns the linked list into a chord array
	void createChordArray(){
		chords = chordList.toArray(new chord[0]);
	}
	//creates a harmonizer object
	/*
	void setHarmonizer(voicingConstants voices){
		createChordArray();
		this.voices = voices;
		intializeHarmony(voices.NUMVOICES);
		this.harmonizer = new harmonizer(this,voices);	
	}
	
	//harmonizes the chord progression
	//returns true, if the harmonization completes
	boolean harmonize(){
		return harmonizer.harmonize();
	}
	//create necesary arrays for harmonizing
	void intializeHarmony(int voices){
		midiHarmonization = new int[chords.length][voices];
		harmonization = new note[chords.length][voices];
		initializeArray(midiHarmonization);
	}
	*/
	
	//sets all positions in array to UNUSED, a constant found in musicConstants.java
	void initializeArray(int[][] a){
		
		for(int i = 0; i < a.length; i++){
			for(int j =0; j <a[i].length; j++){
				a[i][j] = voices.UNUSED;
			}
		}
	}
	
	//formatted printing
	public String toString(){
		String out = "";	
		int spacing = getPrintSpacing();
		out += "          ";
		//printing chord names
		for(int i = 0; i < chords.length; i++){
			out += chords[i].name;
			for(int j = 0; j < spacing - chords[i].name.length(); j++){
				out += " ";	
			}
		}
		out += ("\n");
		//printing notes
		for(int i = harmonization[0].length -1; i >= 0; i--){
			if(i < 10){
				out+="voice: "+i+"  ";
			}
			else{
				out+="voice: "+i+" ";	
			}
			for(int j = 0; j < harmonization.length; j++){
				out += harmonization[j][i].currentValue;
				int k;
				if(harmonization[j][i].currentValue < 10){
					k = 1;	
				}
				else{
					k = 2;	
				}
				for(; k < spacing; k++){
					out += " ";
				}
			}
			out += "\n";
		}
		return out;
	}
	//used for evenly spaced printing regardless of the length of chord names
	private int getPrintSpacing(){
		int max = 0;
		for(int i = 0; i < chords.length; i++){
			if(chords[i].name.length() > max){
				max = chords[i].name.length();	
			}
		}
		return max+1;
	}
}
