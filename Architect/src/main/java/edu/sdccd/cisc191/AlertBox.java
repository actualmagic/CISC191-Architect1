package edu.sdccd.cisc191;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * AlertBox class
 * Displays window for errors
 */
public class AlertBox {
    /**
     * displays error window
     * @param title title of the window
     * @param msg the error message
     */
    public static void display(String title, String msg){
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);

        Label label = new Label(msg);
        label.setWrapText(true);
        label.setTextAlignment(TextAlignment.CENTER);
        Button closebutton = new Button("Ok");
        closebutton.setOnAction(e -> window.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, closebutton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20,20,20,20));

        Scene scene = new Scene(layout,400, 250);
        window.setScene(scene);
        window.showAndWait();
    }
}
