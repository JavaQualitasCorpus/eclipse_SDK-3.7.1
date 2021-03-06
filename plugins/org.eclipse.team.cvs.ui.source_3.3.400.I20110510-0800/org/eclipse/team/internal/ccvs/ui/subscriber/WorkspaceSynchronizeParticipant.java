/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Sebastian Davids <sdavids@gmx.de> - bug 69926
 *******************************************************************************/
package org.eclipse.team.internal.ccvs.ui.subscriber;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.window.Window;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.core.diff.IDiff;
import org.eclipse.team.core.mapping.provider.ResourceDiffTree;
import org.eclipse.team.internal.ccvs.core.CVSProviderPlugin;
import org.eclipse.team.internal.ccvs.ui.*;
import org.eclipse.team.internal.ccvs.ui.actions.*;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSet;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSetManager;
import org.eclipse.team.internal.ui.synchronize.*;
import org.eclipse.team.ui.TeamUI;
import org.eclipse.team.ui.synchronize.*;
import org.eclipse.ui.*;

public class WorkspaceSynchronizeParticipant extends ScopableSubscriberParticipant implements IChangeSetProvider {

	public static final String ID = "org.eclipse.team.cvs.ui.cvsworkspace-participant"; //$NON-NLS-1$

	/**
	 * The id of a workspace action group to which additions actions can 
	 * be added.
	 */
	public static final String TOOLBAR_CONTRIBUTION_GROUP = "toolbar_group_1"; //$NON-NLS-1$
	
	public static final String CONTEXT_MENU_CONTRIBUTION_GROUP_1 = "context_group_1"; //$NON-NLS-1$
	public static final String CONTEXT_MENU_CONTRIBUTION_GROUP_2 = "context_group_2"; //$NON-NLS-1$
	public static final String CONTEXT_MENU_CONTRIBUTION_GROUP_3 = "context_group_3"; //$NON-NLS-1$
	public static final String CONTEXT_MENU_CONTRIBUTION_GROUP_4 = "context_group_4"; //$NON-NLS-1$

    private WorkspaceChangeSetCapability capability;

	/**
	 * CVS workspace action contribution
	 */
	public class WorkspaceActionContribution extends SynchronizePageActionGroup {
		private WorkspaceCommitAction commitToolbar;
		private WorkspaceUpdateAction updateToolbar;
		
		public void initialize(ISynchronizePageConfiguration configuration) {
			super.initialize(configuration);
			
			updateToolbar = new WorkspaceUpdateAction(
					configuration, 
					getVisibleRootsSelectionProvider(), 
					"WorkspaceToolbarUpdateAction."); //$NON-NLS-1$
			updateToolbar.setPromptBeforeUpdate(true);
			appendToGroup(
					ISynchronizePageConfiguration.P_TOOLBAR_MENU,
					TOOLBAR_CONTRIBUTION_GROUP,
					updateToolbar);
			
			commitToolbar = new WorkspaceCommitAction(
					configuration, 
					getVisibleRootsSelectionProvider(), 
					"WorkspaceToolbarCommitAction."); //$NON-NLS-1$
			appendToGroup(
					ISynchronizePageConfiguration.P_TOOLBAR_MENU,
					TOOLBAR_CONTRIBUTION_GROUP,
					commitToolbar);
			
			appendToGroup(
					ISynchronizePageConfiguration.P_CONTEXT_MENU, 
					CONTEXT_MENU_CONTRIBUTION_GROUP_1,
					new WorkspaceUpdateAction(configuration));
			appendToGroup(
					ISynchronizePageConfiguration.P_CONTEXT_MENU, 
					CONTEXT_MENU_CONTRIBUTION_GROUP_1,
					new WorkspaceCommitAction(configuration));
			appendToGroup(
					ISynchronizePageConfiguration.P_CONTEXT_MENU, 
					CONTEXT_MENU_CONTRIBUTION_GROUP_2,
					new OverrideAndUpdateAction(configuration));
			appendToGroup(
					ISynchronizePageConfiguration.P_CONTEXT_MENU, 
					CONTEXT_MENU_CONTRIBUTION_GROUP_2,
					new OverrideAndCommitAction(configuration));
			appendToGroup(
					ISynchronizePageConfiguration.P_CONTEXT_MENU, 
					CONTEXT_MENU_CONTRIBUTION_GROUP_2,
					new ConfirmMergedAction(configuration));		
			appendToGroup(
					ISynchronizePageConfiguration.P_CONTEXT_MENU, 
					CONTEXT_MENU_CONTRIBUTION_GROUP_3,
					new CVSActionDelegateWrapper(new IgnoreAction(), configuration));
			if (!configuration.getSite().isModal()) {
				appendToGroup(
						ISynchronizePageConfiguration.P_CONTEXT_MENU, 
						CONTEXT_MENU_CONTRIBUTION_GROUP_3,
						new CreatePatchAction(configuration));
				appendToGroup(
						ISynchronizePageConfiguration.P_CONTEXT_MENU, 
						CONTEXT_MENU_CONTRIBUTION_GROUP_3,
						new ApplyPatchAction(configuration));
				appendToGroup(
						ISynchronizePageConfiguration.P_CONTEXT_MENU, 
						CONTEXT_MENU_CONTRIBUTION_GROUP_3,
						new CVSActionDelegateWrapper(new BranchAction(), configuration));
				appendToGroup(
						ISynchronizePageConfiguration.P_CONTEXT_MENU, 
						CONTEXT_MENU_CONTRIBUTION_GROUP_3,
						new CVSActionDelegateWrapper(new ShowAnnotationAction(), configuration));
				appendToGroup(
						ISynchronizePageConfiguration.P_CONTEXT_MENU, 
						CONTEXT_MENU_CONTRIBUTION_GROUP_3,
						new CVSActionDelegateWrapper(new ShowResourceInHistoryAction(), configuration));
				appendToGroup(
						ISynchronizePageConfiguration.P_CONTEXT_MENU, 
						CONTEXT_MENU_CONTRIBUTION_GROUP_3,
						new CVSActionDelegateWrapper(new SetKeywordSubstitutionAction(), configuration));	
			}
			appendToGroup(
					ISynchronizePageConfiguration.P_CONTEXT_MENU, 
					CONTEXT_MENU_CONTRIBUTION_GROUP_4,
					new RefreshDirtyStateAction(configuration));
		}
	}
	
	public class WorkspaceChangeSetCapability extends CVSChangeSetCapability {
        /* (non-Javadoc)
         * @see org.eclipse.team.ui.synchronize.ChangeSetCapability#createChangeSet(org.eclipse.team.core.synchronize.SyncInfo[])
         */
        public ActiveChangeSet createChangeSet(ISynchronizePageConfiguration configuration, IDiff[] infos) {
	        ActiveChangeSet set = getActiveChangeSetManager().createSet(CVSUIMessages.WorkspaceChangeSetCapability_1, new IDiff[0]); 
			CommitSetDialog dialog = new CommitSetDialog(configuration.getSite().getShell(), set, getResources(infos), CommitSetDialog.NEW);  
			dialog.open();
			if (dialog.getReturnCode() != Window.OK) return null;
			set.add(infos);
			return set;
        }

        private IResource[] getResources(IDiff[] diffs) {
        	Set result = new HashSet();
        	for (int i = 0; i < diffs.length; i++) {
				IDiff diff = diffs[i];
				IResource resource = ResourceDiffTree.getResourceFor(diff);
				if (resource != null)
					result.add(resource);
			}
            return (IResource[]) result.toArray(new IResource[result.size()]);
        }
        
        /* (non-Javadoc)
         * @see org.eclipse.team.ui.synchronize.ChangeSetCapability#editChangeSet(org.eclipse.team.core.subscribers.ActiveChangeSet)
         */
        public void editChangeSet(ISynchronizePageConfiguration configuration, ActiveChangeSet set) {
	        CommitSetDialog dialog = new CommitSetDialog(configuration.getSite().getShell(), set, set.getResources(), CommitSetDialog.EDIT);
			dialog.open();
			if (dialog.getReturnCode() != Window.OK) return;
			// Nothing to do here as the set was updated by the dialog 
        }

        /* (non-Javadoc)
         * @see org.eclipse.team.ui.synchronize.ChangeSetCapability#getActiveChangeSetManager()
         */
        public ActiveChangeSetManager getActiveChangeSetManager() {
            return CVSUIPlugin.getPlugin().getChangeSetManager();
        }
	}
	
	/**
	 * No-arg constructor used for
	 * creation of persisted participant after startup
	 */
	public WorkspaceSynchronizeParticipant() {
	}
	
	public WorkspaceSynchronizeParticipant(ISynchronizeScope scope) {
		super(scope);
		setSubscriber(CVSProviderPlugin.getPlugin().getCVSWorkspaceSubscriber());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.team.ui.synchronize.ISynchronizeParticipant#init(org.eclipse.ui.IMemento)
	 */
	public void init(String secondaryId, IMemento memento) throws PartInitException {
		super.init(secondaryId, memento);
		setSubscriber(CVSProviderPlugin.getPlugin().getCVSWorkspaceSubscriber());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.team.ui.synchronize.subscribers.SubscriberParticipant#initializeConfiguration(org.eclipse.team.ui.synchronize.ISynchronizePageConfiguration)
	 */
	protected void initializeConfiguration(ISynchronizePageConfiguration configuration) {
		super.initializeConfiguration(configuration);
		ILabelDecorator labelDecorator = getLabelDecorator(configuration);
		configuration.addLabelDecorator(labelDecorator);
		configuration.addMenuGroup(ISynchronizePageConfiguration.P_TOOLBAR_MENU, TOOLBAR_CONTRIBUTION_GROUP);
		configuration.addActionContribution(new WorkspaceActionContribution());
		configuration.setSupportedModes(ISynchronizePageConfiguration.ALL_MODES);
		configuration.setMode(ISynchronizePageConfiguration.BOTH_MODE);
		
		// Add context menu groups here to give the client displaying the
		// page a chance to remove the context menu
		configuration.addMenuGroup(
				ISynchronizePageConfiguration.P_CONTEXT_MENU, 
				CONTEXT_MENU_CONTRIBUTION_GROUP_1);
		configuration.addMenuGroup(
				ISynchronizePageConfiguration.P_CONTEXT_MENU, 
				CONTEXT_MENU_CONTRIBUTION_GROUP_2);
		configuration.addMenuGroup(
				ISynchronizePageConfiguration.P_CONTEXT_MENU, 
				CONTEXT_MENU_CONTRIBUTION_GROUP_3);
		configuration.addMenuGroup(
				ISynchronizePageConfiguration.P_CONTEXT_MENU, 
				CONTEXT_MENU_CONTRIBUTION_GROUP_4);
	}
	
	protected  ILabelDecorator getLabelDecorator(ISynchronizePageConfiguration configuration) {
	    return new CVSParticipantLabelDecorator(configuration);
	}
	
	protected ISynchronizeParticipantDescriptor getDescriptor() {
		return TeamUI.getSynchronizeManager().getParticipantDescriptor(ID);
	}
	
	/* (non-Javadoc)
     * @see org.eclipse.team.ui.synchronize.SubscriberParticipant#updateLabels(org.eclipse.team.ui.synchronize.ISynchronizeModelElement, org.eclipse.compare.CompareConfiguration, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void prepareCompareInput(ISynchronizeModelElement element, CompareConfiguration config, IProgressMonitor monitor) throws TeamException {
        monitor.beginTask(null, 100);
        CVSParticipant.deriveBaseContentsFromLocal(element, Policy.subMonitorFor(monitor, 10));
        super.prepareCompareInput(element, config, Policy.subMonitorFor(monitor, 80));
        CVSParticipant.updateLabelsForCVS(element, config, Policy.subMonitorFor(monitor, 10));
        monitor.done();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.team.ui.synchronize.AbstractSynchronizeParticipant#getPreferencePages()
     */
    public PreferencePage[] getPreferencePages() {
        return CVSParticipant.addCVSPreferencePages(super.getPreferencePages());
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.team.ui.synchronize.AbstractSynchronizeParticipant#getChangeSetCapability()
     */
    public ChangeSetCapability getChangeSetCapability() {
        if (capability == null) {
            capability = new WorkspaceChangeSetCapability();
        }
        return capability;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.team.ui.synchronize.AbstractSynchronizeParticipant#isViewerContributionsSupported()
     */
    protected boolean isViewerContributionsSupported() {
        return true;
    }

    public void refresh(IResource[] resources, IWorkbenchPartSite site) {
        refresh(resources, getShortTaskName(), getLongTaskName(resources), site);
    }
    
}
