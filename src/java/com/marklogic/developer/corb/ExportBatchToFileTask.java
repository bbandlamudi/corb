package com.marklogic.developer.corb;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.marklogic.xcc.ResultSequence;

public class ExportBatchToFileTask extends ExportToFileTask {
	
	protected String getFileName(){
		String fileName = getProperty("EXPORT-FILE-NAME");
		if(fileName == null || (fileName=fileName.trim()).length() == 0){
			String batchRef = properties.getProperty(Manager.URIS_BATCH_REF);
			if(batchRef != null && (batchRef=batchRef.trim()).length() > 0){
				fileName = batchRef.substring(batchRef.lastIndexOf('/')+1); 
			}
		}
		if(fileName == null){
			fileName = inputUri.substring(inputUri.lastIndexOf('/')+1);
		}
		return fileName;
	}
	
	synchronized protected void writeToFile(String fileName, ResultSequence seq) throws IOException{
		if(!seq.hasNext()) return;
		BufferedOutputStream writer = null;
		try{
			writer = new BufferedOutputStream(new FileOutputStream(new File(exportDir,getFileName()),true));
			writer.write(getValueAsBytes(seq.next().getItem()));
			while(seq.hasNext()){
				writer.write(NEWLINE);
				writer.write(getValueAsBytes(seq.next().getItem()));
			}
		}finally{
			if(writer != null){
				writer.close();
			}
		}
	}
}
