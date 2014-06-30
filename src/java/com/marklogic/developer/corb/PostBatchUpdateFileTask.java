package com.marklogic.developer.corb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class PostBatchUpdateFileTask extends ExportToFileTask {
	
	protected String getFileName(){
		String fileName = getProperty("EXPORT-FILE-NAME");
		if(fileName == null || (fileName=fileName.trim()).length() == 0){
			String batchRef = properties.getProperty(Manager.URIS_BATCH_REF);
			if(batchRef != null && (batchRef=batchRef.trim()).length() > 0){
				fileName = batchRef.substring(batchRef.lastIndexOf('/')+1); 
			}
		}
		return fileName;
	}
	
	protected abstract String getTopContent();
	
	protected abstract String getBottomContent();
		
	protected void writeToFile(String fileName) throws IOException{		
		String topContent = getTopContent();
		topContent = topContent != null ? topContent.trim() : "";	
		
		String bottomContent = getBottomContent();
		bottomContent = bottomContent != null ? bottomContent.trim() : "";
		

			File exportFile = new File(exportDir,getFileName());
			if(exportFile.exists()){
				if(topContent.length() > 0){
					InputStream in =  new BufferedInputStream(new FileInputStream(exportFile));
					File newFile = new File(exportDir,getFileName()+".new");
					BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(newFile,true));
					out.write(topContent.getBytes());
					out.write(NEWLINE);				
					copyInputStreamToOutputStream(in,out);
					//no need to close the streams as they should already be closed. 
					
					exportFile.delete();
					newFile.renameTo(exportFile);
				}			

				if(bottomContent.length() > 0){
					BufferedOutputStream out = null;
					try{
						out = new BufferedOutputStream(new FileOutputStream(new File(exportDir,getFileName()),true));
						out.write(NEWLINE);
						out.write(bottomContent.getBytes());
					}finally{
						if(out != null){
							out.close();
						}
					}
				}
			}
		
	}
	

	
	public void copyInputStreamToOutputStream(final InputStream in, final OutputStream out) throws IOException{
		try{
	        try{
	            final byte[] buffer = new byte[1024];
	            int n;
	            while ((n = in.read(buffer)) != -1)
	                out.write(buffer, 0, n);
	        }finally{
	            out.close();
	        }
	    }finally{
	        in.close();
	    }
	}
	
	
	@Override
	public String call() throws Exception {
		Thread.yield(); // try to avoid thread starvation
		invoke();
		Thread.yield(); // try to avoid thread starvation
		writeToFile(getFileName());
		Thread.yield(); // try to avoid thread starvation
		return TRUE;
	}
}
