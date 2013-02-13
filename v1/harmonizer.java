/*
* Author: John Bernier
* Created: 1/2013
* harmonizer.java:
*		takes an 2d array of chords/notes and generates a harmonized version of
*		the progression based on the rules of counterpoint
*/
//TODO:
//create a git hub repo for this project
//counterpoint functions still need more extensive testing, although they seem
//to be working

import java.util.Random;
import java.util.LinkedList;

public class harmonizer{
	
//***************************
//GLOBAL VARIABLES AND CONSTANTS
//***************************

	//all note values will be midi values
	//invalid/null notes are denoted by a -1
	
	//maximum and minimum allowed midi note values
	final int MIDIMAX = 128;
	final int MIDIMIN = 0;
	final int INVALID = -1;
	
	//number of notes allowed in a chord, currently only 4 notes max
	final int CHORDSIZE = 4;
	
	//number of voices being used in the harmonization
	final int VOICES = 4;
	
	//a perfect 11th is the range of voice
	final int voiceRange = 17; 
	
	// each voice starts a major 6th above the one below it
	final int voiceOverlap = 9;
	
	//the farthest any voice can be from an adjacent voice is a major 10th
	final int MaxVoiceDistance = 16;
								 
	//a perfect 11th below the root note closest to middle C, 
	//is the lowest note allowed.
	final int lowestNote = 29;
	
	//will make referring to elements in the array make more sense
	//for use in the harmonization array
	//refers to the voice 
	final int bass = 0, tenor = 1, alto = 2, soprano = 3;
	//for use in the chords array
	//refers to the scale degrees of a chord
	final int root = 0, third = 1, fifth = 2, seventh = 3;
	
	//lowest note for each of the four voices
	//for lowNote[] [bass] lowest note in the bass range
	//				[alto] lowest note in the alto range
	//				[tenor] lowest note in the tenor range
	//				[soprano] lowest note in the soprano range
	int[] lowNote;
	
	//chords of the progression and the harmonization of it
	//for chords[][], [n][root] is the root
	//				  [n][third] the 3rd
	//				  [n][fifth] the 5th 
	//				  [n][seventh] the 7th
	
	//for harmonization[][],[n][bass] is the bass voice
	//						[n][tenor] is the tenor voice
	//						[n][alto] is the alto voice
	//						[n][soprano] is the soprano voice
	int[][] chords, harmonization;
	
	//key of the progression
	int key;
	
	//closest root note of key to middle c
	int midRoot;
	
	//used to make the harmonizations non-deterministic
	Random rand;
	
	
//***************************
//		CONSTRUCTOR
//***************************
	public harmonizer(int[][] chordArray, int key){
		
		//initialize global variables
		rand = new Random();
		lowNote = new int[4];
		this.key = key;
		calculateLowestNote();
		
		//intialize global data structures
		chords = setClassToMidi(chordArray);
		harmonization = new int[chords.length][VOICES];
		initializeArray(harmonization);
		
		//start harmonization algorithm
		harmonize();
	}
	public int[][] getHarmonization()
	{
		return harmonization;	
	}
	
//***************************
//		SETUP FUNCTIONS
//***************************

	//main harmonization function
	private void harmonize(){
		/*
		//first chord has specific rules, and must be set up properly
		//setFirstChord();
		*/
		for(int i = 0; i < chords.length; i++)
		{
			setHarmony(i);	
		}
	}
	
	/*
	//first chord has specific rules, and must be set up properly to ensure
	//that the rest of the chord progression can develop correctly
	private void setFirstChord(){
		
		//set the first bass note to the root of the chord
		harmonization[bass][0] = chords[0][root] - 12;
		
		//function that will randomly select a note from the chord that is in
		//the voices range
		getValidNote(chords[0],tenor,0);
		getValidNote(chords[0],alto,0);
		
		//only the root and the fifth can be used to start the top voice
		int[] rootAndFifth = {chords[0][root],INVALID,chords[0][fifth],INVALID};
		getValidNote(rootAndFifth,soprano,0);
	}
	*/
	private void setHarmony(int position){
		for(int i = 0; i < VOICES; i++){
			harmonization[position][i] = getValidNote(chords[position],i,position);
		}
	}
	
	//will find a valid note if one exists
	private int getValidNote(int[] chord, int voice, int position)
	{
		int[][] notes = findPossibleNotes(chord,voice);
		int validNote = findValidNote(notes,voice,position);
		
		return validNote;
	}
	
	//for each note in the chord, find all octaves of it that are in the 
	//range of the voice
	private int[][] findPossibleNotes(int[] chord, int voice){
		int[][] notes = new int[CHORDSIZE][];
		for(int i = 0; i < CHORDSIZE; i++){
			notes[i] = notesInRange(voice,chord[i]);
		}
		
		/*
		System.out.println("voice: "+voice);
		System.out.println("voice range: "+lowNote[voice]+" - "+(lowNote[voice]+voiceRange));
		for(int i = 0; i < notes.length; i++){
			for(int j = 0; j < notes[i].length; j++){
				System.out.println("notes["+i+"]["+j+"]: "+notes[i][j]);
			}
		}
		*/
		
		return notes;
		
	}
	
	//checks possible notes against counterpoint rules to see if they are valid
	private int findValidNote(int[][] notes, int voice, int position)
	{
		int note = 0;
		while(note != INVALID){
			//posistion 0 is the note, position 1 is the scale degree
			int[] randNote = chooseRandomNote(notes);
			note = randNote[0];
			int scaleDegree = randNote[1];
			if(checkRules(note,scaleDegree,voice,position)){
				return note;
			}
		}
		return INVALID;
	}
	
	//choose a random note from the provided notes
	private int[] chooseRandomNote(int[][] notes){
		
		//check to make sure that there are still valid notes to choose from
		boolean empty = true;
		for(int i = 0; i < notes.length; i++){
			for(int j = 0; j < notes[i].length; j++){
				if(notes[i][j] != INVALID){
					empty = false;
				}
			}
		}//if there are no valid notes, return
		if(empty){
			int[] choice = {INVALID,INVALID};
			return choice;
		}
		
		//find and choose a random valid note
		int note = INVALID;
		int scaleDegree = INVALID;
		while(note == INVALID){
			//some scale degrees aren't used/ dont have valid
			//notes, so we only want to look at scale degree which have
			//valid notes.
			do{
			scaleDegree = rand.nextInt(CHORDSIZE);
			}while(notes[scaleDegree].length <= 0);
			
			int randNote = rand.nextInt(notes[scaleDegree].length);
			note = notes[scaleDegree][randNote];
		}
		int[] choice = {note,scaleDegree};
		return choice;
	}
	
	//finds the all octaves of a note that are within range of a given voice
	private int[] notesInRange(int voice, int note){
		
		if(note == -1){
			return new int[0];
		}
		
		//we dont know how many valid notes there will be, so we use a list
		LinkedList<Integer> notesInRange = new LinkedList<Integer>();
		
		
		//will be positive if note is above the range
		//negative if the note is below the range
		int highLow = note - lowNote[voice];
		//note is above the low end of the range
		if(highLow >= 0){
			//tempNote will be the first version of note in range of the voice
			//tempNote2 will be all variations of the note which are in range
			int tempNote = note;
			int tempNote2;
			//if note is out of range, lower it until it is in range
			while(!inVoiceRange(tempNote,lowNote[voice])){
				tempNote -= 12;
				//if the note reaches 0, there are no valid notes in the voices range
				if(tempNote < MIDIMIN){
					return new int[0];
				}
			}
			
			//both of the below cases are needed, if, for example the note
			//starts in the middle of the range
			
			//checks if note can still be in range if lowered
			tempNote2 = tempNote;
			while(inVoiceRange(tempNote2,lowNote[voice])){
				notesInRange.addFirst(tempNote2);
				tempNote2 += 12;
				if(tempNote2 > MIDIMAX){
					break;
				}
			}
			
			//checks if note can still be in range if raised
			tempNote2 = tempNote - 12;
			while(inVoiceRange(tempNote2,lowNote[voice])){
				notesInRange.addFirst(tempNote2);
				tempNote2 -= 12;
				if(tempNote < MIDIMIN){
					break;
				}
			}
		}
		//if the note is too low for the range
		else{
			int tempNote = note;
			//find first valid note
			while(!inVoiceRange(tempNote,lowNote[voice])){
				tempNote += 12;	
				//if the note reaches 0, there are no valid notes in the voices range
				if(tempNote < MIDIMIN){
					return null;
				}
			}
			//check that note and higher octaves of it
			while(inVoiceRange(tempNote,lowNote[voice])){
				notesInRange.addFirst(tempNote);
				tempNote += 12;
				if(tempNote > MIDIMAX){
					break;
				}
			}
		}
		return LinkedListToArray(notesInRange);
	}
	
	//checks if a note is within the range of a voice
	private boolean inVoiceRange(int note, int voiceLowNote){
		if(note >= voiceLowNote && note <= voiceLowNote + voiceRange){
			return true;
		}
		else{
			return false;
		}
	}
	
	//sets the lowest note for each voice based on parameters set at top of file
	private void calculateLowestNote(){
		int midC = 60; //midi value for middle C
		
		if((key%12)+midC <= 65){
			midRoot = (key%12)+midC; 
		}
		else{
			midRoot = midC-(key%12);
		}
		
		//setting the lowest note allowed in the harmonization
		lowNote[bass] = midRoot - lowestNote;
		lowNote[tenor] = lowNote[bass]+voiceOverlap;
		lowNote[alto] = lowNote[tenor]+voiceOverlap;
		lowNote[soprano] = lowNote[alto]+voiceOverlap;
	}
	
	
//***************************
//COUNTERPOINT RULE FUNCTIONS 
//***************************

	private boolean checkRules(int note, int scaleDegree, int voice, int position){
		
		int[] emptyArray = new int[0];
		boolean passed = true;
		boolean[] rules = new boolean[10];
		/*
		private boolean forbidden(int[] leapsUp, int[] leapsDown, int[] intervals,
					  int note, int voice, int position){
		*/
		//general rules for all voices 
		//forbids leaps of 7th, and unison notes
		rules[0] = forbidden(new int[]{10,11}, new int[]{10,11},new int[] {0},
							 note,voice,position);
		//forbids parallel fifths
		rules[1] = parallel(new int[]{7},2,note,voice,position);
		//forbids parallel octaves
		rules[2] = parallel(new int[]{12},2,note,voice,position);
		//allows for leaps of fifths, sixths, and octaves, if followed by
		//opposite motion by a second or third
		rules[3] = leap(new int[] {7,8,9,12}, new int[]{7,8,9,12},emptyArray, 
						new int[] {1,2,3,4},new int[] {1,2,3,4}, emptyArray, 
						note,voice,position);
		//restricts largest leap to an octave			   	   	   
		rules[4] = maxLeap(12,note,voice,position);
		
		//disallow voice crossing
		rules[5] = !voiceCrossing(note,voice,position);
		
		//checks voices within a certain range of eachother
		rules[6] = maxVoiceDistance(MaxVoiceDistance,note,voice,position);
		
		//checks to make sure that all required notes are going to be used
		rules[7] = requiredNotes(new int[] {root,third},3,note,position);
		
		rules[8] = requiredNotes(new int[] {root,third,seventh},4,note,position);
		
		//voice specific rules
		//lowest voice can not use scale degree five, unless the chord is 
		//a seventh chord

		if(voice == 0){
			if(scaleDegree == fifth && !seventhChord(position)){
				rules[9] = false;
			}
			else{
				rules[9] = true;
			}
		}
		else{
			rules[9] = true;
		}
		
		//System.out.println(rules[5]);

		for(int i = 0; i < rules.length; i++){
			passed = passed && rules[i];
		}
		return passed;
	}
	
	//generalized function that can check for parallel intervals of any type
	//and can check for parallel sequences of that interval of any length
	private boolean parallel(int[] interval, int sequenceLength, int note, int voice, int position){
		
		//if the chord is at an earlier position than the sequence length
		//that the sequence can not have occured because not enough chords have been
		//played
		if(position < sequenceLength-1){
			return true;
		}
		
		//parallel voices will be an array that has each voice that creates
		//the desired interval with the given voice for as far back as the 
		//the sequence requires
		int[][] parallelVoices = new int[sequenceLength][];
		parallelVoices[0] = findParallels(interval,note,voice,position);
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
		
		/*for testing */
		//boolean sequenceFound = checkParallelSequence(parallelVoices);
		//System.out.println("sequenceFound: "+sequenceFound+", interval: "+interval+", position: "+position);
		//return !sequenceFound;
		
		return !checkParallelSequence(parallelVoices);
	}
	//find the set of notes that are the specified interval away from the provided
	//note
	private int[] findParallels(int[] interval,int note, int voice, int position){
		
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
					if(harmonization[position][i] != INVALID){					
						if(Math.abs(harmonization[position][i] - note) == interval[j]){
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
	private boolean checkParallelSequence(int[][] parallelVoices){
		
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
	private boolean leap(int[] upIntervals, int[] downIntervals, int[] upUpResponse,
						 int[] upDownResponse, int[] downUpResponse,int[] downDownResponse,
						 int note, int voice, int position){
		if(position < 2){
			return true;
		}
		//checking for an upward leap
		if(detectLeap(upIntervals,harmonization[position-1][voice],voice,position-1) == 1){
			//if an upward leap in the previous two notes was detected
			//check if the potential new note follows the guide lines for
			//of counterpoint following the leap
			if(detectLeap(upUpResponse,note,voice,position) == 1){
				return true;
			}
			//normally counterpoint does not allow for upward motion after
			//an upward leap, but if someone wanted to make that part of thier
			//counterpoint rules, they could
			else if(detectLeap(upDownResponse,note,voice,position) == -1){
				return true;
			}
			else{
				return false;	
			}
		}
		//does the same thing as the above block, except for downward leaps
		else if(detectLeap(downIntervals,harmonization[position-1][voice],voice,position-1) == -1){
			if(detectLeap(downUpResponse,note,voice,position) == 1){
				return true;
			}
			else if(detectLeap(downDownResponse,note,voice,position) == -1){
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
	private int detectLeap(int[] interval, int note, int voice, int position){
		
		//checking if the previous note would cause 
		for(int i = 0; i < interval.length; i++){
			if(Math.abs(harmonization[position-1][voice] - note) == interval[i]){
				//checking if it is a leap up or a leap down
				if(harmonization[position-1][voice] - note < 0){
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
	private boolean maxLeap(int maxLeap, int note, int voice, int position){
		
		if(position < 1){
			return true;	
		}
		if(Math.abs(harmonization[position-1][voice] - note) <= maxLeap){
			return true;
		}
		else{
			return false;
		}
	}
	//disallows voice line crossing
	private boolean voiceCrossing(int note, int voice, int position){
		//check to make sure that the note is lower than the voices above it
		for(int i = voice; i < VOICES; i++){
			//dont check voices that haven't been assigned notes
			if(harmonization[position][i] != INVALID){
				if(harmonization[position][i] < note){
					return true;
				}
			}
		}
		//check to make sure that the note is higher than the voices below it
		for(int i = voice; i >= 0; i--){
			//dont check voices that haven't been assigned notes
			if(harmonization[position][i] != INVALID){
				if(harmonization[position][i] > note){
					return true;
				}
			}
		}
		return false;
	}
	//limits how far adjacent voices can be apart
	private boolean maxVoiceDistance(int maxDistance, int note, int voice, int position){
		
		//if the current voice is not the top voice
		if(voice+1 < VOICES){
			//and the note assigned to it is valid
			if(harmonization[position][voice+1] != INVALID){
				//check to see if the distance between the two notes is less than
				//the maximum allowed distance
				if(Math.abs(harmonization[position][voice+1] - note) > maxDistance){
					return false;	
				}
			}
		}
		//if current voice is not the bottom voice
		if(voice-1 > 0){
			//and the note assigned to it is valid
			if(harmonization[position][voice-1] != INVALID){
				//check to see if the distance between the two notes is less than
				//the maximum allowed distance
				if(Math.abs(harmonization[position][voice-1] - note) > maxDistance){
					return false;	
				}
			}
		}
		
		return true;
	}
	//forbids certain types of leaps and intervals
	private boolean forbidden(int[] leapsUp, int[] leapsDown, int[] intervals,
					  int note, int voice, int position){
	
		if(position > 0){
			//checking for invalid upward leaps
			for(int i = 0; i < leapsUp.length; i++){
				if(detectLeap(leapsUp,note,voice,position) == 1){
					return false;
				}
			}
			//checking for invalid downward leaps
			for(int i = 0; i < leapsDown.length; i++){
				if(detectLeap(leapsDown,note,voice,position) == -1){
					return false;
				}
			}
		}
		//checking for invalid intervals
		if(findParallels(intervals,note,voice,position).length > 0){
			return false;
		}
		return true;
	}
	//you can specify which notes are required to be played in chords
	//requiredNotes is actually a list of scale degrees, not notes in the chord
	//this allows the function to generalize
	private boolean requiredNotes(int[] requiredNotes, int chordSize, int note, int position){
		
		//make sure the chord type is correct
		if(getChordSize(chords[position]) != chordSize){
			return true;
		}
		
		//find all required notes that are yet to be used
		int[] notesMissing = notesMissing(requiredNotes,position);
		
		//if there are no notes missing, return true
		if(notesMissing.length == 0){
			return true;	
		}
		//check to see if the given note is one of those required notes
		for(int i = 0; i < notesMissing.length; i++){
			if((note%12) == (notesMissing[i]%12)){
				return true;	
			}
		}
		return false;
	}
	//finds which required scale degrees have not been used yet.
	private int[] notesMissing(int[] requiredNotes,int position){
		
		LinkedList<Integer> notes = new LinkedList<Integer>();
		
		//for each required scaleDegree
		for(int i = 0; i < requiredNotes.length; i++){
			boolean noteNeeded = true;
			//get the corresponding required note
			int note = chords[position][requiredNotes[i]];
			//check to see if the note has been used yet in the harmonization
			for(int j = 0; j < harmonization[position].length; j++){
				//if note is valid
				if(harmonization[position][j] != INVALID){
					if((harmonization[position][j]%12) == (note%12)){
						noteNeeded = false;
					}
				}
			}
			//if the note has not been used, then add it to the list
			if(noteNeeded){
				notes.addFirst(note);	
			}
		}
		return LinkedListToArray(notes);
	}
	private int getChordSize(int[] chord){
	
		int size = 0;
		for(int i = 0; i < chord.length; i++){
			if(chord[i] != INVALID){
				size++;	
			}
		}
		return size;
	}
	
	//checks if a chord is a seventh chord or not
	//this function does not really generalize well
	//because it makes assumptions about the type of 
	//music being harmonized
	//this may be removed
	private boolean seventhChord(int position){
		if(chords[position][3] != -1){
			return true;
		}
		else{
			return false;
		}
	}
//***************************
//		HELPER FUNCTIONS		
//***************************
	//will convert all chords in progression to midi notes around middle c
	private int[][] setClassToMidi(int[][] chords){
		
		int[][] midiChords = new int[chords.length][chords[0].length];
		for(int i = 0; i < midiChords.length; i++){
			for(int j = 0; j < midiChords[0].length; j++){
				//represents a non used note, aka not a 7th chord
				if(chords[i][j] != INVALID){
					midiChords[i][j] = chords[i][j] + midRoot + key;
				}
				else{
					midiChords[i][j] = INVALID;
				}
			}
		}
		return midiChords;
	}
	//sets all positions in an array to -1
	private void initializeArray(int[][] a){
		for(int i = 0; i < a.length; i++){
			for(int j = 0; j < a[0].length; j++){
				a[i][j] = -1;
			}
		}
	}
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
}
