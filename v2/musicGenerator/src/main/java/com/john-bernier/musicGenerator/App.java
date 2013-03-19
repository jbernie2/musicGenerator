/*
*Author: John Bernier 2/13
*App.java:
*	The main function for testing the program
*
*TODO:
*	test leading tone resolution functionality in voiceLeadingPrimitives
*
*	1. add more voice leading primitives (leading tone resolution)
*	2. unit tests
*	3. continue to refactor code
*	4. pgl integration
*	5. possibly create sub packages for major components
*	6. figure out how to pass in custom voice leading profiles
*	7. might need a new note choosing algorithm, other than backtracking
*		constraint satisfaction could replace both the note choosing and
*		the rule checking
*/

package com.john.bernier.musicGenerator;

public class App 
{
    public static void main(String[] args)
    {
        System.out.println("constructing chords");
        
        chord[] chords = new chord[3];
        chordProgression progression = new chordProgression(0);
        voicingConstants voices;
        
        try{
        	//creating a chord progession to harmonize
			chords[0] = new triad("I",new int[]{0,4,7},0,0);
			chords[1] = new seventhChord("V7",new int[]{7,11,2,5},7,0);
			chords[2] = new triad("I",new int[]{0,4,7},0,0);
			for(int i = 0; i < chords.length; i++){
				progression.addChord(chords[i]);	
			}
			
			//creating a voicing profile
			 voices = new voicingConstants(4,31,17,9);
			
		}catch(invalidNoteException e){
				System.out.println(e);	
				return;
		}
		catch(malformedChordException e){
			System.out.println(e);	
			return;
		}
		
		//harmonize chord progression
		progression.setHarmonizer(voices);
		progression.harmonize();
		
		//format printing the chord progression
		System.out.println(progression.toString());
		
		//outputting a midi file of the progression
		progression.outputMidi();
	}
}
