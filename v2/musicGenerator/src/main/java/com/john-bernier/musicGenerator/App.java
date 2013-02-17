package com.john.bernier.musicGenerator;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args)
    {
        System.out.println("constructing chords");
        
        chord[] chords = new chord[3];
        int[][] harmonization;
        
        try{
			chords[0] = new triad("I",new int[]{0,4,7},0,0);
			chords[1] = new seventhChord("V7",new int[]{7,11,2,5},7,0);
			chords[2] = new triad("I",new int[]{0,4,7},0,0);
		
			voicingConstants voices = new voicingConstants(4,31,17,9);
			noteGetter getNotes = new noteGetter(chords,voices);
		
			/*
			System.out.println(chords[0].toString());
			System.out.println(chords[1].toString());
			System.out.println(chords[2].toString());
			
			for(int i = 0; i < chords.length; i++){
				for(int j = 0; j < voices.NUMVOICES; j++){
					System.out.println("chord: "+chords[i].name);
					System.out.println("voice:  "+j);
					note[] notes = getNotes.getValidNotes(i,j);
					for(int k = 0; k < notes.length; k++){
						System.out.println(notes[k].toString());
					}
				}
			}
			*/
			
			chordProgression progression = new chordProgression(0);
			for(int i = 0; i < chords.length; i++){
				progression.addChord(chords[i]);	
			}
			harmonizer h = new harmonizer(progression,voices);
			boolean worked = h.harmonize();
			harmonization = h.voiceLeading.harmonization;
			if(worked){
				for(int i = harmonization[0].length -1; i >= 0; i--){
					for(int j = 0; j < harmonization.length; j++){
						System.out.print(harmonization[j][i]+"  ");
					}
					System.out.println();
				}
			}
			else{
				System.out.println("harmony: false");	
			}
			
		}catch(invalidNoteException e){
				System.out.println(e);	
				return;
		}
		catch(malformedChordException e){
			System.out.println(e);	
			return;
		}
		
		
		midifile m = new midifile();
		for(int i = 0; i < harmonization.length; i++){
			int[] chord = new int[harmonization[i].length];
			for(int j = 0; j < harmonization[i].length; j++){
				chord[j] = harmonization[i][j];
			}
			m.setNotes(chord);	
		}
		m.endfile();
	}
}
