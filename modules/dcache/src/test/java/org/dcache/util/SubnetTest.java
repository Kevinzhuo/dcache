package org.dcache.util;

import org.junit.Test;
import org.springframework.util.SerializationUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.*;

public class SubnetTest {

    @Test
    public void testCreateWithInetAddressAndMask() throws UnknownHostException {
        Subnet subnet = Subnet.create(InetAddress.getByName("192.168.0.0"), 24);
        assertTrue(subnet.containsHost("192.168.0.1"));
        assertTrue(subnet.contains(InetAddress.getByName("192.168.0.1")));
        assertTrue(subnet.containsAny(new InetAddress[] {InetAddress.getByName("134.102.101.100"), InetAddress.getByName("192.168.0.1"), InetAddress.getByName("cern.ch")}));
        assertFalse(subnet.containsHost("192.168.1.1"));
        assertFalse(subnet.contains(InetAddress.getByName("192.168.1.1")));
        assertFalse(subnet.containsAny(new InetAddress[] {InetAddress.getByName("134.102.101.100"), InetAddress.getByName("cern.ch")}));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCreateStringWithInvalidCidrPattern() {
        Subnet.create("something");
    }

    @Test
    public void testCreateStringWithValidCidrPatternIPv4() throws UnknownHostException {
        Subnet subnet = Subnet.create("192.168.0.0/24");
        assertTrue(subnet.containsHost("192.168.0.1"));
        assertTrue(subnet.contains(InetAddress.getByName("192.168.0.1")));
        assertTrue(subnet.containsAny(new InetAddress[] {InetAddress.getByName("134.102.101.100"), InetAddress.getByName("192.168.0.1"), InetAddress.getByName("cern.ch")}));
        assertFalse(subnet.containsHost("192.168.1.1"));
        assertFalse(subnet.contains(InetAddress.getByName("192.168.1.1")));
        assertFalse(subnet.containsAny(new InetAddress[] {InetAddress.getByName("134.102.101.100"), InetAddress.getByName("cern.ch")}));
    }

    @Test
    public void testCreate() throws UnknownHostException {
        Subnet subnet = Subnet.create();
        assertTrue(subnet.containsHost("192.168.0.1"));
        assertTrue(subnet.contains(InetAddress.getByName("192.168.0.1")));
        assertTrue(subnet.containsAny(new InetAddress[] {InetAddress.getByName("134.102.101.100"), InetAddress.getByName("192.168.0.1"), InetAddress.getByName("cern.ch")}));
        assertTrue(subnet.containsHost("192.168.1.1"));
        assertTrue(subnet.contains(InetAddress.getByName("192.168.1.1")));
        assertTrue(subnet.containsAny(new InetAddress[] {InetAddress.getByName("134.102.101.100"), InetAddress.getByName("cern.ch")}));
    }

    @Test(expected=NumberFormatException.class)
    public void testCidrWithInvalidMask() {
        Subnet.create("1.2.3.4/abc");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCidrWithInvalidAddress() {
        Subnet.create("foobar/22");
    }

    @Test
    public void testToString() {
        Subnet subnet = Subnet.create();
        assertEquals("all", subnet.toString());

        subnet = Subnet.create("::/0");
        assertEquals("::/0", subnet.toString());

        subnet = Subnet.create("0.0.0.0/0");
        assertEquals("0.0.0.0/0", subnet.toString());

        subnet = Subnet.create("192.168.0.0/24");
        assertEquals("192.168.0.0/24", subnet.toString());

        subnet = Subnet.create("192.168.0.0");
        assertEquals("192.168.0.0/32", subnet.toString());

        subnet = Subnet.create("192.168.0.0/32");
        assertEquals("192.168.0.0/32", subnet.toString());

        subnet = Subnet.create("192.168.0.0/255.255.255.255");
        assertEquals("192.168.0.0/32", subnet.toString());

        subnet = Subnet.create("::1");
        assertEquals("::1/128", subnet.toString());

        subnet = Subnet.create("2000::00ff:ff00");
        assertEquals("2000::ff:ff00/128", subnet.toString());

        // special addresses for documentation starting with 2001:
        // are mapped to IPv4 and inverted:
        subnet = Subnet.create("2001::00ff:ff00");
        assertEquals("255.0.0.255/32", subnet.toString());

        // special addresses starting with 2002 are mapped to IPv4
        // using bytes 2 to 5 (starting counting at 0):
        subnet = Subnet.create("2002:0102:0304::00ff:ff00");
        assertEquals("1.2.3.4/32", subnet.toString());

        // special addresses starting with ::ffff: are mapped to IPv4
        // using bytes 12 to 16 (starting counting at 0):
        subnet = Subnet.create("::ffff:192.168.0.1");
        assertEquals("192.168.0.1/32", subnet.toString());

        subnet = Subnet.create("2003::00ff:ff00");
        assertEquals("2003::ff:ff00/128", subnet.toString());

        // embedded ipv4 addresses:
        subnet = Subnet.create("::ffff:192.168.0.0/120");
        assertEquals("192.168.0.0/24", subnet.toString());

        subnet = Subnet.create("::ffff:c0a8:0/120");
        assertEquals("192.168.0.0/24", subnet.toString());

        subnet = Subnet.create("::ffff:c0a8:1");
        assertEquals("192.168.0.1/32", subnet.toString());

        // compatible ipv4 addresses:
        subnet = Subnet.create("::192.168.0.0/120");
        assertEquals("192.168.0.0/24", subnet.toString());

        subnet = Subnet.create("::c0a8:0/120");
        assertEquals("192.168.0.0/24", subnet.toString());

        subnet = Subnet.create("::c0a8:1");
        assertEquals("192.168.0.1/32", subnet.toString());

        subnet = Subnet.create("1234:1234:1234:1234:1234:1234:1234:1234/96");
        assertEquals("1234:1234:1234:1234:1234:1234::/96", subnet.toString());

        subnet = Subnet.create("1234:1234:1234::1234:1234:1234/96");
        assertEquals("1234:1234:1234::1234:0:0/96", subnet.toString());

        subnet = Subnet.create("0:11:0:11::1");
        assertEquals("0:11:0:11::1/128", subnet.toString());

        subnet = Subnet.create("00ff:ff:00ff:00ff:00ff::1");
        assertEquals("ff:ff:ff:ff:ff::1/128", subnet.toString());

        subnet = Subnet.create("192.168.0.0/255.255.255.0");
        assertEquals("192.168.0.0/24", subnet.toString());

        // this tests still fails - will fix in non-bugfix-patch
        // subnet = Subnet.create("::ffff:192.168.0.0/128");
        // assertEquals("192.168.0.0/32", subnet.toString());
    }

    @Test
    public void serializeSubnetTest() {
        Subnet original = Subnet.create();
        Object copy = SerializationUtils.deserialize(
                SerializationUtils.serialize(original));

        assertTrue(copy instanceof Subnet);
        assertEquals(original, copy);
    }

    @Test
    public void testMatch() throws UnknownHostException {
        assertTrue(matches("0.0.0.0/0", "131.169.252.76"));
        assertTrue(matches("0.0.0.0/0.0.0.0", "131.169.252.76"));
        assertTrue(matches("128.0.0.0/1", "131.169.252.76"));
        assertTrue(matches("130.0.0.0/7", "131.169.252.76"));
        assertTrue(matches("131.0.0.0/8", "131.169.252.76"));
        assertTrue(matches("131.128.0.0/9", "131.169.252.76"));
        assertTrue(matches("131.168.0.0/15", "131.169.252.76"));
        assertTrue(matches("131.169.0.0/16", "131.169.252.76"));
        assertTrue(matches("131.169.128.0/17", "131.169.252.76"));
        assertTrue(matches("131.169.252.0/23", "131.169.252.76"));
        assertTrue(matches("131.169.252.0/24", "131.169.252.76"));
        assertTrue(matches("131.169.252.0/25", "131.169.252.76"));
        assertTrue(matches("131.169.252.76/31", "131.169.252.76"));
        assertTrue(matches("131.169.252.76/32", "131.169.252.76"));

        assertFalse(matches("128.0.0.0/1", "127.169.252.76"));
        assertFalse(matches("130.0.0.0/7", "1.169.253.76"));
        assertFalse(matches("131.0.0.0/8", "132.169.252.76"));
        assertFalse(matches("131.128.0.0/9", "131.127.128.76"));
        assertFalse(matches("131.168.0.0/15", "131.167.252.76"));
        assertFalse(matches("131.169.0.0/16", "131.168.252.76"));
        assertFalse(matches("131.169.128.0/17", "131.169.127.128"));
        assertFalse(matches("131.169.252.0/23", "131.169.251.76"));
        assertFalse(matches("131.169.252.0/24", "131.169.253.76"));
        assertFalse(matches("131.169.252.0/25", "131.169.252.128"));
        assertFalse(matches("131.169.252.76/31", "131.169.251.77"));
        assertFalse(matches("131.169.252.76/32", "131.169.252.75"));
    }

    private boolean matches(String subnetLabel, String ip)
    {
        Subnet subnet = Subnet.create(subnetLabel);
        InetAddress address;
        try {
            address = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return subnet.contains(address);
    }

}
