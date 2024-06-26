package edu.sdccd.cisc191;

import edu.sdccd.cisc191.ciphers.*;
import edu.sdccd.cisc191.hashes.MD4;
import edu.sdccd.cisc191.hashes.MD4Engine;
import edu.sdccd.cisc191.hashes.SHA2;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
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
    ComboBox<String> cipherList;
    private static TextArea messageInput;
    private static Stage window;
    private static Scene mainScene;
    private static Scene enigmaScene;
    private static Scene nihilistScene;
    private static String outputText;

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
    public void start(Stage primaryStage){
        window = primaryStage;
        window.setTitle("Decode");

        //list of Ciphers
        cipherList = new ComboBox<>();
        cipherList.getItems().addAll(
                "Hill Cipher",
                "Caesar Cipher",
                "Vigenere Cipher",
                "Atbash Cipher",
                "Affine Cipher",
                "MD4 Hash",
                "Enigma",
                "Morse Code",
                "Phonetic Cipher",
                "Nihilist Cipher",
                "AES Cipher",
                "RSA",
                "RailFence Cipher",
                "SHA2"
        );

        //Get Help Button
        Button help = new Button("Help");
        help.setOnAction(e -> {
            switch (cipherList.getValue()) {
                case "Hill Cipher":
                    AlertBox.display("Hill Cipher", "The key must be a word or series of letters");
                    break;
                case "Caesar Cipher":
                    AlertBox.display("Caesar Cipher", "The key must be a number");
                    break;
                case "Vigenere Cipher":
                    AlertBox.display("Vigenere Cipher", "The key must be a word or series of letters");
                    break;
                case "Atbash Cipher":
                    AlertBox.display("Atbash Cipher", "There is no key :D");
                    break;
                case "Affine Cipher":
                    AlertBox.display("Affine Cipher","They key must be formatted as #,#");
                    break;
                case "Morse Code":
                    AlertBox.display("Morse Code", "There is no key!");
                    break;
                case "Phonetic Cipher":
                    AlertBox.display("Phonetic Cipher", "There is no key!");
                    break;
                case "Nihilist Cipher":
                    AlertBox.display("Nihilist Cipher", "First entry is key for the message\n"+
                            "Second entry is key for Polybius Square" +
                            "\nBoth entries are words");
                    break;
                case "RailFence Cipher":
                    AlertBox.display("RailFence Cipher", "The key must be a number");
                    break;
            }
        }
    );

        //labels
        Label keyLabel = new Label("Enter key:");

        //text input
        TextField key = new TextField();

        //Text Area for message input
        messageInput = new TextArea();
        messageInput.setWrapText(true);

        //Decode/encode button
        Button encode = new Button("Encode");
        encode.setOnAction(e -> Client.encode(messageInput.getText(), key.getText(), cipherList.getValue()));
        Button decode = new Button("Decode");
        decode.setOnAction(e -> Client.decode(messageInput.getText(), key.getText(), cipherList.getValue()));

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
                        messageInput.appendText(scanner.nextLine());
                    }
                }catch (FileNotFoundException f){
                    f.getMessage();
                }
            } else {
                System.out.println("File is not valid");
            }
        });

        //get URL
        TextField link = new TextField();
        Button url = new Button("Get Link");
        url.setOnAction(e -> {
            try {
                String content = getUrl(link.getText());
                messageInput.appendText(content);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        //layout for getting the link
        HBox layout4 = new HBox(10);
        layout4.setAlignment(Pos.CENTER);
        layout4.getChildren().addAll(link, url);

        //layout for key and the cipher list
        HBox layout2 = new HBox(10);
        layout2.getChildren().addAll(key, cipherList, help);
        layout2.setAlignment(Pos.CENTER);

        HBox label = new HBox(10);
        label.getChildren().addAll(keyLabel);
        label.setAlignment(Pos.CENTER);

        cipherList.setOnAction(e -> {
            if(Objects.equals(cipherList.getValue(), "Enigma")){
                enigmaWindow();
            }
            else if(cipherList.getValue() == "Morse Code"){
                label.getChildren().clear();
                layout2.getChildren().clear();
                layout2.getChildren().addAll(cipherList, help);
            }
            else if(cipherList.getValue() == "Phonetic Cipher"){
                label.getChildren().clear();
                layout2.getChildren().clear();
                layout2.getChildren().addAll(cipherList, help);
            }
            else if(Objects.equals(cipherList.getValue(), "Nihilist Cipher")){
                TextField squareKey = new TextField();
                layout2.getChildren().clear();
                layout2.getChildren().addAll(key, squareKey, cipherList, help);
                encode.setOnAction(h -> {
                    outputText = Nihilist.encode(messageInput.getText(), key.getText(), squareKey.getText());
                    outputWindow();
                });
                decode.setOnAction(h -> {
                    outputText = Nihilist.decode(messageInput.getText(), key.getText(), squareKey.getText());
                    outputWindow();
                });
            }
            else if(Objects.equals(cipherList.getValue(), "AES Cipher")){
                HBox comboBoxes = new HBox(10);
                ComboBox<String> aesMode = new ComboBox<>();
                aesMode.getItems().addAll(
                        "ECB",
                        "CTR"
                );
                ComboBox<String> aesBoolean = new ComboBox<>();
                aesBoolean.getItems().addAll(
                        "True",
                        "False"
                );
                layout2.getChildren().clear();
                layout2.getChildren().addAll(key, aesMode, aesBoolean, cipherList, help);
                encode.setOnAction(w -> {
                    boolean flag;
                    flag = Objects.equals(aesBoolean.getValue(), "True");
                    Rijndael rijndael = new Rijndael(messageInput.getText(), key.getText(), aesMode.getValue(), flag);
                    outputText = rijndael.encode();
                    outputWindow();
                });
                decode.setOnAction(w -> {
                    boolean flag;
                    flag = Objects.equals(aesBoolean.getValue(), "True");
                    Rijndael rijndael = new Rijndael(messageInput.getText(), key.getText(), aesMode.getValue(), flag);
                    outputText = rijndael.decode();
                    outputWindow();
                });

            }
            else if(cipherList.getValue() == "RSA"){
                ComboBox<Integer> temp= new ComboBox<>();
                temp.getItems().addAll(
                        1024,
                        2048,
                        4096
                );
                layout2.getChildren().clear();
                layout2.getChildren().addAll(key, temp, cipherList, help);
                encode.setOnAction(h ->{
                    RSA rsa = new RSA(temp.getValue());
                    BigInteger message = new BigInteger(messageInput.getText(),16);
                    outputText = RSA.encode(message, rsa.getEncryptionKey()).toString(16);
                    outputText += "\n\nSecret Primes: \n" + rsa.getSecretPrimes()[0].toString(16) + "\n" + rsa.getSecretPrimes()[1].toString(16);
                    outputWindow();
                });
                decode.setOnAction(h ->{
                    BigInteger message = new BigInteger(messageInput.getText(), 16);
                    String[] primeString = key.getText().split(" ");
                    BigInteger[] primes = new BigInteger[]{new BigInteger(primeString[0], 16), new BigInteger(primeString[1], 16)};

                    RSA rsa = new RSA(primes);
                    outputText = rsa.decode(message).toString(16);
                  outputWindow();
                });
            }
            else if(cipherList.getValue() == "SHA2"){
                label.getChildren().clear();
                layout2.getChildren().clear();
                layout2.getChildren().addAll(cipherList, help);
                encode.setOnAction(h -> {
                    SHA2 sha2 = new SHA2(messageInput.getText());
                    outputText = sha2.runHash();
                    outputWindow();
                });
            }
            else{
                label.getChildren().clear();
                label.getChildren().add(keyLabel);
                layout2.getChildren().clear();
                layout2.getChildren().addAll(key, cipherList, help);
                encode.setOnAction(enc -> Client.encode(messageInput.getText(), key.getText(), cipherList.getValue()));
                decode.setOnAction(enc -> Client.decode(messageInput.getText(), key.getText(), cipherList.getValue()));
            }
//            switch(cipherList.getValue()){
//                case "Enigma":
//                    enigmaWindow();
//                    break;
//                case "Nihilist Cipher":
//                    TextField squareKey = new TextField();
//                    layout2.getChildren().clear();
//                    layout2.getChildren().addAll(key, squareKey, cipherList, help);
//                    encode.setOnAction(h -> {
//                        outputText = Nihilist.encode(messageInput.getText(), key.getText(), squareKey.getText());
//                        outputWindow();
//                    });
//            }
        });


        //layout for encode and decode buttons
        HBox layout3 = new HBox(10);
        layout3.setAlignment(Pos.CENTER);
        layout3.getChildren().addAll(encode, decode);
        //final layout for all layouts
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(label, layout2, layout3, messageInput, files, layout4);



        //shows the scene
        mainScene = new Scene(layout, 800,600);
        window.setScene(mainScene);
        window.show();
    }
    /**
     * Encodes the message based on the cipher selected
     * @param inputText the input message
     * @param key the key to the cipher
     * @param cipherType the type of cipher selected
     */
    public static void encode(String inputText, String key, String cipherType) {
        switch(cipherType) {
            case "Hill Cipher":
                outputText = Hill.encode(inputText, key);
                outputWindow();
                break;
            case "Caesar Cipher":
                try {
                    outputText = Caesar.encode(inputText, key);
                    outputWindow();
                } catch (NumberFormatException e) {
                    AlertBox.display("Error", "ERROR!\nThe key must be a number");
                }
                break;
            case "Vigenere Cipher":
                outputText = Vigenere.encode(inputText, key);
                outputWindow();
                break;
            case "Atbash Cipher":
                outputText = Atbash.encrypt(inputText);
                outputWindow();
                break;
            case "Affine Cipher":
                try{
                    outputText = Affine.encode(inputText, key);
                    outputWindow();
                    break;
                } catch (Exception e) {
                    AlertBox.display("Error", "ERROR!\nInput must be #,#\nThe first number must not be even or a multiple of 13");
                }
                break;
            case "MD4 Hash":
                if(key.equalsIgnoreCase("LIST")) {
                    String[] list = inputText.split("\n");
                    StringBuilder output = new StringBuilder();
                    for(String str : list) {
//                        output.append(MD4.hashAsString(str) + "\n");
                    }

                    outputText = output.toString();
                } else {
//                    outputText = MD4.hashAsString(inputText);
                }
                outputWindow();
                break;
            case "Morse Code":
                outputText = MorseCode.engToMor(inputText);
                outputWindow();
                break;
            case "Phonetic Cipher":
                outputText = Phonetic.printPhoneticEncoded(inputText);
                outputWindow();
                break;
            case "RailFence Cipher":
                try {
                    outputText = RailFence.encode(inputText, key);
                    outputWindow();
                } catch (NumberFormatException e) {
                    AlertBox.display("Error", "ERROR!\nThe key must be a number");
                }
                break;
        }
    }
    /**
     * Decodes the message based off the cipher selected
     * @param inputText the input message
     * @param key the key to the cipher
     * @param cipherType the type of cipher selected
     */
    public static void decode(String inputText, String key, String cipherType){
        switch (cipherType) {
            case "Hill Cipher":
                outputText = Hill.decode(inputText, key);
                outputWindow();
                break;
            case "Caesar Cipher":
                try {
                    outputText = Caesar.decode(inputText, key);
                    outputWindow();
                } catch (NumberFormatException e) {
                    AlertBox.display("Error", "ERROR!\nThe key must be a number");
                }
                break;
            case "Vigenere Cipher":
                outputText = Vigenere.decode(inputText, key);
                outputWindow();
                break;
            case "Atbash Cipher":
                outputText = Atbash.decrypt(inputText);
                outputWindow();
                break;
            case "Affine Cipher":
                try{
                    outputText = Affine.decode(inputText, key);
                    outputWindow();
                    break;
                } catch(Exception e){
                    AlertBox.display("Error", "ERROR!\nInput must be #,#\nThe first number must not be even or a multiple of 13");
                }
                break;
            case "MD4 Hash":
                String[] list = inputText.split("\n");
                int numThreads = 8; //TODO Get Number of Threads
                HashMap<Character, char[]> formatMap = new HashMap<>(); //TODO: Get this as user input
                formatMap.put('a', new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'});
                formatMap.put('A', new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'});
                formatMap.put('0', new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'});

                MD4Engine md4Engine = new MD4Engine(list, formatMap, key);
                md4Engine.runMD4Crack();

//                HashMap<String, String> crackedPasswords = md4Engine.getCrackedPasswords();
                StringBuilder output = new StringBuilder();
                for(String str : list) {
//                    output.append(str).append(" --> ").append(crackedPasswords.get(str)).append("\n");
                }
                outputText = output.toString();
                outputWindow();
                break;
            case "Morse Code":
                outputText = MorseCode.morToEng(inputText);
                outputWindow();
                break;
            case "Phonetic Cipher":
                outputText = Phonetic.printPhoneticDecoded(inputText);
                outputWindow();
            case "RailFence Cipher":
                try {
                    outputText = RailFence.decode(inputText, key);
                    outputWindow();
                } catch (NumberFormatException e) {
                    AlertBox.display("Error", "ERROR!\nThe key must be a number");
                }
                break;
        }
    }
    /**
     * Displays output window
     * Back button will switch back to mainScene
     */
    public static void outputWindow() {
        createSecondWindow(mainScene);
    }

    /**
     * Displays output window
     * @param scene the scene the back button will go to
     */
    private static void createSecondWindow(Scene scene) {
        Label result = new Label("Result:"); //re
        TextArea output = new TextArea(outputText);
        output.setWrapText(true);
        output.setPrefSize(300,200);

        //back button to change scene to previous scene
        Button back = new Button("Back");
        back.setOnAction(e -> window.setScene(scene));

        //Calls upon getOutputFile to download output file
        Button file = new Button("Get File");
        file.setOnAction(e -> {
            try {
                getOutputFile();
                AlertBox.display("File Saved", "Your output file has been saved");
            } catch (FileNotFoundException ex) {
                AlertBox.display("Error", "Error!\n No File Found");
            }
        });

        //layout for the back button and Get File button
        HBox view = new HBox(10);
        view.setAlignment(Pos.CENTER);
        view.getChildren().addAll(back, file);

        //final layout for all layouts in scene
        VBox layout4 = new VBox(10);
        layout4.setPadding(new Insets(50,50,50,50));
        layout4.setAlignment(Pos.CENTER);
        layout4.getChildren().addAll(result, output, view);
        Scene outputScene = new Scene(layout4, 800, 600);
        window.setScene(outputScene);
    }
    /**
     * Displays output window
     * Back button will switch back to enigmaScene
     */
    public static void enigmaOutputWindow(){
        createSecondWindow(enigmaScene);
    }
    /**
     * Download outputText using FileChooser
     */
    public static void getOutputFile() throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(window);

        if (file != null) {
            PrintWriter pw = new PrintWriter(file);
            pw.println(outputText);
            pw.close();
        }
    }

    /**
     * Creates a window for enigma cipher
     */
    public static void enigmaWindow(){
        HBox layout2 = new HBox(20);
        layout2.setAlignment(Pos.CENTER);

        //reflector label & reflector comboBox
        Label reflectorLabel = new Label("Reflector:");
        ComboBox<String> reflector = new ComboBox<>();
        reflector.getItems().addAll(
                "UKW B",
                "UKW C"
        );

        //help button
        Button help = new Button("Help");
        help.setOnAction(e -> {
            AlertBox.display("Help", "Position and Ring must be a number from 1-26" +
                    "\nPlugboard must be pairs of unique letters (can't repeat the same letter)");
        });
        //layout for reflector label & combobox and help button
        layout2.getChildren().addAll(reflectorLabel, reflector, help);

        //rotor + positon + ring label
        Label rotorLabel = new Label("Rotor 1:");
        Label positionLabel = new Label("Position:");
        Label ringLabel = new Label("Ring:");

        //Combobox for all rotor options
        ComboBox<String> rotor = new ComboBox<>();
        rotor.getItems().addAll(
                "I",
                "II",
                "III",
                "IV",
                "V"
        );

        //TextFields for position and ring input
        TextField positionInput = new TextField();
        TextField ringInput = new TextField();

        //layout for first rotor, position, and ring inputs
        HBox layout4 = new HBox(50);
        layout4.setAlignment(Pos.CENTER);
        layout4.getChildren().addAll(rotorLabel, rotor, positionLabel, positionInput, ringLabel, ringInput);

        //rotor + positon + ring label
        Label rotorLabel2 = new Label("Rotor 2:");
        Label positionLabel2 = new Label("Position:");
        Label ringLabel2 = new Label("Ring:");

        //Combobox for all rotor options
        ComboBox<String> rotor2 = new ComboBox<>();
        rotor2.getItems().addAll(
                "I",
                "II",
                "III",
                "IV",
                "V"
        );

        //TextFields for position and ring input
        TextField positionInput2 = new TextField();
        TextField ringInput2 = new TextField();

        //layout for second rotor, position, and ring inputs
        HBox layout5 = new HBox(50);
        layout5.setAlignment(Pos.CENTER);
        layout5.getChildren().addAll(rotorLabel2, rotor2, positionLabel2, positionInput2, ringLabel2, ringInput2);

        //rotor + positon + ring label
        Label rotorLabel3 = new Label("Rotor 3:");
        Label positionLabel3 = new Label("Position:");
        Label ringLabel3 = new Label("Ring:");

        //Combobox for all rotor options
        ComboBox<String> rotor3 = new ComboBox<>();
        rotor3.getItems().addAll(
                "I",
                "II",
                "III",
                "IV",
                "V"
        );

        //TextFields for position and ring input
        TextField positionInput3 = new TextField();
        TextField ringInput3 = new TextField();

        //layout for third rotor, position, and ring inputs
        HBox layout6 = new HBox(50);
        layout6.setAlignment(Pos.CENTER);
        layout6.getChildren().addAll(rotorLabel3, rotor3, positionLabel3, positionInput3, ringLabel3, ringInput3);

        //plugboard TextField Input
        Label plugboard = new Label("Plugboard");
        TextField plugboardInput = new TextField();

        //layout for plugboard input and label
        HBox layout8 = new HBox(20);
        layout8.setAlignment(Pos.CENTER);
        layout8.getChildren().addAll(plugboard, plugboardInput);

        //TextArea for input message
        TextArea inputText = new TextArea();
        inputText.setWrapText(true);

        //TextField for user to input link
        TextField link = new TextField();
        Button url = new Button("Get Link");
        url.setOnAction(e -> {
            try {
                String content = getUrl(link.getText());
                messageInput.appendText(content);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        //layout for getting link
        HBox layout9 = new HBox(10);
        layout9.setAlignment(Pos.CENTER);
        layout9.getChildren().addAll(link, url);

        //buttons for back to mainScene, encode, cryptanalyze
        Button back = new Button("Back");
        back.setOnAction(e -> window.setScene(mainScene));
        Button encode = new Button("Encode");
        encode.setOnAction(e -> {
        Enigma enigma = new Enigma(new int[]{CipherTools.romanToInteger(rotor.getValue()),Integer.parseInt(positionInput.getText()),Integer.parseInt(ringInput.getText())},
                    new int[]{CipherTools.romanToInteger(rotor2.getValue()),Integer.parseInt(positionInput2.getText()),Integer.parseInt(ringInput2.getText())},
                    new int[]{CipherTools.romanToInteger(rotor3.getValue()),Integer.parseInt(positionInput3.getText()),Integer.parseInt(ringInput3.getText())}, reflector.getValue(), plugboardInput.getText());
            outputText = enigma.encode(inputText.getText().toUpperCase().replaceAll("[^A-Z]", ""));
            enigmaOutputWindow();
        });
        Button decode = new Button("Cryptanalyze");
        decode.setOnAction(e -> {
            EnigmaEngine engine = new EnigmaEngine(inputText.getText().toUpperCase().replaceAll("[^A-Z]", ""));
            outputText = engine.cryptanalyze();
            enigmaOutputWindow();
        });

        //layout for encode, cryptanalyze, back button
        HBox layout7 = new HBox(10);
        layout7.setAlignment(Pos.CENTER);
        layout7.getChildren().addAll(encode, decode, back);

        //final layout for all layouts in scene
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(layout2, layout4, layout5, layout6, layout8, inputText, layout9, layout7 );

        //shows scene
        enigmaScene = new Scene(layout, 800, 600);
        window.setScene(enigmaScene);
    }

//    public static void nihilistScene(){
//
//    }

    /**************************************************************************
     * Gets URL of website
     * @param input URL inputted by user
     * @return Text from the URL
     *************************************************************************/
    public static String getUrl(String input){
        try {
            Document document = Jsoup.connect(input).get();
            return document.text();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
} //end class Client