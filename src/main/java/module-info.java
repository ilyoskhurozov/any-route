module uz.ilyoskhurozov.anyroute {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign2;

    opens uz.ilyoskhurozov.anyroute to javafx.fxml;
    exports uz.ilyoskhurozov.anyroute;
}