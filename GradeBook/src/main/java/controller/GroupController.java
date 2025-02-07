package controller;

import dao.GroupDao;

public class GroupController {
    GroupDao groupDao=new GroupDao();
    public void addGroup( String name, String description){
        groupDao.addGroup( name, description);
    }
}
