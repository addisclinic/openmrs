<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="View Encounter Queue" otherwise="/login.htm" 
    redirect="/module/sana/queue/v1/queue.htm" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/module/sana/localHeader.jsp" %>

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/sana/jquery-1.4.4.js" />
<script language="javascript">

function send(){
    var frm = document.getElementById("review");
    frm.submit();
}

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
  jQuery.get("${pageContext.request.contextPath}/moduleServlet/sana/conceptSearchServlet",
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
  jQuery.post("${pageContext.request.contextPath}/moduleServlet/sana/retakeServlet",
             {'msg':msg, 'queueItemId':queueID},
             function(data) {
                if(data == "OK") {
                 document.location.href= "queueDeferred.form"
                } else {
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
  min-height: 584px;
  width: 100%;
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
<div class="bigContainer" id="container">
	<div class="leftBox" id="leftContainer">
		<h1><a href="${pageContext.request.contextPath}/patientDashboard.form?patientId=${encounter.queueItem.patient.patientId }">${encounter.queueItem.patient.familyName}, ${encounter.queueItem.patient.givenName}</a> (<spring:message code="sana.id" /> ${encounter.queueItem.patient.patientIdentifier}) ${encounter.queueItem.patient.age} ${encounter.queueItem.patient.gender}</h1>
		<h3><spring:message code="sana.status" />: ${encounter.status}</h3>
		<div id="pastDiagnoses" class="patientInfo"><p>${encounter.existingDiagnoses}</p></div>
		<h3><spring:message code="sana.patient_visit_responses" /></h3>
		<div id="patientInfoBox" class="blueBackground"><p>${encounter.patientResponses}</p></div>
		<h3><spring:message code="sana.recommendations" /></h3>
		<div id="diagnosisBox" class="blueBackground">
			<p><spring:message code="sana.select_vocab" />  <select id="conceptSourceName" name="conceptSourceName"><c:forEach var="source" items="${encounter.conceptSources}"><option>${source}</option></c:forEach></select></p>
			<form name="searchForm">
				<input type="hidden" id="queueItemId" name="queueItemId" value="${encounter.queueItem.queueItemId}"/>
				<input type="hidden" id="encounterId" name="encounterId" value="${encounter.queueItem.encounterId}"/> 
				<p><input type="text" name="searchInput"/>
				<input type="submit" onclick="conceptDictSearch(); return false;" id="searchButton" value="Search for Diagnosis to Add"><br></p>
				<div id="searchResults" class="indented"></div>
				<div id="horizBar2" class="indented"><hr align=left noshade size=4 width=90% style="color: white;"></div>
			</form>
			<form id="review" name="input" action="${pageContext.request.contextPath }/moduleServlet/sana/saveResponseServlet" method="post">
				<input type="hidden" name="queueItemId" value="${encounter.queueItem.queueItemId}"/>
				<input type="hidden" name="encounterId" value="${encounter.queueItem.encounterId}"/>
				<p><spring:message code="sana.diagnoses" />: <input type="button" id="clearButton" onclick="return clearList()" value="Clear List"></p>
					<div id="diagnosisList" class="indented" id="diagnosis-list"></div>
					<input type="hidden" name="HiddenDiagnoses" id="HiddenDiagnoses" value=""/>
				</p>
				<div id="horizBar2" class="indented"><hr align=left noshade size=4 width=90% style="color: white;"></div>
				<p>
					<spring:message code="sana.diagnosis_urgency" />: <br>
					<input type="radio" name="Urgency" value="Emergency" required="True"/> <spring:message code="sana.emergency" /><br>
					<input type="radio" name="Urgency" value="Urgent" required="True"/> <spring:message code="sana.urgent" /><br>
					<input type="radio" name="Urgency" value="Non-Urgent" required="True"/> <spring:message code="sana.non_urgent" /><br>
					<br/>
          <spring:message code="sana.treatment" />: <br>
          <textarea wrap="virtual" class="t1" name="Treatment" rows=3 required="True"></textarea><br><br>
					<spring:message code="sana.assessment" />: <br>
					<textarea wrap="virtual" class="t1" name="Assessment" rows=3 required="True"></textarea><br><br>	
					<spring:message code="sana.recommendations" />: <br>
					<textarea wrap="virtual" class="t1" name="Recommendations" rows=3></textarea><br><br>
					<input type="submit" value="Send" class="btn"/>
					<input type="button" value="Back" class="btn" onClick="backToQueue()"/>
					<input type="button" value="Retake" class="btn" onClick="retake()"/><br><br>
				</p>
			</form>
	 	</div>
	</div>
	
	<div class="rightBox" id="rightContainer">
		<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
					id="TestViewer" 
					width="100%" height="100%"
					codebase="http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab">
					<param name="movie" value="${pageContext.request.contextPath}/moduleResources/sana/MediaFileViewer2.swf" />
					<param name="quality" value="high" />
					<param name="bgcolor" value="#869ca7" />
					<param name="allowScriptAccess" value="always" />
					<param name="allowFullScreen" value="true" />
					<param name="FlashVars" value="patientFirstName=${encounter.queueItem.patient.givenName}&patientLastName=${encounter.queueItem.patient.familyName}&dateUploaded=${encounter.queueItem.dateUploaded}&encounterID=${encounter.queueItem.encounterId}&contextPath=${pageContext.request.contextPath}">
					<embed src="${pageContext.request.contextPath}/moduleResources/sana/MediaFileViewer2.swf" quality="high" bgcolor="#869ca7"
						width="100%" height="100%" 
						name="TestViewer" 
						align="middle"
						play="true"
						loop="false"
						quality="high"
						allowScriptAccess="always"
						allowFullScreen="true"
						type="application/x-shockwave-flash"
						FlashVars="patientFirstName=${encounter.queueItem.patient.givenName}&patientLastName=${encounter.queueItem.patient.familyName}&dateUploaded=${encounter.queueItem.dateUploaded}&encounterID=${encounter.queueItem.encounterId}&contextPath=${pageContext.request.contextPath}"
						pluginspage="http://www.adobe.com/go/getflashplayer">
					</embed>
			</object>
	</div>
</div>
<div style="clear: both;"></div>
<%@ include file="/WEB-INF/template/footer.jsp"%>



