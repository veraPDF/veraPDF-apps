/**
 * 
 */
package org.verapdf.apps;

import java.util.EnumSet;

import org.verapdf.processor.TaskType;

/**
 * @author  <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>
 *
 * @version 0.1
 * 
 * Created 30 Oct 2016:21:39:07
 */

public enum ProcessType {
	VALIDATE("validate", EnumSet.of(TaskType.VALIDATE)),
	FIX("fix", EnumSet.of(TaskType.VALIDATE, TaskType.FIX_METADATA)),
	EXTRACT("extract", EnumSet.of(TaskType.EXTRACT_FEATURES)),
	VALIDATE_EXTRACT("validate and extract", EnumSet.of(TaskType.VALIDATE, TaskType.EXTRACT_FEATURES)),
	EXTRACT_FIX("extract and fix", EnumSet.of(TaskType.VALIDATE, TaskType.FIX_METADATA, TaskType.EXTRACT_FEATURES));
	
	private final EnumSet<TaskType> tasks;
	private final String value;
	
	private ProcessType(final String value, EnumSet<TaskType> tasks) {
		this.value = value;
		this.tasks = EnumSet.copyOf(tasks);
	}
	
	public EnumSet<TaskType> getTasks() {
		return this.tasks;
	}
	
	public String getValue() {
		return this.value;
	}
}
