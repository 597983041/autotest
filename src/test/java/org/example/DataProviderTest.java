package org.example;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
 
public class DataProviderTest {

	@Test(dataProvider = "data")
	public void dataProviderTest(String name, int age, String hobby) {
		System.out.println(name + " " + age + " " + hobby);
	}

	@DataProvider(name = "data")
	Object[][] getData() {
		return new Object[][]{
				{"June", 22, "Sing"},
				{"Jack", 22, "Football"},
				{"Duke", 22, "Chess"}
		};
	}
}
 