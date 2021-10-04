package Data;

import com.company.City;
import com.company.CityLoader;
import com.company.QBERCalc.QuantumBitTransmitanceCalculator;

import java.util.ArrayList;

public class SimValues {
    public static final double stepT = 1;
    public static final double duration = 3600 *24 ;
    public static final double MAX_TIME = 3600;
    public static final double MIN_WINDOW = 50;
    public static final double entanglementGenHz = 52.36*1000; //https://www.nature.com/articles/s41534-021-00462-7
    public static final double frequencyBinEntanglementFidelity = 0.9574;
    public static final int SearchDepth = 5;
    public static final String satData = "src/Data/QSAT_MIN.txt";
    public static final String cityData = "src/Data/cities.txt";
    public static final int concurentThreads = 10;
    public static ThreadLocal<QuantumBitTransmitanceCalculator> calc = new ThreadLocal<>();
    public static final ArrayList<City> cities = new ArrayList<>(CityLoader.loadCities(SimValues.cityData));
}
