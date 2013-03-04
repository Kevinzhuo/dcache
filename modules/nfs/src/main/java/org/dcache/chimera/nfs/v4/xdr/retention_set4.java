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

public class retention_set4 implements XdrAble {
    public boolean rs_enable;
    public uint64_t [] rs_duration;

    public retention_set4() {
    }

    public retention_set4(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeBoolean(rs_enable);
        { int $size = rs_duration.length; xdr.xdrEncodeInt($size); for ( int $idx = 0; $idx < $size; ++$idx ) { rs_duration[$idx].xdrEncode(xdr); } }
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        rs_enable = xdr.xdrDecodeBoolean();
        { int $size = xdr.xdrDecodeInt(); rs_duration = new uint64_t[$size]; for ( int $idx = 0; $idx < $size; ++$idx ) { rs_duration[$idx] = new uint64_t(xdr); } }
    }

}
// End of retention_set4.java
