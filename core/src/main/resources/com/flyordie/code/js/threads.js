var VT_SUSPEND = {};

function vt_suspend(fiber) {
    // TODO mi van, ha ez az eventloop szál?
    throw VT_SUSPEND;
}
function vt_continue(fiber) {
    try {
        var generatedRootResumerFunction = eval(vt_generateResumer(fiber, vt_fiberMain.toString()));
        generatedRootResumerFunction();
    } catch (e) {
        if (e != VT_SUSPEND)
            throw e;
    }
}
function vt_scheduleContinuation() {
    setTimeout(vt_runNext, 0);
}
function vt_push(/* function, args... */) {
    throw "TODO";
}
function vt_pop() {
    throw "TODO";
}
function vt_preemptPoint() {}
