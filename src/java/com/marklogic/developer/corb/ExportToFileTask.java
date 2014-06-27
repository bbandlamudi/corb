package com.marklogic.developer.corb;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import com.marklogic.xcc.Request;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.types.XdmBinary;
import com.marklogic.xcc.types.XdmItem;

/**
 * @author Bhagat Bandlamudi, MarkLogic Corporation
 */
public class ExportToFileTask extends AbstractTask {
	private static String TRUE = "true";
	private static String FALSE = "false";
	
	protected String exportFileDir;
	
	public void setExportFileDir(String exportFileDir){
		this.exportFileDir = exportFileDir;
	}
	
	private ResultSequence invoke() throws Exception{
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
	
	private String getFileName(){
		return inputUri.substring(inputUri.lastIndexOf('/')+1);
	}

	@Override
	public String call() throws Exception {
		ResultSequence seq = invoke();
		if(seq.hasNext()){			
			XdmItem item = seq.next().getItem();
			// try to avoid thread starvation
            Thread.yield();
			BufferedOutputStream writer = null;
			try{
				writer = new BufferedOutputStream(new FileOutputStream(new File(exportFileDir,getFileName())));
				if(item instanceof XdmBinary){
					writer.write(((XdmBinary) item).asBinaryData());
				}else{
					writer.write(item.asString().getBytes());
				}
			}finally{
				if(writer != null){
					writer.close();
				}
				// try to avoid thread starvation
	            Thread.yield();
			}			
			return TRUE;
		}
		return FALSE;
	}

}
