$(document).ready(function() {

	$(".buttons-group").on("click",function (e) {
		
	    switch ($(e.target).data("value")) {
	        case "listview":
		    	$("#list").css("display", "block");
		    	$("#map").css("display","none");
	            break;
	        case "mapview":
	       		$("#list").css("display","none");
	       		$("#map").css("display", "block");
	       		google.maps.event.trigger(map, 'resize');
	       		initMap();
	            break;
	    }
	});
});
 
 