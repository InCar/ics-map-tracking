// class Maptrack{
//   def: Object;
//   private  _initial(opt: Object) {
//     // 默认参数
//     def = {
//       mapType: '',
//         dom: '',
//         mapconfig: {
//           gps: [116.404, 39.915], // 经纬度
//           zoom: 10,                // 层级
//         },
//         mapTrack: false,
//         trackconfig: {
//           trackApi: '',
//           trackParam: {pageNum: 1, pageSize: 10}
//         }
//     };
//     this.def = extend(def,opt,true);
//     this.hasDom = false;
//     this.listeners = [];
//     this.handlers = {};
// }

//   // 对象合并
//   public extend(o:Object,n:Object,override:Boolean) {
//     for(var key in n){
//         if(n.hasOwnProperty(key) && (!o.hasOwnProperty(key) || override)){
//           if (typeof n[key] !== "object") o[key]=n[key];
//           else Object.assign(o[key], n[key])
//         }
//     }
//     return o;
// }
// }

// // plugin.js
// ;(function(undefined) {
//   "use strict"
//   var _global;



//    // 将插件对象暴露给全局对象
//    _global = (function(){ return this || (0, eval)('this'); }());
//    if (typeof module !== "undefined" && module.exports) {
//        module.exports = Maptrack;
//    } else if (typeof define === "function" && define.amd) {
//        define(function(){return Maptrack;});
//    } else {
//        !('Maptrack' in _global) && (_global.Maptrack = Maptrack);
//    }
//  }());



// class Maptrack {
//   opt: Object;
//   constructor(opt: Object) {
//       this.opt = opt;
//   }

//   _initial(){
//       // 默认参数
//       var def = {
//         mapType: '',
//           dom: '',
//           mapconfig: {
//             gps: [116.404, 39.915], // 经纬度
//             zoom: 10,                // 层级
//           },
//           mapTrack: false,
//           trackconfig: {
//             trackApi: '',
//             trackParam: {pageNum: 1, pageSize: 10}
//           }
//       };
//       this.def = extend(def,opt,true);
//   }
//    // 对象合并
//    public extend(o:Object,n:Object,override:Boolean) {
//     for(var key in n){
//         if(n.hasOwnProperty(key) && (!o.hasOwnProperty(key) || override)){
//           if (typeof n[key] !== "object") o[key]=n[key];
//           else Object.assign(o[key], n[key])
//         }
//     }
//     return o;
// }

// }