function postJSON( _obj ){
    _obj['type'] = "POST";
    _obj['dataType'] = "json";
    $.ajax( _obj );
}

function getJSON( _obj ){
    _obj['type'] = "GET";
    _obj['dataType'] = "json";
    $.ajax( _obj );
}

function postWithBody( _obj ){
    _obj['type'] = "POST";
    _obj['dataType'] = "json";
	_obj['data'] = JSON.stringify(_obj.data);
	_obj['contentType'] = 'application/json;charset=utf-8';
    $.ajax( _obj );
}

function callback( resp ){
	console.log( resp );
    //alert("²Ù×÷³É¹¦¡£");
}

function pickData( keys ){
	var obj = {};
	keys.forEach(
	  x=>{
		obj[ x ] = $("#"+ x).val();
	});
	return obj;
}




          