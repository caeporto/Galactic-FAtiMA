import FAtiMA.Core.conditions.Condition;
import FAtiMA.Core.emotionalState.ActiveEmotion;
import FAtiMA.Core.emotionalState.EmotionalState;
import FAtiMA.Core.exceptions.ActionsParsingException;
import FAtiMA.Core.exceptions.GoalLibParsingException;
import FAtiMA.Core.exceptions.UnknownGoalException;
import FAtiMA.Core.goals.Goal;
import FAtiMA.Core.goals.GoalLibrary;
import FAtiMA.Core.plans.Effect;
import FAtiMA.Core.plans.Step;
import FAtiMA.Core.sensorEffector.Event;
import FAtiMA.Core.util.ConfigurationManager;
import FAtiMA.Core.wellFormedNames.Name;
import FAtiMA.Core.wellFormedNames.Substitution;
import FAtiMA.Core.wellFormedNames.Symbol;
import FAtiMA.Core.wellFormedNames.Unifier;
import FAtiMA.DeliberativeComponent.DeliberativeComponent;
import FAtiMA.DeliberativeComponent.Intention;
import FAtiMA.OCCAffectDerivation.OCCAffectDerivationComponent;
import FAtiMA.ReactiveComponent.ReactiveComponent;
import FAtiMA.ToM.ToMComponent;
import FAtiMA.advancedMemoryComponent.AdvancedMemoryComponent;
import FAtiMA.culture.CulturalDimensionsComponent;
import FAtiMA.emotionalIntelligence.EmotionalIntelligence;
import FAtiMA.empathy.EmpathyComponent;
import FAtiMA.maslowHierarchyOfNeeds.MotivationalComponent;
import FAtiMA.maslowHierarchyOfNeeds.Motivator;
import FAtiMA.maslowHierarchyOfNeeds.MotivatorHierarchy;
import FAtiMA.socialRelations.SocialRelationsComponent;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import FAtiMA.Core.AgentCore;
import javafx.util.Pair;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by doyle on 02/01/2017.
 */

public class Controller {

    private static final HashMap<Thread, AgentCore> agents = new HashMap();
    private static WorldTest worldTest;
    private static Thread worldThread;

    private static final String[] ag_modules = {"MaslowHierarchy", "MotivationalState", "SocialRelations", "AdvancedMemory", "EmotionalIntelligence", "ToM", "CulturalDimensionsComponent", "EmpathyComponent"};
    private static String agentName;

    //Data - Needs - Emotions - Goals(...)
    private Model dataModel = new Model();

    private static final Image image = new Image("spritesheetBully.png");

    private static final int COLUMNS  =  3;
    private static final int COUNT    =  8;
    private static final int OFFSET_X =  3;
    private static final int OFFSET_Y =  5;
    private static final int WIDTH    = 130;
    private static final int HEIGHT   = 130;

    private Animation animation;

    @FXML private javafx.scene.control.Label foodLabel;
    @FXML private javafx.scene.control.Label foodLabelMS;
    @FXML private ProgressBar foodBarMonster;

    @FXML private javafx.scene.control.Label liquidLabel;
    @FXML private javafx.scene.control.Label liquidLabelMS;
    @FXML private ProgressBar liquidBarMonster;

    @FXML private javafx.scene.control.Label sleepLabel;
    @FXML private javafx.scene.control.Label sleepLabelMS;
    @FXML private ProgressBar sleepBarMonster;

    @FXML private javafx.scene.control.Label moodLabel;

    @FXML private ImageView Monster;

    @FXML private javafx.scene.layout.VBox emotionsBox;

    @FXML private javafx.scene.layout.VBox goalsBox;

    @FXML private javafx.scene.layout.VBox intentionsBox;

    @FXML private ProgressBar moodBar;

    //Enviroment
    @FXML private TextArea textAreaEnv;
    @FXML private ChoiceBox choiceBoxEnv; // ChoiceBoxListCell<E>
    @FXML private Button buttonEnv;
    private static int buffsize = 250;
    private Random _r;

    private static HashMap<String, Label> currentActiveEmotions; //used to display current active emotions
    private static HashMap<String, Label> currentActiveIntentions; //used to display current active intentions

    public void initialize(){
        System.out.println("Controller initialized.");

        _r = new Random();

        //world initialization
        worldThread = new Thread()
        {
            public void run() {
                String[] arguments = {View.arguments[0], View.arguments[1]};
                try {
                    worldTest = WorldTest.createWorld(arguments, textAreaEnv);
                    worldTest.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worldThread.setName("World");
        worldThread.start();

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.ParseFile(worldTest.GetUserOptionsFile()+".txt");

        currentActiveEmotions = new HashMap<>();
        currentActiveIntentions = new HashMap<>();

        foodLabel.textProperty().bind(dataModel.foodValueProperty());
        foodLabelMS.textProperty().bind(dataModel.foodValueProperty());

        liquidLabel.textProperty().bind(dataModel.liquidValueProperty());
        liquidLabelMS.textProperty().bind(dataModel.liquidValueProperty());

        sleepLabel.textProperty().bind(dataModel.sleepValueProperty());
        sleepLabelMS.textProperty().bind(dataModel.sleepValueProperty());

        moodLabel.textProperty().bind(dataModel.moodValueProperty());

        String xml = View.arguments[0];
        String scenery = View.arguments[1];
        int agent_num = View.arguments.length; // -2

        ArrayList<String> modules = new ArrayList<>(Arrays.asList(ag_modules));
        ArrayList<String> ag_config = new ArrayList<>();
        boolean first = true;
        for(int i = 2; i < agent_num; i++)
        {
            if(!modules.contains(View.arguments[i]))
            {
                if(first)
                {
                    first = !first;
                    ag_config.add(View.arguments[i]);
                }
                else {
                    try {
                        String[] ag_args = ag_config.toArray(new String[ag_config.size()]);
                        String[] scenery_args = new String[]{xml, scenery};
                        initializeAgent(addArray(scenery_args, ag_args));
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (UnknownGoalException e) {
                        e.printStackTrace();
                    } catch (ActionsParsingException e) {
                        e.printStackTrace();
                    } catch (GoalLibParsingException e) {
                        e.printStackTrace();
                    }
                    ag_config.clear();
                    ag_config.add(View.arguments[i]);
                }
            }
            else
            {
                ag_config.add(View.arguments[i]);
            }
        }

        //inicializa o primeiro e/ou ultimo agente
        try {
            String[] ag_args2 = ag_config.toArray(new String[ag_config.size()]);
            String[] scenery_args2 = new String[]{xml, scenery};
            agentName = ag_args2[0];
            initializeAgent(addArray(scenery_args2, ag_args2));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnknownGoalException e) {
            e.printStackTrace();
        } catch (ActionsParsingException e) {
            e.printStackTrace();
        } catch (GoalLibParsingException e) {
            e.printStackTrace();
        }

        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                initializeGoals();
                while (true) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            updateNeeds();
                            updateEmotions();
                            updateIntentions();
                        }
                    });
                    Thread.sleep(100);
                }
            }
        };
        Thread updateThread = new Thread(task);
        updateThread.start();

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

        buttonEnv.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                takeAction();
            }
        });

    }

    public void updateNeeds(){
        AgentCore ag = agents.get(this.getThreadByName(agentName));
        MotivationalComponent component = (MotivationalComponent) ag.getComponent("MotivationalState");
        Motivator[] motivators = component.getMotivators(MotivatorHierarchy.PHYSIOLOGY);
        for(int i = 0; i < motivators.length; i++)
        {
            Motivator mot = motivators[i];
            if(mot != null)
            {
                switch(i){
                    case 0: //Food
                        Float foodIntensity = mot.GetIntensity();
                        dataModel.setFoodValue("Food : " + Float.toString(foodIntensity));
                        foodBarMonster.setProgress(foodIntensity/10);
                        break;
                    case 1: //Liquid
                        Float liquidIntensity = mot.GetIntensity();
                        dataModel.setLiquidValue("Liquid : " + Float.toString(liquidIntensity));
                        liquidBarMonster.setProgress(liquidIntensity/10);
                        break;
                    case 2: //Sleep
                        Float sleepIntensity = mot.GetIntensity();
                        dataModel.setSleepValue("Sleep : " + Float.toString(sleepIntensity));
                        sleepBarMonster.setProgress(sleepIntensity/10);
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                }
            }
        }
    }

    public void updateEmotions(){
        AgentCore ag = agents.get(this.getThreadByName(agentName));
        EmotionalState state = ag.getEmotionalState();
        Float mood = state.GetMood(); //mood
        Iterator<ActiveEmotion> active_emotions = state.GetEmotionsIterator(); //set of currently active emotions
        //emotionsBox.getChildren().removeAll(currentActiveEmotions.values());
        for (; active_emotions.hasNext(); ) {
            ActiveEmotion emotion = active_emotions.next();
            String emotion_name = emotion.getType();
            Float intensity = emotion.GetIntensity();
            Name direction = emotion.GetDirection();
            Event event = emotion.GetCause(); //action, subject, target, etc...
            String action = event.GetAction();
            String subject = event.GetSubject();
            String target = event.GetTarget();
            String currentEmotion = emotion_name+action+subject+target;
            boolean edited = false;
            for(String s : currentActiveEmotions.keySet())
            {
                if(s.equals(currentEmotion))
                {
                    //editar valor na interface
                    Label label = currentActiveEmotions.get(currentEmotion);
                    label.setText(subject+" "+emotion_name+" "+action+" "+target+" "+intensity);
                    edited = true;
                    break;
                }
            }
            if(!edited)
            {
                //criar emocao na interface
                Label label = new Label(subject+" "+emotion_name+" "+action+" "+target+" "+intensity);
                currentActiveEmotions.put(currentEmotion, label);
                emotionsBox.getChildren().add(label);
            }
            //System.err.println("Active Emotion: "+emotion_name+" Intensity: "+intensity+" Who is Feeling: "+agentName+" Against who: "+direction.GetFirstLiteral().getName());
            //System.err.println("Felt on Action: "+action+" Subject: "+subject+" target: "+target);
        }
        dataModel.setMoodValue("Mood: "+Float.toString(mood));
        moodBar.setProgress(mood);
    }

    public void initializeGoals(){
        AgentCore ag = agents.get(this.getThreadByName(agentName));
        GoalLibrary goalLibrary = ag.getGoalLibrary();
        ListIterator<Goal> currentGoals = goalLibrary.GetGoals();

        for(; currentGoals.hasNext(); )
        {
            Goal goal = currentGoals.next();
            String status = goal.GenerateGoalStatus("");
            goalsBox.getChildren().add(new Label(status));
        }

    }

    public void updateIntentions(){
        AgentCore ag = agents.get(this.getThreadByName(agentName));
        DeliberativeComponent dc = (DeliberativeComponent) ag.getComponent("Deliberative");
        Iterator<Intention> currentIntentions = dc.getIntentionsIterator();
        for (; currentIntentions.hasNext(); )
        {
            Intention intention = currentIntentions.next();
            Float success = new Float(intention.getGoal().GetImportanceOfSuccess(ag));
            Float failure = new Float(intention.getGoal().GetImportanceOfFailure(ag));
            Integer n_plans = new Integer(intention.NumberOfAlternativePlans());
            Float prob = new Float(intention.GetProbability(ag));
            String name = intention.getGoal().getName().GetFirstLiteral().getName();
            String currentIntention = name;
            boolean edited = false;
            for(String s : currentActiveIntentions.keySet())
            {
                if(s.equals(currentIntention))
                {
                    //editar valor na interface
                    Label label = currentActiveIntentions.get(currentIntention);
                    label.setText(name+" "+success+" "+failure+" "+n_plans+" "+prob);
                    edited = true;
                    break;
                }
            }
            if(!edited)
            {
                //criar emocao na interface
                Label label = new Label(name+" "+success+" "+failure+" "+n_plans+" "+prob);
                currentActiveIntentions.put(currentIntention, label);
                intentionsBox.getChildren().add(label);
            }
        }
    }

    public void initializeAgent(String[] args) throws ParserConfigurationException, SAXException, IOException, UnknownGoalException, ActionsParsingException, GoalLibParsingException
    {
        AgentCore aG = initializeAgentCore(args);
        ArrayList<String> extraFiles = new ArrayList<String>();
        String cultureFile = ConfigurationManager.getMindPath() + ConfigurationManager.getOptionalConfigurationValue("cultureName") + ".xml"; //getting culture from the scenario .xml
        String role = ConfigurationManager.getAgentProperties().get("role");
        String relationsFile = ConfigurationManager.getRolePath() + role + "/" + role + ".xml";

        if (!aG.getAgentLoad())
        {
            if(cultureFile != null)
                extraFiles.add(cultureFile);

            //FAtiMA Light is always loaded
            aG.addComponent(new ReactiveComponent());
            aG.addComponent(new OCCAffectDerivationComponent());
            aG.addComponent(new DeliberativeComponent());
            //FAtiMA Advanced Components
            for(int i = 3; i < args.length; i++)
            {
                if(args[i].equals(FAtiMA.motivationalSystem.MotivationalComponent.NAME))
                    aG.addComponent(new FAtiMA.motivationalSystem.MotivationalComponent(extraFiles));
                else if(args[i].equals("MaslowHierarchy"))
                    aG.addComponent(new FAtiMA.maslowHierarchyOfNeeds.MotivationalComponent(extraFiles));
                else if(args[i].equals(SocialRelationsComponent.NAME))
                    aG.addComponent(new SocialRelationsComponent(relationsFile, extraFiles));
                else if(args[i].equals(AdvancedMemoryComponent.NAME))
                    aG.addComponent(new AdvancedMemoryComponent());
                else if(args[i].equals(EmotionalIntelligence.NAME))
                    aG.addComponent(new EmotionalIntelligence(extraFiles));
                else if(args[i].equals(ToMComponent.NAME))
                    aG.addComponent(new ToMComponent(ConfigurationManager.getName()));
                else if(args[i].equals(CulturalDimensionsComponent.NAME)) //if added need ToM as well
                    aG.addComponent(new CulturalDimensionsComponent(cultureFile));
                else if(args[i].equals(EmpathyComponent.NAME))
                    aG.addComponent(new EmpathyComponent());
            }

//			if(scenario.equals("ScenarioWithDrives"))
//			{
//				aG.addComponent(new MotivationalComponent(extraFiles));
//				String relationsFile = ConfigurationManager.getRolePath() + "GuilhasDrives/GuilhasDrives.xml";
//				aG.addComponent(new SocialRelationsComponent(relationsFile, extraFiles));
//			}
//			else if(scenario.equals("MaslowScenarioWithDrives"))
//			{
//				aG.addComponent(new FAtiMA.maslowHierarchyOfNeeds.MotivationalComponent(extraFiles));
//				String relationsFile = ConfigurationManager.getRolePath() + "MaslowDrives/MaslowDrives.xml";
//				aG.addComponent(new SocialRelationsComponent(relationsFile, extraFiles));
//				aG.addComponent(new AdvancedMemoryComponent());
//				aG.addComponent(new EmotionalIntelligence(extraFiles));
//			}
            //aG.addComponent(new SocialRelationsComponent(relationsFile, extraFiles));
            //aG.addComponent(new ToMComponent(ConfigurationManager.getName()));
            //aG.addComponent(new CulturalDimensionsComponent(cultureFile));
            //aG.addComponent(new AdvancedMemoryComponent());
            //aG.addComponent(new EmotionalIntelligence(extraFiles));

        }

        Thread thread = new Thread()
        {
            public void run() {
                aG.StartAgent();
            }
        };
        thread.setName(aG.getName());
        agents.put(thread, aG);

        thread.start();
    }

    private AgentCore initializeAgentCore(String args[]) throws ParserConfigurationException, SAXException, IOException, UnknownGoalException, ActionsParsingException, GoalLibParsingException {
        if(args.length < 3){
            System.err.println("ERROR - expecting 3 arguments at least: Scenarios File, Scenario Name, and Agent Name");
            System.exit(1);
        }

        String scenarioFile = args[0];
        String scenarioName = args[1];
        String agentName = args[2];

        FAtiMA.Core.AgentCore agent = new AgentCore(agentName);
        agent.initialize(scenarioFile,scenarioName,agentName);

        return agent;
    }

    private String[] addArray(String[] first, String[] second) {
        List<String> both = new ArrayList<String>(first.length + second.length);
        Collections.addAll(both, first);
        Collections.addAll(both, second);
        return both.toArray(new String[both.size()]);
    }

    public Thread getThreadByName(String threadName) {
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().equals(threadName)) return t;
        }
        return null;
    }

    private void ParseFile(String name) {
        byte[] buffer = new byte[buffsize];
        String data = "";
        int readCharacters;

        FileInputStream f;

        try {
            f = new FileInputStream(name);
            while((readCharacters=f.read(buffer))>0) {
                data = data + new String(buffer,0,readCharacters);
            }
        }catch (FileNotFoundException e) {
            System.out.println(name + "(Error, the system cannot find this options file)");
            System.exit(-1);
        }
        catch (IOException e) {
            System.out.println(name + "(Error while reading this options file)");
            System.exit(-1);
        }

        StringTokenizer st = new StringTokenizer(data,"\r\n");
        //choiceBoxEnv.getItems().clear();
        while(st.hasMoreTokens())
            choiceBoxEnv.getItems().add(st.nextToken());
    }

    private void takeAction() {
        String aux_action = (String) choiceBoxEnv.getSelectionModel().getSelectedItem();
        System.err.println(aux_action);
        StringTokenizer st = new StringTokenizer(aux_action, " ");
        if (st.countTokens() > 1) {
            String subject = st.nextToken();
            String action = st.nextToken();
            while (st.hasMoreTokens())
                action = action + " " + st.nextToken();
            this.UpdateActionEffects(subject, ConvertToActionName(action));
        }

        String perception = "ACTION-FINISHED " + aux_action;
        worldTest.WriteLine(aux_action);
        worldTest.SendPerceptionToAll(perception);
    }

    private Name ConvertToActionName(String action)
    {
        StringTokenizer st = new StringTokenizer(action," ");
        String actionName = st.nextToken() + "(";
        while(st.hasMoreTokens())
        {
            actionName += st.nextToken() + ",";
        }
        if(actionName.endsWith(","))
        {
            actionName = actionName.substring(0,actionName.length()-1);
        }

        actionName = actionName + ")";
        return Name.ParseName(actionName);
    }

    private void UpdateActionEffects(String subject, Name action)
    {
        ArrayList<Substitution> bindings;
        Step gStep;

        for(Step s : worldTest.GetActions())
        {
            bindings = new ArrayList<Substitution>();
            bindings.add(new Substitution(new Symbol("[SELF]"), new Symbol(subject)));
            bindings.add(new Substitution(new Symbol("[AGENT]"), new Symbol(subject)));
            if(Unifier.Unify(s.getName(),action, bindings))
            {
                gStep = (Step) s.clone();
                gStep.MakeGround(bindings);
                PropertiesChanged(gStep.getEffects());
            }
        }
    }

    private void PropertiesChanged(ArrayList<Effect> effects)
    {
        Condition c;
        String msg;

        for(Effect e : effects)
        {
            c = e.GetEffect();
            String name = c.getName().toString();
            if(!name.startsWith("EVENT") && !name.startsWith("SpeechContext"))
            {
                if(e.GetProbability(null) > _r.nextFloat())
                {
                    msg = "PROPERTY-CHANGED " + c.getToM() + " " + name + " " + c.GetValue();
                    worldTest.WriteLine(msg);
                    worldTest.SendPerceptionToAll(msg);
                }
            }
        }
    }

}
