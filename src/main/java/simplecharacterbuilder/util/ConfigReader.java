package simplecharacterbuilder.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
	private final Properties prop   = new Properties();
	
	public ConfigReader(String configPath) {
		loadProperty(configPath);
	}
	
	private void loadProperty(String configPath) {
		try (InputStream inputStream = new FileInputStream(configPath)) {
			this.prop.load(inputStream);
		} catch (Exception e) {
			System.err.println("Error loading config");
			e.printStackTrace();
		}
	}
	
	public int readInt(String propertyName) {
		return Integer.parseInt(this.prop.getProperty(propertyName).trim());
	}
	
	public boolean readBoolean(String propertyName) {
		return Boolean.parseBoolean((this.prop.getProperty(propertyName)).trim());
	}
}
