package utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
//import java.util.logging.Logger;
import java.util.logging.Logger;

//import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.GridPane;
//import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This small utility is used to display two text files:
 * 'About' G_String, and 'About' urGenova as part of 'Help'
 *
 * @see <a href="https://github.com/G-String-Legacy/G_String/blob/main/workbench/GS_L/src/utilities/About.java">utilities.About</a>
 * @author ralph
 * @version %v..%
 */
public class About {

	/**
	 * array list of text lines
	 */
	private ArrayList<String> salItems = new ArrayList<>();

	/**
	 * document file path
	 */
	private String sFileName = null;

	/**
	 * GUI window
	 */
	private Stage myStage;

	/**
	 * subclass of JavaFX <code>Dialog</code>
	 */
	private Alert alert = null;

	/**
	 * title of Alert window
	 */
	private String sTitle = null;

	/**
	 * pointer to logger
	 */
	private Logger logger = null;

	/**
	 * constructor
	 *
	 * @param _myStage  pointer to MainStage
	 * @param _logger  pointer to application logger
	 * @param _sFileName  path to 'About file)
	 * @param _sTitle  dialog title
	 */
	public About(Stage _myStage, Logger _logger, String _sFileName, String _sTitle)
	{
		//constructor
		sTitle = _sTitle;
		myStage = _myStage;
		sFileName = _sFileName;
		logger = _logger;
		InputStream stIn = getClass().getResourceAsStream(sFileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stIn));
        String line;
        try {
			while ((line = reader.readLine()) != null) {
			    salItems.add(line);
			}
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}
        try {
			reader.close();
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}
	}

	/**
	 * operates display
	 */
	public void show()
	{
		alert = new Alert(AlertType.INFORMATION);
		alert.initOwner(myStage);
		Double dX = myStage.getX() + 200.0;
		Double dY = myStage.getY() + 75.0;
		alert.setX(dX);
		alert.setY(dY);
		alert.setWidth(250.0);
		alert.setTitle(sTitle);
		alert.setHeaderText(null);
		alert.setGraphic(null);
		GridPane Content = new GridPane();
		Content.setPrefWidth(200.00);
		Content.setHgap(20.0);
		Content.setVgap(5.0);
		Integer iCount = 0;
		String[] sItems = null;
		Text t1, t2;
		for (String s:salItems)
		{
			sItems = s.split("=");
			if (sItems.length == 2)
			{
				t1 = new Text(sItems[0] + ":");
				t1.setFont(Font.font("Serif", 16));
				t1.setFill(Color.rgb(80, 40, 13));
				t2 = new Text(sItems[1]);
				t2.setFont(Font.font("Serif", 16));
				t2.setFill(Color.rgb(80, 40, 13));
				Content.add(t1, 0, iCount);
				Content.add(t2, 1, iCount);
				iCount++;
			} else {
				t1 = new Text(sItems[0]);
				t1.setFont(Font.font("Serif", 16));
				t1.setFill(Color.rgb(80, 40, 13));
				Content.add(t1, 0, iCount);
				iCount++;
			}
		}
		alert.setResizable(true);
		DialogPane dp = alert.getDialogPane();
		dp.getStylesheets().add("../resources/myDialog.css");
		dp.getStyleClass().add("myDialog");
		dp.setContent(Content);
		ButtonBar buttonBar = (ButtonBar)dp.lookup(".button-bar");
		buttonBar.getButtons().forEach(b->b.setStyle("-fx-font-size: 16;-fx-background-color: #551200;-fx-text-fill: #ffffff;-fx-font-weight: bold;"));
		alert.showAndWait();
	}

	/**
	 * turns 'About' display off
	 */
	void closeAbout()
	{
		alert.close();
	}
}
