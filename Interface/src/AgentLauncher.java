import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import FAtiMA.Core.AgentCore;
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
import FAtiMA.socialRelations.SocialRelationsComponent;
import FAtiMA.empathy.EmpathyComponent;

/**
 * Created by caetanoportodasilva on 11/01/17.
 */
public class AgentLauncher {

    public static final ArrayList<AgentCore> agents = new ArrayList<>(10);

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, UnknownGoalException, ActionsParsingException, GoalLibParsingException {
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

        agents.add(aG);

        aG.StartAgent();
    }

    static private AgentCore initializeAgentCore(String args[]) throws ParserConfigurationException, SAXException, IOException, UnknownGoalException, ActionsParsingException, GoalLibParsingException {
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
}
