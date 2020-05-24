package simplecharacterbuilder.characterbuilder.util.holder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import simplecharacterbuilder.common.generated.ObjectFactory;

public class JAXBContextHolder {
	private static final JAXBContext CONTEXT;
	static {
		try {
			CONTEXT = JAXBContext.newInstance(ObjectFactory.class);
		} catch (JAXBException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private JAXBContextHolder() {}

	public static Marshaller createMarshaller() {
		try {
			Marshaller marshaller = CONTEXT.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			return marshaller;
		} catch (JAXBException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Unmarshaller createUnmarshaller() {
		try {
			return CONTEXT.createUnmarshaller();
		} catch (JAXBException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
