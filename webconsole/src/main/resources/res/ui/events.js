/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
function render() {
    renderStatusLine();
    renderTable( ["Received", "Topic", "Properties"] );
    renderStatusLine();	
}

function renderStatusLine() {
	document.write( "<div class='fullwidth'>");
    document.write( "<div class='statusline'></div>" );
    document.write( "</div>" );
}

function renderTable( /* Array of String */ columns ) {
    renderButtons();
	document.write( "<div class='table'>");
    document.write( "<table id='events' class='tablelayout'>" );

    document.write( "<thead><tr>" );
    for ( var name in columns ) {
        document.write( "<th>" + columns[name] + "</th>" );
    }
    document.write( "</tr></thead><tbody>" );
    document.write( "</tbody></table>" );
    document.write( "</div>");
    renderButtons();
}

function renderButtons( ) {
	document.write( "<div class='fullwidth'>");
    document.write( "<div class='buttons'>" );
    document.write( "<div class='button'><button id='reloadButton' type='button' name='reload'>Reload</button></div>" );
    document.write( "<div class='button'><button id='clearButton' type='button' name='clear'>Clear List</button></div>" );
    document.write( "</div>" );
    document.write( "</div>" );
}

function renderData( eventData )  {
	$(".statusline").empty().append(eventData.status);
	$("#events > tbody > tr").remove();	
    for ( var idx in eventData.data ) {
        entry( eventData.data[idx] );
    }
}

function entry( /* Object */ dataEntry ) {
    var trElement = tr( null, { id: "entry" + dataEntry.id } );
    entryInternal( trElement,  dataEntry );
	$("#events > tbody").append(trElement);	
}


function entryInternal( /* Element */ parent, /* Object */ dataEntry ) {
    var id = dataEntry.id;
    var topic = dataEntry.topic;
    var properties = dataEntry.properties;

    parent.appendChild( td( null, null, [ text( new Date(dataEntry.received) ) ] ) );
    parent.appendChild( td( null, null, [ text( topic ) ] ) );

    var propE;
    if ( dataEntry.info ) {
    	propE = text(dataEntry.info);
    } else {
	    var tableE = createElement("table");
	    var bodyE = createElement("tbody");
	    tableE.appendChild(bodyE);
	
	    for( var p in dataEntry.properties ) {
	    	bodyE.appendChild(tr(null, null, [td(null, null, [text(p)] ),
	    	                                  td(null, null, [text(dataEntry.properties[p])])]));
	    }
	    propE = tableE;
    }
    
    parent.appendChild( td( null, null, [propE] ) );
}

function loadData() {
	$.get(pluginRoot + "/data.json", null, function(data) {
	    renderData(data);
	}, "json");	
}

function renderEvents() {
	render();
	
    loadData();
    
    $("#events").tablesorter();
    $("#reloadButton").click(loadData);
    $("#clearButton").click(function () {
    	$("#events > tbody > tr").remove();
    	$.post(pluginRoot, { "action":"clear" }, function(data) {
    	    renderData(data);
    	}, "json");
    });
}