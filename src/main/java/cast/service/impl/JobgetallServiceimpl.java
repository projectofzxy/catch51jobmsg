package cast.service.impl;

import cast.dao.JobgetallDao;
import cast.pojo.Jobgetall;
import cast.service.JobgetallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobgetallServiceimpl implements JobgetallService {

    @Autowired
    private JobgetallDao jobgetallDao;
    @Override
    @Transactional
    public void save(Jobgetall jobgetall) {
        Jobgetall param=new Jobgetall();
        param.setUrl(jobgetall.getUrl());
        param.setTime(jobgetall.getTime());
        List<Jobgetall> list = this.findJobgetall(param);
        if (list.size()==0){
            this.jobgetallDao.saveAndFlush(jobgetall);

        }

    }

    @Override
    public List findJobgetall(Jobgetall jobgetall) {
        Example example=Example.of(jobgetall);
        List list = this.jobgetallDao.findAll(example);
        return list;
    }
}