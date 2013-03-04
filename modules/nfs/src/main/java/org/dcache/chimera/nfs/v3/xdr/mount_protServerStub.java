/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v3.xdr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import org.dcache.xdr.OncRpcException;
import org.dcache.xdr.RpcCall;
import org.dcache.xdr.RpcDispatchable;
import org.dcache.xdr.XdrVoid;

/**
 */
public abstract class mount_protServerStub implements RpcDispatchable {

    private final static Logger _log = LoggerFactory.getLogger(mount_protServerStub.class);

    @Override
    public void dispatchOncRpcCall(RpcCall call)
           throws OncRpcException, IOException {

        int version = call.getProgramVersion();
        int procedure = call.getProcedure();

        if ( version == 3 ) {
            switch ( procedure ) {
            case 0: {
                _log.debug("MOUNTPROC3_NULL_3");
                call.retrieveCall(XdrVoid.XDR_VOID);
                MOUNTPROC3_NULL_3(call);
                call.reply( XdrVoid.XDR_VOID);
                break;
            }
            case 1: {
                _log.debug("MOUNTPROC3_MNT_3");
                dirpath args$ = new dirpath();
                call.retrieveCall(args$);
                mountres3 result$ = MOUNTPROC3_MNT_3(call, args$);
                call.reply( result$);
                break;
            }
            case 2: {
                call.retrieveCall(XdrVoid.XDR_VOID);
                mountlist result$ = MOUNTPROC3_DUMP_3(call);
                call.reply( result$);
                break;
            }
            case 3: {
                dirpath args$ = new dirpath();
                call.retrieveCall(args$);
                MOUNTPROC3_UMNT_3(call, args$);
                call.reply( XdrVoid.XDR_VOID);
                break;
            }
            case 4: {
                call.retrieveCall(XdrVoid.XDR_VOID);
                MOUNTPROC3_UMNTALL_3(call);
                call.reply( XdrVoid.XDR_VOID);
                break;
            }
            case 5: {
                call.retrieveCall(XdrVoid.XDR_VOID);
                exports result$ = MOUNTPROC3_EXPORT_3(call);
                call.reply( result$);
                break;
            }
            default:
                call.failProcedureUnavailable();
            }
        } else if ( version == 1 ) {
            switch ( procedure ) {
            case 0: {
                call.retrieveCall(XdrVoid.XDR_VOID);
                MOUNTPROC_NULL_1(call);
                call.reply( XdrVoid.XDR_VOID);
                break;
            }
            case 1: {
                dirpath args$ = new dirpath();
                call.retrieveCall(args$);
                fhstatus result$ = MOUNTPROC_MNT_1(call, args$);
                call.reply( result$);
                break;
            }
            case 2: {
                call.retrieveCall(XdrVoid.XDR_VOID);
                mountlist result$ = MOUNTPROC_DUMP_1(call);
                call.reply( result$);
                break;
            }
            case 3: {
                dirpath args$ = new dirpath();
                call.retrieveCall(args$);
                MOUNTPROC_UMNT_1(call, args$);
                call.reply( XdrVoid.XDR_VOID);
                break;
            }
            case 4: {
                call.retrieveCall(XdrVoid.XDR_VOID);
                MOUNTPROC_UMNTALL_1(call);
                call.reply( XdrVoid.XDR_VOID);
                break;
            }
            case 5: {
                _log.debug("processing message MOUNTPROC_EXPORT_1");
                call.retrieveCall(XdrVoid.XDR_VOID);
                exports result$ = MOUNTPROC_EXPORT_1(call);
                call.reply( result$);
                break;
            }
            case 6: {
                call.retrieveCall(XdrVoid.XDR_VOID);
                exports result$ = MOUNTPROC_EXPORTALL_1(call);
                call.reply( result$);
                break;
            }
            default:
                call.failProcedureUnavailable();
            }
        } else {
            call.failProgramMismatch(1,3);
        }
    }

    public abstract void MOUNTPROC3_NULL_3(RpcCall call$);

    public abstract mountres3 MOUNTPROC3_MNT_3(RpcCall call$, dirpath arg1);

    public abstract mountlist MOUNTPROC3_DUMP_3(RpcCall call$);

    public abstract void MOUNTPROC3_UMNT_3(RpcCall call$, dirpath arg1);

    public abstract void MOUNTPROC3_UMNTALL_3(RpcCall call$);

    public abstract exports MOUNTPROC3_EXPORT_3(RpcCall call$);

    public abstract void MOUNTPROC_NULL_1(RpcCall call$);

    public abstract fhstatus MOUNTPROC_MNT_1(RpcCall call$, dirpath arg1);

    public abstract mountlist MOUNTPROC_DUMP_1(RpcCall call$);

    public abstract void MOUNTPROC_UMNT_1(RpcCall call$, dirpath arg1);

    public abstract void MOUNTPROC_UMNTALL_1(RpcCall call$);

    public abstract exports MOUNTPROC_EXPORT_1(RpcCall call$);

    public abstract exports MOUNTPROC_EXPORTALL_1(RpcCall call$);

}
// End of mount_protServerStub.java
