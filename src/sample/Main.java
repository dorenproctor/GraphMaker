package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main extends Application {

    Node lastAdded, selected;
    List<Node> nodes;
    int idtracker = 0;
    boolean drawing = false;
    boolean connecting = false;
    int radius = 20;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Group root = new Group();
        Image img = new Image(new URL("https://www.vancouver.wsu.edu/sites/www.vancouver.wsu.edu/files/images/campusmap-small.jpg").openStream());
        ImageView imgview = new ImageView(img);
        root.getChildren().add(imgview);
        Button breakBtn = new Button("Break");
        Button drawBtn = new Button("Drawing");
        Button connectBtn = new Button("Connect");
        HBox hbox = new HBox();
        hbox.getChildren().add(breakBtn);
        hbox.getChildren().add(drawBtn);
        hbox.getChildren().add(connectBtn);
        root.getChildren().add(hbox);
        breakBtn.setLayoutY(img.getHeight());
        drawBtn.setLayoutY(img.getHeight());
        drawBtn.setLayoutX(breakBtn.getWidth());
        root.setId("pane");
        primaryStage.setTitle("Hello World");
        Scene scene = new Scene(root, img.getWidth(), img.getHeight()+drawBtn.getHeight());
        scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        File file = new File("src/sample/sampleGraph.txt");
        Scanner scanner = new Scanner(file);
        nodes = new LinkedList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] args = line.split(",");
            Node temp = new Node(Integer.valueOf(args[1]), Integer.valueOf(args[2]), Integer.valueOf(args[0]));
            if (idtracker < temp.id) {
                idtracker = temp.id+1;
            }
            for (int i = 3; i<args.length; i++) {
                temp.connections.add(Integer.valueOf(args[i]));
                drawCircle(temp.x, temp.y, temp.id, root);
            }
            nodes.add(temp);
        }
        for (Node i : nodes) {
            for (int j : i.connections) {
                drawLine(getNodeById(j), i, root);
            }
        }
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                for (Node i : nodes) {
                    System.out.print(i.id+","+i.x+","+i.y);
                    for (int j : i.connections) {
                        System.out.print(","+j);
                    }
                    System.out.print("\n");
                }
            }
        }));

        breakBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                lastAdded = null;
                drawing = false;
                selected = null;
                connecting = false;
            }
        });
        drawBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                drawing = true;
                selected = null;
                connecting = false;
                System.out.println("Drawing: "+String.valueOf(drawing));
            }
        });
        connectBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                connecting = true;
                drawing = false;
                lastAdded = null;
                selected = null;
                System.out.println("Connecting: "+String.valueOf(connecting));
            }
        });
        imgview.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                int x = (int) mouseEvent.getX();
                int y = (int) mouseEvent.getY();
                System.out.println("blah or something");
                if (drawing) {
                    Node newNode = new Node(x, y, idtracker++);
                    nodes.add(newNode);
                    drawCircle(x, y, newNode.id, root);
                    if (lastAdded != null) {
                        drawLine(newNode, lastAdded, root);
                        newNode.connections.add(lastAdded.id);
                        lastAdded.connections.add(newNode.id);
                    }
                    lastAdded = newNode;
                    System.out.println(newNode.x + "   " + newNode.y);
                }
                else if (connecting) {
                    Node nearest = getNear(x, y);
                    if (nearest != null) {
                        if (selected == null) {
                            selected = nearest;
                        }
                        else if (nearest.id != selected.id){
                            nearest.connections.add(selected.id);
                            selected.connections.add(nearest.id);
                            drawLine(nearest, selected, root);
                            selected = null;
                            connecting = false;
                        }
                    }
                }
            }
        });

    }

    public Node getNear(int x, int y) {
        for (Node i : nodes) {
            if (Math.abs(i.x - x) <= radius && Math.abs(i.y - y) <= radius) {
                System.out.println("Node found: "+i.id);
                return i;
            }
        }
        System.out.println("No node found");
        return null;
    }

    public void drawCircle(int x, int y, int id, Group root) {
        Text text = new Text( "  "+String.valueOf((id)));
        text.setStroke(Color.RED);
        text.setX(x);
        text.setY(y);
        root.getChildren().add(text);
        Circle circle = new Circle(x, y, radius);
        circle.setStroke(Color.RED);
        circle.setFill(null);
        circle.setStrokeWidth(2.0);
        root.getChildren().add(circle);
    }

    public void drawLine(Node node1, Node node2, Group root) {
        Line line = new Line(node1.x, node1.y, node2.x, node2.y);
        line.setStroke(Color.RED);
        line.setStrokeWidth(1);
        root.getChildren().add(line);
    }

    public Node getNodeById(int id) {
        for (Node i : nodes) {
            if (i.id == id) {
                return i;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
