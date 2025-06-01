package bgu.spl.mics.application.objects;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private static LiDarDataBase instance;
    private  final List<StampedCloudPoints> cloudPoints; // List of stamped cloud points

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    private LiDarDataBase(String filePath) {
        if(filePath == null || filePath.isEmpty())
            throw new IllegalArgumentException("File path cannot be null or empty");
        List<stambedCloudPointJSON> cloudPointJSON =loadData(filePath);
        this.cloudPoints = convertFromJSON(cloudPointJSON);
    }

    private List<StampedCloudPoints> convertFromJSON(List<stambedCloudPointJSON> cloudPointJSON) {
        List<StampedCloudPoints> listToRturn= new ArrayList<>();

        for(stambedCloudPointJSON jsonCloud:cloudPointJSON){
            List<CloudPoint> listCloudPoint = new ArrayList<>();
            for (List<Double> clodPoint : jsonCloud.getCloudPoints()){
                CloudPoint cloudToAdd =new CloudPoint(clodPoint.get(0),clodPoint.get(1));
                listCloudPoint.add(cloudToAdd);
            }

            StampedCloudPoints stambedToAdd =new StampedCloudPoints(jsonCloud.getId(),jsonCloud.getTime(),listCloudPoint);
            listToRturn.add(stambedToAdd);
        }


        return listToRturn;

    }

    public static LiDarDataBase getInstance(String filePath) {
        // TODO: Implement this
        if (instance == null) {
            instance = new LiDarDataBase(filePath);
        }

        return instance;
    }
    private List<stambedCloudPointJSON> loadData(String filePath) {
        try{
            Gson gson = new Gson();
            gson = new GsonBuilder().setPrettyPrinting().create();
            Reader reader = Files.newBufferedReader(Paths.get(filePath));
            Type listType = new TypeToken<List<stambedCloudPointJSON>>() {}.getType();
            List<stambedCloudPointJSON> ls= gson.fromJson(reader, listType);

            return ls;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("failed to load data"+e);
        }

    }
    public StampedCloudPoints getCloudPoints(String objectId, int time) {
        for (StampedCloudPoints stampedCloudPoint : cloudPoints) {
            if (stampedCloudPoint.getId().equals(objectId) && stampedCloudPoint.getTime() == time) {
                return stampedCloudPoint;
            }
        }
        return null; // Return null if no matching data is found
    }



    @Override
    public String toString() {
        return "LiDarDataBase{" +
                "cloudPoints=" + cloudPoints +
                '}';
    }
    public List<StampedCloudPoints> getCloudPoints() {
        return cloudPoints;
    }

    public int getDataSize() {
        return cloudPoints.size();
    }



    public StampedCloudPoints getCloudPointsbyID(String objectId,int time) {
        StampedCloudPoints stampedCloudPoints = null;
        for (StampedCloudPoints stamped : cloudPoints) {
            if (stamped.getId().equals(objectId)&& stamped.getTime() == time ) {
                stampedCloudPoints =stamped;
            }

        }


        return stampedCloudPoints; // No matching data found
    }



    public ArrayList<StampedCloudPoints> getCloudPointsbyTime(int time) {
        ArrayList<StampedCloudPoints> list = new ArrayList<>();
        for (StampedCloudPoints stamped : cloudPoints) {
            if (stamped.getTime()==time) {
                list.add(stamped);
            }
        }
        return list;
    }
    //for test
    private LiDarDataBase(List<StampedCloudPoints> mockData) {
        this.cloudPoints = mockData;
    }
    public static void initializeMockData(List<StampedCloudPoints> mockData) {
        instance = new LiDarDataBase(mockData);
    }
    public static LiDarDataBase getMockInstance(List<StampedCloudPoints> mockData) {
        if (instance == null) {
            instance = new LiDarDataBase(mockData);
        }
        return instance;
    }

}
