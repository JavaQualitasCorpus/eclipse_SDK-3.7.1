/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.launching.environments;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;
import org.eclipse.jdt.internal.launching.LaunchingPlugin;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.jdt.launching.environments.IExecutionEnvironmentsManager;

import com.ibm.icu.text.MessageFormat;

/**
 * Resolves variables of the form ${ee_home:<id>}
 */
public class ExecutionEnvironmentVariableResolver implements IDynamicVariableResolver {

	/* (non-Javadoc)
	 * @see org.eclipse.core.variables.IDynamicVariableResolver#resolveValue(org.eclipse.core.variables.IDynamicVariable, java.lang.String)
	 */
	public String resolveValue(IDynamicVariable variable, String argument) throws CoreException {
		if (argument == null) {
			throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.ID_PLUGIN, EnvironmentMessages.ExecutionEnvironmentVariableResolver_0));
		}
		IExecutionEnvironmentsManager manager = JavaRuntime.getExecutionEnvironmentsManager();
		IExecutionEnvironment env = manager.getEnvironment(argument);
		if (env == null) {
			throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.ID_PLUGIN, MessageFormat.format(EnvironmentMessages.ExecutionEnvironmentVariableResolver_1, new String[]{argument})));
		}
		IPath path = JavaRuntime.newJREContainerPath(env);
		IVMInstall jre = JavaRuntime.getVMInstall(path);
		if (jre == null) {
			throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.ID_PLUGIN, MessageFormat.format(EnvironmentMessages.ExecutionEnvironmentVariableResolver_2, new String[]{argument})));
		}
		return jre.getInstallLocation().getAbsolutePath();
	}

}