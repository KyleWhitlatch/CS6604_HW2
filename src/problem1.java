/**
 * Created by Miner on 1/30/2020.
 */

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.input.*;

import java.util.ArrayList;
import java.util.Queue;
import javafx.*;

public class SceneBuild extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialize label
        Label mylabel = new Label();
        mylabel.setText("Select Problem:");
        // Initialize buttons and button events, create three separate buttons for the three problems
        Button p1button = new Button();
        Button p2button = new Button();
        Button p3button = new Button();
        p1button.setText("Partition-based Scheme");
        p2button.setText("Working Set Location Scheme");
        p3button.setText("Forwarding Pointers");

        p1button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                p1solution();
            }
        });

        p2button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                p2solution();
            }
        });

        p3button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                p3solution();
            }
        });


        // Setup gridpane with children for main scene
        GridPane gridPane = new GridPane();
        gridPane.setMinSize(400,200);
        gridPane.setPadding(new Insets(10,10,10,10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.add(mylabel,0,0);
        gridPane.add(p1button,0,1);
        gridPane.add(p2button,0,2);
        gridPane.add(p3button,0,3);

        // Setup scene and window parameters
        Scene s = new Scene(gridPane);
        primaryStage.setTitle("Homework #1");
        primaryStage.setScene(s);
        primaryStage.show();
    }

    // BEGIN SECONDARY FUNCTIONS

    // Problem 1: Create partition scheme.
    public void p1solution(){
        int globalPosX = 900;
        int globalPosY = 250;

        Stage p1stage = new Stage();

        // Create user input gridpane with customization buttons.
        Label inLabel = new Label("User Inputs");
        Label xLabel = new Label("X Position (No overlap)");
        Label callLabel = new Label("Select Caller");
        TextField xPosField = new TextField("3");
        TextField calleeField = new TextField("A");

        GridPane mygrid = new GridPane();

        mygrid.add(inLabel,0,0);
        mygrid.add(xLabel,0,1);
        mygrid.add(xPosField,0,2);
        mygrid.add(callLabel,0,3);
        mygrid.add(calleeField,0,4);
        mygrid.setAlignment(Pos.TOP_LEFT);

        // Setup the tree structure.
        Group p1Group = new Group();
        partTree p1Tree = new partTree(p1Group,globalPosX,globalPosY);
        partNode nodeArray[] = new partNode[50];

        // Setup tree creation vars, these can be changed depending on the type of tree.

        int numMain = 3;
        int numSub = 2;
        int numLeaves = 3;
        int index = 1;
        String[] branchNames = {"Left","Right","Middle"};
        nodeArray[0] = p1Tree.addNode("root", null, 0, 0);
        partNode rootNode = nodeArray[0];
        partNode mainParent;
        partNode subParent;

        // Create Tree node hierarchy based on tree creation vars.

        for(int i = 0; i < numMain; i++){

            nodeArray[index] = p1Tree.addNode(branchNames[i], nodeArray[0], 1, index);
            mainParent = nodeArray[index];
            index++;

            for(int j = 0; j < numSub; j++){

                nodeArray[index] = p1Tree.addNode(branchNames[j],mainParent, 2, index);
                subParent = nodeArray[index];
                index++;

                for(int k = 0; k < numLeaves; k++){

                    nodeArray[index] = p1Tree.addNode(branchNames[k],subParent, 3, index);
                    index++;

                }

            }

        }

        // Update isLeaf vars in leaf nodes.
        p1Tree.assignLeaves(rootNode);

        // Assign Representatives for Partitions in certain LCA nodes

        int numReps = 4;
        partRep treeReps[] = new partRep[numReps];

        treeReps[0] = new partRep(rootNode.leftChild);
        treeReps[1] = new partRep(rootNode.rightChild);
        treeReps[2] = new partRep(rootNode.middleChild.leftChild);
        treeReps[3] = new partRep(rootNode.middleChild.rightChild);

        // Setup caller and callees and event handling for user inputs
        int startPos = 3;
        partCaller userCaller = new partCaller(p1Tree.getNodebyNum(rootNode, startPos),"X",p1Group);

        // Setup callee node positions
        String calleeIDs[] = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        int calleeCells[] = {4, 7, 8, 21, 23, 26, 14, 13, 18, 17};
        int numCallees = calleeIDs.length;
        partCaller Callees[] = new partCaller[numCallees];

        for(int i = 0; i < numCallees; i++){
            Callees[i] = new partCaller(p1Tree.getNodebyNum(rootNode,calleeCells[i]), calleeIDs[i], p1Group);
        }

        // Setup memory for representatives

        for(int i = 0; i< treeReps.length; i++) {
            treeReps[i].getLeafCallees(treeReps[i].repNode);
        }

        // Update caller node position on user update.
        xPosField.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)) {
                    // Get new caller position.
                    String moveCell = xPosField.getText();
                    // Get the node belonging to new caller position.
                    partNode newCell = p1Tree.getNodebyNum(rootNode,Integer.parseInt(moveCell));

                    // Check that the selected node is a leaf node and doesn't have another caller.
                    if(newCell.isLeaf && !newCell.hasCaller) {
                        // Update caller position in old cell and at old rep.
                        userCaller.callerNode.hasCaller = false;
                        userCaller.callerNode.nodeRep.removeCallee(userCaller);
                        // Update caller position in new cell and at new rep.
                        userCaller.setCallerNode(newCell);
                        userCaller.setUserCellPos(userCaller.callerNode.nodeNum);
                        userCaller.updateCallerText();
                        userCaller.callerNode.nodeRep.addCallee(userCaller);
                    }

                }
            }
        });

        // Make call to callee on user update.
        calleeField.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)) {
                    // Get callee ID
                    int calleeIdx = 0;
                    String newCalleeID = calleeField.getText();
                    // Check which callee is being called.
                    for(int i = 0; i < Callees.length; i++){

                        if(Callees[i].callerID.equals(newCalleeID)){
                            calleeIdx = i;
                            break;
                        }

                    }

                    // Clear previous drawings if any, and search for Callee.
                    p1Tree.clearLines(rootNode);
                    p1Tree.resetLeaves(rootNode);
                    p1Tree.searchCallees(userCaller, Callees[calleeIdx]);

                }
            }
        });

        // Get Shapes and text and draw on window.
        p1Tree.inOrderAddLines(p1Tree.root, p1Group);
        p1Tree.inOrderGetNodeShapes(p1Tree.root, p1Group);
        p1Tree.inOrderGetNodeText(p1Tree.root, p1Group);

        // Update and display scene.
        p1Group.getChildren().add(mygrid);
        Scene mys = new Scene(p1Group, globalPosX , globalPosY);
        p1stage.setScene(mys);
        p1stage.setTitle("Problem #1 Solution");
        p1stage.show();

    }

    // Problem 2: Create working set scheme.
    public void p2solution(){
        Stage p2stage = new Stage();
        int globalPosX = 900;
        int globalPosY = 250;

        // Create user input gridpane with customization buttons.
        Label inLabel = new Label("User Inputs");
        Label histLabel = new Label("Select # of towers to hold information");
        Label locLabel = new Label("Select Location");
        TextField histField = new TextField("3");
        TextField locField = new TextField("A");
        ArrayList<String> history = new ArrayList<>();


        GridPane mygrid = new GridPane();
        Circle nodeArray[] = new Circle[10];
        Label hasInfo[] = new Label[10];
        Label nodeLabel[] = new Label[10];

        mygrid.add(inLabel,0,0);
        mygrid.add(histLabel,0,1);
        mygrid.add(locLabel,0,3);
        mygrid.add(histField,0,2);
        mygrid.add(locField,0,4);

        for(int x = 0; x <  nodeArray.length; x++){
            hasInfo[x] = new Label("X");
            nodeArray[x] = new Circle(20);
            nodeLabel[x] = new Label(""+(char)(65+x)); //don't worry about it
            mygrid.add(hasInfo[x],x+2,2);
            mygrid.add(nodeArray[x], x+2,3);
            mygrid.add(nodeLabel[x],x+2,4);
        }
        histField.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                System.out.println("Handling the history field" + history.toString());
                if(event.getCode().equals(KeyCode.ENTER)) {
                    System.out.println(""+history.size()+" "+Integer.parseInt(histField.getText()));
                    System.out.println((history.size() > Integer.parseInt(histField.getText())));
                    if(history.size() > Integer.parseInt(histField.getText())){
                        for(int x = history.size()-1; x >= Integer.parseInt(histField.getText());x--) {
                            System.out.println(history.get(x));
                            history.remove(x);
                        }
                        //history.trimToSize();
                        System.out.println(history.size());
                        System.out.println(history.toString());
                    }

                }
            }



        });
        locField.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)) {

                    //just don't worry about this indexing, I promise it works

                    history.add(0, locField.getText());
                    if (history.size() > Integer.parseInt(histField.getText())) {
                        for (int x = history.size() - 1; x >= Integer.parseInt(histField.getText()); x--)
                            history.remove(x);
                        //history.trimToSize();
                        updateHistory(history, hasInfo);
                        updateLocation(locField.getText(), nodeLabel);


                    }
                }
            }


        });







        Scene s = new Scene(mygrid,globalPosX,globalPosY);
        p2stage.setScene(s);
        p2stage.setTitle("Problem 2 - Working Sets");
        p2stage.show();

    }
    public void updateHistory(ArrayList<String> h, Label[] n){
        char c;
        for(Label l : n){
            l.setText("X");
        }
        for(String s : h){ //welcome to Java 8
            c = s.charAt(0);
            n[c-65].setText("✓"); //hope you can read ASCII
        }
    }
    public void updateLocation(String l, Label[] n){
        for(int x = 0; x < n.length; x++){
//            System.out.println(""+(char)(65+x));
            n[x].setText((char)(65+x)+""); //More ASCII hell
        }
        for(Label s : n){
            if(s.getText().equalsIgnoreCase(l)){
                s.setText(s.getText()+"*");
            }
        }
    }

    // Problem 3: Create dynamic tree-structure scheme with pointers.

    // Prob 3 "Global" vars
    int numLevels;
    boolean treeCreated;
    pointTree p3Tree;
    pointNode nodeArray[];
    pointNode rootNode;
    pointCaller xCaller;
    pointCaller[] Callees;
    pointCaller newCaller;
    public void p3solution(){

        int globalPosX = 900;
        int globalPosY = 600;

        Stage p3stage = new Stage();

        // Create user input gridpane with customization buttons.
        Label treeLabel = new Label("Tree Customization");
        Label treelvlLabel = new Label("Select # of Tree Levels (Best: 2-5)");
        TextField treelvltext = new TextField("3");
        Button createTreeButton = new Button("Create!");
        Label inLabel = new Label("User Inputs");
        Label xLabel = new Label("X Position (No overlap)");
        Label callLabel = new Label("Select Caller");
        Label cmrLabel = new Label("Init. X CMR");
        TextField xPosField = new TextField("0");
        TextField xcmrin = new TextField("5");
        TextField calleeField = new TextField("1");

        GridPane mygrid = new GridPane();

        // Setup grid.
        mygrid.add(treeLabel,0,0);
        mygrid.add(treelvltext,0,1); mygrid.add(treelvlLabel, 1, 1);
        mygrid.add(createTreeButton,0,2);
        mygrid.add(inLabel, 0,3);
        mygrid.add(xPosField,0,4); mygrid.add(xLabel,1,4);
        mygrid.add(xcmrin,0,5); mygrid.add(cmrLabel,1,5);
        mygrid.add(calleeField,0,6); mygrid.add(callLabel,1,6);
        mygrid.setAlignment(Pos.TOP_LEFT);

        // Setup the tree structure.
        Group p3Group = new Group();
        p3Tree = new pointTree(p3Group,globalPosX,globalPosY,1);
        nodeArray = new pointNode[1000];
        treeCreated = false;

        // Declare objects shared between button calls.
        nodeArray[0] = p3Tree.addNode("root", null, 0, 0);
        rootNode = nodeArray[0];
        rootNode.setTreeLevel(0);
        numLevels = 3;

        // Get tree levels from user.
        treelvltext.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)) {

                    // Get user defined tree # levels.
                    String numlvlsString = treelvltext.getText();
                    numLevels = Integer.parseInt(numlvlsString);

                }
            }
        });

        // Handles tree creation on user command.
        createTreeButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent actionEvent) {

                // Create Tree node hierarchy based on tree creation vars.

                // Check if tree already created.
                if(treeCreated)
                    return;

                // Dont make too many levels and overflow nodeArray.
                if(numLevels > 8) // Max levels
                    return;

                treeCreated = true;
                p3Tree.createTree(rootNode, numLevels,nodeArray);

                // Find number of callees (0.5 * numleaves) and generate callees with random CMR

                int numleaves = 3*(int)Math.pow((double)(numLevels-1),2);
                int numCallees = (numleaves/2) + 1 ; // plus 1 for X

                Callees = new pointCaller[numCallees + 1];
                String calleeIDs[] = new String[numCallees + 1];
                ArrayList<pointNode> leafList = new ArrayList<pointNode>();

                for(int i = 0; i < (numCallees+1); i++) {
                    calleeIDs[i] = "" + i;
                    if(i == numCallees)
                        calleeIDs[i] = "X";
                }

                // Get array of leaf nodes.
                for(int i = 0; i < nodeArray.length; i++){

                    if(nodeArray[i] == null)
                        break;

                    if(nodeArray[i].isLeaf) {
                        leafList.add(nodeArray[i]);
                    }

                }

                // Initialize other callees and setup caller X
                xCaller = p3Tree.initRandCallees(leafList, Callees, calleeIDs, p3Group);
                xCaller.pointCallerNode.hasCallerX = true;
                xCaller.CMR = 5; // Initial CMR
                xPosField.setText("" + xCaller.callerNode.nodeNum);
                xCaller.setMaxUpdateLayer(numLevels);
                xCaller.initPointers();

                // Get Shapes and text and draw on window.
                p3Tree.inOrderAddLines(p3Tree.root, p3Group);
                p3Tree.inOrderGetNodeShapes(p3Tree.root, p3Group);
                p3Tree.inOrderGetNodeText(p3Tree.root, p3Group);

            }

        });

        // Update caller node position on user update.
        xPosField.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)) {
                    // Get new caller position.
                    String moveCell = xPosField.getText();
                    // Get the node belonging to new caller position.
                    pointNode newCell = p3Tree.getNodebyNum(rootNode,Integer.parseInt(moveCell));

                    // Check that the selected node is a leaf node and doesn't have another caller.
                    if(newCell.isLeaf && !newCell.hasCaller) {
                        // Update caller position in old cell and at old rep.
                        xCaller.updateLocAndPointers(newCell, p3Tree);
                        xCaller.updateCallerText();

                    }

                }
            }
        });

        // Make call to callee on user update.
        calleeField.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)) {

                    String newCalleeID = calleeField.getText();
                    // Check which callee is being called.
                    for(int i = 0; i < Callees.length; i++){

                        if(Callees[i].callerID.equals(newCalleeID)){
                            newCaller = Callees[i];
                            break;
                        }

                    }

//                    // Clear previous drawings if any, and search for Callee.
                    p3Tree.clearAllLines(rootNode, p3Group);
                    p3Tree.resetLeaves(rootNode);
                    p3Tree.searchForX(newCaller, xCaller);

                }
            }
        });

        // Handles updating of caller X cmr with user command.
        xcmrin.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)) {

                    // Update caller X cmr
                    String newcmr = xcmrin.getText();

                    xCaller.CMR = Integer.parseInt(newcmr);
                    xCaller.setMaxUpdateLayer(numLevels);

                }
            }
        });

        // Update and display scene.
        p3Group.getChildren().add(mygrid);
        Scene mys = new Scene(p3Group, globalPosX , globalPosY);
        p3stage.setScene(mys);
        p3stage.setTitle("Problem #3 Solution");
        p3stage.show();

    }

    // END SECONDARY FUNCTIONS

}
