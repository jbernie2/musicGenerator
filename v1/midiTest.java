public class midiTest
{
	public static void main(String[] args)
	{
		int key = 0;
		
		/*
		//Progression: i V7 i
		int[][] chords = {{0,3,7,-1},{7,11,2,5},{0,3,7,-1}};
		*/
		
		//Progression i iv7 V7/V V7 VI V7 i
		int[][] chords = {{0,3,7,-1},{5,8,0,3},{2,6,9,0},{7,11,2,5},
						  {10,0,5,-1},{7,11,2,5},{0,3,7,-1}};
						  
		/*
		//atonal progression
		int [][] chords = {{0,6,4,-1},{5,11,1,-1},{7,8,10,-1},{9,3,2,-1}};
		*/
		
		harmonizer h = new harmonizer(chords,key);
		int[][] harmonization = h.getHarmonization();
		
		for(int i = harmonization[0].length - 1; i >= 0 ; i--){
			for(int j = 0; j < harmonization.length; j++){
				int note = harmonization[j][i];
				if(note > 9){
					System.out.print(note+" ");
				}
				else{
					System.out.print(note+"  ");
				}
			}
			System.out.println();
		}
		
		
		midifile m = new midifile();
		for(int i = 0; i < harmonization.length; i++){
			int[] chord = new int[harmonization[i].length];
			for(int j = 0; j < harmonization[i].length; j++){
				chord[j] = harmonization[i][j];
			}
			m.setNotes(chord	);	
		}
		m.endfile();
	}
}
