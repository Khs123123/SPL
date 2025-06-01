package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PoseWrabJSON {
    List<Pose> pose;
    public PoseWrabJSON() {
        pose = new ArrayList<>();
    }
    public List<Pose> loadData(String filePath){
        try{
            Gson gson = new Gson();
            gson = new GsonBuilder().setPrettyPrinting().create();
            Reader reader = Files.newBufferedReader(Paths.get(filePath));
            Type listType = new TypeToken<List<Pose>>() {}.getType();
            pose= gson.fromJson(reader, listType);
            return pose;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("failed to load data"+e);
        }
    }
    public List<Pose> getPose() {
        return pose;
    }


}
