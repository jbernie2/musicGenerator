/*
*Author: John Bernier 2/13
*voicingConstants.java:
*	rules that control how many voices are you in the harmonization, the
*	range that each voice has, and where they all are in relation to eachother
*/

package com.john.bernier.musicGenerator;

class voicingConstants extends musicConstants{
	
	final int LOWVOICE = 0;
	int NUMVOICES;
	int LOWNOTE;
	
	//for each voice, 
	//[n][0] is the lowest note in range
	//[n][1] is the highest note in range
	//position 0 is the lowest voice
	int[][] voices;
	
	//useful if you want to make the ranges and overlaps of all the voices different
	voicingConstants(int NUMVOICES, int LOWNOTE)throws invalidNoteException{
		intialize(NUMVOICES, LOWNOTE);
	}
	//sets all voices range and overlap to be the sam
	voicingConstants(int NUMVOICES, int LOWNOTE, int voiceRange, int voiceOverlap)throws invalidNoteException{
		
		intialize(NUMVOICES, LOWNOTE);
		setRange(0,INVALID,voiceRange,voiceOverlap);
		for(int i = 1; i < voices.length; i++){
			setRange(i,voices[i-1][1],voiceRange,voiceOverlap);
		}
	}
	
	//sets the global variables and initializes the global array
	void intialize(int NUMVOICES, int LOWNOTE) throws invalidNoteException{
		if(NUMVOICES <= 0 || LOWNOTE < 0){
			throw new invalidNoteException("input must be positive");
		}
		this.NUMVOICES = NUMVOICES;
		this.LOWNOTE = LOWNOTE;
		voices = new int[NUMVOICES][2];
		for(int i = 1; i < voices.length; i++){
			voices[i][0] = INVALID;
			voices[i][1] = INVALID;
		}
	}
	//sets the range of an individual voice
	//voiceRange denotes the distance between a voices highest and lowest
	//possible notes
	//voiceOverlap denotes how much each voice overlaps with each adjacent voice
	void setRange(int voice, int lowerVoiceLimit, int voiceRange, int voiceOverlap)throws invalidNoteException{
		if(!(voice < NUMVOICES && voice >= 0)){
			throw new invalidNoteException("voice must be between 0 and "+NUMVOICES);
		}
		
		if(voice == LOWVOICE){
			voices[LOWVOICE][0] = LOWNOTE;
			voices[LOWVOICE][1] = LOWNOTE+voiceRange;
		}
		else{
			voices[voice][0] = lowerVoiceLimit - voiceOverlap;
			voices[voice][1] = voices[voice][0] + voiceRange;
		}
	}
	//checks if a note is in the range of a voice
	boolean inRange(int voice, int note){
		if(voices[voice][0] <= note && voices[voice][1] >= note){
			return true;	
		}
		return false;
	}
}

