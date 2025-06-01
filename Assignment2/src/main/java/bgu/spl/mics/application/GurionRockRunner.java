package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {
    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */
    public static void main(String[] args) {


        bgu.spl.mics.application.objects.StatisticalFolder statisticalFolder = new StatisticalFolder();

        String configFilePath = args[0];//the path is given in parameter
        String baseDirectory = Paths.get(configFilePath).getParent().toFile().getAbsolutePath() + "/";
 
        // TODO: Parse configuration file.
        Gson gson = new Gson();
        InputJSON input =null;
        try {
            Gson gson1 = new GsonBuilder().setPrettyPrinting().create();
            Reader reader = Files.newBufferedReader(Paths.get(configFilePath));
            input = gson.fromJson(reader, InputJSON.class);

            Type inputTybe = new TypeToken<List<InputJSON>>() {}.getType();
            reader.close();
        }

        catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<CameraService> camServises= input.getCameras().loadData(baseDirectory);

        List<Thread> camThredList= new ArrayList<>(); 
        for(CameraService camServise:camServises) {
            camThredList.add(new Thread(camServise)) ;

        }

        String lidarDataBase= baseDirectory + input.getLidarWorkers().getLidarsDataPath();
        LiDarDataBase instance=LiDarDataBase.getInstance(lidarDataBase);



        String poseJsonFile=baseDirectory + input.getPoseJsonFile();
        PoseWrabJSON pose=new PoseWrabJSON();
        pose.loadData(poseJsonFile);
        ArrayList<Pose> poseslist=new ArrayList<>();
        poseslist.addAll(pose.getPose());
        if (poseslist==null) {
            throw new NullPointerException("The poses list is null");
        }
        if(poseslist.size()==0)
            throw new NullPointerException("No poses found");
        Thread poseThread=new Thread(new PoseService(new GPSIMU(poseslist))); 


        List<LiDarService> liDarServices=input.getLidarWorkers().getLidarServises();
        List<Thread>  LidarThreads=new ArrayList<Thread>(); 

        for(LiDarService lidar:liDarServices){
            LidarThreads.add(new Thread(lidar));
        }


        FusionSlam fusionSlam=FusionSlam.getInstance();
        Thread fusionSlamService = new Thread(new FusionSlamService(fusionSlam)); 

        TimeService Clock= new TimeService(input.getTickTime(), input.getDuration());
        Thread time =new Thread(Clock);
        ArrayList allThreads=new ArrayList();

        fusionSlamService.start();
        allThreads.add(fusionSlamService);
        for(Thread thread:camThredList){
            thread.start();
            allThreads.add(thread);
        }
        for(Thread thread:LidarThreads){
            thread.start();
            allThreads.add(thread);
        }
        poseThread.start();
        allThreads.add(poseThread);
        try {
            Thread.sleep(1000); // wait for all services to initialize before starting the time service
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        time.start();
        allThreads.add(time);

        Clock.setNumofoperetinalservices(allThreads.size());


        try{
            fusionSlamService.join();

            time.join();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        Output output = new Output(fusionSlam);



        Gson outputFile = new GsonBuilder().setPrettyPrinting().create();
        try {
            if(fusionSlam.getErrorDescription()==null) {
                FileWriter fileWriter = new FileWriter(baseDirectory + "output.json");
                outputFile.toJson(output, fileWriter);
                fileWriter.flush();
                fileWriter.close();
            } else {
                FileWriter fileWriter = new FileWriter(baseDirectory + "error_output.json");
                outputFile.toJson(output, fileWriter);
                fileWriter.flush();
                fileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
