package FAtiMA.maslowHierarchyOfNeeds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author caetanoportodasilva
 * This class is supposed to imitate MotivatorType but adapted to the 
 * Maslow's Hierarchy of Needs
 * This work is based on the original hierarchy of needs (1943)
 */

public class MotivatorHierarchy {
	/**
	 * Food - related to hunger - somatically associated
	 * Liquid - related to thirst - somatically associated
	 * Rest - related to energy
	 * Oxygen - related to the ability of regularly and unconsciously breathing
	 * Sex - related to the drive for sexual relations - somatically associated
	 * FreedomOfMovement - related to the urge of being able to come and go as you wish
	 * Temperature - related to the ability of unconsciously controlling your body's temperature
	 */
	public static final short PHYSIOLOGY = 0;
	/**
	 * PhysiologicalSafety - it is related to any physical harm that befalls yourself
	 * PsychologicalSafety - it is related to certainty of events, if a goal is considered
	 * certain of being achieved and all the same fails then you may suffer such urge 
	 */
	public static final short SAFENESS = 1;
	/**
	 * GiveLove - it is not related at any level to sexual relations. Giving love is 
	 * seeking to fill a void by understanding and accepting "selected" others. i.e, it is 
	 * also not about selfless acceptance of other (as in buddhism) 
	 * ReceiveLove - it is not related at any level to sexual relations. Receiving love is
	 * a way of staving off the pangs of loneliness and rejection. 
	 */
	public static final short RELATIONSHIPS = 2; 
	/**
	 * Achievement - related to the need for competence and self-esteem
	 * Recognition - related to the need for recognition and admiration from others
	 */
	public static final short ESTEEM = 3;
	/**
	 * SelfActualization - the desire to become more and more what one is, 
	 * to become everything that one is capable of becoming.
	 * self-actualization can take many forms, depending on the individual. 
	 * These variations may include the quest for knowledge, understanding, peace, 
	 * self-fulfillment, meaning in life, beauty, etc...
	 * it is the pursue of a cause or vocation that is realized as fitting for oneself.
	 */
	public static final short REALIZATION = 4;
	
	public static final short NO_MOTIVATORS = 5;
	
	private static final String[] _motivatorHierarchies = {"Physiology",
		   											       "Safety",
													       "Relationships",
													       "Esteem",
													       "Realization"};
	
	private static final List<Class<? extends Needs>> _levelHierarchy
	= new ArrayList<Class<? extends Needs>>(Arrays.asList(PhysiologyNeeds.class, SafetyNeeds.class,
														   RelationshipNeeds.class, EsteemNeeds.class,
														   RealizationNeeds.class));
		
	public static int numberOfLevelsInHierarchy(){
		return _motivatorHierarchies.length;
	}
	
	/**
	 * Parses a string that corresponds to the motivator type and returns the appropriate
	 * motivator type (enumerable)
	 * @param motivatorType - the name of the motivator to search for
	 * @return - the id of the motivator type
	 * 
	 */
	public static short ParseHierarchyLevel(String hierarchy) throws InvalidHierarchyMotivatorTypeException {
		short i;
		
		if(hierarchy == null) throw new InvalidHierarchyMotivatorTypeException(null);
		
		for(i=0; i < _motivatorHierarchies.length; i ++) {
			if(_motivatorHierarchies[i].equals(hierarchy)) 
				return i;
		}
		
		throw new InvalidHierarchyMotivatorTypeException(hierarchy);
	}
	
	public static short ParseHierarchyLevel(Class<? extends Needs> hierarchy) throws InvalidHierarchyMotivatorTypeException {
		short i;
		
		if(hierarchy == null) throw new InvalidHierarchyMotivatorTypeException(null);
		
		for (i=0; i < _levelHierarchy.size(); i++){
			if(hierarchy == _levelHierarchy.get(i))
				return i;
		}
		
		throw new InvalidHierarchyMotivatorTypeException(null);
	}
	
	public static Class<? extends Needs> ParseHierarchyLevel(short hierarchy) throws InvalidHierarchyMotivatorTypeException {
		short i;
		
		if(hierarchy < 0 || hierarchy > 4) throw new InvalidHierarchyMotivatorTypeException(null);
		
		for (i=0; i < _levelHierarchy.size(); i++){
			if(i == hierarchy)
				return _levelHierarchy.get(i);
		}
		
		throw new InvalidHierarchyMotivatorTypeException(null);
	}
	
	public static short ParseNeedInHierarchy(short hierarchy, String need){
		if(hierarchy == MotivatorHierarchy.PHYSIOLOGY)
			return PhysiologyNeeds.parseNeeds(need);
		else if(hierarchy == MotivatorHierarchy.SAFENESS)
			return SafetyNeeds.parseNeeds(need);
		else if(hierarchy == MotivatorHierarchy.RELATIONSHIPS)
			return RelationshipNeeds.parseNeeds(need);
		else if(hierarchy == MotivatorHierarchy.ESTEEM)
			return EsteemNeeds.parseNeeds(need);
		else if(hierarchy == MotivatorHierarchy.REALIZATION)
			return RealizationNeeds.parseNeeds(need);
		return -1;
	}
	
	/**
	 * Gets the motivator's hierarchy name, given its identifier
	 * @param the id of the hierarchy motivatorType
	 * @return the name of the hierarchy motivatorType
	 */
	public static String GetMotivatorHierarchyName(short motivatorType) {
	    if(motivatorType == -1) return "Neutral";
		if(motivatorType >= 0 && motivatorType < NO_MOTIVATORS) return _motivatorHierarchies[motivatorType];
		return null;
	}
}
