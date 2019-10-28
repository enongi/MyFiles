export const calcDate = (date1, date2) => {
  var date3 = date2 - date1

  var days = Math.floor(date3 / (24 * 3600 * 1000))

  var leave1 = date3 % (24 * 3600 * 1000) // 计算天数后剩余的毫秒数
  var hours = Math.floor(leave1 / (3600 * 1000))

  var leave2 = leave1 % (3600 * 1000) // 计算小时数后剩余的毫秒数
  var minutes = Math.floor(leave2 / (60 * 1000))

  var leave3 = leave2 % (60 * 1000) // 计算分钟数后剩余的毫秒数
  var seconds = Math.round(date3 / 1000)
  return {
    leave1,
    leave2,
    leave3,
    days: days,
    hours: hours,
    minutes: minutes,
    seconds: seconds
  }
}

/**
 * 日期格式化
 */
export function dateFormat(date, format) {
  //let format = 'yyyy-MM-dd hh:mm:ss';
  if (date != 'Invalid Date') {
    var o = {
      "M+": date.getMonth() + 1, //month
      "d+": date.getDate(), //day
      "h+": date.getHours(), //hour
      "m+": date.getMinutes(), //minute
      "s+": date.getSeconds(), //second
      "q+": Math.floor((date.getMonth() + 3) / 3), //quarter
      "S": date.getMilliseconds() //millisecond
    }
    if (/(y+)/.test(format)) format = format.replace(RegExp.$1,
      (date.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
      if (new RegExp("(" + k + ")").test(format))
        format = format.replace(RegExp.$1,
          RegExp.$1.length == 1 ? o[k] :
            ("00" + o[k]).substr(("" + o[k]).length));
    return format;
  }
  return '';

}


export function dateToDate(date) {
  var sDate = new Date();
  if (typeof date == 'object'
    && typeof new Date().getMonth == "function"
  ) {
    sDate = date;
  }
  else if (typeof date == "string") {
    var arr = date.split('-')
    if (arr.length == 3) {
      sDate = new Date(arr[0] + '-' + arr[1] + '-' + arr[2]);
    }
  }

  return sDate;
}


export function addMonth(date, num) {
  num = parseInt(num);
  var sDate = dateToDate(date);

  var sYear = sDate.getFullYear();
  var sMonth = sDate.getMonth() + 1;
  var sDay = sDate.getDate();

  var eYear = sYear;
  var eMonth = sMonth + num;
  var eDay = sDay;
  while (eMonth > 12) {
    eYear++;
    eMonth -= 12;
  }

  var eDate = new Date(eYear, eMonth - 1, eDay);

  while (eDate.getMonth() != eMonth - 1) {
    eDay--;
    eDate = new Date(eYear, eMonth - 1, eDay);
  }

  return eDate;
}

export function getDate(strDate) {
    var st = strDate;
    var a = st.split(" ");
    var b = a[0].split("-");
    var c = a[1].split(":");
    var date = new Date(b[0], b[1], b[2], c[0], c[1], c[2]);
    return date;
  }
