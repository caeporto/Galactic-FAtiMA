import javafx.animation.Animation;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Created by doyle on 02/01/2017.
 */

public class Controller {

    //private View prototypeView = new View();
    private Model prototypeModel = new Model();

    private int bindTestInteger = 0;

    private static final Image image = new Image("spritesheetBully.png");

    private static final int COLUMNS  =   3;
    private static final int COUNT    =  8;
    private static final int OFFSET_X =  3;
    private static final int OFFSET_Y =  5;
    private static final int WIDTH    = 130;
    private static final int HEIGHT   = 130;

    private Animation animation;


    @FXML
    private javafx.scene.control.Label testLabel;

    @FXML
    private ImageView Monster;

    public void initialize(){
        System.out.println("Controller initialized.");

        testLabel.textProperty().bind(prototypeModel.dadoTesteProperty());

        Monster.setImage(image);

        Monster.setViewport(new Rectangle2D(OFFSET_X, OFFSET_Y, WIDTH, HEIGHT));

         animation = new SpriteAnimation(
                Monster,
                Duration.millis(1500),
                COUNT, COLUMNS,
                OFFSET_X, OFFSET_Y,
                WIDTH, HEIGHT
        );

        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();

    }

    public void bindTest(){
        bindTestInteger+=1;
        prototypeModel.setDadoTeste(prototypeModel.getDadoTeste() + bindTestInteger);
    }

}
