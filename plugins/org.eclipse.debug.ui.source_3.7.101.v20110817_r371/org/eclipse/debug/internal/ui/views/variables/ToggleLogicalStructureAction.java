/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.internal.ui.views.variables;

import org.eclipse.debug.internal.ui.DebugPluginImages;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.IDebugHelpContextIds;
import org.eclipse.debug.internal.ui.IInternalDebugUIConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.PlatformUI;

/**
 * Action to toggle the use of contributed variables content providers on and off.
 * When on, all registered variables content providers for the current debug model
 * are used.  When off, the default content provider (that shows all children)
 * is used for all debug models.
 */
public class ToggleLogicalStructureAction extends Action {
	
	private VariablesView fView;

	public ToggleLogicalStructureAction(VariablesView view) {
		super(null, IAction.AS_CHECK_BOX);
		setView(view);
		setToolTipText(VariablesViewMessages.ToggleObjectBrowsersAction_1);  
		setHoverImageDescriptor(DebugPluginImages.getImageDescriptor(IInternalDebugUIConstants.IMG_LCL_SHOW_LOGICAL_STRUCTURE));
		setDisabledImageDescriptor(DebugPluginImages.getImageDescriptor(IInternalDebugUIConstants.IMG_DLCL_SHOW_LOGICAL_STRUCTURE));
		setImageDescriptor(DebugPluginImages.getImageDescriptor(IInternalDebugUIConstants.IMG_ELCL_SHOW_LOGICAL_STRUCTURE));
		setId(DebugUIPlugin.getUniqueIdentifier() + ".ToggleObjectBrowsersAction"); //$NON-NLS-1$
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IDebugHelpContextIds.VARIABLES_CONTENT_PROVIDERS_ACTION);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		if (!getView().isAvailable()) {
			return;
		}
		getView().setShowLogicalStructure(isChecked());	
		BusyIndicator.showWhile(getView().getViewer().getControl().getDisplay(), new Runnable() {
			public void run() {
				getView().getViewer().refresh();					
			}
		});		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#setChecked(boolean)
	 */
	public void setChecked(boolean value) {
		super.setChecked(value);
	}
	
	protected VariablesView getView() {
		return fView;
	}

	protected void setView(VariablesView view) {
		fView = view;
	}

}
