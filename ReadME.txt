FAtiMA is an Autonomous Agent Architecture with reactive and BDI based deliberative behaviour, initially developed to control the minds of the 
agents in FearNot! (in European project VICTEC).The architecture focuses on using emotions and personality to influence the agent’s
behaviour. Inspired in the OCC model of emotions, the agents appraise each event according to their standards, goals and personality, 
and use the resulting emotions to influence reasoning.For example, a fearful agent may decide to give up a goal more easily.

Continuing work in another research project (LIREC) extended the architecture with additional features that endows the agents with
skills for long-term interactions with users and other agents. For instance an Autobiographical Memory that allows the agents to
remember and mark emotionally significant events, the inclusion of a motivational system (taken from system PSI), which solves 
some limitations with goal activation and goal selection we had in the previous version (e.g. non-dynamic importance of goals),
and makes authoring more intuitive. It also incorporates mechanisms for Theory of Mind reasoning about other agents (i.e. reasoning
about the beliefs of others agents) and emotional intelligence mechanisms (e.g. reasoning about and regulating emotions in others).

FAtiMA Modular presents a refinement of the original architecture where some of the core aspects of the architecture were divided into
several independent components. This allows the use of smaller, lighter versions of FAtiMA with only a few components, and also favors 
extensibility by allowing FAtiMA to be extended by adding new components. 


Setting up the FAtiMA-Modular in Eclipse

1.	Open Eclipse by setting the workspace to the FAtiMA-Modular directory.

2.	Then click on File->Import->General->Existing Project into Workspace->Select root directory. 
	Make sure that the root directory is the FAtiMA-Modular directory. 
	You should be able to see the following folders appearing in the projects list:

		AgentLauncher – project used to launch a FAtiMA agent and to configure which components will be loaded.

		data - contains the data files for some example agents/scenarios

		FAtiMA - contains the source code for FAtiMA Core

		FAtiMA.AdvancedMemory – source code for the AvancedMemory Component, which contains functionalities 
					such as memory retrieval and forgetting. This component requires the installation 
					of Drools. If you want to use this component see the file “Drools Installation 
					and Setup (Core and IDE).html” for more details on how to install drools.

		FAtiMA.Culture – culture component, defines cultural-dependent behavior of agents through the use of 
				 rituals and cultural dimensions

		FAtiMA.DeliberativeComponent – Deliberative layer of FAtiMA architecture which handles BDI goal-based behavior 
					       and planning capabilities of the agent.

		FAtiMA.EmotionalIntelligence – component that endows the agent with the capability of reasoning and 
					       planning about emotions.

		FAtiMA.Empathy – empathy model that allows the agent to perform an empathic appraisal triggered by 
				 emotions in others.

		FAtiMA.MotivationalSystem – component that models PSI needs such as Energy, Integrity and Affiliation 
					    and uses them to activate goals.

		FAtiMA.OCCAffectDerivation – component that generates emotions according to OCC theory of emotion. 

		FAtiMA.ReactiveComponent – FatiMA reactive layer, implements reactive emotion rules and action tendencies.

		FAtiMA.SocialRelations – models social relations between the agents such as Like and Respect.

		FAtiMA.TheoryOfMind – Theory of mind component endows the agents with mindreading capabilities, and allows 
				      them to have a model of others internal states (beliefs, emotions, relations, 
				      memory, etc) and use that knowledge to achieve its own goals.  

		WorldTest - a simple world simulator for testing and debugging agents and simulating simple interactions

		LanguageServer - Language engine used to together with FAtiMA in previous projects for Natural Language
				 generation


        Check all of them and click Finish.

3.	To debug/run the project, right click on the respective project ->Run As/Debug As->Open Run Configurations/Open Debug Configurations. 

4.	When the dialog is open, double click on Java Application. Write the name of the run configuration under the Name column.
        In the Main tab, choose the respective project from the Browse list, then choose the Main class. The Main class of the 
	respective class should be listed when you click the Search button. In order to run a FAtiMA agent use the class AgentLauncher 
	from the project AgentLauncher as an entry point. The main class for the WorldTest program is WorldTest in the project WorldTest.

5.	Next, you need to set up the arguments in the Arguments tab. WorldTest arguments are the name of the scenarios file and the 
	name of a specific scenario. Scenarios are defined inside a scenario library xml file. For instance, by launching the worldtest
	with the arguments “SimpleScenarios.xml SimpleScenario”, we will launch the SimpleScenario defined in the SimpleScenarios.xml file.

6.	The arguments for running the FAtiMA agent are scenarios file, the scenario name, and finally the agent’s name. Eg., for Tim in 
	the MindBlindness scenario, the arguments will be “SimpleScenarios.xml SimpleScenario Guilhas”. Then make sure that the working 
	directory is pointing to where the data folder is located. If you did not change the structure of the FAtiMA-Modular folder,
	then the working directory should be a level up the Default directory. So, click on Other in the Working directory panel and type
 	in the arguments in ${workspace_loc}

7.	Repeat steps 3 to 6 for each application (agent) you want to debug/run. 

8.	When everything is set up, debug/run the application by clicking the debug/run button. Remember to start the WorldTest first before 
	you load any other agents.


Refer to the Documentation folder for some more information about FAtiMA Modular and how to author/specify agents in FAtiMA.

Please feel free to contact if you have any questions.

João Dias - joao.dias@gaips.inesc-id.pt
