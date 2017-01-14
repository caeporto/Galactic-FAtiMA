import FAtiMA.Core.exceptions.ActionsParsingException;
import FAtiMA.Core.exceptions.GoalLibParsingException;
import FAtiMA.Core.exceptions.UnknownGoalException;
import FAtiMA.Core.util.ConfigurationManager;
import FAtiMA.DeliberativeComponent.DeliberativeComponent;
import FAtiMA.OCCAffectDerivation.OCCAffectDerivationComponent;
import FAtiMA.ReactiveComponent.ReactiveComponent;
import FAtiMA.ToM.ToMComponent;
import FAtiMA.advancedMemoryComponent.AdvancedMemoryComponent;
import FAtiMA.culture.CulturalDimensionsComponent;
import FAtiMA.emotionalIntelligence.EmotionalIntelligence;
import FAtiMA.empathy.EmpathyComponent;
import FAtiMA.socialRelations.SocialRelationsComponent;
import javafx.animation.Animation;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import FAtiMA.Core.AgentCore;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

/**
 * Created by doyle on 02/01/2017.
 */

public class Controller {

    public static final HashMap<Thread, AgentCore> agents = new HashMap();

    public static final String[] ag_modules = {"MaslowHierarchy", "MotivationalState", "SocialRelations", "AdvancedMemory", "EmotionalIntelligence", "ToM", "CulturalDimensionsComponent", "EmpathyComponent"};

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

//        AgentCore ag = AgentLauncher.agents.get(0);
//        MotivationalComponent component = (MotivationalComponent) ag.getComponent("MaslowHierarchy");
//        Motivator[] motivators = component.getMotivators(MotivatorHierarchy.PHYSIOLOGY);
//        Motivator mot = motivators[0];
//        Float intensity = mot.GetIntensity();
//        prototypeModel.setDadoTeste(Float.toString(intensity));
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

}
