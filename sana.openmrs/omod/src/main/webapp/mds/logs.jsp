<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/module/sana/localHeader.jsp" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/sana/jquery-1.4.4.js"></script>

<div>   
<h2><spring:message code="sana.mdsLog"/></h2>
</div>
<div id="output"></div>
<script language="javascript">
//Functions for Onclick methods
    var page = 1; 
    var auto = 0;
    var limit = 20;
    var logurl = "${pageContext.request.scheme}://" +
		      "${pageContext.request.localName}" +
		      ":${pageContext.request.serverPort}" +
		      "/mds/log/";
    var loglisturl = "${pageContext.request.scheme}://" +
              "${pageContext.request.localName}" +
              ":${pageContext.request.serverPort}" +
              "/mds/log/list";
		      
    function getLogs()
    {
    	$('#logs').load(loglisturl);
	    document.getElementById('status').innerHTML = new Date();
    }
    
    function getLogPage(p){
        page = p;
        $('#logs').load(loglisturl + "?page=" + page +"&limit=" + limit );
        document.getElementById('status').innerHTML = new Date();
    }
    
    function openLogsInNewWindow(){
        window.open(logurl);
    }
    function toggleDetails(id) {
        if(auto == 1){
                document.getElementById('refreshB').value = "Click to Start!";
                clearInterval();
                auto = 0;
            }
        var detail = $("#log-"+id+"-detail");
        if(!detail.hasClass("data")) {
            getDetails(id, function() { detail.addClass("data"); detail.toggle("slow"); });
        } else {
            detail.toggle("slow");
        }
    }
    function showDetail(id) {
        jQuery("#log-"+id+"-detail").show();
    }
    
    function json_update(msg, callback) {
        id = msg['id'];
        data = msg['data'];
        updateDetails(id,data);
        callback();
    }

    function getDetails(id, callback) {
        $.getJSON("/mds/log-detail/" + id, 
                {}, 
                function(data) {json_update(data, callback); });  
    }

    function buildRowHtml(record) { 
        return ("+" + "&nbsp;<b>"+ record['level_name'] + "&nbsp;" 
           + record['filename'] + ":" + record['line_number'] + "</b> &nbsp;" + record['message']);
    }

    function updateDetails(id, data) {
        var message = '<td colspan="3"><dl>';

        for (var i in data) {
      
            if("ERROR" == data[i]['level_name']){
                message += '<dd class="err">' + buildRowHtml(data[i]) + "</dd>";
            } else if("DEBUG" == data[i]['level_name']){
                message += '<dd class="debug">' + buildRowHtml(data[i]) + "</dd>";
            } else
            message += '<dd>' + buildRowHtml(data[i]) + '</dd>';

            //message = message + "" + data[i].message + "<br/>";
        }
        message = message + "</dl></td>";
        var detail = $("#log-"+id+"-detail");
        detail.html(message);
        detail.attr('data', data);
      
    }

    function hideDetail(id) {
        jQuery("#log-"+id+"-detail").hide();
    }
</script>
<style>
td.selectp
{
    text-decoration:underline;
}
.err
{
    background-color: #ffaaaa;
}
.debug
{
    background-color: #dffddd;
}
#logs
{
    background-color: #ffffff;
    
}
.logheader
{
    background-color: #e2baa5;
    font-weight: bold;
    width: inherit;
    padding: 0;
    border-spacing: 0px;
    border-collapse: collapse;
}
.pagenav
{
    color: #1100ff;
}
.detail
{
    background-color: #eeeeff;
}
</style>
<table>
    <tr width="100%">
        <td width="124px" align="left"><b>Last Updated:</b></td>
        <td  id="status" width="360px" align="center"></td>
        <td align="right">
        <input id="refreshB" type="button" onclick="getLogs()" value="Refresh Logs" />
        </td>   
    </tr>
</table>
<table width="100%">
    <tbody>
        <tr>
            <td class="pagenav">Go To Page:
                <select onchange="getLogPage(value)">
                    <option>1</option>
                    <option>2</option>
                    <option>3</option>
                </select>
            </td>
        </tr>
    </tbody>
</table>
<div id="logs">
...loading
</div>
<div id="dbg">
<p>Press the following button to show the mds logs in a new window.</p>
<input id="logButton" onClick="openLogsInNewWindow()" type="button" 
    value="Open Logs In New Window"/>
</div>
<script type="text/javascript">
    window.onLoad = getLogPage(1);
</script>
<%@ include file="/WEB-INF/template/footer.jsp" %>