package com.example.demo1;

import javafx.stage.Stage;
import logic.Table;
import logic.TableManager;
import logic.User;
import logic.UserManager;

public class SceneController {

    private static Stage primaryStage;
    public static TableManager tableManager;

    public static String activeUsername;

    public SceneController(Stage stage) {
        primaryStage = stage;
        tableManager = new TableManager();
    }


    public static void openLoginView() {
        // Otworzenie formularza login
        LoginView loginView = new LoginView();
        loginView.display();

    }
    public static void openRegisterForm()
    {
        RegisterForm registrationForm = new RegisterForm();
        registrationForm.display();

    }
    public static void openMenuView()
    {
        MainMenuView menuView = new MainMenuView();
        menuView.display();
    }

    public static void openLoungeView(){
        LoungeView loungeView = new LoungeView();
        loungeView.display();
    }

    public static void openCreateTableView(){
        CreateTableView createTableView = new CreateTableView(tableManager);
        createTableView.display();
    }
    public static void openTableView(Table table){
        SeansView view = new SeansView(primaryStage, table.getId());
        view.display();
    }

    public static void clearTables(){
        tableManager.getTables().clear();
    }


}
