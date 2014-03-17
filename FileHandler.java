package p2pFileSharing;
import java.io.*;

class FileHandler {

	String inputFileName;
	long fileSize;
	int pieceSize;
	int pieceCount;
	
	public boolean CheckHasFile (int peerID) throws IOException {
		
		String fileName = System.getProperty("user.dir")+"/src/p2pFileSharing/PeerInfo.cfg";
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
	
	public void ReadCommonConfigFile () throws IOException {
		String FileName = System.getProperty("user.dir")+"/src/p2pFileSharing/Common.cfg";
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
		
		this.pieceCount = (int)this.fileSize/this.pieceSize;
		
		br.close();
	}
	
	
	public void SplitFile (String FileName, long FileSize, int PieceSize) throws IOException {
		
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
	
	public void JoinFile (String FileName, long FileSize, int PieceSize) throws IOException {
		int pieceCount = (int)FileSize/PieceSize;
		int bytesRead = 0;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(FileName+".new"));
		
		for (int i=0; i<=pieceCount; i++) {
			bis = new BufferedInputStream(new FileInputStream(FileName+"."+i));
			while ((bytesRead = bis.read()) != -1) {
				bos.write(bytesRead);
			}
			bis.close();
		}
		bos.close();
	}
	
	public void SplitTextFile (String FileName, long FileSize, int PieceSize) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FileName)));
		//BufferedInputStream bis = new BufferedInputStream(new FileInputStream(FileName));
		
		int pieceCount=0;
		String splitFileName;		
		String readFile;
		String toBeWritten="";
		
		while ((readFile=br.readLine()) != null) {
		
			toBeWritten += readFile;
			
			if (toBeWritten.length() >= PieceSize) {
				splitFileName = FileName+"."+pieceCount;
				String SubStrToBeWritten = toBeWritten.substring(0, PieceSize);
				this.WriteToFile(splitFileName, SubStrToBeWritten);
				pieceCount++;
				
				if (toBeWritten.length() == PieceSize) {
					toBeWritten = "";
				}
				//if (toBeWritten.length() >= PieceSize) 
				else {
					toBeWritten=toBeWritten.substring(PieceSize);
				}				
				splitFileName="";				
			}									
		}
		if (toBeWritten != "") {
			splitFileName = FileName+"."+pieceCount;
			this.WriteToFile(splitFileName, toBeWritten);
		}
		 
		br.close();		
	}
	
	public void JoinTextFile (String FileName, long FileSize, int PieceSize) throws IOException {
		
		int pieceCount = (int)FileSize/PieceSize;
		
		System.out.println("Piece count: "+pieceCount);
		String ReadFromFileName = FileName;
		String ReadFromFile = "";
		
		for (int i=0; i<pieceCount; i++) {
			ReadFromFileName = FileName+"."+i;
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ReadFromFileName)));
			ReadFromFile += br.readLine();
			br.close();
		}
		
		this.WriteToFile(FileName+".new", ReadFromFile);
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
