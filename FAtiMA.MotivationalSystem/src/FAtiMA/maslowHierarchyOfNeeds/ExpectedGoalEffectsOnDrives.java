package FAtiMA.maslowHierarchyOfNeeds;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class ExpectedGoalEffectsOnDrives implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 6979903369257676042L;
	private String _goalName;
	private ArrayList<EffectOnDrive> _effects;
	
	public ExpectedGoalEffectsOnDrives(String goalName)
	{
		this._goalName = goalName;
		_effects = new ArrayList<EffectOnDrive>();
	}
	
	public String getGoalName()
	{
		return _goalName;
	}
	
	public void AddEffect(EffectOnDrive e)
	{
		_effects.add(e);
	}
	
	public Collection<EffectOnDrive> getEffects()
	{
		return _effects;
	}

}
