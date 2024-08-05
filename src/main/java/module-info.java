module uz.khurozov.route {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign2;

    opens uz.khurozov.route to javafx.fxml;
    exports uz.khurozov.route;
}