package repository;

import domain.*;
import utils.Page;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DuckRepository  extends UserRepository {
    DataBaseLoaderDuck dataBaseLoader;

    public DuckRepository() {
        dataBaseLoader = new DataBaseLoaderDuck();
    }


    public Page<Duck> getDucksByType(int pageNumber, int pageSize, DuckType type) throws SQLException {
        int offset = pageNumber * pageSize;
        int totalElements = dataBaseLoader.getTotalDuckCountByType(type);

        if (offset >= totalElements && totalElements > 0) {
            return new Page<>(new ArrayList<>(), 0,totalElements);
        }
        List<Duck> pageContent=dataBaseLoader.getDuckByType(pageSize, offset, type);
        return new Page<>(pageContent, pageContent.size(),totalElements);
    }

    public Page<Duck> getDucks(int pageNumber, int pageSize) throws SQLException {
        int offset = pageNumber * pageSize;
        int totalElements = dataBaseLoader.getTotalDuckCount();

        if (offset >= totalElements && totalElements > 0) {
            return new Page<>(new ArrayList<>(), 0,totalElements);
        }
        List<Duck> pageContent = dataBaseLoader.getDucksPage(pageSize,offset);
        return new Page<>(pageContent, pageContent.size(),totalElements);
    }

    public List<SwimmingDuck> getSwimmingDucks() throws SQLException {
        return dataBaseLoader.getAllSwimmingDucks();
    }

    public void addDuck(Duck duck) throws SQLException {
        dataBaseLoader.saveDuck(duck);
    }

    public void removeDuck(Duck duck) throws SQLException {
            dataBaseLoader.removeDuck(duck);
    }

}
