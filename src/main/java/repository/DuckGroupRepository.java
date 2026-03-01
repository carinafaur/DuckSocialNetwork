package repository;

import domain.Duck;
import domain.DuckGroup;
import domain.SwimmingDuck;
import utils.Page;

import java.io.FileWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DuckGroupRepository {
    DataBaseLoaderDuckGroup dataBaseLoader;
    DuckRepository userRepository;

    public DuckGroupRepository(DuckRepository userRepository) {
        dataBaseLoader = new DataBaseLoaderDuckGroup();
        this.userRepository = userRepository;
    }

    public Page<DuckGroup<? extends Duck>> getDuckGroups(int pageNumber, int pageSize) throws SQLException {
        int offset = pageNumber * pageSize;
        int totalElements = dataBaseLoader.getDuckGroupCount();

        if (offset >= totalElements && totalElements > 0) {
            return new Page<>(new ArrayList<>(), 0, totalElements);
        }
        List<DuckGroup<? extends Duck>> pageContent = dataBaseLoader.getDuckGroupsPage(pageSize, offset);
        return new Page<>(pageContent, pageContent.size(), totalElements);
    }

    public DuckGroup<Duck> findDuckGroup(Long id) throws SQLException {
        DuckGroup<? extends Duck> duckGroup = dataBaseLoader.findDuckGroupById(id);
        return (DuckGroup<Duck>) duckGroup;
    }

    public void addDuckGroup(DuckGroup<? extends Duck> duckGroup) throws SQLException {

        dataBaseLoader.saveDuckGroup(duckGroup);
    }

    public <T extends Duck> void addDuckToGroup(DuckGroup<T> duckGroup, T duck) throws SQLException {
        duckGroup.addMember(duck);
        dataBaseLoader.deleteDuckGroup(duckGroup);
        dataBaseLoader.saveDuckGroup(duckGroup);
    }
}
