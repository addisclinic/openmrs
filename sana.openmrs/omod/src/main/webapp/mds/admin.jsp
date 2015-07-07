<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/module/sana/localHeader.jsp" %>
<div>   
<h2><spring:message code="sana.mdsAdmin"/></h2>
</div>

<div id="output"></div>
<script language="javascript">
//Functions for Onclick methods
   
   window.onload = function()
   {
       var logs = jquery.get("http://localhost/mds/log")
       jQuery("#output").html(logs);
       
   }
</script>
<%@ include file="/WEB-INF/template/footer.jsp" %>