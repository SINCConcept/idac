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
package ac.at.tuwien.mt.gui.internal;

import java.util.List;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ac.at.tuwien.mt.model.thing.message.Attribute;
import ac.at.tuwien.mt.model.thing.message.DataType;
import ac.at.tuwien.mt.model.thing.message.MetaModel;

/**
 * @author Florin Bogdan Balint
 *
 */
public final class DefaultTreeNodeMetaModelHelper {

	protected DefaultTreeNodeMetaModelHelper() {
		// helper class
	}

	public static TreeNode getTreeFromMetaModel(MetaModel metaModel) {
		TreeNode root = new DefaultTreeNode("ROOT", null);

		for (Attribute attribute : metaModel.getAttributes()) {
			if (attribute.getDataType() != DataType.ATTRIBUTE) {
				root.getChildren().add(new DefaultTreeNode(attribute));
			} else {
				TreeNode treeNode = getTreeFromAttribute(attribute);
				root.getChildren().add(treeNode);
			}
		}
		return root;
	}

	private static TreeNode getTreeFromAttribute(Attribute attribute) {
		TreeNode node = new DefaultTreeNode(attribute);
		for (Attribute attr : attribute.getMetaModel().getAttributes()) {
			if (attr.getDataType() != DataType.ATTRIBUTE) {
				node.getChildren().add(new DefaultTreeNode(attr));
			} else {
				TreeNode treeNode = getTreeFromAttribute(attr);
				node.getChildren().add(treeNode);
			}
		}
		return node;
	}

	public static MetaModel getMetaModelFromTree(TreeNode treeNode) {
		MetaModel metaModel = new MetaModel();
		List<TreeNode> children = treeNode.getChildren();
		for (TreeNode tn : children) {
			Attribute attribute = getAttributeFromTree(tn);
			metaModel.getAttributes().add(attribute);
		}
		return metaModel;
	}

	public static Attribute getAttributeFromTree(TreeNode treeNode) {
		Attribute attribute = (Attribute) treeNode.getData();
		if (attribute.getDataType() == DataType.ATTRIBUTE) {
			// get the metaModel of this attribute
			MetaModel metaModel = getMetaModelFromTree(treeNode);
			attribute.setMetaModel(metaModel);
		}
		return attribute;
	}
}
