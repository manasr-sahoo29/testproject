<%@page contentType="text/html" import="imedix.cook,imedix.rcdpStats,java.util.*,imedix.dataobj,com.google.gson.Gson, com.google.gson.JsonArray, com.google.gson.JsonPrimitive" %>
<%@page errorPage="error.jsp" %> 
<%@include file="..//includes/chkcook.jsp" %>

<!-- bgcolor=#C8DCE1 --> 

<%	
	cook cookx = new cook();
	String ccode = cookx.getCookieValue("center", request.getCookies ());
	String cname = cookx.getCookieValue("centername", request.getCookies ());
	cname = cname.substring(0,cname.lastIndexOf(" "));
	String str= "<I>Online Telemedicine Center </I><br><b>"+cname + "</b>";
	//out.println(str.toUpperCase());
	//String ccode="",cname;
	String path = request.getRealPath("/");
	rcdpStats dpStats = new rcdpStats(request.getRealPath("/"));
	Vector v = (Vector)dpStats.getGenderData("XXXX");
	//out.println( v.size() );
	String jsonGenderStr="", jsonGenderLabel="";
	for (int i = 0; i < v.size(); i++){
		dataobj innerObj = (dataobj)v.elementAt(i);
		jsonGenderStr += "" + innerObj.getValue("nop")+",";	
		jsonGenderLabel += "" + innerObj.getValue("sex")+",";	
			
	} 
	jsonGenderStr = jsonGenderStr.substring(0,jsonGenderStr.length()-1);
	jsonGenderLabel = jsonGenderLabel.substring(0,jsonGenderLabel.length()-1);
	//////////////////////////////////////////////////////////////////////////////////////

	Vector diseasev = (Vector)dpStats.getDiseaseData("XXXX");
	//out.println( diseasev.size() );
	String jsonDiseaseStr="", jsonDiseaseLabel="";
	for (int i = 0; i < diseasev.size(); i++){
		dataobj innerObj = (dataobj)diseasev.elementAt(i);
		if (Integer.parseInt(innerObj.getValue("nop"))>10) {
			jsonDiseaseStr += "" + innerObj.getValue("nop")+",";	
			jsonDiseaseLabel += "" + innerObj.getValue("class")+",";	
		}	
	} 
	jsonDiseaseStr = jsonDiseaseStr.substring(0,jsonDiseaseStr.length()-1);
	jsonDiseaseLabel = jsonDiseaseLabel.substring(0,jsonDiseaseLabel.length()-1);

	///////////////////////////////////////////////////////////////////////////////////////
	dataobj agev = (dataobj)dpStats.getAgeData("XXXX");
	String jsonAgeStr="", jsonAgeLabel="A,E,C,T,I,N";
	jsonAgeStr += "" + agev.getValue("A")+",";
	jsonAgeStr += "" + agev.getValue("E")+",";
	jsonAgeStr += "" + agev.getValue("C")+",";
	jsonAgeStr += "" + agev.getValue("T")+",";
	jsonAgeStr += "" + agev.getValue("I")+",";
	jsonAgeStr += "" + agev.getValue("N")+"";

	////////////////////////////////////////////////////////////////////////////////////////
	Vector centerv = (Vector)dpStats.getCenterData("XXXX");
	//out.println( diseasev.size() );
	String jsonCenterStr="", jsonCenterLabel="";
	for (int i = 0; i < centerv.size(); i++){
		dataobj innerObj = (dataobj)centerv.elementAt(i);
		if (Integer.parseInt(innerObj.getValue("PATIENTS"))>10) {
			jsonCenterStr += "" + innerObj.getValue("PATIENTS")+",";	
			jsonCenterLabel += "" + innerObj.getValue("CODE")+",";	
		}	
	} 
	jsonCenterStr = jsonCenterStr.substring(0,jsonCenterStr.length()-1);
	jsonCenterLabel = jsonCenterLabel.substring(0,jsonCenterLabel.length()-1);
	
	////////////////////////////////////////////////////////////////////////////////////////
	Vector getTpatQRefToDataVC = (Vector)dpStats.getTpatQRefToData("XXXX");
	//out.println( diseasev.size() );
	String jsonPatQRefToStr="", jsonPatQRefToLabel="";
	for (int i = 0; i < getTpatQRefToDataVC.size(); i++){
		dataobj innerObj = (dataobj)getTpatQRefToDataVC.elementAt(i);
		if (Integer.parseInt(innerObj.getValue("PATIENTS"))>1) {
			jsonPatQRefToStr += "" + innerObj.getValue("PATIENTS")+",";	
			jsonPatQRefToLabel += "" + innerObj.getValue("assignedhos")+",";	
		}	
	} 
	jsonPatQRefToStr = jsonPatQRefToStr.substring(0,jsonPatQRefToStr.length()-1);
	jsonPatQRefToLabel = jsonPatQRefToLabel.substring(0,jsonPatQRefToLabel.length()-1);
	
	////////////////////////////////////////////////////////////////////////////////////////
	Vector getTpatQRefByDataVC = (Vector)dpStats.getTpatQRefByData("XXXX");
	//out.println( diseasev.size() );
	String jsonPatQRefByStr="", jsonPatQRefByLabel="";
	for (int i = 0; i < getTpatQRefByDataVC.size(); i++){
		dataobj innerObj = (dataobj)getTpatQRefByDataVC.elementAt(i);
		//String dataObjStr = innerObj.getAllKey();
		//out.println(dataObjStr);
		if (Integer.parseInt(innerObj.getValue("PATIENTS"))>10) {
			jsonPatQRefByStr += "" + innerObj.getValue("PATIENTS")+",";	
			jsonPatQRefByLabel += "" + innerObj.getValue("refer_center")+",";	
		}	
	} 
	jsonPatQRefByStr = jsonPatQRefByStr.substring(0,jsonPatQRefByStr.length()-1);
	jsonPatQRefByLabel = jsonPatQRefByLabel.substring(0,jsonPatQRefByLabel.length()-1);	
	
%>
				
<HTML>
<BODY background="../images/txture.jpg">

<center>
	<BR><BR>

		<font color=#2C432D size="3px" ><%=str.toUpperCase()%></font>
<p align = 'justify' style="padding: 15px 15px 15px 15px">
<b>Disclaimer</b>: <font color=MAROON>This is a non-commercial website meant only for research purposes and building awareness about telehealth. The site may be withdrawn for technical and/or administrative reasons without prior notice. Maintaining security of patients identity and sensitive medical data is the users responsibility. The information and advice published or made available through this web site and iMediX System is not intended to replace the services of a physician, nor does it constitute a doctor-patient relationship. Information on this web site is not a substitute for professional medical advice. Physicians should apply their own discretion to use the information on this web site for diagnosing or treating a medical or health condition. IIT Kharagpur is not liable for any direct or indirect claim, loss or damage resulting from use of this web site and/or any web site(s) linked to/from it. 
</p>
	
</center>
</BODY>
</HTML>

