package FAtiMA.maslowHierarchyOfNeeds;

import java.io.Serializable;

import FAtiMA.Core.wellFormedNames.Symbol;



public class EffectOnDrive implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4056126221833123610L;
	private float _value;
	private short _type;
	private String _hierarchy; //added in accordance to maslow's hierarchy
	private String _driveName;
	private Symbol _target;
	
	public EffectOnDrive(Short type, String hierarchy, String driveName, Symbol target, float value)
	{
		this._hierarchy = hierarchy;
		this._type = type;
		this._target = target;
		this._value = value;
		this._driveName = driveName;
	}
	
	public String getHierarchy(){
		return this._hierarchy;
	}
	
	public short getType()
	{
		return this._type;
	}
	
	public Symbol getTarget()
	{
		return this._target;
	}
	
	public float getValue()
	{
		return this._value;
	}
	
	public String getDriveName()
	{
		return this._driveName;
	}

}

