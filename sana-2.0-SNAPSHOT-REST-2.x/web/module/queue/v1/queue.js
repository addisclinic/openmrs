    

   var PREV_ID = "hidprevid";
   var NEXT_ID = "hidnextid";
   var GOTO_ID = "gotopageid";
   var QLIMIT_ID = "queuelimitid";
   var QFORM_ID = "queueFormid";
   var QSORT_ID = "hidsortid";
   
   //Functions for Onclick methods
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