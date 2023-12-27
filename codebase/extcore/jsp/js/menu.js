
var flag = false;

function setPosition(v){
	flag = false;
	if(document.all.position==null)return;
	document.all.position.innerHTML = getLocation(this, document.all.myname.value , v);
}

function getLocation(doc,uloc,loc){
	if(loc==null || loc.length==0)
		loc = uloc;
	else
		loc = "<font  face='Tohoma' color=aaaaaa>"+ uloc + " >&nbsp;" + loc;

	if(doc.parent!=null && doc.parent.document.all.myname!=null){
		loc = getLocation(doc.parent,doc.parent.document.all.myname.value,loc);
	}
	else if(doc.parent.menu!=null && doc.parent.menu.document.all.myname!=null){
		loc = getLocation(doc.parent,doc.parent.menu.document.all.myname.value , loc);
	}
	else if(!flag && doc.parent.etop!=null && doc.parent.etop.document.all.myname!=null){
		flag=true;
		loc = getLocation(doc.parent, doc.parent.etop.document.all.myname.value , loc);
		
	}
	return loc
}