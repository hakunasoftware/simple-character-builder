package simplecharacterbuilder.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;

public class ErrorLogfileWriter {
	private static final ErrorLogfileWriter INSTANCE = new ErrorLogfileWriter();
	
	private final File logFileDir = new File("ErrorLogs");
	
	private ErrorLogfileWriter() {}
	
	public static void logException(Exception e) {
		INSTANCE.logFileDir.mkdir();
        try {
        	String fileName = "CharacterBuilderException_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".log";
        	try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(new File(INSTANCE.logFileDir, fileName), true)), true)) {
        		e.printStackTrace(writer);
        		e.printStackTrace();
        	}
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "An error occured during writing the error logfile. Fuck.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
