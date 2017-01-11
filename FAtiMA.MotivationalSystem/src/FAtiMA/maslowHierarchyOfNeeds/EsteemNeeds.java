package FAtiMA.maslowHierarchyOfNeeds;

public enum EsteemNeeds implements Needs{
	Achievement(0),
	Recognition(1);
	
	private final short _value;
	
	private EsteemNeeds (int value) { this._value = (short) value; }
	
	public short getValue() { return this._value; }
	
	public static short parseNeeds(String need){
		for(short i = 0; i < needs.length; i++){
			if(needs[i].equals(need)){
				return i;
			}
		}
		return -1;
	}
	
	private static final String[] needs = {Achievement.name(),
										   Recognition.name()};
	
	public static final String[] getNeeds(){
		return needs;
	}
	
	public static final String getHierarchy(){
		return "Esteem";
	}
}
