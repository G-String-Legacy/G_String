package utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Optional;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class Popup {
	private Logger logger;
	private Stage stage = null;;
	private int iPackage = -1;
	private int iObject = -1;
	private String sMethod = null;
	private String sMessage;
	private Throwable e;
	private Filer flr;
	private StringBuilder sb = null;
	private File fPM = null;
	private Object stackTrace = null;
	private enum AlertLevel {
		Log,
		Info,
		Warning,
		Crash
	}
	private AlertLevel aLevel = null;

	
	public Popup(Logger _logger, Stage _stage) {
		logger = _logger;
		stage = _stage;
	}
	
	public void setFiler(Filer _flr) {
		flr = _flr;
	}
	
	
	public void tell(int _iPackage, int _iObject, String _sMethod, String _sMessage, Throwable _e) {
		sMethod = _sMethod;
		iObject = _iObject;
		iPackage = _iPackage;
		sMessage = _sMessage;
		e = _e;
		if (e != null)
			stackTrace = e.getStackTrace();
		else
			aLevel = AlertLevel.Log;
		tell();
	}
	
	public void tell (String _sMethod, Throwable _e) {		
		sMethod = _sMethod;
		e = _e;
		if (e != null)
			stackTrace = e.getStackTrace();
		else
			stackTrace = null;
		tell();
	}
	
	public  void tell (String _sMethod, String _sMessage) {
		aLevel = AlertLevel.Log;
		sMethod = _sMethod;
		sMessage = _sMessage;
		stackTrace = null;
		tell();
	}
	
	public void tell (String _sMethod, String _sMessage, Throwable _e) {		
		sMethod = _sMethod;
		e = _e;
		if (e != null)
			stackTrace = e.getStackTrace();
		else
			stackTrace = null;
		sMessage = _sMessage;
		tell();
	}
	
	private void tell() {
		Boolean bOFF = logger.getLevel().getName().equals("OFF");
		sb = new StringBuilder("M" + sMethod + ": " + sMessage);
		if ((aLevel == AlertLevel.Log) && (sMessage != null)) {
			if (!bOFF)
				logger.info(sb.toString());
			return;
		}
		String sLog = null;
		String sPostMortem = null;
		Alert alert = new Alert(AlertType.WARNING);
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.setMinHeight(Region.USE_PREF_SIZE);
		dialogPane.getStylesheets().add(
				   getClass().getResource("/resources/myDialog.css").toExternalForm());
		dialogPane.getStyleClass().add("myDialog");
		Double dX = stage.getX() + 200.0;
		Double dY = stage.getY() + 75.0;
		alert.setX(dX);
		alert.setY(dY);
		alert.initOwner(stage);
		alert.setTitle("Something went awry!");
		alert.setHeaderText("Source: Package " + iPackage + "; Object " + iObject + "; Method " + sMethod + ";");
		ButtonType pm = new ButtonType("Autopsy");
		dialogPane.getButtonTypes().add(pm);
		ButtonType cont = new ButtonType("Continue");
		dialogPane.getButtonTypes().add(cont);
		ButtonType exit = new ButtonType("Exit");
		dialogPane.getButtonTypes().add(exit);
		Button blind = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
		blind.setVisible(false);
		Button okButton = (Button) alert.getDialogPane().lookupButton(exit);
		Button pmButton = (Button) alert.getDialogPane().lookupButton(pm);
		pmButton.setPrefWidth(50.0);
		Button contButton = (Button) alert.getDialogPane().lookupButton(cont);
		okButton.setStyle("-fx-font-size: 16;-fx-background-color: #805015;-fx-text-fill: #ffffff;-fx-font-weight: bold;");
		pmButton.setStyle("-fx-font-size: 16;-fx-background-color: #805015;-fx-text-fill: #ffffff;-fx-font-weight: bold;");
		pmButton.setPrefWidth(200.0);
		contButton.setStyle("-fx-font-size: 16;-fx-background-color: #805015;-fx-text-fill: #ffffff;-fx-font-weight: bold;");
		pmButton.setVisible(false);
		if (sMessage != null)
			sb = new StringBuilder(sMessage);
		if (e != null) {
			if (sb == null)
				sb = new StringBuilder(e.getMessage());
			else
				sb.append("\n" + e.getMessage());
		}
		if (sMessage != null) {
			aLevel = AlertLevel.Info;
			sb = new StringBuilder (sMessage);
		}
		if (e != null) {
			aLevel = AlertLevel.Warning;
			if (sb == null)
				sb = new StringBuilder(e.getMessage());
			else
				sb.append("\n" + "M" + sMethod + "; " + e.getMessage());
			sLog = sb.toString();
		}
		if (stackTrace != null) {
			aLevel = AlertLevel.Crash;
			sb.append("; Stacktrace available");
			sLog = sb.toString();
			for ( StackTraceElement ste : e.getStackTrace())
				sb.append("\n" + ste.toString());
			sPostMortem = sb.toString();
			pmButton.setVisible(true);
			pmButton.setPrefWidth(10);
			contButton.setVisible(false);
			logger.severe(sLog);
		} else if (aLevel == AlertLevel.Warning)
			logger.warning(sLog);
		if (sb == null)
			return;
		alert.setContentText(sMessage);
		Optional<ButtonType> result = alert.showAndWait();
		switch (result.get().getText()) {
			case "Exit":
				System.exit(iObject);
				break;
			case "Continue":
				return;
			case "Autopsy":
			try {
				saveString(sPostMortem, flr);
				System.exit(2);
			} catch (IOException e1) {
				e1.printStackTrace();	//no display needed
				System.exit(iObject);
			}
		}
	}
	
	private void saveString(String s, Filer _flr) throws IOException {
		OutputStream outputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		fPM = flr.getFile(false, "Save Post Mortem", stage);
		if (fPM != null) {
				outputStream = new FileOutputStream(fPM);
				outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
				outputStreamWriter.write(sb.toString());
				outputStreamWriter.flush();
				outputStreamWriter.close();
		}
	}
	
	public void setObject (int _iObject) {
		iObject = _iObject;		
	}
		
	public void setObject(int _iPackage, int _iObject) {
		iPackage = _iPackage;
		iObject = _iObject;
	}
	
	public void setPackage(int _iPackage) {
		iPackage = _iPackage;
	}
}
