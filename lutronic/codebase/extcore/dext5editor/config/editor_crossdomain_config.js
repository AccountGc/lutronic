/*
Copyright (c) 2013, Raonwiz Technology Inc. All rights reserved.
 - document domain config (cross domain)
*/
(function () {
    var logMode = false;
    try {
        var G_Document_Domain = null;
        if (typeof URLSearchParams == 'function') { var p = new URLSearchParams(document.location.search); G_Document_Domain = p.get('d'); }
        else { var _href = document.location.href; var reg = new RegExp('[?&]d=([^&#]*)', ''); var matches = reg.exec(_href); G_Document_Domain = matches ? matches[1] : null; }
        if (typeof G_Document_Domain == 'string') { document.domain = G_Document_Domain; }
    } catch (e) {
        logMode && window && window.console && console.log(e);
    }
})();

//var dext5EnforceDocumentDomain = true;
//document.domain = "dext5.com";