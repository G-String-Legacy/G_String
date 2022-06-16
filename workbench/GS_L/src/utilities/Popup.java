package utilities;

import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * popup is the means to communicate with the user additionally to the regular
 * stepping process.
 * When popup is triggered by a Throwable exception, it sends the message to the log file.
 * When popup is triggered by other needs, the message goes to a popup window.
 *
 */

public class Popup {
	private Logger logger;
	private String sMessage;
	private String sClass = null;
	private String sMethod = null;

	
	public Popup(Logger _logger, Stage _stage) {
		logger = _logger;
	}
	
	public void setClass (@SuppressWarnings("rawtypes") Class _this) {
		sClass = _this.getName();
	}
	
	public void setClass (String _sClass) {
		sClass = _sClass;
	}
	
	public void tell(String _sMethod, Throwable _e) {
		sMethod = _sMethod;
		sMessage = sClass + "(" + sMethod + "): " + _e.getMessage();
		logger.warning(sMessage);
	}
	
	public void tell(String _sMethod, String _sMessage) {
		sMethod = _sMethod;
        Alert a = new Alert(AlertType.INFORMATION);
        a.setTitle("Information");
        a.setHeaderText(sClass + "(" + sMethod + ")");
        a.setResizable(true);
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.setContentText(_sMessage);
        a.showAndWait();
	}
	
}
