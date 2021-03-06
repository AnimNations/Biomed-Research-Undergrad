package facebook;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class ParseJson {
	
	

	public static void main(String[] args) {
		String[] files = {"GR1", "GR2", "GR3", "GR4", "GR5", "GR6", "GR7", "GR8", "GR9",
				"NR1", "NR2", "NR3", "NR4", "NR5", "NR6", "NR7", "NR8", "NR9",
				"T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11"};
		for(int i=0; i<files.length; i++){
			runProgram(files[i]);
		}
	}
	
public static void runProgram(String FILENAME){
	try{
		HashSet<String> memberNames = new HashSet<String>();
		HashSet<String> memberNames2 = new HashSet<String>(); //used to create frequency data
		//ArrayList<String> memberNames2 = new ArrayList<String>();

		String inputFileString = "/Users/Anim/workspace/Facebook_Project/"+FILENAME+".json";	
    	//read json file
    	JSONTokener tokener = new JSONTokener(new FileReader(inputFileString));
    	//get json object
    	JSONObject object = new JSONObject(tokener);
    	//get json array under "data"
    	JSONArray dataArray = (JSONArray) object.getJSONArray("data");
    	//System.out.println("number of feeds: " + dataArray.length());
    	
        String outputFileString = "/Users/Anim/workspace/Facebook_Project/"+FILENAME+"sorted.txt";
        File file = new File(outputFileString);
        BufferedWriter bw = null;
  		bw = new BufferedWriter(new FileWriter(file, true));   
  		
		
		getMemberNames(inputFileString);
		
		//loop each object to collect all names
		for(int j = 0;j < dataArray.length();j++){
			JSONObject data = (JSONObject) dataArray.getJSONObject(j);
			String memberName = data.getJSONObject("from").getString("name");
			memberNames2.add(memberName);    			
			String hashtag = "n/a";   			
			String numOfPost = "";    			
			String messagePost = data.getString("message");   			
			if(memberName.contains("Johannes Thrul") || memberName.contains("Tsp Study") || memberName.contains("Danielle Ramo")) {
			}
    			//check whether the message has a hashtag (remove the first 3 chars that may containing "#")
    			String removeNumOfPost;
    			if(messagePost.length() > 4){
    				removeNumOfPost = messagePost.substring(4,messagePost.length());
    				if(removeNumOfPost.contains("#")){
    					int index = removeNumOfPost.indexOf("#");
    					hashtag = removeNumOfPost.substring(index,removeNumOfPost.length());
    				}
    			}  				
			//get postType
			if(memberName.contains("Johannes Thrul") || memberName.contains("Tsp Study") || memberName.contains("Danielle Ramo")){
				if(hashtag.contains("TDII")) {
				} else if(!numOfPost.equals("")) {
				} else if(data.getString("type").equals("event")){
					messagePost = data.getString("description");
				} else {
				}
			}
			JSONObject comments;
			if(data.getJSONObject("comments") != null){
				comments = (JSONObject) data.getJSONObject("comments");
				JSONArray dataArrayInComments = (JSONArray) comments.getJSONArray("data");

				for(int k = 0;k < dataArrayInComments.length();k++){
					JSONObject dataInComment = (JSONObject) dataArrayInComments.getJSONObject(k);
	    			if(dataInComment == null)
	    				continue;
	    			if(dataInComment.getJSONObject("from") == null){
	    				memberName = "Participant";
	    			}else{
	    				memberName = dataInComment.getJSONObject("from").getString("name");
	    				memberNames2.add(memberName);
					}
	    			if(memberName.contains("Johannes Thrul") || memberName.contains("Tsp Study") || memberName.contains("Danielle Ramo")){
					}	
				}
			}
		}
		
	/* CREATE NETWORK ANALYTICS - NAMES & LINKS */
		network NETWORK = new network();
		ArrayList<String> names2= new ArrayList<String>(); //used to access all the names
		names2.addAll(memberNames2); //move all the names from hashset memberNames2 to ArrayList names
		for(int i=names2.size()-1; i>=0; i--){
			String s = names2.get(i);
			if (s.matches(".*[a-z].*")){ //if the name contains a letter, remove it (since we are dealing with ID's)
				names2.remove(s);
			}
		}
		Collections.sort(names2); //sorts the names in "String Order"
		for(String s : names2){
			NETWORK.addNode(new node(s)); //create all the nodes in the network
		}
		
		//create links
		NETWORK.setLinks();
		
		//old code to create Links
		/* //links all members together to track like and comment frequency
		ArrayList<nodeLink> links= new ArrayList<nodeLink>(); //used to store all the links
		for(int nameNum=0; nameNum<names.size()-1; nameNum++){
			for(int secNum=nameNum+1; secNum<names.size(); secNum++){
				String a= names.get(nameNum);
				String b= names.get(secNum);
				links.add(new nodeLink(a,b));
			}
		} */
		
    	//loop each object in "data" array
		for(int j = 0;j < dataArray.length();j++){
			JSONObject data = (JSONObject) dataArray.getJSONObject(j);
			String random = data.getJSONObject("from").getString("id");
			
			String memberName = data.getJSONObject("from").getString("name");
			String memberType = "Participant";
			memberNames.add(memberName);
			
			String postType = "User generated post";
			
			String referalToUser = "";
			
			String hashtag = "n/a";
			
			String numOfPost = "";
			
			String messagePost = data.getString("message");
			
			int numOfComments = 0;
			String messageComment = "n/a";
			
			String timestamp = data.getString("created_time");
			
			//if memberName is "Johannes Thrul" or "Tsp Study" or "Danielle Ramo", memberType is their name
			//if memberName is not the above names, memberType is "participant"
			if(memberName.contains("Johannes Thrul") || memberName.contains("Tsp Study") || memberName.contains("Danielle Ramo"))
				memberType = memberName;
			
			//get "referal to user" and "hashtag"
			if(!messagePost.equals("")){
				
				//deal with "#1-#90"
				//if the string starts with "#", check whether it is like "#1""#89",or "# 1""# 89", that is number of post with FB group
    			if(messagePost.charAt(0) == '#'){
    				if(messagePost.charAt(1) == ' '){
	    				if(Character.isDigit(messagePost.charAt(2)))
	    					numOfPost += messagePost.charAt(2);
	    				if(Character.isDigit(messagePost.charAt(3)))
	    					numOfPost += messagePost.charAt(3);
    				}else{
	    				if(Character.isDigit(messagePost.charAt(1)))
	    					numOfPost += messagePost.charAt(1);
	    				if(Character.isDigit(messagePost.charAt(2)))
	    					numOfPost += messagePost.charAt(2);
    				}		
    			}else if(Character.isDigit(messagePost.charAt(0))){  	    //if the string starts with a digit, like "1." "2. "
    				numOfPost += messagePost.charAt(0);
	    			if(Character.isDigit(messagePost.charAt(1)))
	    				numOfPost += messagePost.charAt(1);
    			}		

    			
    			//check whether the message has a hashtag (remove the first 3 chars that may containing "#")
    			String removeNumOfPost;
    			if(messagePost.length() > 4)
    			{
    				removeNumOfPost = messagePost.substring(4,messagePost.length());
    			
    				if(removeNumOfPost.contains("#")){
    					int index = removeNumOfPost.indexOf("#");
    					hashtag = removeNumOfPost.substring(index,removeNumOfPost.length());
    				}
    			}
			}
			
			//get postType
			if(memberName.contains("Johannes Thrul") || memberName.contains("Tsp Study") || memberName.contains("Danielle Ramo")){
				if(hashtag.contains("TDII"))
					postType = "TDII";
				else if(!numOfPost.equals(""))
					postType = "TSP daily post";
				else if(data.getString("type").equals("event")){
					messagePost = data.getString("description");
					postType = "Event";
				}
				else
					postType = "TSP other post";
			}
			
			//check if the post refers to users
			Iterator<String> iterator = memberNames.iterator();
			while(iterator.hasNext()){
				String currentName = iterator.next();
				if(messagePost.contains(currentName))
					referalToUser += currentName + " ";
			}
			
			//write a line of post
			String result = "";
			result = random + " | " + memberType + " | " + postType + " | " + referalToUser + " | " + hashtag + " | " + 
						numOfPost + " | " + messagePost + " | " + numOfComments + " | " + messageComment + " | " + timestamp;
			bw.write(result);
			bw.write("\n");
			
			//with each "like" in post, write a line
			ArrayList<String> peopleWhoLiked = new ArrayList<String>();
			int numberOfPostLikes = 0;
			if(data.getJSONObject("likes") != null){
				JSONArray dataArrayInLike = (JSONArray) data.getJSONObject("likes").get("data");
				 numberOfPostLikes= dataArrayInLike.length();
				for(int numOfLike = 0; numOfLike < dataArrayInLike.length();numOfLike++){
					JSONObject dataInLike = (JSONObject) dataArrayInLike.getJSONObject(numOfLike);
					String idOfLike = dataInLike.getString("id");
					peopleWhoLiked.add(idOfLike);
					memberType = "Participant";
					String postLikeType = postType + " like";
					numOfComments = 0;
					messageComment = "n/a";
					timestamp = "n/a";
					
	    			result = idOfLike + " | " + memberType + " | " + postLikeType + " | " + referalToUser + " | " + hashtag + " | " + 
    						numOfPost + " | " + messagePost + " | " + numOfComments + " | " + messageComment + " | " + timestamp;
	    			bw.write(result);
	    			bw.write("\n");
				}
			}
				
			ArrayList<String> peopleWhoCommented = new ArrayList<String>();
			int numberOfPostComments = 0;
			JSONObject comments;
			//if(data.getJSONObject("comments") = null)  //in case there is no comment within the post
				//continue;
			//else{
			if(data.getJSONObject("comments") != null){
				comments = (JSONObject) data.getJSONObject("comments");
				JSONArray dataArrayInComments = (JSONArray) comments.getJSONArray("data");
				//System.out.println(dataArrayInComments.length());
				numberOfPostComments= dataArrayInComments.length();
				for(int k = 0;k < dataArrayInComments.length();k++){
					JSONObject dataInComment = (JSONObject) dataArrayInComments.getJSONObject(k);
	    			if(dataInComment == null)
	    				continue;
	    			if(dataInComment.getJSONObject("from") == null){
	    				random = "n/a";
	    				memberType = "Participant";
	    				memberName = "Participant";
	    			}else{
	    				random = dataInComment.getJSONObject("from").getString("id");
	    				memberName = dataInComment.getJSONObject("from").getString("name");
	    				memberNames.add(memberName);
					}
	    			peopleWhoCommented.add(random);
	    			memberType = "Participant";
	    			if(memberName.contains("Johannes Thrul") || memberName.contains("Tsp Study") || memberName.contains("Danielle Ramo"))
	    				memberType = memberName;
	    			
					numOfComments += 1;
					
					messageComment = dataInComment.getString("message");
					timestamp = dataInComment.getString("created_time");
					
					//check if the comment has a hashtag
					if(messageComment.contains("#")){
						int index = messageComment.indexOf("#");
						hashtag = messageComment.substring(index,messageComment.length());
					}
					
					referalToUser = "";
					//check if the comment refers to users
	    			Iterator<String> iteratorComment = memberNames.iterator();
	    			while(iteratorComment.hasNext()){
	    				String currentName = iteratorComment.next();
	    				if(messageComment.contains(currentName))
	    					referalToUser += currentName + " ";
	    			}
						
					String commentType = "User generated post response comment";
					if(postType.contains("TDII"))
						commentType = "TDII response comment";
					else if(postType.contains("TSP daily post"))
						commentType = "TSP daily post response comment";
					else if(postType.contains("TSP other post"))
						commentType = "TSP other post response comment";
					else if(postType.contains("Event"))
						commentType = "Event response comment";

					result = "";
					result = random + " | " + memberType + " | " + commentType + " | " + referalToUser + " | " + hashtag + " | " + 
						numOfPost + " | " + messagePost + " | " + numOfComments + " | " + messageComment + " | " + timestamp;
					bw.write(result);
					bw.write("\n");
					
					//get the number of "like_count",how many likes there are, how many lines to write
					if(dataInComment.getString("like_count") != ""){
						String likeCount = dataInComment.getString("like_count");
						System.out.println("number of like in comment: " + likeCount);
						String commentLikeType = commentType + " like";
						int likeOfCount = Integer.parseInt(likeCount);
						for(int numOfCommentLike = 0; numOfCommentLike < likeOfCount; numOfCommentLike++){
	    					result = "n/a" + " | " + "n/a" + " | " + commentLikeType + " | " + referalToUser + " | " + hashtag + " | " + 
	        						numOfPost + " | " + messagePost + " | " + numOfComments + " | " + messageComment + " | " + "n/a";
	        				bw.write(result);
	        				bw.write("\n");
						}
						
					}
					
				}
			}    			
			//Display like data of the post
			String likeResult= "Number of Likes on the post = "+numberOfPostLikes;
			bw.write(likeResult);
			bw.write("\n");
			//remove this line if you want to see the members in time stamp order and frequency
			sortAndRemove(peopleWhoLiked);
			//
			if(peopleWhoLiked.size()!=0){
				
				//loops all likers for post
				for(int likers=0; likers < peopleWhoLiked.size(); likers++){
					String like1 = peopleWhoLiked.get(likers).toString();
					bw.write(like1); //Displays name of liker
					bw.write("\n");
				}
				
				//add like frequency to all links
				for(int likeLink1=0; likeLink1<peopleWhoLiked.size()-1; likeLink1++){
					String like1 = peopleWhoLiked.get(likeLink1).toString();
	    			for(int likeLink2=likeLink1+1; likeLink2<peopleWhoLiked.size(); likeLink2++){
	    				String like2 = peopleWhoLiked.get(likeLink2).toString();
						for(nodeLink l:NETWORK.getLinks()){
							if(l.getName1().equals(like1) && l.getName2().equals(like2) ){ //Checks to see if the link exists in the ArrayList
								l.addLike(); //if Link Exists in the ArrayList of links, then add 1 to the like count on that link
							}
						}	
	    			}
				}
				
			}
			//Display comment data of the post
			String commentResult= "Number of Comments on the post = "+numberOfPostComments;
			bw.write(commentResult);
			bw.write("\n");
			//remove this line if you want to see the members in time stamp order and frequency
			sortAndRemove(peopleWhoCommented);
			//
			if(peopleWhoCommented.size()!=0){
				//loops all commenters for post
				for(int commenters=0; commenters < peopleWhoCommented.size(); commenters++){
					String com1 = peopleWhoCommented.get(commenters).toString();
					bw.write(com1); //Displays name of commenter
					bw.write("\n");
					if(commenters!=peopleWhoCommented.size()-1){
						for(int nextCommenter=commenters+1; nextCommenter<peopleWhoCommented.size(); nextCommenter++){
							String com2 = peopleWhoCommented.get(nextCommenter).toString();
							for(nodeLink k:NETWORK.getLinks()){
								if(k.getName1().equals(com1) && k.getName2().equals(com2) ){ //Checks to see if the link exists in the ArrayList
									k.addComment(); //if Link Exists in the ArrayList of links, then add 1 to the comment count on that link
								}
							}
						}
					}
				}
			}
			bw.write("\n");
		}
		
		String analysisFinder = "$RESULTS: ";
		bw.write(analysisFinder);
		bw.write("\n");
	
		
		
		
		
	/* DISPLAY ALL LINKS IN THE NETWORK */
		//System.out.println(names);
		//links.get(0).addLike(); was testing if .addLike() works
		for(nodeLink l:NETWORK.getLinks()){
			bw.write( l.displayStats() );
			bw.write("\n");
		}
		bw.close();
	
		
		
		
		
	/* BEGIN MAKING JSON FILES */
		//Begin making the JSON files for d3.js
		JSONObject commentsD3 = new JSONObject();
		JSONObject likesD3 = new JSONObject();
		
		
		//Create JSON Files for D3 Website
		createCommentJSON(NETWORK, commentsD3, FILENAME);
		createLikeJSON(NETWORK, likesD3, FILENAME);
		
		
		
    }catch (Exception e) {
        e.printStackTrace();
    }
}
	
	
	/*
	 	will create the comments JSON file
	 	@param a - ArrayList of all the nodeLinks
	 	@param n - ArrayList of the names of all the different members
	 	@param comJSON - JSONObject that will be used to make JSON file for d3.js Comments Graphic
	*/
	public static void createCommentJSON(network NETWORK, JSONObject comJSON, String FILENAME){
		NETWORK.calculateCommentCentrality(); //calculate the node's centrality measures
		JSONArray listNodes = new JSONArray(); //Create a JSON array for the nodes
		int centralNodeValue = 0; //get value of centrality for central node
		for(node n : NETWORK.getNodes()){
			JSONObject currentID = new JSONObject();
			currentID.put("id", n.getName());
			//check if node is central node
			if(NETWORK.isCentralCommentNode(n)){
				currentID.put("group", 2);
				centralNodeValue = n.getCommentCentrality();
			}
			else currentID.put("group", 1);
			listNodes.put(currentID);
		}
		comJSON.put("nodes", listNodes);
		
		JSONArray listLinks = new JSONArray(); //Create a JSON array for the links
		for(nodeLink l : NETWORK.getLinks()){
			JSONObject currentLink = new JSONObject();
			currentLink.put("source", l.getName1());
			currentLink.put("target", l.getName2());
			currentLink.put("value", l.getComments());
			listLinks.put(currentLink);
		}
		comJSON.put("links", listLinks);
		
		JSONArray listInfo = new JSONArray(); //Create a JSON array for the info
			
			//Create a JSONObject for Density
			JSONObject density = new JSONObject();
			density.put("id", "Density");
			//double d = ; //calculate the Density and store as d
			density.put("value", NETWORK.calculateCommentDensity());
			listInfo.put(density); //store density into 'info' JSONArray
			
		/*	JSONObject l = new JSONObject();
			l.put("id", "l");
			l.put("value", NETWORK.getCommentL());
			listInfo.put(l);
			
			JSONObject g = new JSONObject();
			g.put("id", "g");
			g.put("value", NETWORK.getG());
			listInfo.put(g); 	*/
			
			JSONObject avgDegree = new JSONObject();
			avgDegree.put("id", "Average Degree");
				double avgD = NETWORK.calcAvgCommentDegree();
			avgDegree.put("value", avgD);
			listInfo.put(avgDegree);
			
			JSONObject central = new JSONObject();
			central.put("id", "Central Value");
			central.put("value", centralNodeValue);
			listInfo.put(central);
			
		comJSON.put("info", listInfo);
		
		try (FileWriter file = new FileWriter("/Users/Anim/workspace/Facebook_Project/generate/"+FILENAME+"Comments.json")) {

            file.write(comJSON.toString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
	
	/*
	 	will create the likes JSON file
	 	@param a - ArrayList of all the nodeLinks
	 	@param n - ArrayList of the names of all the different members
	 	@param comJSON - JSONObject that will be used to make JSON file for d3.js Comments Graphic
	*/
	public static void createLikeJSON(network NETWORK, JSONObject likeJSON, String FILENAME){
		NETWORK.calculateLikeCentrality();
		JSONArray listNodes = new JSONArray(); //Create a JSON array for the nodes
		int centralNodeValue = 0;
		for(node n : NETWORK.getNodes()){		
			JSONObject currentID = new JSONObject();
			currentID.put("id", n.getName());
			//check if node is central node
			if(NETWORK.isCentralLikeNode(n)){
				currentID.put("group", 2);
				centralNodeValue = n.getLikeCentrality();
			}
			else currentID.put("group", 1);
			listNodes.put(currentID);
		}
		likeJSON.put("nodes", listNodes);
		
		JSONArray listLinks = new JSONArray(); //Create a JSON array for the links
		for(nodeLink l : NETWORK.getLinks()){
			JSONObject currentLink = new JSONObject();
			currentLink.put("source", l.getName1());
			currentLink.put("target", l.getName2());
			currentLink.put("value", l.getLikes());
			listLinks.put(currentLink);
		}
		likeJSON.put("links", listLinks);
		
		JSONArray listInfo = new JSONArray(); //Create a JSON array for the info
		
		//Create a JSONObject for Density
		JSONObject density = new JSONObject();
		density.put("id", "Density");
		//double d = ; //calculate the Density and store as d
		density.put("value", NETWORK.calculateLikeDensity());
		listInfo.put(density); //store density into 'info' JSONArray
		
	/*	JSONObject l = new JSONObject();
		l.put("id", "l");
		l.put("value", NETWORK.getLikeL());
		listInfo.put(l);
		
		JSONObject g = new JSONObject();
		g.put("id", "g");
		g.put("value", NETWORK.getG());
		listInfo.put(g);	*/
		
		JSONObject avgDegree = new JSONObject();
		avgDegree.put("id", "Average Degree");
			double avgD = NETWORK.calcAvgLikeDegree();
		avgDegree.put("value", avgD);
		listInfo.put(avgDegree);
		
		JSONObject central = new JSONObject();
		central.put("id", "Central Value");
		central.put("value", centralNodeValue);
		listInfo.put(central);
	
		likeJSON.put("info", listInfo);
	
		try (FileWriter file = new FileWriter("/Users/Anim/workspace/Facebook_Project/generate/"+FILENAME+"Likes.json")) {
	
	        file.write(likeJSON.toString());
	        file.flush();
	
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		
	}
	
	/* 	Sorts an ArrayList in alphabetical order
	 	@param list - the ArrayList you wish to sort
	 */
	public static void sortAndRemove(ArrayList<String> list){
		Collections.sort(list); //sorts the list by "String Order"	
		LinkedHashSet<String> remover = new LinkedHashSet<String>(); //Creates a LinkedHashset used to removed duplicate entries		
		remover.addAll(list); //Copies all entries into the LinkedHashset		
		list.clear(); //Clears the ArrayList (removes all entries)		
		list.addAll(remover); //Puts all the entries from the LinkedHashSet back into the ArrayList
		//LinkedHashSets automatically removes all duplicate entries, that's why this works.
	}
	public static void getMemberNames(String inputFileString){
		
		try{
        	//read json file
        	JSONTokener tokener = new JSONTokener(new FileReader(inputFileString));
        	//get json object
        	JSONObject object = new JSONObject(tokener);
        	//get json array under "data"
        	JSONArray dataArray = (JSONArray) object.getJSONArray("data");
        	//System.out.println("number of feeds: " + dataArray.length());
        	
        	//create a hashset to save all members' names
        	HashSet<String> memberNames = new HashSet<String>();
        	
        	//loop each object in "data" array
    		for(int j = 0;j < dataArray.length();j++){
    			JSONObject data = (JSONObject) dataArray.getJSONObject(j);
    			//String random = data.getJSONObject("from").getString("id");
    			
    			String memberName = data.getJSONObject("from").getString("name");
    			memberNames.add(memberName);

    				
    			JSONObject comments;
    			if(data.getJSONObject("comments") == null){  //in case there is no comment within the post
    				continue;
    			}else{
    				comments = (JSONObject) data.getJSONObject("comments");
    				JSONArray dataArrayInComments = (JSONArray) comments.getJSONArray("data");
    				//System.out.println(dataArrayInComments.length());
    				
    				for(int k = 0;k < dataArrayInComments.length();k++){
    					JSONObject dataInComment = (JSONObject) dataArrayInComments.getJSONObject(k);
    	    			if(dataInComment == null)
    	    				continue;
    	    			if(dataInComment.getJSONObject("from") == null){
    	    				memberName = "Participant";
    	    			}else{
    	    				memberName = dataInComment.getJSONObject("from").getString("name");
    	    				memberNames.add(memberName);
    					}
    					
    				}
    			}
    		}

        }catch (Exception e) {
            e.printStackTrace();
        }
		
	}
}