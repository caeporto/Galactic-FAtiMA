import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Created by doyle on 02/01/2017.
 */

public class Controller {

    //private View prototypeView = new View();
    private Model prototypeModel = new Model();

    private int bindTestInteger = 0;

    private final Image image1 = new Image("teste_pose1.png");
    private final Image image2 = new Image("teste_pose2.png");
    private final Image image3 = new Image("teste_pose3.png");
    private final Image image4 = new Image("teste_pose4.png");
    private final Image image5 = new Image("teste_pose5.png");
    private final Image image6 = new Image("teste_pose6.png");
    private final Image image7 = new Image("teste_pose7.png");
    private final Image image8 = new Image("teste_pose8.png");

    @FXML
    private javafx.scene.control.Label testLabel;

    @FXML
    private ImageView Monster;

    public void initialize(){
        System.out.println("Controller initialized.");

        testLabel.textProperty().bind(prototypeModel.dadoTesteProperty());

    }

    public void bindTest(){
        bindTestInteger+=1;
        prototypeModel.setDadoTeste(prototypeModel.getDadoTeste() + bindTestInteger);
    }

}
