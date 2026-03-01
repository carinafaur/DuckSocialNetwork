package domain;

import exceptions.DuckGroupException;

import java.util.ArrayList;
import java.util.List;

public class DuckGroup<T extends Duck> {
    private long id;
    private String name;
    private List<T> members;
    private DuckType groupType;
    public DuckGroup(long id, String name, List<T> members, DuckType groupType) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.groupType = groupType;
    }

    public void addMember(T member) {
        members.add(member);
        member.setGroup(this.id);
    }

    public void showMembers() {
        for(T member : members) {
            member.print();
        }
    }

    public double[] getAvgPerformance() {
        double sumR = 0, sumS = 0;
        for(T d : members) {
                sumR += d.getResistance();
                sumS += d.getSpeed();
            }
        double avgR = sumR / members.size();
        double avgS = sumS / members.size();
        return new double[]{avgR, avgS};
    }

    public String getGroupName() {
        return name;
    }

    public List<T> getMembers(){
        return members;
    }

    public long getId() {
        return id;
    }

    public DuckType getDuckType() {
        return groupType;
    }

    public boolean hasMembers(){
        return !members.isEmpty();
    }

    @Override
    public String toString() {
        return "ID: "+id+" NAME: "+name+" MEMBERS: "+members.toString();
    }
}
