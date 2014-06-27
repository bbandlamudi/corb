package com.marklogic.developer.corb;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.marklogic.xcc.Request;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.types.XdmBinary;
import com.marklogic.xcc.types.XdmItem;

/**
 * @author Bhagat Bandlamudi, MarkLogic Corporation
 */
public class ExportToFileTask extends AbstractTask {
	protected static String TRUE = "true";
	protected static String FALSE = "false";
	
	protected static byte[] NEWLINE = "\n".getBytes();
	
	protected String exportDir;
	
	public void setExportDir(String exportFileDir){
		this.exportDir = exportFileDir;
	}
	
	public String getExportDir(){
		return this.exportDir == null ? System.getProperty("java.io.tmpdir") : this.exportDir;
	}
	
	protected String getFileName(){
		return inputUri.substring(inputUri.lastIndexOf('/')+1);
	}
	
	protected ResultSequence invoke() throws Exception{
		Thread.yield();
        Session session = null;
        try {
            session = newSession();
            Request request = session.newModuleInvoke(moduleUri);
            request.setNewStringVariable("URI", inputUri);
            // try to avoid thread starvation
            Thread.yield();
            ResultSequence response = session.submitRequest(request);
            session.close();
            session = null;
            return response;
        } finally {
            if (null != session) {
                session.close();
                session = null;
            }
            // try to avoid thread starvation
            Thread.yield();
        }
	}
	
	protected void writeToFile(String fileName, ResultSequence seq) throws IOException{
		if(!seq.hasNext()) return;
		BufferedOutputStream writer = null;
		try{
			writer = new BufferedOutputStream(new FileOutputStream(new File(exportDir,getFileName())));
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
		
	protected byte[] getValueAsBytes(XdmItem item){
		if(item instanceof XdmBinary){
			return ((XdmBinary) item).asBinaryData();
		}else{
			return item.asString().getBytes();
		}
	}
	
	@Override
	public String call() throws Exception {
		Thread.yield(); // try to avoid thread starvation
		ResultSequence seq = invoke();
		Thread.yield(); // try to avoid thread starvation
		writeToFile(getFileName(),seq);
		Thread.yield(); // try to avoid thread starvation
		return TRUE;
	}

}
