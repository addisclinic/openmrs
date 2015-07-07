<html>
<head>
<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:htmlInclude file="/scripts/easyAjax.js" />

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />

<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/css/redmond/jquery-ui-1.7.2.custom.css" />

<script type="text/javascript">
	var $j = jQuery.noConflict(); 
</script>

<%--
Parameters
	model.num == \d  limits the number of encounters shown to the value given
	model.hideHeader == 'true' hides the 'All Encounter' header above the table listing
	model.hideFormEntry == 'true' does not show the "Enter Forms" popup no matter what the gp has
	model.formEntryReturnUrl == what URL to return to when a form has been cancelled or successfully filled out
--%>

</head>
<body>
<div id="encounterPortlet">
   
    <openmrs:hasPrivilege privilege="View Encounters">
        <div id="encounters">
            <div class="boxHeader"><spring:message code="Encounter.header"/></div>
            <div class="box">
                <table cellspacing="0" cellpadding="2" class="patientEncounters">
                        <tr>
                            <th colspan="10" class="tableTitle"><spring:message code="All Encounters"/></th>
                        </tr>
                        <tr>
                            <th class="encounterId"><spring:message code="sana.mediaviewer.dashboard_id"/></th>
                            <th class="encounterEdit" align="center"><spring:message code="sana.mediaviewer.dashboard_openmrs_view"/></th>
                            <th class="encounterView" align="center"><spring:message code="sana.mediaviewer.dashboard_flash_view"/></th>
                            <th class="encounterUpload" align="center"> <spring:message code="sana.mediaviewer.dashboard_upload"/> </th>
                            <th class="encounterDatetimeHeader"><spring:message code="Encounter.datetime"/></th>
                            <th class="encounterTypeHeader"><spring:message code="Encounter.type"/></th>
                            <th class="encounterProviderHeader"><spring:message code="Encounter.provider"/></th>
                            <th class="encounterFormHeader"><spring:message code="Encounter.form"/></th>
                            <th class="encounterLocationHeader"><spring:message code="Encounter.location"/></th>
                            <th class="encounterEntererHeader"><spring:message code="Encounter.enterer"/></th>
                        </tr>
                        <openmrs:forEachEncounter encounters="${model.allEncounters}" sortBy="encounterDatetime" descending="true" var="enc">
                            <tr class="<c:choose><c:when test="${count % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
                                <td class="encounterID">${enc.encounterId}</td>
                                <td class="encounterEdit" align="center">
                                        <openmrs:hasPrivilege privilege="Edit Encounters">
                                            <c:set var="editUrl" value="${pageContext.request.contextPath}/admin/encounters/encounter.form?encounterId=${enc.encounterId}"/>
                                            <a href="${editUrl}">
                                                <img src="${pageContext.request.contextPath}/images/file.gif" title="<spring:message code="general.edit"/>" border="0" align="top" />
                                            </a>
                                        </openmrs:hasPrivilege>
                                </td>
                                <td class="encounterView" align="center">
                                        <c:set var="viewEncounterUrl" value="${pageContext.request.contextPath}/module/sana/mediaviewer/mediaViewer.form?encounterId=${enc.encounterId}"/>
                                        <a href="${viewEncounterUrl}">
                                            <img src="${pageContext.request.contextPath}/images/open.gif" title="<spring:message code="general.view"/>" border="0" align="top" />
                                        </a>
                                </td>
                                <td class="encounterUpload" align="center">
                                        <c:set var="uploadEncounterUrl" value="${pageContext.request.contextPath}/module/sana/mediaviewer/uploadEncounter.form?encounterId=${enc.encounterId}"/>
                                        <a href="${uploadEncounterUrl}">
                                            <img src="${pageContext.request.contextPath}/images/add.gif" title="<spring:message code="upload"/>" border="0" align="top" />
                                        </a>
                                </td>
                                <td class="encounterDatetime">
                                    <openmrs:formatDate date="${enc.encounterDatetime}" type="small" />
                                </td>
                                 <td class="encounterType">${enc.encounterType.name}</td>
                                 <td class="encounterProvider">${enc.provider.personName}</td>
                                 <td class="encounterForm">${enc.form.name}</td>
                                 <td class="encounterLocation">${enc.location.name}</td>
                                 <td class="encounterEnterer">${enc.creator.personName}</td>
                            </tr>
                        </openmrs:forEachEncounter>
                </table>
            </div>
        </div>
    </openmrs:hasPrivilege>

</div>
</body>
</html>
