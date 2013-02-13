/*
*Author: John Bernier 2/13
*invalidNoteException.java:
*	is thrown when an invalid note is attempted to be used, 
*	generally this means that the note is negative or 
*	over 127 (the highest midi note)
*/

package com.john.bernier.musicGenerator;

class invalidNoteException extends Exception{
	invalidNoteException(String message){
		super(message);
	}
}
