package com.houseOnline.common;

import java.io.Reader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class JAXBHelper {
	
	public static void toString(Object object) {
		
		try {
			
			JAXBContext jc = JAXBContext.newInstance(object.getClass());
			
			Marshaller marshaller = jc.createMarshaller();
			
	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        
	        marshaller.marshal(object, System.out);
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	public static Object newInstance(Reader reader, Class<?> clazz) throws JAXBException {
		
		JAXBContext jc = JAXBContext.newInstance(clazz);
		
		Unmarshaller unMarshaller = jc.createUnmarshaller();
		
		return unMarshaller.unmarshal(reader);
	}
}
