package com.pong;

// Importaciones necesarias
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getAppHeight;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getAppWidth;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameScene;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getInput;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getWorldProperties;

public class Pong extends GameApplication {

    // Constantes para el tamaño y velocidad de las palas y la pelota
    private static final int PADDLE_WIDTH = 30;
    private static final int PADDLE_HEIGHT = 100;
    private static final int BALL_SIZE = 20;
    private static final int PADDLE_SPEED = 5;
    private static final int BALL_SPEED = 5;

    // Entidades para las palas y la pelota
    private Entity paddle1;
    private Entity paddle2;
    private Entity ball;

    @Override
    protected void initSettings(GameSettings settings) {
        // Configura el título del juego
        settings.setTitle("Pong");
    }

    @Override
    protected void initInput() {
        // Asigna acciones a las teclas para mover las palas
        getInput().addAction(new UserAction("Up 1") {
            @Override
            protected void onAction() {
                paddle1.translateY(-PADDLE_SPEED);
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("Down 1") {
            @Override
            protected void onAction() {
                paddle1.translateY(PADDLE_SPEED);
            }
        }, KeyCode.S);

        getInput().addAction(new UserAction("Up 2") {
            @Override
            protected void onAction() {
                paddle2.translateY(-PADDLE_SPEED);
            }
        }, KeyCode.UP);

        getInput().addAction(new UserAction("Down 2") {
            @Override
            protected void onAction() {
                paddle2.translateY(PADDLE_SPEED);
            }
        }, KeyCode.DOWN);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        // Inicializa las variables del juego, como las puntuaciones
        vars.put("score1", 0);
        vars.put("score2", 0);
    }

    @Override
    protected void initGame() {
        // Crea y posiciona las palas y la pelota
        paddle1 = spawnBat(0, getAppHeight() / 2 - PADDLE_HEIGHT / 2);
        paddle2 = spawnBat(getAppWidth() - PADDLE_WIDTH, getAppHeight() / 2 - PADDLE_HEIGHT / 2);

        ball = spawnBall(getAppWidth() / 2 - BALL_SIZE / 2, getAppHeight() / 2 - BALL_SIZE / 2);
    }

    @Override
    protected void initUI() {
        // Crea y posiciona los elementos de la interfaz de usuario para mostrar las puntuaciones
        Text textScore1 = getUIFactoryService().newText("", Color.BLACK, 22);
        Text textScore2 = getUIFactoryService().newText("", Color.BLACK, 22);

        textScore1.setTranslateX(10);
        textScore1.setTranslateY(50);

        textScore2.setTranslateX(getAppWidth() - 30);
        textScore2.setTranslateY(50);

        // Vincula los textos de las puntuaciones a las variables del juego
        textScore1.textProperty().bind(getWorldProperties().intProperty("score1").asString());
        textScore2.textProperty().bind(getWorldProperties().intProperty("score2").asString());

        // Añade los textos de puntuación a la escena del juego
        getGameScene().addUINodes(textScore1, textScore2);
    }

    @Override
    protected void onUpdate(double tpf) {
        // Actualiza la posición de la pelota en cada fotograma
        Point2D velocity = ball.getObject("velocity");
        ball.translate(velocity);

        // Detecta colisiones de la pelota con las palas y cambia la dirección de la pelota
        if (ball.getX() == paddle1.getRightX()
                && ball.getY() < paddle1.getBottomY()
                && ball.getBottomY() > paddle1.getY()) {
            ball.setProperty("velocity", new Point2D(-velocity.getX(), velocity.getY()));
        }

        if (ball.getRightX() == paddle2.getX()
                && ball.getY() < paddle2.getBottomY()
                && ball.getBottomY() > paddle2.getY()) {
            ball.setProperty("velocity", new Point2D(-velocity.getX(), velocity.getY()));
        }

        // Detecta si la pelota ha salido por los lados izquierdo o derecho de la pantalla y actualiza la puntuación
        if (ball.getX() <= 0) {
            getWorldProperties().increment("score2", +1);
            resetBall();
        }

        if (ball.getRightX() >= getAppWidth()) {
            getWorldProperties().increment("score1", +1);
            resetBall();
        }

        // Detecta colisiones de la pelota con los bordes superior e inferior de la pantalla y cambia la dirección de la pelota
        if (ball.getY() <= 0) {
            ball.setY(0);
            ball.setProperty("velocity", new Point2D(velocity.getX(), -velocity.getY()));
        }

        if (ball.getBottomY() >= getAppHeight()) {
            ball.setY(getAppHeight() - BALL_SIZE);
            ball.setProperty("velocity", new Point2D(velocity.getX(), -velocity.getY()));
        }
    }

    // Método para crear una pala en una posición específica
    private Entity spawnBat(double x, double y) {
        return entityBuilder()
                .at(x, y)
                .viewWithBBox(new Rectangle(PADDLE_WIDTH, PADDLE_HEIGHT))
                .buildAndAttach();
    }

    // Método para crear la pelota en una posición específica
    private Entity spawnBall(double x, double y) {
        return entityBuilder()
                .at(x, y)
                .viewWithBBox(new Rectangle(BALL_SIZE, BALL_SIZE))
                .with("velocity", new Point2D(BALL_SPEED, BALL_SPEED))
                .buildAndAttach();
    }

    // Método para resetear la posición y velocidad de la pelota
    private void resetBall() {
        ball.setPosition(getAppWidth() / 2 - BALL_SIZE / 2, getAppHeight() / 2 - BALL_SIZE / 2);
        ball.setProperty("velocity", new Point2D(BALL_SPEED, BALL_SPEED));
    }

    public static void main(String[] args) {
        // Método principal para iniciar el juego
        launch(args);
    }
}
