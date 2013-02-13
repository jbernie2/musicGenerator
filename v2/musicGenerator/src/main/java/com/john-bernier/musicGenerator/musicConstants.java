/*
*Author: John Bernier 2/13
*constants.java:
*	contains all constants used in the package, chose to use final ints instead
*	of enum types so that the values can more easily be used in calculations
*
*/

package com.john.bernier.musicGenerator;

class musicConstants{
	
	//the maximum allowed midi value
	static final int MIDIMAX = 127;
	//minimum allowed midi value
	static final int MIDIMIN = 0;
	//all invalid midi notes will be labeled as -1
	static final int INVALID = -1;
	//all unused midi positions will be labeled -2
	static final int UNUSED = -2;
	//number of notes in the chromatic scale
	static final int NUMNOTES = 12;
	
	boolean VALID(int i){
		if(i >= MIDIMIN && i <= MIDIMAX){
			return true;	
		}
		return false;
	}
}
