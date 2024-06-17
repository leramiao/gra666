package com.example.demo1;

import javafx.stage.Stage;
import logic.*;

public class SceneController {

    private static Stage primaryStage;
    public static SeansManager seansManager;

    public static Theme theme;
    public static String activeUsername;

    public SceneController(Stage stage) {
        primaryStage = stage;
        theme = Theme.HEAVEN;
        seansManager = new SeansManager();
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
    public static void openSettingsView()
    {
        SettingsView settingsView = new SettingsView();
        settingsView.display();

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
        CreateTableView createTableView = new CreateTableView(seansManager);
        createTableView.display();
    }
    public static void openTableView(Table table){
        SeansView view = new SeansView(primaryStage, table.getId(), theme);
        view.display();
    }

    public static void clearTables(){
        seansManager.getSeanse().clear();
    }


}
