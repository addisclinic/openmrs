<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:require privilege="View Encounter Queue" otherwise="/login.htm" 
    redirect="/module/sana/queue/v1/queue.htm" />
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="/WEB-INF/view/module/sana/localHeader.jsp" %>

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
<script>

   //Functions for Onclick methods
   var PROCEDURE = "combopro";
   
    window.onload = function(){
        if(document.getElementById("hidprevid").value == ""){     
              //alert("In if ");
             document.getElementById("hidprevid").value = 0;
             document.getElementById("hidnextid").value = 
                 document.getElementById("queuesizeid").value;
        }
        sortretain();
    }

    //
    function clickqueuelimit(i){
        document.getElementById("gotopageid").value = "";
        document.getElementById("queuelimitid").value = i;
   }

    // Gets the selected value from the Procedure selection combo
    function onChangeQueueItemLimit(){
        var selIndex = document.getElementById("queuelimit").selectedIndex;
        var selValue = document.getElementById("queuelimit").options[selIndex].value;
        document.getElementById("queuelimitid").value = selValue;
        document.forms["queueFormid"].submit();
    }
   
   function sortretain(){
        //alert("SortRetain:");
        var frm1=document.forms['queueForm'].elements;
        var sortvar1=frm1['sortname']; 
        if(document.getElementById("hidsortid").value == "" 
            || document.getElementById("hidsortid").value == '1'){
            //alert("SortRetain: if");
            sortvar1[1].checked = true;
        } else {
            sortvar1[0].checked = true;  
            // alert("SortRetain:else ");
        }
    }

   // Toggles the sort order between LIFO and FIFO
   function sortOrder() {
      var frm=document.forms['queueForm'].elements;
      var sortvar=frm['sortname'];
      if(sortvar[0].checked){
          //alert("LIFO");
        queueForm.hidsortid.value=0;
      } else {
         //alert("FIFO");
        queueForm.hidsortid.value=1;
      }
        document.forms["queueFormid"].submit();
    }

    // Returns true if the goto page within the range of available pages
    function gotopage(queuelimtset,queuelistcount)
    {
        var queuelimit = queuelistcount;
        if(document.getElementById("queuelimitid").value != ""){
             queuelimit =  document.getElementById("queuelimitid").value*1;
        }
        //alert(queuelimit);
        if(document.getElementById("totcountid").value*1 < 
                document.getElementById("gotopageid").value*1){
          alert("You have"+" "+document.getElementById("totcountid").value*1
                  +" "+"pages");
          document.getElementById("gotopageid").value = "";
          return false;
        }
        if(document.getElementById("gotopageid").value*1 <=0 
                || isNaN(document.getElementById("gotopageid").value)) {
            alert("Please Enter a Positive Numeric Value");
            document.getElementById("gotopageid").value = "";
            return false;
        } else {
            var pageno = document.getElementById("gotopageid").value*1;
            var count = pageno*queuelimit;
            var substract = pageno-1;
            if(pageno >=2) {
                document.getElementById("hidprevid").value = 
                    count-queuelimit-substract;
                document.getElementById("hidnextid").value = 
                    count-queuelimit-substract;
            } else {
                document.getElementById("hidprevid").value =0;
                document.getElementById("hidnextid").value =0;
            }
            //alert(document.getElementById("hidprevid").value);
            return true;
        }
    }

    // Resets the prev, next and goto hidden pagenation fields
    function resetpagefiels(){
      document.getElementById("hidprevid").value = 0;
      document.getElementById("hidnextid").value = 10;
      document.getElementById("gotopageid").value = "";
    }

    // Handles advancing to previous page of items in the queue
    function pageNationNext(queuelistcount) {
        sortretain();
        //alert(queuelistcount);
        document.getElementById("gotopageid").value = "";   
        var prev = 1;
        var next = queuelistcount;
      
        if(document.getElementById("hidprevid").value == "") {     
            document.getElementById("hidprevid").value = 0;
              //document.getElementById("hidnextid").value = queuelistcount;
        } else {
             //document.getElementById("hidprevid").value = 
             // document.getElementById("hidnextid").value;
             var next=document.getElementById("hidnextid").value;
             if(document.getElementById("queuelimitid").value == "") {
                var addi = queuelistcount*1;  
             } else {
                var addi = document.getElementById("hidnextid").value*1;
             }
             document.getElementById("hidprevid").value = 
             document.getElementById("hidprevid").value*1+addi*1-1;
             //document.getElementById("hidnextid").value = next*1+addi*1-1;
       }
    }
    
    // Handles advancing to previous page of items in the queue
    // Should prevent advancing past first page
    function pageNationPrev(queuelistcount) {   
         sortretain();
         //alert(queuelistcount);
         document.getElementById("gotopageid").value = "";
         var prev = 1;
         var next = queuelistcount;
        if(document.getElementById("hidprevid").value == '1')
             document.getElementById("hidprevid").value = 0;
         
        if(document.getElementById("hidprevid").value == "" 
                || document.getElementById("hidprevid").value*1 <= 0){     
            document.getElementById("hidprevid").value = 0;
            //document.getElementById("hidnextid").value = queuelistcount;
        } else {
            //document.getElementById("hidnextid").value = 
            //    document.getElementById("hidprevid").value;
            var next=document.getElementById("hidprevid").value;
            if(document.getElementById("queuelimitid").value == ""){
                var addi = queuelistcount*1;
            } else {
                var addi = document.getElementById("hidnextid").value; 
            }
            document.getElementById("hidprevid").value = next*1-addi*1+1;                                  
        }
    }

    //for storing procedure string text.
    function clickprocedure(smsrow) {
        //var selIdx = smsrow.selectedIndex;
        //var selOption = smsrow.options[selIdx];
        //alert("The selected option is " + smsrow);
        document.getElementById("proid").value = smsrow;
    }

    // Gets the selected value from the Procedure selection combo
    function onChangeSelectedProcedure(){
        var selIndex = document.getElementById("combopro").selectedIndex;
        var selValue = document.getElementById("combopro").options[selIndex].value;
        document.getElementById("proid").value = selValue;
        document.forms["queueFormid"].submit();
    }
    
    // for storing days and months in hidden fields.
    function clickdatefield(d,m){
        //alert("The date : " + d + " "+ m);
        document.getElementById("daysid").value = d;
        document.getElementById("monthid").value = m;
        document.getElementById("daysarcid").value = "";
    }

    // Sets the hidden day range fields
    function clickdatefieldarc(d,m){
         document.getElementById("daysid").value = "";
         document.getElementById("daysarcid").value = d;
    }
    
    //storing the selected(checked) IDs for archieving.
    var temp = new Array();
    function clickar(chk){
        //alert(chk.value);
        if(chk.checked){
            document.getElementById("chklistid").value += chk.value + ";";
            //alert(document.getElementById("chklistid").value);
        }
    }

    // Toggles selected state of all items currently visible in the queue to
    // true. Stores all the ids in the hidden chklistid field.
    function checkall(chk,checknone)
    {
        if(checknone.checked)
            checknone.checked = false;
        document.getElementById("chklistid").value = "";
        //alert("Checkbox lenght : "+chk.length);           
        for(var i = 0  ; i < chk.length ; i ++)
        {
            chk[i].checked = true;
            document.getElementById("chklistid").value += chk[i].value + ";";
        }
        //alert(document.getElementById("chklistid").value);
    } 
    
    // Toggles selected state of all items currently visible in the queue to
    // false. Removes all the ids in the hidden chklistid field. 
    // chklistid is a semi-colon separated list
    function checknone(chk, checkall){
        if(checkall.checked)
            checkall.checked = false;
        for(var i = 0 ; i < chk.length ; i ++)
            chk[i].checked = false;
        document.getElementById("chklistid").value = "";
    }

    // Deprecated?
    function checkshall(checkarchive){
        checkarchive.checked = false;
    }

    // Deprecated?
    function checkarchived(checkshowall){
        checkshowall.checked = false;
    }
</script>

<h1><spring:message code="sana.queue_title" /></h1>
<ul id="menu">
    <li class="first">
        <a href="${pageContext.request.contextPath}/module/sana/queue/v1/queue.form">
          <spring:message code='sana.queue_pending_cases'/>
        </a>
    </li>
    <li>
        <a href="${pageContext.request.contextPath}/module/sana/queue/v1/queueDeferred.form">
          <spring:message code='sana.queue_deferred_cases'/>
        </a>
    </li>
    <li >
        <a href="${pageContext.request.contextPath}/module/sana/queue/v1/queueClosed.form">
          <spring:message code='sana.queue_closed_cases'/>
        </a>
    </li>
</ul>

<!-- Retriving List of queue items,procedure rows,dateItems from the controller 
     through map  -->
<c:set var="queueItems" value='${map["queueItems"]}' />
<c:set var="procedures" value='${map["procedures"]}' />
<c:set var="dateItems" value='${map["dates"]}' />
<c:set var="limit" value='${map["limit"]}' />
<c:set var="queuesize" value='${map["queuesize"]}' />
<c:set var="maxsize" value='${map["maxsize"]}' />
<c:set var="count" value='${map["count"]}' />
<c:set var="start" value='${map["start"]}' />
<c:set var="queuestatus" value='${map["queuestatus"]}' />
<c:choose>
    <c:when test="${queuestatus=='CLOSED'}">
        <c:set var="formaction" value="${pageContext.request.contextPath}/module/sana/queue/v1/queueClosed.form"  />
    </c:when>
    <c:when test="${queuestatus=='DEFERRED'}">
        <c:set var="formaction" value="${pageContext.request.contextPath}/module/sana/queue/v1/queueDeferred.form"  />
    </c:when>
    <c:otherwise>
        <c:set var="formaction" value="${pageContext.request.contextPath}/module/sana/queue/v1/queue.form"  />
    </c:otherwise>
</c:choose>

<!-- Table Header -->  
<!--
<form action="${pageContext.request.contextPath}/module/sana/queue/v1/queue.form" 
        method="POST" id="queueFormid" name="queueForm">
-->
<form action="${formaction}" method="POST" id="queueFormid" name="queueForm">
    <input type=hidden name="chklist" id="chklistid" />
    <!-- TODO ADD THE SORT BY HERE and items per page -->
    <b class="boxHeader">
    <table width="100%" class="boxHeader">
        <td align="left">
            <b>Select All/Active/InActive ${data}${data}</b>
         <!-- For Retaining the Selected Archive Options. -->
            <select name="archive">
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
                    <option>Show InActive</option>
                </c:otherwise>
            </c:choose>
            </select>
              <input type=submit id="subpro" name="subproname" value="Filter" 
                title="Search through Queue items" onclick="resetpagefiels()">

              <!-- Placing days & months Text Field hidden for retrival in 
                   controller 
              -->
              <!--
              <c:set var="hiddaysname" 
                  value="<%= request.getParameter(\"daysname\")%>" />
              <input type=hidden id="daysid" name="daysname" size="2" 
                  maxlength="2" title="Enter days only to Search" 
                  value="${hiddaysname}">
              <c:set var="hiddaysarc" value="<%=request.getParameter(\"daysarcname\")%>" />
              <input type=hidden id="daysarcid" name="daysarcname" 
                  value="${hiddaysarc}">
              <input type=hidden id="monthid" name="monthname" maxlength="2" 
                  size="2" title="Enter months only to Search">
              -->
              <c:set var="hidprevname" value="<%= request.getParameter(\"hidprevname\")%>" />
              <input type="hidden" name="hidprevname" id="hidprevid" value="${hidprevname}">
              <c:set var="hidnextname" value="<%= request.getParameter(\"limit\")%>" />
              <c:if test="${hidnextname != ''}">
                  <input type="hidden" name="hidnextname" id="hidnextid" 
                      value="${hidnextname}" />
              </c:if>
              <c:if test="${hidnextname == ''}">
                  <input type="hidden" name="hidnextname" id="hidnextid" 
                  value="${count}" />
              </c:if>
              <c:set var="hidsortname" 
                  value="<%= request.getParameter(\"hidsortname\")%>" />
              <input type="hidden" name="hidsortname" id="hidsortid" 
                  value="${hidsortname}"/>
              <input type="hidden" name="queuesize" id="queuesizeid" 
                  value="${queuesize}" />
              <input type="hidden" name="pagenoname" id="pagenoid" 
                  value="${start}" />
              <input type="hidden" name="queuestatus" id="queuestatusid"
                  value="${queuestatus}" />
            </td>
            <!-- FIFO/LIFO ordering -->
            <td align="right">
                <b><spring:message code='sana.queue_sort_by'/> &nbsp&nbsp</b>
                <b><input type="radio" name="sortname" id="lifo" value="LIFO" 
                    onclick="sortOrder(sortname);">Newest First &nbsp&nbsp
                <b><input type="radio" name="sortname" id="fifo" value="FIFO" 
                    onclick="sortOrder(sortname);">Oldest First</b>
            </td>
        </tr>
        <tr><td colspan="2">&nbsp</td></tr>
        <tr>
        
        <!-- Filter by procedure type  -->
        <td align="left">
            <input type=hidden name="totcountname" id="totcountid" 
                value="${count}"/>
            <!-- Drop down for filtering by Procedure title -->
            <b><spring:message code='sana.queue_procedure_title_select'/></b>
            <select name="comboPro" id="combopro" 
                onchange="onChangeSelectedProcedure();"> 
                <option selected onClick="clickprocedure('SHOW ALL');">
                    SHOW ALL
                </option>
                <c:forEach var="procedurelist1" items="${procedures}">
                    <c:set var="combo" 
                        value="<%= request.getParameter(\"proname\")%>" />
                    <c:if test="${combo == procedurelist1}" >
                        <option onclick="clickprocedure('${procedurelist1}');" 
                            selected>
                            ${procedurelist1}
                        </option>
                    </c:if>
                    <c:if test="${combo != procedurelist1}">
                        <option onclick="clickprocedure('${procedurelist1}');" >
                            ${procedurelist1}
                        </option>
                    </c:if>
                </c:forEach>
            </select>
        </td>
        <td align="right">
            <!-- Text input for searching procedures by name -->
            <b><spring:message code='sana.queue_procedure_title_search'/></b>
            <c:set var="pronametext" 
                    value="<%= request.getParameter(\"proname\")%>"  />
            <input type=text  id="proid" name="proname" size="30" 
                      title="Enter Procedure text for Search"  
                      value="${pronametext}" />
        </td>
        </tr>
    </table>
    </b>
<!-- End Table header -->   
    
<!-- End queue item table -->       
    <b class="boxHeader"><spring:message code='sana.queue_patient_cases'/></b>
        <div class="box">
            <table id="queueTable" cellpadding="10" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th>Archive</th>
                        <th><spring:message code='sana.queue_status'/></th>
                        <th><spring:message code='sana.queue_patient_id'/></th>
                        <th><spring:message code='sana.queue_patient_name'/></th>
                        <th><spring:message code='sana.queue_patient_age'/></th>
                        <th><spring:message code='sana.queue_patient_sex'/></th>
                        <th><spring:message code='sana.queue_procedure'/></th>
                        <th><spring:message code='sana.queue_date_taken' /></th>
                        <th><spring:message code='sana.queue_phone' /></th>
                        <th><spring:message code='sana.queue_media' /></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${queueItems}" varStatus="status">
                        <jsp:useBean id="status" 
                               type="javax.servlet.jsp.jstl.core.LoopTagStatus" />
                        <c:choose>
                            <c:when test="<%=status.getCount()%2==0%>">
                                <c:set var="rowclass" value="row-even" />
                            </c:when>
                            <c:otherwise>
                                <c:set var="rowclass" value="row-odd" />
                            </c:otherwise>
                        </c:choose>
                        
                     <tr class="<c:out value="${rowclass}"/>" 
                     id="row${item.queueItemId }">
                        <!-- Sending only checked Item.id to clickarr() -->
                        <td ><input type="checkbox" name="chk" 
                              value=${item.id} onclick="clickar(this)" > </td>
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
                            <a href="${pageContext.request.contextPath}/module/sana/queue/v1/encounterViewer.form?encounterId=${item.encounter.encounterId}">
                                ${item.procedureTitle}
                            </a>
                        </td>
                        <td valign="top">${item.dateCreated}<fmt:formatDate value="${item.dateCreated}" pattern="MM/dd/yyyy hh:mm"/></td>
                        <td valign="top">${item.phoneIdentifier}</td>
                        <td valign="top" style="white-space: nowrap;">
                        <ul class="mediaList">
                            <c:forEach var="obs" items="${item.encounter.obs }">
                            <c:if test="${obs.complex }">
                            <li>
                                <a href="${pageContext.request.contextPath}/moduleServlet/sana/complexObsServlet?obsId=${obs.obsId}&view=SHOW&viewType=DOWNLOAD">                                       
                                    <c:choose>
                                        <c:when test="${obs.valueText=='SOUND'}">
                                            <img border="0" src="${pageContext.request.contextPath }/moduleResources/sana/audioThumbnail.png"/>
                                        </c:when>
                                        <c:when test="${obs.valueText=='VIDEO'}">
                                            <img border="0" src="${pageContext.request.contextPath }/moduleResources/sana/videoThumbnail.png"/>
                                        </c:when>
                                        <c:when test="${obs.valueText=='BINARYFILE'}">
                                            <img border="0" src="${pageContext.request.contextPath }/moduleResources/sana/binaryThumbnail.png"/>
                                        </c:when>
                                        <c:otherwise>
                                            <img src="${pageContext.request.contextPath }/moduleServlet/sana/complexObsServlet?obsId=${obs.obsId }&view=VIEW_IMAGE" width="200"/>
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
<!-- End queue item table -->
        
<!-- Table footer -->       
<b class="boxHeader">
    <!-- Pagenation -->
    <table width="100%" class="boxHeader">
        <tr>
        <!-- Go to page by number entered into text box -->
        <td align="left">
            <b>
            <c:set var="gotopage" value="<%= request.getParameter(\"start\")%>" />
                Go To Page
            <input type=text name="start" id="gotopageid" value="${gotopage}" size=3/>
            <input  type=submit value="Go" onclick="return gotopage('${limit}','${count}')"/>
            </b>
        </td>
        <!-- ITEMS PER PAGE  -->
        <td align="center">
        <!--<input type=submit id="queuelimitsubmitid" name="queuelimitsubmitname" value="Items Per Page" title="Click here to set queue size">-->
            <b><spring:message code='sana.queue_items_per_page'/>
            <c:set var="queuelimtset" value="<%= request.getParameter(\"limit\")%>" />
            <input type=hidden name="limit" id="queuelimitid" value="${queuelimit}"/>
            <select name="queueLimit" id="queuelimit"
                onchange="onChangeQueueItemLimit();">
                <c:forEach var="i" begin="1" end="${maxsize}" step="1" varStatus="status">
                    <c:choose>
                      <c:when test="${i == limit}" >
                         <option onclick="return clickqueuelimit('${i}');" selected>${i}</option>
                      </c:when>
                      <c:otherwise>
                         <option onclick="return clickqueuelimit('${i}');">${i}</option>
                      </c:otherwise>
                    </c:choose>
                </c:forEach> 
            </select>
            </b>
        </td>
        <td align="right">
            <b>
            <c:if test="${pageno == 1}">
                <input type="submit" align="center" name="prevname" id="previd" 
                    value="Prev" onclick="pageNationPrev('${count}')" 
                    disabled="disabled"/>  
                &nbsp&nbsp&nbsp&nbsp&nbsp${start} - ${count}&nbsp&nbsp&nbsp
            </c:if>
            <c:if test="${pageno > 1}">
                <input type="submit" align="center" name="prevname" id="previd" 
                value="Prev" onclick="pageNationPrev('${count}')" />  
                &nbsp&nbsp&nbsp&nbsp&nbsp${pageno} - ${count}&nbsp&nbsp&nbsp
            </c:if>
            <c:if test="${pageno == count}">
                <input type="submit" name="nextname" id="nextid" value="Next" 
                onclick="pageNationNext('${count}')" disabled="disabled"/>
            </c:if>
            <c:if test="${pageno < count}">
                <input type="submit" name="nextname" id="nextid" value="Next" 
                onclick="pageNationNext('${count}')">
            </c:if>
            </b>
        </td>
        </tr>
        <tr><td colspan="3">&nbsp</td></tr>
        <tr>
            <td align="center" colspan="3">
               <input type="checkbox" name="checkselectall" id="chkselectid" 
                   title="Check to Select All" 
                   onclick="checkall(chk,checkdeselectall)" />
               <b>Select All &nbsp&nbsp</b>
                <input type="checkbox" name="checkdeselectall" id="chkdeselectid" 
                    title="Check to Deselect All" 
                    onclick="checknone(chk,checkselectall)" />
                <b>Select None &nbsp&nbsp</b>
                <input type=submit id="subarchive" name="subarchivename" 
                    value="Archive Selected" 
                    title="Click to Archive checked records">
            </td>
        </tr>   
    </table>
</form>
<%@ include file="/WEB-INF/template/footer.jsp" %>
