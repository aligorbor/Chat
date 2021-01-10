package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;

public class Controller {

    @FXML
    private TextField inputField;

    @FXML
    private ListView<String> listView;

    @FXML
    private ListView<String> listPersons;

    @FXML
    private ChoiceBox<String> choicePersonAccount;

    @FXML
    private Label labelPersonChat;

    MultipleSelectionModel<String> personsSelectionModel;

    private final ObservableList<String> personList = FXCollections.observableArrayList
            ("Иван", "Петр", "Александр", "Роман", "Денис");
    private final ArrayList<ObservableList<String>> arrChat = new ArrayList<>();

    @FXML
    void initialize() {
        personsSelectionModel = listPersons.getSelectionModel();
        personsSelectionModel.selectedItemProperty().addListener((changed, oldValue, newValue) -> {
            labelPersonChat.setText(newValue);
            int index = personsSelectionModel.getSelectedIndex();
            listView.setItems(arrChat.get(index));
        });

        listPersons.setItems(personList);
        while (arrChat.size() < personList.size())
            arrChat.add(FXCollections.observableArrayList());
        choicePersonAccount.setItems(personList);
        choicePersonAccount.setValue(choicePersonAccount.getItems().get(0));
        personsSelectionModel.select(0);
    }

    @FXML
    void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isBlank()) {
            message = choicePersonAccount.getValue() + ": " + message;
            addMessageToList(message);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText("Ошибка ввода сообщения");
            alert.setContentText("Нельзя отправлять пустое сообщение");
            alert.show();
        }
        inputField.clear();
    }

    private void addMessageToList(String message) {
        listView.getItems().add(message);
    }

}
