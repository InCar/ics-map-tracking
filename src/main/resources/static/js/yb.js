// (function( global, factory ) {
//     if ( typeof module === "object" && typeof module.exports === "object" ) {
//         module.exports = global.document ?
//             factory( global, true ) :
//             function( w ) {
//                 if ( !w.document ) {
//                     throw new Error( "jQuery requires a window with a document" );
//                 }
//                 return factory( w );
//             };
//     } else {
//         factory( global );
//     }
// }(typeof window !== "undefined" ? window : this, function( window, noGlobal ) {
    // 构造函数
    function SetData(obj) {
        for (let key in obj) {
            console.log(key)
            this[key] = obj.key
        }
         this.initBmap = initBmap
        
    }

    // init 方法
    var maptrack = {}
    var dom = ''
    maptrack.init = function (obj) {
        let track = new SetData({
            dom: obj.dom,
            bmap: obj.bmap
        });
        dom = obj.dom
        //  console.log(track)
        //  if (track.bmap) track.initBmap(track.bmap)
    }
    window.maptrack = maptrack;
	//百度地图API功能
	function loadJScript() {
		var script = document.createElement("script");
		script.type = "text/javascript";
		script.src = `http://api.map.baidu.com/api?v=2.0&ak=ehokpezgpQESNRi1ld0fQmRSgAoO6YAG&callback=initBmap(${dom})`;
		document.head.appendChild(script);
	}
	function initBmap(dom) {
        var map = new BMap.Map(dom);            // 创建Map实例
		var point = new BMap.Point(116.404, 39.915); // 创建点坐标
		map.centerAndZoom(point,15);                 
		map.enableScrollWheelZoom();                 //启用滚轮放大缩小
    }  
    window.onload = loadJScript;  //异步加载地图
// }));

