package base;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.io.File;

import base.Folder;
import base.Note;
import base.NoteBook;
import base.TextNote;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * 
 * NoteBook GUI with JAVAFX
 * 
 * COMP 3021
 * 
 * 
 * @author valerio
 *
 */
public class NoteBookWindow extends Application{

	/**
	 * TextArea containing the note
	 */
	final TextArea textAreaNote = new TextArea("");
	/**
	 * list view showing the titles of the current folder
	 */
	final ListView<String> titleslistView = new ListView<String>();
	/**
	 * 
	 * Combobox for selecting the folder
	 * 
	 */
	final ComboBox<String> foldersComboBox = new ComboBox<String>();
	/**
	 * This is our Notebook object
	 */
	NoteBook noteBook = null;
	/**
	 * current folder selected by the user
	 */
	String currentFolder = "";
	/**
	 * current search string
	 */
	String currentSearch = "";
	/**
	 * current note selected by user
	 */
	String currentNote = "";
	/**
	 * the stage of this display
	 */
	Stage stage;

	public static void main(String[] args) {
		launch(NoteBookWindow.class, args);
	}

	@Override
	public void start(Stage stage) {
		this.stage = stage;
		loadNoteBook();
		// Use a border pane as the root for scene
		BorderPane border = new BorderPane();
		// add top, left and center
		border.setTop(addHBox());
		border.setLeft(addVBox());
		border.setCenter(addGridPane());

		Scene scene = new Scene(border);
		stage.setScene(scene);
		stage.setTitle("NoteBook COMP 3021");
		stage.show();
	}

	/**
	 * This create the top section
	 * 
	 * @return
	 */
	private HBox addHBox() {

		HBox hbox = new HBox();
		hbox.setPadding(new Insets(15, 12, 15, 12));
		hbox.setSpacing(10); // Gap between nodes

		Button buttonLoad = new Button("Load from File");
		buttonLoad.setPrefSize(100, 20);
		Button buttonSave = new Button("Save to File");
		buttonSave.setPrefSize(100, 20);
		Label label = new Label("Search :");
		TextField textSearch = new TextField();
		textSearch.setText("");
		Button buttonSearch = new Button("Search");
		buttonSearch.setPrefSize(100, 20);
		Button buttonClear = new Button("Clear Search");
		buttonClear.setPrefSize(100, 20);
		buttonSearch.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				currentSearch = textSearch.getText();
				textAreaNote.setText("");
				ArrayList<String> list = new ArrayList<String>();
				ObservableList<String> combox2 = FXCollections.observableArrayList(list);
				ArrayList<Folder> foldlers = noteBook.getFolders();
				foldlers.forEach(f-> {if (f.getName().equals(currentFolder))
						f.searchNotes(currentSearch).forEach(n->{if (n instanceof TextNote) combox2.add(n.getTitle());});});
				titleslistView.setItems(combox2);
			}
		});
		buttonClear.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				updateListView();
				textAreaNote.setText("");
				textSearch.setText("");
			}
		});
		buttonLoad.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Please Choose A File Which Contains a NoteBook Object!");
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Serialized Object File (*.ser)", "*.ser");
				fileChooser.getExtensionFilters().add(extFilter);
				File file = fileChooser.showOpenDialog(stage);
				if (file != null) {
					loadNoteBook(file);
					updateGUI();
				}
			}
		});
		buttonSave.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Please Choose A File Which Contains a NoteBook Object!");
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Serialized Object File (*.ser)", "*.ser");
				fileChooser.getExtensionFilters().add(extFilter);
				File file = fileChooser.showOpenDialog(stage);
				if (file != null) {
					if (noteBook.save(file.getName())) {
						Alert alert = new Alert(AlertType.INFORMATION); 
						alert.setTitle("Successfully saved"); 
						alert.setContentText("Your file has been saved to file " + file.getName()); 
						alert.showAndWait();
					}
				}
			}
		});

		hbox.getChildren().addAll(buttonLoad, buttonSave, label, textSearch, buttonSearch, buttonClear);

		return hbox;
	}

	/**
	 * this create the section on the left
	 * 
	 * @return
	 */
	private VBox addVBox() {

		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10)); // Set all sides to 10
		vbox.setSpacing(8); // Gap between nodes

		// TODO: This line is a fake folder list. We should display the folders in noteBook variable! Replace this with your implementation
		ArrayList<String> s = new ArrayList<String>();
		noteBook.getFolders().forEach((f) -> s.add(f.getName()));
		foldersComboBox.getItems().addAll(s);

		foldersComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue ov, Object t, Object t1) {
				currentFolder = t1.toString();
				// this contains the name of the folder selected
				// TODO update listview
				updateListView();
			}
		});
		
		foldersComboBox.setValue("");

		titleslistView.setPrefHeight(100);

		titleslistView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue ov, Object t, Object t1) {
				if (t1 == null)
					return;
				String title = t1.toString();
				// This is the selected title
				// TODO load the content of the selected note in
				// textAreNote
				String content = "";
				List<Note> notes = noteBook.searchNotes(title);
				for (Note n: notes) {
					if (n.getTitle().equals(title) && n instanceof TextNote) {
						TextNote tn = (TextNote) n;
						content = tn.getContent();
						currentNote = n.getTitle();
					}
				}
				textAreaNote.setText(content);
			}
		});

		Button buttonAddFolder = new Button("Add a Folder");
		buttonAddFolder.setPrefSize(100, 20);
		buttonAddFolder.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				TextInputDialog dialog = new TextInputDialog("Add a Folder"); 
				dialog.setTitle("Input"); 
				dialog.setHeaderText("Add a new folder for your notebook:"); 
				dialog.setContentText("Please enter the name you want to create:");
				Optional<String> result = dialog.showAndWait(); 
				if (result.isPresent()) {
					if (result.get()=="") {
						Alert alert = new Alert(AlertType.WARNING); 
						alert.setTitle("Warning"); 
						alert.setHeaderText("Warning"); 
						alert.setContentText("Please input a valid folder name");
						alert.showAndWait();
					}
					else {
						Folder folder = new Folder(result.get());
						if (noteBook.getFolders().contains(folder)) {
							Alert alert = new Alert(AlertType.WARNING); 
							alert.setTitle("Warning"); 
							alert.setHeaderText("Warning"); 
							alert.setContentText("You already have a folder named "+folder.getName());
							alert.showAndWait();
						}
						else {
							noteBook.getFolders().add(folder);
							updateGUI();
						}
					}
				}
			}
		});
		
		Button buttonAddNote = new Button("Add a Note");
		buttonAddNote.setPrefSize(100, 20);
		buttonAddNote.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (currentFolder == "") {
					Alert alert = new Alert(AlertType.WARNING); 
					alert.setTitle("Warning"); 
					alert.setHeaderText("Warning"); 
					alert.setContentText("Please choose a folder first!");
					alert.showAndWait();
				}
				else {
					TextInputDialog dialog = new TextInputDialog("Add a Note"); 
					dialog.setTitle("Input"); 
					dialog.setHeaderText("Add a new note to current folder"); 
					dialog.setContentText("Please enter the name of your note:");
					Optional<String> inputNoteName = dialog.showAndWait(); 
					if (inputNoteName.isPresent()) {
						if (inputNoteName.get()!="" && noteBook.createTextNote(currentFolder, inputNoteName.get())) {
							Alert alert = new Alert(AlertType.INFORMATION); 
							alert.setTitle("Successful!"); 
							alert.setHeaderText("Message"); 
							alert.setContentText("Insert note "+inputNoteName.get()+" to folder "+currentFolder+" successfully!");
							alert.showAndWait();
							updateListView();
						}
						else {
							Alert alert = new Alert(AlertType.WARNING); 
							alert.setTitle("Warning!"); 
							alert.setHeaderText("Warning"); 
							alert.setContentText("Cannot insert note "+inputNoteName.get()+" to folder "+currentFolder);
							alert.showAndWait();
						}
					}
					else {
						Alert alert = new Alert(AlertType.WARNING); 
						alert.setTitle("Warning!"); 
						alert.setHeaderText("Warning"); 
						alert.setContentText("Please input name of note");
						alert.showAndWait();
					}
				}
			}
		});
		
		vbox.getChildren().add(new Label("Choose folder: "));
		HBox hBox = new HBox();
		hBox.setPadding(new Insets(15, 12, 15, 12));
		hBox.setSpacing(10);
		hBox.getChildren().addAll(foldersComboBox, buttonAddFolder);
		vbox.getChildren().add(hBox);
		vbox.getChildren().add(new Label("Choose note title"));
		vbox.getChildren().add(titleslistView);
		vbox.getChildren().add(buttonAddNote);

		return vbox;
	}

	private void updateListView() {
		ArrayList<String> list = new ArrayList<String>();

		// TODO populate the list object with all the TextNote titles of the
		// currentFolder

		ObservableList<String> combox2 = FXCollections.observableArrayList(list);
		ArrayList<Folder> foldlers = noteBook.getFolders();
		foldlers.forEach(f-> {if (f.getName().equals(currentFolder))
				f.getNotes().forEach(n->{if (n instanceof TextNote) combox2.add(n.getTitle());});});
		titleslistView.setItems(combox2);
		textAreaNote.setText("");
		currentNote = "";
	}
	
	private void updateGUI() {
		ArrayList<String> s = new ArrayList<String>();
		noteBook.getFolders().forEach((f) -> s.add(f.getName()));
		foldersComboBox.setValue("");
		foldersComboBox.getItems().clear();
		foldersComboBox.getItems().addAll(s);
		currentFolder = "";
		updateListView();
	}

	/*
	 * Creates a grid for the center region with four columns and three rows
	 */
	private GridPane addGridPane() {

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));
		textAreaNote.setEditable(true);
		textAreaNote.setMaxSize(450, 400);
		textAreaNote.setWrapText(true);
		textAreaNote.setPrefWidth(450);
		textAreaNote.setPrefHeight(400);
		
		ImageView saveView = new ImageView(new Image(new File("save.png").toURI().toString())); 
		saveView.setFitHeight(18); 
		saveView.setFitWidth(18); 
		saveView.setPreserveRatio(true);
		ImageView deleteView = new ImageView(new Image(new File("delete.png").toURI().toString())); 
		deleteView.setFitHeight(18); 
		deleteView.setFitWidth(18); 
		deleteView.setPreserveRatio(true);
		Button buttonSave = new Button("Save Note");
		buttonSave.setPrefSize(100, 20);
		Button buttonDelete = new Button("Delete Note");
		buttonDelete.setPrefSize(100, 20);
		
		buttonSave.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (currentFolder == "" || currentNote == "") {
					Alert alert = new Alert(AlertType.WARNING); 
					alert.setTitle("Warning"); 
					alert.setHeaderText("Warning"); 
					alert.setContentText("Please select a folder and a note!");
					alert.showAndWait();
				}
				else {
					Folder folder = null;
					for (Folder f : noteBook.getFolders()) {
						if (f.getName().equals(currentFolder))
							folder = f;
					}
					TextNote tnote = null;
					for (Note n : folder.getNotes()){
						if (n.getTitle().equals(currentNote) && n instanceof TextNote)
							tnote = (TextNote) n;
					}
					tnote.content = textAreaNote.getText();
//					tnote.content = textAreaNote.toString();
				}
			}
		});
		
		buttonDelete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (currentFolder == "" || currentNote == "") {
					Alert alert = new Alert(AlertType.WARNING); 
					alert.setTitle("Warning"); 
					alert.setHeaderText("Warning"); 
					alert.setContentText("Please select a folder and a note!");
					alert.showAndWait();
				}
				else {
					Folder folder = null;
					for (Folder f : noteBook.getFolders()) {
						if (f.getName().equals(currentFolder)) {
							folder = f;
							break;
						}
					}
					if (folder != null && folder.removeNotes(currentNote)) {
						updateListView();
						currentNote = "";
						textAreaNote.setText("");
						Alert alert = new Alert(AlertType.CONFIRMATION); 
						alert.setTitle("Succeed!"); 
						alert.setHeaderText("Confirmation"); 
						alert.setContentText("Your note has been succcessfully removed");
						alert.showAndWait();
					}
				}
			}
		});
		
		HBox hbox = new HBox();
		hbox.setSpacing(10); // Gap between nodes
		hbox.getChildren().addAll(saveView,buttonSave,deleteView,buttonDelete);
		grid.getChildren().add(hbox);
		
		// 0 0 is the position in the grid
		grid.add(textAreaNote, 0, 1);

		return grid;
	}

	private void loadNoteBook() {
		NoteBook nb = new NoteBook();
		nb.createTextNote("COMP3021", "COMP3021 syllabus", "Be able to implement object-oriented concepts in Java.");
		nb.createTextNote("COMP3021", "course information",
				"Introduction to Java Programming. Fundamentals include language syntax, object-oriented programming, inheritance, interface, polymorphism, exception handling, multithreading and lambdas.");
		nb.createTextNote("COMP3021", "Lab requirement",
				"Each lab has 2 credits, 1 for attendence and the other is based the completeness of your lab.");

		nb.createTextNote("Books", "The Throwback Special: A Novel",
				"Here is the absorbing story of twenty-two men who gather every fall to painstakingly reenact what ESPN called â€œthe most shocking play in NFL historyâ€� and the Washington Redskins dubbed the â€œThrowback Specialâ€�: the November 1985 play in which the Redskinsâ€™ Joe Theismann had his leg horribly broken by Lawrence Taylor of the New York Giants live on Monday Night Football. With wit and great empathy, Chris Bachelder introduces us to Charles, a psychologist whose expertise is in high demand; George, a garrulous public librarian; Fat Michael, envied and despised by the others for being exquisitely fit; Jeff, a recently divorced man who has become a theorist of marriage; and many more. Over the course of a weekend, the men reveal their secret hopes, fears, and passions as they choose roles, spend a long night of the soul preparing for the play, and finally enact their bizarre ritual for what may be the last time. Along the way, mishaps, misunderstandings, and grievances pile up, and the comforting traditions holding the group together threaten to give way. The Throwback Special is a moving and comic tale filled with pitch-perfect observations about manhood, marriage, middle age, and the rituals we all enact as part of being alive.");
		nb.createTextNote("Books", "Another Brooklyn: A Novel",
				"The acclaimed New York Times bestselling and National Book Awardâ€“winning author of Brown Girl Dreaming delivers her first adult novel in twenty years. Running into a long-ago friend sets memory from the 1970s in motion for August, transporting her to a time and a place where friendship was everythingâ€”until it wasnâ€™t. For August and her girls, sharing confidences as they ambled through neighborhood streets, Brooklyn was a place where they believed that they were beautiful, talented, brilliantâ€”a part of a future that belonged to them. But beneath the hopeful veneer, there was another Brooklyn, a dangerous place where grown men reached for innocent girls in dark hallways, where ghosts haunted the night, where mothers disappeared. A world where madness was just a sunset away and fathers found hope in religion. Like Louise Meriwetherâ€™s Daddy Was a Number Runner and Dorothy Allisonâ€™s Bastard Out of Carolina, Jacqueline Woodsonâ€™s Another Brooklyn heartbreakingly illuminates the formative time when childhood gives way to adulthoodâ€”the promise and peril of growing upâ€”and exquisitely renders a powerful, indelible, and fleeting friendship that united four young lives.");

		nb.createTextNote("Holiday", "Vietnam",
				"What I should Bring? When I should go? Ask Romina if she wants to come");
		nb.createTextNote("Holiday", "Los Angeles", "Peter said he wants to go next Agugust");
		nb.createTextNote("Holiday", "Christmas", "Possible destinations : Home, New York or Rome");
		noteBook = nb;
	}
	
	private void loadNoteBook(File file) {
		NoteBook nb = new NoteBook(file.getName());
		noteBook = nb;
	}
}
