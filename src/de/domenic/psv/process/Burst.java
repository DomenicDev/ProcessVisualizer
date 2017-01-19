package de.domenic.psv.process;

/**
 * This class represents a Burst.
 *
 * Created by Domenic on 25.12.2016.
 */
public class Burst {

    private int length;
    private BurstType type;

    /**
     * All burst types defined
     */
    public enum BurstType {

        CPU, // CPU-Burst
        IO   // IO-Burst

    }

    /**
     * Create a new Burst with the given parameters.
     * @param type the burst type
     * @param length length of the burst
     */
    public Burst(BurstType type, int length) {
        this.type = type;
        this.length = length;
    }

    /**
     * @return the {@link BurstType} of this burst object
     */
    public BurstType getType() {
        return type;
    }

    /**
     * @return the length of the burst
     */
    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return getType().name() + "(" + getLength() + ")";
    }
}
