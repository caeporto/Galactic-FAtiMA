package FAtiMA.maslowHierarchyOfNeeds;
/*
 * InvalidMotivatorTypeException.java - Exception thrown when an invalid MotivatorType 
 * is parsed in the enumerable Class of MotivatorType
 */
/**
 *  Exception thrown when an invalid MotivatorType is parsed in the enumerable Class of MotivatorType
 * 
 *  @author Meiyii Lim
 */

public class InvalidHierarchyMotivatorTypeException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public InvalidHierarchyMotivatorTypeException(String motivator) {
        super("ERROR: Invalid hierarchy motivator type " + motivator);
    }
    
    public InvalidHierarchyMotivatorTypeException(int num) {
        super("ERROR: invalid hierarchy motivator type indentifier " + num);
    }
}

