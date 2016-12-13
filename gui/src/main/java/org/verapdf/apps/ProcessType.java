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
	VALIDATE("Validation", EnumSet.of(TaskType.VALIDATE)),
	FIX("fix", EnumSet.of(TaskType.VALIDATE, TaskType.FIX_METADATA)),
	EXTRACT("Features", EnumSet.of(TaskType.EXTRACT_FEATURES)),
	VALIDATE_EXTRACT("Validation and Features", EnumSet.of(TaskType.VALIDATE, TaskType.EXTRACT_FEATURES)),
	EXTRACT_FIX("extract and fix", EnumSet.of(TaskType.VALIDATE, TaskType.FIX_METADATA, TaskType.EXTRACT_FEATURES)),
	POLICY("Policy", EnumSet.of(TaskType.VALIDATE, TaskType.EXTRACT_FEATURES)),
	POLICY_FIX("policy and fix", EnumSet.of(TaskType.VALIDATE, TaskType.FIX_METADATA, TaskType.EXTRACT_FEATURES)),
	NO_PROCESS("", EnumSet.noneOf(TaskType.class));
	
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
	
	public static ProcessType addProcess(ProcessType base, ProcessType toAdd) {
		if (base == NO_PROCESS) return toAdd;
		if (toAdd == NO_PROCESS) return base;
		if (base == VALIDATE) {
			if (toAdd == EXTRACT) return VALIDATE_EXTRACT;
		} else if (base == EXTRACT) {
			if (toAdd == VALIDATE) return VALIDATE_EXTRACT;
			else if (toAdd == FIX) return EXTRACT_FIX;
		} else if (base == FIX) {
			if (toAdd == EXTRACT || toAdd == VALIDATE_EXTRACT) return EXTRACT_FIX;
			else if (toAdd == POLICY) return POLICY_FIX;
		} else if (base == VALIDATE_EXTRACT) {
			if (toAdd == FIX) return EXTRACT_FIX;
		} else if (base == EXTRACT_FIX) {
			if (toAdd == POLICY || toAdd == POLICY_FIX) return POLICY_FIX;
			else return EXTRACT_FIX;
		} else if (base == POLICY) {
				if (toAdd == FIX || toAdd == EXTRACT_FIX) return POLICY_FIX;
			else return POLICY;
		} else if (base == POLICY_FIX) {
			return POLICY_FIX;
		}
		return toAdd;
	}
	
	public static ProcessType[] getOptionValues() {
		return new ProcessType[]{VALIDATE, EXTRACT, VALIDATE_EXTRACT, POLICY};
	}
}
