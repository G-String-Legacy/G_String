package utilities;

import model.Facet;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class FacetModView extends HBox{
	/**
	 * a line in the D Study window
	 */

	Integer id = -1;
	Double dLevel = 0.0;
	Integer iLevel = 0;
	String sName = null;
	Boolean bFixed = false;

	public FacetModView (Facet f)
	{
		CheckBox cbFixed = null;
		this.setPadding(new Insets(0, 0, 0, 3));
		sName = f.getName();
		iLevel = (int) Math.round(f.getFacetLevel());
		bFixed = f.getFixed();
		Label lbName = new Label(sName);
		lbName.setMinWidth(60.0);
		TextField tfLevel = new TextField(iLevel.toString());
		tfLevel.setPrefWidth(80);
		tfLevel.setAlignment(Pos.CENTER_RIGHT);
		tfLevel.textProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != newValue)
			{
				Double dTemp = Double.parseDouble(newValue);
				f.setdFacetLevel(dTemp);
			}
		});
		Boolean bFixed = f.getFixed();
		if (!bFixed)
			cbFixed = new CheckBox("Random");
		else
			cbFixed = new CheckBox("Fixed");
		cbFixed.setSelected(bFixed);
		cbFixed.setPadding(new Insets(0,0,8,3));
		cbFixed.selectedProperty().addListener(new ChangeListener<Boolean>() {
	           public void changed(ObservableValue<? extends Boolean> ov,
	                 Boolean old_val, Boolean new_val)
	           		{
	                   if (old_val != new_val)
	                	   f.setFixed(new_val);
	           		}
	              });
		this.setAlignment(Pos.CENTER_LEFT);
		this.getChildren().addAll(lbName, tfLevel, cbFixed);
	}
}
