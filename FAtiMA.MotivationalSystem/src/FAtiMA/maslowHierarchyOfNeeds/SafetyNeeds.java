package FAtiMA.maslowHierarchyOfNeeds;

public enum SafetyNeeds implements Needs {
	PhysiologicalSafety(0),
	PsychologicalSafety(1);
	
	private final short _value;
	
	private SafetyNeeds(int value) { this._value = (short) value; }
	
	public short getValue() { return this._value; }
	
	public static short parseNeeds(String need){
		for(short i = 0; i < needs.length; i++){
			if(needs[i].equals(need)){
				return i;
			}
		}
		return -1;
	}
	
	private static final String[] needs = {PhysiologicalSafety.name(),
										   PsychologicalSafety.name()};
	
	public static final String[] getNeeds(){
		return needs;
	}
	
	public static final String getHierarchy(){
		return "Safety";
	}
}
