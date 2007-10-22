package org.jscsi.target.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OperationException extends Exception {
	
	/** The logger interface. */
	private static final Log LOGGER = LogFactory.getLog(OperationException.class);
	
	public OperationException() {
		super("Undefined Exception");
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("Undefined Exception");
		}
	}

	public OperationException(String message) {
		super(message);
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug(message);
		}
	}
	
}