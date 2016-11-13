/*
 * Copyright (c) 2016. All rights reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS".
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES.
 * 
 * Author: Florin Bogdan Balint
 * 
 */

package ac.at.tuwien.mt.gui.beans.things;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.ws.rs.ServiceUnavailableException;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ac.at.tuwien.mt.gui.beans.UserControllerBean;
import ac.at.tuwien.mt.gui.internal.DefaultTreeNodeMetaModelHelper;
import ac.at.tuwien.mt.gui.primefaces.Messages;
import ac.at.tuwien.mt.gui.rest.RESTDataContractClient;
import ac.at.tuwien.mt.gui.rest.RESTMicroserviceLocator;
import ac.at.tuwien.mt.gui.rest.RESTMonitoringClient;
import ac.at.tuwien.mt.model.helper.DefaultIDGenerator;
import ac.at.tuwien.mt.model.microservice.MicroserviceInfo;
import ac.at.tuwien.mt.model.microservice.MicroserviceType;
import ac.at.tuwien.mt.model.microservice.monitoring.QueueInfo;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.model.thing.message.Attribute;
import ac.at.tuwien.mt.model.thing.message.DataType;
import ac.at.tuwien.mt.model.thing.message.MetaModel;

/**
 * @author Florin Bogdan Balint
 *
 */
@SuppressWarnings("serial")
@ManagedBean(name = "tcb")
@SessionScoped
public class ThingControllerBean implements Serializable {

	private static final Logger LOGGER = LogManager.getLogger(ThingControllerBean.class);

	private Thing thing = new Thing();
	private Attribute attrToAdd = new Attribute();

	private ThingControllerMode mode;
	private List<Thing> things = new ArrayList<Thing>();

	@ManagedProperty(value = "#{ucb}")
	private UserControllerBean userControllerBean;

	private TreeNode root = new DefaultTreeNode("ROOT", null);
	private List<String> list = new ArrayList<String>();

	private String addAsChildTo;

	@PostConstruct
	public void init() {
		if (thing != null) {
			root = DefaultTreeNodeMetaModelHelper.getTreeFromMetaModel(thing.getMetaModel());
		}
	}

	private TreeNode find(TreeNode treeNode, String toFind) {
		List<TreeNode> children = treeNode.getChildren();
		for (TreeNode tn : children) {
			if (tn.getData() instanceof Attribute) {
				Attribute current = (Attribute) tn.getData();
				if (current.toString().equals(toFind)) {
					return tn;
				}
			}
			if (tn.getChildCount() > 0) {
				for (TreeNode tnChild : tn.getChildren()) {
					return find(tnChild, toFind);
				}
			}
		}
		return null;
	}

	public String addAttribute() {
		String toFind = attrToAdd.toString();
		if (attrToAdd.getDataType().equals(DataType.ATTRIBUTE)) {
			list.add(toFind);
		}

		if (addAsChildTo.equals("ROOT")) {
			root.getChildren().add(new DefaultTreeNode(attrToAdd));
		} else {
			TreeNode treeNode = find(root, addAsChildTo);
			if (treeNode != null) {
				treeNode.getChildren().add(new DefaultTreeNode(attrToAdd));
			} else {
				// nothing to do...
				LOGGER.error("This should not happen...");
			}
		}
		attrToAdd = new Attribute();
		if (mode.equals(ThingControllerMode.UPDATE)) {
			return "thing_edit.xhtml";
		}
		if (mode.equals(ThingControllerMode.CLONE)) {
			return "thing_clone.xhtml";
		}
		return "thing_add.xhtml";
	}

	public String resetMetaModel() {
		root = new DefaultTreeNode("ROOT", null);
		if (mode.equals(ThingControllerMode.UPDATE)) {
			return "thing_edit.xhtml";
		}
		if (mode.equals(ThingControllerMode.CLONE)) {
			return "thing_clone.xhtml";
		}
		return "thing_add.xhtml";
	}

	public String[] getPossibleLayers() {
		if (list.isEmpty()) {
			list.add("ROOT");
		}
		return list.toArray(new String[list.size()]);
	}

	public String clone() {
		LOGGER.debug("Updating device.");
		addConfigMessage();
		try {
			MicroserviceInfo msInfo = RESTMicroserviceLocator.locateMicroservice(MicroserviceType.DATACONTRACT);
			String ownerId = userControllerBean.getPerson().getPersonId();
			RESTDataContractClient client = new RESTDataContractClient(msInfo);
			thing.setOwnerId(ownerId);
			MetaModel metaModelFromTree = DefaultTreeNodeMetaModelHelper.getMetaModelFromTree(root);
			thing.setMetaModel(metaModelFromTree);
			client.insertThing(thing);
		} catch (ServiceUnavailableException e) {
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, "Could not connect to server, please try again later!");
			return "thing_clone.xhtml";
		}
		return returnToWelcome(FacesMessage.SEVERITY_INFO, "Your Thing with the resourceID: '" + thing.getResourceId() + "' was created successfully");
	}

	public void addConfigMessage() {
		try {
			thing.setThingId(DefaultIDGenerator.generateID());
			MicroserviceInfo msInfo = RESTMicroserviceLocator.locateMicroservice(MicroserviceType.MONITORING);
			RESTMonitoringClient client = new RESTMonitoringClient(msInfo);
			QueueInfo microserviceQueueInfo = client.getQueueInfo();

			QueueInfo queueInfoToSet = new QueueInfo();
			queueInfoToSet.setQueueName(thing.getThingId());
			queueInfoToSet.setBrokerURL(microserviceQueueInfo.getBrokerURL());
			thing.setQueueInfo(queueInfoToSet);

			Messages.addMessage(FacesMessage.SEVERITY_INFO, "Please configure your Thing to send ALL messages to the following broker URL: " + microserviceQueueInfo.getBrokerURL());
			Messages.addMessage(FacesMessage.SEVERITY_INFO, "Please configure your Thing to send ALL messages to the following queue: " + thing.getThingId());
		} catch (ServiceUnavailableException e) {
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, "Could not connect to server, please try again later!");
		}
	}

	public String update() {
		LOGGER.debug("Updating device.");

		try {
			MicroserviceInfo locateDataContractMicroservice = RESTMicroserviceLocator.locateMicroservice(MicroserviceType.DATACONTRACT);
			String ownerId = userControllerBean.getPerson().getPersonId();
			RESTDataContractClient client = new RESTDataContractClient(locateDataContractMicroservice);
			thing.setOwnerId(ownerId);
			MetaModel metaModelFromTree = DefaultTreeNodeMetaModelHelper.getMetaModelFromTree(root);
			thing.setMetaModel(metaModelFromTree);
			client.updateThing(thing);
		} catch (ServiceUnavailableException e) {
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, "Could not connect to server, please try again later!");
			return "thing_update.xhtml";
		}

		return returnToWelcome(FacesMessage.SEVERITY_INFO, "Your Thing with the resourceID: '" + thing.getResourceId() + "' was updated successfully");
	}

	public String delete() {
		LOGGER.debug("Deleting device.");
		try {
			MicroserviceInfo locateDataContractMicroservice = RESTMicroserviceLocator.locateMicroservice(MicroserviceType.DATACONTRACT);
			String ownerId = userControllerBean.getPerson().getPersonId();
			RESTDataContractClient client = new RESTDataContractClient(locateDataContractMicroservice);
			thing.setOwnerId(ownerId);
			client.deleteThing(thing);
		} catch (ServiceUnavailableException e) {
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, "Could not connect to server, please try again later!");
			return "thing_delete.xhtml";
		}

		return returnToWelcome(FacesMessage.SEVERITY_INFO, "Your Thing with the resourceID: '" + thing.getResourceId() + "' was deleted successfully");
	}

	public String add() {
		LOGGER.debug("Registering new device.");
		addConfigMessage();
		try {
			String ownerId = userControllerBean.getPerson().getPersonId();
			thing.setOwnerId(ownerId);
			MetaModel metaModelFromTree = DefaultTreeNodeMetaModelHelper.getMetaModelFromTree(root);
			thing.setMetaModel(metaModelFromTree);

			MicroserviceInfo locateDataContractMicroservice = RESTMicroserviceLocator.locateMicroservice(MicroserviceType.DATACONTRACT);
			RESTDataContractClient client = new RESTDataContractClient(locateDataContractMicroservice);
			client.insertThing(thing);
		} catch (ServiceUnavailableException e) {
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, "Could not connect to server, please try again later!");
			LOGGER.error(e);
			return "thing_add.xhtml";
		}

		return returnToWelcome(FacesMessage.SEVERITY_INFO, "Your Thing with the resourceID: '" + thing.getResourceId() + "' was added successfully");
	}

	/**
	 * Returns to the welcome page and removes this bean from the session
	 * 
	 * @param severity
	 * @param message
	 * @return
	 */
	private String returnToWelcome(Severity severity, String message) {
		Messages.addMessage(severity, message);
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("tcb");
		return "welcome.xhtml";
	}

	/**
	 * @return the device
	 */
	public Thing getThing() {
		return thing;
	}

	/**
	 * @param thing
	 *            the device to set
	 */
	public void setThing(Thing thing) {
		this.thing = SerializationUtils.clone(thing);
		if (thing != null) {
			root = DefaultTreeNodeMetaModelHelper.getTreeFromMetaModel(thing.getMetaModel());
		}
	}

	/**
	 * @return the userControllerBean
	 */
	public UserControllerBean getUserControllerBean() {
		return userControllerBean;
	}

	/**
	 * @param userControllerBean
	 *            the userControllerBean to set
	 */
	public void setUserControllerBean(UserControllerBean userControllerBean) {
		this.userControllerBean = userControllerBean;
	}

	/**
	 * @return the mode
	 */
	public ThingControllerMode getMode() {
		return mode;
	}

	/**
	 * @param mode
	 *            the mode to set
	 */
	public void setMode(ThingControllerMode mode) {
		this.mode = mode;
	}

	public enum ThingControllerMode {
		REGISTER, UPDATE, CLONE;
	}

	public void setRegisterMode(ActionEvent arg0) throws AbortProcessingException {
		mode = ThingControllerMode.REGISTER;
	}

	public void setUpdateMode(ActionEvent arg0) throws AbortProcessingException {
		mode = ThingControllerMode.UPDATE;
	}

	public void setCloneMode(ActionEvent arg0) throws AbortProcessingException {
		mode = ThingControllerMode.CLONE;
	}

	public boolean isRegisterMode() {
		if (mode == null) {
			return false;
		}
		if (mode.equals(ThingControllerMode.REGISTER)) {
			return true;
		}
		return false;
	}

	public boolean isUpdateMode() {
		if (mode == null) {
			return false;
		}
		if (mode.equals(ThingControllerMode.UPDATE)) {
			return true;
		}
		return false;
	}

	public boolean isCloneMode() {
		if (mode == null) {
			return false;
		}
		if (mode.equals(ThingControllerMode.CLONE)) {
			return true;
		}
		return false;
	}

	/**
	 * @return the things
	 */
	public List<Thing> getThings() {
		return things;
	}

	/**
	 * @param things
	 *            the things to set
	 */
	public void setThings(List<Thing> things) {
		this.things = things;
	}

	public TreeNode getRoot() {
		return root;
	}

	/**
	 * @return the addAsChildTo
	 */
	public String getAddAsChildTo() {
		return addAsChildTo;
	}

	/**
	 * @param addAsChildTo
	 *            the addAsChildTo to set
	 */
	public void setAddAsChildTo(String addAsChildTo) {
		this.addAsChildTo = addAsChildTo;
	}

	/**
	 * @return the attrToAdd
	 */
	public Attribute getAttrToAdd() {
		return attrToAdd;
	}

	/**
	 * @param attrToAdd
	 *            the attrToAdd to set
	 */
	public void setAttrToAdd(Attribute attrToAdd) {
		this.attrToAdd = attrToAdd;
	}

	public DataType[] getDataTypes() {
		return DataType.values();
	}

}
