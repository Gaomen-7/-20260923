/* 从表单对象中提取非空字段,返回新对象。 */
export function pickForm( form ){
    let param = {};
    for( let key in form ){
        let val = form[key];
        if( val !== null && val !== undefined && val !== '' ){
            param[key] = val;
        }
    }
    return param;
}

/* 格式化日期时间为 YYYY-MM-DD HH:mm:ss */
export function formatDate( val ){
    if( !val ) return "";
    let date = new Date(val);
    let y = date.getFullYear();
    let m = (date.getMonth()+1).toString().padStart(2,'0');
    let d = date.getDate().toString().padStart(2,'0');
    let h = date.getHours().toString().padStart(2,'0');
    let mi = date.getMinutes().toString().padStart(2,'0');
    let s = date.getSeconds().toString().padStart(2,'0');
    return `${y}-${m}-${d} ${h}:${mi}:${s}`;
}
