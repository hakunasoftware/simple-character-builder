package simplecharacterbuilder.common.resourceaccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class ConfigReader {
	private final Properties prop = new Properties();

	private List<String> allValuesCache;

	public ConfigReader(File configFile) {
		loadProperty(configFile);
	}

	private void loadProperty(File configFile) {
		try (InputStream inputStream = new FileInputStream(configFile)) {
			this.prop.load(inputStream);
		} catch (Exception e) {
			System.err.println("Error loading config");
			e.printStackTrace();
		}
	}

	public int readInt(String propertyName) {
		return Integer.parseInt(readString(propertyName));
	}

	public boolean readBoolean(String propertyName) {
		return Boolean.parseBoolean(readString(propertyName));
	}

	public String readString(String propertyName) {
		return this.prop.getProperty(propertyName).trim();
	}

	public List<String> readAllValues() {
		return allValuesCache != null ? allValuesCache
				: (allValuesCache = Collections.list(this.prop.elements()).stream().map(e -> (String) e).collect(Collectors.toList()));
	}
}
