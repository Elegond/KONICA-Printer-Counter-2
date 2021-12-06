package work.saretzki.gui;
	
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Application.Parameters;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;


public class ClientGUI extends Application {
	@Override
	public void init() throws Exception {
		Parameters p = getParameters();
		List<String> list = p.getRaw();
		for (String r : list) {
			System.out.println(r);
		}
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			VBox root = new VBox();
			Button b = new Button("button");
			//menu
			MenuBar mb = new MenuBar();
			Menu menuFile = new Menu("Datei");
			MenuItem mItemNew = new MenuItem("New");
			MenuItem mItemOpen = new MenuItem("Open ...");
			MenuItem mItemSave = new MenuItem("Save");
			MenuItem mItemSaveAs = new MenuItem("Save as ...");
			MenuItem mItemClose = new MenuItem("Close");
			
			mItemClose.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					Platform.exit();
				}
			});
			
			menuFile.getItems().addAll(mItemNew, mItemOpen, mItemSave,mItemSaveAs,new SeparatorMenuItem(), mItemClose);
			
			Menu menuHelp = new Menu("Extras");
			MenuItem mItemSettings = new MenuItem("Einstellungen");
			MenuItem mItemAbout = new MenuItem("Über");
			
			
			menuHelp.getItems().addAll(mItemSettings,new SeparatorMenuItem(),mItemAbout);
			mb.getMenus().addAll(menuFile,menuHelp);
			
			//menu end

			ToggleButton tb = new ToggleButton("ToggleButton");
			ToggleButton tb1 = new ToggleButton("ToggleButton1");
			ToggleButton tb2 = new ToggleButton("ToggleButton2");
			ToggleGroup tg = new ToggleGroup();
			tb.setToggleGroup(tg);
			tb1.setToggleGroup(tg);
			Label label = new Label("Hallo Welt");
			ChoiceBox<String> chb = new ChoiceBox<String>(
					FXCollections.observableArrayList("Deutsch", "Englisch", "Japanisch"));
			String[] text = new String[] { "Hallo Welt", "Hello World", "bla" };
			CheckBox cb = new CheckBox("Box");

			TextArea tf = new TextArea();
			tf.setPromptText("text");
			tf.setPrefRowCount(3);
			root.getChildren().add(mb);
			root.getChildren().add(tf);
			root.getChildren().add(b);
			root.getChildren().add(label);

			root.getChildren().add(new Separator(Orientation.HORIZONTAL));
			root.getChildren().add(cb);

			root.getChildren().add(new Separator(Orientation.HORIZONTAL));
			root.getChildren().add(tb);
			root.getChildren().add(tb1);
			root.getChildren().add(tb2);

			root.getChildren().add(new Separator(Orientation.HORIZONTAL));
			root.getChildren().add(chb);
			chb.setTooltip(new Tooltip("Sprache"));

			chb.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					primaryStage.setTitle(text[newValue.intValue()]);
				}
			});

			b.setOnAction((ActionEvent e) -> {
				label.setText(tf.getText());
			});

			Scene scene = new Scene(root, 400, 400);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Hallo Welt");
			primaryStage.show();
			b.requestFocus();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() throws Exception {
		System.exit(0);
	}
	public void l() {
		launch("");
	}
	
}
