package cast.service;


import cast.pojo.Jobgetall;

import java.util.List;

public interface JobgetallService {
    public void save(Jobgetall jobgetall);

    public List<Jobgetall> findJobgetall(Jobgetall jobgetall);
}
