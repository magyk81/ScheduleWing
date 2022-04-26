import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private Employee[] mEmployees;
    private Section[] mSections;

    @Override
    public void start(Stage stage) {

        Employee sampleEmployee = new Employee(
                "Robin Campos",
                JobEnum.EXPEDITOR_CLERK,
                true,
                false,
                LocalDate.of(1993, Month.SEPTEMBER, 14)
        );
        mEmployees = new Employee[] { sampleEmployee };

        mSections = new Section[] {
                new Section("Dock", false),
                new Section("APPS", false),
                new Section("States Loader", false),
                new Section("LCUS Loader", false),
                new Section("Scanway", false),
                new Section("ADUS", false),
                new Section("Prep", false),
                new Section("LCUS Belt", false),

                new Section("Dock", true),
                new Section("Scanway", true),
                new Section("LCUS", true),
                new Section("States (non-Sunday)", true),
                new Section("ADUS (only Tuesday-Friday)", true),
                new Section("States (Sunday)", true)
        };

        // UI stuff.
        Group root = new Group();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(1150);
        stage.setHeight(700);

        TextField textField = new TextField();
        textField.setTranslateX(10);
        textField.setTranslateY(10);

        Button buttonLoad = new Button("Load File");
        buttonLoad.setTranslateX(10);
        buttonLoad.setTranslateY(40);
        buttonLoad.setOnAction(event -> loadFile(textField.getText()));
        Button buttonSave = new Button("Save File");
        buttonSave.setTranslateX(10);
        buttonSave.setTranslateY(70);
        buttonSave.setOnAction(event -> saveFile(textField.getText()));

        root.getChildren().addAll(textField, buttonLoad, buttonSave);

        final int topPosY = 30, posXa = 200;

        PositionSlot[][] apps = new PositionSlot[10][];
        for (int i = 0; i < apps.length; i++) {
            int offset = 40;
            if (i < 6) {
                String slotLabel;
                if (i == 0 || i == 3) slotLabel = "Load";
                else if (i == 1 || i == 4) slotLabel = "Cull";
                else slotLabel = "DJ";
                offset = (i < 3) ? 0 : 20;

                apps[i] = new PositionSlot[2];
                apps[i][0] = new PositionSlot(
                        posXa,
                        topPosY + (i * PositionSlot.HEIGHT) + offset,
                        slotLabel);
                apps[i][1] = new PositionSlot(
                        CraftEnum.MAILHANDLER,
                        posXa + PositionSlot.WIDTH,
                        topPosY + (i * PositionSlot.HEIGHT) + offset);
                for (PositionSlot slot : apps[i]) { slot.addTo(root.getChildren()); }
            } else {
                apps[i] = new PositionSlot[3];
                apps[i][0] = new PositionSlot(
                        posXa,
                        topPosY + (i * PositionSlot.HEIGHT) + offset,
                        "Sweep " + (i - 6));
                apps[i][1] = new PositionSlot(
                        CraftEnum.MAILHANDLER,
                        posXa + PositionSlot.WIDTH,
                        topPosY + (i * PositionSlot.HEIGHT) + offset);
                apps[i][2] = new PositionSlot(
                        CraftEnum.MAILHANDLER,
                        posXa + (PositionSlot.WIDTH * 2),
                        topPosY + (i * PositionSlot.HEIGHT) + offset);
                for (PositionSlot slot : apps[i]) { slot.addTo(root.getChildren()); }
            }
        }

        Text appsLabel = new Text("Apps");
        appsLabel.setFont(Font.font(20));
        appsLabel.setX(posXa); appsLabel.setY(apps[0][0].getTop() - 10);
        root.getChildren().add(appsLabel);

        PositionSlot[][] scanway = new PositionSlot[2][];
        final int scanwayPosY = apps[apps.length - 1][0].getBottom() + (int) (PositionSlot.HEIGHT * 1.5);
        for (int i = 0; i < scanway.length; i++) {
            if (i < 1) {
                scanway[i] = new PositionSlot[2];
                scanway[i][0] = new PositionSlot(
                        posXa,
                        scanwayPosY,
                        "Key");
                scanway[i][1] = new PositionSlot(
                        CraftEnum.CLERK,
                        posXa + PositionSlot.WIDTH,
                        scanwayPosY);
                for (PositionSlot slot : scanway[i]) { slot.addTo(root.getChildren()); }
            } else {
                scanway[i] = new PositionSlot[3];
                scanway[i][0] = new PositionSlot(
                        posXa,
                        scanwayPosY + (i * PositionSlot.HEIGHT),
                        "Sweep");
                scanway[i][1] = new PositionSlot(
                        CraftEnum.MAILHANDLER,
                        posXa + PositionSlot.WIDTH,
                        scanwayPosY + (i * PositionSlot.HEIGHT));
                scanway[i][2] = new PositionSlot(
                        CraftEnum.MAILHANDLER,
                        posXa + (PositionSlot.WIDTH * 2),
                        scanwayPosY + (i * PositionSlot.HEIGHT));
                for (PositionSlot slot : scanway[i]) { slot.addTo(root.getChildren()); }
            }
        }

        Text scanwayLabel = new Text("Scanway");
        scanwayLabel.setFont(Font.font(20));
        scanwayLabel.setX(posXa); scanwayLabel.setY(scanway[0][0].getTop() - 10);
        root.getChildren().add(scanwayLabel);

        PositionSlot[][] adus = new PositionSlot[4][];
        final int adusPosY = scanway[scanway.length - 1][0].getBottom() + (int) (PositionSlot.HEIGHT * 1.5);
        for (int i = 0; i < adus.length; i++) {
            if (i == 0) {
                adus[i] = new PositionSlot[4];
                adus[i][0] = new PositionSlot(
                        posXa,
                        adusPosY,
                        "Facers");
                for (int j = 1; j < adus[i].length; j++) {
                    adus[i][j] = new PositionSlot(
                            CraftEnum.CLERK,
                            posXa + (PositionSlot.WIDTH * j),
                            adusPosY);
                }
                for (PositionSlot slot : adus[i]) { slot.addTo(root.getChildren()); }
            } else if (i < 3) {
                adus[i] = new PositionSlot[3];
                adus[i][0] = new PositionSlot(
                        posXa,
                        adusPosY + (i * PositionSlot.HEIGHT),
                        (i == 1 ? "Odd" : "Even") + " Sweep");
                adus[i][1] = new PositionSlot(
                        CraftEnum.MAILHANDLER,
                        posXa + PositionSlot.WIDTH,
                        adusPosY + (i * PositionSlot.HEIGHT));
                adus[i][2] = new PositionSlot(
                        CraftEnum.MAILHANDLER,
                        posXa + (PositionSlot.WIDTH * 2),
                        adusPosY + (i * PositionSlot.HEIGHT));
                for (PositionSlot slot : adus[i]) { slot.addTo(root.getChildren()); }
            } else {
                adus[i] = new PositionSlot[2];
                adus[i][0] = new PositionSlot(
                        posXa,
                        adusPosY + (i * PositionSlot.HEIGHT),
                        "Loader");
                adus[i][1] = new PositionSlot(
                        CraftEnum.MAILHANDLER,
                        posXa + PositionSlot.WIDTH,
                        adusPosY + (i * PositionSlot.HEIGHT));
            }
        }

        Text adusLabel = new Text("ADUS");
        adusLabel.setFont(Font.font(20));
        adusLabel.setX(posXa); adusLabel.setY(adus[0][0].getTop() - 10);
        root.getChildren().add(adusLabel);

        int posXb = adus[0][adus[0].length - 1].getRight() + (PositionSlot.WIDTH / 2);

        PositionSlot[][] prep = new PositionSlot[4][];
        for (int i = 0; i < prep.length; i++) {
            prep[i] = new PositionSlot[2];
            for (int j = 0; j < prep[i].length; j++) {
                prep[i][j] = new PositionSlot(
                        CraftEnum.MAILHANDLER,
                        posXb + (PositionSlot.WIDTH * j),
                        topPosY + (PositionSlot.HEIGHT * i));
            }
            for (PositionSlot slot : prep[i]) { slot.addTo(root.getChildren()); }
        }

        Text prepLabel = new Text("Prep");
        prepLabel.setFont(Font.font(20));
        prepLabel.setX(posXb); prepLabel.setY(prep[0][0].getTop() - 10);
        root.getChildren().add(prepLabel);

        PositionSlot[][] dockMailhandlers = new PositionSlot[5][];
        final int dockMailhandlersPosY =
                prep[prep.length - 1][0].getBottom() + (int) (PositionSlot.HEIGHT * 1.5);
        for (int i = 0; i < dockMailhandlers.length; i++) {
            final String dockMailhandlersSlotLabel;
            if (i == 0) dockMailhandlersSlotLabel = "A";
            else if (i == 1) dockMailhandlersSlotLabel = "B";
            else if (i == 2) dockMailhandlersSlotLabel = "C";
            else if (i == 3) dockMailhandlersSlotLabel = "D";
            else dockMailhandlersSlotLabel = "T-5";
            if (i == 2 || i == 4) {
                dockMailhandlers[i] = new PositionSlot[3];
                dockMailhandlers[i][0] = new PositionSlot(
                        posXb,
                        dockMailhandlersPosY + (i * PositionSlot.HEIGHT),
                        dockMailhandlersSlotLabel);
                for (int j = 1; j < dockMailhandlers[i].length; j++) {
                    dockMailhandlers[i][j] = new PositionSlot(
                            CraftEnum.MAILHANDLER,
                            posXb + (PositionSlot.WIDTH * j),
                            dockMailhandlersPosY + (i * PositionSlot.HEIGHT));
                }
                for (PositionSlot slot : dockMailhandlers[i]) { slot.addTo(root.getChildren()); }
            } else {
                dockMailhandlers[i] = new PositionSlot[2];
                dockMailhandlers[i][0] = new PositionSlot(
                        posXb,
                        dockMailhandlersPosY + (i * PositionSlot.HEIGHT),
                        dockMailhandlersSlotLabel);
                dockMailhandlers[i][1] = new PositionSlot(
                        CraftEnum.MAILHANDLER,
                        posXb + PositionSlot.WIDTH,
                        dockMailhandlersPosY + (i * PositionSlot.HEIGHT));
                for (PositionSlot slot : dockMailhandlers[i]) { slot.addTo(root.getChildren()); }
            }
        }

        Text dockMailhandlersLabel = new Text("Dock Mailhandlers");
        dockMailhandlersLabel.setFont(Font.font(20));
        dockMailhandlersLabel.setX(posXb); dockMailhandlersLabel.setY(dockMailhandlers[0][0].getTop() - 10);
        root.getChildren().add(dockMailhandlersLabel);

        PositionSlot[][] dockClerks = new PositionSlot[4][];
        final int dockClerksPosY =
                dockMailhandlers[dockMailhandlers.length - 1][0].getBottom() + (int) (PositionSlot.HEIGHT * 1.5);
        for (int i = 0; i < dockClerks.length; i++) {
            final String dockClerksSlotLabel;
            if (i == 0) dockClerksSlotLabel = "A";
            else if (i == 1) dockClerksSlotLabel = "B";
            else if (i == 2) dockClerksSlotLabel = "C";
            else dockClerksSlotLabel = "D";
            dockClerks[i] = new PositionSlot[3];
            dockClerks[i][0] = new PositionSlot(
                    posXb,
                    dockClerksPosY + (i * PositionSlot.HEIGHT),
                    dockClerksSlotLabel);
            for (int j = 1; j < dockClerks[i].length; j++) {
                dockClerks[i][j] = new PositionSlot(
                        CraftEnum.CLERK,
                        posXb + (PositionSlot.WIDTH * j),
                        dockClerksPosY + (i * PositionSlot.HEIGHT));
            }
            for (PositionSlot slot : dockClerks[i]) { slot.addTo(root.getChildren()); }
        }

        int posXc = dockMailhandlers[dockMailhandlers.length - 1]
                [dockMailhandlers[dockMailhandlers.length - 1].length - 1].getRight() + (PositionSlot.WIDTH / 2);

        Text dockClerksLabel = new Text("Dock Clerks");
        dockClerksLabel.setFont(Font.font(20));
        dockClerksLabel.setX(posXb); dockClerksLabel.setY(dockClerks[0][0].getTop() - 10);
        root.getChildren().add(dockClerksLabel);

        PositionSlot[][] lcus = new PositionSlot[8][];
        for (int i = 0; i < lcus.length; i++) {
            final String lcusSlotLabel;
            if (i == 0) lcusSlotLabel = "Loader";
            else if (i == 1) lcusSlotLabel = "Keyer";
            else if (i == 2) lcusSlotLabel = "Belt";
            else if (i == 3) lcusSlotLabel = "North";
            else if (i == 4) lcusSlotLabel = "South";
            else if (i == 5) lcusSlotLabel = "A0's";
            else if (i == 6) lcusSlotLabel = "870";
            else lcusSlotLabel = "875";

            if (i == 1 || i == 4) {
                lcus[i] = new PositionSlot[2];
                lcus[i][1] = new PositionSlot(
                        CraftEnum.CLERK,
                        posXc + PositionSlot.WIDTH,
                        topPosY + (i * PositionSlot.HEIGHT));
            } else if (i == 2) {
                lcus[i] = new PositionSlot[7];
                for (int j = 1; j < lcus[i].length; j++) {
                    lcus[i][j] = new PositionSlot(
                            CraftEnum.EITHER,
                            posXc + (PositionSlot.WIDTH * j),
                            topPosY + (i * PositionSlot.HEIGHT));
                }
            } else {
                lcus[i] = new PositionSlot[3];
                for (int j = 1; j < lcus[i].length; j++) {
                    lcus[i][j] = new PositionSlot(
                            i == 0 ? CraftEnum.MAILHANDLER : CraftEnum.CLERK,
                            posXc + (PositionSlot.WIDTH * j),
                            topPosY + (i * PositionSlot.HEIGHT));
                }
            }
            lcus[i][0] = new PositionSlot(
                    posXc,
                    topPosY + (i * PositionSlot.HEIGHT),
                    lcusSlotLabel);
        }
        for (PositionSlot[] slots : lcus) {for (PositionSlot slot : slots) { slot.addTo(root.getChildren()); } }

        Text lcusLabel = new Text("LCUS");
        lcusLabel.setFont(Font.font(20));
        lcusLabel.setX(posXc); lcusLabel.setY(lcus[0][0].getTop() - 10);
        root.getChildren().add(lcusLabel);

        PositionSlot[][] states = new PositionSlot[11][];
        int statesPosY = lcus[lcus.length - 1][0].getBottom() + (int) (PositionSlot.HEIGHT * 1.5);
        for (int i = 0; i < states.length; i++) {
            final String statesSlotLabel;
            if (i == 0) statesSlotLabel = "Loader";
            else if (i == 1) statesSlotLabel = "Belt";
            else if (i < 7) statesSlotLabel = (i - 1) + "00";
            else if (i == 7) statesSlotLabel = "500/600";
            else if (i < 10) statesSlotLabel = (i - 2) + "Belt";
            else statesSlotLabel = "569";

            if (i == 0) {
                states[i] = new PositionSlot[2];
                states[i][1] = new PositionSlot(
                        CraftEnum.MAILHANDLER,
                        posXc + PositionSlot.WIDTH,
                        statesPosY);
            } else if (i == states.length - 1 || (i > 1 && i < 8)) {
                states[i] = new PositionSlot[2];
                states[i][1] = new PositionSlot(
                        CraftEnum.CLERK,
                        posXc + PositionSlot.WIDTH,
                        statesPosY + (i * PositionSlot.HEIGHT));
            } else {
                states[i] = new PositionSlot[i == 1 ? 7 : 3];
                for (int j = 1; j < states[i].length; j++) {
                    states[i][j] = new PositionSlot(
                            CraftEnum.CLERK,
                            posXc + (PositionSlot.WIDTH * j),
                            statesPosY + (i * PositionSlot.HEIGHT));
                }
            }
            states[i][0] = new PositionSlot(
                    posXc,
                    statesPosY + (i * PositionSlot.HEIGHT),
                    statesSlotLabel);
        }
        for (PositionSlot[] slots : states) {for (PositionSlot slot : slots) { slot.addTo(root.getChildren()); } }

        Text statesLabel = new Text("States");
        statesLabel.setFont(Font.font(20));
        statesLabel.setX(posXc); statesLabel.setY(states[0][0].getTop() - 10);
        root.getChildren().add(statesLabel);

        // Testing
        apps[0][1].setEmployee(sampleEmployee);

        stage.show();
    }

    private void loadFile(String path) {
        List<String> lines = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(path));
            String line;
            do {
                line = reader.readLine();
                lines.add(line);
            } while (line != null);
            reader.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        mEmployees = new Employee[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            mEmployees[i] = new Employee(lines.get(i).split(","));
        }
    }

    private void saveFile(String path) {
        if (mEmployees == null) return;
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(path, false));
            for (Employee employee : mEmployees) {
                writer.write(employee.toCsvLine());
                writer.newLine();
            }
            writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
