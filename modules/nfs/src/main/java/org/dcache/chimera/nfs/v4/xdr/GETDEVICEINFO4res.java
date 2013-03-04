/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v4.xdr;

import java.io.IOException;

import org.dcache.chimera.nfs.nfsstat;
import org.dcache.xdr.OncRpcException;
import org.dcache.xdr.XdrAble;
import org.dcache.xdr.XdrDecodingStream;
import org.dcache.xdr.XdrEncodingStream;

public class GETDEVICEINFO4res implements XdrAble {
    public int gdir_status;
    public GETDEVICEINFO4resok gdir_resok4;
    public count4 gdir_mincount;

    public GETDEVICEINFO4res() {
    }

    public GETDEVICEINFO4res(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeInt(gdir_status);
        switch ( gdir_status ) {
        case nfsstat.NFS_OK:
            gdir_resok4.xdrEncode(xdr);
            break;
        case nfsstat.NFSERR_TOOSMALL:
            gdir_mincount.xdrEncode(xdr);
            break;
        default:
            break;
        }
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        gdir_status = xdr.xdrDecodeInt();
        switch ( gdir_status ) {
        case nfsstat.NFS_OK:
            gdir_resok4 = new GETDEVICEINFO4resok(xdr);
            break;
        case nfsstat.NFSERR_TOOSMALL:
            gdir_mincount = new count4(xdr);
            break;
        default:
            break;
        }
    }

}
// End of GETDEVICEINFO4res.java
