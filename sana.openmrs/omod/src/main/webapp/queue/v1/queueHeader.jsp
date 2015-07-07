<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="View Encounter Queue" otherwise="/login.htm" 
    redirect="/module/sana/queue/v1/queue.htm" />
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/sana/jquery-1.4.4.js"></script>
<style>

.row-even {
        background-color: #fff;
}

.row-odd {
        background-color: #ddd;
}

#message {
        font-size: 10pt;
}

.mediaList, .mediaList li {
        display: inline;
}

</style>
