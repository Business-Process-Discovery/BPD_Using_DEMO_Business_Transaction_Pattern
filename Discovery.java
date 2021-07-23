package Discovery;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

//import sun.jvm.hotspot.ui.ProcessListPanel;

//import org.w3c.dom.Text;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.*;
import java.util.*;
//import java.text.*;


public class Discovery {

	ArrayList<String> filesReceived = new ArrayList<String>();
	//static String output_path = new String();

	List<List<String>> ActorsTobeDesigned;
	List<List<String>> TptTobeDesigned;
	String[][] TkdependTobeDesigned;
	List<List<String>> TkviewTobeDesigned;
	List<FinalObjectList> finalObjectListTobeDesigned;
	List<List<String>> ListOfBusinessRules;
	List<List<String>> ListOfBusinessRulesInputQuestionInfo;

	String tkview_mode = new String();


	public String getTkview_mode() {
		return tkview_mode;
	}

	public void setTkview_mode(String tkview_mode) {
		this.tkview_mode = tkview_mode;
	}

	public List<List<String>> getActorsTobeDesigned() {
		return ActorsTobeDesigned;
	}


	public void setActorsTobeDesigned(List<List<String>> actorsTobeDesigned) {
		ActorsTobeDesigned = actorsTobeDesigned;
	}


	public List<List<String>> getTptTobeDesigned() {
		return TptTobeDesigned;
	}


	public void setTptTobeDesigned(List<List<String>> tptTobeDesigned) {
		TptTobeDesigned = tptTobeDesigned;
	}


	public String[][] getTkdependTobeDesigned() {
		return TkdependTobeDesigned;
	}


	public void setTkdependTobeDesigned(String[][] tkdependTobeDesigned) {
		TkdependTobeDesigned = tkdependTobeDesigned;
	}


	public List<List<String>> getTkviewTobeDesigned() {
		return TkviewTobeDesigned;
	}


	public void setTkviewTobeDesigned(List<List<String>> tkviewTobeDesigned) {
		TkviewTobeDesigned = tkviewTobeDesigned;
	}

	public void setObjectsListTobeDesigned(List<FinalObjectList> finalObjectsList) {
		finalObjectListTobeDesigned = finalObjectsList;
	}
	public List<FinalObjectList>  getObjectsListTobeDesigned(){
		return finalObjectListTobeDesigned;
	}

	public void setBusinessRuleListTobeDesigned(List<List<String>> listOfBusinessRules) {
		ListOfBusinessRules = listOfBusinessRules;
	}
	public List<List<String>>  getBusinessRuleListTobeDesigned(){
		return 	ListOfBusinessRulesInput;
	}

	public void setBusinessRuleInputQuestionsListTobeDesigned(List<List<String>> listOfBusinessRulesInputQuestionInfo) {
		ListOfBusinessRulesInputQuestionInfo = listOfBusinessRulesInputQuestionInfo;
	}
	public List<List<String>>  getBusinessRuleInputQuestionsListTobeDesigned(){
		return 	ListOfBusinessRulesInputQuestionInfo;
	}


	public Discovery()
	{

	}


    public void StartDiscovery(ArrayList<String> inputs)
    {

        try {

        	filesReceived = inputs;

            //List with the Name of the Event and the User initiator of the event and the Destination task of the event
        	System.out.println("Starting Extracting of Business Events...");
					System.out.println("Test");
            List<List<String>> listOfEventsInfo =  extractListOfEvents();
            System.out.println("...Extracting of Business Events finished.");

            //List with the Name of the Task and the User executor of the task
        	System.out.println("Starting Extracting of Business Tasks...");
            List<List<String>> listOfTasksInfo =  extractListOfTasksInfo();
            System.out.println("...Extracting of Business Tasks finished.");

            //List with the Name of the Actors of the process and a smal description and the User executor of the task
        	System.out.println("Starting Extracting of Business Actors...");
            List<List<String>> listOfActorsInfo =  extractListOfActors();
            System.out.println("...Extracting of Business Actors finished.");

						System.out.println("Starting Extracting of Business Rules...");
						List<List<String>> listOfBusinessRulesInfo =  extractListOfBusinessRules();
            System.out.println("Extracting of Business Rules finished.");

						System.out.println("Starting Extracting of Business Rules Input Questions...");
            List<List<String>> listOfBusinessRulesInputQuestionInfo =  extractListOfBusinessRulesInputQuestions();
            System.out.println("Extracting of Business rules input questions finished.");


            System.out.println("ListofTasksInfo: " + listOfTasksInfo);
            System.out.println ("\n");
            System.out.println("ListofEventsInfo: " + listOfTasksInfo);
            System.out.println ("\n");
            System.out.println("ListofActorsInfo: " + listOfActorsInfo);
            System.out.println ("\n");
						System.out.println("ListofBusinessRulesInfo: " + listOfBusinessRulesInfo);
            System.out.println ("\n");
            System.out.println("ListofBusinessRulesInputQuestionInfo: " + listOfBusinessRulesInputQuestionInfo);
            System.out.println ("\n");

						List<List<String>> taskRulesAndQuestionsList = createBusinesTasksAndRulesQuestion(listOfTasksInfo, listOfBusinessRulesInfo,  listOfBusinessRulesInputQuestionInfo );
            System.out.println("TaskRulesAndQuestionsList: ");
            for (List<String> list : taskRulesAndQuestionsList){
                System.out.println(list);
            }

            //Join the Event and Activity List
            List<List<String>> completeListInfo = new ArrayList<>();
            for (List<String> task : listOfTasksInfo){
                completeListInfo.add(task);
            }
            for (List<String> event : listOfEventsInfo){
                completeListInfo.add(event);
            }
            System.out.println("CompleteList: " + completeListInfo + "\n");

            List<String> firstList = getThefirstElementofTheProcess(completeListInfo);
            //System.out.println("1st element of the proces: " + firstList);

            List<List<String>> completeAndOrderedListInfo = getOrderedListInfo (completeListInfo, firstList);
            System.out.println("CompleteAndOrderedListInfo: " + completeAndOrderedListInfo);

            //create the processGraph from the Event and Activity List
            Graph processGraph = createProcessGraph(completeListInfo);
            printGraph(processGraph);


            //MAP  the DEMOgraph with ProcessGraph for Request and Declare
            List<List<String>> transactions = new ArrayList<>(); //will have the format [ [TK01, actorInitiator, actorExecutor]]
						List<List<String>> transactionsDependenciesHelper = new ArrayList<>();
					  List <Graph> graphs = new ArrayList<>();
            Map<Vertex,  LinkedList<Vertex>> processMap = processGraph.adjacencyMap;
            int transactionNumber = 1;
						outerloop:
            for (int list = 0; list < completeAndOrderedListInfo.size(); list++) {
                for (Map.Entry<Vertex,  LinkedList<Vertex>> entry : processMap.entrySet()) {

                    if ( (completeAndOrderedListInfo.get(list).size() == 5)
                    && (completeAndOrderedListInfo.get(list).get(3).equals("Pedido"))
                    && (completeAndOrderedListInfo.get(list).get(0).equals(entry.getKey().getVertexName())) ){
                        //LinkedList<Vertex> toVertexes = entry.getValue();
                          for (int list1 = 0; list1 < completeAndOrderedListInfo.size(); list1++) {
                              if (completeAndOrderedListInfo.get(list1).get(0).equals(completeAndOrderedListInfo.get(list).get(2))
                              ){ //if Name of the To Vertex is same and its an Event
                                  String actorExecutor = completeAndOrderedListInfo.get(list1).get(1);
                                  /*int counter = 0;
                                  for (int t = 0; t < transactions.size(); t++){ //Chek if its a loop
                                      if ( (transactions.get(t).get(2).equals(actorExecutor)) ////if the  alleged actor that is executor already has a "Pedido" then we do not create the transaction
                                      || ( (transactions.get(t).get(2).equals(completeAndOrderedListInfo.get(list).get(1)) )
                                      && (transactions.get(t).get(3).equals(actorExecutor)) )){
                                          counter = counter +1 ;
                                      }
                                  }*/
																	List<String> innerListDepHelper = new ArrayList<>();
	                                innerListDepHelper.add("TK" + transactionNumber);
	                                innerListDepHelper.add(completeAndOrderedListInfo.get(list).get(0)); //Origin
	                                innerListDepHelper.add(completeAndOrderedListInfo.get(listaux1).get(0)); //Destination
	                                transactionsDependenciesHelper.add(innerListDepHelper);

                                  List<String> innerList = new ArrayList<>();
                                  innerList.add("TK" + transactionNumber);
                                  innerList.add("");
                                  innerList.add(completeAndOrderedListInfo.get(list).get(1)); //actor iniciator
                                  innerList.add(actorExecutor); //actor executor
                                  innerList.add("PK" + transactionNumber);
                                  innerList.add("");

                                  transactions.add(innerList);
                                  transactionNumber = transactionNumber + 1;

                                  Graph demoGraph = createDEMOGraph();
																	demoGraph.setActorIniciator(completeAndOrderedListInfo.get(list).get(1));
                                  demoGraph.setActorExecutor(actorExecutor);

                                  demoGraph.getVertex("Request product").setMapped(entry.getKey().getVertexName()); //map Element name
                                  // or demoGraph.getVertex("Request product").setMapped(completeAndOrderedListInfo.get(list).get(0));
                                  demoGraph.getVertex("Request product").setActor(entry.getKey().getVertexActor());; //map Element Actor
                                  // or demoGraph.getVertex("Request product").setActor(completeAndOrderedListInfo.get(list).get(1));

                                  graphs.add(demoGraph);

                                  continue outerloop;
                            }
                        }
                    }

                }
            }
						for (int listR = 0; listR < completeAndOrderedListInfo.size(); listR++) {
                for (Map.Entry<Vertex,  LinkedList<Vertex>> entry : processMap.entrySet()) {
									if ( (completeAndOrderedListInfo.get(listR).size() == 5)
									&& (completeAndOrderedListInfo.get(listR).get(3).equals("Resposta"))
									&& (completeAndOrderedListInfo.get(listR).get(0).equals(entry.getKey().getVertexName())) ){

											LinkedList<Vertex> toVertexes = entry.getValue();
											String executorActor = entry.getKey().getVertexActor();

											for (int i = 0; i < toVertexes.size(); i++){ //get the actor of the activity happenning right after activity response and event
													for (int list2 = 0; list2 < completeAndOrderedListInfo.size(); list2++) {
															if (completeAndOrderedListInfo.get(list2).get(0).equals(toVertexes.get(i).getVertexName())
															){ //if Name of the To Vertex is same and its an Event
																	String actorIniciator = completeAndOrderedListInfo.get(list2).get(1);

																	for (int t = 0; t < transactions.size(); t++){

                                        Graph g = graphs.get(t);
                                        if (transactions.get(t).get(2).equals(actorIniciator)
                                        && (transactions.get(t).get(3).equals(executorActor) )
                                        && (g.getVertex("Request product").getVertexActor().equals(actorIniciator))  ){ //if activties actor matches transaction iniciator actor

                                            g.getVertex("Declare product").setMapped(entry.getKey().getVertexName()); //map Element name
                                            g.getVertex("Declare product").setActor(executorActor);; //map Element Actor
                                        }
                                    }
															}
													}
											}
										}
								}
						}
            for (int list = 0; list < completeAndOrderedListInfo.size(); list++) {
                for (Map.Entry<Vertex,  LinkedList<Vertex>> entry : processMap.entrySet()) {
                    //Map ACCEPT element
                    if ( (completeAndOrderedListInfo.get(list).size() == 5)
                    && (completeAndOrderedListInfo.get(list).get(2) == "" ) //if this activity is the last one
                    && (completeAndOrderedListInfo.get(list).get(0).equals(entry.getKey().getVertexName()) == true ) ){
                        for (int t = 0; t < transactions.size(); t++){
                            if (transactions.get(t).get(2).equals(completeAndOrderedListInfo.get(list).get(1))){ //if activties actor matches transaction iniciator actor
                                Graph g = graphs.get(t);
                                //printDEMOGraph(g);
                                g.getVertex("Accept product").setMapped(entry.getKey().getVertexName()); //map Element name
                                g.getVertex("Accept product").setActor(entry.getKey().getVertexActor());; //map Element Actor

                            }
                        }
                    }
                    //Map the rest
                }
            }
						List<String> unmappedElements = mapExecuteAndReqDecisionAndReturnTheRestOfUnmappedElements(completeAndOrderedListInfo, transactions, listOfActorsInfo, processGraph, graphs);
            createTIAndTE (transactions, completeAndOrderedListInfo);
            System.out.println("Unmapped Elements:" + unmappedElements);

            List<ObjectList> objectList =  extractListOfObjectsInfo();

            System.out.println("ObjectList: ");
            for (int i = 0; i < objectList.size(); i++){
                printoutObjectList(objectList.get(i));
            }

            /*for (Graph g: graphs){
                printDEMOGraph(g);
            }*/
            for (int i = 0; i < transactions.size(); i++){
                System.out.println(transactions.get(i));
            }
            System.out.println ("\n");

            String[][] dependenciesMatrix = createTransactionsDependencies(transactions, listOfActorsInfo, transactionsDependenciesHelper, completeAndOrderedListInfo);
            printMatrix(dependenciesMatrix, transactions.size() + 1);
            System.out.println ("\n");

            List<List<String>> tkview = createTKView(graphs, transactions);
            for (int i = 0; i < tkview.size(); i++){
                System.out.println(tkview.get(i));
            }

            List<FinalObjectList> finalObjectsList = createFinalObjectList(objectList, tkview);
            System.out.println ("FINAL OBJECT LIST:");
            for (int i = 0; i < finalObjectsList.size(); i++){
                printoutFinalObjectList(finalObjectsList.get(i));
            }

            setActorsTobeDesigned(listOfActorsInfo);
            setTptTobeDesigned(transactions);
            setTkdependTobeDesigned(dependenciesMatrix);
            setTkviewTobeDesigned(tkview);
            setObjectsListTobeDesigned(finalObjectsList);



        }
        // This exception block catches all the exception raised.
        catch (Exception e) {
            System.out.println(e);
        }

    }


    /**
	 * Function that accesses the XML files of the actors and extracts in a form of list all the actors of the process, with their name
	 * @return List of Lists of Strings with the next format: [ [actorName, description(empty) ], [...],... ]
	 */
    public List<List<String>> extractListOfActors() throws Exception{
            //File actorstxtfile = new File("/home/eugenia/Documents/TESE/Project/actors.txt");
    		//File actorstxtfile = new File(output_path + "\\actors.txt");
            //FileWriter actorstxtwriter = new FileWriter(actorstxtfile);

            // creating a constructor of file class and parsing an XML file
            File file = new File( filesReceived.get(0) );

            //list of lists to save information about each task: Name, Destination and Actor role
            List<List<String>> actorsInfoList = new ArrayList<>();

            // Defines a factory API that enables applications to obtain a parser that produces DOM object trees from XML documents.
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            // we are creating an object of builder to parse the  xml file.
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);

            /*here normalize method Puts all Text nodes in the full depth of the sub-tree underneath this Node, including attribute nodes, into a "normal"
            form where only structure separates Text nodes, i.e., there are neither adjacent Text nodes nor empty Text nodes. */
            doc.getDocumentElement().normalize();

            // Here nodeList contains all the nodes with name PropsAndValues.
            NodeList nodeList
                = doc.getElementsByTagName("PropsAndValues");

            // Iterate through all the nodes in NodeList using for loop
            for (int i = 1; i < nodeList.getLength(); ++i) {
                Node node = nodeList.item(i);

                if (node.getNodeType()
                    == Node.ELEMENT_NODE) {
                    Element tElement = (Element)node;
                    List<String> innerList = new ArrayList<>();
                    innerList.add(tElement.getElementsByTagName("Name").item(0).getTextContent());
                    innerList.add("");

                    System.out.println("Actor extracted:" + innerList.toString());

                    actorsInfoList.add(innerList);

                    //Write Content
                    //actorstxtwriter.write(tElement.getElementsByTagName("Name").item(0).getTextContent() + ';' + "" + '\n');
                }
            }
           // actorstxtwriter.close();
            return actorsInfoList;
    }

    /**
	 * Function that accesses the XML files of the events
	 * and extracts in a form of list all the events, with their name, user execution it and the destination element it is point to.
	 * @return List of Lists of Strings with the next format: [ [event1, actorOfEvent1, DestinationOfEvent1 ], [...],... ]
	 */
    public List<List<String>> extractListOfEvents() throws Exception{

        //list of lists to save information about each task: Name, Destination and Actor role
        List<List<String>> eventInfoList = new ArrayList<>();
				System.out.println();

				try{
	        // creating a constructor of file class and
	        // parsing an XML file
	     		//  File eventFile = new File( "/home/eugenia/Documents/TESE/Project/Business Event2.xml");
	        File eventFile = new File( filesReceived.get(1) );
					System.out.println("Event file received");
	        // Defines a factory API that enables applications to obtain a parser that produces DOM object trees from XML documents.
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

	        // we are creating an object of builder to parse the  xml file.
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        Document doc = db.parse(eventFile);

	        /*here normalize method Puts all Text nodes in the full depth of the sub-tree underneath this  Node, including attribute nodes, into a "normal"
	                form where only structure separates Text nodes, i.e., there are neither adjacent Text nodes nor empty Text nodes. */
	        doc.getDocumentElement().normalize();

	        // Here nodeList contains all the nodes with name PropsAndValues.
	        NodeList nodeList = doc.getElementsByTagName("PropsAndValues");

	        // Iterate through all the nodes in NodeList using for loop.
	        for (int i = 1; i < nodeList.getLength(); ++i) {
							System.out.println("Iterating the Node List");
	            Node node = nodeList.item(i);

							if (node.getNodeType() == Node.ELEMENT_NODE) {
	                Element tElement = (Element)node;

	                String originalString = tElement.getElementsByTagName("Next_to").item(0).getTextContent();
	                if (originalString.contains("\n") == true ){  //if we have a gateway with multiples possibilities
	                    String[] parts = originalString.split("\n");

	                    for (int j = 0; j< parts.length ; j++){
	                        List<String> innerList = new ArrayList<>();
	                        innerList.add(tElement.getElementsByTagName("Name").item(0).getTextContent());
	                        innerList.add(tElement.getElementsByTagName("User").item(0).getTextContent());
	                        innerList.add(parts[j]); //the Next_to

													System.out.println("Event extracted :" + innerList.toString());
	                        eventInfoList.add(innerList);
	                    }
	                }
	                else{
	                    List<String> innerList = new ArrayList<>();
	                    innerList.add(tElement.getElementsByTagName("Name").item(0).getTextContent());
	                    innerList.add(tElement.getElementsByTagName("User").item(0).getTextContent());
	                    innerList.add(tElement.getElementsByTagName("Next_to").item(0).getTextContent());

											System.out.println("Event extracted:" + innerList.toString());

	                    eventInfoList.add(innerList);
	                }
	            }

	        }
	        for (List<String> list : eventInfoList){
	            String originalString = list.get(2);
	            // Solve a bug in batch importation - TO REMOVE LATER
	            originalString = originalString.replace("\"" , "");
	            //////////////////////////////////////////////////////////////
	            if (originalString.contains("Definition:Business Event:") == true){
	                String replaceEventString = originalString.replace("Definition:Business Event:", "");
	                list.set(2, replaceEventString);
	            }
	            if (originalString.contains("Definition:Business Task:") == true){
	                String replaceTaskString = originalString.replace("Definition:Business Task:", "");
	                list.set(2, replaceTaskString);
	            }
	        }
	        //System.out.println("EventList: " + eventInfoList);
	        return eventInfoList;

				}catch (Exception e){
            return eventInfoList;
        }
    }
    /**
	 * Function that accesses the XML files of the activites
	 * and extracts in a form of list all the activities, with their name, user execution it and the next to element (activity) it is point to.
	 * @return List of Lists of Strings with the next format: [ [activity1, actorOfActivity1, NextToOfActivity1 ], [...],... ]
	 */
    public List<List<String>> extractListOfTasksInfo() throws Exception{

        //lista of list to save information about each task: Name, Destination and Actor role
        List<List<String>> taskInfoList = new ArrayList<>();

        // creating a constructor of file class and parsing an XML file
        //File eventFile = new File( "/home/eugenia/Documents/TESE/Project/Business Task2.xml");

        System.out.println("file to open = " + filesReceived.get(2) );

        File eventFile = new File( filesReceived.get(2) );

        // Defines a factory API that enables applications to obtain a parser that produces DOM object trees from XML documents.
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        // we are creating an object of builder to parse the  xml file.
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(eventFile);

        /*here normalize method Puts all Text nodes in the full depth of the sub-tree underneath this Node, including attribute nodes, into a "normal"
                form where only structure separates Text nodes, i.e., there are neither adjacent Text nodes nor empty Text nodes. */
        doc.getDocumentElement().normalize();

        // Here nodeList contains all the nodes with name PropsAndValues.
        NodeList nodeList = doc.getElementsByTagName("PropsAndValues");

        // Iterate through all the nodes in NodeList using for loop.
        for (int i = 1; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);

						if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element tElement = (Element)node;
								System.out.println("Before searching for Next_to...");
                String originalStringNextTo = tElement.getElementsByTagName("Next_to").item(0).getTextContent();
								System.out.println("...After searching for Next_to: " + originalStringNextTo);


								System.out.println("Before searching for Destination and Request Task...");
								String originalStringDestinationRequest = tElement.getElementsByTagName("Request_Task").item(0).getTextContent();
								System.out.println("...After searching for Destination and Request Task...");

								System.out.println("Before searching for Destination and Answer Task...");
                String originalStringDestinationAnswer = tElement.getElementsByTagName("Answer_Task").item(0).getTextContent();
									System.out.println("...After searching for Destination and Answer Task...");

                if (!(originalStringDestinationRequest == "")){ //if the task has a destination then we choose this Property

                    if (originalStringDestinationRequest.contains("\n") == true ){  //if we have a gateway with multiples possibilities
                        String[] parts = originalStringDestinationRequest.split("\n");

												for (int j = 0; j< parts.length ; j++){
                            List<String> innerList = new ArrayList<>();
                            innerList.add(tElement.getElementsByTagName("Name").item(0).getTextContent());
                            innerList.add(tElement.getElementsByTagName("User").item(0).getTextContent());
                            innerList.add(parts[j]); //the Destination
														innerList.add("Pedido");
													  innerList.add(tElement.getElementsByTagName("Business_x0020_Rule").item(0).getTextContent());

														System.out.println("Task extracted (gateway):" + innerList.toString());
                            taskInfoList.add(innerList);
                        }
                    }
                    else {
                        List<String> innerList = new ArrayList<>();
                        innerList.add(tElement.getElementsByTagName("Name").item(0).getTextContent());
                        innerList.add(tElement.getElementsByTagName("User").item(0).getTextContent());
												innerList.add(tElement.getElementsByTagName("Request_Task").item(0).getTextContent());
                        //innerList.add(tElement.getElementsByTagName("Classification").item(0).getTextContent());
                        innerList.add("Pedido");
                        innerList.add(tElement.getElementsByTagName("Business_x0020_Rule").item(0).getTextContent());

												System.out.println("Task extracted:" + innerList.toString());
                        taskInfoList.add(innerList);
                    }
                }
								if (!(originalStringDestinationAnswer == "")){ //if the task has a destination then we choose this Property

                    if (originalStringDestinationAnswer.contains("\n") == true ){  //if we have a gateway with multiples possibilities
                        String[] parts = originalStringDestinationAnswer.split("\n");

												for (int j = 0; j< parts.length ; j++){
                            List<String> innerList = new ArrayList<>();
                            innerList.add(tElement.getElementsByTagName("Name").item(0).getTextContent());
                            innerList.add(tElement.getElementsByTagName("User").item(0).getTextContent());
                            innerList.add(parts[j]); //the Destination
														innerList.add("Resposta");
													  innerList.add(tElement.getElementsByTagName("Business_x0020_Rule").item(0).getTextContent());

														System.out.println("Task extracted (gateway):" + innerList.toString());
                            taskInfoList.add(innerList);
                        }
                    }
                    else {
                        List<String> innerList = new ArrayList<>();
                        innerList.add(tElement.getElementsByTagName("Name").item(0).getTextContent());
                        innerList.add(tElement.getElementsByTagName("User").item(0).getTextContent());
												innerList.add(tElement.getElementsByTagName("Answer_Task").item(0).getTextContent());
                        //innerList.add(tElement.getElementsByTagName("Classification").item(0).getTextContent());
                        innerList.add("Resposta");
                        innerList.add(tElement.getElementsByTagName("Business_x0020_Rule").item(0).getTextContent());

												System.out.println("Task extracted:" + innerList.toString());
                        taskInfoList.add(innerList);
                    }
                }
								if ((originalStringNextTo.contains("\n") == true )
                && (originalStringDestinationAnswer == "")
                && (originalStringDestinationRequest == "")
                ){  //if we have a gateway with multiples possibilities

                    String[] parts = originalStringNextTo.split("\n");

                    for (int j = 0; j< parts.length ; j++){
                        List<String> innerList = new ArrayList<>();
                        innerList.add(tElement.getElementsByTagName("Name").item(0).getTextContent());
                        innerList.add(tElement.getElementsByTagName("User").item(0).getTextContent());
                        innerList.add(parts[j]); //the Next to
                        innerList.add(tElement.getElementsByTagName("Classification").item(0).getTextContent());
                        innerList.add(tElement.getElementsByTagName("Business_x0020_Rule").item(0).getTextContent());
                        taskInfoList.add(innerList);
                    }
                }
                if (!(originalStringNextTo.contains("\n") == true )
                && (originalStringDestinationAnswer == "")
                && (originalStringDestinationRequest == "")
                ){
                    List<String> innerList = new ArrayList<>();
                    innerList.add(tElement.getElementsByTagName("Name").item(0).getTextContent());
                    innerList.add(tElement.getElementsByTagName("User").item(0).getTextContent());
                    innerList.add(tElement.getElementsByTagName("Next_to").item(0).getTextContent());
                    innerList.add(tElement.getElementsByTagName("Classification").item(0).getTextContent());
                    innerList.add(tElement.getElementsByTagName("Business_x0020_Rule").item(0).getTextContent());
                    taskInfoList.add(innerList);
                }
            }
        }

        for (List<String> list : taskInfoList){
            String originalString = list.get(2);
            // Solve a bug in batch importation - TO REMOVE LATER
            originalString = originalString.replace("\"" , "");
            //////////////////////////////////////////////////////////////
            if (originalString.contains("Definition:Business Event:") == true){
                String replaceEventString = originalString.replace("Definition:Business Event:", "");
                list.set(2, replaceEventString);
            }
            if (originalString.contains("Definition:Business Task:") == true){
                String replaceTaskString = originalString.replace("Definition:Business Task:", "");
                list.set(2, replaceTaskString);
            }

        }
    /*System.out.println("TaskList");
    for (List<String> list : taskInfoList){
        System.out.println(list);
    }*/
    //System.out.println(taskInfoList);
    return taskInfoList;
    }

		public static List<List<String>> extractListOfBusinessRules() {

        //lista of list to save information about each task: Name, Destination and Actor role
        List<List<String>> businessRuleList = new ArrayList<>();

				try{


	        // creating a constructor of file class and parsing an XML file
	        //File ruleFile = new File( "/home/eugenia/Documents/TESE/Project/IMPROVEMENTS/V2/EX10/Business Rule.xml");

					File rulefile = new File( filesReceived.get(3) );

	        // Defines a factory API that enables applications to obtain a parser that produces DOM object trees from XML documents.
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

	        // we are creating an object of builder to parse the  xml file.
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        Document doc = db.parse(ruleFile);

	        /*here normalize method Puts all Text nodes in the full depth of the sub-tree underneath this Node, including attribute nodes, into a "normal"
	                form where only structure separates Text nodes, i.e., there are neither adjacent Text nodes nor empty Text nodes. */
	        doc.getDocumentElement().normalize();

	        // Here nodeList contains all the nodes with name PropsAndValues.
	        NodeList nodeList = doc.getElementsByTagName("PropsAndValues");

	        // Iterate through all the nodes in NodeList using for loop.
	        for (int i = 1; i < nodeList.getLength(); ++i) {
	            Node node = nodeList.item(i);

	            if (node.getNodeType() == Node.ELEMENT_NODE) {
	                Element tElement = (Element)node;

	                String inputQuestion = tElement.getElementsByTagName("Business_x0020_Rule_x0020_Input_x0020_Question").item(0).getTextContent();
	                //System.out.println("InputQuestion:" + inputQuestion);

	                if (!(inputQuestion == "")){ //if the taks has a destination then we choose this Property
	                    if (inputQuestion.contains("\n") == true ){  //if we have a gateway with multiples possibilities

	                        String[] parts = inputQuestion.split("\n");

	                        for (int j = 0; j< parts.length ; j++){
	                            List<String> innerList = new ArrayList<>();
	                            innerList.add(tElement.getElementsByTagName("Name").item(0).getTextContent());
	                            innerList.add(parts[j]);
	                            businessRuleList.add(innerList);
	                        }
	                    }
	                    else {
	                        List<String> innerList = new ArrayList<>();
	                        innerList.add(tElement.getElementsByTagName("Name").item(0).getTextContent());
	                        innerList.add(tElement.getElementsByTagName("Business_x0020_Rule_x0020_Input_x0020_Question").item(0).getTextContent());
	                        businessRuleList.add(innerList);
	                    }
	                }
	                else {
	                    System.out.println( "Did not defined the business rule input questions");
	                }
	            }
	            //System.out.println("BusinessRuleList:" + businessRuleList);
	        }
	    	return businessRuleList;

				}catch(Exception e){
			 		return businessRuleList;
				}
    }

    public static List<List<String>> extractListOfBusinessRulesInputQuestions() {

        //lista of list to save information about each task: Name, Destination and Actor role
        List<List<String>> businessRuleInputQuestionsList = new ArrayList<>();

				try{
	        // creating a constructor of file class and parsing an XML file
	        //File questionFile = new File( "/home/eugenia/Documents/TESE/Project/IMPROVEMENTS/V2/EX10/Business Rule Input Question.xml");
					File questionfile = new File( filesReceived.get(4) );

	        // Defines a factory API that enables applications to obtain a parser that produces DOM object trees from XML documents.
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

	        // we are creating an object of builder to parse the  xml file.
	        DocumentBuilder db = dbf.newDocumentBuilder();

	        Document doc = db.parse(questionFile);

	        /*here normalize method Puts all Text nodes in the full depth of the sub-tree underneath this Node, including attribute nodes, into a "normal"
	      form where only structure separates Text nodes, i.e., there are neither adjacent Text nodes nor empty Text nodes. */
	        doc.getDocumentElement().normalize();

	        // Here nodeList contains all the nodes with name PropsAndValues.
	        NodeList nodeList = doc.getElementsByTagName("PropsAndValues");

	        // Iterate through all the nodes in NodeList using for loop.
	        for (int i = 1; i < nodeList.getLength(); ++i) {

	            Node node = nodeList.item(i);

	            if (node.getNodeType() == Node.ELEMENT_NODE) {
	                Element tElement = (Element)node;
	                String inputQuestionNextBusinesTask = tElement.getElementsByTagName("Business_x0020_Task").item(0).getTextContent();

	                if (inputQuestionNextBusinesTask.contains("\n") == true ){  //if we have a gateway with multiples possibilities
	                    String[] parts = inputQuestionNextBusinesTask.split("\n");

	                    for (int j = 0; j< parts.length ; j++){
	                        List<String> innerList = new ArrayList<>();
	                        innerList.add(tElement.getElementsByTagName("Name").item(0).getTextContent());
	                        innerList.add(parts[j]);
	                        businessRuleInputQuestionsList.add(innerList);
	                    }
	                }
	                else {
	                    List<String> innerList = new ArrayList<>();
	                    innerList.add(tElement.getElementsByTagName("Name").item(0).getTextContent());
	                    innerList.add(tElement.getElementsByTagName("Business_x0020_Task").item(0).getTextContent());
	                    businessRuleInputQuestionsList.add(innerList);
	                }
	            }
	        }
	        for (List<String> list : businessRuleInputQuestionsList){
	            //System.out.println( "List:" + list);
	            String originalString = list.get(1);
	            //System.out.println( "originalString " + originalString);

	            if (originalString.contains("Definition:Business Event:") == true){
	                String replaceEventString = originalString.replace("Definition:Business Event:", "");
	                list.set(1, replaceEventString);
	            }
	            if (originalString.contains("Definition:Business Task:") == true){
	                String replaceTaskString = originalString.replace("Definition:Business Task:", "");
	                list.set(1, replaceTaskString);
	            }
	        }
	        //System.out.println( "Cheguei ao final");
	    		return businessRuleInputQuestionsList;

				}catch(Exception e){
						return businessRuleInputQuestionsList;
					}

    }

		public List<ObjectList> extractListOfObjectsInfo() throws Exception{

	        //lista of list to save information about each task: Name, Destination and Actor role
	        List<ObjectList> objectInfoList = new ArrayList<>();
	        // creating a constructor of file class and parsing an XML file
	        //File eventFile = new File( "/home/eugenia/Documents/TESE/Project/DiscoveryV6/TestarPOC3MultipleBO/Business Task.xml");

	        System.out.println("file to open = " + filesReceived.get(2) );

	        File objectFile = new File( filesReceived.get(2) );

	        // Defines a factory API that enables applications to obtain a parser that produces DOM object trees from XML documents.
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

	        // we are creating an object of builder to parse the  xml file.
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        Document doc = db.parse(objectFile);

	        /*here normalize method Puts all Text nodes in the full depth of the sub-tree underneath this Node, including attribute nodes, into a "normal"
	                form where only structure separates Text nodes, i.e., there are neither adjacent Text nodes nor empty Text nodes. */
	        doc.getDocumentElement().normalize();

	        // Here nodeList contains all the nodes with name PropsAndValues.
	        NodeList nodeList = doc.getElementsByTagName("PropsAndValues");

	        // Iterate through all the nodes in NodeList using for loop.

	        for (int i = 1; i < nodeList.getLength(); ++i) {

	            Node node = nodeList.item(i);
	            ObjectList list = new ObjectList();

	            if (node.getNodeType() == Node.ELEMENT_NODE) {

	                Element tElement = (Element)node;
	                String objectsString = tElement.getElementsByTagName("Business_x0020_Object").item(0).getTextContent();

	                if (!(objectsString == "")){
	                    if (objectsString.contains("\n") == true ){

	                        String[] parts = objectsString.split("\n");
	                        List<String> objectInnerList = new ArrayList<>();
	                        list.setTaskName(tElement.getElementsByTagName("Name").item(0).getTextContent());

	                        for (int j = 0; j< parts.length ; j++){
	                            objectInnerList.add(parts[j]); //add business object name
	                        }

	                        list.setList(objectInnerList);
	                        objectInfoList.add(list);
	                    }
	                    else{

	                        List<String> objectInnerList = new ArrayList<>();
	                        list.setTaskName(tElement.getElementsByTagName("Name").item(0).getTextContent());
	                        objectInnerList.add(tElement.getElementsByTagName("Business_x0020_Object").item(0).getTextContent());
	                        list.setList(objectInnerList);
	                        objectInfoList.add(list);
	                    }
	                }
	            }
	        }

	    /*System.out.println("ObjectList");
	    for (ObjectList list : objectInfoList){
	        printoutObjectList(list);
	    }*/

	    return objectInfoList;
	    }

    public static class ObjectList{

        private String taskName;
        private List<String> objectsList;

        public ObjectList(){
            taskName = null;
            objectsList = new ArrayList<>();

        }
        public void setTaskName(String taskName){
            this.taskName = taskName;
        }
        public String getTaskName(){
            return this.taskName;
        }
        public void setList(List<String> objectList){
            this.objectsList.clear();
            this.objectsList.addAll(objectList);
        }
        public List<String> getObjectList(){
            return this.objectsList;
        }
    }

    public static void printoutObjectList(ObjectList l) {
        System.out.println("TaskName: " + l.getTaskName());
        System.out.println("ObjectList: " + l.getObjectList());
    }

    public static class FinalObjectList{
        private String transaction;
        private String transactionStep;
        private String taskName;
        private List<String> objectsList;

        public FinalObjectList(){
            transaction = null;
            transactionStep = null;
            taskName = null;
            objectsList = new ArrayList<>();
        }

        public void setTaskName(String taskName){
            this.taskName = taskName;
        }
        public void setTransactionName(String transaction){
            this.transaction = transaction;
        }
        public void setTransactionStepName(String transactionStep){
            this.transactionStep = transactionStep;
        }
        public String getTaskName(){
            return this.taskName;
        }
        public String getTransactionName(){
            return this.transaction ;
        }
        public String getTransactionStepName(){
            return this.transactionStep;
        }
        public void setList(List<String> objectList){
            this.objectsList.clear();
            this.objectsList.addAll(objectList);
        }
        public List<String> getObjectList(){
            return this.objectsList;
        }
    }
    public static void printoutFinalObjectList(FinalObjectList l) {
        System.out.println("TransactionName: " + l.getTransactionName());
        System.out.println("TransactionStepName: " + l.getTransactionStepName());
        System.out.println("TaskName: " + l.getTaskName());
        System.out.println("ObjectList: " + l.getObjectList());
    }


    public static class Vertex{

        String name;
        String actor;
        String mapped;
        String classification;

        Vertex (String name) {
            this.name = name;
            this.actor = null;
            this.mapped = null;
            this.classification = null;

        }
        Vertex (String name, String actor, String mapped, String classification) {
            this.name = name;
            this.actor = actor;
            this.mapped = mapped;
            this.classification = classification;
        }
        public String getVertexName(){
            return this.name;
        }
        public String getVertexActor(){
            return this.actor;
        }
        public String getVertexMapped(){
            return this.mapped;
        }
        public String getVertexClassification(){
            return this.classification;
        }
        public void setName(String name){
            this.name = name;
        }
        public void setActor(String actor){
            this.actor = actor;
        }
        public void setMapped(String mapped){
            this.mapped = mapped;
        }
        public void setClassification(String classification){
            this.classification = classification;
        }

    }

    public static class Graph {

        private Map <Vertex,  LinkedList<Vertex>> adjacencyMap;
        private Map<String, Vertex> myVertices;
        private static final TreeSet<Vertex> EMPTY_SET = new TreeSet<Vertex>();
				private String demoActorInitiator;
        private String demoActorExecutor;

        /**
        * Construct empty Graph
        */
        public Graph(){
            adjacencyMap = new HashMap<Vertex,  LinkedList<Vertex>>();
            myVertices = new HashMap<String, Vertex>();
						demoActorInitiator = null;
            demoActorExecutor = null;

        }
		public void setActorIniciator(String actorInitiator){
            this.demoActorInitiator = actorInitiator;
        }

        public void setActorExecutor(String actorExecutor){
            this.demoActorExecutor = actorExecutor;
        }

        public String getActorIniciator(){
            return this.demoActorInitiator;
        }

        public String getActorExecutor(){
            return this.demoActorExecutor;
        }
        /**
        * Add a new vertex name with no neighbors (if vertex does not yet exist)
        *
        * @param name
        *            vertex to be added
        */
        public Vertex addVertex(String name, String actor, String mapped, String classification) {
            Vertex v;
            v = myVertices.get(name);
            if (v == null) {
                v = new Vertex(name, actor,mapped, classification);
                myVertices.put(name, v);
                adjacencyMap.putIfAbsent(v, new LinkedList<>());

            }
            return v;
        }
        public Vertex addVertex(String name) {
            Vertex v;
            v = myVertices.get(name);
            if (v == null) {
                v = new Vertex(name);
                myVertices.put(name, v);
                adjacencyMap.putIfAbsent(v, new LinkedList<>());

            }
            return v;
        }

        public void removeVertex(String label) {
            Vertex v = new Vertex(label);
            adjacencyMap.values().stream().forEach(e -> e.remove(v));
            adjacencyMap.remove(new Vertex(label));
        }

        public Vertex getVertex(String name) {
            return myVertices.get(name);
        }


        public void addEdge(String from, String to, String actorFrom, String actorTo, String classificationFrom, String classificationTo) {
            Vertex v, w;
            if (hasEdge(from, to))
                return;
            if ((v = getVertex(from)) == null)
                v = addVertex(from, actorFrom, null, classificationFrom);
            if ((w = getVertex(to)) == null)
                w = addVertex(to, actorTo, null, classificationTo);
            adjacencyMap.get(v).add(w);

        }
        public void addEdge(String from, String to) {
            Vertex v, w;
            if (hasEdge(from, to))
                return;
            if ((v = getVertex(from)) == null)
                v = addVertex(from);
            if ((w = getVertex(to)) == null)
                w = addVertex(to);
            adjacencyMap.get(v).add(w);
        }

        // for DEMO edges
        public void addDEMOEdge(String from, String to) {

            Vertex v, w;
            if (hasEdge(from, to))
                return;

            if ((v = getVertex(from)) == null)
                v = addVertex(from);

            if ((w = getVertex(to)) == null)
                w = addVertex(to);

            adjacencyMap.get(v).add(w);


        }

        public List<Vertex> getAdjVertices(String name) {
            return adjacencyMap.get(new Vertex(name));
        }

        /**
         * Return an iterator over the neighbors of Vertex named v
         * @param name the String name of a Vertex
         * @return an Iterator over Vertices that are adjacent
         * to the Vertex named v, empty set if v is not in graph
         */
        public Iterable<Vertex> adjacentTo(String name) {
            if (!hasVertex(name))
                return EMPTY_SET;
            return adjacencyMap.get(getVertex(name));
        }
        /**
         * Return an iterator over the neighbors of Vertex v
         * @param v the Vertex
         * @return an Iterator over Vertices that are adjacent
         * to the Vertex v, empty set if v is not in graph
         */
        public Iterable<Vertex> adjacentTo(Vertex v) {
            if (!adjacencyMap.containsKey(v))
                return EMPTY_SET;
            return adjacencyMap.get(v);
        }
        /**
         * Returns an Iterator over all Vertices in this Graph
         * @return an Iterator over all Vertices in this Graph
         */
        public Iterable<Vertex> getVertices() {
            return myVertices.values();
        }
        /**
         * Returns true iff v is in this Graph, false otherwise
         * @param name a String name of a Vertex that may be in
         * this Graph
         * @return true iff v is in this Graph
         */
        public boolean hasVertex(String name) {
            return myVertices.containsKey(name);
        }

        /**
         * Is from-to, an edge in this Graph. The graph is
         * undirected so the order of from and to does not
         * matter.
         * @param from the name of the first Vertex
         * @param to the name of the second Vertex
         * @return true iff from-to exists in this Graph
         */
        public boolean hasEdge(String from, String to) {

            if (!hasVertex(from) || !hasVertex(to))
                return false;
            return adjacencyMap.get(myVertices.get(from)).contains(myVertices.get(to));
        }

    }
    /*
    * Return a Graph's vertices and their list of linked nodes
    */
    public static void printGraph(Graph graph) {
        Map<Vertex,  LinkedList<Vertex>> map = graph.adjacencyMap;
        System.out.println("Graph:");
        for (Map.Entry<Vertex,  LinkedList<Vertex>> entry : map.entrySet()) {
            String name = entry.getKey().getVertexName();
            String actor = entry.getKey().getVertexActor();
            String classification = entry.getKey().getVertexClassification();
            LinkedList<Vertex> value = entry.getValue();
            if (value.size() != 0){
                System.out.println("From Vertex: " + name + "; ExecutingActor: " + actor  + "; Classification: " + classification);
                for (int i = 0; i < value.size(); i++){
                    System.out.println("To Vertex: " + entry.getValue().get(i).getVertexName());
                }
            }
            else {
                System.out.println("From Vertex: " + name + "; ExecutingActor: " + actor  + "; Classification: " + classification + "; To Vertex: " + null);
            }
        }
    }
    public static void printDEMOGraph(Graph graph) {
        Map<Vertex,  LinkedList<Vertex>> map = graph.adjacencyMap;
        System.out.println("Graph:");
        for (Map.Entry<Vertex,  LinkedList<Vertex>> entry : map.entrySet()) {
            String name = entry.getKey().getVertexName();
            String actor = entry.getKey().getVertexActor();
            String mapped = entry.getKey().getVertexMapped();
            LinkedList<Vertex> value = entry.getValue();
            if (value.size() != 0){
                System.out.println("From Vertex: " + name + "; ExecutingActor: " + actor + "; MappedFromProcess: " + mapped + "; To Vertex: " + entry.getValue().get(0).getVertexName());
            }
            else {
                System.out.println("From Vertex: " + name + "; ExecutingActor: " + actor + "; MappedFromProcess: " + mapped + "; To Vertex: " + null);
            }
        }
    }

    public static Graph createDEMOGraph() {

        Graph demoGraph = new Graph();

        demoGraph.addDEMOEdge("Decide the type of product to order", "Request product");
        demoGraph.addDEMOEdge("Request product", "Verify if execute product is possible");
        demoGraph.addDEMOEdge("Verify if execute product is possible", "Promise product");
        demoGraph.addDEMOEdge("Promise product", "Execute product");
        demoGraph.addDEMOEdge("Execute product", "Declare product");
        demoGraph.addDEMOEdge("Declare product", "Check product");
        demoGraph.addDEMOEdge("Check product", "Accept product");
        return demoGraph;
    }

    public static Graph createProcessGraph(List<List<String>> completeListInfo){

        Graph processGraph = new Graph();
            for (int element = 0; element < completeListInfo.size(); element++){
                for (int j = 0; j < completeListInfo.size(); j++){
                    if (completeListInfo.get(element).get(2).equals(completeListInfo.get(j).get(0)) ){
                        String from = completeListInfo.get(element).get(0);
                        String to = completeListInfo.get(element).get(2);
                        String actorFrom = completeListInfo.get(element).get(1);
                        String actorTo = completeListInfo.get(j).get(1);

                        if ( (completeListInfo.get(element).size() == 5) && (completeListInfo.get(j).size() == 5) ){
                            String classificationFrom = completeListInfo.get(element).get(3);
                            String classificationTo = completeListInfo.get(j).get(3);
                            processGraph.addEdge(from, to, actorFrom, actorTo, classificationFrom, classificationTo);
                        }
                        else if ( !(completeListInfo.get(element).size() == 5) && (completeListInfo.get(j).size() == 3)){
                            String classificationTo = completeListInfo.get(j).get(3);
                            processGraph.addEdge(from, to, actorFrom, actorTo, null, classificationTo);
                        }
                        else if ( (completeListInfo.get(element).size() == 3) && !(completeListInfo.get(j).size() == 5)) {
                            String classificationFrom = completeListInfo.get(element).get(3);
                            processGraph.addEdge(from, to, actorFrom, actorTo, classificationFrom, null);
                        }
                        else if ( !(completeListInfo.get(element).size() == 3) && !(completeListInfo.get(j).size() == 3)) {
                            processGraph.addEdge(from, to, actorFrom, actorTo, null, null);
                        }

                    }
                }

            }
            return processGraph;
    }


    public static String getNextActor( List<List<String>> completeListInfo, String elementName){

        for (int element = 0; element < completeListInfo.size(); element++){
            for (int j = 0; j < completeListInfo.size(); j++){
                if ( (completeListInfo.get(element).get(0).equals(elementName)) //if 1st name equals the element in the list
                && (completeListInfo.get(element).get(2).equals(completeListInfo.get(j).get(0)))){ //and To Element euquals the 1st in another list
                    String nextActor = completeListInfo.get(j).get(1);
                    return nextActor;
                }
            }
        }
        return null;
    }

    public static List<String> getThefirstElementofTheProcess(List<List<String>> completeListInfo){

        for (int element = 0; element < completeListInfo.size(); element++){
            int counter = 0;
            for (int element2 = 0; element2 < completeListInfo.size(); element2++){
                String el = completeListInfo.get(element).get(0);

                if ( !(el.equals(completeListInfo.get(element2).get(2))) ){
                    counter = counter + 1;
                }
            }
            if (counter == completeListInfo.size()){
                return completeListInfo.get(element);
            }
        }
        return null;
    }

    public static List<List<String>> getOrderedListInfo (List<List<String>> completeListInfo, List<String> firstElement){

        List<List<String>> completeAndOrderedList = new ArrayList<>();
        int fromHere =  0;
        completeAndOrderedList.add(firstElement);

				for (int i = 0; i < completeListInfo.size() ; i++){
            if(firstElement.get(0).equals(completeListInfo.get(i).get(0)) //If the first activity is behaving like a gateway
            && (!(firstElement.get(2).equals(completeListInfo.get(i).get(2)))) //i.e the origin is the same but the destination different
            ){                                                                                   // then we add it to the completeAndOrderedList
                completeAndOrderedList.add(completeListInfo.get(i));
            }
        }

        for (int outerloop = 0; outerloop < completeListInfo.size() ; outerloop++){
            for (int f = fromHere; f < completeAndOrderedList.size(); f++){
                List<List<String>> auxiliarList = new ArrayList<>();
                for (int j = 0; j < completeListInfo.size() ; j++){
                    if ( !(completeAndOrderedList.get(f).get(2) == null )){
                        if (completeAndOrderedList.get(f).get(2).equals(completeListInfo.get(j).get(0))
                        && (doNotContainList(completeListInfo.get(j),completeAndOrderedList) == true) ){ //if element name equals the Flows To or Destination name
                            auxiliarList.add(completeListInfo.get(j));
                        }
                    }
                }
                if (!(completeAndOrderedList.get(f).get(2) == null && !(auxiliarList.size()== 0)) ){
                    for (int aux = 0; aux< auxiliarList.size(); aux ++){
                        completeAndOrderedList.add(auxiliarList.get(aux));
                    }
                    fromHere = fromHere + 1;
                }
            }
        }
        return completeAndOrderedList;
    }

    public static Boolean doNotContainList( List<String> list, List<List<String>> completeAndOrderedList) {

        int counter = 0;
        for (int element = 0 ; element < completeAndOrderedList.size() ; element ++){
            if ( (completeAndOrderedList.get(element).get(0).equals(list.get(0)))
            && (completeAndOrderedList.get(element).get(1).equals(list.get(1)))
            && (completeAndOrderedList.get(element).get(2).equals(list.get(2))) ){
                counter = counter +1;
            }
        }
        if (counter == 0){
            return true;
        }
        else{
            return false;
        }

    }

		public static String[][] createTransactionsDependencies(List<List<String>> transactionsList, List<List<String>> actorsList,List<List<String>> transactionsDepHelper, List<List<String>> completeListInfo){

        int N = transactionsList.size() + 1;
        String[][] depMatrix = new String[N][N];
        depMatrix [0][0] = "Inv";

        for (int i = 1; i< N; i++){
            depMatrix [0][i] = transactionsList.get(i-1).get(0); //creating columns
            depMatrix [i][0] = transactionsList.get(i-1).get(0); //creating lines
        }
        for (int i = 0; i < transactionsDepHelper.size(); i++){
					outerloop:
            for (int j = 1; j < transactionsDepHelper.size(); j++){
                if (transactionsDepHelper.get(i).get(2).equals(transactionsDepHelper.get(j).get(1)) //if transaction iniciator equals an executor of a transaction that happened already
                ){
                    depMatrix [j + 1][i + 1] = "RaP";
                }
								if (transactionsDepHelper.get(j).get(1).equals(transactionsDepHelper.get(0).get(1))
                ){
                    continue outerloop;
                }
                if (notInAnyDestination(transactionsDepHelper.get(j).get(1), transactionsDepHelper) == true
								&& (!(transactionsDepHelper.get(j).get(1).equals(transactionsDepHelper.get(0).get(1))))
								){ //if the Origin taks is not a Destination in any transaction
                    String previous = "";
                    //then we need to check the previous task and see if it is
                    while (notInAnyDestination(previous, transactionsDepHelper) == true){ //repeat until find a previous task to be a Destination of any transation
                        List <String> previousList = getPreviousListElement(completeListInfo, transactionsDepHelper.get(j).get(1));
                        previous = previousList.get(0);
                    }
                    for (int x= 0; x< transactionsDepHelper.size(); x++){
                        if (transactionsDepHelper.get(x).get(2).equals(previous)){
                            depMatrix [j + 1][x + 1] = "RaP";
                        }
                    }
                }
            }
        }
        return depMatrix;
    }

		public static boolean notInAnyDestination (String origin, List<List<String>> transactionsDepHelper){

        for (int i= 0; i< transactionsDepHelper.size(); i++){
            if (transactionsDepHelper.get(i).get(2).equals(origin)){ //if origin Task is a destination in any transaction, return false
                return false;
            }
        }
        return true;
    }

    public static void printMatrix (String[][] depMatrix, int N){
        String result = "";
        for (int x = 0; x< N; x++){
            for (int y = 0; y< N; y++){
                result += depMatrix[x][y];
                result += " ";
            }
            result += "\n";
        }
        System.out.println (result);
    }

    public  List<List<String>> createTKView(List <Graph> graphs, List<List<String>> transactions){
        List<List<String>> tkview = new ArrayList<>();

        List<String> innerList = new ArrayList<>();
        innerList.add("TransactionKind");
        innerList.add("View");
        innerList.add("Request Decision");
        innerList.add("Request");
        innerList.add("Promise Decision ");
        innerList.add("Promise");
        innerList.add("Decline");
        innerList.add("After Decline Decision");
        innerList.add("Execute ");
        innerList.add("Declare");
        innerList.add("Decision Accept");
        innerList.add("Accept");
        innerList.add("Reject");
        innerList.add("Evaluate Rejection");
        innerList.add("Stop");
        tkview.add(innerList);

        for (int t = 0 ; t < transactions.size(); t++){
            Graph demoGraph = graphs.get(t);
            String mappedRequest = demoGraph.getVertex("Request product").getVertexMapped();
            String mappedDeclare = demoGraph.getVertex("Declare product").getVertexMapped();
            String mappedAccept = demoGraph.getVertex("Accept product").getVertexMapped();
						String mappedDecisionRequest = demoGraph.getVertex("Decide the type of product to order").getVertexMapped();
            String mappedExecute = demoGraph.getVertex("Execute product").getVertexMapped();

            List<String> innerListTransaction = new ArrayList<>();
            innerListTransaction.add(transactions.get(t).get(0)); //TransactionKind
            //innerListTransaction.add("Custom"); //View
            //innerListTransaction.add("CustomHappyFlowOnly"); //View
            innerListTransaction.add( getTkview_mode() ); //View

						if ( !(mappedDecisionRequest == null) ){
                innerListTransaction.add(mappedDecisionRequest); //Request Decision
            }
            else{
                innerListTransaction.add(""); //Request Decision
            }
            if ( !(mappedRequest == null) ){
                innerListTransaction.add(mappedRequest); //Request
            }
            else{
                innerListTransaction.add(""); //Request
            }
            innerListTransaction.add(""); //Promise Decision
            innerListTransaction.add(""); //Promise
            innerListTransaction.add(""); //Decline
            innerListTransaction.add(""); //After Decline Decision
						if ( !(mappedExecute == null) ){
                innerListTransaction.add(mappedExecute); //Execute
            }
            else{
                innerListTransaction.add(""); //Execute
            }
            if ( !(mappedDeclare == null) ){
                innerListTransaction.add(mappedDeclare); //Declare
            }
            else{
                innerListTransaction.add(""); //Declare
            }
            innerListTransaction.add(""); //Decision Accept
            if ( !(mappedAccept == null) ){
                innerListTransaction.add(mappedAccept); //Accept
            }
            else{
                innerListTransaction.add(""); //Accept
            }
            innerListTransaction.add(""); //Reject
            innerListTransaction.add(""); //Evaluate Rejection
            innerListTransaction.add(""); //Stop

            tkview.add(innerListTransaction);
        }
        return tkview;

    }



    public static List<String> getNextListElement( List<List<String>> completeListInfo, String elementName){ //tem de recer o list.get(x).get(2)

        for (int element = 0; element < completeListInfo.size(); element++){
            if ( (completeListInfo.get(element).get(0).equals(elementName)) //if 1st name equals the element in the list
            ){
                List <String> nextList= completeListInfo.get(element);
                return nextList;
            }
        }
        return null;

    }

    public static List<String> getPreviousListElement( List<List<String>> completeListInfo, String elementName){ //tem de recer o list.get(x).get(0)
        for (int element = 0; element < completeListInfo.size(); element++){
            if ( (completeListInfo.get(element).get(2).equals(elementName)) //if 1st name equals the element in the list
            ){
                List<String> previousList = completeListInfo.get(element);
                return previousList;
            }
        }
        return null;
    }

    public static boolean vertexIsATask( List<List<String>> completeListInfo, String vertexName){ //tem de recer o list.get(x).get(0)

        int counter = 0;
        for (int element = 0; element < completeListInfo.size(); element++){
            if ( (completeListInfo.get(element).get(0).equals(vertexName)) //if 1st name equals the element in the list
            &&  !(completeListInfo.get(element).size() == 5)){
                counter = counter + 1;
            }
        }
        if(counter != 0){
            return false;
        }
        if (counter == 0){
            return true;
        }
        return false;
    }

		public static List<String> mapExecuteAndReqDecisionAndReturnTheRestOfUnmappedElements(List<List<String>> completeAndOrderedList,List<List<String>> transactions, List<List<String>> actorList, Graph processGraph, List <Graph> graphs){

        Map<String, List<String>> actorsElements = new HashMap<String, List<String>>();
        Map<String, List<String>> actorsElements2 = new HashMap<String, List<String>>();
        Map<Vertex,  LinkedList<Vertex>> processMap = processGraph.adjacencyMap;

        for (int eachActor = 0; eachActor < actorList.size(); eachActor ++){
            List<String> listOfActorsElements = new ArrayList<>();
            List<String> listOfActorsElements2 = new ArrayList<>();
            for (int eachElement = 0 ; eachElement < completeAndOrderedList.size(); eachElement ++){
                if (completeAndOrderedList.get(eachElement).get(1).equals(actorList.get(eachActor).get(0))
                && !(listOfActorsElements.contains(completeAndOrderedList.get(eachElement).get(0)))){

                    listOfActorsElements.add(completeAndOrderedList.get(eachElement).get(0) );
                    listOfActorsElements2.add(completeAndOrderedList.get(eachElement).get(0) );
                }
            }
            actorsElements.put(actorList.get(eachActor).get(0), listOfActorsElements);
            actorsElements2.put(actorList.get(eachActor).get(0), listOfActorsElements);
        }
        for (Map.Entry<String,  List<String>> entry : actorsElements.entrySet()) {
            List<String> elements = entry.getValue();
            System.out.println("Actor:" + entry.getKey());
            for (int i = 0; i < elements.size(); i++){
                System.out.println("Elements:" + elements.get(i));
            }
        }
        //Percorrer o grafo e mapear o execute e rd
        for (Map.Entry<String,  List<String>> entry : actorsElements.entrySet()) {
            String uniqueList = "";
            //map the 1st Request Decision
            if (entry.getKey().equals(actorList.get(0).get(0)) == true){
                //System.out.println("entrou no 1st RD");
                int el = 0;
                while((entry.getKey().equals(completeAndOrderedList.get(el).get(1))  == true && (completeAndOrderedList.get(el).get(3).equals("")) )){
                //while 1st actor is the same as the actor of a list And classification is empty
                    //requestDecisionElements.add(completeAndOrderedList.get(el).get(0));
                    uniqueList = uniqueList + (completeAndOrderedList.get(el).get(0));
                    completeAndOrderedList.get(el).set(3, "Request Decision");
                    el = el +1;
                    //System.out.println("entrou no while" + el);
                    //System.out.println("unique list: " + uniqueList + " ");
                    //System.out.println("requestDecisionElements:" + requestDecisionElements);
                }
                for (int t = 0; t < transactions.size(); t++){
                    if (transactions.get(t).get(2).equals(actorList.get(0).get(0))){
                        Graph g = graphs.get(t);
                        //printDEMOGraph(g);
                        g.getVertex("Decide the type of product to order").setMapped(uniqueList); //map Element name
                        g.getVertex("Decide the type of product to order").setActor(actorList.get(0).get(0)); //map Element Actor
                        //printDEMOGraph(g);
                    }
                }
            }

            //map Execute
            for (int list = 0; list < completeAndOrderedList.size(); list++) {
                for (Map.Entry<Vertex,  LinkedList<Vertex>> entry1 : processMap.entrySet()) {
                    if (completeAndOrderedList.get(list).get(0).equals(entry1.getKey().getVertexName())){
                        LinkedList<Vertex> toVertexes = entry1.getValue();
                        //System.out.println("vertex name: " + entry1.getKey().getVertexName());


                        for (int i = 0; i < toVertexes.size(); i++){
                            //List<String> previousList = getPreviousListElement(completeAndOrderedList, entry1.getKey().getVertexName());
                            List<String> nextList = getNextListElement(completeAndOrderedList, toVertexes.get(i).getVertexName());
                            List<String> previousList = getPreviousListElement(completeAndOrderedList, entry1.getKey().getVertexName());
                            //System.out.println("previousList: " + previousList);
                            //System.out.println("nextList: " + nextList);

                            if (!(nextList == null)){
                                //If its and Execute followed by a Request
                                if ( (vertexIsATask(completeAndOrderedList, entry1.getKey().getVertexName())== true) //if veretex is a task
                                &&   (nextList.size() == 5) //If the next elemet its a Task
                                && ((nextList.get(3).equals("Pedido") || (nextList.get(3).equals("Resposta") ))) //and it is a declare task
                                && (entry1.getKey().getVertexClassification().equals("")) //And the previous element do not have classification
                                && (entry1.getKey().getVertexActor().equals(nextList.get(1)))//and they are in the same actor
                                //&& (previousList.size() == 3)//and if the previous element is an event
                                //&& (entry1.getKey().getVertexActor().equals(previousList.get(1)))
																){ //and they are in the same actor
                                    //System.out.println("Entrou no execute complex");
                                    //System.out.println("getVertexName: " + entry1.getKey().getVertexName());

                                    for (int t = 0; t < transactions.size(); t++){
                                        if (transactions.get(t).get(3).equals(entry1.getKey().getVertexActor())){ //if activities actor matches transaction executor actor
                                            //System.out.println("Entrou no If depois do execute complex");
                                            //System.out.println("transactions.get(t).get(3):" + transactions.get(t).get(3));
                                            //System.out.println("entry1.getKey().getVertexActor():" + entry1.getKey().getVertexActor());
                                            Graph g = graphs.get(t);

                                            if (g.getVertex("Execute product").getVertexMapped() == null){
                                                entry1.getKey().setClassification("Execute");
                                                //printDEMOGraph(g);
                                                g.getVertex("Execute product").setMapped( entry1.getKey().getVertexName()); //map Element name
                                                g.getVertex("Execute product").setActor(entry1.getKey().getVertexActor()); //map Element Actor
                                                printDEMOGraph(g);

                                            }
                                        }
                                    }
                                }
                                //If its a simple execute
                                //List<String> getNextNext = getNextListElement(completeAndOrderedList, nextList.get(0));
                                if ( (vertexIsATask(completeAndOrderedList, entry1.getKey().getVertexName())== true) //and is also a task
                                &&  (nextList.size() == 5) //If the next elemtn its a Task
                                && (nextList.get(3).equals("Resposta")) //and it is a declare task
                                && (entry1.getKey().getVertexClassification().equals("")) //And the previous element do not have classification
                                && (entry1.getKey().getVertexActor().equals(nextList.get(1)))//and they are in the same actor
                                ){

                                    //System.out.println("Entrou no execute simples");
                                    for (int t = 0; t < transactions.size(); t++){
                                        if (transactions.get(t).get(3).equals(entry1.getKey().getVertexActor())){ //if activities actor matches transaction executor actor
                                            Graph g = graphs.get(t);
                                            //System.out.println("g.getVertex(Execute product).getVertexClassification(): " + g.getVertex("Execute product").getVertexClassification());

                                            if (g.getVertex("Execute product").getVertexMapped() == null){ // if the execute of a tansaction where this ator is executor is still null
                                                entry1.getKey().setClassification("Execute");
                                                //printDEMOGraph(g);
                                                g.getVertex("Execute product").setMapped(entry1.getKey().getVertexName()); //map Element name
                                                g.getVertex("Execute product").setActor(entry1.getKey().getVertexActor()); //map Element Actor
                                                printDEMOGraph(g);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
        loop:
        for (Map.Entry<Vertex,  LinkedList<Vertex>> entry1 : processMap.entrySet()) {
            LinkedList<Vertex> toVertexes = entry1.getValue();
            //System.out.println("vertex name: " + entry1.getKey().getVertexName());

            for (int i = 0; i < toVertexes.size(); i++){
                //List<String> previousList = getPreviousListElement(completeAndOrderedList, entry1.getKey().getVertexName());
                List<String> nextList = getNextListElement(completeAndOrderedList, toVertexes.get(i).getVertexName());
                List<String> previousList = getPreviousListElement(completeAndOrderedList, entry1.getKey().getVertexName());
                //System.out.println("previousList: " + previousList);
                //System.out.println("nextList: " + nextList);

                if (!(previousList == null) && !(nextList == null)){
                    //MAP ACCEPT
                    //List<String> getPreviousPrevious = getPreviousListElement(completeAndOrderedList, previousList.get(0));
                    if ( (vertexIsATask(completeAndOrderedList, entry1.getKey().getVertexName())== true) //and is also a task
                    &&  (nextList.size() == 5) //If the next element its a Task
                    && ((previousList.size() == 5) || (previousList.size() == 3)) //and it is a declare task
                    && (entry1.getKey().getVertexClassification().equals("")) //And the current element do not have classification
                    && (entry1.getKey().getVertexActor().equals(nextList.get(1)))//and they are in the same actor
                    && (!(entry1.getKey().getVertexActor().equals(previousList.get(1))))//and they are in the same actor
										&& (previousList.get(3).equals("Resposta")) //And the previous element was an Answer
										){
                        //System.out.println("Entrou no accept complex ");
                        //System.out.println("entry1.getKey().getVertexClassification(): " + entry1.getKey().getVertexClassification());
                        for (int t = 0; t < transactions.size(); t++){
                            if (transactions.get(t).get(2).equals(entry1.getKey().getVertexActor())){ //if activities actor matches transaction initiator actor
                                //System.out.println("Entrou no IF do accept complex ");
                                Graph g = graphs.get(t);
                                if((g.getVertex("Accept product").getVertexMapped() == null)){
                                    entry1.getKey().setClassification("Accept");
                                    //printDEMOGraph(g);
                                    g.getVertex("Accept product").setMapped(entry1.getKey().getVertexName()); //map Element name
                                    g.getVertex("Accept product").setActor(entry1.getKey().getVertexActor()); //map Element Actor
                                    //printDEMOGraph(g);

                                    continue loop; //if he finds the first theen did not map the other

                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Chegou aos unmapped tasks");
        List<String> unmappedTasks = unmappedTasks(completeAndOrderedList, processGraph, graphs, transactions);
        System.out.println("Calculou os unmapped tasks");
        //System.out.println(unmappedTasks);
        return unmappedTasks;

    }


    public static List<String> unmappedTasks(List<List <String>> completeAndOrderedList, Graph processGraph, List <Graph> graphs, List<List <String>> transactions){

        List<String> unmappedTasks = new ArrayList<>();
        for (int i = 0; i< completeAndOrderedList.size(); i++) {
            if( completeAndOrderedList.get(i).size() == 5){
                // if elements actor correspnds to one of the actor of this transaction graph
                if (elementIsNotInAnyDemoGraph(completeAndOrderedList.get(i), graphs) == true){
                    unmappedTasks.add(completeAndOrderedList.get(i).get(0));
                }
            }
        }
        return unmappedTasks;
    }

    public static boolean elementIsNotInAnyDemoGraph(List <String> elementList, List<Graph> graphs){
        int counter = 0;
        for (Graph g: graphs){
            Map<Vertex,  LinkedList<Vertex>> processMap = g.adjacencyMap;
            outerloop:
            for (Map.Entry<Vertex,  LinkedList<Vertex>> entry : processMap.entrySet()) {

                Vertex v = entry.getKey();
                if( !(g.getVertex(v.getVertexName()).getVertexMapped() == null)){

                    if ( ((g.getActorExecutor().equals(elementList.get(1))) || (g.getActorIniciator().equals(elementList.get(1))))
                    && ((g.getVertex(v.getVertexName()).getVertexMapped().equals(elementList.get(0))) || (g.getVertex(v.getVertexName()).getVertexMapped().contains(elementList.get(0)))) ){ //if elements name is a mapped elemtn in DEMO graph then its mapped
                        return false;
                    }
                }
                else{
                    continue outerloop;
                }
            }
            counter = counter +1;
        }
        if (counter == graphs.size()){
            return true;
        }
        return false;

    }



    public static void createTIAndTE (List<List <String>> transactions,List<List <String>> completeAndOrderedList){
        Map<String, List<String>> actorsLanes = new HashMap<String, List<String>>();

        for (int t = 0; t < transactions.size(); t ++){
            List<String> listOfActorsElements = new ArrayList<>();
            String actorInit = "";
            actorInit = actorInit + "Initiator " +  transactions.get(t).get(0) + ": " + transactions.get(t).get(2)  ;
            String actorExec = "";
            actorExec = actorExec + "Executor " + transactions.get(t).get(0) + ": " + transactions.get(t).get(3) ;
            listOfActorsElements.add("");
            actorsLanes.put(actorInit, listOfActorsElements);
            actorsLanes.put(actorExec, listOfActorsElements);
        }

        for (Map.Entry<String,  List<String>> entry : actorsLanes.entrySet()) {
            //List<String> elements = entry.getValue();
            System.out.println("Lane:" + entry.getKey());
            /*for (int i = 0; i < elements.size(); i++){
                System.out.println("Elements:" + elements.get(i));
            }*/
        }
    }

    public static List<FinalObjectList> createFinalObjectList( List<ObjectList> objectList, List<List<String>> tkview){

        List<FinalObjectList> finalObjectList = new ArrayList<>();
        for (int i = 1; i < tkview.size(); i++){
            for (int j = 0; j < tkview.get(i).size(); j++){
                for (int x = 0; x < objectList.size(); x++){

                    //System.out.println("tkview.get(i).get(j):"  + tkview.get(i).get(j));

                    if (!(tkview.get(i).get(j) == null)

                    && (!(objectList.get(x).getObjectList() == null))

                    && (tkview.get(i).get(j).equals(objectList.get(x).getTaskName()))

                    ){ //if name of taks in tkview matches the taks name in objects lists

                        FinalObjectList finalList = new FinalObjectList();

                        finalList.setTransactionName(tkview.get(i).get(0)); //add TKx

                        finalList.setTransactionStepName(tkview.get(0).get(j)); //add TransactionStep (Execute, Accept, etc)

                        finalList.setTaskName(tkview.get(i).get(j)); //add task name

                        finalList.setList(objectList.get(x).getObjectList()); //add object name

                        finalObjectList.add(finalList);

                    }

                }
            }

        }
        //System.out.println("FinalObjectList:" + finalObjectList);
        return finalObjectList;

    }

		public static List<List<String>> createBusinesTasksAndRulesQuestion(List<List<String>> listOfTasksInfo,List<List<String>> listOfBusinessRulesInfo, List<List<String>> listOfBusinessRulesInputQuestionInfo ){

        List<List<String>> list = new ArrayList<>();
        List<List<String>> finalList = new ArrayList<>();
        for (int i = 0; i< listOfTasksInfo.size(); i++){
            for (int j = 0; j< listOfBusinessRulesInfo.size(); j++){
                for (int z = 0; z< listOfBusinessRulesInputQuestionInfo.size(); z++){
                    if ((listOfTasksInfo.get(i).get(4).equals(listOfBusinessRulesInfo.get(j).get(0)))
                    && (listOfBusinessRulesInfo.get(j).get(1).equals(listOfBusinessRulesInputQuestionInfo.get(z).get(0)))
                    ){
                        List<String> innerList = new ArrayList<>();
                        innerList.add(listOfTasksInfo.get(i).get(0));
                        innerList.add(listOfBusinessRulesInfo.get(j).get(0));
                        innerList.add(listOfBusinessRulesInputQuestionInfo.get(z).get(0));
                        innerList.add(listOfBusinessRulesInputQuestionInfo.get(z).get(1));
                        list.add(innerList);
                    }
                }
            }
        }
        for (int i = 0; i< list.size(); i++){
            if (doNotContainList(list.get(i),finalList) == true
            ){
                finalList.add(list.get(i));
            }
        }
        return finalList;
    }


}
