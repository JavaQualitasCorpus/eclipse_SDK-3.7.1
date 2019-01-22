/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.help.internal.context;
import org.eclipse.help.*;
/**
 * <p>
 * An enhanced version of <code>org.eclipse.help.IContext</code> interface
 * allowing obtaining a styled text. Used by Intro plug-ing and
 * org.eclipse.help.ui.internal.ContextHelpDialog TODO Create interface that
 * will return description as XML and make it public
 * </p>
 * 
 * @since 3.0
 */
public interface IStyledContext extends IContext {
	/**
	 * Returns the text description for this context with bold markers
	 * 
	 * @return String with <@#$b>and </@#$b> to mark bold range (as
	 *         IContext.getText() used to in 2.x)
	 */
	public String getStyledText();
}
