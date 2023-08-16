module uz.khurozov.fuzzyroute {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign2;
    requires jFuzzyLogic;

    opens uz.khurozov.fuzzyroute to javafx.fxml;
    exports uz.khurozov.fuzzyroute;
}