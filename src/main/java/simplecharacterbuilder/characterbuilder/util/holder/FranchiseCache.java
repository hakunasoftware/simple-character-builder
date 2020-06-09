package simplecharacterbuilder.characterbuilder.util.holder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;

import simplecharacterbuilder.characterbuilder.util.transform.ValueFormatter;
import simplecharacterbuilder.common.generated.Actor;
import simplecharacterbuilder.common.resourceaccess.GameFileAccessor;
import simplecharacterbuilder.common.resourceaccess.PropertyRepository;

public class FranchiseCache {
	private static final FranchiseCache INSTANCE = new FranchiseCache();

	private final Unmarshaller unmarshaller = JAXBContextHolder.createUnmarshaller();

	private final Map<Actor, File> franchiseActorsInfoXmls = new HashMap<>();
	private final Map<Actor, File> franchiseActorsPortraits = new HashMap<>();
	
	private List<Runnable> refreshListener = new ArrayList<>();

	private String franchise;

	private FranchiseCache() {}

	public static void setFranchise(String franchise) {
		if(ValueFormatter.isEmpty(franchise)) {
			INSTANCE.franchise = "NotADirectory";
			refresh();
			return;
		}
		if(franchise.equals(INSTANCE.franchise)) {
			return;
		}
		INSTANCE.franchise = franchise;
		refresh();
	}

	public static Map<Actor, File> getActorsAndInfoXmls() {
		return new HashMap<>(INSTANCE.franchiseActorsInfoXmls);
	}

	public static Map<Actor, File> getActorsAndPortraits() {
		return new HashMap<>(INSTANCE.franchiseActorsPortraits);
	}
	
	public static void addRefreshListener(Runnable refreshListener) {
		INSTANCE.refreshListener.add(refreshListener);
	}

	private static void refresh() {
		INSTANCE.franchiseActorsInfoXmls.clear();
		INSTANCE.franchiseActorsPortraits.clear();
		File franchiseFolder = getFranchiseDirectory();
		if(!franchiseFolder.isDirectory()) {
			INSTANCE.refreshListener.stream().forEach(r -> r.run());
			return;
		}
		FileUtils.listFiles(franchiseFolder, new String[] { "xml" }, true).stream()
				.filter(f -> "Info.xml".equalsIgnoreCase(f.getName()))
				.forEach(f -> {
					Actor actor = unmarshallInfoXml(f);
					INSTANCE.franchiseActorsInfoXmls.put(actor, f);
					INSTANCE.franchiseActorsPortraits.put(actor, getPortraitForInfoXml(f));
				});
		INSTANCE.refreshListener.stream().forEach(r -> r.run());
	}

	private static Actor unmarshallInfoXml(File file) {
		try {
			return (Actor) INSTANCE.unmarshaller.unmarshal(file);
		} catch (JAXBException e) {
			throw new IllegalArgumentException("Error parsing Info.xml " + file.getAbsolutePath(), e);
		}
	}
	
	private static File getFranchiseDirectory() {
		return new File(GameFileAccessor.getFileFromProperty(PropertyRepository.ACTORS_FOLDER), INSTANCE.franchise);
	}
	
	private static File getPortraitForInfoXml(File infoXml) {
		return new File(infoXml.getParentFile(), "Portrait.png");
	}
	
	public String getFranchise() {
		return franchise;
	}

}
