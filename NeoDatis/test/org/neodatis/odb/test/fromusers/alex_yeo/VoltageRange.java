package org.neodatis.odb.test.fromusers.alex_yeo;
import java.io.Serializable;


/**
 * DOCUMENT ME!
 *
 * @author   $author$
 * @version  $Revision: 1.1 $
 */
public class VoltageRange implements Serializable
{
    /** Generated */
    private static final long serialVersionUID = 7912157083735488705L;

    /** DOCUMENT ME! */
    private float lowerLimit;

    /** DOCUMENT ME! */
    private float upperLimit;

    /**
     * Copy Constructor
     *
     * @param  voltageRange  a <code>VoltageRange</code> object
     */
    public VoltageRange(VoltageRange voltageRange)
    {
        this.lowerLimit = voltageRange.lowerLimit;
        this.upperLimit = voltageRange.upperLimit;
    }

    /**
     * Creates a new VoltageRange object.
     *
     * @param  lowerLimit  DOCUMENT ME!
     * @param  upperLimit  DOCUMENT ME!
     */
    public VoltageRange(float lowerLimit, float upperLimit)
    {
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   obj  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null)
        {
            return false;
        }

        if (getClass() != obj.getClass())
        {
            return false;
        }

        VoltageRange other = (VoltageRange) obj;

        if (
            Float.floatToIntBits(lowerLimit) !=
              Float.floatToIntBits(other.lowerLimit)
        )
        {
            return false;
        }

        if (
            Float.floatToIntBits(upperLimit) !=
              Float.floatToIntBits(other.upperLimit)
        )
        {
            return false;
        }

        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDescriptor()
    {
        return String.format(
                "Voltage range: low-%.1f and high-%.1f",
                lowerLimit,
                upperLimit
                );
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public float getLowerLimit()
    {
        return lowerLimit;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public float getUpperLimit()
    {
        return upperLimit;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + Float.floatToIntBits(lowerLimit);
        result = (prime * result) + Float.floatToIntBits(upperLimit);

        return result;
    }

    /**
     * Constructs a <code>String</code> with all attributes in name = value
     * format.
     *
     * @return  a <code>String</code> representation of this object.
     */
    public String toString()
    {
        final String TAB = "    ";

        StringBuilder retValue = new StringBuilder();

        retValue.append("VoltageRange ( ")
                .append(super.toString())
                .append(TAB)
                .append("lowerLimit = ")
                .append(this.lowerLimit)
                .append(TAB)
                .append("upperLimit = ")
                .append(this.upperLimit)
                .append(TAB)
                .append(" )");

        return retValue.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @author   $author$
     * @version  $Revision: 1.1 $
     */
    // ESCA-JAVA0076:these magic numbers delimit the voltage ranges.
    public static class Factory
    {
        /**
         * Creates a new Factory object.
         */
        private Factory()
        {
            //do nothing
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public static VoltageRange createBipolar10Volts()
        {
            return new VoltageRange(-10, 10);
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public static VoltageRange createBipolar30Volts()
        {
            return new VoltageRange(-30, 30);
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public static VoltageRange createDefaultRange()
        {
            return new VoltageRange(0, 0);
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public static VoltageRange createUnipolar10Volts()
        {
            return new VoltageRange(0, 10);
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public static VoltageRange createUnipolar30Volts()
        {
            return new VoltageRange(0, 30);
        }
    }
}
