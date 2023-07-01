
function addWatcher(tgtObjArr) {
  var objArr = null;
  for(var inxA=0; tgtObjArr && inxA<tgtObjArr.length; inxA++) {
    objArr = tgtObjArr[inxA][0].split(":");

    for(var inxB=0; objArr && inxB<objArr.length; inxB++) {
      if($(objArr[inxB]) && tgtObjArr[inxA][1].trim()) {
        new ObjWatcher(objArr[inxB], tgtObjArr[inxA][1].trim().split(":"));
      }
    }
  }
}


var ObjWatcher = Class.create();

ObjWatcher.prototype = {
  initialize: function(txtBox, attArr) {
    this.txtBox = $(txtBox);

    for(var inxZ=0; attArr && inxZ<attArr.length; inxZ++) {
      switch(attArr[inxZ]) {
        case 'blurfill0' :
          this.txtBox.onblur = this.blurfill0.bindAsEventListener(this);
          break;

        case 'blurfillfloat' :
            this.txtBox.onblur = this.blurfillfloat.bindAsEventListener(this);
            break;

        case 'bluruppercase' :
          this.txtBox.onblur = this.bluruppercase.bindAsEventListener(this);
          break;

        case 'disablepaste_m' :
          this.txtBox.onpaste = this.disablepaste_m.bindAsEventListener(this);
          break;

        case 'disablepaste_k' :
          this.txtBox.onkeydown = this.disablepaste_k.bindAsEventListener(this);
          break;

        case 'numeric' :
          this.txtBox.setStyle({'imeMode':'disabled'});
          this.txtBox.onkeypress = this.numeric.bindAsEventListener(this);
          break;

        case 'numericbar' :
          this.txtBox.setStyle({'imeMode':'disabled'});
          this.txtBox.onkeypress = this.numericbar.bindAsEventListener(this);
          break;

        case 'numericdot' :
            this.txtBox.setStyle({'imeMode':'disabled'});
            this.txtBox.setStyle({'textAlign':'right'});
            this.txtBox.onkeypress = this.numericdot.bindAsEventListener(this);
            break;

        case 'numeric_alpha' :
          this.txtBox.setStyle({'imeMode':'disabled'});
          this.txtBox.onkeypress = this.numeric_alpha.bindAsEventListener(this);
          break;

        case 'except_specchar' :
          this.txtBox.onkeypress = this.except_specchar.bindAsEventListener(this);
          break;

        case 'imeEng' :
          if(Prototype.Browser.IE) {
            this.txtBox.setStyle({'imeMode':'inactive'});
          }
          break;

        case 'imeEngOnly' :
          this.txtBox.setStyle({'imeMode':'disabled'});
          break;

        case 'imeKor' :
          if(Prototype.Browser.IE) {
            this.txtBox.setStyle({'imeMode':'active'});
          }
          break;

        case 'dateform' :
          this.txtBox.setStyle({'imeMode':'disabled'});
          this.txtBox.onfocus = this.focusdateform.bindAsEventListener(this);
          this.txtBox.onkeypress = this.numeric.bindAsEventListener(this);
          this.txtBox.onblur = this.blurdateform.bindAsEventListener(this);
          break;
      }
    }
  },

  blurfill0: function(evt) {
    var tmpBoxValue = this.txtBox.value.trim();

    if(tmpBoxValue.length && this.txtBox.maxLength && tmpBoxValue.length < this.txtBox.maxLength) {
      for(var inxA=this.txtBox.maxLength-tmpBoxValue.length; inxA > 0; inxA--) {
        tmpBoxValue = '0' + tmpBoxValue;
      }
      this.txtBox.value=tmpBoxValue;
    }
  },

  blurfillfloat: function(evt) {
    var tmpBoxValue = this.txtBox.value.trim();
    var rsltValue = "";
    if(tmpBoxValue.length) {
        var dotSplit = tmpBoxValue.split(".");
        if(tmpBoxValue.indexOf(".") > -1) {
            if(tmpBoxValue.indexOf(".") == 0) {
                rsltValue = "0" + tmpBoxValue;
            } else if(tmpBoxValue.indexOf(".") == tmpBoxValue.length-1) {
                rsltValue = tmpBoxValue + "0";
            }else {
                rsltValue = tmpBoxValue;
            }
        } else {
            rsltValue = tmpBoxValue + ".0";
        }
    }

    if((parseFloat(rsltValue)+"" == "NaN")) {
        this.txtBox.value="";
    } else {
        this.txtBox.value=rsltValue;
    }
  },

  bluruppercase: function(evt) {
    var tmpBoxValue = this.txtBox.value.trim();
    this.txtBox.value=tmpBoxValue.toUpperCase();
  },

  disablepaste_m: function(evt) {
    if(Prototype.Browser.IE) {
      alert("붙여넣기는 지원되지 않습니다.");
        evt.returnValue=false;
    } else {
        evt.preventDefault();
    }
  },

  disablepaste_k: function(evt) {
    if(evt.keyCode == 17) {
       alert("붙여넣기는 지원되지 않습니다.");
       if(Prototype.Browser.IE) {
         evt.returnValue=false;
       } else {
            evt.preventDefault();
       }
    }
  },

  numeric: function(evt) {
    if(Prototype.Browser.IE) {
      if(evt.keyCode >= 48 && evt.keyCode <= 57) {
         return;
      } else {
         evt.returnValue=false;
      }
    } else {
      if((evt.charCode >= 48 && evt.charCode <= 57) ||
         evt.charCode == 0) {
         return;
      } else {
            evt.preventDefault();
      }
    }
  },

  numericbar: function(evt) {
    if(Prototype.Browser.IE) {
      if((evt.keyCode >= 48 && evt.keyCode <= 57) ||
         evt.keyCode == 45) {
         return;
      } else {
         evt.returnValue=false;
      }
    } else {
      if((evt.charCode >= 48 && evt.charCode <= 57) ||
         evt.charCode == 45 ||
         evt.charCode == 0) {
         return;
      } else {
            evt.preventDefault();
      }
    }
  },

  numericdot: function(evt) {
    if(Prototype.Browser.IE) {
      if((evt.keyCode >= 48 && evt.keyCode <= 57) ||
         evt.keyCode == 46) {
         return;
      } else {
         evt.returnValue=false;
      }
    } else {
      if((evt.charCode >= 48 && evt.charCode <= 57) ||
         evt.charCode == 46 ||
         evt.charCode == 0) {
         return;
      } else {
            evt.preventDefault();
      }
    }
  },

  numeric_alpha: function(evt) {
    if(Prototype.Browser.IE) {
      if((evt.keyCode >= 48 && evt.keyCode <= 57) ||
         (evt.keyCode >= 65 && evt.keyCode <= 90) ||
         (evt.keyCode >= 97 && evt.keyCode <= 122)) {
         return;
      } else {
         evt.returnValue=false;
      }
    } else {
      if((evt.charCode >= 48 && evt.charCode <= 57) ||
         (evt.charCode >= 65 && evt.charCode <= 90) ||
         (evt.charCode >= 97 && evt.charCode <= 122) ||
         evt.charCode == 0) {
         return;
      } else {
            evt.preventDefault();
      }
    }
  },

  except_specchar: function(evt) {
    if(Prototype.Browser.IE) {
      if((evt.keyCode > 32 && evt.keyCode < 48) ||
         (evt.keyCode > 57 && evt.keyCode < 65) ||
         (evt.keyCode > 90 && evt.keyCode < 97)) {
          evt.returnValue=false;
      } else {
          return;
      }
    } else {
      if((evt.charCode > 32 && evt.charCode < 48) ||
         (evt.charCode > 57 && evt.charCode < 65) ||
         (evt.charCode > 90 && evt.charCode < 97)) {
          evt.preventDefault();
      } else {
          return;
      }
    }
  },


  focusdateform: function(evt) {
    this.txtBox.maxLength = 8;

    if(this.txtBox.value.length > 8) {
      this.txtBox.value = this.txtBox.value.split("/").join("");
    }
    this.txtBox.select();
  },

  blurdateform:function(evt) {
    this.txtBox.maxLength = 10;

    if(this.txtBox.value.length == 8) {
      var tmpDateArr = [this.txtBox.value.substring(0, 4), this.txtBox.value.substring(4, 6), this.txtBox.value.substring(6)];
      var tmpDate = new Date(tmpDateArr[0], parseInt(tmpDateArr[1]-1)+"", tmpDateArr[2]);

      if((tmpDate.getFullYear()+"" ==  parseInt(tmpDateArr[0])+"") &&
         (tmpDate.getMonth()+""    == (parseInt(tmpDateArr[1].charAt(0)=="0" ? tmpDateArr[1].charAt(1) : tmpDateArr[1])-1)+"") &&
         (tmpDate.getDate()+""     == parseInt(tmpDateArr[2].charAt(0)=="0" ? tmpDateArr[2].charAt(1) : tmpDateArr[2])+"" )) {
        this.txtBox.value = [this.txtBox.value.substring(0, 4),
                             this.txtBox.value.substring(4, 6),
                             this.txtBox.value.substring(6)].join("/");
      } else {
        this.txtBox.value = "";
      }
    } else {
      this.txtBox.value = "";
    }
  }
};


