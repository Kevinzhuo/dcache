/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v4.xdr;

import java.io.IOException;

import org.dcache.xdr.OncRpcException;
import org.dcache.xdr.XdrAble;
import org.dcache.xdr.XdrDecodingStream;
import org.dcache.xdr.XdrEncodingStream;

public class CB_WANTS_CANCELLED4res implements XdrAble {
    public int cwcr_status;

    public CB_WANTS_CANCELLED4res() {
    }

    public CB_WANTS_CANCELLED4res(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeInt(cwcr_status);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        cwcr_status = xdr.xdrDecodeInt();
    }

}
// End of CB_WANTS_CANCELLED4res.java
