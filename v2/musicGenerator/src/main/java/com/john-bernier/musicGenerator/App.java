/*
*Author: John Bernier 2/13
*App.java:
*	The main function for testing the program
*
*TODO:
*	1. make voiceLeadingPrimatives.java work with a note[][] instead of int[][]
*	2. unit tests
*	3. continue to refactor code
*	4. add more voice leading primitives
*	5. move midi file creation to a separate file, separate package?
*	6. possibly create sub packages for major components
*/

package com.john.bernier.musicGenerator;

public class App 
{
    public static void main(String[] args)
    {
        System.out.println("constructing chords");
        
        chord[] chords = new chord[3];
        chordProgression progression = new chordProgression(0);
        
        try{
        	//creating a chord progession to harmonize
			chords[0] = new triad("I",new int[]{0,4,7},0,0);
			chords[1] = new seventhChord("V7",new int[]{7,11,2,5},7,0);
			chords[2] = new triad("I",new int[]{0,4,7},0,0);
			for(int i = 0; i < chords.length; i++){
				progression.addChord(chords[i]);	
			}
			
			//creating a voicing profile
			voicingConstants voices = new voicingConstants(4,31,17,9);
			harmonizer h = new harmonizer(progression,voices);
			h.harmonize();
			
			System.out.println(progression.toString());
			
			note[][] harmonization = progression.harmonization;
			
			//outputting a midi file
			//will move this to another file at some point
			midifile m = new midifile();
			for(int i = 0; i < harmonization.length; i++){
				int[] chord = new int[harmonization[i].length];
				for(int j = 0; j < harmonization[i].length; j++){
					chord[j] = harmonization[i][j].currentValue;
				}
				m.setNotes(chord);	
			}
			m.endfile();
			
		}catch(invalidNoteException e){
				System.out.println(e);	
				return;
		}
		catch(malformedChordException e){
			System.out.println(e);	
			return;
		}
	}
}
