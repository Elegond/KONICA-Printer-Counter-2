package work.saretzki.gui;
	
import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;


public class ServerGUI extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			VBox root = new VBox();
			VBox hyper = new VBox();
			VBox progress = new VBox();
			
			Slider s = new Slider();
			s.setMin(-0.1);
			s.setMax(1);
			s.setShowTickLabels(true);
			s.setMajorTickUnit(0.1);
			s.setShowTickMarks(true);
			s.setMinorTickCount(9);
			s.setBlockIncrement(0.1);

			ProgressBar pb = new ProgressBar();
			pb.progressProperty().bind(s.valueProperty());
			pb.setPrefSize(800, 10);
			pb.setPrefHeight(10);
			pb.setPrefWidth(Toolkit.getDefaultToolkit().getScreenSize().getWidth());
			
			ProgressIndicator pi = new ProgressIndicator();
			pi.progressProperty().bind(s.valueProperty());
			pi.setPrefHeight(80);
			pi.setMinHeight(40);
			
			ScrollPane sp = new ScrollPane();
			sp.setContent(new ImageView(new Image(getClass().getResourceAsStream("859657.jpg"))));
			sp.setPrefSize(400, 400);
			
			
			Hyperlink hl = new Hyperlink("google.de");
			
			hl.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent event) {
					try {
						Desktop.getDesktop().browse(new URI("http://google.de"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			});
			
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
			mb.getMenus().add(menuFile);
			
			//menu end
			
			
			ObservableList<String> item = FXCollections.observableArrayList("efs","svds","sdfg","sdxy");
			ListView<String> list = new ListView<>();
			list.setItems(item);
			list.setPrefHeight(150);
			list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			
			list.setCellFactory(TextFieldListCell.forListView());
			list.setEditable(true);
			
			Label label = new Label();
			list.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<Integer>() {

				@Override
				public void onChanged(Change<? extends Integer> c) {
					ObservableList<? extends Integer> innerList = c.getList();
					StringBuilder sb = new StringBuilder();
					for(int i = 0; i<innerList.size(); i++) {
						try {
						sb.append(item.get(innerList.get(i))+"\n");
						}catch (Exception e) {
							//fuck off buggy shit
						}
					}
					label.setText(sb.toString());
				}
			});
			
			

			root.getChildren().add(mb);
			
			root.getChildren().add(sp);
			progress.getChildren().add(s);
			
			progress.getChildren().add(pb);
			progress.getChildren().add(pi);
			hyper.getChildren().add(hl);
			Accordion acc = new Accordion();
			
			acc.getPanes().add(new TitledPane("Progress", progress));
			acc.getPanes().add(new TitledPane("Link", hyper));
			root.getChildren().add(acc);
			
			root.getChildren().add(list);

			root.getChildren().add(label);
			
			Scene scene = new Scene(root,1000,800);
			
			primaryStage.setScene(scene);
			primaryStage.setFullScreen(true);
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void stop() throws Exception {
		System.exit(0);
		super.stop();
	}
	public static void l() {
		launch("");
	}
}
