
/*
	主要目的: 过滤为空的表单数据。
*/
export function pickForm( form ){
	//1.获取对象所有的键
	let keys = Object.keys( form );
	let param = {};
	//2.迭代所有键 K
	keys.forEach(
		( K )=>{
			let V = form[ K ];
			console.log(`[DEBUG][${K}]: [${V}]`);			
			if( V!==undefined ){
				if(typeof V =='string' && V.trim()==''){
					return;
				}
				console.log(`【SET】${K}:${V}`);
				param[ K ] = V;
			}
		}
	);
	return param;
}

export function formatDate( date ){
	if( date ){
		date = date.replace(/T/g, " ");
		date = date.replace(/(\..+)/g, "");
		return date;
	}	
	return "";
}

export function validate( form, rules ){
	for(let r=0; r<rules.length; r++){
		let R = rules[r];
		let V = form[R.key];
		if( V===undefined || V==='' ){
			return {
				result: false,
				message: R.message
			}
		}
	}
	return { result: true };
}



export function numToBool(form, KEY){
	let V = form[ KEY ];
	let ret = ( V==1 ) ? true : false;
	form[ KEY ] = ret;
}

export function boolToNum(form, KEY){
	let V = form[ KEY ];
	form[ KEY ] = (V) ? true : false;
}