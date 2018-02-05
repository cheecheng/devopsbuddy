package com.cheecheng.devopsbuddy;

import com.cheecheng.devopsbuddy.backend.service.I18NService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DevopsbuddyApplicationTests {

/*
	@Test
	public void contextLoads() {
	}
*/

	// **** Integration test ****

	private I18NService i18NService;

	/*
	Cannot use constructor injection here, because
	java.lang.Exception: Test class should have exactly one public zero-argument constructor

	@Autowired
	public DevopsbuddyApplicationTests(I18NService i18NService) {
		this.i18NService = i18NService;
	}
	*/

	@Autowired
	public void setI18NService(I18NService i18NService) {
		this.i18NService = i18NService;
	}

	@Test
	public void testMessageByLocaleService() throws Exception {
		String expected = "Bootstrap starter template";
		String messageId = "index.main.callout";
		String actual = i18NService.getMessage(messageId);
		Assert.assertEquals("The actual and expected strings don't match", expected, actual);
	}
}
