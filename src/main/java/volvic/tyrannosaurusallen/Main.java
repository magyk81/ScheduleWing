package volvic.tyrannosaurusallen;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Main extends Application {

    private Employee[] mEmployees;
    private Section[] mSections;
    private final ListView<Employee> mListView = new ListView<>();
    private final Text mEmployeeInfo = new Text();

    @Override
    public void start(Stage stage) {

        Employee sampleEmployee = new Employee(
                "Robin Campos",
                JobEnum.EXPEDITOR_CLERK,
                true,
                false,
                LocalDate.of(1993, Month.SEPTEMBER, 14),
                "MTR" // Monday, Tuesday, and Thursday off
        );

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
        stage.setWidth(1250);
        stage.setHeight(770);

        final int indent = 10;
        TextField textField = new TextField();
        textField.setTranslateX(indent);
        textField.setTranslateY(indent);

        Button buttonLoad = new Button("Load File");
        buttonLoad.setTranslateX(indent);
        buttonLoad.setTranslateY(40);
        buttonLoad.setOnAction(event -> loadFile(textField.getText()));
        Button buttonSave = new Button("Save File");
        buttonSave.setTranslateX(indent);
        buttonSave.setTranslateY(70);
        buttonSave.setOnAction(event -> saveFile(textField.getText()));
        Button buttonApply = new Button("Apply as Previous");
        buttonApply.setTranslateX((indent * 2) + 80);
        buttonApply.setTranslateY(buttonLoad.getTranslateY());
        buttonApply.setOnAction(event -> applyAsPrev(buttonApply));

        mListView.setTranslateX(indent);
        mListView.setTranslateY(100);
        int listViewWidth = 200, listViewHeight = 360;
        mListView.setMaxWidth(listViewWidth);
        mListView.setMaxHeight(listViewHeight);
        mListView.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, employee, t1) -> showEmployeeInfo(t1));

        root.getChildren().addAll(textField, buttonLoad, buttonSave, buttonApply, mListView, mEmployeeInfo);

        Line[] employeeInfoBorder = new Line[4];
        int employeeInfoPosY = 100 + listViewHeight + indent, employeeInfoHeight = 250;
        employeeInfoBorder[0] = new Line(indent, employeeInfoPosY, indent + listViewWidth, employeeInfoPosY);
        employeeInfoBorder[1] = new Line(indent + listViewWidth, employeeInfoPosY,
                indent + listViewWidth, employeeInfoPosY + employeeInfoHeight);
        employeeInfoBorder[2] = new Line(indent, employeeInfoPosY + employeeInfoHeight,
                indent + listViewWidth, employeeInfoPosY + employeeInfoHeight);
        employeeInfoBorder[3] = new Line(indent, employeeInfoPosY, indent, employeeInfoPosY + employeeInfoHeight);
        root.getChildren().addAll(employeeInfoBorder);
        mEmployeeInfo.setX(indent * 2); mEmployeeInfo.setY(employeeInfoPosY + indent * 2);

        Consumer<PositionSlot> consoomer = positionSlot -> {
            Employee selectedEmployee = mListView.getSelectionModel().getSelectedItem();
            if (selectedEmployee != null) {
                if (positionSlot.getEmployee() == selectedEmployee) {
                    selectedEmployee.removePosition(positionSlot);
                } else {
                    PositionSlot existingPosition = selectedEmployee.addPosition(positionSlot);
                    if (existingPosition != null) {
                        // Should work now.
                        selectedEmployee.addPosition(positionSlot);
                    }
                    positionSlot.setEmployee(selectedEmployee);
                }
                showEmployeeInfo(selectedEmployee);
                buttonApply.setDisable(false);
            } else positionSlot.clearEmployee();
        };

        final int topPosY = 30, posXa = 300;

        Text appsLabel = new Text("Apps");
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
                        topPosY + (i * PositionSlot.HEIGHT) + offset,
                        slotLabel, appsLabel, consoomer);
                for (PositionSlot slot : apps[i]) { slot.addTo(root.getChildren()); }
            } else {
                apps[i] = new PositionSlot[3];
                String subtitle = "Sweep " + (i - 6);
                apps[i][0] = new PositionSlot(
                        posXa,
                        topPosY + (i * PositionSlot.HEIGHT) + offset,
                        subtitle);
                apps[i][1] = new PositionSlot(
                        CraftEnum.MAILHANDLER,
                        posXa + PositionSlot.WIDTH,
                        topPosY + (i * PositionSlot.HEIGHT) + offset,
                        subtitle, appsLabel, consoomer);
                apps[i][2] = new PositionSlot(
                        CraftEnum.MAILHANDLER,
                        posXa + (PositionSlot.WIDTH * 2),
                        topPosY + (i * PositionSlot.HEIGHT) + offset,
                        subtitle, appsLabel, consoomer);
                for (PositionSlot slot : apps[i]) { slot.addTo(root.getChildren()); }
            }
        }

        appsLabel.setFont(Font.font(20));
        appsLabel.setX(posXa); appsLabel.setY(apps[0][0].getTop() - 10);
        root.getChildren().add(appsLabel);

        Text scanwayLabel = new Text("Scanway");
        PositionSlot[][] scanway = new PositionSlot[2][];
        final int scanwayPosY = apps[apps.length - 1][0].getBottom() + (int) (PositionSlot.HEIGHT * 1.5);
        for (int i = 0; i < scanway.length; i++) {
            if (i < 1) {
                scanway[i] = new PositionSlot[2];
                String subtitle = "Key";
                scanway[i][0] = new PositionSlot(
                        posXa,
                        scanwayPosY,
                        subtitle);
                scanway[i][1] = new PositionSlot(
                        CraftEnum.CLERK,
                        posXa + PositionSlot.WIDTH,
                        scanwayPosY,
                        subtitle, scanwayLabel, consoomer);
                for (PositionSlot slot : scanway[i]) { slot.addTo(root.getChildren()); }
            } else {
                scanway[i] = new PositionSlot[3];
                String subtitle = "Sweep";
                scanway[i][0] = new PositionSlot(
                        posXa,
                        scanwayPosY + (i * PositionSlot.HEIGHT),
                        subtitle);
                scanway[i][1] = new PositionSlot(
                        CraftEnum.MAILHANDLER,
                        posXa + PositionSlot.WIDTH,
                        scanwayPosY + (i * PositionSlot.HEIGHT),
                        subtitle, scanwayLabel, consoomer);
                scanway[i][2] = new PositionSlot(
                        CraftEnum.MAILHANDLER,
                        posXa + (PositionSlot.WIDTH * 2),
                        scanwayPosY + (i * PositionSlot.HEIGHT),
                        subtitle, scanwayLabel, consoomer);
                for (PositionSlot slot : scanway[i]) { slot.addTo(root.getChildren()); }
            }
        }

        scanwayLabel.setFont(Font.font(20));
        scanwayLabel.setX(posXa); scanwayLabel.setY(scanway[0][0].getTop() - 10);
        root.getChildren().add(scanwayLabel);

        Text adusLabel = new Text("ADUS");
        PositionSlot[][] adus = new PositionSlot[4][];
        final int adusPosY = scanway[scanway.length - 1][0].getBottom() + (int) (PositionSlot.HEIGHT * 1.5);
        for (int i = 0; i < adus.length; i++) {
            if (i == 0) {
                adus[i] = new PositionSlot[4];
                String subtitle = "Facers";
                adus[i][0] = new PositionSlot(
                        posXa,
                        adusPosY,
                        subtitle);
                for (int j = 1; j < adus[i].length; j++) {
                    adus[i][j] = new PositionSlot(
                            CraftEnum.CLERK,
                            posXa + (PositionSlot.WIDTH * j),
                            adusPosY,
                            subtitle, adusLabel, consoomer);
                }
                for (PositionSlot slot : adus[i]) { slot.addTo(root.getChildren()); }
            } else if (i < 3) {
                adus[i] = new PositionSlot[3];
                String subtitle = (i == 1 ? "Odd" : "Even") + " Sweep";
                adus[i][0] = new PositionSlot(
                        posXa,
                        adusPosY + (i * PositionSlot.HEIGHT),
                        subtitle);
                adus[i][1] = new PositionSlot(
                        CraftEnum.MAILHANDLER,
                        posXa + PositionSlot.WIDTH,
                        adusPosY + (i * PositionSlot.HEIGHT),
                        subtitle, adusLabel, consoomer);
                adus[i][2] = new PositionSlot(
                        CraftEnum.MAILHANDLER,
                        posXa + (PositionSlot.WIDTH * 2),
                        adusPosY + (i * PositionSlot.HEIGHT),
                        subtitle, adusLabel, consoomer);
                for (PositionSlot slot : adus[i]) { slot.addTo(root.getChildren()); }
            } else {
                adus[i] = new PositionSlot[2];
                String subtitle = "Loader";
                adus[i][0] = new PositionSlot(
                        posXa,
                        adusPosY + (i * PositionSlot.HEIGHT),
                        subtitle);
                adus[i][1] = new PositionSlot(
                        CraftEnum.MAILHANDLER,
                        posXa + PositionSlot.WIDTH,
                        adusPosY + (i * PositionSlot.HEIGHT),
                        subtitle, adusLabel, consoomer);
            }
        }

        adusLabel.setFont(Font.font(20));
        adusLabel.setX(posXa); adusLabel.setY(adus[0][0].getTop() - 10);
        root.getChildren().add(adusLabel);

        int posXb = adus[0][adus[0].length - 1].getRight() + (PositionSlot.WIDTH / 2);

        Text prepLabel = new Text("Prep");
        PositionSlot[][] prep = new PositionSlot[4][];
        for (int i = 0; i < prep.length; i++) {
            prep[i] = new PositionSlot[2];
            for (int j = 0; j < prep[i].length; j++) {
                prep[i][j] = new PositionSlot(
                        CraftEnum.MAILHANDLER,
                        posXb + (PositionSlot.WIDTH * j),
                        topPosY + (PositionSlot.HEIGHT * i),
                        null, prepLabel, consoomer);
            }
            for (PositionSlot slot : prep[i]) { slot.addTo(root.getChildren()); }
        }

        prepLabel.setFont(Font.font(20));
        prepLabel.setX(posXb); prepLabel.setY(prep[0][0].getTop() - 10);
        root.getChildren().add(prepLabel);

        Text dockMailhandlersLabel = new Text("Dock Mailhandlers");
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
                            dockMailhandlersPosY + (i * PositionSlot.HEIGHT),
                            dockMailhandlersSlotLabel, dockMailhandlersLabel, consoomer);
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
                        dockMailhandlersPosY + (i * PositionSlot.HEIGHT),
                        dockMailhandlersSlotLabel, dockMailhandlersLabel, consoomer);
                for (PositionSlot slot : dockMailhandlers[i]) { slot.addTo(root.getChildren()); }
            }
        }

        dockMailhandlersLabel.setFont(Font.font(20));
        dockMailhandlersLabel.setX(posXb); dockMailhandlersLabel.setY(dockMailhandlers[0][0].getTop() - 10);
        root.getChildren().add(dockMailhandlersLabel);

        Text dockClerksLabel = new Text("Dock Clerks");
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
                        dockClerksPosY + (i * PositionSlot.HEIGHT),
                        dockClerksSlotLabel, dockClerksLabel, consoomer);
            }
            for (PositionSlot slot : dockClerks[i]) { slot.addTo(root.getChildren()); }
        }

        int posXc = dockMailhandlers[dockMailhandlers.length - 1]
                [dockMailhandlers[dockMailhandlers.length - 1].length - 1].getRight() + (PositionSlot.WIDTH / 2);

        dockClerksLabel.setFont(Font.font(20));
        dockClerksLabel.setX(posXb); dockClerksLabel.setY(dockClerks[0][0].getTop() - 10);
        root.getChildren().add(dockClerksLabel);

        Text lcusLabel = new Text("LCUS");
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
                        topPosY + (i * PositionSlot.HEIGHT),
                        lcusSlotLabel, lcusLabel, consoomer);
            } else if (i == 2) {
                lcus[i] = new PositionSlot[7];
                for (int j = 1; j < lcus[i].length; j++) {
                    lcus[i][j] = new PositionSlot(
                            CraftEnum.EITHER,
                            posXc + (PositionSlot.WIDTH * j),
                            topPosY + (i * PositionSlot.HEIGHT),
                            lcusSlotLabel, lcusLabel, consoomer);
                }
            } else {
                lcus[i] = new PositionSlot[3];
                for (int j = 1; j < lcus[i].length; j++) {
                    lcus[i][j] = new PositionSlot(
                            i == 0 ? CraftEnum.MAILHANDLER : CraftEnum.CLERK,
                            posXc + (PositionSlot.WIDTH * j),
                            topPosY + (i * PositionSlot.HEIGHT),
                            lcusSlotLabel, lcusLabel, consoomer);
                }
            }
            lcus[i][0] = new PositionSlot(
                    posXc,
                    topPosY + (i * PositionSlot.HEIGHT),
                    lcusSlotLabel);
        }
        for (PositionSlot[] slots : lcus) {for (PositionSlot slot : slots) { slot.addTo(root.getChildren()); } }

        lcusLabel.setFont(Font.font(20));
        lcusLabel.setX(posXc); lcusLabel.setY(lcus[0][0].getTop() - 10);
        root.getChildren().add(lcusLabel);

        Text statesLabel = new Text("States");
        PositionSlot[][] states = new PositionSlot[11][];
        int statesPosY = lcus[lcus.length - 1][0].getBottom() + (int) (PositionSlot.HEIGHT * 1.5);
        for (int i = 0; i < states.length; i++) {
            final String statesSlotLabel;
            if (i == 0) statesSlotLabel = "Loader";
            else if (i == 1) statesSlotLabel = "Belt";
            else if (i < 7) statesSlotLabel = (i - 2) + "00";
            else if (i == 7) statesSlotLabel = "500/600";
            else if (i == 8) statesSlotLabel = (i - 1) + "00";
            else if (i < 10) statesSlotLabel = i + "00";
            else statesSlotLabel = "569";

            if (i == 0) {
                states[i] = new PositionSlot[2];
                states[i][1] = new PositionSlot(
                        CraftEnum.MAILHANDLER,
                        posXc + PositionSlot.WIDTH,
                        statesPosY,
                        statesSlotLabel, statesLabel, consoomer);
            } else if (i == states.length - 1 || (i > 1 && i < 8)) {
                states[i] = new PositionSlot[2];
                states[i][1] = new PositionSlot(
                        CraftEnum.CLERK,
                        posXc + PositionSlot.WIDTH,
                        statesPosY + (i * PositionSlot.HEIGHT),
                        statesSlotLabel, statesLabel, consoomer);
            } else {
                states[i] = new PositionSlot[i == 1 ? 7 : 3];
                for (int j = 1; j < states[i].length; j++) {
                    states[i][j] = new PositionSlot(
                            CraftEnum.CLERK,
                            posXc + (PositionSlot.WIDTH * j),
                            statesPosY + (i * PositionSlot.HEIGHT),
                            statesSlotLabel, statesLabel, consoomer);
                }
            }
            states[i][0] = new PositionSlot(
                    posXc,
                    statesPosY + (i * PositionSlot.HEIGHT),
                    statesSlotLabel);
        }
        for (PositionSlot[] slots : states) {for (PositionSlot slot : slots) { slot.addTo(root.getChildren()); } }

        statesLabel.setFont(Font.font(20));
        statesLabel.setX(posXc); statesLabel.setY(states[0][0].getTop() - 10);
        root.getChildren().add(statesLabel);

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

        mEmployees = new Employee[lines.size() - ((lines.get(lines.size() - 1) == null) ? 1 : 0)];
        for (int i = 0; i < mEmployees.length; i++) {
            mEmployees[i] = new Employee(lines.get(i).split(","));
        }

        mListView.setItems(FXCollections.observableArrayList(mEmployees));
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

    private void applyAsPrev(Button button) {
        for (Employee employee : mEmployees) { employee.applyAsPrev(); }
        showEmployeeInfo(mListView.getSelectionModel().getSelectedItem());
        button.setDisable(true);
    }

    private void showEmployeeInfo(Employee employee) {
        mEmployeeInfo.setText(employee.getInfo());
    }

    public static void main(String[] args) {
        launch();
    }
}