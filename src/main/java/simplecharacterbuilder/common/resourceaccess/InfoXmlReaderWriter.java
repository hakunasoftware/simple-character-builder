package simplecharacterbuilder.common.resourceaccess;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import simplecharacterbuilder.common.ErrorLogfileWriter;

public class InfoXmlReaderWriter{
	private final File xmlFile;
	private final Document doc;

	public InfoXmlReaderWriter(String xmlURI) {
		if (!xmlURI.endsWith("Info.xml")) {
			throw new IllegalArgumentException("The URI " + xmlURI + " is not a valid path");
		}
		this.xmlFile = new File(xmlURI);
		if (!xmlFile.exists()) {
			throw new IllegalArgumentException("The file " + xmlURI + " doesn't exist");
		}
		this.doc = getDocumentFromFile();
	}
	
	public String readStringFromUniqueTagPath(String tagPath) {
		return findNodesFromTagWithUniqueParents(new Tag(tagPath)).get(0).getTextContent();
	}
	
	public Integer readIntegerFromUniqueTagPath(String tagPath) {
		return Integer.valueOf(readStringFromUniqueTagPath(tagPath));
	}

	/**
	 * Changes the value of the target node. Changes need to be persisted.
	 * @param tagPath
	 * @param newContent
	 */
	public void writeStringToUniqueTagPath(String tagPath, String newContent) {
		findNodesFromTagWithUniqueParents(new Tag(tagPath)).get(0).setTextContent(newContent);
	}
	
	/**
	 * Changes the value of the target node. Changes need to be persisted.
	 * @param tagPath
	 * @param newContent
	 */
	public void writeIntToUniqueTagPath(String tagPath, int newContent) {
		writeStringToUniqueTagPath(tagPath, String.valueOf(newContent));
	}

	public void persistChangesToDocument() {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.transform(new DOMSource(doc), new StreamResult(xmlFile));
		} catch (Exception e) {
			ErrorLogfileWriter.logException(e);
		}
	}

	private Document getDocumentFromFile() {
		try {
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			return docBuilder.parse(xmlFile);
		} catch (Exception e) {
			ErrorLogfileWriter.logException(e);
			throw new IllegalArgumentException("Reading Document from file failed");
		}
	}
	
	private List<Node> findNodesFromTagWithUniqueParents(Tag tag) {
		String[] parentsNames = tag.getParentsNames();
		Node currentNode      = doc.getDocumentElement();
		if(parentsNames != null) {
			for(int i = 0; i < parentsNames.length; i++) {
				currentNode =  findUniqueNode(currentNode.getChildNodes(), parentsNames[i]);
			}
		}
		return findChildrenWithName(currentNode.getChildNodes(), tag.getChildName());
	}
	
	private List<Node> findChildrenWithName(NodeList childNodes, String childName) {
		List<Node> matchingNodes = new ArrayList<>();
		for(int i = 0; i < childNodes.getLength(); i++) {
			Node currentNode = childNodes.item(i);
			if(childName.equals(currentNode.getNodeName())){
				matchingNodes.add(currentNode);
			}
		}
		return matchingNodes;
	}

	private Node findUniqueNode(NodeList nodeList, String nodeName) {
		for(int i = 0; i < nodeList.getLength(); i++) {
			Node currentNode = nodeList.item(i);
			if(currentNode.getNodeName().equals(nodeName)) {
				return currentNode;
			}
		}
		
		throw new IllegalArgumentException("No node called " + nodeName + " exists.");
	}

	
	private static class Tag {
		private final String[] parentsNames;
		private final String childName;
		private final boolean hasParents;

		protected Tag(String tagName) {
			this.hasParents = tagName.contains("/");
			
			if (hasParents) {
				String[] components = tagName.split("/");
				parentsNames = Arrays.copyOf(components, components.length - 1);
				childName = components[components.length - 1];
			} else {
				parentsNames = null;
				childName = tagName;
			}
		}
		
		public String[] getParentsNames() {
			return parentsNames;
		}

		public String getChildName() {
			return childName;
		}
	}
}