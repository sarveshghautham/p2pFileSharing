package p2pFileSharing;
import java.io.*;
import java.util.ArrayList;

class FileHandler {

	String inputFileName;
	long fileSize;
	int pieceSize;
	int pieceCount;
	
	public boolean CheckHasFile (int peerID) throws IOException {
		
		String fileName = System.getProperty("user.dir")+"/p2pFileSharing/PeerInfo.cfg";
		//String fileName = System.getProperty("user.dir")+"/src/p2pFileSharing/PeerInfo.cfg";
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		String line="";
		String []tokens = new String[4];
		
		while ((line = br.readLine()) != null) {
			tokens = line.split("\\s+");
			if (peerID == Integer.parseInt(tokens[0])) {
				
				if (Integer.parseInt(tokens[3]) == 1) {
					br.close();
					return true;
				}
				else {
					br.close();
					return false;
				}
			}
		}
		
		br.close();
		return false;
	}
	
	public String getFileName() throws IOException {
		
		String fileName = System.getProperty("user.dir")+"/p2pFileSharing/Common.cfg";
		//String fileName = System.getProperty("user.dir")+"/src/p2pFileSharing/Common.cfg";
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		String line="";
		String []tokens = new String[4];
		int count=0;
		
		while ((line = br.readLine()) != null) {
			if (count == 3)
				break;
			count++;
		}
		
		tokens = line.split("\\s+");
		br.close();
		
		return tokens[1];
	}
	
	public int[] GetIntervalTimes() throws IOException {
		
		int []time = new int[2];
		int lineCount=0;
		
		String FileName = System.getProperty("user.dir")+"/p2pFileSharing/Common.cfg";
		//String FileName = System.getProperty("user.dir")+"/src/p2pFileSharing/Common.cfg";
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FileName)));
		String line=null;
		
		String []temp = new String[2];
		int i=0;
		
		while ( (line = br.readLine() ) != null )
		{
			if (lineCount == 1 || lineCount == 2) {
				temp = line.split(" ");
				time[i] = Integer.parseInt(temp[1]);
				i++;
			}
			lineCount++;
		}
		br.close();

		return time;
	}
	
	public int GetNumberOfPreferredNeighbors () throws IOException {
		String FileName = System.getProperty("user.dir")+"/p2pFileSharing/Common.cfg";
		//String FileName = System.getProperty("user.dir")+"/src/p2pFileSharing/Common.cfg";
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FileName)));
		String line=null;
		
		String []temp = new String[2];
		
		line = br.readLine();
		temp = line.split(" ");
		br.close();
		return (Integer.parseInt(temp[1]));
	}
	
	public void ReadCommonConfigFile () throws IOException {
		String FileName = System.getProperty("user.dir")+"/p2pFileSharing/Common.cfg";
		//String FileName = System.getProperty("user.dir")+"/src/p2pFileSharing/Common.cfg";
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FileName)));
		String line=null;
		int lineCount=0;
		String []temp = new String[2];
		
		while((line = br.readLine()) != null) {
			temp = line.split(" ");
			if (lineCount == 3) {
				this.inputFileName = temp[1];
			}
			else if (lineCount == 4) {
				this.fileSize = Long.parseLong(temp[1]);
			}
			else if (lineCount == 5) {
				this.pieceSize = Integer.parseInt(temp[1]);
			}
			lineCount++;
		}
		
		double pieceCount = (double)this.fileSize/this.pieceSize;
		double checkPieceCount = Math.floor(pieceCount);
		if (pieceCount > checkPieceCount)
			this.pieceCount = (int)pieceCount + 1;
		else
			this.pieceCount = (int)pieceCount;
		
		br.close();
	}
	
	
	public void SplitFile (String FileName, long FileSize, int PieceSize, int PeerID) throws IOException {
		
		FileName = System.getProperty("user.dir")+"/p2pFileSharing/peer_"+PeerID+"/"+FileName;
		//FileName = System.getProperty("user.dir")+"/src/p2pFileSharing/peer_"+PeerID+"/"+FileName;
		
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(FileName));
		int bytesRead=0;
		int bytesReadCount=0;
		int count=0;
		
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(FileName+"."+count));
		
		while ((bytesRead = bis.read()) != -1) {
			
			if (bytesReadCount < PieceSize) {
				bos.write(bytesRead);
				bytesReadCount++;
			}
			else {
				count++;
				bos.close();
				bos = new BufferedOutputStream(new FileOutputStream(FileName+"."+count));
				bos.write(bytesRead);
				bytesReadCount=1;				
			}
		}
		
		bos.close();
		bis.close();
	}
	
	public void JoinFile (String FileName, long FileSize, int PieceSize, int pieceCount, int PeerID) throws IOException {
		
		int bytesRead = 0;
		BufferedInputStream bis = null;
		
		FileName = System.getProperty("user.dir")+"/p2pFileSharing/peer_"+PeerID+"/"+FileName;
		//FileName = System.getProperty("user.dir")+"/src/p2pFileSharing/peer_"+PeerID+"/"+FileName;
		
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(FileName+".new"));
		
		for (int i=0; i<pieceCount; i++) {
			bis = new BufferedInputStream(new FileInputStream(FileName+"."+i));
			while ((bytesRead = bis.read()) != -1) {
				bos.write(bytesRead);
			}
			bis.close();
		}
		bos.close();
	}
	
	public void WriteToFile (String FileName, String data) throws IOException{

		File file = new File(FileName);
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(data);
		bw.close();
	}
	
	public ArrayList<Integer> readPiece (int pieceIndex, int PeerID) throws IOException {
		
		int bytesRead=0;
		String FileName = System.getProperty("user.dir")+"/p2pFileSharing/peer_"+PeerID+"/"+this.getFileName();
		//String FileName = System.getProperty("user.dir")+"/src/p2pFileSharing/peer_"+PeerID+"/"+this.getFileName();
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(FileName+"."+pieceIndex));
		ArrayList<Integer> fileRead = new ArrayList<Integer> ();
		
		
		try {
		
		while ((bytesRead = bis.read()) != -1) {
			fileRead.add(bytesRead);
		}
			
		bis.close();
		}
		catch (EOFException ex) {
			//Do nothing
		}
		
		return fileRead;
		
	}
	
	public void writePiece (ArrayList<Integer> FilePiece, int pieceIndex, int PeerID) throws IOException {
		
		String FileName = System.getProperty("user.dir")+"/p2pFileSharing/peer_"+PeerID+"/"+this.getFileName();
		//String FileName = System.getProperty("user.dir")+"/src/p2pFileSharing/peer_"+PeerID+"/"+this.getFileName();
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(FileName+"."+pieceIndex));
		for (int i = 0; i < FilePiece.size(); i++) {
			bos.write(FilePiece.get(i));		
		}
		bos.close();
	}
	
	/*
	public static void main (String []args) throws IOException {
		FileHandler f = new FileHandler();
		String FileName = System.getProperty("user.dir")+"/src/p2pFileSharing/1.mp3";
		File f1 = new File(FileName);
		long fileSize = f1.length();
		System.out.println("FileSize: "+fileSize);
		//f.FileSplit(FileName, fileSize, 10000);
		//f.JoinFile(FileName, fileSize, 10000);
		f.SplitFile(FileName, fileSize, 10000);
		f.JoinFile(FileName, fileSize, 10000);
	}
	*/
}
