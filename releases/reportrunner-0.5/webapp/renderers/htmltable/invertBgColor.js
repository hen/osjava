  //HH: Copied from zebra.js


  // this function is needed to work around 
  // a bug in IE related to element attributes
  function hasClass(obj) {
     var result = false;
     if (obj.getAttributeNode("class") != null) {
         result = obj.getAttributeNode("class").value;
     }
     return result;
  }   

  function setTrColor(tr, color) {
      tr.bgcolor = color;

      // get all the cells in this row...
      var tds = tr.getElementsByTagName("td");
    
      // and iterate through them...
      for (var j = 0; j < tds.length; j++) {
        var mytd = tds[j];
        mytd.style.backgroundColor = color;
      }
  }


 function initInvertBgColor(id) {

    var defaultSelected = arguments[1] ? arguments[1] : "#dd0";

    // obtain a reference to the desired table
    // if no such table exists, abort
    var table = document.getElementById(id);
    if (! table) { return; }
    
    // by definition, tables can have more than one tbody
    // element, so we'll have to get the list of child
    // &lt;tbody&gt;s 
    var tbodies = table.getElementsByTagName("tbody");

    // and iterate through them...
    for (var h = 0; h < tbodies.length; h++) {
    
        // find all the &lt;tr&gt; elements... 
        var trs = tbodies[h].getElementsByTagName("tr");
      
        // ... and iterate through them
        for (var i = 0; i < trs.length; i++) {
                var tr = trs[i];
                addEvent(tr, "click", function(event) {

    var selected = document.getElementById('swatch').bgColor;
    if(selected == null) {
    selected = defaultSelected;
    } else {
    selected = selected.toLowerCase();
    }

                var td = null;
                if(event.target == null) { 
                    td = event.srcElement;
                } else {
                    td = event.target;
                }       
                var obj = td.parentNode;
                var unselected = obj.getAttribute('unselected');
                if(unselected == null) {
                    unselected = td.style.backgroundColor;
                    obj.setAttribute('unselected', unselected);
                }
                var color = obj.bgcolor;
                if(color == null) {
                    setTrColor(obj, selected);
                    return;
                }

                color = color.toLowerCase();
                if(color == unselected) {
                    setTrColor(obj, selected);
                } else
                if(color == selected) {
                    setTrColor(obj, unselected);
                    obj.removeAttribute('unselected');
                } else {
                    setTrColor(obj, selected);
                }
          } );
      }
    }
  }
