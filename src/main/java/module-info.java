module com.reversepong.reversepong {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;

    opens com.pong to javafx.fxml;
    exports com.pong;
}