package org.neodatis.odb.test.fromusers.alex_yeo;


/**
 * DOCUMENT ME!
 *
 * @author   $author$
 * @version  $Revision: 1.1 $
 */
public class McUsbDaqDevice implements IDevice<DeviceType>
{

    /** DOCUMENT ME! */
    private int boardIndex;

    /** DOCUMENT ME! */
    private McUsbDaqInput input;

    /** DOCUMENT ME! */
    private VoltageRangePolarity rangePolarity;

    /**
     * Creates a new McUsbDaq0Device object.
     */
    public McUsbDaqDevice()
    {
        this.input = McUsbDaqInput.NULL;
        this.rangePolarity = VoltageRangePolarity.NULL;
    }

    /**
     * Creates a new McUsbDaqDevice object.
     *
     * @param  boardIndex     DOCUMENT ME!
     * @param  input          DOCUMENT ME!
     * @param  rangePolarity  DOCUMENT ME!
     */
    public McUsbDaqDevice(
        int boardIndex,
        McUsbDaqInput input,
        VoltageRangePolarity rangePolarity
        )
    {
        this.boardIndex = boardIndex;
        this.input = input;
        this.rangePolarity = rangePolarity;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public IDevice<?> copy()
    {
        McUsbDaqDevice copy =
            new McUsbDaqDevice(boardIndex, input, rangePolarity);

        return copy;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   obj  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
     public boolean equals(Object obj)
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

        McUsbDaqDevice other = (McUsbDaqDevice) obj;

        if (boardIndex != other.boardIndex)
        {
            return false;
        }

        if (input == null)
        {
            if (other.input != null)
            {
                return false;
            }
        }
        else if (!input.equals(other.input))
        {
            return false;
        }

        if (rangePolarity == null)
        {
            if (other.rangePolarity != null)
            {
                return false;
            }
        }
        else if (!rangePolarity.equals(other.rangePolarity))
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
    public int getBoardIndex()
    {
        return boardIndex;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
     public String getDescriptor()
    {
        return String.format(
                "USB-3114#0 Config: Board#%d, Input is %s with %s",
                boardIndex,
                input,
                rangePolarity.getRange().getDescriptor()
                );
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public McUsbDaqInput getInput()
    {
        return input;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public VoltageRangePolarity getRangePolarity()
    {
        return rangePolarity;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
     public DeviceType getType()
    {
        return DeviceType.McUsbDaq;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
     public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + boardIndex;
        result = (prime * result) + ((input == null) ? 0 : input.hashCode());
        result =
            (prime * result) +
            ((rangePolarity == null) ? 0 : rangePolarity.hashCode());

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
     public boolean isConfigurable()
    {
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  boardIndex  DOCUMENT ME!
     */
    public void setBoardIndex(int boardIndex)
    {
        this.boardIndex = boardIndex;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  input  DOCUMENT ME!
     */
    public void setInput(McUsbDaqInput input)
    {
        this.input = input;

        if (input.toString().startsWith("V"))
        {
            rangePolarity = VoltageRangePolarity.UNIPOLAR10;
        }
        else if (input.toString().startsWith("D"))
        {
            rangePolarity = VoltageRangePolarity.UNIPOLAR5;
        }
        else
        {
            rangePolarity = VoltageRangePolarity.NULL;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rangePolarity  DOCUMENT ME!
     */
    public void setRangePolarity(VoltageRangePolarity rangePolarity)
    {
        this.rangePolarity = rangePolarity;
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

        retValue.append("McUsbDaq0Device ( ")
                .append(super.toString())
                .append(TAB)
                .append("boardIndex = ")
                .append(this.boardIndex)
                .append(TAB)
                .append("input = ")
                .append(this.input)
                .append(TAB)
                .append("rangePolarity = ")
                .append(this.rangePolarity)
                .append(TAB)
                .append(" )");

        return retValue.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  device  DOCUMENT ME!
     */
     public void update(IDevice<?> device)
    {
        if (device.getType().equals(DeviceType.McUsbDaq))
        {
            McUsbDaqDevice usb = (McUsbDaqDevice) device;
            boardIndex = usb.getBoardIndex();
            input = usb.getInput();
            rangePolarity = usb.getRangePolarity();
        }
    }
}
