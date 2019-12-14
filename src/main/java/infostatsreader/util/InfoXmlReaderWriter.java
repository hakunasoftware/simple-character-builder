package infostatsreader.util;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import lombok.AllArgsConstructor;
import lombok.Getter;

public abstract class InfoXmlReaderWriter<E extends DTO> {
	private final File xmlFile;
	private final Document doc;

	public abstract E readDTOFromXml();

	public abstract void updateXmlFromDTO(E dto);

	protected InfoXmlReaderWriter(String xmlURI) {
		if (!xmlURI.endsWith("/Info.xml")) {
			throw new IllegalArgumentException("The URI " + xmlURI + " is not a valid path");
		}
		this.xmlFile = new File(xmlURI);
		if (!xmlFile.exists()) {
			throw new IllegalArgumentException("The file " + xmlURI + " doesn't exist");
		}
		this.doc = getDocumentFromFile();
	}

	private Document getDocumentFromFile() {
		try {
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			return docBuilder.parse(xmlFile);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Reading Document from file failed");
		}
	}

	protected String readStringByUniqueTagName(String tagName) {
		return doc.getDocumentElement().getElementsByTagName(tagName).item(0).getTextContent();
	}

	protected Integer readIntegerByUniqueTagName(String tagName) {
		return Integer.valueOf(readStringByUniqueTagName(tagName));
	}

	protected void persistChangesToDocument() {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.transform(new DOMSource(doc), new StreamResult(xmlFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void updateOrAddUniqueTagFromString(String tagName, String newContent) {
		Element docElement = doc.getDocumentElement();

		// TODO: split tagname into parent/child nodes first using Tag class

		NodeList nodeList = docElement.getElementsByTagName(tagName);

		if (nodeList == null) {
			throw new IllegalArgumentException("No nodes with the tag " + tagName + " found");
		}

		if (nodeList.getLength() != 0) {
			nodeList.item(0).setTextContent(newContent);
		} else {
			Element newElement = doc.createElement(tagName);
			newElement.appendChild(doc.createTextNode(newContent));

			docElement.appendChild(doc.createTextNode("\t"));
			docElement.appendChild(newElement);
			docElement.appendChild(doc.createTextNode("\n"));
		}
	}

	protected void updateOrAddUniqueTagFromInt(String tagName, int newContent) {
		updateOrAddUniqueTagFromString(tagName, String.valueOf(newContent));
	}

	@Getter
	private static class Tag {
		private final String parentName;
		private final String childName;
		private final boolean hasParent;

		Tag(String tagName) {
			hasParent = tagName.contains("/");
			
			if (hasParent) {
				String[] components = tagName.split("/");
				parentName = components[components.length - 2];
				childName = components[components.length - 1];
			} else {
				parentName = null;
				childName = tagName;
			}
		}
	}
}