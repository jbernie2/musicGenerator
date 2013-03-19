/*
*Author: John Bernier 2/13
*voiceLeadingProfile.java:
*	A customized set of voiceLeading rules, I will hardcode them for now,
*	but in the future the user will be able to pass in the rules they want to use
*	most likely through some sort of graphical interface or config file
*/

package com.john.bernier.musicGenerator;

class voiceLeadingProfile extends voiceLeadingPrimitives{
	
	chordProgression progression;
	
	voiceLeadingProfile(chordProgression progression, note[][] harmonization, voicingConstants voices){
		super(progression.chords, harmonization, voices);
		this.progression = progression;
	}
	
	boolean checkNote(note currentNote, int voice, int position){
	
		boolean[] rules = new boolean[8];
		int[] emptyArray = new int[0];
		
		//forbids leaps of sevenths
		rules[0] = forbidden(new int[]{10,11}, new int[]{10,11},new int[] {0},
							 currentNote,voice,position);
		
		//forbids parallel fifths
		rules[1] = parallel(new int[]{7},2,currentNote,voice,position);
		//forbids parallel octaves
		rules[2] = parallel(new int[]{12},2,currentNote,voice,position);
		//allows for leaps of fifths, sixths, and octaves, if followed by
		//opposite motion by a second or third
		rules[3] = leap(new int[] {7,8,9,12}, new int[]{7,8,9,12},emptyArray, 
						new int[] {1,2,3,4},new int[] {1,2,3,4}, emptyArray, 
						currentNote,voice,position);
		//restricts largest leap to an octave			   	   	   
		rules[4] = maxLeap(12,currentNote,voice,position);
		
		//disallow voice crossing
		rules[5] = !voiceCrossing(currentNote,voice,position);
		
		//disallow fifths as the root note on triads
		if(checkChordType(position,new String[] {"triad"})){
			if(voice == 0 && checkInterval(currentNote, new int[] {7})){
				rules[6] = false;	
			}
			else{
				rules[6] = true;	
			}
		}
		else{
			rules[6] = true;
		}
		
		//require triads to have root and fifth
		if(checkChordType(position,new String[] {"triad"})){
			rules[7] = requiredNotes(new int[][] {{0},{3,4}}, currentNote, position);
		}else if(checkChordType(position,new String[] {"seventhChord"})){
			rules[7] = requiredNotes(new int[][] {{0},{3,4},{10,11}}, currentNote, position);		
		}
		else{
			rules[7] = true;	
		}
		
		//leading tone resolution
		
		
		return allTrue(rules);
	}
	
	//checks if the note provided is one of the specified intervals
	//this allows for rules like: dont use fifths in root notes
	boolean checkInterval(note currentNote, int[] intervals){
		for(int i = 0; i < intervals.length; i++){
			if(intervals[i] == currentNote.interval){
				return true;	
			}
		}
		return false;
	}
	//checks if a note is of a certain scale degree
	boolean checkSetClass(note currentNote, int[] setClasses){
		for(int i = 0; i < setClasses.length; i++){
			if(setClasses[i] == currentNote.setClass){
				return true;	
			}
		}
		return false;	
	}
	//checks if a chord is of a certain type ie. triad or seventh chord
	boolean checkChordType(int position, String[] types)
	{
		for(int i = 0; i < types.length; i++){
			if(chords[position].getType().equals(types[i])){
				return true;
			}
		}
		return false;
	}
	//checks a chords name against a list
	//allows for rules like: if V chord do _______
	boolean checkChordName(int position, String[] names){
		for(int i = 0; i < names.length; i++){
			if(chords[position].getName().equals(names[i])){
				return true;
			}
		}
		return false;
	}
	//checks if the inputted note satified all the constraints
	boolean allTrue(boolean[] rules){
		for(int i = 0; i < rules.length; i++){
			if(!rules[i]){
				return false;	
			}
		}
		return true;
	}
}
