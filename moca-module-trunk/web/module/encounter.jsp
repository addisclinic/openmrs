<html>
<head>
<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<openmrs:require privilege="View Moca Queue" otherwise="/login.htm" redirect="/module/moca/queue.htm" />
	
<%@ include file="/WEB-INF/template/header.jsp" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/moca/jquery-1.2.6.js"></script>
<script language="javascript">

function backToQueue() {
  document.location.href= "queue.form"
}

function clearList(){
    var lst = document.getElementById('diagnosisList');
	while(lst.hasChildNodes()){
		lst.removeChild(lst.lastChild);
	}
    var hiddenList = document.getElementById('HiddenDiagnoses');
    hiddenList.value = "";
}

/* Concept dictionary search */
function conceptDictSearch(searchBox) {
  var phrase = document.searchForm.searchInput.value;
  var source = document.getElementById('conceptSourceName').value;
  jQuery.get("${pageContext.request.contextPath}/moduleServlet/moca/conceptSearchServlet",
             {'phrase':phrase,'conceptSourceName':source},
             function(data) {
               var lst = document.getElementById('searchResults');
               var count = 0;
               while (count < lst.childNodes.length){
                lst.removeChild(lst.childNodes[count]);
               }
               var results = data.split(",");
               count = 0;
               while (count < results.length){
               	var item = document.createElement('a');
               	var oneResult = results[count].split("|");
               	if(oneResult.length > 1){
               		item.id = oneResult[1];
               		item.innerHTML = oneResult[0];
				}else{
					item.innerHTML = oneResult[0];
				}
				jQuery(item).attr('href', '#');
  				item.onclick=function(){addDiagnosis(this)}; 
  				lst.appendChild(item);
  				lst.appendChild(document.createElement('br'));
               	count = count+1;
               }
             });
}

function addDiagnosis(item){
	var idNum = item.id;
    var name = item.innerHTML;
	var lst = document.getElementById('diagnosisList');
	var numDiagnoses = lst.childNodes.length / 2 + 1;
	var diagnosis = document.createElement('text');
  	diagnosis.innerHTML = numDiagnoses + ". " + name;
  	diagnosis.id = name;
	lst.appendChild(diagnosis);
	lst.appendChild(document.createElement('br'));
	
	var hiddenList = document.getElementById('HiddenDiagnoses');
	var src = document.getElementById('conceptSourceName').value;
	if(src == "Default"){
		hiddenList.value = hiddenList.value + name + ",";
	}else{
		hiddenList.value = hiddenList.value + name + " | " + src + " ID " + idNum + ",";
	}
	
}

function retake() {
  var msg = prompt("Message to Request More Patient Information from Referring Clinician: ",'');
  if(msg != null){
  var queueID = document.getElementById('queueItemId').value;
  jQuery.post("${pageContext.request.contextPath}/moduleServlet/moca/retakeServlet",
             {'msg':msg,'queueItemId':queueID},
             function(data) {
                if(data == "OK") {
                 document.location.href= "queueDeferred.form"
                }
                else {
                 alert("Error: Could not send message. Check OpenMRS Configuration settings");
                }
             });
  }
}
</script>

<style>

html, body {
margin:0;
padding:0;
border:0;
height:96%;
width:100%;
}

.leftBox {
  border: 1px solid #6D9BC5;
  width: 28%;
  height: 100%;
  float: left;
  background-color: #C6D9F1;
  overflow:auto;
  overflow-x: hidden;
  scrollbar-base-color: transparent;
  scrollbar-arrow-color: transparent;
}

.rightBox {
  border: 1px solid #6D9BC5;
  width: 70%;
  height: 100%;
  float: left;
}

.bigContainer{
  width: 100%;
  height: 100%;
  position: relative;
}

.foot {
 float:left;
 clear:both;
}

h1 {
  font-size:16px;
  font-style:normal;
  font-family:verdana;
  color:#000000;
  margin:0px; 
  padding:8px;
  border:0px;
}

h3{
  color:#000000;
  background-color: #FFFFFF;
  font-size:13px;
  font-style:normal;
  font-family:verdana;
  padding:8px;
}

p{
  font-size:11px;
  font-style:normal;
  font-family:verdana;
  padding-left:8px;
  padding-right:8px;
}

div.indented{
  padding-left:8px;
  padding-right:8px;
}

indent{
  padding-left:8px;
  padding-right:8px;
}

blueBackground{
  color:#000000;
  font-size:12px;
  font-style:normal;
  font-family:verdana;
  background-color: #C6D9F1;
  border:0;
  padding:5px;
  padding-left:10px;
}

input.btn{
  font-size:11px;
  font-style:normal;
  font-family:verdana;
  width: 25%;
}

input.t3{
  display:none;
}

textarea.t1{
  color:#000000;
  font-size:11px;
  font-style:normal;
  font-family:verdana;
  width:90%
}

textarea.t2{
  color:#000000;
  font-size:11px;
  font-style:normal;
  font-family:verdana;
  background-color: #C6D9F1;
  height: 42%;
  border:0;
  width:100%;
  padding:5px;
}

textarea.t3{
  color:#000000;
  font-size:11px;
  font-style:normal;
  font-family:verdana;
  background-color: #C6D9F1;
  width:100%;
  border:0;
  padding:5px;
}


dropDownBox{
  color:#000000;
  font-size:11px;
  font-style:normal;
  font-family:verdana;
  width:100%;
}

input.collapseButton {
  color:#000000;
  background-color: #FFFFFF;
  font-size:12px;
  font-style:normal;
  font-family:verdana;
  border: none;
}

#searchResults {
	max-height:300px;
	overflow: auto;
	overflow-x: hidden;
	scrollbar-base-color: transparent;
    scrollbar-arrow-color: transparent;
}

</style>
</head>
<body>
<div class="bigContainer" id="container">
	<div class="leftBox" id="leftContainer">
		<h1><a href="${pageContext.request.contextPath}/patientDashboard.form?patientId=${object.queueItem.patient.patientId }">${object.queueItem.patient.familyName}, ${object.queueItem.patient.givenName}</a> (<spring:message code="moca.id" /> ${object.queueItem.patient.patientIdentifier}) ${object.queueItem.patient.age} ${object.queueItem.patient.gender}</h1>
		<h3><spring:message code="moca.status" />: ${object.status}</h3>
		<div id="pastDiagnoses" class="patientInfo"><p>${object.existingDiagnoses}</p></div>
		<h3><spring:message code="moca.patient_visit_responses" /></h3>
		<div id="patientInfoBox" class="blueBackground"><p>${object.patientResponses}</p></div>
		<h3><spring:message code="moca.recommendations" /></h3>
		<div id="diagnosisBox" class="blueBackground">
			<p><spring:message code="moca.select_vocab" />  <select id="conceptSourceName" name="conceptSourceName"><c:forEach var="source" items="${object.conceptSources}"><option>${source}</option></c:forEach></select></p>
			<form name="searchForm">
				<input type="hidden" id="queueItemId" name="queueItemId" value="${object.queueItem.queueItemId}"/>
				<input type="hidden" id="encounterId" name="encounterId" value="${object.queueItem.encounterId}"/> 
				<p><input type="text" name="searchInput"/>
				<input type="submit" onclick="conceptDictSearch(); return false;" id="searchButton" value="Search for Diagnosis to Add"><br></p>
				<div id="searchResults" class="indented"></div>
				<div id="horizBar2" class="indented"><hr align=left noshade size=4 width=90% style="color: white;"></div>
			</form>
			<form name="input" action="${pageContext.request.contextPath }/moduleServlet/moca/saveResponseServlet" method="post">
				<input type="hidden" name="queueItemId" value="${object.queueItem.queueItemId}"/>
				<input type="hidden" name="encounterId" value="${object.queueItem.encounterId}"/>
				<p><spring:message code="moca.diagnoses" />: <input type="button" id="clearButton" onclick="return clearList()" value="Clear List"></p>
					<div id="diagnosisList" class="indented" id="diagnosis-list"></div>
					<input type="hidden" name="HiddenDiagnoses" id="HiddenDiagnoses" value=""/>
				</p>
				<div id="horizBar2" class="indented"><hr align=left noshade size=4 width=90% style="color: white;"></div>
				<p>
					<spring:message code="moca.diagnosis_urgency" />: <br>
					<input type="radio" name="Urgency" value="Emergency" /> <spring:message code="moca.emergency" /><br>
					<input type="radio" name="Urgency" value="Urgent" /> <spring:message code="moca.urgent" /><br>
					<input type="radio" name="Urgency" value="Non-Urgent" /> <spring:message code="moca.non_urgent" /><br>
					<br/>
					<spring:message code="moca.treatment" />: <br>
					<textarea wrap="virtual" class="t1" name="Treatment" rows=3></textarea><br><br>	
					<spring:message code="moca.comments" />: <br>
					<textarea wrap="virtual" class="t1" name="Comments" rows=3></textarea><br><br>
					<input type="submit" value="Send" class="btn"/>
					<input type="button" value="Back" class="btn" onClick="backToQueue()"/>
					<input type="button" value="Retake" class="btn" onClick="retake()"/><br><br>
				</p>
			</form>
	 	</div>
	 	
	</div>
	
	<div class="rightBox">
		<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
					id="TestViewer" width="100%" height="100%"
					codebase="http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab">
					<param name="movie" value="${pageContext.request.contextPath}/moduleResources/flashmediaviewer/MediaFileViewer.swf" />
					<param name="quality" value="high" />
					<param name="bgcolor" value="#869ca7" />
					<param name="allowScriptAccess" value="always" />
					<param name="allowFullScreen" value="true" />
					<param name="FlashVars" value="patientFirstName=${object.queueItem.patient.givenName}&patientLastName=${object.queueItem.patient.familyName}&dateUploaded=${object.queueItem.dateUploaded}&encounterID=${object.queueItem.encounterId}&contextPath=${pageContext.request.contextPath}">
					<embed src="${pageContext.request.contextPath}/moduleResources/flashmediaviewer/MediaFileViewer.swf" quality="high" bgcolor="#869ca7"
						width="100%" height="100%" name="TestViewer" align="middle"
						play="true"
						loop="false"
						quality="high"
						allowScriptAccess="always"
						allowFullScreen="true"
						type="application/x-shockwave-flash"
						FlashVars="patientFirstName=${object.queueItem.patient.givenName}&patientLastName=${object.queueItem.patient.familyName}&dateUploaded=${object.queueItem.dateUploaded}&encounterID=${object.queueItem.encounterId}&contextPath=${pageContext.request.contextPath}"
						pluginspage="http://www.adobe.com/go/getflashplayer">
					</embed>
			</object>
	</div>
</div>
</body>
</html>

