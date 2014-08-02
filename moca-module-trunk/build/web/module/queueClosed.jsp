<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ page import="java.util.*" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<openmrs:require privilege="View Moca Queue" otherwise="/login.htm" redirect="/module/moca/queue.htm" />
<script src="${pageContext.request.contextPath}/openmrs.js" type="text/javascript" ></script>
<link href="${pageContext.request.contextPath}/openmrs.css" type="text/css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/style.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/moca/jquery-1.2.6.js"></script>

<%@ include file="/WEB-INF/template/header.jsp" %><br/>

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
<%
List ls = new ArrayList();
%>
<script language="javascript">
//Functions for Onclick methods
   
   window.onload = function()
   {
        if(document.getElementById("hidprevid").value == "")
        {     
             document.getElementById("hidprevid").value = 0;
             document.getElementById("hidnextid").value = document.getElementById("queuesizeid").value;
        }
         sortretain();
   }
   
   function clickqueuelimit(i)
   {
     document.getElementById("gotopageid").value = "";
    
     document.getElementById("queuelimitid").value = i;
     document.getElementById("queueFormid").submit();
     return true;
     
   }
    function sortretain()
    {
        var frm1=document.forms['queueForm'].elements;
        var sortvar1=frm1['sortname']; 
            
        if(document.getElementById("hidsortid").value == "" || document.getElementById("hidsortid").value == '1')
        {
         sortvar1[1].checked = true;
         }
       else
       {
         sortvar1[0].checked = true;  
         }
    }
    function sortOrder()
    {
     
      var frm=document.forms['queueForm'].elements;
      var sortvar=frm['sortname'];
      
      if(sortvar[0].checked){
      queueForm.hidsortid.value=0;
      }
      else
      {
      queueForm.hidsortid.value=1;
      }
     document.forms["queueFormid"].submit();
    }
    function gotopage(queuelimtset,queuelistcount)
    {
        var queuelimit = queuelistcount;
        if(document.getElementById("queuelimitid").value != "")
        {
             
             queuelimit =  document.getElementById("queuelimitid").value*1;
        }
        if(document.getElementById("totcountid").value*1 < document.getElementById("gotopageid").value*1)
        {
          alert("You have"+" "+document.getElementById("totcountid").value*1+" "+"pages");
          document.getElementById("gotopageid").value = "";
          return false;
        }
        if(document.getElementById("gotopageid").value*1 <=0 || isNaN(document.getElementById("gotopageid").value))
        {
         alert("Please Enter a Positive Numeric Value");
         document.getElementById("gotopageid").value = "";
         return false;
        }
        else
        {
	    	var pageno = document.getElementById("gotopageid").value*1;
	    	var count = pageno*queuelimit;
	    	var substract = pageno-1;
	    	if(pageno >=2)
	    	{
		    	document.getElementById("hidprevid").value = count-queuelimit-substract;
		    	document.getElementById("hidnextid").value = count-queuelimit-substract;
	    	}
	    	else
	    	{
		    	document.getElementById("hidprevid").value =0;
		    	document.getElementById("hidnextid").value =0;
	    	}
	    	return true;
    	}
    }
   
    function resetpagefiels()
    {
      document.getElementById("hidprevid").value = 0;
      document.getElementById("hidnextid").value = 10;
      document.getElementById("gotopageid").value = "";
    }
    
    function pageNationNext(queuelistcount)
    {
        sortretain();
        document.getElementById("gotopageid").value = "";   
        var prev = 1;
        var next = queuelistcount;
      
        if(document.getElementById("hidprevid").value == "")
        {     
              document.getElementById("hidprevid").value = 0;
        }
        else
        {
	         var next=document.getElementById("hidnextid").value;
	         if(document.getElementById("queuelimitid").value == "")
	         {
	            var addi = queuelistcount*1;
	         }
	         else
	         {
	            var addi = document.getElementById("hidnextid").value*1;
	         }
	         document.getElementById("hidprevid").value = document.getElementById("hidprevid").value*1+addi*1-1;
       }
       
    }
    function pageNationPrev(queuelistcount)
    {   
          sortretain();
         document.getElementById("gotopageid").value = "";
         var prev = 1;
         var next = queuelistcount;
        if(document.getElementById("hidprevid").value == '1')
             document.getElementById("hidprevid").value = 0;
         
        if(document.getElementById("hidprevid").value == "" || document.getElementById("hidprevid").value*1 <= 0)
        {     
              document.getElementById("hidprevid").value = 0;
        }
        else
        {
            var next=document.getElementById("hidprevid").value;
            if(document.getElementById("queuelimitid").value == "")
            {
           		 var addi = queuelistcount*1;
            }
            else
            {
            	var addi = document.getElementById("hidnextid").value;
          
            }
            document.getElementById("hidprevid").value = next*1-addi*1+1;
          }
    }
	function clickprocedure(smsrow)//for storing procedure string text.
	{
		document.getElementById("proid").value = smsrow;
	}
	function clickdatefield(d,m)// for storing days and months which is hidden.
	{
				document.getElementById("daysid").value = d;
				document.getElementById("monthid").value = m;
				document.getElementById("daysarcid").value = "";
	}
	function clickdatefieldarc(d,m)
	{
	     document.getElementById("daysid").value = "";
	     document.getElementById("daysarcid").value = d;
	    
	}
	var temp = new Array();
	function clickar(chk)//storing the selected(checked) IDs for archieving.
	{
		if(chk.checked)
		{
			
			document.getElementById("chklistid").value += chk.value + ";";
		}
	}
	function checkall(chk,checknone)//storing all the IDs for  archiving in the hidden text.
	{
		if(checknone.checked)
			checknone.checked = false;
		document.getElementById("chklistid").value = "";
		for(var i = 0  ; i < chk.length ; i ++)
		{
			chk[i].checked = true;
			document.getElementById("chklistid").value += chk[i].value + ";";
		}
	} 
	function checknone(chk, checkall)//For deselecting the Checked ids.
	{
		if(checkall.checked)
			checkall.checked = false;
		for(var i = 0 ; i < chk.length ; i ++)
			chk[i].checked = false;
		document.getElementById("chklistid").value = "";
	}
	function checkshall(checkarchive)
	{
		checkarchive.checked = false;
	}
	function checkarchived(checkshowall)
	{
		checkshowall.checked = false;
	}
</script>

<h1><spring:message code="moca.queue_title" /></h1>
<ul id="menu">
	<li class="first">
		<a href="${pageContext.request.contextPath}/module/moca/queue.form"><spring:message code='moca.queue_pending_cases'/></a>
	</li>
	<li>
		<a href="${pageContext.request.contextPath}/module/moca/queueDeferred.form"><spring:message code='moca.queue_deferred_cases'/></a>
	</li>
	<li >
		<a href="${pageContext.request.contextPath}/module/moca/queueClosed.form"><spring:message code='moca.queue_closed_cases'/></a>
	</li>
</ul>
<!-- Retriving List of queue items,procedure rows,dateItems ffrom the controller through map -->
<c:set var="queueItems" value='${map["queueItems"]}' />
<c:set var="procedurelist" value='${map["procedurerows"]}' />
<c:set var="dateItems" value='${map["dateItems"]}' />
<c:set var="queuelistcount" value='${map["queuelistcount"]}' />
<c:set var="queuesize" value='${map["queuesize"]}' />
<c:set var="count" value='${map["totocount"]}' />
<c:set var="pageno" value='${map["pageno"]}' />
<!--  Added new Procedure and Search bottons -->
<form action="${pageContext.request.contextPath}/module/moca/queueClosed.form" method="post" id="queueFormid" name="queueForm">
<input type=hidden name="chklist" id="chklistid" />
<b class="boxHeader">

<!-- For Retaining Selected items upon Requests. -->
&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
<select name="comboPro" id="combopro" > 
<option selected onClick="clickprocedure('SHOW ALL');">SHOW ALL</option>
<c:forEach var="procedurelist1" items="${procedurelist}">

<c:set var="combo" value="<%= request.getParameter(\"proname\")%>" />
<c:if test="${combo == procedurelist1}" >

<option onclick="clickprocedure('${procedurelist1}');"  selected>${procedurelist1}</option>
</c:if>
<c:if test="${combo != procedurelist1}">
   <option onclick="clickprocedure('${procedurelist1}');" >${procedurelist1}</option>
</c:if>
</c:forEach>
</select>

&nbsp Procedure &nbsp
<c:set var="pronametext" value="<%= request.getParameter(\"proname\")%>"  />

<input type=text  id="proid" name="proname" size="30" title="Enter Procedure text for Search"  value="${pronametext}" />
&nbsp &nbsp&nbsp&nbsp&nbsp

<!-- For Retaining the Selected DateItem. -->
<select name="comboDate"> 
<option selected onClick="clickdatefield('0','0');">SHOW ALL</option>
<c:forEach var="daterows" items="${dateItems}">
<c:set var="combodate" value="<%= request.getParameter(\"daysname\")%>" />
<c:choose>
<c:when test="${combodate == daterows.days}" >
<option onclick="clickdatefield('${daterows.days}','${daterows.months}');"  selected>${daterows.textName}</option>
</c:when>
<c:when test="${daterows.days != 7}" >
<option onclick="clickdatefield('${daterows.days}','${daterows.months}');" >${daterows.textName}</option>
</c:when>
	<c:otherwise> 
	<option onclick="clickdatefield('${daterows.days}','${daterows.months}');" >${daterows.textName}</option>
	</c:otherwise>
</c:choose>
</c:forEach>
</select>

 &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
 Select All/Active/InActive ${data}${data}
<select name="optionarchive">
<c:set var="optioncombo" value="<%= request.getParameter(\"optionarchive\")%>" />
<c:choose>
	<c:when test="${optioncombo=='Show ALL'}">
	<option selected>Show ALL</option>
	 <option>Show InActive</option> 
	 <option>Show Active</option>
    </c:when>
	<c:when test="${optioncombo=='Show InActive'}">
		<option>Show ALL</option> 
		<option selected>Show InActive</option> 
		<option>Show Active</option>
	</c:when>
	<c:when test="${optioncombo=='Show Active'}">
		<option>Show ALL</option>
		 <option>Show InActive</option> 
		 <option selected>Show Active</option>
	</c:when>
	<c:otherwise>
		<option>Show ALL</option>
		 <option selected>Show Active</option> 
		 <option >Show InActive</option>
	</c:otherwise>
</c:choose>
</select>

 <input type=submit id="subpro" name="subproname" value="Filter" title="Search through Moca items" onclick="resetpagefiels()">
<!-- Placing days & months Text Field hidden for retrival in controller -->
<c:set var="hiddaysname" value="<%= request.getParameter(\"daysname\")%>" />
<input type=hidden id="daysid" name="daysname" size="2" maxlength="2" title="Enter days only to Search" value="${hiddaysname}">
<c:set var="hiddaysarc" value="<%=request.getParameter(\"daysarcname\")%>" />
<input type=hidden id="daysarcid" name="daysarcname" value="${hiddaysarc}">
<input type=hidden id="monthid" name="monthname" maxlength="2" size="2" title="Enter months only to Search">
<c:set var="hidprevname" value="<%= request.getParameter(\"hidprevname\")%>" />
<input type="hidden" name="hidprevname" id="hidprevid" value="${hidprevname}">
<c:set var="hidnextname" value="<%= request.getParameter(\"queuelimitname\")%>" />
<c:if test="${hidnextname != ''}">
<input type="hidden" name="hidnextname" id="hidnextid" value="${hidnextname}" />
</c:if>
<c:if test="${hidnextname == ''}">
<input type="hidden" name="hidnextname" id="hidnextid" value="${queuelistcount}" />
</c:if>
<c:set var="hidsortname" value="<%= request.getParameter(\"hidsortname\")%>" />
<input type="hidden" name="hidsortname" id="hidsortid" value="${hidsortname}">
<input type="hidden" name="queuesize" id="queuesizeid" value="${queuesize}" />
<input type="hidden" name="pagenoname" id="pagenoid" value="${pageno}" />
&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
</b>
<b class="boxHeader"><spring:message code='moca.queue_patient_cases'/></b>
	<div class="box">
		<table id="queueTable" cellpadding="10" cellspacing="0" width="100%">
			<thead>
				<tr>
					<th>Archive</th>
					<th><spring:message code='moca.queue_status'/></th>
					<th><spring:message code='moca.queue_patient_id'/></th>
					<th><spring:message code='moca.queue_patient_name'/></th>
					<th><spring:message code='moca.queue_patient_age'/></th>
					<th><spring:message code='moca.queue_patient_sex'/></th>
					<th><spring:message code='moca.queue_procedure'/></th>
					<th><spring:message code='moca.queue_date_taken' /></th>
					<th><spring:message code='moca.queue_phone' /></th>
					<th><spring:message code='moca.queue_media' /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="item" items="${queueItems}" varStatus="status">
					<jsp:useBean id="status" type="javax.servlet.jsp.jstl.core.LoopTagStatus" />
					<c:choose>
		          		<c:when test="<%=status.getCount()%2==0%>">
		            		<c:set var="rowclass" value="row-even" />
		          		</c:when>
		          		<c:otherwise>
		            		<c:set var="rowclass" value="row-odd" />
		          		</c:otherwise>
		        	</c:choose>
		        	
					<tr class="<c:out value="${rowclass}"/>" id="row${item.queueItemId }">
					<!-- Sending only checked Item.id to clickarr() -->
					<td ><input type="checkbox" name="chk" value=${item.id} onclick="clickar(this)" > </td>
						<td valign="top"><spring:message code="${item.status.code }"/></td>
						<td valign="top">${item.patient.patientIdentifier}</td>
						<td valign="top">
							<a href="${pageContext.request.contextPath}/patientDashboard.form?patientId=${item.patient.patientId }">
								${item.patient.familyName}, ${item.patient.givenName}
							</a>
						</td>
						<td valign="top">${item.patient.age}${data}</td>
						<td valign="top">${item.patient.gender}</td>
						<td valign="top">
							<a href="${pageContext.request.contextPath}/module/moca/encounterViewer.form?encounterId=${item.encounter.encounterId}">
								${item.procedureTitle}
							</a>
						</td>
						<td valign="top"><fmt:formatDate value="${item.dateCreated}" pattern="MM/dd/yyyy hh:mm"/></td>
						<td valign="top">${item.phoneIdentifier}</td>
						<td valign="top" style="white-space: nowrap;">
							<ul class="mediaList">
								<c:forEach var="obs" items="${item.encounter.obs }">
								<c:if test="${obs.complex }">
									<li>
										<a href="${pageContext.request.contextPath}/moduleServlet/flashmediaviewer/complexObsServlet?obsId=${obs.obsId}&view=SHOW&viewType=DOWNLOAD">										
											<c:choose>
											<c:when test="${obs.valueText=='SOUND'}"><img border="0" src="${pageContext.request.contextPath }/moduleResources/moca/audioThumbnail.png"/>
											</c:when>
											<c:when test="${obs.valueText=='VIDEO'}"><img border="0" src="${pageContext.request.contextPath }/moduleResources/moca/videoThumbnail.png"/>
											</c:when>
											<c:when test="${obs.valueText=='BINARYFILE'}"><img border="0" src="${pageContext.request.contextPath }/moduleResources/moca/binaryThumbnail.png"/>
											</c:when>
											<c:otherwise>
												<img src="${pageContext.request.contextPath }/moduleServlet/flashmediaviewer/complexObsServlet?obsId=${obs.obsId }&view=VIEW_THUMBNAIL"/>
											</c:otherwise>
											</c:choose>
										</a>
									</li>								
								</c:if>
								</c:forEach>
							</ul>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
<b class="boxHeader">
&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
 <!--<input type=submit value="Sort By" />-->
 Sort By
 <input type="radio" name="sortname" id="lifo" value="LIFO" onclick="sortOrder(sortname);">Newest First &nbsp&nbsp&nbsp&nbsp 
 <input type="radio" name="sortname" id="fifo" value="FIFO" onclick="sortOrder(sortname);">Oldest First&nbsp&nbsp&nbsp&nbsp 

    <c:if test="${pageno == 1}">
    <input type="submit" align="center" name="prevname" id="previd" value="Prev" onclick="pageNationPrev('${queuelistcount}')" disabled="disabled"/>  
    &nbsp&nbsp&nbsp&nbsp&nbsp${pageno} - ${count}&nbsp&nbsp&nbsp
    </c:if>
    <c:if test="${pageno > 1}">
    <input type="submit" align="center" name="prevname" id="previd" value="Prev" onclick="pageNationPrev('${queuelistcount}')" />  &nbsp&nbsp&nbsp&nbsp&nbsp${pageno} - ${count}&nbsp&nbsp&nbsp
    </c:if>
    <c:if test="${pageno == count}">
    <input type="submit" name="nextname" id="nextid" value="Next" onclick="pageNationNext('${queuelistcount}')" disabled="disabled"/>
    </c:if>
    <c:if test="${pageno < count}">
    <input type="submit" name="nextname" id="nextid" value="Next" onclick="pageNationNext('${queuelistcount}')">
    </c:if>
    
    <c:set var="gotopage" value="<%= request.getParameter(\"gotopagename\")%>" />
    &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
    Go To Page
    <input type=text name="gotopagename" id="gotopageid" value="${gotopage}" size=3/>
    <input  type=submit value="Go" onclick="return gotopage('${queuelimtset}','${queuelistcount}')"/>
   <!--<input type=submit id="queuelimitsubmitid" name="queuelimitsubmitname" value="Items Per Page" title="Click here to set queue size">-->
   &nbsp&nbsp&nbsp&nbsp
    Items Per Page
	<c:set var="queuelimtset" value="<%= request.getParameter(\"queuelimitname\")%>" />
	<input type=hidden name="queuelimitname" id="queuelimitid" value="${queuelimtset}"/>
	<select name="queueLimit">
	    <c:forEach var="i" begin="1" end="${queuelistcount}" step="1" varStatus="status">
	      <option onclick="return clickqueuelimit('${i}');">${i}</option>
	    </c:forEach> 
    </select>
    <input type=hidden name="totcountname" id="totcountid" value="${count}"/>
    <br><br>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
	<input type="checkbox" name="checkselectall" id="chkselectid" title="Check to Select All" onclick="checkall(chk,checkdeselectall)" />Select All &nbsp&nbsp&nbsp&nbsp

    <input type="checkbox" name="checkdeselectall" id="chkdeselectid" title="Check to Deselect All" onclick="checknone(chk,checkselectall)" />Select None
&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<input type=submit id="subarchive" name="subarchivename" value="Archive Selected" title="Click to Archive checked records">

&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp >>
<select name="comboArchive"> 
<c:forEach var="daterows" items="${dateItems}">
<c:set var="combodatee" value="<%= request.getParameter(\"daysarcname\")%>" />

<c:if test="${combodatee == daterows.days}" >
	<option onclick="clickdatefieldarc('${daterows.days}','${daterows.months}');"  selected>${daterows.textName}</option>
</c:if>

<c:if test="${combodatee != daterows.days}"> 
	<option onclick="clickdatefieldarc('${daterows.days}','${daterows.months}');" >${daterows.textName}</option>
</c:if>

</c:forEach>
</select>
&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
<input type=submit id="subdatearchive" name="subdatearchivename" value="Filter Date" onclick="resetpagefiels()" title="Click to see record beyond selected Date">
&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
</b>	
</form>
<br>
<%@ include file="/WEB-INF/template/footer.jsp" %>
