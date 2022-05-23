package cast.task;

import cast.pojo.Jobgetall;
import cast.service.JobgetallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

@Component
public class SpringDatapipeline implements Pipeline {
    @Autowired
    private JobgetallService jobgetallService;
    @Override
    public void process(ResultItems resultItems, Task task) {
        Jobgetall jobgetall=resultItems.get("jobgetall");
        if (jobgetall!=null)
        {
            this.jobgetallService.save(jobgetall);
        }

    }
}
