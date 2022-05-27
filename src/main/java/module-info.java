module volvic.tyrannosaurusallen {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens volvic.tyrannosaurusallen to javafx.fxml;
    exports volvic.tyrannosaurusallen;
}