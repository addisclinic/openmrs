<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/module/sana/localHeader.jsp" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/sana/jquery-1.4.4.js"></script>

<script type="text/javascript">
			
			function validateForm(type) {
				var result = true;
				if(type == "add"){
					var file = document.getElementById("csvFile");
					var csName = document.getElementById("conceptSourceName1");
					var csDescription = document.getElementById("conceptSourceDescription1");
					var colConceptName = document.getElementById("columnConceptName1");
					var colIDNum = document.getElementById("columnIdNum1");
					
					if (file.value == "") {
						document.getElementById("csvFileError").style.display = "";
						result = false;
					} else{
						document.getElementById("csvFileError").style.display = "none";
					}
					
					if (csName.value == "") {
						document.getElementById("conceptSourceName1Error").style.display = "";
						result = false;
					} else{
						document.getElementById("conceptSourceName1Error").style.display = "none";
					}
					
					if (csDescription.value == "") {
						document.getElementById("conceptSourceDescription1Error").style.display = "";
						result = false;
					}else{
						document.getElementById("conceptSourceDescription1Error").style.display = "none";
					}
					
					if (colConceptName.value == "") {
						document.getElementById("columnConceptName1Error").style.display = "";
						result = false;
					}else{
						document.getElementById("columnConceptName1Error").style.display = "none";
					}
					
					if (colIDNum.value == "") {
						document.getElementById("columnIdNum1Error").style.display = "";
						result = false;
					}else{
						document.getElementById("columnIdNum1Error").style.display = "none";
					}
					
				}
				else if (type == "update"){
						var file1 = document.getElementById("retiredCsvFile");
						var file2 = document.getElementById("newCsvFile");
						var csName = document.getElementById("conceptSourceName2");
						var colConceptName = document.getElementById("columnConceptName2");
						var colIDNum = document.getElementById("columnIdNum2");
						
						if (file1.value == "" && file2.value == "") {
							document.getElementById("retiredCsvFileError").style.display = "";
							result = false;
						} else{
							document.getElementById("retiredCsvFileError").style.display = "none";
						}
						
						if (csName.value == "") {
							document.getElementById("conceptSourceName2Error").style.display = "";
							result = false;
						}else{
							document.getElementById("conceptSourceName2Error").style.display = "none";
						}
						
						if (colConceptName.value == "") {
							document.getElementById("columnConceptName2Error").style.display = "";
							result = false;
						}else{
							document.getElementById("columnConceptName2Error").style.display = "none";
						}
						
						if (colIDNum.value == "") {
							document.getElementById("columnIdNum2Error").style.display = "";
							result = false;
						}else{
							document.getElementById("columnIdNum2Error").style.display = "none";
						}
				}
				else if (type == "delete"){
					var csName = document.getElementById("conceptSourceName3");
					if (csName.value == "") {
						document.getElementById("conceptSourceName3Error").style.display = "";
						result = false;
					}else{
						var r=confirm("Are you sure you want to delete the concept source " + csName.value + "?");
						result = r;
					}
				}
				else{
					result = false;
				}
				return result;
			}
			
</script>

<h2><spring:message code='sana.modify_concept_dictionary'/></h2><br>
<b class="boxHeader"><spring:message code='sana.add_vocabulary'/></b>
	<div class="box">
		<form name="input" action="${pageContext.request.contextPath }/moduleServlet/sana/lexiconServlet" onSubmit="return validateForm('add')" enctype="multipart/form-data" method="post">
		<input type="hidden" name="action" value="add"/>
		<table cellpadding="5" cellspacing="0">
            <tr>
                <td><b><spring:message code='sana.upload_file'/></b></td>
                <td/>
            </tr>
            <tr>
                <td><spring:message code='sana.select_csv_file'/>: </td>
                <td><input type="file" id="csvFile" name="csvFile" size="40"/>
                <span class="error" id="csvFileError" style="display:none">Upload a csv file</span></td>
            </tr>
            <tr>
                <td><b><spring:message code='sana.create_concept_source'/></b></td>
                <td/>
            </tr>
            <tr>
                <td><spring:message code='sana.name_concept_source'/>: </td>
                <td><input type="text" id="conceptSourceName1" name="conceptSourceName" size="40"/>
                <span class="error" id="conceptSourceName1Error" style="display:none">Enter concept source name</span></td>
                
            </tr>
            <tr>
                <td><spring:message code='sana.name_concept_source_code'/>: </td>
                <td><input type="text" id="conceptSourceCode1" name="conceptSourceCode" size="40"/>
                <span class="error" id="conceptSourceCode1Error" style="display:none">Enter concept source name</span></td>
                
            </tr>
            <tr>
                <td><spring:message code='sana.description_concept_source'/>: </td>
                <td><input type="text" id="conceptSourceDescription1" name="conceptSourceDescription" size="40"/>
                <span class="error" id="conceptSourceDescription1Error" style="display:none">Enter description</span></td>
                
            </tr>
            <tr>
                <td><b><spring:message code='sana.parsing'/></b></td>
                <td/>
            </tr>
            <tr>
                <td><spring:message code='sana.column_concept_name'/>: </td>
                <td><input type="text" id="columnConceptName1" name="columnConceptName" size="40"/>
                <span class="error" id="columnConceptName1Error" style="display:none">Enter column number for concept names</span></td>
                
            </tr>
            <tr>
                <td><spring:message code='sana.column_id_num'/>: </td>
                <td><input type="text" id="columnIdNum1" name="columnIdNum" size="40"/>
                <span class="error" id="columnIdNum1Error" style="display:none">Enter column number for ID #s of concepts</span></td>
                
            </tr>
            <tr>
                <td><spring:message code='sana.column_concept_class'/>: </td>
                <td><input type="text" id="columnConceptClass1" name="columnConceptClass" size="40"/></td>
            </tr>
            
            <tr>
                <td><spring:message code='sana.column_concept_description'/>: </td>
                <td><input type="text" id="columnConceptDescription1" name="columnConceptDescription" size="40"/></td>
            </tr>
        </table>
        <div id="addVocabButton" style="margin-top: 5px;margin-left:5px;">       
            <input type="submit" value="Submit"/>
        </div>
		</form>
	</div>

<br>

<b class="boxHeader"><spring:message code='sana.update_vocabulary'/></b>
	<div class="box">
		<form name="input" action="${pageContext.request.contextPath }/moduleServlet/sana/lexiconServlet" onSubmit="return validateForm('update')" enctype="multipart/form-data" method="post">
		<input type="hidden" name="action" value="update"/>
		<table cellpadding="5" cellspacing="0">
            <tr>
                <td><b><spring:message code='sana.upload_files'/></b></td>
                <td/>
            </tr>
            <tr>
                <td><spring:message code='sana.select_retired_csv_file'/>: </td>
                <td><input type="file" id="retiredCsvFile" name="retiredCsvFile" size="40"/>
                <span class="error" id="retiredCsvFileError" style="display:none">Upload a retired or new concepts file</span></td>
            </tr>
            <tr>
                <td><spring:message code='sana.select_new_csv_file'/>: </td>
                <td><input type="file" id="newCsvFile" name="newCsvFile" size="40"/></td>
            </tr>
            <tr>
                <td><b><spring:message code='sana.select_concept_source'/></b></td>
                <td/>
            </tr>
            <tr>
                <td><spring:message code='sana.name_existing_concept_source'/>: </td>
                <td>
                	<select id="conceptSourceName2" name="conceptSourceName">
                		<c:forEach var="source" items="${sources}">
                			<option>${source.name}</option>
						</c:forEach>
					</select>
					<span class="error" id="conceptSourceName2Error" style="display:none">Select concept source</span>
                </td>
            </tr>
            <tr>
                <td><b><spring:message code='sana.parsing'/></b></td>
                <td/>
            </tr>
            <tr>
                <td><spring:message code='sana.column_concept_name'/>: </td>
                <td><input type="text" id="columnConceptName2" name="columnConceptName" size="40"/>
                <span class="error" id="columnConceptName2Error" style="display:none">Enter column number for concept names</span></td>
            </tr>
             <tr>
                <td><spring:message code='sana.column_id_num'/>: </td>
                <td><input type="text" id="columnIdNum2" name="columnIdNum" size="40"/>
                <span class="error" id="columnIdNum2Error" style="display:none">Enter column number for ID #s of concepts</span></td>
            </tr>
            <tr>
                <td><spring:message code='sana.column_concept_class'/>: </td>
                <td><input type="text" id="columnConceptClass2" name="columnConceptClass" size="40"/></td>
            </tr>
            <tr>
                <td><spring:message code='sana.column_concept_description'/>: </td>
                <td><input type="text" id="columnConceptDescription2" name="columnConceptDescription" size="40"/></td>
            </tr>
        </table>
        <div id="updateVocabButton" style="margin-top: 5px;margin-left:5px;">       
            <input type="submit" value="Submit"/>
        </div>
		</form>
	</div>
<br>
<b class="boxHeader"><spring:message code='sana.delete_vocabulary' /></b>
<div class="box">
	<form name="input"
		action="${pageContext.request.contextPath }/moduleServlet/sana/lexiconServlet"
		onSubmit="return validateForm('delete')" enctype="multipart/form-data"
		method="post">
		<input type="hidden" name="action" value="delete" />
		<table cellpadding="5" cellspacing="0">
			<tr>
				<td><b><spring:message
							code='sana.select_concept_source_delete' /></b></td>
				<td />
			</tr>
			<tr>
				<td><spring:message code='sana.name_existing_concept_source' />:
				</td>
				<td><select id="conceptSourceName3" name="conceptSourceName">
						<c:forEach var="source" items="${sources}">
							<option>${source.name}</option>
						</c:forEach>
				</select> <span class="error" id="conceptSourceName3Error"
					style="display: none">Select concept source</span></td>
			</tr>
		</table>
		<div id="updateVocabButton" style="margin-top: 5px; margin-left: 5px;">
			<input type="submit" value="Submit" />
		</div>
	</form>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>


