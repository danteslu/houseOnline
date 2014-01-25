package test.common;

import org.junit.Test;

import com.houseOnline.common.xml.XSDNode;
import com.houseOnline.common.xml.XSDReader;
import com.houseOnline.model.HouseDetail;

public class TestXSDReader {

	@Test
	public void test() throws Exception {
		XSDReader reader = new XSDReader();
		
		XSDNode node = reader.getXSDForClass(HouseDetail.class.getName());
		
		System.out.println(node);
	}

}
