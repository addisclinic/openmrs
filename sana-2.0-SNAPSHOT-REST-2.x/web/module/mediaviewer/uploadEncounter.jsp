<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<style>
	
.outline {
  border: 1px solid #6D9BC5;
  width: 100%;
  float: left;
}

.outline2 {
  border: 1px solid #6D9BC5;
  width: 69%;
  height: 500px;
  float: left;
  margin-left: 1%;
  
}

.foot {
 float:left;
 clear:both;
}

.uploadForm{
  color:#000000;
  font-size:12px;
  font-style:normal;
  font-family:verdana;
  padding:2px;
  padding-left:5px;
}

h1 {
  background-color: #6D9BC5;
  font-size:13px;
  font-style:normal;
  font-family:verdana;
  color:#ffffff;
  margin:0px; 
  padding:5px;
  border:0px;
}


input.btn{
  font-size:12px;
  font-style:normal;
  font-family:verdana;
  margin-top:3px;
  margin-bottom:3px;
}



</style>

<h2><spring:message code='sana.mediaviewer.upload_patient_media_files'/></h2>
<div class="outline">
	<h1><spring:message code='sana.mediaviewer.add_to_encounter'/></h1>
	<div class="uploadForm">
		<form name="input" action="${pageContext.request.contextPath }/moduleServlet/sana/mediaviewer/uploadToEncounterServlet?encounterId=${encounterId}" enctype="multipart/form-data" method="post"><br>
		<spring:message code='sana.mediaviewer.encounter_id'/>: ${encounterId}<br><br>
		<spring:message code='sana.mediaviewer.file'/>: <input type="file" id="medImageFile1" name="medImageFile1"/><br><br>
		<spring:message code='sana.mediaviewer.file'/>: <input type="file" id="medImageFile2" name="medImageFile2"/><br><br>
		<spring:message code='sana.mediaviewer.file'/>: <input type="file" id="medImageFile3" name="medImageFile3"/><br><br>
		<spring:message code='sana.mediaviewer.file'/>: <input type="file" id="medImageFile4" name="medImageFile4"/><br><br>
		<spring:message code='sana.mediaviewer.file'/>: <input type="file" id="medImageFile5" name="medImageFile5"/><br><br>
		<input type="submit" value="Send" class="btn"/><br>
		</form>
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
