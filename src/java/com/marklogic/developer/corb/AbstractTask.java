package com.marklogic.developer.corb;

import java.util.Properties;

import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.Session;
/**
 * 
 * @author Bhagat Bandlamudi, MarkLogic Corporation
 *
 */
public abstract class AbstractTask implements Task{
	protected ContentSource cs;
	protected String moduleUri;
	protected Properties properties;
	protected String inputUri;

    public void setContentSource(ContentSource cs){
    	this.cs = cs;
    }
    
    public void setModuleURI(String moduleUri){
    	this.moduleUri = moduleUri;
    }
    
    public void setProperties(Properties properties){
    	this.properties = properties;
    }
    
	public void setInputURI(String inputUri) {
		this.inputUri = inputUri;
	}
	
	public Session newSession() {
        return cs.newSession();
    }
	
	public String getProperty(String key){
		String val = System.getProperty(key);
		if(val == null || val.trim().length() == 0){
			val = properties.getProperty(key);
		}
		return val;
	}
	
    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {
        super.finalize();
    }
    
}
