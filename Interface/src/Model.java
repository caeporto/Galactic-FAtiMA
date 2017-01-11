import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by doyle on 02/01/2017.
 */
public class Model {

    private StringProperty dadoTeste = new SimpleStringProperty();

    public Model(){
        this.dadoTeste.set("0.8");
    }

    public final java.lang.String getDadoTeste(){
        return this.dadoTesteProperty().get();
    }

    public StringProperty dadoTesteProperty() {
        return dadoTeste;
    }

    public void setDadoTeste(String dadoTeste) {
        this.dadoTeste.set(dadoTeste);
    }
}
