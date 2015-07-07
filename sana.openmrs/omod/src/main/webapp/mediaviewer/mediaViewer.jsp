<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<style>
.viewerContainer{
  width: 100%;
  height: 100%;s
  position: relative;
}

.viewerBox {
  width: 100%;
  height: 100%;
  float: left;
}

</style>

<h4>
<spring:message code="sana.mediaviewer.viewer_date" />: <openmrs:formatDate date="${enc.encounterDatetime}" type="small" /> <br/>
<spring:message code="sana.mediaviewer.viewer_patient" />: ${enc.patient.familyName}, ${enc.patient.givenName}</a> <br/>
<spring:message code="sana.mediaviewer.viewer_id" />: ${enc.patient.patientIdentifier}<br/>
<spring:message code="sana.mediaviewer.viewer_age" />: ${enc.patient.age} ${enc.patient.gender}<br/>
<spring:message code="sana.mediaviewer.viewer_gender" /> ${enc.patient.gender}<br/>
</h4>
    

<div class="viewerContainer" id="container">
	<div class="viewerBox">
		<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
			id="TestViewer" width="100%" height="100%"
			codebase="http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab">
			<param name="movie"
				value="${pageContext.request.contextPath}/moduleResources/sana/MediaFileViewer.swf" />
			<param name="quality" value="high" />
			<param name="bgcolor" value="#869ca7" />
			<param name="allowScriptAccess" value="always" />
			<param name="allowFullScreen" value="true" />
			<param name="FlashVars"
				value="patientFirstName=${enc.patient.givenName}&patientLastName=${enc.patient.familyName}&dateUploaded=${enc.encounterDatetime}&encounterID=${enc.encounterId}&contextPath=${pageContext.request.contextPath}&patientId=${enc.patient.patientId}">
			<embed
				src="${pageContext.request.contextPath}/moduleResources/sana/MediaFileViewer.swf"
				quality="high" bgcolor="#869ca7" width="100%" height="100%"
				name="TestViewer" align="middle" play="true" loop="false"
				quality="high" allowScriptAccess="always" allowFullScreen="true"
				type="application/x-shockwave-flash"
				FlashVars="patientFirstName=${enc.patient.givenName}&patientLastName=${enc.patient.familyName}&dateUploaded=${enc.encounterDatetime}&encounterID=${enc.encounterId}&contextPath=${pageContext.request.contextPath}&patientId=${enc.patient.patientId}"
				pluginspage="http://www.adobe.com/go/getflashplayer">
			</embed>
		</object>
	</div>
</div>
<%@ include file="/WEB-INF/template/footer.jsp"%>