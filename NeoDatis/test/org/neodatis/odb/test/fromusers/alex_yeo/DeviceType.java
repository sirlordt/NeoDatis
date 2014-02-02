package org.neodatis.odb.test.fromusers.alex_yeo;
/**
 * DOCUMENT ME!
 *
 * @author   $author$
 * @version  $Revision: 1.1 $
 */
public enum DeviceType
{
    Moxa("MoXa Uport 1450"), McUsbDaq("Measurement Computing USB-3114"),
    Farsync("FarSync Flex USB Synchronous Adapter"), Ethernet("Ethernet");

    /** DOCUMENT ME! */
    private String descriptor;

    /**
     * Creates a new DeviceType object.
     *
     * @param  descriptor  DOCUMENT ME!
     */
    private DeviceType(String descriptor)
    {
        this.descriptor = descriptor;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDescriptor()
    {
        return descriptor;
    }
}
