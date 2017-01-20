import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by doyle on 02/01/2017.
 */
public class Model {

    //Test Value (Remove Later)
    private StringProperty dadoTeste = new SimpleStringProperty();

    //Emotions
    //Mood
    private StringProperty moodValue = new SimpleStringProperty();

    //Needs
    private StringProperty foodValue = new SimpleStringProperty();
    private StringProperty liquidValue = new SimpleStringProperty();
    private StringProperty sleepValue = new SimpleStringProperty();

    //JavaFX Methods + Getters + Setters
    public final java.lang.String getDadoTeste(){
        return this.dadoTesteProperty().get();
    }
    public StringProperty dadoTesteProperty() {
        return dadoTeste;
    }
    public void setDadoTeste(String dadoTeste) {
        this.dadoTeste.set(dadoTeste);
    }

    public final java.lang.String getMood(){
        return this.moodValueProperty().get();
    }
    public StringProperty moodValueProperty() {
        return moodValue;
    }
    public void setMoodValue(String md) {
        this.moodValue.set(md);
    }

    public final java.lang.String getFoodValue(){
        return this.foodValueProperty().get();
    }
    public StringProperty foodValueProperty() {
        return foodValue;
    }
    public void setFoodValue(String food) {
        this.foodValue.set(food);
    }

    public final java.lang.String getLiquidValue(){ return this.liquidValueProperty().get(); }
    public StringProperty liquidValueProperty() { return liquidValue; }
    public void setLiquidValue(String liquid) { this.liquidValue.set(liquid); }

    public final java.lang.String getSleepValue(){ return this.sleepValueProperty().get(); }
    public StringProperty sleepValueProperty() { return sleepValue; }
    public void setSleepValue(String sleep) { this.sleepValue.set(sleep); }

}
