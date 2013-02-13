/*
*Author: John Bernier 2/13
*malformedChordException.java:
*	thrown when a chord has invalid notes, or an invalid number of notes
*/

package com.john.bernier.musicGenerator;

class malformedChordException extends Exception{
	malformedChordException(String message){
		super(message);	
	}
}
