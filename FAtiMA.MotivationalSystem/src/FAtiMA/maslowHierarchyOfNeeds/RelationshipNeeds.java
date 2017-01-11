package FAtiMA.maslowHierarchyOfNeeds;

public enum RelationshipNeeds implements Needs {
	GiveLove(0),
	ReceiveLove(1);
	
	private final short _value;
	
	private RelationshipNeeds (int value) { this._value = (short) value; }
	
	public short getValue() { return this._value; }
	
	public static short parseNeeds(String need){
		for(short i = 0; i < needs.length; i++){
			if(needs[i].equals(need)){
				return i;
			}
		}
		return -1;
	}
	
	private static final String[] needs = {GiveLove.name(),
										   ReceiveLove.name()};
	
	public static final String[] getNeeds(){
		return needs;
	}
	
	public static final String getHierarchy(){
		return "Relationships";
	}
}