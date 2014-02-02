package org.neodatis.odb.test.fromusers.alex_yeo;


/**
 * DOCUMENT ME!
 *
 * @author   $author$
 * @version  $Revision: 1.2 $
 */
public enum VoltageRangePolarity
{
    UNIPOLAR10(new VoltageRange(0, 10)), BIPOLAR10(new VoltageRange(-10, 10)),
    NULL(new VoltageRange(0, 0)), UNIPOLAR5(new VoltageRange(0, 5));

    /** DOCUMENT ME! */
    private VoltageRange range;

    /**
     * Creates a new VoltageRangePolarity object.
     *
     * @param  range  DOCUMENT ME!
     */
    private VoltageRangePolarity(VoltageRange range)
    {
        this.range = range;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public VoltageRange getRange()
    {
        return range;
    }
}
