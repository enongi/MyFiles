/* eslint-disable */
import XLSX from 'xlsx';
import request from '@/router/axios'


var tranKey = [];
var tranDic = {};
var isSelection = false

function auto_width(ws, data) {
  /*set worksheet max width per col*/
  const colWidth = data.map(row => row.map(val => {
    /*if null/undefined*/
    if (val == null) {
      return {
        'wch': 10
      };
    }
    /*if chinese*/
    else if (val.toString().charCodeAt(0) > 255) {
      return {
        'wch': val.toString().length * 2
      };
    } else {
      return {
        'wch': val.toString().length
      };
    }
  }))
  /*start in the first row*/
  let result = colWidth[0];
  for (let i = 1; i < colWidth.length; i++) {
    for (let j = 0; j < colWidth[i].length; j++) {
      if (result[j]['wch'] < colWidth[i][j]['wch']) {
        result[j]['wch'] = colWidth[i][j]['wch'];
      }
    }
  }
  ws['!cols'] = result;
}

function json_to_array(key, jsonData) {
  return jsonData.map(v => key.map(j => {
    return v[j]
  }));
}

// fix data,return string
function fixdata(data) {
  let o = ''
  let l = 0
  const w = 10240
  for (; l < data.byteLength / w; ++l) o += String.fromCharCode.apply(null, new Uint8Array(data.slice(l * w, l * w + w)))
  o += String.fromCharCode.apply(null, new Uint8Array(data.slice(l * w)))
  return o
}

// get head from excel file,return array
function get_header_row(sheet) {
  const headers = []
  const range = XLSX.utils.decode_range(sheet['!ref'])
  let C
  const R = range.s.r /* start in the first row */
  for (C = range.s.c; C <= range.e.c; ++C) { /* walk every column in the range */
    var cell = sheet[XLSX.utils.encode_cell({
      c: C,
      r: R
    })] /* find the cell in the first row */
    var hdr = 'UNKNOWN ' + C // <-- replace with your desired default
    if (cell && cell.t) hdr = XLSX.utils.format_cell(cell)
    headers.push(hdr)
  }
  return headers
}

export const export_table_to_excel = (id, filename) => {
  const table = document.getElementById(id);
  const wb = XLSX.utils.table_to_book(table);
  //console.log(XLSX.utils.table_to_book(table))
  XLSX.writeFile(wb, filename);

  /* the second way */
  // const table = document.getElementById(id);
  // const wb = XLSX.utils.book_new();
  // const ws = XLSX.utils.table_to_sheet(table);
  // XLSX.utils.book_append_sheet(wb, ws, filename);
  // XLSX.writeFile(wb, filename);
}

export const export_json_to_excel = ({
                                       data,
                                       key,
                                       title,
                                       filename,
                                       autoWidth
                                     }) => {
  const wb = XLSX.utils.book_new();
  data.unshift(title);
  const ws = XLSX.utils.json_to_sheet(data, {
    header: key,
    skipHeader: true
  });
  if (autoWidth) {
    const arr = json_to_array(key, data);
    auto_width(ws, arr);
  }
  XLSX.utils.book_append_sheet(wb, ws, filename);
  XLSX.writeFile(wb, filename + '.xlsx');
}

export const excelExport = ({
                              option,
                              data,
                              filename,
                          extras,
                          ifTree
                            }) => {
  tranKey = []
  tranDic = {}
  isSelection = false

  var params = analyseOption(option,extras);
  //console.log(params)

  //树形表格遍历子集
  if(ifTree){
    data = analyseData(data,0)
  }

  tranKey

  tranDic

  var pros = []
  for (var i = 0; i < tranKey.length; i++) {
    var promise = translate(tranDic[tranKey[i]]);
    pros.push(promise);
  }
  Promise.all(pros).then(function (values) {
    for (var i = 0; i < values.length; i++) {
      tranDic[tranKey[i]] = values[i].data.data;
    }
    export_array_to_excel({
      key: params.key,
      data: data,
      title: params.title,
      filename: filename,
      autoWidth: true
    });
  });
}

export const analyseOption = (option,extras) => {
  isSelection = option.selection;
  var datas = option.column;
  var key = []
  var title = []
  var children = []
  datas.map(function (v, i) {
    if (!v.hide || (extras && extras.indexOf(v.prop)!=-1)) {
      if (!datas[i].children) {
        key.push(v.prop)
        title.push(v.label)
        //children.push('')
        if (v.dicUrl) {
          tranKey.push(v.prop)
          tranDic[v.prop] = v.dicUrl
        }
      } else {
        datas[i].children.map(function (vv, ii) {
          if (!datas[i].children[ii].children){
            key.push(vv.prop)
            title.push(v.label+'/'+vv.label)
            //children.push(vv.label)

            //console.log(vv.prop)
            if (vv.dicUrl) {
              tranKey.push(vv.prop)
              tranDic[vv.prop] = vv.dicUrl
              //console.log(tranDic[vv.prop])
            }
          } else {
            datas[i].children[ii].children.map(function (vvv,iii) {
              key.push(vvv.prop)
              title.push(v.label+'/'+vv.label+'/'+vvv.label)
              if (vvv.dicUrl) {
                tranKey.push(vvv.prop)
                tranDic[vvv.prop] = vvv.dicUrl
                //console.log(tranDic[vvv.prop])
              }
            })
          }
        })
      }
    }



  // isSelection = option.selection;
  // var datas = option.column;
  // var key = []
  // var title = []
  // datas.map(function (v, i) {
  //   if (!v.hide) {
  //     if (!datas[i].children) {
  //       key.push(v.prop)
  //       title.push(v.label)
  //       if (v.dicUrl) {
  //         tranKey.push(v.prop)
  //         tranDic[v.prop] = v.dicUrl
  //       }
  //     } else {
  //       datas[i].children.map(function (vv, ii) {
  //         if (!datas[i].children[ii].children){
  //           key.push(vv.prop)
  //           title.push(vv.label)
  //
  //           console.log(vv.prop)
  //           if (vv.dicUrl) {
  //             tranKey.push(vv.prop)
  //             tranDic[vv.prop] = vv.dicUrl
  //             console.log(tranDic[vv.prop])
  //           }
  //         } else {
  //           datas[i].children[ii].children.map(function (vvv,iii) {
  //             key.push(vvv.prop)
  //             title.push(vvv.label)
  //
  //             console.log(vvv.prop)
  //             if (vvv.dicUrl) {
  //               tranKey.push(vvv.prop)
  //               tranDic[vvv.prop] = vvv.dicUrl
  //               console.log(tranDic[vvv.prop])
  //             }
  //           })
  //         }
  //       })
  //     }
  //   }



    // if (!v.hide) {
    //   if (!datas[i].children) {
    //     key.push(v.prop)
    //     title.push(v.label)
    //     if (v.dicUrl) {
    //       tranKey.push(v.prop)
    //       tranDic[v.prop] = v.dicUrl
    //     }
    //   } else {
    //     datas[i].children.map(function (vv, ii) {
    //       key.push(vv.prop)
    //       title.push(vv.label)
    //
    //       console.log(vv.prop)
    //       if (vv.dicUrl) {
    //         tranKey.push(vv.prop)
    //         tranDic[vv.prop] = vv.dicUrl
    //         console.log(tranDic[vv.prop])
    //       }
    //     })
    //   }
    // }
    
    
    
    // if(!v.hide){
    //   console.log(datas[i].children)
    //   key.push(v.prop)
    //   title.push(v.label)
    //   if(v.dicUrl){
    //     tranKey.push(v.prop)
    //     tranDic[v.prop] = v.dicUrl
    //   }
    // }
  });
  return {key: key, title: title};
}


// 导出不带有汉字标题的execel内容
export const export_array_to_excel = ({
                                        key,
                                        data,
                                        title,
                                        filename,
                                        autoWidth
                                      }) => {
  const wb = XLSX.utils.book_new();
  const arr = json_to_arrayWithScene(key, data);
  arr.unshift(title)
  const ws = XLSX.utils.aoa_to_sheet(arr);
  if (autoWidth) {
    auto_width(ws, arr);
  }
  XLSX.utils.book_append_sheet(wb, ws, filename);
  XLSX.writeFile(wb, filename + '.xlsx');
}

function json_to_arrayWithScene(key, jsonData) {
  //console.log(tranDic)
  return jsonData.map(v => key.map(j => {
    if (contains(tranKey, j)) {//需翻译
      var dics = tranDic[j];
      if (dics) {
        for (var i = 0; i < dics.length; i++) {
          if (dics[i].value == v[j]) {
            return dics[i].label;
          }
        }
        return v[j]
      } else {
        return v[j]
      }
    } else {
      return v[j]
    }

  }));
}

export function contains(arr, val) {
  for (var i = 0; i < arr.length; i++) {
    if (arr[i] == val) {
      return true;
    }
  }
  return false;
}

export function translate(url) {
  return request({
    url: url,
    method: 'get'
  })
}

// 导出带有汉字标题的execel内容
export const export_array_to_excel2 = ({
                                         key,
                                         data,
                                         title,
                                         filename,
                                         autoWidth
                                       }) => {
  const wb = XLSX.utils.book_new();
  const arr = json_to_array(key, data);
  arr.unshift(key)
  arr.unshift(title)
  const ws = XLSX.utils.aoa_to_sheet(arr);
  if (autoWidth) {
    auto_width(ws, arr);
  }
  XLSX.utils.book_append_sheet(wb, ws, filename);
  XLSX.writeFile(wb, filename + '.xlsx');
}


export const analyseData = (datas,num) => {
  var reDatas = []
  for(var i = 0 ; i < datas.length ; i++){
    var temp = ''
    for(var m = 0 ; m < num ; m++){
      temp += '  '
    }
    datas[i].CNNAME = temp+datas[i].CNNAME
    reDatas.push(datas[i])
    reDatas = reDatas.concat(findChildren(datas[i],num))
  }

  return reDatas
}

export const findChildren = (data,num) => {
  var reDatas = []
  if(data.children && data.children.length>0){
    reDatas = analyseData(data.children,num+1)
  }
  return reDatas
}



export const read = (data, type) => {
  /* if type == 'base64' must fix data first */
  // const fixedData = fixdata(data)
  // const workbook = XLSX.read(btoa(fixedData), { type: 'base64' })
  const workbook = XLSX.read(data, {
    type: type
  });
  const firstSheetName = workbook.SheetNames[0];
  const worksheet = workbook.Sheets[firstSheetName];
  const header = get_header_row(worksheet);
  const results = XLSX.utils.sheet_to_json(worksheet);
  return {
    header,
    results
  };
}

export const readesxle = async (file, header, jsointitle) => {
  return new Promise(function (resolve, reject) {
    const resultdata = {
      ErrCode: "9",
      ErrText: '导入文件格式不正确。',
      Rows: []
    }
    const fileExt = file.name.split('.').pop().toLocaleLowerCase()
    if (fileExt === 'xlsx' || fileExt === 'xls') {
      const reader = new FileReader();

      const thisXLSX = XLSX;
      const thisheader = header;
      const thisjsointitle = jsointitle;
      reader.readAsArrayBuffer(file)
      reader.onloadstart = e => {
      }
      // reader.onprogress = e => {
      //   this.progressPercent = Math.round(e.loaded / e.total * 100)
      // }
      reader.onerror = e => {
        resultdata.ErrText = '文件读取出错';
        resultdata.ErrCode = "1";
        resolve(resultdata);
      }
      reader.onload = e => {
        const data = e.target.result
        const
          workbook = thisXLSX.read(data, {
            type: "array"
          });
        let tempFlag = true;

        const firstSheetName = workbook.SheetNames[0];
        const worksheet = workbook.Sheets[firstSheetName];
        const sheetsheader = get_header_row(worksheet);
        const sheetarray = thisXLSX.utils.sheet_to_json(worksheet);

        thisheader.forEach((item, index) => {
          if (sheetsheader.findIndex(x => x == item) == -1) {
            tempFlag = false
          }
        });
        if (tempFlag) {
          let sheetresult = [];
          for (let i = 0; i < sheetarray.length; i++) {
            sheetresult.push({});
            for (let j = 0; j < thisheader.length; j++) {
              if (sheetarray[i][thisheader[j]] == undefined || sheetarray[i][thisheader[j]] == null)
                sheetresult[i][thisjsointitle[j]] = "";
              else
                sheetresult[i][thisjsointitle[j]] = sheetarray[i][thisheader[j]];
            }
          }
          resultdata.ErrCode = "0";
          resultdata.EErrText = "文件导入成功";
          resultdata.Rows = sheetresult;
        } else {
          resultdata.ErrCode = "1";
          resultdata.EErrText = "导入文件格式不正确。";
          resultdata.Rows = [];
        }
        resolve(resultdata);
      }
    } else {
      resultdata.ErrCode = "1";
      resultdata.ErrText = '文件：' + file.name + '不是EXCEL文件，请选择后缀为.xlsx或者.xls的EXCEL文件。';
      resolve(resultdata);
    }
  })
}


//var XLSX = require('xlsx');

export const exportXls = function (data) {
  var ws = {
    s: {
      "!row": [{wpx: 67}]
    }
  };
  ws['!cols'] = [];
  for (var n = 0; n != data[0].length; ++n) {
    ws['!cols'].push({
      wpx: 170
    });
  }
  var range = {
    s: {
      c: 10000000,
      r: 10000000,
    },
    e: {
      c: 0,
      r: 0
    }
  };
  for (var R = 0; R != data.length; ++R) {
    for (var C = 0; C != data[R].length; ++C) {
      if (range.s.r > R)
        range.s.r = R;
      if (range.s.c > C)
        range.s.c = C;
      if (range.e.r < R)
        range.e.r = R;
      if (range.e.c < C)
        range.e.c = C;
      var cell = {
        v: data[R][C],
        s: {
          fill: {fgColor: {rgb: "33000000"}},
          alignment: {horizontal: "center", vertical: "center"},
        }
      };
      if (cell.v == null)
        continue;
      var cell_ref = XLSX.utils.encode_cell({
        c: C,
        r: R
      });

      if (typeof cell.v === 'number')
        cell.t = 'n';
      else if (typeof cell.v === 'boolean')
        cell.t = 'b';
      else if (cell.v instanceof Date) {
        cell.t = 'n';
        cell.z = XLSX.SSF._table[14];
        cell.v = datenum(cell.v);
      } else
        cell.t = 's';
      if (R) {
        delete cell.s.fill;
      }
      ws[cell_ref] = cell;
    }
  }
  data.fileName = "sample.xlsx";
  var workbook = new Workbook();
  var wsName = data.fileName.split(".xlsx")[0];
  workbook.SheetNames.push(wsName);
  workbook.Sheets[wsName] = ws;
  if (range.s.c < 10000000)
    ws['!ref'] = XLSX.utils.encode_range(range);
  var wopts = {
    bookType: 'xlsx',
    bookSST: false,
    type: 'binary'
  };
  var wbout = XLSX.write(workbook, wopts);
  XLSX.writeFile(workbook, data.fileName);
  return wbout;
};

function Workbook() {
  if (!(this instanceof Workbook))
    return new Workbook();
  this.SheetNames = [];
  this.Sheets = {};
}


export default {
  export_table_to_excel,
  export_array_to_excel,
  export_json_to_excel,
  export_array_to_excel2,
  read,
  readesxle,
  excelExport,
  analyseOption,
  exportXls
}
