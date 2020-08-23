package simplecharacterbuilder.common.statgenerator;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.border.Border;

import simplecharacterbuilder.common.generated.Actor;
import simplecharacterbuilder.common.resourceaccess.ConfigReader;
import simplecharacterbuilder.common.resourceaccess.ConfigReaderRepository;
import simplecharacterbuilder.common.resourceaccess.PropertyRepository;
import simplecharacterbuilder.common.uicomponents.CharacterBuilderComponent.CharacterBuilderMainComponent;

public class StatGenerator extends CharacterBuilderMainComponent {

	public static final int WIDTH = RegularStatSelectionPanel.WIDTH + BeautySelectionPanel.WIDTH
			+ StatDisplayPanel.WIDTH + 4 * GAP_WIDTH;
	public static final int HEIGHT = RegularStatSelectionPanel.HEIGHT + 2 * GAP_WIDTH;

	public static final int COMPARISON_WIDTH = 82;

	private final RegularStatSelectionPanel regularStatSelectionPanel;
	private final BeautySelectionPanel beautySelectionPanel;
	private final StatDisplayPanel statDisplayPanel;
	private final StatCalculator statCalculator;

	private final boolean ALWAYS_SCALE;
	private final boolean NEVER_SCALE;

	protected StatGenerator(boolean showComparison) {
		statCalculator = new StatCalculator();
		regularStatSelectionPanel = new RegularStatSelectionPanel(this, GAP_WIDTH, GAP_WIDTH, showComparison);
		beautySelectionPanel = new BeautySelectionPanel(this, 2 * GAP_WIDTH + RegularStatSelectionPanel.WIDTH,
				GAP_WIDTH, showComparison);
		statDisplayPanel = new StatDisplayPanel(this,
				3 * GAP_WIDTH + RegularStatSelectionPanel.WIDTH + BeautySelectionPanel.WIDTH, GAP_WIDTH,
				showComparison);

		mainPanel.add(regularStatSelectionPanel);
		mainPanel.add(statDisplayPanel);
		mainPanel.add(beautySelectionPanel);

		if (showComparison) {
			mainPanel.setBounds(mainPanel.getX(), mainPanel.getY(), mainPanel.getWidth() + COMPARISON_WIDTH,
					mainPanel.getHeight());
		}

		ConfigReader configReader = ConfigReaderRepository.getCharacterbuilderConfigReader();
		ALWAYS_SCALE = configReader.readBoolean(PropertyRepository.ALWAYS_SCALE);
		NEVER_SCALE = configReader.readBoolean(PropertyRepository.NEVER_SCALE);
	}

	public static StatGenerator createInstance(boolean showComparison) {
		return new StatGenerator(showComparison);
	}

	public Map<Stat, Integer> getStats() {
		return statDisplayPanel.getStats();
	}

	public void setStatSuggestions(Map<Stat, Integer> stats) {
		long valuesOutOfBounds = Stat.getAll().stream().filter(stat -> stats.get(stat) > statCalculator.getMax(stat))
				.count();
		if (!NEVER_SCALE && valuesOutOfBounds >= 1 && (ALWAYS_SCALE || confirmScaling(valuesOutOfBounds))) {
			statCalculator.scaleToMaximalValue(stats);
		}
		statDisplayPanel.displayStats(statCalculator.generateStatSuggestions(stats));
	}

	private boolean confirmScaling(long valuesOutOfBounds) {
		StringBuilder dialogueText = new StringBuilder().append(valuesOutOfBounds);
		if (valuesOutOfBounds == 1) {
			dialogueText.append(" stat exceeds its maximal value.");
		} else {
			dialogueText.append(" stats exceed their maximal value.");
		}
		dialogueText.append(" Do you want to downscale all stats?");
		return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(mainPanel.getParent(), dialogueText,
				"Loading stats from Info.xml", JOptionPane.YES_NO_OPTION);
	}

	public void setStats(Map<Stat, Integer> stats) {
		statDisplayPanel.displayStats(stats);
	}

	public void setVisible(boolean aFlag) {
		mainPanel.setVisible(aFlag);
	}

	public void setBorder(Border border) {
		mainPanel.setBorder(border);
	}

	public boolean confirmIntentIfWarningsExist() {
		List<String> warnings = statDisplayPanel.getEvaluationWarnings();
		if (warnings.isEmpty()) {
			return true;
		}

		StringBuilder dialogueText = new StringBuilder(
				"The following values don't comply with the configured settings: \n\n");
		warnings.stream().forEach(warning -> dialogueText.append("- ").append(warning).append("\n"));
		dialogueText.append("\nAre you sure that you want to proceed?");

		return JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(mainPanel.getParent(), dialogueText.toString(),
				"Are you sure?", JOptionPane.OK_CANCEL_OPTION);
	}

	public void setComparisonValues(Map<Stat, Integer> values) {
		statDisplayPanel.setComparisonValues(values);
	}

	RegularStatSelectionPanel getRegularStatSelectionPanel() {
		return regularStatSelectionPanel;
	}

	StatDisplayPanel getStatDisplayPanel() {
		return statDisplayPanel;
	}

	BeautySelectionPanel getBeautySelectionPanel() {
		return beautySelectionPanel;
	}

	StatCalculator getStatCalculator() {
		return statCalculator;
	}

	@Override
	public void setValues(Actor actor) {
		Map<Stat, Integer> selectedStats = getStats();
		Actor.Stats stats = new Actor.Stats();
		stats.setAgi(mapSelectionToValue(selectedStats, Stat.AGILITY));
		stats.setBeauty(mapSelectionToValue(selectedStats, Stat.BEAUTY));
		stats.setCharisma(mapSelectionToValue(selectedStats, Stat.CHARISMA));
		stats.setCon(mapSelectionToValue(selectedStats, Stat.CONSTITUTION));
		stats.setInt(mapSelectionToValue(selectedStats, Stat.INTELLIGENCE));
		stats.setObedience(mapSelectionToValue(selectedStats, Stat.OBEDIENCE));
		stats.setSex(mapSelectionToValue(selectedStats, Stat.SEX));
		stats.setStr(mapSelectionToValue(selectedStats, Stat.STRENGTH));
		actor.setStats(stats);
	}
	
	private BigInteger mapSelectionToValue(Map<Stat, Integer> selectedStats, Stat stat) {
		Integer selectedValue = selectedStats.get(stat);
		return selectedValue != 0 ? BigInteger.valueOf(selectedValue) : null;
	}
}