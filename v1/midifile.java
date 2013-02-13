/**
 * midifile.java
 *
 * A very short program which builds and writes
 * a one-note Midi file.
 *
 * author  Karl Brown
 * last updated 2/24/2003
 */

import java.io.*;
import java.util.*;
import javax.sound.midi.*; // package for all midi classes
public class midifile
{
	//interval determines the lengths of the notes being played
	//currently all notes will be played for the same duration
	long interval;
	long time;
	Sequence s;
	Track t;
	MidiEvent me;
	MetaMessage mt;
	ShortMessage mm;
	public midifile()
	{
		try{
			//set the interval length for all notes in midi ticks
			interval = 240; 
			
			//initialize the time variable
			time = 1;
			//****  Create a new MIDI sequence with 24 ticks per beat  ****
			s = new Sequence(javax.sound.midi.Sequence.PPQ,24);
	
			//****  Obtain a MIDI track from the sequence  ****
			t = s.createTrack();
	
			//****  General MIDI sysex -- turn on General MIDI sound set  ****
			byte[] b = {(byte)0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte)0xF7};
			SysexMessage sm = new SysexMessage();
			sm.setMessage(b, 6);
			me = new MidiEvent(sm,(long)0);
			t.add(me);
	
			//****  set tempo (meta event)  ****
			mt = new MetaMessage();
			byte[] bt = {0x02, (byte)0x00, 0x00};
			mt.setMessage(0x51 ,bt, 3);
			me = new MidiEvent(mt,(long)0);
			t.add(me);
	
			//****  set track name (meta event)  ****
			mt = new MetaMessage();
			String TrackName = new String("midifile track");
			mt.setMessage(0x03 ,TrackName.getBytes(), TrackName.length());
			me = new MidiEvent(mt,(long)0);
			t.add(me);
	
			//****  set omni on  ****
			mm = new ShortMessage();
			mm.setMessage(0xB0, 0x7D,0x00);
			me = new MidiEvent(mm,(long)0);
			t.add(me);
	
			//****  set poly on  ****
			mm = new ShortMessage();
			mm.setMessage(0xB0, 0x7F,0x00);
			me = new MidiEvent(mm,(long)0);
			t.add(me);
	
			//****  set instrument to Piano  ****
			mm = new ShortMessage();
			mm.setMessage(0xC0, 0x00, 0x00);
			me = new MidiEvent(mm,(long)0);
			t.add(me);
		}catch(InvalidMidiDataException e){
			System.out.println("midi failure");
		}

	}
	//give an array of notes, this function will have them
	//all play simultaneously in the midi file
	public void setNotes(int[] notes) {
		//System.out.println("midifile begin ");
		time += interval;
		try{
	
			for(int i = 0; i < notes.length; i++)
			{
				//****  note on - middle C  ****
				mm = new ShortMessage();
				mm.setMessage(0x90,notes[i],0x60);
				me = new MidiEvent(mm,time-interval);
				t.add(me);
			
				//****  note off - middle C - 120 ticks later  ****
				mm = new ShortMessage();
				mm.setMessage(0x80,notes[i],0x40);
				me = new MidiEvent(mm,time);
				t.add(me);
			}
		}catch(Exception e){
			System.out.println("Exception caught " + e.toString());
		}
	}
	public void endfile()
	{
		try{
			//****  set end of track (meta event) 19 ticks later  ****
			mt = new MetaMessage();
			byte[] bet = {}; // empty array
			mt.setMessage(0x2F,bet,0);
			me = new MidiEvent(mt, time+interval);
			t.add(me);
	
			//****  write the MIDI sequence to a MIDI file  ****
			File f = new File("midifile.mid");
			MidiSystem.write(s,1,f);
		} //try
			catch(Exception e)
		{
			System.out.println("Exception caught " + e.toString());
		} //catch
		//System.out.println("midifile end ");
	} //main
	public void setInterval(long newInterval)
	{
		interval = newInterval;	
	}
} //midifile
