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

public class open_none_delegation4 implements XdrAble {
    public int ond_why;
    public boolean ond_server_will_push_deleg;
    public boolean ond_server_will_signal_avail;

    public open_none_delegation4() {
    }

    public open_none_delegation4(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeInt(ond_why);
        switch ( ond_why ) {
        case why_no_delegation4.WND4_CONTENTION:
            xdr.xdrEncodeBoolean(ond_server_will_push_deleg);
            break;
        case why_no_delegation4.WND4_RESOURCE:
            xdr.xdrEncodeBoolean(ond_server_will_signal_avail);
            break;
        default:
            break;
        }
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        ond_why = xdr.xdrDecodeInt();
        switch ( ond_why ) {
        case why_no_delegation4.WND4_CONTENTION:
            ond_server_will_push_deleg = xdr.xdrDecodeBoolean();
            break;
        case why_no_delegation4.WND4_RESOURCE:
            ond_server_will_signal_avail = xdr.xdrDecodeBoolean();
            break;
        default:
            break;
        }
    }

}
// End of open_none_delegation4.java
