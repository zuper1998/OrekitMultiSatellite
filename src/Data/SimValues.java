package Data;

import com.company.QBERCalc.QuantumBitTransmitanceCalculator;

public class SimValues {
    public static final double stepT = 10;
    public static final double duration = 3600 * 24;
    public static final double MAX_TIME = 3600;
    public static final double MIN_WINDOW = 50;
    public static final QuantumBitTransmitanceCalculator calc = new QuantumBitTransmitanceCalculator();
    public static final int SearchDepth = 5;
    public static final String satData = "src/Data/QSAT_MAX.txt";
}
