module swp.com.zeitenrechner {
    requires javafx.controls;
    requires javafx.fxml;


    opens swp.com.zeitenrechner to javafx.fxml;
    exports swp.com.zeitenrechner;
}