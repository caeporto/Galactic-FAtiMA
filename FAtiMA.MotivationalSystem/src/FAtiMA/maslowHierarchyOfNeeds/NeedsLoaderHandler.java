package FAtiMA.maslowHierarchyOfNeeds;

import org.xml.sax.Attributes;

import FAtiMA.Core.AgentModel;
import FAtiMA.Core.util.AgentLogger;
import FAtiMA.Core.util.Constants;
import FAtiMA.Core.util.parsers.ReflectXMLHandler;
import FAtiMA.Core.wellFormedNames.Substitution;
import FAtiMA.Core.wellFormedNames.Symbol;
import FAtiMA.maslowHierarchyOfNeeds.EffectType;
import FAtiMA.maslowHierarchyOfNeeds.InvalidMotivatorTypeException;
import FAtiMA.maslowHierarchyOfNeeds.MotivationalComponent;
import FAtiMA.maslowHierarchyOfNeeds.Motivator;
//import FAtiMA.maslowHierarchyOfNeeds.MotivatorHierarchy;

public class NeedsLoaderHandler extends ReflectXMLHandler {
	
	AgentModel _agent;
	String _currentGoalKey;
	String _currentStepKey;
		

	//###################
	//Nao precisa o 2 argumento, pra que esta aqui??!
	public NeedsLoaderHandler(AgentModel agent, MotivationalComponent motivationalState){
		this._agent = agent;
	}
	
	public void MotivationalParameter(Attributes attributes) throws InvalidMotivatorTypeException {
		String hierarchy;
		String motivation;
		short type;

		hierarchy = attributes.getValue("hierarchy");
		if(hierarchy.equals(PhysiologyNeeds.getHierarchy())){
			motivation = attributes.getValue("motivator");
			if(motivation != null)
			{
				for(short i = 0; i < PhysiologyNeeds.getNeeds().length; i++){
					String aux_motivation = PhysiologyNeeds.getNeeds()[i];
					if(motivation.equals(aux_motivation)){
						MotivationalComponent ms = (MotivationalComponent)_agent.getComponent(MotivationalComponent.NAME);
						type = i;
						ms.AddMotivator(MotivatorHierarchy.PHYSIOLOGY, new Motivator(hierarchy, motivation, type,
								new Float(attributes.getValue("decayFactor")).floatValue(),
								new Float(attributes.getValue("weight")).floatValue(),
								new Float(attributes.getValue("intensity")).floatValue()));
						AgentLogger.GetInstance().logAndPrint("Physiology Motivator found: " + type);
						break;
					}
				}
			}
			else
			{
				Float f = new Float(attributes.getValue("prepotency"));
				if(f != null)
				{
					MotivationalComponent ms = (MotivationalComponent)_agent.getComponent(MotivationalComponent.NAME);
					ms._physiologicalPrepotency = f;
				}
			}
		}
		else if(hierarchy.equals(SafetyNeeds.getHierarchy())){
			motivation = attributes.getValue("motivator");
			if(motivation != null)
			{
				for(short i = 0; i < SafetyNeeds.getNeeds().length; i++){
					String aux_motivation = SafetyNeeds.getNeeds()[i];
					if(motivation.equals(aux_motivation)){
						MotivationalComponent ms = (MotivationalComponent)_agent.getComponent(MotivationalComponent.NAME);
						type = i;
						ms.AddMotivator(MotivatorHierarchy.SAFENESS, new Motivator(hierarchy, motivation, type,
								new Float(attributes.getValue("decayFactor")).floatValue(),
								new Float(attributes.getValue("weight")).floatValue(),
								new Float(attributes.getValue("intensity")).floatValue()));
						AgentLogger.GetInstance().logAndPrint("Safety Motivator found: " + type);
						break;
					}
				}
			}
			else
			{
				Float f = new Float(attributes.getValue("prepotency"));
				if(f != null)
				{
					MotivationalComponent ms = (MotivationalComponent)_agent.getComponent(MotivationalComponent.NAME);
					ms._safetyPrepotency = f;
				}
			}
		}
		else if(hierarchy.equals(RelationshipNeeds.getHierarchy())){
			motivation = attributes.getValue("motivator");
			if(motivation != null)
			{
				for(short i = 0; i < RelationshipNeeds.getNeeds().length; i++){
					String aux_motivation = RelationshipNeeds.getNeeds()[i];
					if(motivation.equals(aux_motivation)){
						MotivationalComponent ms = (MotivationalComponent)_agent.getComponent(MotivationalComponent.NAME);
						type = i;
						ms.AddMotivator(MotivatorHierarchy.RELATIONSHIPS, new Motivator(hierarchy, motivation, type,
								new Float(attributes.getValue("decayFactor")).floatValue(),
								new Float(attributes.getValue("weight")).floatValue(),
								new Float(attributes.getValue("intensity")).floatValue()));
						AgentLogger.GetInstance().logAndPrint("Relationships Motivator found: " + type);
						break;
					}
				}
			}
			else
			{
				Float f = new Float(attributes.getValue("prepotency"));
				if(f != null)
				{
					MotivationalComponent ms = (MotivationalComponent)_agent.getComponent(MotivationalComponent.NAME);
					ms._relationshipsPrepotency = f;
				}
			}
		}
		else if(hierarchy.equals(EsteemNeeds.getHierarchy())){
			motivation = attributes.getValue("motivator");
			if(motivation != null)
			{
				for(short i = 0; i < EsteemNeeds.getNeeds().length; i++){
					String aux_motivation = EsteemNeeds.getNeeds()[i];
					if(motivation.equals(aux_motivation)){
						MotivationalComponent ms = (MotivationalComponent)_agent.getComponent(MotivationalComponent.NAME);
						type = i;
						ms.AddMotivator(MotivatorHierarchy.ESTEEM, new Motivator(hierarchy, motivation, type,
								new Float(attributes.getValue("decayFactor")).floatValue(),
								new Float(attributes.getValue("weight")).floatValue(),
								new Float(attributes.getValue("intensity")).floatValue()));
						AgentLogger.GetInstance().logAndPrint("Esteem Motivator found: " + type);
						break;
					}
				}
			}
			else
			{
				Float f = new Float(attributes.getValue("prepotency"));
				if(f != null)
				{
					MotivationalComponent ms = (MotivationalComponent)_agent.getComponent(MotivationalComponent.NAME);
					ms._esteemPrepotency = f;
				}
			}
		}
		else if(hierarchy.equals(RealizationNeeds.getHierarchy())){
			motivation = attributes.getValue("motivator");
			for(short i = 0; i < RealizationNeeds.getNeeds().length; i++){
				String aux_motivation = RealizationNeeds.getNeeds()[i];
				if(motivation.equals(aux_motivation)){
					MotivationalComponent ms = (MotivationalComponent)_agent.getComponent(MotivationalComponent.NAME);
					type = i;
					ms.AddMotivator(MotivatorHierarchy.REALIZATION, new Motivator(hierarchy, motivation, type,
							new Float(attributes.getValue("decayFactor")).floatValue(),
							new Float(attributes.getValue("weight")).floatValue(),
							new Float(attributes.getValue("intensity")).floatValue()));
					AgentLogger.GetInstance().logAndPrint("Realization Motivator found: " + type);
					break;
				}
			}
		}
	}
	
	public void Goal(Attributes attributes) {
    	_currentGoalKey = attributes.getValue("name");
    }
	
	public void ActivePursuitGoal(Attributes attributes) {
    	_currentGoalKey = attributes.getValue("name");
    }
	public void InterestGoal(Attributes attributes)
	{
		_currentGoalKey = attributes.getValue("name");
	}
	
	public void Action(Attributes attributes){
	 	_currentStepKey = attributes.getValue("name");	
	}
	
	public void Motivator(Attributes attributes)
	{
		MotivationalComponent motivComp = (MotivationalComponent)_agent.getComponent(MotivationalComponent.NAME);
		
		String hierarchy = attributes.getValue("hierarchy");
		String driveName = attributes.getValue("drive");
		String value = attributes.getValue("value");
		String target = attributes.getValue("target");

		if(driveName != null && _currentStepKey != null){
			motivComp.addActionEffectsOnDrive(_currentStepKey, hierarchy, driveName, new Symbol(target), Float.parseFloat(value));
		}
	}
	
	public void OnSelect(Attributes attributes)
	{
		this.setGoalExpectedEffectOnDrive(attributes, EffectType.ON_SELECT);
	}

	public void OnIgnore(Attributes attributes)
	{
		this.setGoalExpectedEffectOnDrive(attributes, EffectType.ON_IGNORE);
	}
	
	private void setGoalExpectedEffectOnDrive(Attributes attributes, short effectType){
		MotivationalComponent ms = (MotivationalComponent)_agent.getComponent(MotivationalComponent.NAME);
		
		String hierarchy = attributes.getValue("hierarchy");
		String driveName = attributes.getValue("drive");
		String value = attributes.getValue("value");
		String target = attributes.getValue("target");
		Symbol t = new Symbol(target);
		Substitution self = new Substitution(new Symbol("[SELF]"), new Symbol(Constants.SELF));
		t.MakeGround(self);

		if(driveName != null && _currentGoalKey != null){
			ms.addExpectedGoalEffectOnDrive(_currentGoalKey, effectType, hierarchy, driveName, t, Float.parseFloat(value));
		}
	}
}
