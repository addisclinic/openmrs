<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/module/sana/localHeader.jsp" %>
<div>   
<h2><spring:message code="sana.mdsSXML"/></h2>
</div>
<div id="output"></div>
<style>
div
{
    background-color:#ffffff;
    margin: auto;
}
.main
{
    background-color:#ffffff;
    margin: auto;
}

.table
{
    width:100%;
}
.header
{
    background-color:#e5eecc;
    width:100%;
    height: 48px;
}
.content
{
    background-color:#ffffff;
}
.tools
{
    width:100%;
    background-color:#e5eecc;
    color:#000000;
    valign: top;
    height: 400px;
    padding: 3px 8px 3px 5px;
    spacing: 3px 3px 3px 3px;
}

.footer
{
    valign: center;
    text-align: center;
    colspan: 2;
}
.code
{
    border:1px solid #c3c3c3;
    width:100%;
    height:400px;
    background-color:#ffffff;
    color:#000000;
    valign: top;   
}

</style>
<script type="text/javascript">

var mds = "${pageContext.request.scheme}://" +
          "${pageContext.request.localName}" +
          ":${pageContext.request.serverPort}" +
          "/mds/";

function validateXML(obj){
    var txt = document.getElementById(obj).value;
    document.getElementById('output').innerHTML = txt;
    if (window.DOMParser)
    {
        parser=new DOMParser();
        xmlDoc=parser.parseFromString(txt,"text/xml");
    }
    else // Internet Explorer
    {
        xmlDoc=new ActiveXObject("Microsoft.XMLDOM");
        xmlDoc.async=false;
        xmlDoc.loadXML(txt); 
     }
}

function getCode(){
   return document.getElementById('text').innerHTML;
}


function doSubmit(){
     document.forms["procedure"].submit();

}
     
function onSubmit(){
       xmlhttp=new XMLHttpRequest();
       xmlhttp.onreadystatechange=function()
       {
            
             document.getElementById('output').innerHTML = "..." + xmlhttp.responseText;
       }
       //msg = escape("<procedure><text>"+ document.getElementById('text').value + "<text></procedure>");
       xmlhttp.open("POST", mds + "p/");
       xmlhttp.send(document.forms["procedure"].value);
       document.getElementById('output').innerHTML = "Waiting...";
}

</script>

<div class="main">
    <form id='procedure' method="post" target="view" type="miltipart/form-encoded" action="http://dev.sana.csail.mit.edu/mds/p/">
    <table class="tools" cellspacing="10">
        <tr>
            <td colspan="2"><h3>Procedure XML Validator</h3></td>
        </tr>
        <tr>
            <td><b>XML</b></td>
            <td><b>Results</b></td>
        </tr>
        <tr>
            <td width="50%">
                <form name="procedure">
                <textarea  name="text" id="code" class="code">Copy and paste your form here....</textarea>
                </form>
            </td>
            <td width="50%">
                <iframe id="output" name="view" class="code"></iframe>
            </td>
        </tr>
        <tr>
            <td colspan="2" class="footer">
                <input align="center" type="submit" id="validate" value="Click to validate!" onclick="doSubmit()"></input>
            </td>
            <td></td>
        </tr>
    </table>
    </form>

    <div class="h1" height="100%">
        <p><a href="http://sana.mit.edu/wiki/index.php?title=How_to_Define_Your_Own_Procedures">Custom Procedure Documentation</p>
    </div>

    <div>
        <h2 class="header">Available Schema</h2>
        <ul>
        <li><a href="http://dev.sana.csail.mit.edu/xml/procedure.xsd">Procedure Schema(XSD)</a></li>
        <li><a href="http://dev.sana.csail.mit.edu/xml/procedure.dtd">Procedure Document Type Declaration(DTD)</a></li>
        <li><a href="http://dev.sana.csail.mit.edu/xml/catalog.xml">Namespace Schema Catalog</a></li>
        </ul>
    </div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>