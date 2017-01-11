package FAtiMA.maslowHierarchyOfNeeds;

public enum PhysiologyNeeds implements Needs{
	Food(0),
	Liquid(1),
	Rest(2),
	Oxygen(3),
	Sex(4),
	FreedomOfMovement(5),
	Temperature(6);
	
	private final short _value;
	
	private PhysiologyNeeds(int value) { this._value = (short) value; }
	
	public short getValue() { return this._value; }
	
	public static short parseNeeds(String need){
		for(short i = 0; i < needs.length; i++){
			if(needs[i].equals(need)){
				return i;
			}
		}
		return -1;
	}
	
	private static final String[] needs = {Food.name(),
										   Liquid.name(),
										   Rest.name(),
										   Oxygen.name(),
										   Sex.name(),
										   FreedomOfMovement.name(),
										   Temperature.name()};
	
	public static final String[] getNeeds(){
		return needs;
	}
	
	public static final String getHierarchy(){
		return "Physiology";
	}
}
