package org.neodatis.odb.test.fromusers.alex_yeo;
/**
 * Represents the actual device that the application will need access to in
 * order to create the requisite interface signal or data.
 *
 * @author   Alex Yeo
 * @version  0.1.0
 */
public interface IDevice<T extends Enum<?>>
{
    /**
     * Makes a copy of the current device object
     *
     * @return  DOCUMENT ME!
     */
    IDevice<?> copy();

    /**
     * Short description of the device
     *
     * @return  DOCUMENT ME!
     */
    String getDescriptor();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    T getType();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isConfigurable();

    /**
     * DOCUMENT ME!
     *
     * @param  device  DOCUMENT ME!
     */
    void update(IDevice<?> device);
}
