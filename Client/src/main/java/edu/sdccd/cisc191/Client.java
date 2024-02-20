package edu.sdccd.cisc191;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.net.*;
import java.io.*;
import java.util.Scanner;


/**
 * This program opens a connection to a computer specified
 * as the first command-line argument.  If no command-line
 * argument is given, it prompts the user for a computer
 * to connect to.  The connection is made to
 * the port specified by LISTENING_PORT.  The program reads one
 * line of text from the connection and then closes the
 * connection.  It displays the text that it read on
 * standard output.  This program is meant to be used with
 * the server program, DateServer, which sends the current
 * date and time on the computer where the server is running.
 */

public class Client extends Application{
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    ComboBox<String> combobox;
    private TextArea textArea;
    
    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public CustomerResponse sendRequest() throws Exception {
        out.println(CustomerRequest.toJSON(new CustomerRequest(1)));
        return CustomerResponse.fromJSON(in.readLine());
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.startConnection("127.0.0.1", 4444);
            System.out.println(client.sendRequest().toString());
            System.out.println(client.sendRequest().getId());
            client.stopConnection();
        } catch(Exception e) {
            e.printStackTrace();
        }
        launch(args);
    }
        @Override
        public void start(Stage primaryStage) throws Exception {
            primaryStage.setTitle("Decode");

            //list of Ciphers
            combobox = new ComboBox<>();
            combobox.getItems().addAll(
                    "Cipher 1",
                    "Cipher 2"
            );
            //listen for selection changes
            combobox.setOnAction(e -> System.out.println(combobox.getValue()));

            //labels
            Label label = new Label("Enter key:");

            //text input
            TextField input = new TextField();

            //Text Area
            textArea = new TextArea();

            //Decode/encode button
            Button button = new Button("Decode");
            button.setOnAction(e -> System.out.println(textArea.getText()));
            Button encode = new Button("Encode");
            encode.setOnAction(e -> System.out.println("it encodes!!!!!!"));

            //Import File Button
            Button files = new Button("Select File");
            files.setOnAction(e -> {
                FileChooser fc = new FileChooser();
                fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT files", "*.TXT"),
                        new FileChooser.ExtensionFilter("txt files", ".txt"));
                File selectedFile = fc.showOpenDialog(null);
                if (selectedFile != null) {
                    try{
                        Scanner scanner = new Scanner(selectedFile);
                        while(scanner.hasNextLine()){
                            textArea.appendText(scanner.nextLine());
                        }
                    }catch (FileNotFoundException f){
                        f.getMessage();
                    }
                } else {
                    System.out.println("File is not valid");
                }
            });


            //layout
            HBox layout2 = new HBox(10);
            layout2.getChildren().addAll(input, combobox);
            layout2.setAlignment(Pos.CENTER);
            HBox layout3 = new HBox(10);
            layout3.setAlignment(Pos.CENTER);
            layout3.getChildren().addAll(button, encode);
            VBox layout = new VBox(10);
            layout.setPadding(new Insets(20, 20, 20, 20));
            layout.setAlignment(Pos.CENTER);
            layout.getChildren().addAll(label, layout2, layout3, textArea, files);

            Scene scene = new Scene(layout, 800,600);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    } //end class Client

