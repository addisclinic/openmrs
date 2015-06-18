<ul id="menu">
    <li class="first">
        <a href="${pageContext.request.contextPath}/admin"><spring:message code="admin.title.short"/></a>
    </li>
    

    
    <openmrs:hasPrivilege privilege="Manage Global Properties">
    <li <c:if test='<%= request.getRequestURI().contains("sana/mds/logs") %>'>class="active"</c:if>>
        <a href="${pageContext.request.contextPath}/module/sana/mds/logs.htm">
            <spring:message code="sana.mdsLog"/>
        </a>
    </li>
    </openmrs:hasPrivilege>
    
    <openmrs:hasPrivilege privilege="Manage Global Properties">
        <li <c:if test='<%= request.getRequestURI().contains("sana/lexicon") %>'>class="active"</c:if>>
            <a href="${pageContext.request.contextPath}/module/sana/lexicon/lexicon.form">
                <spring:message code="sana.lexicon"/>
            </a>
        </li>
    </openmrs:hasPrivilege>
        
    <openmrs:hasPrivilege privilege="View Encounter Queue">
        <li <c:if test='<%= request.getRequestURI().contains("sana/mds/sxml") %>'>class="active"</c:if>>
            <a href="${pageContext.request.contextPath}/module/sana/mds/sxml.form">
                <spring:message code="sana.mdsSXML"/>
            </a>
        </li>
    </openmrs:hasPrivilege>
    
    
    <openmrs:hasPrivilege privilege="View Encounter Queue">
        <li <c:if test='<%= request.getRequestURI().contains("sana/queue") %>'>class="active"</c:if>>
            <a href="${pageContext.request.contextPath}/module/sana/queue/v1/queue.form">
                <spring:message code="sana.view"/>
            </a>
        </li>
    </openmrs:hasPrivilege>
    
    <openmrs:extensionPoint pointId="org.openmrs.module.sana.admin.localHeader" type="html">
            <c:forEach items="${extension.links}" var="link">
                <li <c:if test="${fn:endsWith(pageContext.request.requestURI, link.key)}">class="active"</c:if> >
                    <a href="${pageContext.request.contextPath}/${link.key}"><spring:message code="${link.value}"/></a>
                </li>
            </c:forEach>
    </openmrs:extensionPoint>
</ul>