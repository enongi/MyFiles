import {validatenull} from './validate'
import request from '@/router/axios'
import {mapGetters, mapState} from "vuex";
import store from '@/store'

// 表单序列化
export const serialize = data => {
  let list = []
  Object.keys(data).forEach(ele => {
    list.push(`${ele}=${data[ele]}`)
  })
  return list.join('&')
}
export const getObjType = obj => {
  var toString = Object.prototype.toString
  var map = {
    '[object Boolean]': 'boolean',
    '[object Number]': 'number',
    '[object String]': 'string',
    '[object Function]': 'function',
    '[object Array]': 'array',
    '[object Date]': 'date',
    '[object RegExp]': 'regExp',
    '[object Undefined]': 'undefined',
    '[object Null]': 'null',
    '[object Object]': 'object'
  }
  if (obj instanceof Element) {
    return 'element'
  }
  return map[toString.call(obj)]
}
/**
 * 对象深拷贝
 */
export const deepClone = data => {
  var type = getObjType(data)
  var obj
  if (type === 'array') {
    obj = []
  } else if (type === 'object') {
    obj = {}
  } else {
    // 不再具有下一层次
    return data
  }
  if (type === 'array') {
    for (var i = 0, len = data.length; i < len; i++) {
      obj.push(deepClone(data[i]))
    }
  } else if (type === 'object') {
    for (var key in data) {
      obj[key] = deepClone(data[key])
    }
  }
  return obj
}

/**
 * 寻找当前路由 替换除label，menuType 外属性
 */
export const replace = (obj1, obj2, obj3) => {
  delete obj1.close
// if(obj1.value === '/wel/index' || obj2.value === '/wel/index') {
//   return false
// }

  if ((obj1.value === obj3.fullPath) || obj3.fullPath.indexOf(encodeURIComponent(obj1.value)) != -1) {
    for (var attr in obj2) {
      if (attr !== 'label' && attr !== 'menuType' && attr !== 'key') {
        obj1[attr] = obj2[attr]
      }
    }
    return true
  }
  return false
}


/**
 * 判断路由是否相等
 */
export const diff = (obj1, obj2) => {

  delete obj1.close
  var o1 = obj1 instanceof Object
  var o2 = obj2 instanceof Object
  if (!o1 || !o2) { /*  判断不是对象  */
    return obj1 === obj2
  }

  //只对路径 和key进行比较
  // //若进入同一主体页面 直接 打开原主题页面
  var value1 = obj1['value']
  var value2 = obj2['value']
  if(/^http[s]?:\/\/.*/.test(value1) || /^http[s]?:\/\/.*/.test(value2) ){
    if(value1.indexOf('?')!=-1){
      value1 = value1.substring(0,value1.indexOf('?'))
    }
    if(value2.indexOf('?')!=-1){
      value2 = value2.substring(0,value2.indexOf('?'))
    }
    if(value1===value2){
      return true
    }else {
      return false
    }
  }


  var key1 = obj1['key']
  var key2 = obj2['key']
  if(value1.indexOf('?')!=-1){
    value1 = value1.substring(0,value1.indexOf('?'))
  }
  if(value2.indexOf('?')!=-1){
    value2 = value2.substring(0,value2.indexOf('?'))
  }
  if((value1 === value2) && ((key1 && key2 && key1 == key2)||(!key1 || !key2))){
    return true
  }else {
    return false
  }
  //
  //
  // for (var attr in obj1) {
  //   if (attr !== 'label' && attr !== 'menuType'   && attr !== 'type') {
  //     if(attr === 'value'){
  //       var a1 = getTagValue(obj1[attr])
  //       var a2 = getTagValue(obj2[attr])
  //       if(a1 !== a2){
  //         return false
  //       }
  //     }else {
  //       var t1 = obj1[attr] instanceof Object
  //       var t2 = obj2[attr] instanceof Object
  //       if (t1 && t2) {
  //         if(!diff(obj1[attr], obj2[attr])){
  //           return false
  //         }
  //       } else if (obj1[attr] && obj2[attr]  && obj1[attr] !== obj2[attr] ) {
  //         return false
  //       }
  //     }
  //
  //   }
  // }
  // return true
}

export const getTagValue = (val) =>{
  if(val.indexOf("?")!=-1){
    return val.substring(0,val.indexOf("?"))
  }else {
    return val
  }
}
/**
 * 设置灰度模式
 */
export const toggleGrayMode = (status) => {
  if (status) {
    document.body.className = document.body.className + ' grayMode'
  } else {
    document.body.className = document.body.className.replace(' grayMode', '')
  }
}
/**
 * 设置主题
 */
export const setTheme = (name) => {
  document.body.className = name
}

/**
 *加密处理
 */
export const encryption = (params) => {
  let {
    data,
    type,
    param,
    key
  } = params
  const result = JSON.parse(JSON.stringify(data))
  if (type === 'Base64') {
    param.forEach(ele => {
      result[ele] = btoa(result[ele])
    })
  } else {
    param.forEach(ele => {
      var data = result[ele]
      key = CryptoJS.enc.Latin1.parse(key)
      var iv = key
      // 加密
      var encrypted = CryptoJS.AES.encrypt(
        data,
        key, {
          iv: iv,
          mode: CryptoJS.mode.CBC,
          padding: CryptoJS.pad.ZeroPadding
        })
      result[ele] = encrypted.toString()
    })
  }
  return result
}

/**
 * 浏览器判断是否全屏
 */
export const fullscreenToggel = () => {
  if (fullscreenEnable()) {
    exitFullScreen();
  } else {
    reqFullScreen();
  }
};
/**
 * esc监听全屏
 */
export const listenfullscreen = (callback) => {
  function listen() {
    callback()
  }

  document.addEventListener("fullscreenchange", function () {
    listen();
  });
  document.addEventListener("mozfullscreenchange", function () {
    listen();
  });
  document.addEventListener("webkitfullscreenchange", function () {
    listen();
  });
  document.addEventListener("msfullscreenchange", function () {
    listen();
  });
};
/**
 * 浏览器判断是否全屏
 */
export const fullscreenEnable = () => {
  return document.isFullScreen || document.mozIsFullScreen || document.webkitIsFullScreen
}

/**
 * 浏览器全屏
 */
export const reqFullScreen = () => {
  if (document.documentElement.requestFullScreen) {
    document.documentElement.requestFullScreen();
  } else if (document.documentElement.webkitRequestFullScreen) {
    document.documentElement.webkitRequestFullScreen();
  } else if (document.documentElement.mozRequestFullScreen) {
    document.documentElement.mozRequestFullScreen();
  }
};
/**
 * 浏览器退出全屏
 */
export const exitFullScreen = () => {
  if (document.documentElement.requestFullScreen) {
    document.exitFullScreen();
  } else if (document.documentElement.webkitRequestFullScreen) {
    document.webkitCancelFullScreen();
  } else if (document.documentElement.mozRequestFullScreen) {
    document.mozCancelFullScreen();
  }
};
/**
 * 递归寻找子类的父类
 */

export const findParent = (menu, id) => {
  for (let i = 0; i < menu.length; i++) {
    if (menu[i].children.length != 0) {
      for (let j = 0; j < menu[i].children.length; j++) {
        if (menu[i].children[j].id == id) {
          return menu[i]
        } else {
          if (menu[i].children[j].children.length != 0) {
            return findParent(menu[i].children[j].children, id)
          }
        }
      }
    }
  }
}

/**
 * 动态插入css
 */

export const loadStyle = url => {
  const link = document.createElement('link')
  link.type = 'text/css'
  link.rel = 'stylesheet'
  link.href = url
  const head = document.getElementsByTagName('head')[0]
  head.appendChild(link)
}
/**
 * 判断路由是否相等
 */
export const isObjectValueEqual = (a, b) => {
  let result = true
  Object.keys(a).forEach(ele => {
    const type = typeof (a[ele])
    if (type === 'string' && a[ele] !== b[ele]) result = false
    else if (type === 'object' && JSON.stringify(a[ele]) !== JSON.stringify(b[ele])) result = false
  })
  return result
}
/**
 * 根据字典的value显示label
 */
export const findByvalue = (dic, value) => {
  let result = ''
  if (validatenull(dic)) return value
  if (typeof (value) === 'string' || typeof (value) === 'number' || typeof (value) === 'boolean') {
    let index = 0
    index = findArray(dic, value)
    if (index != -1) {
      result = dic[index].label
    } else {
      result = value
    }
  } else if (value instanceof Array) {
    result = []
    let index = 0
    value.forEach(ele => {
      index = findArray(dic, ele)
      if (index != -1) {
        result.push(dic[index].label)
      } else {
        result.push(value)
      }
    })
    result = result.toString()
  }
  return result
}
/**
 * 根据字典的value查找对应的index
 */
export const findArray = (dic, value) => {
  for (let i = 0; i < dic.length; i++) {
    if (dic[i].value == value) {
      return i
    }
  }
  return -1
}
/**
 * 生成随机len位数字
 */
export const randomLenNum = (len, date) => {
  let random = ''
  random = Math.ceil(Math.random() * 100000000000000).toString().substr(0, len || 4)
  if (date) random = random + Date.now()
  return random
}
/**
 * 打开小窗口
 */
export const openWindow = (url, title, w, h) => {
  // Fixes dual-screen position                            Most browsers       Firefox
  const dualScreenLeft = window.screenLeft !== undefined ? window.screenLeft : screen.left
  const dualScreenTop = window.screenTop !== undefined ? window.screenTop : screen.top

  const width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width
  const height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height

  const left = ((width / 2) - (w / 2)) + dualScreenLeft
  const top = ((height / 2) - (h / 2)) + dualScreenTop
  const newWindow = window.open(url, title, 'toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=yes, copyhistory=no, width=' + w + ', height=' + h + ', top=' + top + ', left=' + left)

  // Puts focus on the newWindow
  if (window.focus) {
    newWindow.focus()
  }
}

/**
 *  <img> <a> src 处理
 * @returns {PromiseLike<T | never> | Promise<T | never>}
 */
export function handleImg(fileName, id) {
  return validatenull(fileName) ? null : request({
    url: '/admin/file/' + fileName,
    method: 'get',
    responseType: 'blob'
  }).then((response) => { // 处理返回的文件流
    let blob = response.data;
    let img = document.getElementById(id);
    img.src = URL.createObjectURL(blob);
    window.setTimeout(function () {
      window.URL.revokeObjectURL(blob)
    }, 0)
  })
}

export function getDicLabel(arr, value) {
  let label = '';
  for (let i = 0; i < arr.length; i++) {
    if (arr[i].value == value) {
      return arr[i].label
    }
  }
  return label;
}

export function setTreeArea(source) {
  let cloneData = JSON.parse(JSON.stringify(source))    // 对源数据深度克隆
  let tree = cloneData.filter(father => {              //循环所有项
    let branchArr = cloneData.filter(child => {
      return father.areainnercode == child.parentnode      //返回每一项的子级数组
    });
    if (branchArr.length > 0) {
      father.children = branchArr;    //如果存在子级，则给父级添加一个children属性，并赋值
    }
    return father.parentnode == 144000000;      //返回第一层
  });
  return tree     //返回树形数据
}

export function setTreeDebtType(source) {
  let cloneData = JSON.parse(JSON.stringify(source))    // 对源数据深度克隆
  let tree = cloneData.filter(father => {              //循环所有项
    let branchArr = cloneData.filter(child => {
      return father.value == child.highervalue      //返回每一项的子级数组
    });
    if (branchArr.length > 0 && father.levelcode == 1) {
      father.children = branchArr;    //如果存在子级，则给父级添加一个children属性，并赋值
    }
    return father.highervalue == '-1';      //返回第一层
  });
  return tree     //返回树形数据
}

export function transArea(codes) {
  return request({
    url: '/platform/companybasicinfo/transArea/' + codes,
    method: 'get'
  })
}

export function transIndustry(codes) {
  return request({
    url: '/platform/companybasicinfo/transIndustry/' + codes,
    method: 'get'
  })
}

export function getRatingDic() {
  return request({
    url: '/platform/systeminoutratinginfo/rating',
    method: 'get',
  })
}

export function getIndustryDic() {
  return request({
    url: '/platform/sysinterindusinfo/indusDic',
    method: 'get',
  })
}
export function getSWDic() {
  return request({
    url: '/platform/sysindustrytype/sWCode',
    method: 'get',
  })
}

export function getAreaDic() {
  return request({
    url: '/platform/sysareacodeinfo/areaCode',
    method: 'get',
  })
}

export function getAreaInnerDic() {
  return request({
    url: '/platform/sysareacodeinfo/areaInnerCode',
    method: 'get',
  })
}

/**
 * 数组类型转字符串
 * @param array
 * @returns {string}
 */
export function arrayToString(array) {
  let len = array.length
  let str = ''
  for (let i = 0; i < len; i++) {
    i == len-1 ? str += array[i] : str += (array[i] + ',')
  }
  return str
}

/**
 * 数组类型转字符串
 * @param array
 * @returns {string}
 */
export function arrayToStringLine(array) {
  let len = array.length
  let str = ''
  for (let i = 0; i < len; i++) {
    i == len-1 ? str += array[i] : str += (array[i] + '/')
  }
  return str
}




