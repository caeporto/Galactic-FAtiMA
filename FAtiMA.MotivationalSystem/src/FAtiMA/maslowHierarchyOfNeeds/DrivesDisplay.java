package FAtiMA.maslowHierarchyOfNeeds;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

//import FAtiMA.Core.util.Constants;

public class DrivesDisplay {
	JPanel _physiologicalPanel;
	JPanel _safetyPanel;
	JPanel _relationshipsPanel;
	JPanel _esteemPanel;
	JPanel _realizationPanel;
	
	//Physiology Needs
	JProgressBar _foodBar;
	JProgressBar _liquidBar;
	JProgressBar _restBar;
	JProgressBar _oxygenBar;
	JProgressBar _sexBar;
	JProgressBar _freedomBar;
	JProgressBar _temperatureBar;
	
	
	//Safety Needs
	JProgressBar _physiologicalSafety;
	JProgressBar _psychologicalSafety;
	
	//Relationship Needs
	JProgressBar _giveLoveBar;
	JProgressBar _receiveLoveBar;
	
	//Esteem Needs
	JProgressBar _achievementBar;
	JProgressBar _recognitionBar;
	
	//Realization Needs
	JProgressBar _realizationBar;

	//TODO: The way that the constructor distinguishes from the agent's needs panel
	//of the other agents needs panel is getting agentName == null; 
	public DrivesDisplay(String agentName, MotivationalComponent ms) {

		super();
		//boolean isSelf = (agentName.equalsIgnoreCase(Constants.SELF));

		_physiologicalPanel = new JPanel();
		_physiologicalPanel.setBorder(BorderFactory.createTitledBorder("Physiological Needs"));
		_physiologicalPanel.setLayout(new BoxLayout(_physiologicalPanel,BoxLayout.Y_AXIS));
		
		_safetyPanel = new JPanel();
		_safetyPanel.setBorder(BorderFactory.createTitledBorder("Safety Needs"));
		_safetyPanel.setLayout(new BoxLayout(_safetyPanel,BoxLayout.Y_AXIS));
		
		_relationshipsPanel = new JPanel();
		_relationshipsPanel.setBorder(BorderFactory.createTitledBorder("Relationships Needs"));
		_relationshipsPanel.setLayout(new BoxLayout(_relationshipsPanel,BoxLayout.Y_AXIS));
		
		_esteemPanel = new JPanel();
		_esteemPanel.setBorder(BorderFactory.createTitledBorder("Esteem Needs"));
		_esteemPanel.setLayout(new BoxLayout(_esteemPanel,BoxLayout.Y_AXIS));
		
		_realizationPanel = new JPanel();
		_realizationPanel.setBorder(BorderFactory.createTitledBorder("Realization Needs"));
		_realizationPanel.setLayout(new BoxLayout(_realizationPanel,BoxLayout.Y_AXIS));
	
		_foodBar = new JProgressBar(0,100);
		_liquidBar = new JProgressBar(0,100);
		_restBar = new JProgressBar(0,100);
		_oxygenBar = new JProgressBar(0,100);
		_sexBar = new JProgressBar(0,100);
		_freedomBar = new JProgressBar(0,100);
		_temperatureBar = new JProgressBar(0,100);
		
		_physiologicalSafety = new JProgressBar(0,100);
		_psychologicalSafety = new JProgressBar(0,100);
		
		_giveLoveBar = new JProgressBar(0,100);
		_receiveLoveBar = new JProgressBar(0,100);
		
		_achievementBar = new JProgressBar(0,100);
		_recognitionBar = new JProgressBar(0,100);
		
		_realizationBar = new JProgressBar(0,100);

		
		if(ms.GetMotivator(MotivatorHierarchy.PHYSIOLOGY, PhysiologyNeeds.Food.getValue()) != null)
			_physiologicalPanel.add(InitializePanel(_foodBar, "Food"));
		if(ms.GetMotivator(MotivatorHierarchy.PHYSIOLOGY, PhysiologyNeeds.Liquid.getValue()) != null)
			_physiologicalPanel.add(InitializePanel(_liquidBar, "Liquid"));
		if(ms.GetMotivator(MotivatorHierarchy.PHYSIOLOGY, PhysiologyNeeds.Rest.getValue()) != null)
			_physiologicalPanel.add(InitializePanel(_restBar, "Rest"));
		if(ms.GetMotivator(MotivatorHierarchy.PHYSIOLOGY, PhysiologyNeeds.Oxygen.getValue()) != null)
			_physiologicalPanel.add(InitializePanel(_oxygenBar, "Oxygen"));
		if(ms.GetMotivator(MotivatorHierarchy.PHYSIOLOGY, PhysiologyNeeds.Sex.getValue()) != null)
			_physiologicalPanel.add(InitializePanel(_sexBar, "Sex"));
		if(ms.GetMotivator(MotivatorHierarchy.PHYSIOLOGY, PhysiologyNeeds.FreedomOfMovement.getValue()) != null)
			_physiologicalPanel.add(InitializePanel(_freedomBar, "FreedomOfMovement"));
		if(ms.GetMotivator(MotivatorHierarchy.PHYSIOLOGY, PhysiologyNeeds.Temperature.getValue()) != null)
			_physiologicalPanel.add(InitializePanel(_temperatureBar, "Temperature"));
		
		if(ms.GetMotivator(MotivatorHierarchy.SAFENESS, SafetyNeeds.PhysiologicalSafety.getValue()) != null)
			_safetyPanel.add(InitializePanel(_physiologicalSafety, "PhysiologicalSafety"));
		if(ms.GetMotivator(MotivatorHierarchy.SAFENESS, SafetyNeeds.PsychologicalSafety.getValue()) != null)
			_safetyPanel.add(InitializePanel(_psychologicalSafety, "PsychologicalSafety"));
		
		if(ms.GetMotivator(MotivatorHierarchy.RELATIONSHIPS, RelationshipNeeds.GiveLove.getValue()) != null)
			_relationshipsPanel.add(InitializePanel(_giveLoveBar, "GiveLove"));
		if(ms.GetMotivator(MotivatorHierarchy.RELATIONSHIPS, RelationshipNeeds.ReceiveLove.getValue()) != null)
			_relationshipsPanel.add(InitializePanel(_receiveLoveBar, "ReceiveLove"));
		
		if(ms.GetMotivator(MotivatorHierarchy.ESTEEM, EsteemNeeds.Achievement.getValue()) != null)
			_esteemPanel.add(InitializePanel(_achievementBar, "Achievement"));
		if(ms.GetMotivator(MotivatorHierarchy.ESTEEM, EsteemNeeds.Recognition.getValue()) != null)
			_esteemPanel.add(InitializePanel(_recognitionBar, "Recognition"));
		
		if(ms.GetMotivator(MotivatorHierarchy.REALIZATION, RealizationNeeds.SelfActualization.getValue()) != null)
			_realizationPanel.add(InitializePanel(_realizationBar, "Realization"));
		
//		if(isSelf){
//			_panel.add(InitializePanel(_certaintyBar, "Certainty"));
//			_panel.add(InitializePanel(_competenceBar, "Competence"));
//		}
	

	}
	private JPanel InitializePanel(JProgressBar driveBar, String driveName)
	{
		JPanel drivePanel = new JPanel();
		drivePanel.setBorder(BorderFactory.createTitledBorder(driveName));
		drivePanel.setMaximumSize(new Dimension(300,60));

		driveBar.setValue(0);
		driveBar.setStringPainted(true);
		driveBar.setForeground(new Color(255,0,0));
		//driveBar.setBackground(new Color(255,0,0));
		drivePanel.add(driveBar);

		return drivePanel;
	}

	
	 
    public boolean Update(MotivationalComponent ms) {
        Float aux;
        
        if(ms.GetMotivator(MotivatorHierarchy.PHYSIOLOGY, PhysiologyNeeds.Food.getValue()) != null)
        {
        	aux = new Float(ms.GetIntensity(MotivatorHierarchy.PHYSIOLOGY, PhysiologyNeeds.Food.getValue()));
        	_foodBar.setString(aux.toString());
        	_foodBar.setValue(Math.round(aux.floatValue()*10));
        }
        
        if(ms.GetMotivator(MotivatorHierarchy.PHYSIOLOGY, PhysiologyNeeds.Liquid.getValue()) != null)
        {
        	aux = new Float(ms.GetIntensity(MotivatorHierarchy.PHYSIOLOGY, PhysiologyNeeds.Liquid.getValue()));
        	_liquidBar.setString(aux.toString());
        	_liquidBar.setValue(Math.round(aux.floatValue()*10));
        }
        
        if(ms.GetMotivator(MotivatorHierarchy.PHYSIOLOGY, PhysiologyNeeds.Rest.getValue()) != null)
        {
        	aux = new Float(ms.GetIntensity(MotivatorHierarchy.PHYSIOLOGY, PhysiologyNeeds.Rest.getValue()));
        	_restBar.setString(aux.toString());
        	_restBar.setValue(Math.round(aux.floatValue()*10));
        }
        
        if(ms.GetMotivator(MotivatorHierarchy.PHYSIOLOGY, PhysiologyNeeds.Oxygen.getValue()) != null)
        {
        	aux = new Float(ms.GetIntensity(MotivatorHierarchy.PHYSIOLOGY, PhysiologyNeeds.Oxygen.getValue()));
        	_oxygenBar.setString(aux.toString());
        	_oxygenBar.setValue(Math.round(aux.floatValue()*10));
        }
        
        if(ms.GetMotivator(MotivatorHierarchy.PHYSIOLOGY, PhysiologyNeeds.Sex.getValue()) != null)
        {
        	aux = new Float(ms.GetIntensity(MotivatorHierarchy.PHYSIOLOGY, PhysiologyNeeds.Sex.getValue()));
        	_sexBar.setString(aux.toString());
        	_sexBar.setValue(Math.round(aux.floatValue()*10));
        }
        
        if(ms.GetMotivator(MotivatorHierarchy.PHYSIOLOGY, PhysiologyNeeds.FreedomOfMovement.getValue()) != null)
        {
        	aux = new Float(ms.GetIntensity(MotivatorHierarchy.PHYSIOLOGY, PhysiologyNeeds.FreedomOfMovement.getValue()));
        	_freedomBar.setString(aux.toString());
        	_freedomBar.setValue(Math.round(aux.floatValue()*10));
        }
        
        if(ms.GetMotivator(MotivatorHierarchy.PHYSIOLOGY, PhysiologyNeeds.Temperature.getValue()) != null)
        {
        	aux = new Float(ms.GetIntensity(MotivatorHierarchy.PHYSIOLOGY, PhysiologyNeeds.Temperature.getValue()));
        	_temperatureBar.setString(aux.toString());
        	_temperatureBar.setValue(Math.round(aux.floatValue()*10));
        }
        
        if(ms.GetMotivator(MotivatorHierarchy.SAFENESS, SafetyNeeds.PhysiologicalSafety.getValue()) != null)
        {
        	aux = new Float(ms.GetIntensity(MotivatorHierarchy.SAFENESS, SafetyNeeds.PhysiologicalSafety.getValue()));
        	_physiologicalSafety.setString(aux.toString());
        	_physiologicalSafety.setValue(Math.round(aux.floatValue()*10));
        }
        
        if(ms.GetMotivator(MotivatorHierarchy.SAFENESS, SafetyNeeds.PsychologicalSafety.getValue()) != null)
        {
        	aux = new Float(ms.GetIntensity(MotivatorHierarchy.SAFENESS, SafetyNeeds.PhysiologicalSafety.getValue()));
        	_psychologicalSafety.setString(aux.toString());
        	_psychologicalSafety.setValue(Math.round(aux.floatValue()*10));
        }
        
        if(ms.GetMotivator(MotivatorHierarchy.RELATIONSHIPS, RelationshipNeeds.GiveLove.getValue()) != null)
        {
        	aux = new Float(ms.GetIntensity(MotivatorHierarchy.RELATIONSHIPS, RelationshipNeeds.GiveLove.getValue()));
        	_giveLoveBar.setString(aux.toString());
        	_giveLoveBar.setValue(Math.round(aux.floatValue()*10));
        }
        
        if(ms.GetMotivator(MotivatorHierarchy.RELATIONSHIPS, RelationshipNeeds.ReceiveLove.getValue()) != null)
        {
        	aux = new Float(ms.GetIntensity(MotivatorHierarchy.RELATIONSHIPS, RelationshipNeeds.ReceiveLove.getValue()));
        	_receiveLoveBar.setString(aux.toString());
        	_receiveLoveBar.setValue(Math.round(aux.floatValue()*10));
        }
        
        if(ms.GetMotivator(MotivatorHierarchy.ESTEEM, EsteemNeeds.Achievement.getValue()) != null)
        {
        	aux = new Float(ms.GetIntensity(MotivatorHierarchy.ESTEEM, EsteemNeeds.Achievement.getValue()));
        	_achievementBar.setString(aux.toString());
        	_achievementBar.setValue(Math.round(aux.floatValue()*10));
        }
        
        if(ms.GetMotivator(MotivatorHierarchy.ESTEEM, EsteemNeeds.Recognition.getValue()) != null)
        {
        	aux = new Float(ms.GetIntensity(MotivatorHierarchy.ESTEEM, EsteemNeeds.Recognition.getValue()));
        	_recognitionBar.setString(aux.toString());
        	_recognitionBar.setValue(Math.round(aux.floatValue()*10));
        }
        
        if(ms.GetMotivator(MotivatorHierarchy.REALIZATION, RealizationNeeds.SelfActualization.getValue()) != null)
        {
        	aux = new Float(ms.GetIntensity(MotivatorHierarchy.REALIZATION, RealizationNeeds.SelfActualization.getValue()));
        	_realizationBar.setString(aux.toString());
        	_realizationBar.setValue(Math.round(aux.floatValue()*10));
        }
        
        if(ms._prepotencyHierarchyLevel == PhysiologyNeeds.class)
        	_physiologicalPanel.setBackground(new Color(255,0,0));
        else
        	_physiologicalPanel.setBackground(null);
        if(ms._prepotencyHierarchyLevel == SafetyNeeds.class)
        	_safetyPanel.setBackground(new Color(255,0,0));
        else
        	_safetyPanel.setBackground(null);
        if(ms._prepotencyHierarchyLevel == RelationshipNeeds.class)
        	_relationshipsPanel.setBackground(new Color(255,0,0));
        else
        	_relationshipsPanel.setBackground(null);
        if(ms._prepotencyHierarchyLevel == EsteemNeeds.class)
        	_esteemPanel.setBackground(new Color(255,0,0));
        else
        	_esteemPanel.setBackground(null);
        if(ms._prepotencyHierarchyLevel == RealizationNeeds.class)
        	_realizationPanel.setBackground(new Color(255,0,0));
        else
        	_realizationPanel.setBackground(null);
        
        return true;
    }
    
	public JPanel getPhysiologicalPanel() {
		return _physiologicalPanel;
	}
	
	public JPanel getSafetyPanel() {
		return _safetyPanel;
	}
	
	public JPanel getRelationshipsPanel() {
		return _relationshipsPanel;
	}
	
	public JPanel getEsteemPanel() {
		return _esteemPanel;
	}
	
	public JPanel getRealizationPanel() {
		return _realizationPanel;
	}
}

