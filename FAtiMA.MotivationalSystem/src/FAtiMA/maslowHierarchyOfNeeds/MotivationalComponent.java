package FAtiMA.maslowHierarchyOfNeeds;

/*
 * MotivatorState.java - Represents the character's motivational state
 */

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import FAtiMA.Core.AgentModel;
import FAtiMA.Core.AgentSimulationTime;
import FAtiMA.Core.Display.AgentDisplayPanel;
import FAtiMA.Core.componentTypes.IAppraisalDerivationComponent;
import FAtiMA.Core.componentTypes.IComponent;
import FAtiMA.Core.componentTypes.IModelOfOtherComponent;
import FAtiMA.Core.emotionalState.AppraisalFrame;
import FAtiMA.Core.goals.ActivePursuitGoal;
import FAtiMA.Core.plans.Step;
import FAtiMA.Core.sensorEffector.Event;
import FAtiMA.Core.util.AgentLogger;
import FAtiMA.Core.util.ConfigurationManager;
import FAtiMA.Core.util.Constants;
import FAtiMA.Core.wellFormedNames.Name;
import FAtiMA.Core.wellFormedNames.Substitution;
import FAtiMA.Core.wellFormedNames.SubstitutionSet;
import FAtiMA.Core.wellFormedNames.Symbol;
import FAtiMA.Core.wellFormedNames.Unifier;
import FAtiMA.DeliberativeComponent.DeliberativeComponent;
import FAtiMA.DeliberativeComponent.IProbabilityStrategy;
import FAtiMA.DeliberativeComponent.IUtilityStrategy;
import FAtiMA.DeliberativeComponent.Intention;
import FAtiMA.DeliberativeComponent.strategies.IActionFailureStrategy;
import FAtiMA.DeliberativeComponent.strategies.IExpectedUtilityStrategy;
import FAtiMA.DeliberativeComponent.strategies.IGoalFailureStrategy;
import FAtiMA.DeliberativeComponent.strategies.IGoalSuccessStrategy;
import FAtiMA.OCCAffectDerivation.OCCAppraisalVariables;

/**
 * Implements the character's motivational state.
 * 
 * @author Meiyii Lim, Samuel Mascarenhas
 */

public class MotivationalComponent implements Serializable, Cloneable,
		IAppraisalDerivationComponent, IModelOfOtherComponent,
		IExpectedUtilityStrategy, IProbabilityStrategy, IUtilityStrategy,
		IGoalSuccessStrategy, IGoalFailureStrategy, IActionFailureStrategy {

	private static final float EFFECT_ON_DRIVES_WEIGHT = 0.5f;
	private static final long serialVersionUID = 1L;
	public static final String NAME = "MotivationalState";
	private static final float MAX_INTENSITY = 10;
	private static final float MIN_INTENSITY = 0;

	public static double determineQuadraticNeedVariation(float currentLevel,
			float deviation) {
		double result = 0;
		float finalLevel;
		double currentLevelStr;
		double finalLevelStr;

		finalLevel = currentLevel + deviation;
		finalLevel = Math.min(finalLevel, MAX_INTENSITY);
		finalLevel = Math.max(finalLevel, MIN_INTENSITY);

		currentLevelStr = Math.pow(MAX_INTENSITY - currentLevel, 2);
		finalLevelStr = Math.pow(MAX_INTENSITY - finalLevel, 2);

		result = -(finalLevelStr - currentLevelStr);
		return result;
	}

	protected Motivator[] _physiologicalMotivators;
	protected Motivator[] _safetyMotivators;
	protected Motivator[] _relationshipMotivators;
	protected Motivator[] _esteemMotivators;
	protected Motivator[] _realizationMotivators;
	
	//if any of the needs is above this threshold, other needs 
	//will take over and make the agent strive for them
	//conversely if any of the needs is below this threshold
	//then the needs from a level below in the hierarchy take over
	protected Class<? extends Needs> _prepotencyHierarchyLevel;
	protected Float _physiologicalPrepotency;
	protected Float _safetyPrepotency;
	protected Float _relationshipsPrepotency;
	protected Float _esteemPrepotency;


	// protected Hashtable<String,Motivator[]> _otherAgentsMotivators;
	protected long _lastTime;
	protected int _goalTried;

	protected int _goalSucceeded;
	protected HashMap<String, Float> _appraisals;
	protected HashMap<String, ExpectedGoalEffectsOnDrives> _goalEffectsOnDrives;

	protected HashMap<String, ActionEffectsOnDrives> _actionEffectsOnDrives;

	private ArrayList<String> _parsingFiles;

	/**
	 * Creates an empty MotivationalState
	 */
	public MotivationalComponent(ArrayList<String> extraFiles) {
		_physiologicalMotivators = new Motivator[PhysiologyNeeds.getNeeds().length];
		_safetyMotivators = new Motivator[SafetyNeeds.getNeeds().length];
		_relationshipMotivators = new Motivator[RelationshipNeeds.getNeeds().length];
		_esteemMotivators = new Motivator[EsteemNeeds.getNeeds().length];
		_realizationMotivators = new Motivator[RealizationNeeds.getNeeds().length];
		_prepotencyHierarchyLevel = PhysiologyNeeds.class;

		_goalTried = 0;
		_goalSucceeded = 0;
		_lastTime = AgentSimulationTime.GetInstance().Time();
		_appraisals = new HashMap<String, Float>();
		_goalEffectsOnDrives = new HashMap<String, ExpectedGoalEffectsOnDrives>();
		_actionEffectsOnDrives = new HashMap<String, ActionEffectsOnDrives>();

		_parsingFiles = new ArrayList<String>();
		_parsingFiles.add(ConfigurationManager.getGoalsFile());
		_parsingFiles.add(ConfigurationManager.getPersonalityFile());
		_parsingFiles.add(ConfigurationManager.getActionsFile());
		_parsingFiles.addAll(extraFiles);
	}

	private void LoadNeeds(AgentModel am) {
		AgentLogger.GetInstance().log("LOADING Needs: ");
		NeedsLoaderHandler needsLoader = new NeedsLoaderHandler(am, this);

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();

			for (String file : _parsingFiles) {
				parser.parse(new File(file), needsLoader);
			}

		} catch (Exception e) {
			throw new RuntimeException("Error on Loading Needs from XML Files:"
					+ e);
		}
	}

	private float newCompetence(boolean succeed) {
		float alpha = 0.25f;
		int value = 0;
		float newCompetence;
		if (succeed) {
			value = 10;
		}

		Motivator competenceM = _esteemMotivators[EsteemNeeds.Achievement.getValue()];

		newCompetence = alpha * value + (1 - alpha)
				* competenceM.GetIntensity();

		if (newCompetence < 1) {
			newCompetence = 1;
		}

		return newCompetence;
	}

	public void addActionEffectsOnDrive(String action, String hierarchy, String driveName,
			Symbol target, float value) {
		ActionEffectsOnDrives effects;
		if (!_actionEffectsOnDrives.containsKey(action)) {
			effects = new ActionEffectsOnDrives(action);
			//_actionEffectsOnDrives.put(action, effects);
		} else {
			effects = _actionEffectsOnDrives.get(action);
		}
		effects.AddEffect(new EffectOnDrive(EffectType.ON_PERFORMANCE,
				hierarchy, driveName, target, value));
		_actionEffectsOnDrives.put(action, effects);
	}

	public void addExpectedGoalEffectOnDrive(String goal, short effectType,
			String hierarchy, String driveName, Symbol target, float value) {
		ExpectedGoalEffectsOnDrives effects;
		if (!_goalEffectsOnDrives.containsKey(goal)) {
			effects = new ExpectedGoalEffectsOnDrives(goal);
			//_goalEffectsOnDrives.put(goal, effects);
		} else {
			effects = _goalEffectsOnDrives.get(goal);
		}
		effects.AddEffect(new EffectOnDrive(effectType, hierarchy, driveName, target,
				value));
		_goalEffectsOnDrives.put(goal, effects);
	}

	/**
	 * Adds a motivator to the MotivationalState
	 */
	public void AddMotivator(Short hierarchy, Motivator motivator) {
		if(hierarchy == MotivatorHierarchy.PHYSIOLOGY)
			_physiologicalMotivators[motivator.GetType()] = new Motivator(motivator);
		else if(hierarchy == MotivatorHierarchy.SAFENESS)
			_safetyMotivators[motivator.GetType()] = new Motivator(motivator);
		else if(hierarchy == MotivatorHierarchy.RELATIONSHIPS)
			_relationshipMotivators[motivator.GetType()] = new Motivator(motivator);
		else if(hierarchy == MotivatorHierarchy.ESTEEM)
			_esteemMotivators[motivator.GetType()] = new Motivator(motivator);
		else if(hierarchy == MotivatorHierarchy.REALIZATION)
			_realizationMotivators[motivator.GetType()] = new Motivator(motivator);
	}

	@Override
	public void appraisal(AgentModel am, Event e, AppraisalFrame as) {
		Float desirability = _appraisals.get(e.toString());
		if (desirability != null) {
			as.SetAppraisalVariable(NAME, (short) 8,
					OCCAppraisalVariables.DESIRABILITY.name(),
					desirability.floatValue());
		}
		_appraisals.remove(e.toString());
	}

	@Override
	public AgentDisplayPanel createDisplayPanel(AgentModel am) {
		return new NeedsPanel(this);
	}

	@Override
	public IComponent createModelOfOther() {
		MotivationalComponent ms = new MotivationalComponent(
				new ArrayList<String>());
		Motivator m2;
		
		if(_physiologicalPrepotency != null)
			ms._physiologicalPrepotency = _physiologicalPrepotency;
		for (Motivator m : _physiologicalMotivators) {
			if(m != null)
			{
				m2 = new Motivator(m);
				m2.SetIntensity(5);
				ms.AddMotivator(MotivatorHierarchy.PHYSIOLOGY, m2);
			}
		}
		
		if(_safetyPrepotency != null)
			ms._safetyPrepotency = _safetyPrepotency;
		for (Motivator m : _safetyMotivators) {
			if(m != null)
			{
				m2 = new Motivator(m);
				m2.SetIntensity(5);
				ms.AddMotivator(MotivatorHierarchy.SAFENESS, m2);
			}
		}
		
		if(_relationshipsPrepotency != null)
			ms._relationshipsPrepotency = _relationshipsPrepotency;
		for (Motivator m : _relationshipMotivators) {
			if(m != null)
			{
				m2 = new Motivator(m);
				m2.SetIntensity(5);
				ms.AddMotivator(MotivatorHierarchy.RELATIONSHIPS, m2);
			}
		}
		
		if(_esteemPrepotency != null)
			ms._esteemPrepotency = _esteemPrepotency;
		for (Motivator m : _esteemMotivators) {
			if(m != null)
			{
				m2 = new Motivator(m);
				m2.SetIntensity(5);
				ms.AddMotivator(MotivatorHierarchy.ESTEEM, m2);
			}
		}
		
		for (Motivator m : _realizationMotivators) {
			if(m != null)
			{
				m2 = new Motivator(m);
				m2.SetIntensity(5);
				ms.AddMotivator(MotivatorHierarchy.REALIZATION, m2);
			}
		}

		ms._actionEffectsOnDrives = (HashMap<String, ActionEffectsOnDrives>) _actionEffectsOnDrives
				.clone();
		ms._goalEffectsOnDrives = (HashMap<String, ExpectedGoalEffectsOnDrives>) _goalEffectsOnDrives
				.clone();

		return ms;
	}

	public float getCompetence(AgentModel am, ActivePursuitGoal g) {
		float generalCompetence = GetIntensity(MotivatorHierarchy.ESTEEM, EsteemNeeds.Achievement.getValue()) / 10;
		Float probability = g.GetProbability(am);

		if (probability != null) {
			return (generalCompetence + probability.floatValue()) / 2;
		} else {
			// if there is no knowledge about the goal probability, the goal was
			// never executed before
			// however, the agent assumes that he will be successful in
			// achieving it
			return (generalCompetence + 1) / 2;
		}
	}

	@Override
	public String[] getComponentDependencies() {
		String[] dependencies = { DeliberativeComponent.NAME };
		return dependencies;
	}

	public float getContributionToNeeds(AgentModel am, ActivePursuitGoal g) {
		float result = 0;
		float expectedContribution;
		float currentIntensity = 0;
		float auxMultiplier = 1;
		boolean test;

		try {
			result = 0;

			ExpectedGoalEffectsOnDrives effects = _goalEffectsOnDrives.get(g
					.getKey());
			if (effects == null)
				return 0;
			for (EffectOnDrive e : effects.getEffects()) {
				if (am.isSelf()) {
					test = e.getTarget().toString().equals(Constants.SELF);
				} else {
					Symbol target = (Symbol) e.getTarget().clone();
					target.MakeGround(g.getAppliedSubstitutions());
					test = target.toString().equals(am.getName());
				}

				if (test) {
					expectedContribution = e.getValue();
					short hierarchy = MotivatorHierarchy.ParseHierarchyLevel(e.getHierarchy());
					short need = MotivatorHierarchy.ParseNeedInHierarchy(hierarchy, e.getDriveName());
					currentIntensity = GetIntensity(hierarchy, need);
					if (e.getType() == EffectType.ON_SELECT) {
						auxMultiplier = 1;
					} else if (e.getType() == EffectType.ON_IGNORE) {
						auxMultiplier = -1;
					}

					result += auxMultiplier
							* MotivationalComponent
									.determineQuadraticNeedVariation(
											currentIntensity,
											expectedContribution) * 0.1f;
				}
			}
			
			if(_esteemMotivators[EsteemNeeds.Achievement.getValue()] != null)
			{
				float currentCompetenceIntensity = GetIntensity(MotivatorHierarchy.ESTEEM, EsteemNeeds.Achievement.getValue());
				float expectedCompetenceContribution = PredictCompetenceChange(true);
				result += MotivationalComponent.determineQuadraticNeedVariation(
						currentCompetenceIntensity, expectedCompetenceContribution) * 0.1f;
			}
			
			if(_safetyMotivators[SafetyNeeds.PsychologicalSafety.getValue()] != null)
			{
				float currentUncertaintyIntensity = GetIntensity(MotivatorHierarchy.SAFENESS, SafetyNeeds.PsychologicalSafety.getValue());
				// expected error assuming that the goal is successful
				float expectedError = 1 - g.getProbability(am);
				float currentError = g.getUncertainty(am);
				float expectedUncertaintyContribution = 10 * (currentError - expectedError);
				result += MotivationalComponent.determineQuadraticNeedVariation(
						currentUncertaintyIntensity,
						expectedUncertaintyContribution) * 0.1f;
			}

		} catch (InvalidHierarchyMotivatorTypeException e) {
			AgentLogger.GetInstance().log("EXCEPTION:" + e);
			e.printStackTrace();
		}

		return result;
	}

	public float getExpectedUtility(AgentModel am, ActivePursuitGoal g) {
		DeliberativeComponent dc = (DeliberativeComponent) am
				.getComponent(DeliberativeComponent.NAME);
		float utility = dc.getUtilityStrategy().getUtility(am, g);
		float probability = dc.getProbabilityStrategy().getProbability(am, g);

		float EU = utility * probability * (1 + g.GetGoalUrgency());

		AgentLogger.GetInstance().intermittentLog(
				"Goal: " + g.getName() + " Utilitity: " + utility
						+ " Competence: " + probability + " Urgency: "
						+ g.GetGoalUrgency() + " Total: " + EU);
		return EU;
	}

	public float getExpectedUtility(AgentModel am, Intention i) {
		DeliberativeComponent dc = (DeliberativeComponent) am
				.getComponent(DeliberativeComponent.NAME);

		float utility = dc.getUtilityStrategy().getUtility(am, i.getGoal());
		float probability = dc.getProbabilityStrategy().getProbability(am, i);

		float EU = utility * probability * (1 + i.getGoal().GetGoalUrgency());

		AgentLogger.GetInstance().intermittentLog(
				"Intention: " + i.getGoal().getName() + " Utilitity: "
						+ utility + " Competence: " + probability
						+ " Urgency: " + i.getGoal().GetGoalUrgency()
						+ " Total: " + EU);
		return EU;
	}

	/**
	 * Gets the received motivator's intensity, i.e. the current level of the
	 * motivator
	 * 
	 * @return a float value corresponding to the motivator's intensity
	 */
	public float GetIntensity(short hierarchy, short type) {
		Motivator mot = GetMotivator(hierarchy, type);
		if(mot != null)
			return mot.GetIntensity();
		return 0.0f;
	}

	public Motivator GetMotivator(short hierarchy, short motivatorType) {
		if(MotivatorHierarchy.PHYSIOLOGY == hierarchy)
			return _physiologicalMotivators[motivatorType];
		else if(MotivatorHierarchy.SAFENESS == hierarchy)
			return _safetyMotivators[motivatorType];
		else if(MotivatorHierarchy.RELATIONSHIPS == hierarchy)
			return _relationshipMotivators[motivatorType];
		else if(MotivatorHierarchy.ESTEEM == hierarchy)
			return _esteemMotivators[motivatorType];
		else if(MotivatorHierarchy.REALIZATION == hierarchy)
			return _realizationMotivators[motivatorType];
		else
			return null;
	}

	public Motivator[] getMotivators(short hierarchy) {
		if(MotivatorHierarchy.PHYSIOLOGY == hierarchy)
			return _physiologicalMotivators;
		else if(MotivatorHierarchy.SAFENESS == hierarchy)
			return _safetyMotivators;
		else if(MotivatorHierarchy.RELATIONSHIPS == hierarchy)
			return _relationshipMotivators;
		else if(MotivatorHierarchy.ESTEEM == hierarchy)
			return _esteemMotivators;
		else if(MotivatorHierarchy.REALIZATION == hierarchy)
			return _realizationMotivators;
		else
			return null;
	}

	/**
	 * Gets the motivator's urgency discretizing the need intensity into diffent
	 * categories (very urgent, urgent, not urgent, satisfied)
	 * 
	 * @return a multiplier corresponding to the motivator's urgency
	 */
	public float GetNeedUrgency(String agentName, short hierarchy, short type) {
		if(MotivatorHierarchy.PHYSIOLOGY == hierarchy)
			return _physiologicalMotivators[type].GetNeedUrgency();
		else if(MotivatorHierarchy.SAFENESS == hierarchy)
			return _safetyMotivators[type].GetNeedUrgency();
		else if(MotivatorHierarchy.RELATIONSHIPS == hierarchy)
			return _relationshipMotivators[type].GetNeedUrgency();
		else if(MotivatorHierarchy.ESTEEM == hierarchy)
			return _esteemMotivators[type].GetNeedUrgency();
		else if(MotivatorHierarchy.REALIZATION == hierarchy)
			return _realizationMotivators[type].GetNeedUrgency();
		else
			return 0.0f;
	}

	public float getProbability(AgentModel am, ActivePursuitGoal g) {
		return getCompetence(am, g);
	}

	public float getProbability(AgentModel am, Intention i) {
		return i.GetProbability(am);
	}

	public float getUtility(AgentModel am, ActivePursuitGoal g) {
		return getContributionToNeeds(am, g) * EFFECT_ON_DRIVES_WEIGHT;
	}

	/**
	 * Gets the received motivator's weight, i.e. how important is the motivator
	 * to the agent
	 * 
	 * @return a float value corresponding to the motivator's weight
	 */
	public float GetWeight(short hierarchy, short type) {
		if(MotivatorHierarchy.PHYSIOLOGY == hierarchy)
			return _physiologicalMotivators[type].GetWeight();
		else if(MotivatorHierarchy.SAFENESS == hierarchy)
			return _safetyMotivators[type].GetWeight();
		else if(MotivatorHierarchy.RELATIONSHIPS == hierarchy)
			return _relationshipMotivators[type].GetWeight();
		else if(MotivatorHierarchy.ESTEEM == hierarchy)
			return _esteemMotivators[type].GetWeight();
		else if(MotivatorHierarchy.REALIZATION == hierarchy)
			return _realizationMotivators[type].GetWeight();
		else
			return 0.0f;
	}

	@Override
	public void initialize(AgentModel am) {
		DeliberativeComponent dp = (DeliberativeComponent) am
				.getComponent(DeliberativeComponent.NAME);

		dp.setExpectedUtilityStrategy(this);
		dp.setProbabilityStrategy(this);
		dp.setUtilityStrategy(this);
		dp.addActionFailureStrategy(this);
		dp.addGoalFailureStrategy(this);
		dp.addGoalSuccessStrategy(this);
		LoadNeeds(am);
	}

	@Override
	public void inverseAppraisal(AgentModel am, AppraisalFrame af) {
		// TODO
	}

	@Override
	public String name() {
		return MotivationalComponent.NAME;
	}

	@Override
	public void perceiveActionFailure(AgentModel am, Step a) {
		// System.out.println("Calling UpdateCertainty (other's action: step completed)");
		UpdateCertainty(-a.getProbability(am));
	}

	@Override
	public void perceiveGoalFailure(AgentModel am, ActivePursuitGoal g) {
		// _numberOfGoalsTried++;
		UpdateCompetence(false);

		// observed error = |estimation of success - realsuccess|
		// given that the goal failed, the real success is none and the formula
		// resumes to
		// observed error = estimation of success - 0 (=) estimation of success
		float observedError = g.getProbability(am);
		float previousExpectedError = g.getUncertainty(am);

		float newExpectedError = ActivePursuitGoal.alfa * observedError
				+ (1 - ActivePursuitGoal.alfa) * previousExpectedError;
		float deltaError = newExpectedError - previousExpectedError;
		UpdateCertainty(-deltaError);
		g.setUncertainty(am, newExpectedError);
	}

	@Override
	public void perceiveGoalSuccess(AgentModel am, ActivePursuitGoal g) {

		UpdateCompetence(true);

		// observed error = |realsuccess - estimation of success|
		// given that the goal succeeded, the real success is 1 and the formula
		// resumes to
		// observed error = 1 - estimation of success
		float observedError = 1 - g.getProbability(am);
		float previousExpectedError = g.getUncertainty(am);

		float newExpectedError = ActivePursuitGoal.alfa * observedError
				+ (1 - ActivePursuitGoal.alfa) * previousExpectedError;
		float deltaError = newExpectedError - previousExpectedError;
		UpdateCertainty(-deltaError);
		g.setUncertainty(am, newExpectedError);
	}

	public float PredictCompetenceChange(boolean succeed) {
		Motivator competenceM = _esteemMotivators[EsteemNeeds.Achievement.getValue()];
		return newCompetence(succeed) - competenceM.GetIntensity();
	}

	@Override
	public AppraisalFrame reappraisal(AgentModel am) {
		return null;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
	}

	/**
	 * Converts the motivational state to XML
	 * 
	 * @return a XML String that contains all information in the motivational
	 *         state
	 */
	public String toXml() {
		String result;

		result = "<MotivationalState>";
		for (int i = 0; i < _physiologicalMotivators.length; i++) {
			result = result + _physiologicalMotivators[i].toXml();
		}
		for (int i = 0; i < _safetyMotivators.length; i++) {
			result = result + _safetyMotivators[i].toXml();
		}
		for (int i = 0; i < _relationshipMotivators.length; i++) {
			result = result + _relationshipMotivators[i].toXml();
		}
		for (int i = 0; i < _esteemMotivators.length; i++) {
			result = result + _esteemMotivators[i].toXml();
		}
		for (int i = 0; i < _realizationMotivators.length; i++) {
			result = result + _realizationMotivators[i].toXml();
		}
		result = result + "</MotivationalState>";
		return result;
	}

	@Override
	public void update(AgentModel am, Event e) {
		float result = UpdateMotivators(am, e);
		_appraisals.put(e.toString(), new Float(result));
	}

	@Override
	public void update(AgentModel am, long time) {
		_appraisals.clear();
		if (time >= _lastTime + 1000) {
			_lastTime = time;

			// decay self motivators
			if(_prepotencyHierarchyLevel != PhysiologyNeeds.class)
			{
				for (int i = 0; i < _physiologicalMotivators.length; i++) {
					if(_physiologicalMotivators[i] != null)
						_physiologicalMotivators[i].DecayMotivator();
				}
			}
			else 
			{
				//this is the prepotent level
				for (int i = 0; i < _physiologicalMotivators.length; i++) {
					if(_physiologicalMotivators[i] != null)
						_physiologicalMotivators[i].DecayMotivatorWithPrepotency();
				}
			}
			if(_prepotencyHierarchyLevel != SafetyNeeds.class)
			{
				for (int i = 0; i < _safetyMotivators.length; i++) {
					if(_safetyMotivators[i] != null)
						_safetyMotivators[i].DecayMotivator();
				}
			}
			else
			{
				for (int i = 0; i < _safetyMotivators.length; i++) {
					if(_safetyMotivators[i] != null)
						_safetyMotivators[i].DecayMotivatorWithPrepotency();
				}
			}
			if(_prepotencyHierarchyLevel != RelationshipNeeds.class)
			{
				for (int i = 0; i < _relationshipMotivators.length; i++) {
					if(_relationshipMotivators[i] != null)
						_relationshipMotivators[i].DecayMotivator();
				}
			}
			else
			{
				for (int i = 0; i < _relationshipMotivators.length; i++) {
					if(_relationshipMotivators[i] != null)
						_relationshipMotivators[i].DecayMotivatorWithPrepotency();
				}
			}
			if(_prepotencyHierarchyLevel != EsteemNeeds.class)
			{
				for (int i = 0; i < _esteemMotivators.length; i++) {
					if(_esteemMotivators[i] != null)
						_esteemMotivators[i].DecayMotivator();
				}
			}
			else
			{
				for (int i = 0; i < _esteemMotivators.length; i++) {
					if(_esteemMotivators[i] != null)
						_esteemMotivators[i].DecayMotivatorWithPrepotency();
				}
			}
			if(_prepotencyHierarchyLevel != RealizationNeeds.class)
			{
				for (int i = 0; i < _realizationMotivators.length; i++) {
					if(_realizationMotivators[i] != null)
						_realizationMotivators[i].DecayMotivator();
				}
			}
			else
			{
				for (int i = 0; i < _realizationMotivators.length; i++) {
					if(_realizationMotivators[i] != null)
						_realizationMotivators[i].DecayMotivatorWithPrepotency();
				}
			}
			
			//after decay check if the prepotency level stays the same or not
			updatePrepotencyNeeds();
		}
	}

	/**
	 * Update the agent's certainty value
	 * 
	 * @param expectation
	 *            - ranges from -1 to 1, -1 means complete violation of
	 *            expectation while 1 means complete fulfillment of expectation
	 *            Changed the factor from 10 to 3 (Meiyii)
	 */
	public void UpdateCertainty(float expectation) {
		// System.out.println("Certainty before update" +
		// _selfMotivators[MotivatorType.CERTAINTY].GetIntensity());
		if(GetMotivator(MotivatorHierarchy.SAFENESS, SafetyNeeds.PsychologicalSafety.getValue()) != null)
			_safetyMotivators[SafetyNeeds.PsychologicalSafety.getValue()].UpdateIntensity(expectation * 3);
		// System.out.println("Certainty after update" +
		// _selfMotivators[MotivatorType.CERTAINTY].GetIntensity());
	}
	
	public float getHierarchyIntensity(short hierarchy)
	{
		float sumPrep = 0.0f;
		short needs = 0;
		
		if(hierarchy == MotivatorHierarchy.PHYSIOLOGY)
		{
			for(short i = 0; i < _physiologicalMotivators.length; i++)
			{
				if(_physiologicalMotivators[i] != null)
				{
					sumPrep += _physiologicalMotivators[i].GetIntensity();
					needs++;
				}
			}
		}
		else if(hierarchy == MotivatorHierarchy.SAFENESS)
		{
			for(short i = 0; i < _safetyMotivators.length; i++)
			{
				if(_safetyMotivators[i] != null)
				{
					sumPrep += _safetyMotivators[i].GetIntensity();
					needs++;
				}
			}
		}
		else if(hierarchy == MotivatorHierarchy.RELATIONSHIPS)
		{
			for(short i = 0; i < _relationshipMotivators.length; i++)
			{
				if(_relationshipMotivators[i] != null)
				{
					sumPrep += _relationshipMotivators[i].GetIntensity();
					needs++;
				}
			}
		}
		else if(hierarchy == MotivatorHierarchy.ESTEEM)
		{
			for(short i = 0; i < _esteemMotivators.length; i++)
			{
				if(_esteemMotivators[i] != null)
				{
					sumPrep += _esteemMotivators[i].GetIntensity();
					needs++;
				}
			}
		}
		else if(hierarchy == MotivatorHierarchy.REALIZATION)
		{
			for(short i = 0; i < _realizationMotivators.length; i++)
			{
				if(_realizationMotivators[i] != null)
				{
					sumPrep += _realizationMotivators[i].GetIntensity();
					needs++;
				}
			}
		}
		
		return (needs != 0)? sumPrep/needs : 0.0f;
	}

	/**
	 * Calculates the agent's competence about a goal
	 * 
	 * @param succeed
	 *            - whether a goal has succeeded, true is success, and false is
	 *            failure
	 */
	public void UpdateCompetence(boolean succeed) {
		Motivator competenceM = _esteemMotivators[EsteemNeeds.Achievement.getValue()];
		// System.out.println("Competence before update" +
		// competenceM.());
		if(competenceM != null)
			competenceM.SetIntensity(newCompetence(succeed));
		// System.out.println("Competence after update" +
		// competenceM.GetIntensity());
	}
	
	private Class<? extends Needs> checkBelowHierarchyLevel(Class<? extends Needs> level)
	{
		
		try {
			short n_level = MotivatorHierarchy.ParseHierarchyLevel(level);
			Class<? extends Needs> lowestLevel = null;
			short hierarchyLevel;
			for(hierarchyLevel = --n_level; hierarchyLevel >= 0; hierarchyLevel--){
				float aux = getHierarchyIntensity(hierarchyLevel);
				switch(hierarchyLevel)
				{
					case MotivatorHierarchy.PHYSIOLOGY:
						if(aux < _physiologicalPrepotency)
							lowestLevel = MotivatorHierarchy.ParseHierarchyLevel(hierarchyLevel);
						break;
					case MotivatorHierarchy.SAFENESS:
						if(aux < _safetyPrepotency)
							lowestLevel = MotivatorHierarchy.ParseHierarchyLevel(hierarchyLevel);
						break;
					case MotivatorHierarchy.RELATIONSHIPS:
						if(aux < _relationshipsPrepotency)
							lowestLevel = MotivatorHierarchy.ParseHierarchyLevel(hierarchyLevel);
						break;
					case MotivatorHierarchy.ESTEEM:
						if(aux < _esteemPrepotency)
							lowestLevel = MotivatorHierarchy.ParseHierarchyLevel(hierarchyLevel);
						break;
				}	
			}
			
			return lowestLevel;
			
		} catch (InvalidHierarchyMotivatorTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Updates the current prepotency level active
	 */
	private void updatePrepotencyNeeds()
	{
		if(_prepotencyHierarchyLevel == PhysiologyNeeds.class)
		{
			float sum = 0.0f;
			sum = getHierarchyIntensity(MotivatorHierarchy.PHYSIOLOGY);
			if(sum > 0.0f)
				if(sum >= _physiologicalPrepotency && _safetyPrepotency != null)
					_prepotencyHierarchyLevel = SafetyNeeds.class;
					
		}
		if(_prepotencyHierarchyLevel == SafetyNeeds.class)
		{
			float sum = 0.0f;
			sum = getHierarchyIntensity(MotivatorHierarchy.SAFENESS);
			if(sum > 0.0f)
				if(sum >= _safetyPrepotency && _relationshipsPrepotency != null)
					_prepotencyHierarchyLevel = RelationshipNeeds.class;
				else { //test if the previous level intensity is below the threshold
					Class <? extends Needs> lowestLevel = checkBelowHierarchyLevel(SafetyNeeds.class);
					if(lowestLevel != null)
						_prepotencyHierarchyLevel = lowestLevel;
				}
		}
		if(_prepotencyHierarchyLevel == RelationshipNeeds.class)
		{
			float sum = 0.0f;
			sum = getHierarchyIntensity(MotivatorHierarchy.RELATIONSHIPS);
			if(sum > 0.0f)
				if(sum >= _relationshipsPrepotency && _esteemPrepotency != null)
					_prepotencyHierarchyLevel = EsteemNeeds.class;
				else { //test if the previous level intensity is below the threshold
					Class <? extends Needs> lowestLevel = checkBelowHierarchyLevel(SafetyNeeds.class);
					if(lowestLevel != null)
						_prepotencyHierarchyLevel = lowestLevel;
				}
		}
		if(_prepotencyHierarchyLevel == EsteemNeeds.class)
		{
			float sum = 0.0f;
			sum = getHierarchyIntensity(MotivatorHierarchy.ESTEEM);
			if(sum > 0.0f)
				if(sum >= _esteemPrepotency)
					_prepotencyHierarchyLevel = RealizationNeeds.class;
				else { //test if the previous level intensity is below the threshold
					Class <? extends Needs> lowestLevel = checkBelowHierarchyLevel(SafetyNeeds.class);
					if(lowestLevel != null)
						_prepotencyHierarchyLevel = lowestLevel;
				}
		}
		if(_prepotencyHierarchyLevel == RealizationNeeds.class)
		{
			//test if the previous level intensity is below the threshold
			Class <? extends Needs> lowestLevel = checkBelowHierarchyLevel(RealizationNeeds.class);
			if(lowestLevel != null)
				_prepotencyHierarchyLevel = lowestLevel;
		}
	}

	/**
	 * Updates the intensity of the motivators based on the event received
	 * 
	 * @throws InvalidMotivatorTypeException
	 */
	public float UpdateMotivators(AgentModel am, Event e) {
		ArrayList<Substitution> substitutions;
		Symbol target;
		float deviation = 0;
		double contributionToNeed = 0f;
		float contributionToSelfNeeds = 0f; // used for events performed by the
											// agent

		for (ActionEffectsOnDrives actionEffects : _actionEffectsOnDrives.values()) {
			Name actionName = actionEffects.getActionName();
			substitutions = Unifier.Unify(e.toStepName(), actionName);
			if (substitutions != null) {
				substitutions.add(new Substitution(new Symbol("[AGENT]"),new Symbol(e.GetSubject())));
				for (EffectOnDrive eff : actionEffects.getEffects()) {
					target = (Symbol) eff.getTarget().clone();
					target.MakeGround(substitutions);
					if (target.toString().equals(Constants.SELF)) {
						AgentLogger.GetInstance().log("Updating motivator " + eff.getDriveName());
						try {
							//System.out.println("MOTIVATOR UPDATE: "+eff.getHierarchy());
							short hierarchy = MotivatorHierarchy.ParseHierarchyLevel(eff.getHierarchy());
							short driveType =  MotivatorHierarchy.ParseNeedInHierarchy(hierarchy, eff.getDriveName());
							float oldLevel = GetIntensity(hierarchy, driveType);
							Motivator mot = GetMotivator(hierarchy, driveType);
							if(mot != null)
							{
								//float sumPrep = getHierarchyIntensity(hierarchy);
								//calculate the prepotency here and decide which drives will be "really" updated
								deviation = mot.UpdateIntensity(eff.getValue());
								contributionToNeed = determineQuadraticNeedVariation(oldLevel, deviation) * 0.1f;
								contributionToSelfNeeds += contributionToNeed;
							}
						} catch (InvalidHierarchyMotivatorTypeException e1) {
							e1.printStackTrace();
						}
					}
				}
				
				//updates the current prepotency hierarchy level
				//that way the current prepotency level will have it's needs
				//with a stronger decay
				updatePrepotencyNeeds();
				return contributionToSelfNeeds; // finishes after finding the
												// action that unifies with the
												// event
			}
		}

		return 0; // no action was found that unified with the event
	}

	public ArrayList<SubstitutionSet> searchEventsWithAppraisal(AgentModel am,
			Symbol subjectVariable, Symbol actionVariable,
			Symbol targetVariable, Symbol paramVariable, float desirability) {
		class Box {
			float value;
			String target;

			Box(String target, float value) {
				this.target = target;
				this.value = value;
			}
		}

		HashMap<String, Box> test;

		ArrayList<SubstitutionSet> substitutions = new ArrayList<SubstitutionSet>();
		ArrayList<Symbol> actionListOfSymbols;
		SubstitutionSet currentSet;
		Symbol target;
		Symbol action;
		Symbol param1;
		String auxName;
		float contributionToNeed = 0f;

		if (am.isSelf()) {
			auxName = Constants.SELF;
		} else {
			auxName = am.getName();
		}

		for (ActionEffectsOnDrives actionEffects : _actionEffectsOnDrives
				.values()) {

			test = new HashMap<String, Box>();

			for (EffectOnDrive eff : actionEffects.getEffects()) {
				try {
					target = eff.getTarget();
					short hierarchy = MotivatorHierarchy.ParseHierarchyLevel(eff.getHierarchy());
					short driveType =  MotivatorHierarchy.ParseNeedInHierarchy(hierarchy, eff.getDriveName());
					float oldValue = GetIntensity(hierarchy, driveType);
					float newValue = Math.max(0,
							Math.min(10, oldValue + eff.getValue()));
					contributionToNeed = (float) determineQuadraticNeedVariation(
							oldValue, newValue - oldValue) * 0.1f;
					if (test.containsKey(target.toString())) {
						Box b = test.get(target.toString());
						b.value = b.value + contributionToNeed;
					} else {
						test.put(target.toString(), new Box(target.toString(),
								contributionToNeed));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			for (Box b : test.values()) {
				if ((desirability >= 0) ? b.value >= desirability
						: b.value <= desirability) {
					
					//this test is done temporarily because right now I'm only interested in
					//in interpersonal emotion regulation and I don't want to consider the 
					//actions where the target causes an emotion in itself
				    //TODO: this must be done properly latter
					if(!b.target.equals("[AGENT]"))
					{
						actionListOfSymbols = actionEffects.getActionName().GetLiteralList();
	
						action = actionListOfSymbols.get(0);
						currentSet = new SubstitutionSet();
						currentSet.AddSubstitution(new Substitution(actionVariable,
								action));
						if (b.target.equals("[AGENT]")) {
							currentSet.AddSubstitution(new Substitution(
									subjectVariable, new Symbol(auxName)));
						}
	
						if (actionListOfSymbols.size() > 1) {
							target = actionListOfSymbols.get(1);
							if (b.target.equals(target.toString())) {
								currentSet.AddSubstitution(new Substitution(
										targetVariable, new Symbol(auxName)));
							} else {
								currentSet.AddSubstitution(new Substitution(
										targetVariable, target));
							}
						}
						
						if(actionListOfSymbols.size() > 2)
						{
							param1 = actionListOfSymbols.get(2);
							currentSet.AddSubstitution(new Substitution(paramVariable, param1));
						}
						substitutions.add(currentSet);
					}
				}
			}
		}

		Collections.shuffle(substitutions);
		return substitutions; // finishes after finding the action that unifies
								// with the event
	}

}

