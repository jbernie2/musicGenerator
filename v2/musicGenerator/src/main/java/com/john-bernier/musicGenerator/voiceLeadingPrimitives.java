/*
*Author: John Bernier 2/13
*voiceLeadingPrimatives.java:
*	basic/generalized voice leading rule functions 
*
*/

package com.john.bernier.musicGenerator;

import java.util.LinkedList;

class voiceLeadingPrimitives{
	
	chord[] chords;
	voicingConstants voices;
	//int[][] harmonization;
	note[][] harmonization;
	
	voiceLeadingPrimitives(chord[] chords, note[][] harmonization, voicingConstants voices){
		this.chords = chords;
		this.voices = voices; 
		harmonization = new note[chords.length][voices.NUMVOICES];
		initialize(harmonization);
		this.harmonization = harmonization;
	}
	//generalized function that can check for parallel intervals of any type
	//and can check for parallel sequences of that interval of any length
	boolean parallel(int[] interval, int sequenceLength, note currentNote, int voice, int position){
		
		//if the chord is at an earlier position than the sequence length
		//that the sequence can not have occured because not enough chords have been
		//played
		int note = currentNote.currentValue;
		
		if(position < sequenceLength-1){
			return true;
		}
		
		//parallel voices will be an array that has each voice that creates
		//the desired interval with the given voice for as far back as the 
		//the sequence requires
		int[][] parallelVoices = new int[sequenceLength][];
		parallelVoices[0] = findParallels(interval,currentNote,voice,position);
		//if the sequence length is 1, than the rule is just checking to see if
		//there are any intervals of the provided type between the voice and 
		//the current harmony of the current chord
		if(sequenceLength == 1 && parallelVoices[0].length > 0){
			return false;
		}
		for(int i = 1; i < sequenceLength; i++){
			parallelVoices[i] = findParallels(interval,harmonization[position-i][voice],voice,position-i);
		}
		
		//returning the negation because if the parallel sequence is found
		//this function returns true, which means the provided note doesn't 
		//pass the test
		return !checkParallelSequence(parallelVoices);
	}
	//find the set of notes that are the specified interval away from the provided
	//note
	int[] findParallels(int[] interval, note currentNote, int voice, int position){
		
		int note = currentNote.currentValue;
		LinkedList<Integer> parallels = new LinkedList<Integer>();
		
		for(int i = 0; i < harmonization[position].length;i++){
			//dont want to compare the note to itself
			//could cause a problem with a rule checking for unisons
			if(i == voice){
				;//do nothing	
			}
			//if the value of one note minus the other is the specified interval
			//than those two notes make up that interval
			//the variable interval is an array because non-perfect intervals 
			//like the third can be either minor 3, or major 4. Having the array
			//allows for them to checked for
			else{
				boolean intervalFound = false;
				for(int j = 0; j < interval.length; j++){
					//make sure that a valid note is being checked
					if(voices.VALID(harmonization[position][i].currentValue)){					
						if(Math.abs(harmonization[position][i].currentValue - note) == interval[j]){
							intervalFound = true;
						}
					}
				}
				if(intervalFound){
					parallels.addFirst(i);
				}
			}
		}
		return LinkedListToArray(parallels);
	}
	boolean checkParallelSequence(int[][] parallelVoices){
		
		//checks to see if the same voice consistently creates the same 
		//interval with the voice in question for the duration of the sequence
		boolean sequenceFound = false;
		//try all voices in the last chord
		for(int i = 0; i < parallelVoices[0].length; i++){
			//against all the same intervals found in the other chords
			for(int j = 1; j < parallelVoices.length; j++){
				for(int k = 0; k < parallelVoices[j].length; k++){
					//if the same interval occurs between the same voices
					//then parallel movement has occurred
					if(parallelVoices[0][i] == parallelVoices[j][k]){
						sequenceFound = true;
					}
				}
				//if parallel movement was found with this voice
				//and it is not the last chord to be checked, 
				//try the next chord
				if(sequenceFound && j != parallelVoices.length - 1){
					sequenceFound = false;
				}
				//otherwise try the next voice
				else{
					//breaking out of inner loop
					j = parallelVoices.length;
				}
			}
		}
		return sequenceFound;
	}
	//so there are six arrays here, the first two represent valid interval jumps that
	//can be made, either up (to a higher note), or down (to a lower note)
	//the other four arrays represent valid responses to each type of jump,
	//for example the upDownResponse is a list of valid descending intervals 
	//following a leap upwards
	boolean leap(int[] upIntervals, int[] downIntervals, int[] upUpResponse,
						 int[] upDownResponse, int[] downUpResponse,int[] downDownResponse,
						 note currentNote, int voice, int position){
		int note = currentNote.currentValue;
	
		if(position < 2){
			return true;
		}
		//checking for an upward leap
		if(detectLeap(upIntervals,harmonization[position-1][voice],voice,position-1) == 1){
			//if an upward leap in the previous two notes was detected
			//check if the potential new note follows the guide lines for
			//of counterpoint following the leap
			if(detectLeap(upUpResponse,currentNote,voice,position) == 1){
				return true;
			}
			//normally counterpoint does not allow for upward motion after
			//an upward leap, but if someone wanted to make that part of thier
			//counterpoint rules, they could
			else if(detectLeap(upDownResponse,currentNote,voice,position) == -1){
				return true;
			}
			else{
				return false;	
			}
		}
		//does the same thing as the above block, except for downward leaps
		else if(detectLeap(downIntervals,harmonization[position-1][voice],voice,position-1) == -1){
			if(detectLeap(downUpResponse,currentNote,voice,position) == 1){
				return true;
			}
			else if(detectLeap(downDownResponse,currentNote,voice,position) == -1){
				return true;
			}
			else{
				return false;	
			}
		}
		else{
			return true;
		}
	}
	//checks if a leap has occurred
	int detectLeap(int[] interval, note currentNote, int voice, int position){
		
		int note = currentNote.currentValue;
		
		//checking if the previous note would cause 
		for(int i = 0; i < interval.length; i++){
			if(Math.abs(harmonization[position-1][voice].currentValue - note) == interval[i]){
				//checking if it is a leap up or a leap down
				if(harmonization[position-1][voice].currentValue - note < 0){
					//leap up returns positive
					return 1;
				}
				else{
					//leap down returns negative
					return -1;
				}
			}
		}
		//no leap
		return 0;
	}
	//limits how far a voice can leap
	boolean maxLeap(int maxLeap, note currentNote, int voice, int position){
		
		int note = currentNote.currentValue;
		
		if(position < 1){
			return true;	
		}
		if(Math.abs(harmonization[position-1][voice].currentValue - note) <= maxLeap){
			return true;
		}
		else{
			return false;
		}
	}
	//disallows voice line crossing
	boolean voiceCrossing(note currentNote, int voice, int position){
		
		int note = currentNote.currentValue;
		
		//check to make sure that the note is lower than the voices above it
		for(int i = voice; i < voices.NUMVOICES; i++){
			//dont check voices that haven't been assigned notes
			if(voices.VALID(harmonization[position][i].currentValue)){
				if(harmonization[position][i].currentValue < note){
					return true;
				}
			}
		}
		//check to make sure that the note is higher than the voices below it
		for(int i = voice; i >= 0; i--){
			//dont check voices that haven't been assigned notes
			if(voices.VALID(harmonization[position][i].currentValue)){
				if(harmonization[position][i].currentValue > note){
					return true;
				}
			}
		}
		return false;
	}
	//limits how far adjacent voices can be apart
	boolean maxVoiceDistance(int maxDistance,note currentNote, int voice, int position){
		
		int note = currentNote.currentValue;
		
		//if the current voice is not the top voice
		if(voice+1 < voices.NUMVOICES){
			//and the note assigned to it is valid
			if(voices.VALID(harmonization[position][voice+1].currentValue)){
				//check to see if the distance between the two notes is less than
				//the maximum allowed distance
				if(Math.abs(harmonization[position][voice+1].currentValue - note) > maxDistance){
					return false;	
				}
			}
		}
		//if current voice is not the bottom voice
		if(voice-1 > 0){
			//and the note assigned to it is valid
			if(voices.VALID(harmonization[position][voice-1].currentValue)){
				//check to see if the distance between the two notes is less than
				//the maximum allowed distance
				if(Math.abs(harmonization[position][voice-1].currentValue - note) > maxDistance){
					return false;	
				}
			}
		}
		
		return true;
	}
	//forbids certain types of leaps and intervals
	boolean forbidden(int[] leapsUp, int[] leapsDown, int[] intervals,
					  note currentNote, int voice, int position){
		
		int note = currentNote.currentValue;
	
		if(position > 0){
			//checking for invalid upward leaps
			for(int i = 0; i < leapsUp.length; i++){
				if(detectLeap(leapsUp,currentNote,voice,position) == 1){
					return false;
				}
			}
			//checking for invalid downward leaps
			for(int i = 0; i < leapsDown.length; i++){
				if(detectLeap(leapsDown,currentNote,voice,position) == -1){
					return false;
				}
			}
		}
		//checking for invalid intervals
		if(findParallels(intervals,currentNote,voice,position).length > 0){
			return false;
		}
		return true;
	}
	
	//checks to see if a chord's voiceleading has the required intervals
	//required intervals is a 2d array, each position is a list of notes required
	//by the chord, each list only needs one note from it in the chord
	//this allows for rules like "harmonization requires the 3rd and 7th of the chord,
	//which could then be represented by {{3,4},{10,11}}, the numbers being the 
	//chromatic distance of those intervals from the root, this also allows for the
	//same rule to work for both major and minor chords, or variations of the same chord
	boolean requiredNotes(int[][] requiredIntervals, note currentNote, int position){
		
		//all intervals that are already used in the harmonization
		//get removed from the requiredIntervals array 
		notesAlreadyInChord(requiredIntervals,position);
		
		//check if there are any required notes left to be used
		if(notesLeft(requiredIntervals)){
			//see if the note is needed in the chord
			if(noteRequired(requiredIntervals,currentNote)){
				return true;		
			}
			else{
				return false;	
			}
		}
		else{
			return true;
		}
	}
	//sub-method for requiredNotes()
	//all intervals that are already used in the harmonization
	//get removed from the requiredIntervals array 
	private void notesAlreadyInChord(int[][] requiredIntervals, int position){
		for(int i = 0; i < harmonization[position].length; i++){
			for(int j = 0; j < requiredIntervals.length; j++){
				for(int k = 0; k < requiredIntervals[j].length; k++){
					if(harmonization[position][i].interval == requiredIntervals[j][k]){
						requiredIntervals[j][k] = voices.UNUSED;
					}
				}
			}
		}
	}
	//sub-method for requiredNotes()
	//check if there are any required notes left to be used
	private boolean notesLeft(int[][] requiredIntervals){
		boolean notesLeft = true;
		for(int i = 0; i < requiredIntervals.length; i++){
			for(int j = 0; j < requiredIntervals[i].length; j++){
				if(requiredIntervals[i][j] == voices.UNUSED){
					notesLeft = false;
				}
			}
			if(notesLeft){
				return true;	
			}
		}
		return false;
	}
	//sub-method for requiredNotes()
	//see if the note is needed in the chord
	private boolean noteRequired(int[][] requiredIntervals, note currentNote){
		for(int j = 0; j < requiredIntervals.length; j++){
			for(int k = 0; k < requiredIntervals[j].length; k++){
				if(requiredIntervals[j][k] == currentNote.currentValue){
					return true;	
				}
			}
		}
		return false;
	}
	//allows for given scale degrees to have to resolve in certain ways
	//this is useful for leading tones, but could be used for other things
	boolean noteResolution(int[] scaleDegrees, int[] resolutionsUp, int[] resolutionsDown,
						   note currentNote, int position, int voice){
		if(position < 1){
			return true;
		}
		if(checkScaleDegree(position-1,voice,scaleDegrees)){
			for(int i = 0; i < resolutionsUp.length; i++){
				if(Math.abs(currentNote.currentValue-harmonization[position-1][voice].currentValue) == resolutionsUp[i]){
					return true;
				}
			}
			for(int i = 0; i < resolutionsDown.length; i++){
				if(Math.abs(currentNote.currentValue-harmonization[position-1][voice].currentValue) == resolutionsDown[i]){
					return true;
				}
			}
			return false;
		}
		return true;
	}
	boolean checkScaleDegree(int position, int voice, int[] scaleDegrees){
		for(int i = 0; i < scaleDegrees.length; i++){
			if((chords[position].relativeKey + harmonization[position][voice].setClass)%12 == scaleDegrees[i]){
				return true;
			}
		}
		return false;
	}
	
	//HELPER FUNCTIONS
	
	//Theres a messy conversion between LinkedLists and int[]
	//so I made a function to hide the ugliness
	private int[] LinkedListToArray(LinkedList<Integer> a){
		Integer[] b;
		int[] returnArray;
		
		returnArray = new int[a.size()];
		b = a.toArray(new Integer[0]);
		for(int i = 0; i < returnArray.length; i++){
			returnArray[i] = (int)b[i];
		}
		return returnArray;
	}
	//intializes an array to the constant UNUSED
	private void initialize(note [][] a){
		for(int i = 0; i < a.length; i++){
			for(int j = 0; j < a[i].length; j++){
				a[i][j] = new note();
				a[i][j].currentValue = voices.UNUSED;
			}
		}
	}
}
	
