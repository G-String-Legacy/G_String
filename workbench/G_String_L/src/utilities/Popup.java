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
	//private int iObject = -1;
	private String sMessage;
	private Filer flr;
	private StringBuilder sb = null;
	private File fPM = null;
	private Object stackTrace = null;
	private Boolean bShow = false;
	private Throwable error = null;
	private String sClass = null;
	private String sMethod = null;

	
	public Popup(Logger _logger, Stage _stage) {
		logger = _logger;
		stage = _stage;
	}
	
	public void setFiler(Filer _flr) {
		flr = _flr;
	}
	
	public void setClass (@SuppressWarnings("rawtypes") Class _this) {
		sClass = _this.getName();
	}
	
	public void setClass (String _sClass) {
		sClass = _sClass;
	}
	
	public void tell(String _sMethod, Throwable _e) {
		sMethod = _sMethod;
		error = _e;
		tell();
	}
	
	public void tell(String _sMethod, String _sMessage) {
		sMethod = _sMethod;
		sMessage = _sMessage;
		tell();
	}
	
	private void tell() {
		/**
		 * This is the main 'tell' method, the others just feed into it.
		 */
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
		if (bShow) {
			alert.setTitle("Important!");
			//alert.setHeaderText(null);
		} else {
			alert.setTitle("Something went awry!");
		}
		alert.setHeaderText("Source: " + sClass + "(" + sMethod + "):");
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
		if (error != null) {
			if (sb == null)
				sb = new StringBuilder(error.getMessage());
			else
				sb.append("\n" + error.getMessage());
			stackTrace = error.getStackTrace();
			if (stackTrace != null) {
				sb.append("; Stacktrace available");
				sLog = sb.toString();
				for ( StackTraceElement ste : error.getStackTrace())
					sb.append("\n" + ste.toString());
				sPostMortem = sb.toString();
				pmButton.setVisible(true);
				pmButton.setPrefWidth(10);
				contButton.setVisible(false);
				logger.severe(sLog);
			} 
		}
		if (sb == null)
			return;
		alert.setContentText(sMessage);
		Optional<ButtonType> result = alert.showAndWait();
		switch (result.get().getText()) {
			case "Exit":
				System.exit(0);
				break;
			case "Continue":
				return;
			case "Autopsy":
			try {
				saveString(sPostMortem, flr);
				System.exit(2);
			} catch (IOException e1) {
				e1.printStackTrace();	//no display needed
				System.exit(0);
			}
		}
	}
	
	private void saveString(String s, Filer _flr) throws IOException {
		OutputStream outputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		fPM = flr.getFile(false, "Save Post Mortem");
		if (fPM != null) {
				outputStream = new FileOutputStream(fPM);
				outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
				outputStreamWriter.write(sb.toString());
				outputStreamWriter.flush();
				outputStreamWriter.close();
		}
	}
	
}
