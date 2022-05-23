package cast.task;

import cast.pojo.Jobgetall;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.microsoft.playwright.Browser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class JobProcessor implements PageProcessor {
    private int count=1;
    private String url="https://search.51job.com/list/000000,000000,0000,00,9,99,+,2,1.html?lang=c&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99&ord_field=0&dibiaoid=0&line=&welfare=";
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-");
    private String today = formatter.format(new Date());
    private Browser browser;
    List<Selectable> list1;
//    @Bean
//    public DataSource dataSource() {
//        HikariConfig config = new HikariConfig();
//        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
//        config.setJdbcUrl("jdbc:mysql://localhost:3306/job_getall?useSSL=false&characterEncoding=utf8&useUnicode=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai");
//        config.setUsername("root");
//        config.setPassword("123456");
//        return new HikariDataSource(config);
//    }


    @Override
    public void process(Page page) {
        WebClient webClient=new WebClient(BrowserVersion.FIREFOX);
        webClient.getOptions().setJavaScriptEnabled(true); //启用JS解释器，默认为true
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setActiveXNative(false);
        webClient.setJavaScriptTimeout(5000);//设置JS执行的超时时间
        webClient.getOptions().setCssEnabled(false); //禁用css支持
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setThrowExceptionOnScriptError(false); //js运行错误时，是否抛出异常
        webClient.getOptions().setTimeout(10000);

        webClient.getOptions().setDoNotTrackEnabled(false);
        HtmlPage page1 = null;
        try {
            if (page.getUrl().toString()==null)
            {page1 = webClient.getPage(url);}
            else {
                page1=webClient.getPage(page.getUrl().toString());
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }finally {
            webClient.close();
        }
        webClient.waitForBackgroundJavaScript(10000);
        String xml = page1.asXml();
        Html html = new Html(xml);
        //   System.out.println(html.toString());
        List<Selectable> list = html.css("div.j_joblist div.e a.el").nodes();

        if (list.size()==0){
            //this.breakpage(page);
            this.savejobinfo(page);
        }else{
            list1 = html.css("div.j_joblist div.e a.el").nodes();
            for (Selectable selectable : list1) {
                String s = selectable.links().toString();
                page.addTargetRequest(s);
            }
            String bkUrl = "https://search.51job.com/list/000000,000000,0000,00,9,99,+,2," + (count + 1) + ".html?lang=c&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99&ord_field=0&dibiaoid=0&line=&welfare=";
            page.addTargetRequest(bkUrl);
        }


    }

//    private void breakpage(Page page) {
//        browser = Playwright
//                .create()
//                .chromium()
//                .launch(new BrowserType.LaunchOptions().setHeadless(false));
//        com.microsoft.playwright.Page page1 = browser.newPage();
//        page1.navigate(page.getUrl().toString());
//
//
//    }

    private void savejobinfo(Page page) {
        Jobgetall jobgetall=new Jobgetall();
        Html html=page.getHtml();
        System.out.println(page.getUrl().toString()+"测试");
        String s = html.css("div.cn p.msg.ltype","title").toString();
        //  System.out.println(s);
        if (s==null){
            return;
        }
        jobgetall.setCityName(html.css("div.cn p.msg.ltype").toString());

    }


    private Site site=Site.me()
            .setCharset("utf8")
            .setRetryTimes(3)
            .setRetrySleepTime(3000)
            .setTimeOut(10000);
    @Override
    public Site getSite() {
        return site;
    }


    @Autowired
    private SpringDatapipeline springDatapipeline;
    @Scheduled(initialDelay = 1000,fixedDelay = 100000)
    public void process(){
        Spider.create(new JobProcessor())
                .addUrl(url)
                .setScheduler(new QueueScheduler()
                        .setDuplicateRemover(new BloomFilterDuplicateRemover(10000)))
                .thread(10)
                //         .addPipeline(this.springDatapipeline)
                .run();
    }
}