/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.ui.actions;

import org.eclipse.debug.core.model.IVariable;

/**
 * Optional extension to the watch expression factory adapter to dynamically enable the
 * 'Create Watch Expression' action, based on the selected variable.
 * <p>
 * By default, the 'Create Watch Expression' action is enabled for instances of
 * {@link org.eclipse.debug.core.model.IVariable} that have an associated
 * {@link org.eclipse.debug.core.model.IWatchExpressionDelegate} registered
 * for that debug model. 
 * When a watch expression factory adapter is available for a variable that implements
 * this interface, the factory is consulted to enable the action.
 * </p>
 * <p>
 * Clients may implementing {@link IWatchExpressionFactoryAdapter} may also implement
 * this interface.
 * </p>
 * @since 3.3
 */
public interface IWatchExpressionFactoryAdapterExtension extends IWatchExpressionFactoryAdapter {
	
	/**
	 * Returns whether a watch expression can be created for the specified variable.
	 * 
	 * @param variable variable a watch expression is required for
	 * @return whether an expression can be created
	 * @exception org.eclipse.core.runtime.CoreException if unable to create a watch
	 *  expression
	 */
	public boolean canCreateWatchExpression(IVariable variable);

}
