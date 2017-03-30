
$(document).ready(function() {

$(".btn-group").on("click",function (e) {
console.log("clicked", $(e.target).data("value"));
    switch ($(e.target).data("value")) {
        case "listview":
	    	$("#display_list_view").css("display", "block");
	    	//$("#map").css("left","-100%");
	    	$("#display_map_view").css("display","none");
            break;
        case "mapview":
       		$("#display_list_view").css("display","none");
       		$("#display_map_view").css("display", "block");
       		google.maps.event.trigger(map, 'resize');
       		//$("#display_map_view").css("left", "0%");
       		//$("#map").css("left","0%");
            break;
    }
});
 });
 
 