package load.balanced.abe.simulation;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;



public final class System {
	  
	public static final String outputPath =createOutputPath();
	public static class out{
		private static PrintStream io = null;
		public static void print(String str){
			 
			java.lang.System.out.print(str);
			
		 if(io ==null){ 
			try {
				io = new PrintStream(new FileOutputStream(outputPath+ "console.txt"));
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			}
		 }
			io.print(str);
		}  
		
		public static void println(String str){
			
			java.lang.System.out.println(str);
			
		  if(io ==null){
			try {
				io = new PrintStream(new FileOutputStream(outputPath+  "console.txt"));
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			}
		  }
			io.println(str);
		}
        public static void println(int str){
			
			java.lang.System.out.println(str);
			
			if(io ==null){
			  try {
				io = new PrintStream(new FileOutputStream(outputPath+ "console.txt"));
			  } catch (FileNotFoundException e) {
				
				e.printStackTrace();
			  }
			}
			io.println(str);
			
		}
        public static void close(){
        	io.close();
        }
       
	}
	public static String printcurrentDate() {
		SimpleDateFormat formatter
		  = new SimpleDateFormat("yyyy.MM.dd G 'at' hh.mm.ss z");
		
		Date currentTime = new Date();
		String dateString = formatter.format(currentTime);
		return dateString;

		
	}
	
	public static String printcurrentDate2() {
		SimpleDateFormat formatter
		  = new SimpleDateFormat("YYYY-'W'ww-u");
		
		Date currentTime = new Date();
		String dateString = formatter.format(currentTime);
		return dateString;

		
	}
	private static String createOutputPath() {
		String  output = java.lang.System.getProperty("user.home") + File.separator ;
		
        File file = new File(output);
        if(!file.exists()){
        	file.mkdir();
        	
        }
		return output;
	}
	

}
