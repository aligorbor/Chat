package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.ArrayList;

public class Controller {

    @FXML
    private TextField inputField;

    @FXML
    private Button sendButton;

    @FXML
    private ListView<String> listView;

    @FXML
    private ListView<String> listPersons;

    @FXML
    private ChoiceBox<String> choicePersonAccount;

    @FXML
    private Label labelPersonChat;

    @FXML
    private TextField passwField;

    @FXML
    private Button authButton;

    private MultipleSelectionModel<String> personsSelectionModel;

    private ObservableList<String> personList = FXCollections.observableArrayList
            ("Общий чат");
    private final ArrayList<ObservableList<String>> arrChat = new ArrayList<>();

    private ObservableList<String> loginList = FXCollections.observableArrayList();

    private Network network;
    private String loginNick;

    public void setNetwork(Network network) {
        this.network = network;
    }

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

        choicePersonAccount.setItems(loginList);
        //   choicePersonAccount.setValue(choicePersonAccount.getItems().get(0));
        choicePersonAccount.setOnAction(actionEvent -> {
            // System.out.println(choicePersonAccount.getValue());
            choicePersonAccount.setDisable(false);
            passwField.setDisable(false);
            authButton.setDisable(false);
            inputField.setDisable(true);
            sendButton.setDisable(true);
        });

        personsSelectionModel.select(0);
    }

    @FXML
    void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isBlank()) {
            int index = personsSelectionModel.getSelectedIndex();
            if (index > 0) {
                message = Network.CMD_PREF_INDIVID + " " + personList.get(index) + ": " + message;
            }
            try {
                network.getOut().writeUTF(message);
            } catch (IOException e) {
                System.out.println("Ошибка при отправке сообщения");
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText("Ошибка ввода сообщения");
            alert.setContentText("Нельзя отправлять пустое сообщение");
            alert.show();
        }
        inputField.clear();

    }

    public void setLoginNick(String loginNick) {
        this.loginNick = loginNick;
    }

    public void appendMessage(String message) {
        if (message.startsWith(Network.CMD_PREF_AUTHOK) || message.startsWith(Network.CMD_PREF_NICK)) {
            message = addNick(message);
        }
        if (message.startsWith(Network.CMD_PREF_INDIVID)) {
            String namePerson;
            if (message.startsWith(Network.CMD_PREF_INDIVIDBACK)) {
                namePerson = message.substring(Network.CMD_PREF_INDIVIDBACK.length() + 1, message.indexOf(":"));
                message = message.substring(Network.CMD_PREF_INDIVIDBACK.length() + 1);
                message = message.replaceFirst(namePerson, loginNick);
            } else {
                namePerson = message.substring(Network.CMD_PREF_INDIVID.length() + 1, message.indexOf(":"));
                message = message.substring(Network.CMD_PREF_INDIVID.length() + 1);
            }
            personsSelectionModel.select(personList.indexOf(namePerson));
        } else {
            personsSelectionModel.select(0);
        }
        if (!message.isBlank())
            listView.getItems().add(message);
    }

    @FXML
    void onAuthClick() {
        network.startAuthorization();
        try {
            network.getOut().writeUTF(Network.CMD_PREF_AUTH + " " + choicePersonAccount.getValue() + " " + passwField.getText());
            passwField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addLoginList(String login) {
        loginList.add(login);
    }

    public void clearLoginList() {
        loginList.removeAll();
    }

    public String addNick(String strNick) {
        String[] parts = strNick.split("\\s");
        if (parts[0].equals(Network.CMD_PREF_AUTHOK)) {
            loginNick = parts[1];
            choicePersonAccount.setDisable(true);
            passwField.setDisable(true);
            authButton.setDisable(true);
            inputField.setDisable(false);
            sendButton.setDisable(false);
            return loginNick + " зашел в чат";
        } else if ((parts[0].equals(Network.CMD_PREF_NICK) || parts[0].equals(Network.CMD_PREF_NICKLIST)) && !parts[1].equals(loginNick)) {
            personList.add(parts[1]);
            arrChat.add(FXCollections.observableArrayList());
            if (parts[0].equals(Network.CMD_PREF_NICK))
                return parts[1] + " зашел в чат";
        } else if (parts[0].equals(Network.CMD_PREF_NICKEND) && !parts[1].equals(loginNick)) {
            arrChat.remove(personList.indexOf(parts[1]));
            personList.remove(parts[1]);
            return parts[1] + " вышел из чата";
        }
        return "";
    }


}
