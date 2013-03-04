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

public class GET_DIR_DELEGATION4args implements XdrAble {
    public boolean gdda_signal_deleg_avail;
    public bitmap4 gdda_notification_types;
    public attr_notice4 gdda_child_attr_delay;
    public attr_notice4 gdda_dir_attr_delay;
    public bitmap4 gdda_child_attributes;
    public bitmap4 gdda_dir_attributes;

    public GET_DIR_DELEGATION4args() {
    }

    public GET_DIR_DELEGATION4args(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeBoolean(gdda_signal_deleg_avail);
        gdda_notification_types.xdrEncode(xdr);
        gdda_child_attr_delay.xdrEncode(xdr);
        gdda_dir_attr_delay.xdrEncode(xdr);
        gdda_child_attributes.xdrEncode(xdr);
        gdda_dir_attributes.xdrEncode(xdr);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        gdda_signal_deleg_avail = xdr.xdrDecodeBoolean();
        gdda_notification_types = new bitmap4(xdr);
        gdda_child_attr_delay = new attr_notice4(xdr);
        gdda_dir_attr_delay = new attr_notice4(xdr);
        gdda_child_attributes = new bitmap4(xdr);
        gdda_dir_attributes = new bitmap4(xdr);
    }

}
// End of GET_DIR_DELEGATION4args.java
