package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.CameraService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class CamerasJSON {
    private List<CameraJSON> CamerasConfigurations;
    private String camera_datas_path;


    public List<CameraService> loadData(String base){
        if(camera_datas_path==null)
            throw new NullPointerException();

        Map<String, List<CameraDataJSON>> camersInput=null;
        Gson gson=new Gson();
        try{
            gson = new GsonBuilder().setPrettyPrinting().create();

            Reader reader = Files.newBufferedReader(Paths.get(base + camera_datas_path));
            Type cameraInputType = new TypeToken<Map<String, List<CameraDataJSON>>>() {}.getType();
            camersInput = gson.fromJson(reader, cameraInputType);


            return saveTheData( camersInput);

        }catch (Exception e){
            e.printStackTrace();

        }
        return null;
    }

    private List<CameraService> saveTheData(Map<String, List<CameraDataJSON>> camersInput) {
        List<CameraService> cameraServicesToReturn = new ArrayList<>();
        for (CameraJSON camera : CamerasConfigurations) {

            List<CameraDataJSON> stambed = camersInput.get(camera.getCameraKey());
            List<StampedDetectedObjects> stambDetected=convertFromJsonStambed(stambed);
            Camera cam =new Camera(camera.getId(),camera.getFrequency(),stambDetected,camera.getCameraKey());
            CameraService cameraService=new CameraService(cam);
            cameraServicesToReturn.add(cameraService);
        }
        return cameraServicesToReturn;
    }

    private List<StampedDetectedObjects> convertFromJsonStambed(List<CameraDataJSON> stambed) {
        List<StampedDetectedObjects> listToReturn = new ArrayList<>();
        for(CameraDataJSON cameraData : stambed){
            List<DetectedObject> lsDetectedObj=new ArrayList<>();
            for (DetectedObjJSON obj : cameraData.getDetectedObjects()) {
                DetectedObject objToAdd=new DetectedObject(obj.getId(),obj.getDescription());
                lsDetectedObj.add(objToAdd);
            }
            StampedDetectedObjects stamToAdd=new StampedDetectedObjects(cameraData.getTime(),lsDetectedObj);

            listToReturn.add(stamToAdd);
        }

        return listToReturn;
    }


    // Getters and Setters
    public List<CameraJSON> getCamerasConfigurations() {
        return CamerasConfigurations;
    }

    public void setCamerasConfigurations(List<CameraJSON> camerasConfigurations) {
        CamerasConfigurations = camerasConfigurations;
    }

    public String toString() {
        return "\nCamerasConfiguration{" +
                "CamerasConfigurations=" + CamerasConfigurations +
                ", camera_datas_path='" + camera_datas_path + '\'' +
                '}';
    }
}

